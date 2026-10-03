package verity.command;

import java.io.IOException;

import verity.task.Task;
import verity.task.TaskList;

/**
 * Represents a command that marks a task as completed.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a mark command.
     *
     * @param taskIndex Zero-based index of the task.
     */
    public MarkCommand(int taskIndex) {
        super(false);
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the task and saves the updated task list.
     *
     * @param context Command execution context.
     * @return User-facing response after execution.
     * @throws IOException If the task list cannot be saved.
     */
    @Override
    public String execute(CommandContext context)
            throws IOException {
        TaskList tasks = context.getTasks();

        Task markedTask = tasks.mark(taskIndex);
        context.getStorage().saveTasks(tasks);
        return context.getUi().getTaskMarkedMessage(markedTask);
    }
}
