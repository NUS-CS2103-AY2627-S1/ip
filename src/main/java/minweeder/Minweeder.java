package minweeder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import minweeder.command.CommandWord;
import minweeder.exception.MinweederException;
import minweeder.parser.Parser;
import minweeder.storage.Storage;
import minweeder.task.Deadline;
import minweeder.task.Event;
import minweeder.task.Loan;
import minweeder.task.LoanType;
import minweeder.task.Task;
import minweeder.task.TaskList;
import minweeder.task.Todo;
import minweeder.ui.Ui;

/**
 * Core of the Minweeder task-tracking application. Loads any saved tasks on
 * construction, then executes one user command at a time via
 * {@link #getResponse(String)}, returning the resulting message so that both
 * a console interface ({@link #main(String[])}) and a GUI can display it.
 */
public class Minweeder {
    private final Ui ui = new Ui();
    private final Storage storage = new Storage();
    private final TaskList tasks;
    private final String startupMessage;
    private boolean isExit = false;
    private boolean isError = false;

    /**
     * Creates a Minweeder instance, loading any previously saved tasks.
     * If loading fails or some lines could not be read, the details are kept
     * to be shown alongside the welcome message.
     */
    public Minweeder() {
        TaskList loadedTasks;
        StringBuilder startup = new StringBuilder();
        try {
            loadedTasks = storage.load();
            if (storage.getSkippedLineCount() > 0) {
                startup.append(ui.showSkippedLines(storage.getSkippedLineCount()));
            }
        } catch (MinweederException e) {
            loadedTasks = new TaskList();
            startup.append(ui.showLoadingError(e.getMessage()));
        }
        this.tasks = loadedTasks;
        this.startupMessage = startup.toString();
    }

    /**
     * Returns the welcome banner, followed by any messages about problems
     * encountered while loading saved tasks.
     *
     * @return the formatted welcome message.
     */
    public String getWelcomeMessage() {
        return startupMessage.isEmpty() ? ui.showWelcome() : ui.showWelcome() + "\n" + startupMessage;
    }

    /**
     * Returns whether the last command executed via {@link #getResponse(String)}
     * was a request to exit the application.
     *
     * @return true if the application should now exit.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns whether the last command executed via {@link #getResponse(String)}
     * resulted in an error message.
     *
     * @return true if the last response was an error.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Adds a task to the list, persists the updated list to storage, and returns
     * a confirmation message. Shared by the todo/deadline/event command handlers.
     *
     * @param label a human-readable name for the task type, e.g. "TODO".
     * @param task the task to add.
     * @return the formatted confirmation message.
     * @throws MinweederException if saving the updated list fails.
     */
    private String addTask(String label, Task task) throws MinweederException {
        if (tasks.stream().anyMatch(task::equals)) {
            throw new MinweederException("you already have that exact " + label.toLowerCase() + " in your list.");
        }
        tasks.add(task);
        storage.save(tasks);
        return ui.showTaskAdded(label, task, tasks.size());
    }

    /**
     * Executes a single user command and returns the resulting message.
     *
     * @param command the raw command text entered by the user.
     * @return the formatted response, or an empty string if the command was blank.
     */
    public String getResponse(String command) {
        isError = false;
        if (command.isEmpty()) {
            return "";
        }
        try {
            String[] breakdown = Parser.splitCommand(command);
            CommandWord commandWord = Parser.parseCommandWord(breakdown);
            return dispatch(commandWord, breakdown);
        } catch (MinweederException e) {
            isError = true;
            return ui.showError(e.getMessage());
        }
    }

    /**
     * Routes a parsed command word to its handler.
     *
     * @param commandWord the command to execute.
     * @param breakdown the command split into its word and remaining arguments.
     * @return the formatted response.
     * @throws MinweederException if the command's arguments are invalid or saving fails.
     */
    private String dispatch(CommandWord commandWord, String[] breakdown) throws MinweederException {
        switch (commandWord) {
            case BYE:
                return handleBye(breakdown);
            case LIST:
                return handleList(breakdown);
            case MARK:
                return handleMark(breakdown);
            case UNMARK:
                return handleUnmark(breakdown);
            case TODO:
                return handleTodo(breakdown);
            case DEADLINE:
                return handleDeadline(breakdown);
            case EVENT:
                return handleEvent(breakdown);
            case LOAN:
                return handleLoan(breakdown);
            case DELETE:
                return handleDelete(breakdown);
            case ON:
                return handleOn(breakdown);
            case FIND:
                return handleFind(breakdown);
            default:
                assert false : "unhandled command word: " + commandWord;
                return "";
        }
    }

