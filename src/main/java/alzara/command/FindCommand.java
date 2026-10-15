package alzara.command;

import java.util.ArrayList;
import java.util.stream.Collectors;

import alzara.storage.Storage;
import alzara.task.Task;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Finds every task whose description contains all of the given keywords and
 * displays them.
 */
public class FindCommand extends Command {
    private final String[] keywords;

    /**
     * Creates a command that finds every task matching all of {@code keywords}.
     *
     * @param keywords the keywords to search task descriptions for
     */
    public FindCommand(String... keywords) {
        this.keywords = keywords;
    }

    /**
     * Filters {@code memory} down to tasks matching every keyword and displays them.
     * Read-only, so {@code storage} is unused.
     */
    @Override
    public void execute(TaskList memory, Ui ui, Storage storage) {
        ArrayList<Task> matches = memory.getTasks().stream()
                .filter(task -> task.matches(keywords))
                .collect(Collectors.toCollection(ArrayList::new));
        ui.showMatchingTasks(matches);
    }
}
