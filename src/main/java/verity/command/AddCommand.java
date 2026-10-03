package verity.command;

import java.io.IOException;

import verity.exception.VerityException;
import verity.task.Task;
import verity.task.TaskList;

/**
 * Represents a command that adds a task.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        super(false);
        this.task = task;
    }

    /**
     * Adds the task, saves the updated task list, and returns feedback.
     *
     * @param context Command execution context.
     * @return User-facing response after execution.
     * @throws IOException If the task list cannot be saved.
     * @throws VerityException If a referenced client does not exist.
     */
    @Override
    public String execute(CommandContext context)
            throws IOException, VerityException {
        TaskList tasks = context.getTasks();

        for (String clientId : task.getClientIds()) {
            if (!context.getClients().containsId(clientId)) {
                throw new VerityException(
                        "That client ID does not exist: " + clientId + ".");
            }
        }
        tasks.add(task);
        context.getStorage().saveTasks(tasks);
        return context.getUi()
                .getTaskAddedMessage(task, tasks.size());
    }
}
