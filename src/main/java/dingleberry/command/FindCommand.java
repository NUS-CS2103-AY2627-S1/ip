package dingleberry.command;

import dingleberry.model.TaskList;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Prints tasks whose string representation contains the given keyword. */
public class FindCommand extends Command {
    /** Stores the case-insensitive search keyword. */
    private final String keyword;

    /**
     * Creates a command that searches for the given keyword.
     *
     * @param searchKeyword the keyword to match against task descriptions.
     */
    public FindCommand(final String searchKeyword) {
        this.keyword = searchKeyword;
    }

    @Override
    public final void execute(final TaskList tasks, final Ui ui,
                              final Storage storage) {
        ui.showTaskList(tasks.findByKeyword(keyword));
    }
}
