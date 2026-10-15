package grug.command;

import java.io.IOException;
import java.util.Optional;

import grug.storage.TaskStorage;
import grug.task.Task;
import grug.task.TaskList;

/**
 * Command to delete a task based on the task number (1-based).
 *
 * @param taskNum The task to delete.
 */
public record DeleteTaskCommand(int taskNum) implements GrugCommand {
    /**
     * Deletes the {@link #taskNum()}-th task (1-based) from the task list, then
     * attempts to save the list to disk.
     *
     * @param tasks   The task list to delete the task from.
     * @param storage The task storage to save to.
     * @return An {@code Err} if {@link #taskNum()} is out of range of the list,
     *         {@code Partial} if saving tasks to storage failed, or {@code Ok}
     *         otherwise.
     */
    @Override
    public CommandResult execute(TaskList tasks, TaskStorage storage) {
        int taskIdx = taskNum - 1;

        // empty optional if index out of bounds
        Optional<Task> optionalRemoved = tasks.removeTask(taskIdx);
        if (optionalRemoved.isEmpty()) {
            return new CommandResult.Err("Guh. Can't find task number %d".formatted(taskNum));
        }

        Task removedTask = optionalRemoved.get();

        String msg = "Bazinga! Deleted: %s".formatted(removedTask);

        try {
            storage.saveTasks(tasks.getTasks());
            return new CommandResult.Ok(msg);
        } catch (IOException e) {
            String partialResultMessage = msg + "\nFailed to save tasks to " + storage.getFilepath();
            return new CommandResult.Partial(partialResultMessage);
        }
    }
}
