package soar.command;

import java.util.Objects;

import soar.storage.Storage;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Shows tasks whose descriptions contain a keyword or phrase.
 */
public class FindCommand extends Command {
    /** Search text already validated by the parser. */
    private final String keyword;

    /**
     * Creates a task-description search.
     *
     * @param keyword non-empty keyword or phrase to find
     */
    public FindCommand(String keyword) {
        this.keyword = Objects.requireNonNull(keyword, "Search keyword must not be null");
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findByDescription(keyword));
    }
}
