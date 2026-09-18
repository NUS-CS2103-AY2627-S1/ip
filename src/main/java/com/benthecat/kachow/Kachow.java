package com.benthecat.kachow;

import java.nio.file.Path;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.parser.Parser;
import com.benthecat.kachow.storage.Storage;
import com.benthecat.kachow.task.Task;
import com.benthecat.kachow.task.TaskList;
import com.benthecat.kachow.ui.Ui;
import com.benthecat.kachow.ui.printer.ConsolePrinter;
import com.benthecat.kachow.ui.printer.Printer;

/**
 * Coordinates Kachow's UI, command parsing, task list, and persistent storage.
 */
public class Kachow {
    private static final String DATA_FILE = "./data/kachow.txt";

    private final Storage storage;
    private TaskList tasks;
    private final Ui userInterface;
    private final Parser parser;
    private final KachowException loadingException;

    /**
     * Creates a Kachow application backed by the default task data file.
     *
     * @param printer Destination for user-facing output.
     */
    public Kachow(Printer printer) {
        this(DATA_FILE, printer);
    }

    /**
     * Creates a Kachow application backed by the given task data file.
     * Invalid stored data is reported and replaced with an empty in-memory task list so the UI can still run.
     *
     * @param filePath Path to the task data file.
     * @param printer Destination for user-facing output.
     */
    public Kachow(String filePath, Printer printer) {
        userInterface = new Ui(printer);
        parser = new Parser();
        storage = new Storage(Path.of(filePath));

        TaskList loadedTasks;
        KachowException loadingError = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (KachowException exception) {
            loadingError = exception;
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        loadingException = loadingError;
    }

    /** Sends the welcome message and any error encountered while loading stored tasks. */
    public void sendWelcomeMessage() {
        userInterface.showWelcome();
        showLoadingError();
        userInterface.outputData();
    }

    /**
     * Handles one command and sends its response to the configured printer.
     *
     * @param input Complete user command.
     * @return {@code false} only when the command asks the application to exit.
     */
    public boolean handleUserInput(String input) {
        boolean shouldContinue = processUserInput(input);
        userInterface.outputData();
        return shouldContinue;
    }

    /** Processes console commands until the user exits or standard input closes. */
    public void run() {
        userInterface.showDivider();
        userInterface.showWelcome();
        userInterface.showDivider();
        showLoadingError();
        userInterface.outputData();

        while (userInterface.hasNextCommand()) {
            userInterface.showDivider();
            boolean shouldContinue = processUserInput(userInterface.readCommand());
            if (!shouldContinue) {
                userInterface.outputData();
                return;
            }
            userInterface.showDivider();
            userInterface.outputData();
        }
    }

    /** Starts the console interface using the default task data file. */
    public static void main(String[] args) {
        new Kachow(new ConsolePrinter()).run();
    }

    /**
     * Processes one command without making assumptions about the output destination.
     *
     * @param input Complete user command.
     * @return {@code false} only when the command asks the application to exit.
     */
    private boolean processUserInput(String input) {
        try {
            Parser.ParsedCommand command = parser.parse(input);
            return execute(command);
        } catch (KachowException exception) {
            userInterface.showError(exception.getMessage());
            return true;
        }
    }

    /** Displays an error encountered while loading stored tasks, if any. */
    private void showLoadingError() {
        if (loadingException != null) {
            userInterface.showLoadingError(loadingException);
        }
    }

    /** Commits in-memory changes only after their complete replacement file has been saved. */
    private void saveChanges(TaskList updatedTasks) throws KachowException {
        storage.save(updatedTasks.getTasks());
        tasks = updatedTasks;
    }

    /**
     * Executes one validated command.
     *
     * @param parsedCommand Command and argument produced by {@link Parser}.
     * @return {@code false} only when the application should exit.
     * @throws KachowException If a task operation cannot be completed or persisted.
     */
    private boolean execute(Parser.ParsedCommand parsedCommand) throws KachowException {
        switch (parsedCommand.command()) {
            case BYE -> {
                parser.requireNoArgument(parsedCommand);
                userInterface.showGoodbye();
                return false;
            }
            case LIST -> showTasks(parsedCommand);
            case FIND -> findTasks(parsedCommand);
            case ON -> findTasksOnDate(parsedCommand);
            case TODO, DEADLINE, EVENT -> addTask(parsedCommand);
            case MARK -> setTaskDoneStatus(parsedCommand, true);
            case UNMARK -> setTaskDoneStatus(parsedCommand, false);
            case EDIT -> editTask(parsedCommand);
            case DELETE -> deleteTask(parsedCommand);
            default -> throw new IllegalStateException("Unsupported command: " + parsedCommand.command());
        }
        return true;
    }

    /** Shows all tasks after rejecting unexpected arguments. */
    private void showTasks(Parser.ParsedCommand command) throws KachowException {
        parser.requireNoArgument(command);
        userInterface.showTaskList(tasks);
    }

    /** Shows tasks whose descriptions match the supplied keyword. */
    private void findTasks(Parser.ParsedCommand command) throws KachowException {
        String keyword = parser.parseSearchKeyword(command);
        userInterface.showSearchResults(keyword, tasks.findByDescription(keyword));
    }

    /** Shows deadlines and events on the supplied calendar date. */
    private void findTasksOnDate(Parser.ParsedCommand command) throws KachowException {
        var date = parser.parseDate(command);
        userInterface.showTasksOn(date, tasks.findOn(date));
    }

    /** Adds a task and announces success only after saving it. */
    private void addTask(Parser.ParsedCommand command) throws KachowException {
        Task task = parser.parseTask(command);
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        updatedTasks.add(task);
        saveChanges(updatedTasks);
        userInterface.showTaskAdded(task, tasks.getSize());
    }

    /** Persists a completion-status change before displaying its confirmation. */
    private void setTaskDoneStatus(Parser.ParsedCommand command, boolean isDone) throws KachowException {
        int taskNumber = parser.parseTaskNumber(command);
        Task task = tasks.get(taskNumber).withDoneStatus(isDone);
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        updatedTasks.replace(taskNumber, task);
        saveChanges(updatedTasks);
        if (isDone) {
            userInterface.showTaskMarked(task);
        } else {
            userInterface.showTaskUnmarked(task);
        }
    }

    /** Applies and saves all requested edits before displaying the updated task. */
    private void editTask(Parser.ParsedCommand command) throws KachowException {
        Parser.EditCommand editCommand = parser.parseEditCommand(command);
        Task currentTask = tasks.get(editCommand.taskNumber());
        Task editedTask = parser.applyEdit(currentTask, editCommand);
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        updatedTasks.replace(editCommand.taskNumber(), editedTask);
        saveChanges(updatedTasks);
        userInterface.showTaskEdited(editedTask);
    }

    /** Deletes and saves a task before displaying the remaining task count. */
    private void deleteTask(Parser.ParsedCommand command) throws KachowException {
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        Task task = updatedTasks.delete(parser.parseTaskNumber(command));
        saveChanges(updatedTasks);
        userInterface.showTaskDeleted(task, tasks.getSize());
    }
}
