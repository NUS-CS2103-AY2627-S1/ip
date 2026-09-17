package panda.lifecycle;

import java.io.IOException;
import java.util.List;

import panda.exception.ApplicationException;
import panda.exception.storage.FileCorruptedException;
import panda.exception.storage.TaskLoadingException;
import panda.exception.task.EventClashException;
import panda.storage.Storage;
import panda.task.Task;
import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Handles Panda's greeting and task-list initialization at startup.
 */
public final class StartManager {

    private StartManager() {
        // Utility class: prevent accidental instantiation.
    }

    /**
     * Greets the user and initializes the task list from storage.
     *
     * @return Initialized task list.
     * @throws ApplicationException If saved tasks cannot be loaded.
     */
    public static TaskList start() throws ApplicationException {
        greet();
        return loadTaskList();
    }

    /**
     * Instantiates TaskList for the application.
     *
     * @return Initialized task list.
     * @throws TaskLoadingException If saved tasks cannot be loaded.
     */
    private static TaskList loadTaskList() throws TaskLoadingException {
        return createTaskList(loadTasks());
    }

    /**
     * Creates a task list and adds decoded tasks to it one at a time.
     *
     * @param tasks Decoded tasks to load.
     * @return Fully initialized task list.
     * @throws TaskLoadingException If stored events overlap.
     */
    static TaskList createTaskList(List<Task> tasks) throws TaskLoadingException {
        TaskList taskList = new TaskList();
        try {
            for (Task task : tasks) {
                taskList.addTask(task);
            }
        } catch (EventClashException exception) {
            FileCorruptedException corruption = new FileCorruptedException(
                    "Stored events overlap.", exception);
            throw createTaskLoadingException(corruption);
        }
        return taskList;
    }

    /**
     * Loads saved tasks and translates storage failures into a recoverable application error.
     *
     * @return Tasks loaded from storage.
     * @throws TaskLoadingException If the save file cannot be read or contains invalid data.
     */
    private static List<Task> loadTasks() throws TaskLoadingException {
        try {
            return Storage.readTasks();
        } catch (FileCorruptedException exception) {
            throw createTaskLoadingException(exception);
        } catch (IOException exception) {
            throw new TaskLoadingException(
                    "Your old training plan gave up before you did. We're starting fresh: "
                            + exception.getMessage(),
                    exception);
        }
    }

    private static TaskLoadingException createTaskLoadingException(FileCorruptedException exception) {
        return new TaskLoadingException(
                "Your old training plan is a mess. Honestly, this tracks. We're starting fresh."
                        + System.lineSeparator()
                        + exception.getMessage(),
                exception);
    }

    /**
     * Prints Panda's initial graphical-interface greeting.
     */
    private static void greet() {
        Ui.printMessage("Oh, you're back, trainee. I was just enjoying the peace. What's the issue today?");
    }
}
