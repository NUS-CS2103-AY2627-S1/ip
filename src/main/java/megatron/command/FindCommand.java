package megatron.command;

import java.util.Objects;

import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public final class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the specified keyword.
     */
    public FindCommand(String keyword) {
        this.keyword = Objects.requireNonNull(keyword);
    }

    /**
     * Finds and displays matching tasks without changing stored data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) {
        ui.showMatchingTasks(tasks.findMatches(keyword));
    }
}
