package spoon;

import java.io.IOException;
import java.time.LocalDate;

import spoon.command.Command;
import spoon.exception.*;
import spoon.parser.Parser;
import spoon.storage.Storage;
import spoon.task.Task;
import spoon.task.TaskList;
import spoon.ui.UserInterface;

/**
 * The root of the Spoon chatbot.
 * Main entry point + implementation logic.
 *
 * @author kongxinyang.
 */
public class Spoon {
    // File path to external file containing data
    private static final String FILE_PATH = "./data/spoon.txt";

    // User Interface
    private final UserInterface userInterface;
    // Storage
    private final Storage storage;
    // List of tasks
    private final TaskList tasks;

    // Constructor
    /**
     * Creates an instance of Spoon.
     */
    public Spoon() {
        this.userInterface = new UserInterface();
        this.storage = new Storage(FILE_PATH);

        TaskList tempTasks;
        try {
            tempTasks = new TaskList(storage.load());
            userInterface.print(userInterface.showLoadSuccess());
        } catch (IOException e) {
            userInterface.print(userInterface.showLoadingError(e.getMessage()));
            tempTasks = new TaskList();
        } catch (SpoonException e) {
            userInterface.print(userInterface.showError(e.getMessage()));
            tempTasks = new TaskList();
        }
        this.tasks = tempTasks;

    }

    // Methods
    /**
     * Parses input and converts them to commands + arguments.
     *
     * @param input user input.
     * @return message to be output.
     * @throws SpoonException if an exception is detected (refer to the different exceptions).
     * @throws InvalidCommandException if a command is invalid.
     */
    private String executeCommand(String input) throws SpoonException {
        Command command = Parser.parseCommand(input);
        String response;

        switch (command) {
            // Exit command
            case BYE: {
                response = userInterface.showExit();
                break;
            }

            // List command
            case LIST: {
                response = userInterface.showTaskList(tasks);
                break;
            }

            // For mark, unmark and delete commands, inputArray[1] would be the index of the task
            // Mark, unmark and delete command
            case MARK, UNMARK, DELETE: {
                response = handleEditCommand(input, command);
                break;
            }

            // Check for tasks on or by a specific date command
            case ON, BY: {
                response = handleDateCommand(input, command);
                break;
            }

            // Filter all tasks that contain a word command
            case FIND: {
                String[] words = Parser.checkFind(input);
                TaskList filteredTasks = tasks.findTasks(words);
                response = userInterface.showFilteredTasks(filteredTasks, String.join(", ", words));
                break;
            }

            // Add todos, deadlines or events command
            case TODO, DEADLINE, EVENT: {
                response = handleAddTask(input);
                break;
            }

            // Help command
            case HELP: {
                response = userInterface.showHelp();
                break;
            }

            // Command.UNKNOWN and default: command not recognized
            case UNKNOWN:
                throw new InvalidCommandException();
            default: {
                // executeCommand is only called with the commands listed above,
                // and therefore should never reach the default case
                assert false : "Execution reached default case in executeCommand";
                throw new FatalErrorException();
            }
        }
        return response;
    }

    /**
     * Handles edit commands.
     *
     * @param input user input.
     * @param command command inputted by user.
     * @return message to be output.
     * @throws SpoonException if an exception is detected (refer to the different exceptions).
     */
    private String handleEditCommand(String input, Command command) throws SpoonException {
        // Check for errors
        int index = Parser.checkEdit(input, tasks);
        Task task = tasks.get(index);

        String response;

        switch (command) {
            // Mark command
            case MARK: {
                task.complete();
                response = userInterface.showMarked(task);
                break;
            }
            // Unmark command
            case UNMARK: {
                task.uncomplete();
                response = userInterface.showUnmarked(task);
                break;
            }
            // Delete command
            case DELETE: {
                tasks.delete(index);
                response = userInterface.showDeleted(task, tasks.size());
                break;
            }
            // Default: placeholder value, should never happen
            default: {
                // handleEditCommit is only called with commands Mark, Unmark and Delete,
                // and therefore should never reach the default case
                assert false : "Execution reached default case in handleEditCommand";
                throw new FatalErrorException();
            }
        }

        saveTasks();

        return response;
    }

    /**
     * Handles date filtering commands.
     *
     * @param input user input.
     * @param command command inputted by user.
     * @return message to be output.
     * @throws SpoonException if an exception is detected (refer to the different exceptions).
     */
    private String handleDateCommand(String input, Command command) throws SpoonException {
        // Check for errors
        LocalDate targetDate = Parser.checkDate(input);

        switch (command) {
            // On command
            case ON: {
                TaskList filteredTasks = tasks.getTasksOn(targetDate);
                return userInterface.showFilteredTasks(filteredTasks,
                        targetDate, Command.ON);
            }
            // By command
            case BY: {
                TaskList filteredTasks = tasks.getTasksBy(targetDate);
                return userInterface.showFilteredTasks(filteredTasks,
                        targetDate, Command.BY);
            }
            // Default: placeholder value, should never happen
            default: {
                // handleDateCommand is only called with commands On and By,
                // and therefore should never reach the default case
                assert false : "Execution reached default case in handleDateCommand";
                throw new FatalErrorException();
            }
        }
    }

    /**
     * Handles task adding commands.
     *
     * @param input user input.
     * @return message to be output.
     * @throws SpoonException if an exception is detected (refer to the different exceptions).
     */
    private String handleAddTask(String input) throws SpoonException {
        // Check for errors
        Task task = Parser.checkAdd(input);
        // Add command
        tasks.add(task);
        String response = userInterface.showAdded(task, tasks.size());

        saveTasks();
        return response;
    }

    /**
     * Saves tasks to an external file.
     */
    private void saveTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            userInterface.print(userInterface.showWritingError(e.getMessage()));
        }
    }

    /**
     * Generates a response for the user's chat message in GUI mode.
     *
     * @param input raw text from TextField.
     * @return formatted reply string from userInterface.
     */
    public String getResponse(String input) {
        try {
            return executeCommand(input);
        } catch (SpoonException e) {
            return userInterface.showError(e.getMessage());
        }
    }

    public String getIntroduction() {
        return userInterface.showIntroduction();
    }

    /**
     * Wakes Spoon up! (starts the Spoon chatbot).
     */
    public void run() {

        // Start message
        userInterface.print(userInterface.showStart());

        // Chat logic
        while (true) {
            // Get input and split it into commands, index and options
            String input = userInterface.readCommand();
            try {
                // Exit command
                if (Parser.parseCommand(input) == Command.BYE) {
                    break;
                }
                // Other commands
                String response = executeCommand(input);
                userInterface.print(response);
            } catch (SpoonException e) {
                userInterface.print(userInterface.showError(e.getMessage()));
            }

            userInterface.print(userInterface.showDivider());
        }

        // Save + clean up
        try {
            storage.save(tasks.getTasks());
            userInterface.print(userInterface.showSave(), userInterface.showExit());
            userInterface.close();
        } catch (IOException e) {
            userInterface.print(userInterface.showWritingError(e.getMessage()));
        }

    }

    // Entry point
    public static void main(String[] args) {
        new Spoon().run();
    }
}
