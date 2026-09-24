package dobby.command;

import dobby.logic.DobbyLogic;

/**
 * Displays tasks whose descriptions contain a search keyword.
 */
public final class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword keyword to find in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Displays tasks whose descriptions contain the keyword. */
    @Override
    public String execute(DobbyLogic logic) {
        return logic.findTasks(keyword);
    }
}
