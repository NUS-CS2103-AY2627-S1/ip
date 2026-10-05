package kaykay.command;

import java.io.IOException;

import kaykay.model.ApplicationData;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/** Displays tasks whose descriptions contain a requested keyword. */
public final class FindCommand extends Command {
    /** Keyword to search for in task descriptions. */
    private final String keyword;

    /**
     * Creates a find command for a keyword.
     *
     * @param keyword keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks whose descriptions contain the command keyword.
     *
     * @param data application data containing the task list to search.
     * @param ui UI used to display matching tasks.
     * @param storage unused storage component supplied by the command interface.
     * @throws IOException declared by the command interface; not thrown by searching.
     */
    @Override
    public void execute(ApplicationData data, Ui ui, Storage storage) throws IOException {
        ui.showMatchingTasks(data.getTasks().findTasks(keyword));
    }
}
