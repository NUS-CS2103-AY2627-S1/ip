package chausistant.command;

import java.util.List;

import chausistant.storage.Storage;
import chausistant.task.Task;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/** Command that displays tasks whose descriptions contain a search phrase. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a command that searches task descriptions for the supplied phrase. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Displays tasks whose descriptions contain the search phrase. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<String> matchingTasks = tasks.findMatchingTasks(keyword).stream()
                .map(Task::printTask)
                .toList();
        ui.showMatchingTasks(matchingTasks);
    }
}
