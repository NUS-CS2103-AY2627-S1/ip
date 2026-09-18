package plana.command;

import java.util.List;
import java.util.stream.IntStream;

import plana.storage.Storage;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Displays tasks whose descriptions contain a supplied keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command for a keyword.
     *
     * @param keyword the keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays matching tasks in their original task-list order and numbers.
     *
     * @param tasks the task list to search.
     * @param ui the user interface used for the search response.
     * @param storage unused by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasksHeader();
        List<Integer> matchingTaskIndexes = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).matchesKeyword(keyword))
                .boxed()
                .toList();

        matchingTaskIndexes.forEach(index -> ui.showTask(index, tasks.get(index)));
        if (matchingTaskIndexes.isEmpty()) {
            ui.showNoMatchingTasks();
        }
        ui.showLine();
    }
}
