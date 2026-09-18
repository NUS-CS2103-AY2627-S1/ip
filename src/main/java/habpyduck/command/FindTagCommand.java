package habpyduck.command;

import java.util.ArrayList;

import habpyduck.storage.Storage;
import habpyduck.task.Task;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Finds tasks that have a specific tag.
 */
public class FindTagCommand extends Command {
    private final String tag;

    /**
     * Creates a command that searches for tasks with the given tag.
     *
     * @param tag the normalized tag to search for.
     */
    public FindTagCommand(String tag) {
        this.tag = tag;
    }

    /**
     * Finds tasks with the tag and shows them to the user.
     *
     * @param tasks the task list to search.
     * @param ui the UI used to show command results.
     * @param storage the storage used by other commands.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> taggedTasks = tasks.findByTag(tag);
        ui.showTaggedTasks(taggedTasks);
    }
}
