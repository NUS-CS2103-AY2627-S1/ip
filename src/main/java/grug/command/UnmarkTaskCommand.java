package grug.command;

import java.io.IOException;
import java.util.Optional;

import grug.storage.TaskStorage;
import grug.task.Task;
import grug.task.TaskList;

/**
 * Command to unmark a task (i.e. mark as incomplete).
 *
 * @param taskNum The 1-based task number.
 */
public record UnmarkTaskCommand(int taskNum) implements GrugCommand {
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

        // empty optional if index out of bounds
        Optional<Task> optionalTask = tasks.getTask(taskIdx);
        if (optionalTask.isEmpty()) {
            return new CommandResult.Err("Guh. Can't find task number %d".formatted(taskNum));
        }

        Task task = optionalTask.get();
        task.markIncomplete();

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
