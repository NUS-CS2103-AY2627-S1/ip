package parser;

import java.util.List;
import java.util.stream.Collectors;

import common.Constants;
import datahandler.DataHandler;
import exception.FriedbergException;
import exception.FriedbergInternalException;
import exception.FriedbergUserInputException;
import task.Task;
import task.TaskStatus;
import task.TaskStringParser;

/**
 * To be passed when executing a Command, so as to allow the command to act on
 * the context
 */
public class CommandContext {
    private static final String ARCHIVE_FILE_NAME = "friedberg_archive";

    private List<Task> tasks;
    private List<Task> archivedTasks;
    private final DataHandler dataHandler;
    private final DataHandler archiveDataHandler;

    /**
     * Initialise a new CommandContext without any arguments
     * Defaults to using constants set in the project
     *
     * @throws FriedbergException Wraps any Datahandler Exception thrown
     */
    public CommandContext() throws FriedbergException {
        try {
            this.dataHandler = new DataHandler(Constants.PROJECT_DATA_DIR_PATH, Constants.FRIEDBERG_DATA_FILE_PATH);
            this.archiveDataHandler = new DataHandler(Constants.PROJECT_DATA_DIR_PATH,
                    Constants.FRIEDBERG_ARCHIVE_FILE_PATH);
        } catch (Exception e) {
            throw new FriedbergInternalException(e.getMessage());
        }
        this.loadTasksFromDataHandler();
        this.loadArchivedTasksFromDataHandler();
    }

    /**
     * Initialises a new CommandContext with the given data handler.
     * This is useful for tests that need temporary storage.
     *
     * @param dataHandler data handler used to load and save tasks
     * @throws FriedbergException if tasks cannot be loaded or deserialized
     */
    public CommandContext(DataHandler dataHandler) throws FriedbergException {
        try {
            this.archiveDataHandler = new DataHandler(dataHandler.getDataFolderPath(),
                    dataHandler.getDataFolderPath().resolve(ARCHIVE_FILE_NAME));
        } catch (Exception e) {
            throw new FriedbergInternalException(e.getMessage());
        }
        this.dataHandler = dataHandler;
        this.loadTasksFromDataHandler();
        this.loadArchivedTasksFromDataHandler();
    }

    /**
     * Initialises a new CommandContext with separate handlers for normal and archived tasks.
     * This is useful for tests that need full control over both storage files.
     *
     * @param dataHandler data handler used to load and save normal tasks
     * @param archiveDataHandler data handler used to load and save archived tasks
     * @throws FriedbergException if tasks cannot be loaded or deserialized
     */
    public CommandContext(DataHandler dataHandler, DataHandler archiveDataHandler) throws FriedbergException {
        this.dataHandler = dataHandler;
        this.archiveDataHandler = archiveDataHandler;
        this.loadTasksFromDataHandler();
        this.loadArchivedTasksFromDataHandler();
    }

    /**
     * Returns the number of tasks in the current task list.
     *
     * @return number of tasks in the current task list
     */
    public int getTasksSize() {
        return this.tasks.size();
    }

    /**
     * Renders all current tasks as a multiline string.
     *
     * @return formatted task list for display
     */
    public String renderTasks() {
        return this.renderTaskList("Here are the tasks in your list:", this.tasks);
    }

    /**
     * Returns the number of tasks in the archive list.
     *
     * @return number of archived tasks
     */
    public int getArchivedTasksSize() {
        return this.archivedTasks.size();
    }

    /**
     * Renders all archived tasks as a multiline string.
     *
     * @return formatted archive list for display
     */
    public String renderArchivedTasks() {
        return this.renderTaskList("Here are the tasks in your archive:", this.archivedTasks);
    }

    /**
     * Marks the task at the given task index as done.
     *
     * @param taskIndex zero-based index of the task to mark
     * @throws FriedbergException if the task index is invalid
     */
    public void markTask(int taskIndex) throws FriedbergException {
        this.validateTaskIndex(taskIndex);
        this.tasks.get(taskIndex).mark();
        assert this.tasks.get(taskIndex).getStatus() == TaskStatus.DONE
                : "Marking a task must leave it done";
        this.saveTasksToDataHandler();
    }

    /**
     * Unmarks the task at the given task index as not done.
     *
     * @param taskIndex zero-based index of the task to unmark
     * @throws FriedbergException if the task index is invalid
     */
    public void unmarkTask(int taskIndex) throws FriedbergException {
        this.validateTaskIndex(taskIndex);
        this.tasks.get(taskIndex).unmark();
        assert this.tasks.get(taskIndex).getStatus() == TaskStatus.IN_PROGRESS
                : "Unmarking a task must leave it in progress";
        this.saveTasksToDataHandler();
    }

    /**
     * Returns the string to render the task for display.
     *
     * @param taskIndex zero-based index of the task to render
     * @return formatted task string for display
     */
    public String renderTask(int taskIndex) {
        return this.tasks.get(taskIndex).renderTask();
    }

