package rudra.command;

import java.util.ArrayList;
import java.util.List;

import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword Search term entered by the user.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.matchesKeyword(this.keyword))
                .toList();
        ui.showMatchingTasks(matchingTasks);
    }
}
