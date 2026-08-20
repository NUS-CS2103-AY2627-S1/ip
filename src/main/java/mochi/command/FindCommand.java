package mochi.command;

import java.util.List;
import java.util.stream.Collectors;

import mochi.TaskList;
import mochi.task.Task;
import mochi.ui.Ui;

/**
 * Finds tasks whose description contains the given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command that searches for the given keyword.
     *
     * @param keyword the keyword to search for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        List<Task> matching = tasks.getTasks().stream()
                .filter(t -> t.toString().contains(keyword))
                .collect(Collectors.toList());
        ui.showMatchingTasks(matching);
    }
}
