package pulbot.command;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/** A user command that can be executed against Pulbot's collaborators. */
public abstract class Command {
    /** Executes this command using the supplied application collaborators. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException;

    /** Returns whether executing this command should terminate Pulbot. */
    public boolean isExit() {
        return false;
    }

    /** Saves a completion change, restoring the previous status if saving fails. */
    protected void saveCompletionChange(TaskList tasks, Task task, boolean wasDone, Storage storage)
            throws PulbotException {
        try {
            storage.save(tasks);
        } catch (PulbotException e) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw e;
        }
    }

    /** Converts a displayed task number to a validated zero-based list index. */
    protected int getTaskIndex(String number, int taskCount) throws PulbotException {
        try {
            int index = Integer.parseInt(number) - 1;
            if (index < 0 || index >= taskCount) {
                throw new PulbotException("There is no task with that number. Please enter a valid task number.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new PulbotException("Please enter a valid task number.");
        }
    }
}
