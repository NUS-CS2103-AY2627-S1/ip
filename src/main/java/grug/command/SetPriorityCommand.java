package grug.command;

import java.io.IOException;
import java.util.Optional;

import grug.storage.TaskStorage;
import grug.task.Task;
import grug.task.TaskList;
import grug.task.TaskPriority;

/**
 * Command to set / update the priority of the {@code tasknum}-th (1-based)
 * task.
 *
 * @param taskNum     The 1-based position of the task in the task list.
 * @param newPriority The new priority of the task.
 */
public record SetPriorityCommand(int taskNum, TaskPriority newPriority) implements GrugCommand {
    /**
     * Unmarks the {@link #taskNum()}-th task (1-based) as incomplete, then attempts
     * to save the list to disk.
     *
     * @param tasks   The task list to modify.
     * @param storage The task storage to save to.
     * @return A {@link CommandResult.Err} if index is out of range, with
     *         appropriate error {@code message} and {@code shouldExit: false}.
     *
     *         Else, a {@link CommandResult.Partial} if tasks failed to be
     *         saved to the {@code storage}, with {@code message} containing the
     *         updated task and the appropriate error message, and
     *         {@code shouldExit: false}.
     *
     *         Else, A {@link CommandResult.Ok} if both operations successful.
     */

    @Override
    public CommandResult execute(TaskList tasks, TaskStorage storage) {
        int taskIdx = taskNum - 1;
        Optional<Task> taskOptional = tasks.getTask(taskIdx);
        if (taskOptional.isEmpty()) {
            return new CommandResult.Err("Guh. Can't find task number %d".formatted(taskNum));
        }

        Task task = taskOptional.get();
        task.setPriority(newPriority);

        String msg = "Bazinga! Updated task %d: %s".formatted(taskNum, task);
        try {
            storage.saveTasks(tasks.getTasks());
            return new CommandResult.Ok(msg);
        } catch (IOException e) {
            String partialResultMessage = msg + "\nFailed to save tasks to " + storage.getFilepath();
            return new CommandResult.Partial(partialResultMessage);
        }
    }
}