    /**
     * Filters the tasks by the given name filter.
     *
     * @param nameFilter text that matching task names should contain
     * @return list of tasks whose names contain the filter text
     */
    public List<Task> filterTasks(String nameFilter) {
        return this.tasks.stream().filter(task -> task.getName().contains(nameFilter))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new task.
     *
     * @param task non-null task to add
     * @throws FriedbergException if the task cannot be saved
     */
    public void addTask(Task task) throws FriedbergException {
        assert task != null : "A task must not be null when added";
        this.tasks.add(task);
        this.saveTasksToDataHandler();
    }

    /**
     * Removes the task at the given task index.
     *
     * @param taskIndex zero-based index of the task to remove
     * @return the task that was removed
     * @throws FriedbergException if the task index is invalid or the updated list
     *                            cannot be saved
     */
    public Task removeTask(int taskIndex) throws FriedbergException {
        this.validateTaskIndex(taskIndex);
        Task removedTask = this.tasks.remove(taskIndex);
        this.saveTasksToDataHandler();
        return removedTask;
    }

    /**
     * Moves a task from the normal task list to the archive list.
     *
     * @param taskIndex zero-based index in the normal task list
     * @return the task that was archived
     * @throws FriedbergException if the index is invalid or either list cannot be saved
     */
    public Task archiveTask(int taskIndex) throws FriedbergException {
        this.validateTaskIndex(taskIndex);
        Task archivedTask = this.tasks.remove(taskIndex);
        this.archivedTasks.add(archivedTask);
        this.saveTasksToDataHandler();
        this.saveArchivedTasksToDataHandler();
        return archivedTask;
    }

    /**
     * Moves a task from the archive list back to the end of the normal task list.
     *
     * @param taskIndex zero-based index in the archive list
     * @return the task that was unarchived
     * @throws FriedbergException if the index is invalid or either list cannot be saved
     */
    public Task unarchiveTask(int taskIndex) throws FriedbergException {
        this.validateArchivedTaskIndex(taskIndex);
        Task unarchivedTask = this.archivedTasks.remove(taskIndex);
        this.tasks.add(unarchivedTask);
        this.saveTasksToDataHandler();
        this.saveArchivedTasksToDataHandler();
        return unarchivedTask;
    }

    private void validateTaskIndex(int taskIndex) throws FriedbergUserInputException {
        this.validateIndex(taskIndex, this.tasks.size());
    }

    private void validateArchivedTaskIndex(int taskIndex) throws FriedbergUserInputException {
        this.validateIndex(taskIndex, this.archivedTasks.size());
    }

    private void validateIndex(int taskIndex, int listSize) throws FriedbergUserInputException {
        if (taskIndex < 0 || taskIndex >= listSize) {
            throw new FriedbergUserInputException(
                    String.format("expected taskIndex to be in range of %d items", listSize));
        }
    }

    private String renderTaskList(String header, List<Task> taskList) {
        StringBuilder response = new StringBuilder(header);
        for (int i = 0; i < taskList.size(); i++) {
            response.append(String.format("\n%d. %s", i + 1, taskList.get(i).renderTask()));
        }
        return response.toString();
    }

    /**
     * Loads the tasks from the data handler.
     *
     * @throws FriedbergException if tasks cannot be loaded or deserialized
     */
    private void loadTasksFromDataHandler() throws FriedbergException {
        String tasksDataString;
        try {
            tasksDataString = this.dataHandler.read();
        } catch (Exception e) {
            throw new FriedbergInternalException(String.format("Unable to load data, e: %s", e.getMessage()));
        }
        this.tasks = TaskStringParser.deserializeTasks(tasksDataString);
        assert this.tasks != null : "Successful deserialization must return a task list";
        assert this.tasks.stream().allMatch(task -> task != null)
                : "A successfully loaded task list must not contain null entries";
    }

    /**
     * Loads the archived tasks from the archive data handler.
     *
     * @throws FriedbergException if archived tasks cannot be loaded or deserialized
     */
    private void loadArchivedTasksFromDataHandler() throws FriedbergException {
        String tasksDataString;
        try {
            tasksDataString = this.archiveDataHandler.read();
        } catch (Exception e) {
            throw new FriedbergInternalException(String.format("Unable to load archive data, e: %s", e.getMessage()));
        }
        this.archivedTasks = TaskStringParser.deserializeTasks(tasksDataString);
        assert this.archivedTasks != null : "Successful archive deserialization must return a task list";
        assert this.archivedTasks.stream().allMatch(task -> task != null)
                : "A successfully loaded archive list must not contain null entries";
    }

    /**
     * Saves the tasks to disk using the data handler.
     *
     * @throws FriedbergInternalException if tasks cannot be saved
     */
    private void saveTasksToDataHandler() throws FriedbergInternalException {
        String tasksDataString = TaskStringParser.serializeTasks(this.tasks);
        try {
            this.dataHandler.write(tasksDataString);
        } catch (Exception e) {
            throw new FriedbergInternalException(String.format("Unable to save data, e: %s", e.getMessage()));
        }
    }

    /**
     * Saves the archived tasks to disk using the archive data handler.
     *
     * @throws FriedbergInternalException if archived tasks cannot be saved
     */
    private void saveArchivedTasksToDataHandler() throws FriedbergInternalException {
        String tasksDataString = TaskStringParser.serializeTasks(this.archivedTasks);
        try {
            this.archiveDataHandler.write(tasksDataString);
        } catch (Exception e) {
            throw new FriedbergInternalException(String.format("Unable to save archive data, e: %s", e.getMessage()));
        }
    }
}
