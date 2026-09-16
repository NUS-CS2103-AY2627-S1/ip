package della;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import della.command.Command;
import della.parser.Parser;
import della.storage.Storage;
import della.task.Task;
import della.task.TaskList;
import della.ui.UI;
import javafx.application.Platform;

/**
 * Runs the Della task-management application.
 */
public class Della {
    /** Stores tasks so that they can be persisted between application runs. */
    private final Storage storage;

    /** Stores the tasks currently managed by Della. */
    private TaskList taskList;

    /** Stores the command parsed from the most recent user input. */
    private Command command;

    /**
     * Creates Della using the specified file to persist tasks.
     *
     * @param filePath the path of the task storage file.
     */
    public Della(String filePath) {
        this.storage = new Storage(filePath);

        if (this.storage.hasData()) {
            try {
                this.taskList = new TaskList(storage.loadTasks());
            } catch (FileNotFoundException e) {
                System.out.println(UI.showError("Unable to load tasks from storage"));
                this.taskList = new TaskList();
            }
        } else {
            this.taskList = new TaskList();
        }
    }

    /**
     * Returns the command parsed from the most recent user input.
     *
     * @return the most recently parsed command.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Runs the command-reading loop until the user enters the {@code bye} command.
     */
    public void run() {
        System.out.println(UI.showWelcome());
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println(getResponse(input));
            if (Parser.parseCommand(input) == Command.BYE) {
                break;
            }
        }
        System.out.println(UI.showFarewell());
        scanner.close();
    }

    /**
     * Returns a response to the user's chat message and performs the corresponding task operation.
     *
     * @param input the user's chat message.
     * @return the response to display to the user.
     */
    public String getResponse(String input) {
        command = Parser.parseCommand(input);
        return switch (command) {
            case BYE -> handleBye();
            case LIST -> handleList();
            case MARK -> handleMark(input, true);
            case UNMARK -> handleMark(input, false);
            case TODO, DEADLINE, EVENT -> handleAdd(input, command);
            case DELETE -> handleDelete(input);
            case FIND -> handleFind(input);
            default -> handleDefault();
        };
    }
    /**
     * Exits the application.
     *
     * @return farewell message.
     */
    private String handleBye() {
        Platform.exit();
        return UI.showFarewell();
    }

    /**
     * Displays all tasks currently stored in the task list.
     *
     * @return the formatted list of tasks.
     */
    private String handleList() {
        return UI.showTasks(taskList.getTasks());
    }

    /**
     * Marks or unmarks the task specified in the user's input.
     *
     * @param input the user's command and task number.
     * @param isMarking {@code true} to mark the task; {@code false} to unmark it.
     * @return a success or error message for the operation.
     */
    private String handleMark(String input, boolean isMarking) {
        try {
            int taskNum = Parser.parseTaskNumber(Parser.parseArguments(input));
            Task task = isMarking ? taskList.mark(taskNum - 1) : taskList.unmark(taskNum - 1);
            storage.updateTaskStatus(taskNum, task);
            return isMarking ? UI.showMarkedTask(task) : UI.showUnmarkedTask(task);
        } catch (ArrayIndexOutOfBoundsException e) {
            return UI.showError(isMarking ? "Cannot mark empty task" : "Cannot unmark empty task");
        } catch (NumberFormatException e) {
            return UI.showError("Enter a valid task number. eg " + (isMarking ? "mark" : "unmark") + " 1");
        } catch (IndexOutOfBoundsException e) {
            return UI.showError(String.format("You only have %d task(s). Try again", taskList.size()));
        } catch (IOException e) {
            return UI.showError("Error in updating task in storage");
        }
    }

    /**
     * Adds a new task based on the specified add command.
     *
     * @param input the user's command and task details.
     * @param command the type of task to add.
     * @return a success or error message for the operation.
     */
    private String handleAdd(String input, Command command) {
        try {
            String argument = Parser.parseArguments(input);
            Task newTask = switch (command) {
                case TODO -> Parser.parseTodo(argument);
                case DEADLINE -> Parser.parseDeadline(argument);
                case EVENT -> Parser.parseEvent(argument);
                default -> throw new AssertionError("Unexpected add command");
            };
            storage.storeTask(newTask);
            taskList.add(newTask);
            return UI.showAddedTask(newTask, taskList.size());
        } catch (ArrayIndexOutOfBoundsException e) {
            return UI.showError("Bro, cannot add empty " + command.name().toLowerCase());
        } catch (DateTimeParseException e) {
            return UI.showError("Enter date in dd/MM/yyyy HHmm format");
        } catch (IllegalArgumentException e) {
            return UI.showError(e.getMessage());
        } catch (IOException e) {
            return UI.showError("Error when storing task");
        }
    }

    /**
     * Deletes the task specified in the user's input from memory and storage.
     *
     * @param input the user's command and task number.
     * @return a success or error message for the operation.
     */
    private String handleDelete(String input) {
        try {
            int taskNum = Parser.parseTaskNumber(Parser.parseArguments(input));
            Task task = taskList.delete(taskNum - 1);
            storage.deleteTask(taskNum);
            return UI.showDeletedTask(task, taskList.size());
        } catch (ArrayIndexOutOfBoundsException e) {
            return UI.showError("Cannot delete empty task");
        } catch (NumberFormatException e) {
            return UI.showError("Enter a valid task number. eg delete 1");
        } catch (IndexOutOfBoundsException e) {
            return UI.showError(String.format("You only have %d task(s). Try again", taskList.size()));
        } catch (IOException e) {
            return UI.showError("Error in deleting task in storage");
        }
    }

    /**
     * Returns tasks whose descriptions match the search term in the user's input.
     *
     * @param input the user's find command and search term.
     * @return the matching tasks or an error message.
     */
    private String handleFind(String input) {
        try {
            ArrayList<Task> searchResult = Parser.parseFindTask(
                    Parser.parseArguments(input), taskList.getTasks());
            return UI.showSearchResult(searchResult);
        } catch (ArrayIndexOutOfBoundsException e) {
            return UI.showError("Bro, cannot find nothing");
        } catch (IllegalArgumentException e) {
            return UI.showError(e.getMessage());
        }
    }

    /**
     * Displays an error message when the user enters an unrecognised command.
     *
     * @return the formatted error message.
     */
    private String handleDefault() {
        return UI.showError("I don't recognise this command :( Try again pls tyvm");
    }

    /**
     * Starts the Della application.
     *
     * @param args Command-line arguments, which are currently ignored.
     */
    public static void main(String[] args) {
        new Della("./data/della.txt").run();
    }
}
