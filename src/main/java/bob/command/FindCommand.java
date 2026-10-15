package bob.command;

import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Prints the tasks whose description contains a keyword, in the order they
 * appear in the list.
 *
 * <p>Whether a task matches is decided by
 * {@link bob.task.Task#matchesKeyword Task.matchesKeyword}, so this command only
 * has to ask — exactly as {@link OnCommand} asks each task whether it falls on a
 * day. That keeps the rule about what counts as a match with the thing being
 * matched, and leaves this class holding only the wording.
 *
 * <p>Like the other listings this one changes nothing, so nothing is saved
 * afterwards and the {@link Storage} it is handed goes unused. The numbers shown
 * against the matches are their numbers in the whole list, so a task found here
 * can be marked or deleted without looking it up again.
 */
public class FindCommand extends Command {

    /** The text the user asked to search for. */
    private final String keyword;

    /**
     * Creates a command that will list the tasks mentioning a keyword.
     *
     * @param keyword the text to look for in each description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks, tasks.findIndexes(task -> task.matchesKeyword(keyword)),
                "Bob found dem:",
                "Bob no find \"" + keyword + "\". Bi-do!");
    }
}