    private String handleBye(String[] breakdown) throws MinweederException {
        Parser.requireNoArguments(breakdown, "bye");
        isExit = true;
        return ui.showGoodbye();
    }

    private String handleList(String[] breakdown) throws MinweederException {
        Parser.requireNoArguments(breakdown, "list");
        return ui.showList(tasks);
    }

    private String handleMark(String[] breakdown) throws MinweederException {
        int index = Parser.parseIndex(breakdown, tasks);
        tasks.get(index).mark();
        storage.save(tasks);
        return ui.showTaskMarked(tasks.get(index));
    }

    private String handleUnmark(String[] breakdown) throws MinweederException {
        int index = Parser.parseIndex(breakdown, tasks);
        tasks.get(index).unmark();
        storage.save(tasks);
        return ui.showTaskUnmarked(tasks.get(index));
    }

    private String handleTodo(String[] breakdown) throws MinweederException {
        String description = Parser.requireArguments(breakdown, "todo", "todo read book");
        Todo todo = new Todo(description);
        return addTask("TODO", todo);
    }

    private String handleDeadline(String[] breakdown) throws MinweederException {
        String example = "deadline return book /by 2/12/2019 1800";
        String arguments = Parser.requireArguments(breakdown, "deadline", example);
        String[] parts = Parser.requireKeyword(arguments, "/by", example);
        LocalDateTime by = Parser.parseDeadlineBy(parts[1], example);
        Deadline deadline = new Deadline(parts[0], by);
        return addTask("Deadline", deadline);
    }

    private String handleEvent(String[] breakdown) throws MinweederException {
        String example = "event project meeting /from Mon 2pm /to 4pm";
        String arguments = Parser.requireArguments(breakdown, "event", example);
        String[] fromParts = Parser.requireKeyword(arguments, "/from", example);
        String[] toParts = Parser.requireKeyword(fromParts[1], "/to", example);
        Event event = new Event(fromParts[0], toParts[0], toParts[1]);
        return addTask("Event", event);
    }

    private String handleLoan(String[] breakdown) throws MinweederException {
        String example = "loan 50 /to Alice (or loan 50 /from Bob)";
        String arguments = Parser.requireArguments(breakdown, "loan", example);
        String keyword = Parser.parseLoanKeyword(arguments, example);
        String[] parts = Parser.requireKeyword(arguments, keyword, example);
        double amount = Parser.parseLoanAmount(parts[0], example);
        LoanType type = keyword.equals("/to") ? LoanType.LENT : LoanType.BORROWED;
        Loan loan = new Loan(parts[1], amount, type);
        return addTask("Loan", loan);
    }

    private String handleDelete(String[] breakdown) throws MinweederException {
        int index = Parser.parseIndex(breakdown, tasks);
        Task deleted = tasks.delete(index);
        storage.save(tasks);
        return ui.showTaskDeleted(deleted, tasks.size());
    }

    private String handleOn(String[] breakdown) throws MinweederException {
        String example = "on 2/12/2019";
        String argument = Parser.requireArguments(breakdown, "on", example);
        LocalDate date = Parser.parseOnDate(argument, example);
        return ui.showTasksOn(date, tasks);
    }

    private String handleFind(String[] breakdown) throws MinweederException {
        String example = "find book";
        String keyword = Parser.requireArguments(breakdown, "find", example);
        return ui.showFoundTasks(tasks.findIndices(keyword), tasks);
    }

    /**
     * Starts Minweeder as a console application: prints the welcome message, then
     * repeatedly reads and executes user commands until told to exit.
     *
     * @param args unused command-line arguments.
     */
    public static void main(String[] args) {
        Minweeder minweeder = new Minweeder();
        System.out.print(minweeder.ui.decorate(minweeder.getWelcomeMessage()));

        while (!minweeder.isExit()) {
            String command = minweeder.ui.readCommand();
            String response = minweeder.getResponse(command);
            if (!response.isEmpty()) {
                System.out.print(minweeder.ui.decorate(response));
            }
        }
        minweeder.ui.close();
    }
}
