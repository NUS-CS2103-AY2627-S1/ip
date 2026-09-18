package bobby;

import java.util.ArrayList;
import java.util.List;

import bobby.parser.Parser;
import bobby.storage.Storage;
import bobby.task.Task;
import bobby.task.TaskList;

/**
 * Generates Bobby chatbot responses for GUI and console front ends.
 */
public class Bobby {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String TAG_COMMAND = "tag";
    private static final String FIND_COMMAND = "find";

    private final TaskList tasks;
    private final String loadingError;

    /**
     * Creates a Bobby chatbot instance for the JavaFX GUI.
     */
    public Bobby() {
        TaskList loadedTasks;
        String errorMessage = null;
        try {
            loadedTasks = new TaskList(Storage.loadTasks());
        } catch (BobbyException e) {
            loadedTasks = new TaskList(new ArrayList<>());
            errorMessage = "The old scroll is clouded: " + e.getMessage();
        }
        tasks = loadedTasks;
        loadingError = errorMessage;
        assert tasks != null : "Bobby should always have a task list.";
    }

    /**
     * Starts the console version of Bobby.
     *
     * @param args command-line arguments passed to the console runner.
     */
    public static void main(String[] args) {
        BobbyConsole.main(args);
    }

    /**
     * Returns the greeting shown when Bobby starts in the GUI.
     *
     * @return welcome message, with a loading error first if the save file cannot be read.
     */
    public String getWelcomeMessage() {
        String welcomeMessage = "Greetings, young one.\nI am Bobby.\n"
                + "Share your task, and we shall bring order to the day.";
        if (loadingError == null) {
            return welcomeMessage;
        }
        return loadingError + "\n" + welcomeMessage;
    }

    /**
     * Returns whether the command asks Bobby to exit.
     *
     * @param command user command.
     * @return true if the command is bye.
     */
    public boolean isExitCommand(String command) {
        return Parser.normalizeCommand(command).equals(BYE_COMMAND);
    }

    /**
     * Handles one GUI command and returns Bobby's response.
     *
     * @param command command entered by the user.
     * @return response to show in the GUI.
     */
    public String getResponse(String command) {
        assert command != null : "GUI should pass a non-null command.";
        command = Parser.normalizeCommand(command);
        if (isExitCommand(command)) {
            return "The pond grows still. Until our paths meet again...";
        }

        try {
            return handleCommand(command, tasks);
        } catch (BobbyException e) {
            return "The path is misty: " + e.getMessage();
        }
    }

    /**
     * Handles one command against the given task list and returns Bobby's response.
     *
     * @param command command entered by the user.
     * @param tasks task list to read or mutate.
     * @return response to show to the user.
     * @throws BobbyException if the command is invalid or storage cannot be updated.
     */
    static String handleCommand(String command, TaskList tasks) throws BobbyException {
        assert command != null : "Command should be non-null before handling.";
        assert tasks != null : "Task list should be non-null before handling.";

        command = Parser.normalizeCommand(command);
        if (command.equals(LIST_COMMAND)) {
            return handleList(tasks);
        } else if (Parser.isFind(command)) {
            return handleFind(command, tasks);
        } else if (Parser.isMark(command)) {
            return handleMark(command, tasks);
        } else if (Parser.isUnmark(command)) {
            return handleUnmark(command, tasks);
        } else if (Parser.isDelete(command)) {
            return handleDelete(command, tasks);
        } else if (Parser.isTag(command)) {
            return handleTag(command, tasks);
        } else {
            return handleAdd(command, tasks);
        }
    }

    /**
     * Returns the response for a list command.
     *
     * @param tasks current task list.
     * @return formatted list response.
     */
    private static String handleList(TaskList tasks) {
        return formatTaskList("These are the stones upon your path:", tasks.asList());
    }

    /**
     * Returns the response for a find command.
     *
     * @param command full user command.
     * @param tasks current task list.
     * @return formatted search response.
     * @throws BobbyException if the keyword is missing.
     */
    private static String handleFind(String command, TaskList tasks) throws BobbyException {
        String keyword = Parser.parseFindKeyword(command);
        return formatTaskList("The pond reflects these matching ripples:", tasks.find(keyword));
    }

    /**
     * Marks a task as done, saves the task list, and returns the response.
     *
     * @param command full user command.
     * @param tasks current task list.
     * @return mark confirmation response.
     * @throws BobbyException if the task number is invalid or saving fails.
     */
    private static String handleMark(String command, TaskList tasks) throws BobbyException {
        int taskIndex = Parser.parseTaskIndex(command, MARK_COMMAND, tasks);
        Task task = tasks.mark(taskIndex);
        Storage.saveTasks(tasks.asList());
        return "Peace. This task now rests complete:\n  " + task;
    }

    /**
     * Marks a task as not done, saves the task list, and returns the response.
     *
     * @param command full user command.
     * @param tasks current task list.
     * @return unmark confirmation response.
     * @throws BobbyException if the task number is invalid or saving fails.
     */
    private static String handleUnmark(String command, TaskList tasks) throws BobbyException {
        int taskIndex = Parser.parseTaskIndex(command, UNMARK_COMMAND, tasks);
        Task task = tasks.unmark(taskIndex);
        Storage.saveTasks(tasks.asList());
        return "Patience. This task returns to the path:\n  " + task;
    }

    /**
     * Deletes a task, saves the task list, and returns the response.
     *
     * @param command full user command.
     * @param tasks current task list.
     * @return delete confirmation response.
     * @throws BobbyException if the task number is invalid or saving fails.
     */
    private static String handleDelete(String command, TaskList tasks) throws BobbyException {
        int taskIndex = Parser.parseTaskIndex(command, DELETE_COMMAND, tasks);
        Task removedTask = tasks.delete(taskIndex);
        Storage.saveTasks(tasks.asList());
        return "The leaf is released:\n  " + removedTask
                + "\nNow " + tasks.size() + " tasks remain on the branch.";
    }

    /**
     * Adds a tag to a task, saves the task list, and returns the response.
     *
     * @param command full user command.
     * @param tasks current task list.
     * @return tag confirmation response.
     * @throws BobbyException if the task number or tag is invalid, or saving fails.
     */
    private static String handleTag(String command, TaskList tasks) throws BobbyException {
        Parser.TagCommand tagCommand = Parser.parseTagCommand(command, tasks);
        Task task = tasks.addTag(tagCommand.taskIndex(), tagCommand.tag());
        Storage.saveTasks(tasks.asList());
        return "A small mark of meaning is tied to this task:\n  " + task;
    }

    /**
     * Adds a new task, saves the task list, and returns the response.
     *
     * @param command full user command.
     * @param tasks current task list.
     * @return add confirmation response.
     * @throws BobbyException if the task command is invalid or saving fails.
     */
    private static String handleAdd(String command, TaskList tasks) throws BobbyException {
        Task task = Parser.parseTask(command);
        tasks.add(task);
        Storage.saveTasks(tasks.asList());
        return "The seed has been planted:\n  " + task
                + "\nNow " + tasks.size() + " tasks grow in the grove.";
    }

    private static String formatTaskList(String heading, List<Task> tasks) {
        assert heading != null : "Task-list heading should be non-null.";
        assert tasks != null : "Tasks to format should be non-null.";

        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return response.toString();
    }
}
