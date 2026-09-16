package brucli.command;

import java.io.IOException;

import brucli.storage.Storage;
import brucli.task.TaskList;

/**
 * Base class for commands that modify one task and persist the task list.
 */
public abstract class TaskMutationCommand extends Command {
    protected final int taskId;

    /**
     * Creates a task mutation targeting a zero-based task ID.
     */
    protected TaskMutationCommand(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Persists the current immutable snapshot of the task list.
     */
    protected void saveTasks(TaskList tasks, Storage storage) throws IOException {
        storage.save(tasks.snapshot());
    }
}
