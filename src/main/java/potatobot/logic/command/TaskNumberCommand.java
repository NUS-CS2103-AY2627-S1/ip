package potatobot.logic.command;

import potatobot.exception.PotatoBotException;
import potatobot.model.task.TaskList;

/**
 * Represents a command that operates on a task selected by its displayed
 * number.
 */
public abstract class TaskNumberCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for the task with the specified displayed number.
     *
     * @param taskNumber Displayed number of the task to use.
     */
    protected TaskNumberCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Validates the displayed task number and converts it to a zero-based index.
     *
     * @param tasks Task list containing the selected task.
     * @return Zero-based index of the selected task.
     * @throws PotatoBotException If the displayed number is outside the task list.
     */
    protected int getTaskIndex(TaskList tasks) throws PotatoBotException {
        if (taskNumber <= 0) {
            throw new PotatoBotException("Do you hear yourself??");
        }
        if (taskNumber > tasks.size()) {
            throw new PotatoBotException(
                    "Think again... We only got " + tasks.size() + " items in the list...");
        }
        int taskIndex = taskNumber - 1;

        // Validation above must make the displayed-number conversion safe for list
        // access.
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Validated task index must be within the list";

        return taskIndex;
    }
}
