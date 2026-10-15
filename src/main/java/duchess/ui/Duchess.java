package duchess.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import duchess.io.Storage;
import duchess.parse.CommandType;
import duchess.tasks.Task;
import duchess.tasks.collections.TaskList;
import duchess.tasks.exceptions.FindException;
import duchess.tasks.exceptions.TaskException;
import duchess.tasks.exceptions.UnrecognizedCommandException;
import duchess.tasks.factories.TaskFactory;
import duchess.ui.exceptions.DuchessException;

/**
 * Represents the chatbot.
 */
public class Duchess {
    public static final String NAME = "Duchess";
    private static final String MARK_FORMAT = "mark INDEX";
    private static final String MARK_EXAMPLE = "mark 1";
    private static final String UNMARK_FORMAT = "unmark INDEX";
    private static final String UNMARK_EXAMPLE = "unmark 1";
    private static final String DELETE_FORMAT = "delete INDEX";
    private static final String DELETE_EXAMPLE = "delete 1";

    private final TaskList tasks;
    private final Storage db;

    /**
     * Creates a new chatbot, with the location
     * of the storage file.
     *
     * @param directory The subdirectory containing the storage file.
     * @param filepath The remaining address to the storage file.
     * @throws IOException If the storage file could not be found nor created.
     */
    public Duchess(String directory, String filepath) throws IOException {
        assert (directory != null) : "Storage directory must not be null";
        assert (filepath != null) : "Storage file path must not be null";

        this.db = new Storage(Path.of(directory, filepath));

        ArrayList<Task> tasksList = this.db.load();
        assert (tasksList != null) : "Storage must return an array list, even if empty";
        this.tasks = new TaskList(tasksList);
    }

    /**
     * Returns the exit message for duchess.
     * For UI to compare the response strings to, to know
     * when to close the application.
     */
    public static String getExitMessage() {
        return "Bye! See you again! :^D";
    }

    /**
     * Normalises user input before command parsing.
     *
     * @param userInput the raw user input
     * @return trimmed, lower-case user input
     */
    private static String normalizeInput(String userInput) {
        return userInput.trim().toLowerCase();
    }

    /**
     * Runs the duchess chatbot to respond to user input.
     */
    public String respondTo(String userInput) {
        assert (userInput != null) : "Should not ask duchess to respond to null strings";

        userInput = Duchess.normalizeInput(userInput);
        CommandType commandType = CommandType.parse(userInput);

        String response = this.handleCommand(userInput, commandType);
        try {
            this.saveStateIfMutated(commandType);
        } catch (IOException e) {
            return e.getMessage();
        }

        return response;
    }

    private String handleCommand(String userInput, CommandType commandType) {
        try {
            return switch (commandType) {
                case BYE -> Duchess.getExitMessage();
                case LIST -> this.displayList();
                case MARK -> this.handleMark(userInput);
                case UNMARK -> this.handleUnmark(userInput);
                case TODO, DEADLINE, EVENT, RECURRING -> this.handleAddTask(userInput);
                case DELETE -> this.handleDeleteTask(userInput);
                case FIND -> this.handleFind(userInput);
                case FIND_EXACT -> this.handleFindExact(userInput);
                default -> throw DuchessException
                        .declareUnrecognizedCommand(userInput);
            };
        } catch (DuchessException | TaskException e) {
            return e.getMessage();
        }
    }

    /**
     * Saves the list of tasks to the storage file.
     * @throws IOException If unable to save to the file.
     */
    private void saveStateIfMutated(CommandType commandType) throws IOException {
        if (!CommandType.isMutator(commandType)) {
            return;
        }

        try {
            this.db.save(this.tasks);
        } catch (IOException e) {
            throw new IOException(
                    "Oh no! The duchess has caught the goldfish syndrome! \n"
                            + "She is unable to remember your current list. \n"
                            + "Please check out the storage path as soon as possible!");
        }
    }

    /**
     * Returns a string representing the list of tasks
     * in user-readable format.
     */
    private String displayList() {
        return this.tasks.getTasksToPrint();
    }

    /**
     * Creates a new task from the user command, and
     * adds it to the list of tasks.
     *
     * @param command The user input.
     * @return A string representing the
     *          changes to the list of tasks.
     * @throws TaskException If the task could not be
     *                          created from the command.
     */
    private String handleAddTask(String command) throws TaskException {
        Task newTask = TaskFactory.createFromCommand(command);
        return this.tasks.addTaskIfNotExist(newTask);
    }

    /**
     * Deletes a task from the list of tasks.
     *
     * @param command The user input.
     * @return A string representing the result of the deletion.
     */
    private String handleDeleteTask(String command) throws UnrecognizedCommandException {
        try {
            int idx = parseTaskIndex(command, "delete");
            return this.tasks.deleteTaskFromIndex(idx);
        } catch (NumberFormatException e) {
            throw UnrecognizedCommandException.declareWithFormatAndExample(
                    command, DELETE_FORMAT, DELETE_EXAMPLE);
        }
    }

    /**
     * Marks a task as complete.
     *
     * @param command The user input.
     * @return A string representing
     *          the task marked as complete.
     */
    private String handleMark(String command) throws UnrecognizedCommandException {
        try {
            int idx = parseTaskIndex(command, "mark");
            return this.tasks.markTaskAt(idx);
        } catch (NumberFormatException e) {
            throw UnrecognizedCommandException.declareWithFormatAndExample(
                    command, MARK_FORMAT, MARK_EXAMPLE);
        }
    }

    /**
     * Marks a task as incomplete.
     *
     * @param command The user input.
     * @return A string representing the task
     *          marked as incomplete.
     */
    private String handleUnmark(String command) throws UnrecognizedCommandException {
        try {
            int idx = parseTaskIndex(command, "unmark");
            return this.tasks.unmarkTaskAt(idx);
        } catch (NumberFormatException e) {
            throw UnrecognizedCommandException.declareWithFormatAndExample(
                    command, UNMARK_FORMAT, UNMARK_EXAMPLE);
        }
    }

    /**
     * Extracts the argument following a command name.
     *
     * @param command the complete command
     * @param commandName the command name
     * @return the trimmed command argument
     */
    private String getArgument(String command, String commandName) {
        return command.substring(commandName.length()).trim();
    }

    /**
     * Extracts and parses the task index following a command name.
     *
     * @param command the complete command
     * @param commandName the command name
     * @return the parsed task index
     * @throws NumberFormatException if the argument is not an integer
     */
    private int parseTaskIndex(String command, String commandName)
            throws NumberFormatException {
        return Integer.parseInt(getArgument(command, commandName));
    }

    /**
     * Finds tasks whose descriptions contain the keyword supplied after the find command.
     * Text separated by spaces are treated as different keywords, and the result
     * only contains tasks whose description match all keywords.
     */
    private String handleFind(String command) throws FindException {
        String keyword = getArgument(command, "find");
        if (keyword.isEmpty()) {
            throw FindException.declareEmptyFindKeyword();
        }

        return this.tasks.getTasksMatching(keyword.split(" ", -1));
    }

    /**
     * Finds tasks whose descriptions contain the keyword supplied after the find command.
     * The entire string after the -e flag is treated as one keyword, and
     * the entire keywords must be contained in the task description as
     * one continuous string, for the task to be displayed.
     */
    private String handleFindExact(String command) throws FindException {
        String keyword = getArgument(command, "find -e");
        if (keyword.isEmpty()) {
            throw FindException.declareEmptyFindExactKeyword();
        }

        return this.tasks.getTasksMatching(keyword);

    }
}
