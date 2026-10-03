package verity.command;

import java.io.IOException;

import verity.task.Task;
import verity.task.TaskList;

/**
 * Represents a command that marks a task as incomplete.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates an unmark command.
     *
     * @param taskIndex Zero-based index of the task.
     */
    public UnmarkCommand(int taskIndex) {
        super(false);
        this.taskIndex = taskIndex;
    }

    /**
     * Unmarks the task and saves the updated task list.
     *
     * @param context Command execution context.
     * @return User-facing response after execution.
     * @throws IOException If the task list cannot be saved.
     */
    @Override
    public String execute(CommandContext context)
            throws IOException {
        TaskList tasks = context.getTasks();

        Task unmarkedTask = tasks.unmark(taskIndex);
        context.getStorage().saveTasks(tasks);
        return context.getUi().getTaskUnmarkedMessage(unmarkedTask);
    }
}
