package samantha.command;

import samantha.exception.InputException;
import samantha.exception.TaskFileWriteException;
import samantha.model.Task;

/**
 * Represents the command that removes a task.
 */
public class DeleteCommand extends Command {
    private final int taskId;
    private Task deletedTask;
    private int deletedTaskIndex;

    /**
     * Creates a command for the given one-based task ID.
     *
     * @param taskId task to remove
     */
    public DeleteCommand(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Indicates that deleting this task can be undone.
     *
     * @return {@code true}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Removes and saves the selected task.
     *
     * @param context current application state and persistence handlers.
     * @return confirmation for the removed task
     * @throws InputException if the task ID does not exist
     * @throws TaskFileWriteException if saving fails
     */
    @Override
    public String execute(CommandContext context)
            throws InputException, TaskFileWriteException {
        Task task = getTask(context.getTasks(), taskId);
        deletedTask = context.getTasks().removeTask(taskId);
        deletedTaskIndex = taskId - 1;
        saveTasks(context.getTasks(), context.getTaskStorage());
        return "I've taken that off the list:\n  "
                + task + "\n"
                + String.format("Now you have %d tasks in the list.", context.getTasks().size());
    }

    /**
     * Restores the task deleted by this command.
     *
     * @param context current application state and persistence handlers
     * @return confirmation for the undone command
     * @throws InputException if the deleted task has not been recorded
     * @throws TaskFileWriteException if the task list cannot be saved
     */
    @Override
    public String undo(CommandContext context) throws InputException, TaskFileWriteException {
        if (deletedTask == null) {
            throw new InputException("This command cannot be undone.");
        }
        context.getTasks().addTaskAt(deletedTask, deletedTaskIndex);
        saveTasks(context.getTasks(), context.getTaskStorage());
        return "I've undone the last command.";
    }
}
