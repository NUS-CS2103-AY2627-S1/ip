package habpyduck.command;

import habpyduck.HabpyDuckException;
import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Marks one task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that unmarks the task with the given user-facing number.
     *
     * @param taskNumber the one-based task number entered by the user.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task as not done, saves the updated list, and reports the change.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show command results.
     * @param storage the storage used to save tasks.
     * @throws HabpyDuckException if the task does not exist or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException {
        int taskIndex = requireValidTaskIndex(taskNumber, tasks);
        if (!tasks.get(taskIndex).isDone()) {
            throw new HabpyDuckException("OH NO!!! This task is already unmarked, friend.");
        }

        tasks.markAsNotDone(taskIndex);
        try {
            storage.saveTasks(tasks.asList());
        } catch (HabpyDuckException e) {
            assert taskIndex >= 0 && taskIndex < tasks.size()
                    : "Unmarked task should still exist so the status change can be rolled back";
            tasks.markAsDone(taskIndex);
            throw e;
        }
        ui.showTaskUnmarked(tasks.get(taskIndex));
    }
}
