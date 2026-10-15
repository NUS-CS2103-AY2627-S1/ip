package bingusdingus;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import bingusdingus.parser.CommandType;
import bingusdingus.parser.InvalidTaskCommandException;
import bingusdingus.parser.Parser;
import bingusdingus.task.Task;
import bingusdingus.task.TaskList;
import bingusdingus.ui.Ui;

/** Runs the Bingus Dingus command-line task manager. */
public class BingusDingus {
    private static final String INVALID_COMMAND_MESSAGE = "I've got no idea watchu talkin' about";

    private final TaskList taskList;
    private final Parser parser;
    private final Ui ui;
    private Runnable lastUndo;

    /** Creates an application with its parser, task list, and response formatter. */
    public BingusDingus() {
        taskList = new TaskList();
        parser = new Parser();
        ui = new Ui();
    }

    /** Starts the application and processes commands entered by the user. */
    public static void main(String[] args) {
        configureStandardStreams();

        BingusDingus bingusDingus = new BingusDingus();
        System.out.println(bingusDingus.ui.showWelcome());

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(bingusDingus.getResponse(command));
            if (bingusDingus.isByeCommand(command)) {
                break;
            }
        }
    }

    /** Configures command-line output explicitly so Unicode is preserved across host console encodings. */
    private static void configureStandardStreams() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
    }

    /** Executes a command and returns the response for the graphical interface. */
    public String getResponse(String input) {
        CommandType commandType = parser.parseCommandType(input);

        return switch (commandType) {
            case BYE -> ui.showGoodbye();
            case LIST -> handleListCommand();
            case HELP -> ui.showHelp();
            case FIND -> handleFindCommand(input);
            case MARK, UNMARK -> handleMarkCommand(input, commandType);
            case DELETE -> handleDeleteCommand(input);
            case UNDO -> handleUndoCommand();
            case TASK -> handleAddCommand(input);
            case UNKNOWN -> ui.showInvalidCommand(INVALID_COMMAND_MESSAGE);
        };
    }

    /** Returns whether the supplied input is the command that closes the application. */
    public boolean isByeCommand(String input) {
        return parser.parseCommandType(input) == CommandType.BYE;
    }

    /** Displays the task list and reports when saved tasks could not be loaded. */
    private String handleListCommand() {
        if (taskList.hasStorageLoadError()) {
            return ui.showStorageLoadError() + "\n" + ui.showTasks(taskList);
        }
        return ui.showTasks(taskList);
    }

    /** Handles a find command and displays the matching tasks. */
    private String handleFindCommand(String input) {
        String keyword = parser.getCommandArgument(input);
        if (keyword.isEmpty()) {
            return ui.showMissingFindKeyword();
        }
        return ui.showMatchingTasks(taskList, taskList.findIndexes(keyword));
    }

    private String handleMarkCommand(String input, CommandType commandType) {
        try {
            int taskIndex = parseTaskIndex(input);
            if (!isValidTaskIndex(taskIndex)) {
                return ui.showInvalidTaskNumber();
            }

            return updateTaskCompletion(taskIndex, commandType == CommandType.MARK);
        } catch (NumberFormatException e) {
            return ui.showInvalidTaskNumberFormat();
        } catch (IllegalStateException e) {
            return ui.showStorageError();
        }
    }

    /** Returns the zero-based task index represented by a command argument. */
    private int parseTaskIndex(String input) {
        return Integer.parseInt(parser.getCommandArgument(input)) - 1;
    }

    /** Returns whether a zero-based task index refers to an existing task. */
    private boolean isValidTaskIndex(int taskIndex) {
        return taskIndex >= 0 && taskIndex < taskList.size();
    }

    /** Updates a task's completion state and returns the corresponding response. */
    private String updateTaskCompletion(int taskIndex, boolean isDone) {
        Task task = taskList.get(taskIndex);
        if (task.isDone() == isDone) {
            return isDone ? ui.showTaskAlreadyDone() : ui.showTaskNotDone();
        }

        taskList.setDone(taskIndex, isDone);
        lastUndo = () -> taskList.setDone(taskIndex, !isDone);
        return isDone ? ui.showTaskMarkedDone(task) : ui.showTaskMarkedNotDone(task);
    }

    private String handleDeleteCommand(String input) {
        try {
            int taskIndex = Integer.parseInt(parser.getCommandArgument(input)) - 1;
            if (!isValidTaskIndex(taskIndex)) {
                return ui.showInvalidTaskNumber();
            }
            Task deletedTask = taskList.remove(taskIndex);
            lastUndo = () -> taskList.insert(taskIndex, deletedTask);
            String deletedDescription = deletedTask.getDescription();
            return ui.showTaskDeleted(deletedDescription, taskList.size());
        } catch (NumberFormatException e) {
            return ui.showInvalidTaskNumberFormat();
        } catch (IllegalStateException e) {
            return ui.showStorageError();
        }
    }

    private String handleAddCommand(String input) {
        try {
            Task task = parser.parseTask(input);
            int taskIndex = taskList.size();
            taskList.add(task);
            lastUndo = () -> taskList.remove(taskIndex);
            return ui.showTaskAdded(taskList.get(taskList.size() - 1), taskList.size());
        } catch (InvalidTaskCommandException | IllegalArgumentException e) {
            return ui.showInvalidCommand(e.getMessage());
        } catch (IllegalStateException e) {
            return ui.showStorageError();
        }
    }

    /** Undoes the most recent successful state-changing command. */
    private String handleUndoCommand() {
        if (lastUndo == null) {
            return ui.showNothingToUndo();
        }

        try {
            lastUndo.run();
            lastUndo = null;
            return ui.showUndoSuccessful();
        } catch (IllegalStateException e) {
            return ui.showStorageError();
        }
    }
}
