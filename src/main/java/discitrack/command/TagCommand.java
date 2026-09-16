package discitrack.command;

import java.io.IOException;
import java.util.List;

import discitrack.exception.DisciTrackException;
import discitrack.exception.SaveException;
import discitrack.storage.Storage;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Adds a tag and saves it, or restores the original task if saving fails.
 */
public class TagCommand extends Command {
    private final int taskNumber;
    private final String tag;
    private final boolean isRemoval;

    /**
     * Creates a command to tag one task.
     *
     * @param taskNumber the full-list task number.
     * @param tag the tag name.
     */
    public TagCommand(int taskNumber, String tag) {
        this(taskNumber, tag, false);
    }

    protected TagCommand(int taskNumber, String tag, boolean isRemoval) {
        this.taskNumber = taskNumber;
        this.tag = tag;
        this.isRemoval = isRemoval;
    }

    /**
     * Changes one tag after validating the task number and tag name.
     *
     * @param tasks the task list.
     * @param ui the response interface.
     * @param storage the task storage.
     * @throws DisciTrackException if the number or tag is invalid, or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DisciTrackException {
        Task task = tasks.get(taskNumber);
        try {
            Task.validateTag(tag);
        } catch (IllegalArgumentException e) {
            throw new DisciTrackException(e.getMessage());
        }
        String existing = task.getTag(tag);
        if (!isRemoval && existing != null) {
            ui.showTagUnchanged(taskNumber, existing, true);
            return;
        }
        if (isRemoval && existing == null) {
            ui.showTagUnchanged(taskNumber, tag, false);
            return;
        }
        List<String> originalTags = task.getTags();
        if (isRemoval) {
            task.removeTag(tag);
        } else {
            task.addTag(tag);
        }
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            task.replaceTags(originalTags);
            throw new SaveException(e);
        }
        ui.showTagChanged(taskNumber, isRemoval ? existing : tag, task, isRemoval);
    }
}
