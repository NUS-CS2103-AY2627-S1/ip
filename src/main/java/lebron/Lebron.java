package lebron;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import lebron.exception.LebronException;
import lebron.parser.ParsedCommand;
import lebron.parser.Parser;
import lebron.storage.Storage;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.task.Todo;
import lebron.ui.Ui;

/**
 * The chatbot's backend: holds the task list and storage, and turns one line
 * of user input into a response. Used by both the console entry point
 * ({@link #main(String[])}) and the JavaFX GUI ({@link Main}).
 */
public class Lebron {
    private final Storage storage;
    private final TaskList tasks;
    private boolean isExit = false;

    /**
     * Creates a chatbot backed by the given data file.
     *
     * @param dataFile where tasks are loaded from and saved to
     */
    public Lebron(Path dataFile) {
        this.storage = new Storage(dataFile);
        this.tasks = new TaskList(storage.load());
    }

    /**
     * Processes one line of user input and returns the text to show the
     * user. Never throws -- a {@link LebronException}'s message becomes the
     * response text, the same text the console prints for an error.
     *
     * @param input the raw line the user typed
     * @return the response text
     */
    public String getResponse(String input) {
        isExit = false;
        try {
            ParsedCommand command = Parser.parse(input);
            isExit = command.getType() == ParsedCommand.Type.BYE;
            return execute(command);
        } catch (LebronException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns whether the most recent {@link #getResponse(String)} call was
     * a {@code bye} command, i.e. whether the caller should now exit.
     *
     * @return true if the chatbot should exit
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Dispatches one parsed command to the handler for its type.
     *
     * @param command the command to run
     * @return the response text
     * @throws LebronException if the command cannot be completed (e.g. a
     *     task number that is out of range)
     */
    private String execute(ParsedCommand command) throws LebronException {
        switch (command.getType()) {
            case LIST:
                return handleList();
            case TODO:
                return handleTodo(command);
            case DEADLINE:
            case EVENT:
                return handleNewTask(command);
            case MARK:
            case UNMARK:
                return handleMarkOrUnmark(command);
            case DELETE:
                return handleDelete(command);
            case FIND:
                return handleFind(command);
            case BYE:
                return "Bye. Hope to see you again soon!";
            default:
                // Parser only ever returns the types handled above, so this
                // is unreachable given correct code; the assert documents
                // that and catches a regression (e.g. a new ParsedCommand.Type
                // added without a case here) under -ea, while the exception
                // still gives defined (if generic) behaviour when it's not.
                assert false : "Unreachable: unhandled command type " + command.getType();
                throw new LebronException("OOPS!!! I don't understand that command.");
        }
    }

    /** Handles {@code LIST}: shows every task. */
    private String handleList() {
        return formatTaskList("Here are the tasks in your list:", tasks.asList());
    }

    /** Handles {@code TODO}: adds a todo and saves. */
    private String handleTodo(ParsedCommand command) {
        Todo todo = new Todo(command.getDescription());
        boolean isDuplicate = tasks.hasDuplicateDetails(todo);
        tasks.add(todo);
        storage.save(tasks);
        return "added: " + command.getDescription() + duplicateNote(isDuplicate);
    }

    /** Handles {@code DEADLINE}/{@code EVENT}: adds the pre-built task and saves. */
    private String handleNewTask(ParsedCommand command) {
        Task task = command.getTask();
        boolean isDuplicate = tasks.hasDuplicateDetails(task);
        tasks.add(task);
        storage.save(tasks);
        return "added: " + task + duplicateNote(isDuplicate);
    }

    /**
     * Returns a short note to append to an "added" message when the new task
     * duplicates an existing one's details, or an empty string otherwise.
     * This only warns -- the task is added either way -- since a deliberate
     * repeat (e.g. "call mom" on two different days) is a legitimate case.
     */
    private static String duplicateNote(boolean isDuplicate) {
        return isDuplicate ? " (note: you already have a task just like this)" : "";
    }

    /**
     * Handles {@code MARK}/{@code UNMARK}: flips a task's done status and
     * saves. A recurring task's {@code markAsDone()} rolls it forward to its
     * next occurrence instead of staying marked done ({@link Task#isRecurring()}),
     * so that case gets its own message.
     */
    private String handleMarkOrUnmark(ParsedCommand command) throws LebronException {
        boolean isMark = command.getType() == ParsedCommand.Type.MARK;
        Task task = isMark ? tasks.mark(command.getIndex()) : tasks.unmark(command.getIndex());
        storage.save(tasks);
        if (isMark && task.isRecurring()) {
            return "Nice! I've marked this task as done. Since it recurs, "
                    + "I've scheduled the next occurrence:" + System.lineSeparator() + "  " + task;
        }
        String header = isMark
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return header + System.lineSeparator() + "  " + task;
    }

    /** Handles {@code DELETE}: removes a task and saves. */
    private String handleDelete(ParsedCommand command) throws LebronException {
        Task removed = tasks.delete(command.getIndex());
        storage.save(tasks);
        return "Noted. I've removed this task:" + System.lineSeparator() + "  " + removed
                + System.lineSeparator() + "Now you have " + tasks.size() + " tasks in the list.";
    }

    /** Handles {@code FIND}: shows the tasks matching a keyword. */
    private String handleFind(ParsedCommand command) {
        return formatTaskList("Here are the matching tasks in your list:", tasks.find(command.getDescription()));
    }

    /**
     * Renders a numbered list of tasks under a header line, one task per
     * line. Shared by {@link #handleList()} and {@link #handleFind}, which
     * differ only in the header and which tasks they pass in.
     *
     * @param header the line shown before the list
     * @param taskList the tasks to number and list, in order
     * @return the header followed by one numbered line per task
     */
    private static String formatTaskList(String header, List<Task> taskList) {
        StringBuilder message = new StringBuilder(header);
        for (int i = 0; i < taskList.size(); i++) {
            message.append(System.lineSeparator()).append(i + 1).append('.').append(taskList.get(i));
        }
        return message.toString();
    }

    /**
     * Runs the console loop: reads and executes commands until the user
     * exits, printing each response through {@code ui}.
     *
     * @param ui the console UI to read commands from and print responses to
     */
    private void runCli(Ui ui) {
        boolean exit = false;
        while (!exit) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            ui.showMessage(getResponse(fullCommand));
            exit = isExit();
            ui.showLine();
        }
        ui.close();
    }

    /**
     * Console entry point. Tasks are persisted to {@code ./data/lebron.txt}
     * (relative to the working directory); the path is built from segments
     * so it works on any OS, and {@link Storage} handles the file/folder not
     * existing yet.
     *
     * <p>The greeting is shown, and only then is the data file loaded (by
     * constructing {@code Lebron}), so a "your data file is corrupted"
     * warning prints after the banner, not before it.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Lebron lebron = new Lebron(Paths.get("data", "lebron.txt"));
        lebron.runCli(ui);
    }
}
