package greenchonk.command;

import java.util.stream.IntStream;

import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks matching the specified keyword.
     *
     * @param keyword the keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int[] matchingTaskIndexes = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).matches(keyword))
                .toArray();

        if (matchingTaskIndexes.length == 0) {
            ui.showNoMatchingTasks();
            return;
        }

        ui.showFindHeader();
        for (int taskIndex : matchingTaskIndexes) {
            ui.showNumberedTask(taskIndex + 1, tasks.get(taskIndex));
        }
    }
}
