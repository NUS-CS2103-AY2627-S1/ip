package ted.command;

import ted.Storage;
import ted.Ui;
import ted.task.Tag;
import ted.task.TaskList;

/**
 * Shows only the tasks that have a given tag.
 * <p>
 * Kept apart from {@link FindCommand} because matching a whole tag is a
 * different rule from finding text inside a description. Like a keyword
 * search, it only looks at the list, so nothing is saved.
 */
public class FindByTagCommand extends Command {
    /** The tag a task must have to be shown. */
    private final Tag tag;

    /**
     * Creates a command that shows the tasks with the given tag.
     *
     * @param tag the tag to look for.
     */
    public FindByTagCommand(Tag tag) {
        this.tag = tag;
    }

    /** Shows the tasks with the tag. Nothing is changed, so nothing is saved. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, tasks.findByTag(tag), tag.toString());
    }
}
