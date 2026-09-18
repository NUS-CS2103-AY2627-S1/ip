package chausistant.command;

import chausistant.exception.ChausistantException;
import chausistant.task.TaskList;

/**
 * Shared validation for commands that operate on one numbered task.
 */
public abstract class NumberedTaskCommand extends Command {
    private final String action;
    private final String taskNumberText;

    /**
     * Creates a command for the task number entered after an action keyword.
     *
     * @param action the command keyword used in validation messages
     * @param taskNumberText the task number entered by the user
     */
    protected NumberedTaskCommand(String action, String taskNumberText) {
        this.action = action;
        this.taskNumberText = taskNumberText;
    }

    /** Converts and validates the entered one-based task number. */
    protected int getTaskIndex(TaskList tasks) throws ChausistantException {
        if (taskNumberText.isBlank()) {
            throw new ChausistantException("Use: " + action + " <task number>.");
        }

        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException error) {
            throw new ChausistantException("A task number must be a whole number.");
        }

        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new ChausistantException("There is no task numbered " + taskNumber + ".");
        }
        return taskIndex;
    }
}
