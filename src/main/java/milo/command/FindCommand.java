package milo.command;

import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Represents the "find" command, which shows the tasks whose description
 * contains a given keyword. Matching is a plain, case-sensitive substring
 * check: it does not read the keyword as a date or a pattern, since a task's
 * description is free text to begin with.
 */
public class FindCommand extends Command {
    /** The text to search for in each task's description. */
    private final String keyword;

    /**
     * Creates a command that will search for the given keyword.
     *
     * @param keyword the text to search for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword this command will search for.
     * Lets a caller (notably a test verifying what {@link Parser} built)
     * inspect the keyword without triggering {@link #execute}'s side effects.
     *
     * @return the keyword this command carries
     */
    public String getKeyword() {
        return keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showResponse(tasks.getMatchingDisplayLines(keyword));
    }
}
