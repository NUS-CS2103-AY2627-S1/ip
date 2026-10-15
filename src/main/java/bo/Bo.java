package bo;

import java.io.IOException;

import bo.model.Task;
import bo.model.TaskList;
import bo.parser.Parser;
import bo.storage.Storage;
import bo.ui.Ui;

/**
 * Owns Bo's application state and executes validated user commands.
 *
 * <p>The command execution API is shared by the command-line and JavaFX user
 * interfaces so that parsing, task mutation, sorting, and persistence are not
 * implemented separately for each interface.</p>
 */
public class Bo {
    /** The parser shared by all commands in this application instance. */
    private final Parser parser;

    /** The task list shared by all commands in this application instance. */
    private final TaskList taskList;

    /** A warning generated while loading tasks, or an empty string. */
    private final String startupWarning;

    /** Creates a Bo application instance and loads its saved tasks. */
    public Bo() {
        parser = new Parser();
        Task[] loadedTasks = new Task[TaskList.DEFAULT_CAPACITY];
        String loadingWarning;
        int taskCount;
        try {
            Storage.LoadResult result = Storage.loadWithReport(loadedTasks);
            loadingWarning = formatLoadingWarnings(result);
            taskCount = result.getTaskCount();
        } catch (IOException | IllegalArgumentException | SecurityException exception) {
            loadingWarning = "Warning: I couldn't load your tasks from disk.";
            taskCount = 0;
        }
        taskList = new TaskList(loadedTasks, taskCount);
        startupWarning = loadingWarning;
    }

    /**
     * Starts the command-line version of Bo.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Bo bo = new Bo();
        ui.showWelcome();
        if (!bo.getStartupWarning().isEmpty()) {
            ui.showResponse(bo.getStartupWarning());
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }

            ui.showSeparator();
            try {
                ui.showResponse(bo.executeCommand(command));
            } catch (BoException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

    /**
     * Executes one complete command and returns the user-facing response.
     *
     * @param command the complete command entered by the user.
     * @return the response to display to the user.
     * @throws BoException if the command is invalid or refers to a missing task.
     */
    public String executeCommand(String command) throws BoException {
        Parser.Command parsedCommand = parser.parse(command);
        switch (parsedCommand.type()) {
        case LIST:
            return formatTasks("Here are the tasks in your list:", taskList.toArray());
        case DELETE:
            return deleteTask(parsedCommand.taskIndex());
        case MARK:
            return markTask(parsedCommand.taskIndex());
        case UNMARK:
            return unmarkTask(parsedCommand.taskIndex());
        case FIND:
            return formatTasks("Here are the matching tasks in your list:",
                    taskList.find(parsedCommand.keyword()));
        case SORT:
            taskList.sortByDate();
            return appendSavingWarning(
                    formatTasks("Here are your tasks sorted by date:", taskList.toArray()));
        case ADD:
            return addTask(parsedCommand.task());
        default:
            throw new BoException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Returns a warning produced while loading the saved task file.
     *
     * @return the startup warning, or an empty string when loading succeeded.
     */
    public String getStartupWarning() {
        return startupWarning;
    }

    /** Adds a task and persists the updated list. */
    private String addTask(Task task) {
        if (!taskList.add(task)) {
            return "The task list is full.";
        }

        String response = "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + "Now you have " + taskList.size() + " tasks in the list.";
        return appendSavingWarning(response);
    }

    /** Deletes the task at the given zero-based index and persists the list. */
    private String deleteTask(int taskIndex) throws BoException {
        checkTaskIndex(taskIndex);
        Task deletedTask = taskList.remove(taskIndex);
        String response = "Noted. I've removed this task:\n"
                + "  " + deletedTask + "\n"
                + "Now you have " + taskList.size() + " tasks in the list.";
        return appendSavingWarning(response);
    }

    /** Marks the task at the given zero-based index as done. */
    private String markTask(int taskIndex) throws BoException {
        checkTaskIndex(taskIndex);
        Task task = taskList.get(taskIndex);
        task.markAsDone();
        String response = "Nice! I've marked this task as done:\n  " + task;
        return appendSavingWarning(response);
    }

    /** Marks the task at the given zero-based index as not done. */
    private String unmarkTask(int taskIndex) throws BoException {
        checkTaskIndex(taskIndex);
        Task task = taskList.get(taskIndex);
        task.unmarkAsDone();
        String response = "OK, I've marked this task as not done yet:\n  " + task;
        return appendSavingWarning(response);
    }

    /** Checks that a command refers to a task currently in the list. */
    private void checkTaskIndex(int taskIndex) throws BoException {
        if (taskIndex < 0 || taskIndex >= taskList.size()) {
            throw new BoException("I couldn't find a task with that number.");
        }
    }

    /** Formats numbered tasks under the supplied heading. */
    private String formatTasks(String heading, Task[] tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.length; i++) {
            response.append("\n").append(i + 1).append(".").append(tasks[i]);
        }
        return response.toString();
    }

    /** Persists the current list and returns a warning if persistence fails. */
    private String saveTasks() {
        try {
            Task[] tasks = taskList.toArray();
            Storage.save(tasks, tasks.length);
            return "";
        } catch (IOException | IllegalArgumentException | SecurityException exception) {
            return "Warning: I couldn't save your tasks to disk.";
        }
    }

    /** Appends a persistence warning without hiding a successful mutation. */
    private String appendSavingWarning(String response) {
        String savingWarning = saveTasks();
        return savingWarning.isEmpty() ? response : response + "\n" + savingWarning;
    }

    /** Formats warnings about malformed or excess saved records. */
    private static String formatLoadingWarnings(Storage.LoadResult result) {
        StringBuilder warning = new StringBuilder();
        if (result.getInvalidLineCount() > 0) {
            warning.append("Warning: I skipped ").append(result.getInvalidLineCount())
                    .append(" invalid task line(s) in the saved file.");
        }
        if (result.getExcessTaskCount() > 0) {
            if (!warning.isEmpty()) {
                warning.append("\n");
            }
            warning.append("Warning: I could only load the first ")
                    .append(TaskList.DEFAULT_CAPACITY).append(" tasks from the saved file.");
        }
        return warning.toString();
    }
}
