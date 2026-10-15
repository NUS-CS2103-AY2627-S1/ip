package minty.command;

import minty.task.Task;
import minty.task.TaskList;

/**
 * Marks one task as completed.
 */
public class MarkCommand extends IndexedTaskCommand {

    /**
     * Creates a mark command from the user's complete input.
     *
     * @param fullCommand complete mark command.
     */
    public MarkCommand(String fullCommand) {
        super(fullCommand, CommandType.MARK);
    }

    /**
     * Marks the selected task.
     *
     * @param tasks task list to update.
     * @param taskIndex validated zero-based task index.
     * @return marked task.
     */
    @Override
    protected Task updateTask(TaskList tasks, int taskIndex) {
        return tasks.mark(taskIndex);
    }

    /**
     * Returns Minty's mark confirmation.
     *
     * @return mark confirmation message.
     */
    @Override
    protected String getConfirmationMessage() {
        return "Woohoo! Marked as done:";
    }
}
