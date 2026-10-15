package minty.command;

import minty.task.Task;
import minty.task.TaskList;

/**
 * Marks one task as not completed.
 */
public class UnmarkCommand extends IndexedTaskCommand {

    /**
     * Creates an unmark command from the user's complete input.
     *
     * @param fullCommand complete unmark command.
     */
    public UnmarkCommand(String fullCommand) {
        super(fullCommand, CommandType.UNMARK);
    }

    /**
     * Unmarks the selected task.
     *
     * @param tasks task list to update.
     * @param taskIndex validated zero-based task index.
     * @return unmarked task.
     */
    @Override
    protected Task updateTask(TaskList tasks, int taskIndex) {
        return tasks.unmark(taskIndex);
    }

    /**
     * Returns Minty's unmark confirmation.
     *
     * @return unmark confirmation message.
     */
    @Override
    protected String getConfirmationMessage() {
        return "Ready for another round! Marked as not done:";
    }
}
