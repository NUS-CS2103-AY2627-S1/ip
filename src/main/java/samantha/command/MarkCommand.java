package samantha.command;

import samantha.exception.InputException;
import samantha.exception.TaskFileWriteException;
import samantha.model.Task;

/**
 * Represents the command that marks a task as complete.
 */
public class MarkCommand extends Command {
    private final int taskId;
    private boolean wasDone;

    /**
     * Creates a command for the given one-based task ID.
     *
     * @param taskId task to mark as complete
     */
    public MarkCommand(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Indicates that marking this task can be undone.
     *
     * @return {@code true}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Marks and saves the selected task.
     *
     * @param context current application state and persistence handlers.
     * @return confirmation for the marked task
     * @throws InputException if the task ID does not exist
     * @throws TaskFileWriteException if saving fails
     */
    @Override
    public String execute(CommandContext context)
            throws InputException, TaskFileWriteException {
        Task task = getTask(context.getTasks(), taskId);
        wasDone = task.isDone();
        task.markDone();
        saveTasks(context.getTasks(), context.getTaskStorage());
        return "That's done:\n  " + task;
    }

    /**
     * Restores the task's completion state from before this command.
     *
     * @param context current application state and persistence handlers
     * @return confirmation for the undone command
     * @throws InputException if the task no longer exists
     * @throws TaskFileWriteException if the task list cannot be saved
     */
    @Override
    public String undo(CommandContext context) throws InputException, TaskFileWriteException {
        Task task = getTask(context.getTasks(), taskId);
        if (wasDone) {
            task.markDone();
        } else {
            task.markNotDone();
        }
        saveTasks(context.getTasks(), context.getTaskStorage());
        return "I've undone the last command.";
    }
}
