package grug.command;

import java.io.IOException;

import grug.storage.TaskStorage;
import grug.task.Task;
import grug.task.TaskList;

/**
 * Command to add a task to the list of tasks.
 *
 * @param task The task to add.
 */
public record AddTaskCommand(Task task) implements GrugCommand {
    /**
     * Adds a new {@link Task} to the tasks list, then attempts to save the
     * save the list to disk.
     *
     * @param tasks   The task list to add to.
     * @param storage The task storage to save to.
     * @return The result of the execution. Guaranteed to be {@code Ok}, or
     *         {@code Partial} if saving tasks to storage failed.
     */
    @Override
    public CommandResult execute(TaskList tasks, TaskStorage storage) {
        tasks.addTask(task);

        String msg = "Bazinga! Added: " + task;
        try {
            storage.saveTasks(tasks.getTasks());
            return new CommandResult.Ok(msg);
        } catch (IOException e) {
            String partialResultMessage = msg + "\nFailed to save tasks to " + storage.getFilepath();
            return new CommandResult.Partial(partialResultMessage);
        }
    }
}
