package habpyduck.command;

import java.util.ArrayList;

import habpyduck.HabpyDuckException;
import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Adds one or more tags to a task.
 */
public class TagCommand extends Command {
    private final int taskNumber;
    private final ArrayList<String> tags;

    /**
     * Creates a command that adds tags to the given user-facing task number.
     *
     * @param taskNumber the one-based task number entered by the user.
     * @param tags the normalized tags to add.
     */
    public TagCommand(int taskNumber, ArrayList<String> tags) {
        this.taskNumber = taskNumber;
        this.tags = new ArrayList<>(tags);
    }

    /**
     * Adds tags to the task, saves the updated list, and reports the tagged task.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show command results.
     * @param storage the storage used to save tasks.
     * @throws HabpyDuckException if the task does not exist, all tags are duplicates, or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException {
        int taskIndex = requireValidTaskIndex(taskNumber, tasks);
        ArrayList<String> addedTags = new ArrayList<>();
        for (String tag : tags) {
            if (tasks.addTag(taskIndex, tag)) {
                addedTags.add(tag);
            }
        }
        if (addedTags.isEmpty()) {
            throw new HabpyDuckException("OH NO!!! This task already has those tag(s), friend.");
        }

        try {
            storage.saveTasks(tasks.asList());
        } catch (HabpyDuckException e) {
            rollbackAddedTags(tasks, taskIndex, addedTags);
            throw e;
        }
        ui.showTaskTagged(tasks.get(taskIndex));
    }

    /**
     * Removes tags that were added before saving failed.
     *
     * @param tasks the task list to restore.
     * @param taskIndex the task that was tagged.
     * @param addedTags the tags that need to be removed.
     */
    private void rollbackAddedTags(TaskList tasks, int taskIndex, ArrayList<String> addedTags) {
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Tagged task should still exist so added tags can be rolled back";
        for (String tag : addedTags) {
            tasks.removeTag(taskIndex, tag);
        }
    }
}
