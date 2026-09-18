package duck.command;

import java.util.Objects;

import duck.storage.Storage;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    /** Keyword to search for in task descriptions. */
    private final String keyword;

    /**
     * Creates a command that searches for the supplied keyword.
     *
     * @param keyword Non-blank keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = Objects.requireNonNull(keyword, "Find keyword cannot be null.");
        if (keyword.isBlank()) {
            throw new IllegalArgumentException("Find keyword cannot be blank.");
        }
    }

    /** Shows all tasks with descriptions that contain the keyword. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.getTasksSnapshot(), tasks.find(this.keyword));
    }
}
