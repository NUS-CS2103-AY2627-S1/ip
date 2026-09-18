package habpyduck.command;

import habpyduck.HabpyDuckException;
import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Removes one tag from a task.
 */
public class UntagCommand extends Command {
    private final int taskNumber;
    private final String tag;

    /**
     * Creates a command that removes one tag from the given user-facing task number.
     *
     * @param taskNumber the one-based task number entered by the user.
     * @param tag the normalized tag to remove.
     */
    public UntagCommand(int taskNumber, String tag) {
        this.taskNumber = taskNumber;
        this.tag = tag;
    }

    /**
     * Removes the tag, saves the updated list, and reports the untagged task.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show command results.
     * @param storage the storage used to save tasks.
     * @throws HabpyDuckException if the task or tag does not exist, or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException {
        int taskIndex = requireValidTaskIndex(taskNumber, tasks);
        boolean hasRemovedTag = tasks.removeTag(taskIndex, tag);
        if (!hasRemovedTag) {
            throw new HabpyDuckException("OH NO!!! This task does not have that tag, friend.");
        }

        try {
            storage.saveTasks(tasks.asList());
        } catch (HabpyDuckException e) {
            assert taskIndex >= 0 && taskIndex < tasks.size()
                    : "Untagged task should still exist so removed tags can be rolled back";
            tasks.addTag(taskIndex, tag);
            throw e;
        }
        ui.showTaskUntagged(tasks.get(taskIndex));
    }
}
