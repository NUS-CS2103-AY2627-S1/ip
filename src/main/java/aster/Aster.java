package aster;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import aster.command.Command;
import aster.command.Parser;
import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.Deadline;
import aster.task.Event;
import aster.task.Task;
import aster.task.TaskList;
import aster.task.Todo;
import aster.ui.Ui;

/**
 * Main entry point and coordinator for the Aster task-list application.
 */
public class Aster {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Aster application wired to the given storage file path.
     *
     * @param filePath storage path, relative or absolute
     */
    public Aster(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load().toArray(Task[]::new));
        } catch (AsterException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the interactive command loop.
     */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            try {
                Parser.ParsedCommand parsedCommand = Parser.parse(fullCommand);
                if (parsedCommand.getCommand() == Command.BYE) {
                    ui.showGoodbye();
                    break;
                }
                execute(parsedCommand);
            } catch (AsterException e) {
                ui.showError(e.getUserMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Dispatches a parsed command to its handler.
     *
     * @param parsedCommand command and arguments to execute
     * @throws AsterException when the command handler reports a user-facing error
     */
    private void execute(Parser.ParsedCommand parsedCommand) throws AsterException {
        switch (parsedCommand.getCommand()) {
            case TODO -> addTodo(parsedCommand.getArguments());
            case DEADLINE -> addDeadline(parsedCommand.getArguments());
            case EVENT -> addEvent(parsedCommand.getArguments());
            case LIST -> ui.showTasks(tasks);
            case ONDATE -> showTasksOnDate(parsedCommand.getArguments());
            case MARK -> markTask(parsedCommand.getArguments());
            case UNMARK -> unmarkTask(parsedCommand.getArguments());
            case DELETE -> deleteTask(parsedCommand.getArguments());
            case FIND -> findTasks(parsedCommand.getArguments());
            case TAG -> tagTask(parsedCommand.getArguments());
            case BYE -> throw new IllegalStateException("The bye command is handled before dispatch.");
            default -> throw new IllegalStateException("Unknown command.");
        }
    }

    /**
     * Creates a to-do task from the given arguments, saves it, and reports it to the user.
     *
     * @param arguments text after the todo keyword
     * @throws AsterException when the description is missing
     */
    private void addTodo(String arguments) throws AsterException {
        String description = Parser.parseTodoDescription(arguments);
        Task task = new Todo(description);
        ensureTaskIsUnique(task);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Creates a deadline task from the given arguments, saves it, and reports it to the user.
     *
     * @param arguments text after the deadline keyword
     * @throws AsterException when the description or /by value is missing
     */
    private void addDeadline(String arguments) throws AsterException {
        Parser.DeadlineDetails details = Parser.parseDeadlineDetails(arguments);
        Task task = new Deadline(details.getDescription(), details.getBy());
        ensureTaskIsUnique(task);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Creates an event task from the given arguments, saves it, and reports it to the user.
     *
     * @param arguments text after the event keyword
     * @throws AsterException when required fields are missing or malformed
     */
    private void addEvent(String arguments) throws AsterException {
        Parser.EventDetails details = Parser.parseEventDetails(arguments);
        Task task = new Event(details.getDescription(), details.getFrom(), details.getTo());
        ensureTaskIsUnique(task);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Shows tasks that occur on the date given in the arguments.
     *
     * @param arguments text after the ondate keyword
     * @throws AsterException when the date is missing or invalid
     */
    private void showTasksOnDate(String arguments) throws AsterException {
        LocalDate targetDate = Parser.parseOnDate(arguments);
        List<Task> matchingTasks = tasks.findTasksOnDate(targetDate);
        ui.showTasksOnDate(targetDate, matchingTasks);
    }

    /**
     * Marks the task identified in the arguments as done, saves it, and reports it to the user.
     *
     * @param arguments text after the mark keyword
     * @throws AsterException when the task number is missing, malformed, or out of range
     */
    private void markTask(String arguments) throws AsterException {
        int taskNumber = Parser.parseTaskNumber(arguments, tasks.size(), "mark");
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        storage.save(tasks);
        ui.showTaskMarkedDone(task);
    }

    /**
     * Marks the task identified in the arguments as not done, saves it, and reports it to the user.
     *
     * @param arguments text after the unmark keyword
     * @throws AsterException when the task number is missing, malformed, or out of range
     */
    private void unmarkTask(String arguments) throws AsterException {
        int taskNumber = Parser.parseTaskNumber(arguments, tasks.size(), "unmark");
        Task task = tasks.get(taskNumber - 1);
        task.markAsNotDone();
        storage.save(tasks);
        ui.showTaskMarkedNotDone(task);
    }

    /**
     * Removes the task identified in the arguments, saves the list, and reports it to the user.
     *
     * @param arguments text after the delete keyword
     * @throws AsterException when the task number is missing, malformed, or out of range
     */
    private void deleteTask(String arguments) throws AsterException {
        int taskNumber = Parser.parseTaskNumber(arguments, tasks.size(), "delete");
        Task removedTask = tasks.remove(taskNumber - 1);
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Finds tasks whose description matches the keyword in the arguments and reports them to the user.
     *
     * @param arguments text after the find keyword
     * @throws AsterException when the keyword is missing
     */
    private void findTasks(String arguments) throws AsterException {
        String keyword = Parser.parseFindKeyword(arguments);
        List<Task> matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }

    /**
     * Adds a tag to the task identified in the arguments, saves the list, and reports it to the user.
     *
     * @param arguments text after the tag keyword
     * @throws AsterException when the task number or tag is missing, malformed, or out of range
     */
    private void tagTask(String arguments) throws AsterException {
        Parser.TagDetails details = Parser.parseTagDetails(arguments);
        int taskNumber = Parser.parseTaskNumber(details.getTaskNumberText(), tasks.size(), "tag");
        Task task = tasks.get(taskNumber - 1);
        task.addTag(details.getTag());
        storage.save(tasks);
        ui.showTaskTagged(task);
    }

    /**
     * Ensures newly-created tasks do not duplicate existing task details.
     *
     * @param candidate task about to be added
     * @throws AsterException when an equivalent task already exists
     */
    private void ensureTaskIsUnique(Task candidate) throws AsterException {
        if (tasks.containsTaskWithSameDetails(candidate)) {
            throw new AsterException("That task already exists in your list.",
                    "Use list to review existing tasks before adding another.");
        }
    }

    /**
     * Launches the Aster application using the default storage file.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Path storagePath = Paths.get("data", "aster.txt");
        new Aster(storagePath.toString()).run();
    }
}
