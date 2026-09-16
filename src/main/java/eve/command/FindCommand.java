package eve.command;

import java.util.List;

import eve.Storage;
import eve.Ui;
import eve.task.Task;
import eve.task.TaskList;

/** Shows the tasks whose description contains a given keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks matching the given keyword.
     *
     * @param keyword the text to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.matching(keyword);
        ui.showMatchingTasks(matches);
    }
}
