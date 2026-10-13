package duke;

import java.util.List;

/**
 * Loads and saves complete task lists, keeping file handling separate from task logic.
 */
public interface TaskRepository {
    /**
     * Loads tasks from the repository.
     *
     * @return Saved tasks in their original order.
     * @throws StorageException If the repository cannot provide valid task data.
     */
    List<Task> loadTasks() throws StorageException;

    /**
     * Saves the supplied tasks to the repository.
     *
     * @param tasks Tasks to persist.
     * @throws StorageException If the repository cannot save the tasks.
     */
    void saveTasks(List<Task> tasks) throws StorageException;
}
