package habpyduck.command;

import habpyduck.HabpyDuckException;
import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Marks one task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the task with the given user-facing number.
     *
     * @param taskNumber the one-based task number entered by the user.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task as done, saves the updated list, and reports the change.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show command results.
     * @param storage the storage used to save tasks.
     * @throws HabpyDuckException if the task does not exist or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException {
        int taskIndex = requireValidTaskIndex(taskNumber, tasks);
        if (tasks.get(taskIndex).isDone()) {
            throw new HabpyDuckException("OH NO!!! This task is already marked, friend.");
        }

        tasks.markAsDone(taskIndex);
        try {
            storage.saveTasks(tasks.asList());
        } catch (HabpyDuckException e) {
            assert taskIndex >= 0 && taskIndex < tasks.size()
                    : "Marked task should still exist so the status change can be rolled back";
            tasks.markAsNotDone(taskIndex);
            throw e;
        }
        ui.showTaskMarked(tasks.get(taskIndex));
    }
}
