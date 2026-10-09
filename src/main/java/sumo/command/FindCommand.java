package sumo.command;

import java.io.IOException;

import sumo.storage.Storage;
import sumo.task.TaskList;
import sumo.ui.Ui;

/** Displays tasks whose descriptions contain a keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword text to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Displays tasks whose descriptions contain this command's keyword. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
