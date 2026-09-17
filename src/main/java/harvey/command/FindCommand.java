package harvey.command;

import harvey.HarveyException;
import harvey.storage.Storage;
import harvey.task.TaskList;
import harvey.ui.Ui;

/**
 * Shows the tasks whose description contains a keyword.
 * <p>
 * Like {@link ListCommand} this only reads the list, so it does not save. The matches are
 * numbered from 1 among themselves; marking or deleting still uses the numbers shown by
 * {@code list}, since those are the positions in the real list.
 */
public class FindCommand extends Command {
    /** The keyword to search for, as the user typed it. */
    private final String keyword;

    /**
     * Creates a command that will search for one keyword.
     *
     * @param keyword the text typed after the command word.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws HarveyException {
        if (keyword.isEmpty()) {
            throw new HarveyException("Tell me what to search for. For example: "
                    + CommandType.FIND.getExample());
        }

        TaskList matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            // Reported as a problem rather than an empty list, so the reply is never
            // just a heading with nothing under it.
            throw new HarveyException("Nothing on your docket matches \"" + keyword + "\".");
        }
        return ui.formatMatchingTasks(matches);
    }
}
