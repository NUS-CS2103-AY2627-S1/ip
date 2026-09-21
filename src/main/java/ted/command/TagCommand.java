package ted.command;

import java.util.List;

import ted.Storage;
import ted.TedException;
import ted.Ui;
import ted.task.Tag;
import ted.task.Task;
import ted.task.TaskList;

/**
 * Attaches tags to a task, or detaches them.
 * Tagging and untagging differ only in which way the task's tags change and in
 * the wording, so one class covers both, the way {@link MarkCommand} covers
 * mark and unmark.
 */
public class TagCommand extends Command {
    /** Zero-based position of the task to change. */
    private final int index;

    /** The tags to attach or detach. */
    private final List<Tag> tags;

    /** {@code true} to attach the tags, {@code false} to detach them. */
    private final boolean isAdding;

    /**
     * Creates a command that changes one task's tags.
     *
     * @param index    zero-based position of the task.
     * @param tags     the tags to attach or detach.
     * @param isAdding {@code true} to attach them, {@code false} to detach them.
     */
    public TagCommand(int index, List<Tag> tags, boolean isAdding) {
        this.index = index;
        this.tags = List.copyOf(tags);
        this.isAdding = isAdding;
    }

    /**
     * Changes the task's tags, shows the result, and saves the list.
     *
     * @throws TedException if no task has that number, an untag names a tag the
     *                      task does not have, or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TedException {
        Task task = tasks.get(index);
        if (isAdding) {
            task.addTags(tags);
        } else {
            requireAllPresent(task);
            task.removeTags(tags);
        }

        ui.showTagged(task, isAdding);
        storage.save(tasks);
    }

    /**
     * Rejects an untag that names a tag the task does not have. Every tag is
     * checked before any is removed, so a typo in one of them cannot leave the
     * task half changed.
     *
     * @param task the task about to lose some of its tags.
     * @throws TedException if the task is missing one of the tags.
     */
    private void requireAllPresent(Task task) throws TedException {
        for (Tag tag : tags) {
            if (!task.hasTag(tag)) {
                throw new TedException("Task " + (index + 1) + " doesn't have the tag " + tag
                        + ", so I left its tags alone.");
            }
        }
    }
}
