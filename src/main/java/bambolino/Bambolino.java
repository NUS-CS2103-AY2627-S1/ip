package bambolino;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import bambolino.exception.BambolinoException;
import bambolino.parser.Parser;
import bambolino.storage.Storage;
import bambolino.task.Deadline;
import bambolino.task.Event;
import bambolino.task.Task;
import bambolino.task.TaskList;
import bambolino.task.Todo;
import bambolino.ui.Ui;

/**
 * A command-line task list application.
 */
public class Bambolino {
    /**
     * Starts Bambolino and processes commands until the user says goodbye.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage = new Storage();
        TaskList tasks = loadTasks(storage, ui);
        while (true) {
            String userInput = ui.readCommand();

            Parser.Command parsedCommand = new Parser().parse(userInput);
            if (parsedCommand.name().equals("bye") && parsedCommand.arguments().isEmpty()) {
                ui.showGoodbye();
                break;
            }

            try {
                processCommand(userInput, tasks, storage, ui);
            } catch (BambolinoException error) {
                ui.showError(error.getMessage());
            }
        }
    }

    /**
     * Processes one command.
     *
     * @param userInput The command entered by the user.
     * @param tasks The task list.
     * @throws BambolinoException If the command or its arguments are invalid.
     */
    public static void processCommand(String userInput, TaskList tasks, Storage storage, Ui ui)
            throws BambolinoException {
        assert userInput != null : "user input must be provided";
        assert tasks != null : "task list must be provided";
        assert storage != null : "storage must be provided";
        assert ui != null : "UI must be provided";
        if (userInput.isBlank()) {
            throw new BambolinoException("please enter a command.");
        }

        Parser.Command parsedCommand = new Parser().parse(userInput);
        assert parsedCommand != null : "parser must return a command";
        String command = parsedCommand.name();
        String arguments = parsedCommand.arguments();

        switch (command) {
        case "todo", "deadline", "event", "mark", "unmark", "delete" -> {
            try {
                storage.checkWritable();
            } catch (IOException error) {
                throw new BambolinoException(error.getMessage());
            }
        }
        default -> { }
        }

        switch (command) {
        case "bye" -> throw new BambolinoException("the bye command does not take any extra words.");
        case "list" -> showTaskList(arguments, tasks, ui);
        case "find" -> findTasks(arguments, tasks, ui);
        case "todo" -> addTodo(arguments, tasks, storage, ui);
        case "deadline" -> addDeadline(arguments, tasks, storage, ui);
        case "event" -> addEvent(arguments, tasks, storage, ui);
        case "mark" -> updateTaskStatus(arguments, tasks, storage, ui, true);
        case "unmark" -> updateTaskStatus(arguments, tasks, storage, ui, false);
        case "delete" -> deleteTask(arguments, tasks, storage, ui);
        default -> throw new BambolinoException("I don't recognise that command. Try todo, deadline, event, list, "
                + "find, mark, unmark, delete, or bye.");
        }
    }

    private static void showTaskList(String arguments, TaskList tasks, Ui ui) throws BambolinoException {
        if (!arguments.isEmpty()) {
            throw new BambolinoException("the list command does not take any extra words.");
        }
        ui.showTaskList(tasks);
    }

    private static void findTasks(String arguments, TaskList tasks, Ui ui) throws BambolinoException {
        if (arguments.isEmpty()) {
            throw new BambolinoException("the find command needs a keyword. Try: find book");
        }
        ui.showMatchingTasks(tasks, arguments);
    }

    private static void addTodo(String arguments, TaskList tasks, Storage storage, Ui ui)
            throws BambolinoException {
        if (arguments.isEmpty()) {
            throw new BambolinoException("a todo needs a description. Try: todo borrow book");
        }
        tasks.add(new Todo(arguments));
        saveTasks(storage, tasks);
        ui.showTaskAdded(tasks.getLast(), tasks.size());
    }

    private static void updateTaskStatus(String arguments, TaskList tasks, Storage storage, Ui ui, boolean isDone)
            throws BambolinoException {
        Task task = getTask(arguments, tasks, isDone ? "mark" : "unmark");
        if (isDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
        saveTasks(storage, tasks);
        ui.showMarkedTask(task, isDone);
    }

    private static void deleteTask(String arguments, TaskList tasks, Storage storage, Ui ui)
            throws BambolinoException {
        int taskIndex = getTaskIndex(arguments, tasks, "delete");
        Task deletedTask = tasks.remove(taskIndex);
        saveTasks(storage, tasks);
        ui.showDeletedTask(deletedTask, tasks.size());
    }

    /** Adds a deadline task after validating its description and date. */
    private static void addDeadline(String arguments, TaskList tasks, Storage storage, Ui ui)
            throws BambolinoException {
        String[] parts = splitParameter(arguments, "by");
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new BambolinoException("a deadline needs both a description and a date after /by.");
        }
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(by);
        } catch (DateTimeParseException error) {
            throw new BambolinoException("deadline dates must use yyyy-mm-dd. Try: deadline return book "
                    + "/by 2019-10-15");
        }
        tasks.add(new Deadline(description, dueDate));
        saveTasks(storage, tasks);
        ui.showTaskAdded(tasks.getLast(), tasks.size());
    }

    /** Adds an event task after validating its description, start, and end text. */
    private static void addEvent(String arguments, TaskList tasks, Storage storage, Ui ui)
            throws BambolinoException {
        String[] fromParts = splitParameter(arguments, "from");
        String[] toParts = splitParameter(fromParts[1], "to");
        if (fromParts[0].matches("(?s).*?(?<!\\S)/to(?!\\S).*")) {
            throw new BambolinoException("an event needs /from before /to, with each parameter used once.");
        }
        String description = fromParts[0].trim();
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new BambolinoException("an event needs a description, a start after /from, and an end after /to.");
        }
        tasks.add(new Event(description, from, to));
        saveTasks(storage, tasks);
        ui.showTaskAdded(tasks.getLast(), tasks.size());
    }

    /** Splits a required parameter, allowing whitespace while rejecting missing or repeated markers. */
    private static String[] splitParameter(String arguments, String parameter) throws BambolinoException {
        String[] parts = arguments.split("(?<!\\S)/" + parameter + "(?!\\S)", -1);
        if (parts.length != 2) {
            throw new BambolinoException("use /" + parameter + " exactly once, separated by spaces.");
        }
        return parts;
    }

    /** Finds a valid task selected by a command. */
    private static Task getTask(String arguments, TaskList tasks, String command)
            throws BambolinoException {
        return tasks.get(getTaskIndex(arguments, tasks, command));
    }

    /** Validates and converts a command's one-based task number to a list index. */
    private static int getTaskIndex(String arguments, TaskList tasks, String command)
            throws BambolinoException {
        if (arguments.isEmpty()) {
            throw new BambolinoException("the " + command + " command needs a task number. Try: " + command + " 1");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException error) {
            throw new BambolinoException("the task number for " + command + " must be a whole number.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BambolinoException("task " + taskNumber + " is not in your list yet.");
        }
        return taskNumber - 1;
    }

    /** Loads saved tasks while allowing the chatbot to start if storage fails. */
    public static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            return new TaskList(storage.load(ui::showWarning));
        } catch (IOException error) {
            ui.showLoadingError();
            return new TaskList();
        }
    }

    /** Saves task changes and reports a storage problem without ending the chatbot. */
    private static void saveTasks(Storage storage, TaskList tasks) throws BambolinoException {
        try {
            storage.save(tasks.asList());
        } catch (IOException error) {
            throw new BambolinoException("I couldn't save your tasks. Please check the data folder.");
        }
    }

}
