package glennon.command;

import glennon.Storage;
import glennon.Ui;
import glennon.task.TaskList;

/**
 * Displays missions whose descriptions contain a keyword.
 */
public final class FindCommand extends Command {
    /** Keyword to find in mission descriptions. */
    private final String keyword;

    /**
     * Creates a command that searches mission descriptions.
     *
     * @param keyword case-sensitive keyword to find.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays matching missions without changing stored state.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used by the application.
     */
    @Override
    public void execute(TaskList missions, Ui ui, Storage storage) {
        ui.showMatchingMissions(missions.findByDescription(keyword), missions.asList());
    }
}
