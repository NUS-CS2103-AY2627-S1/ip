package will.command;

import java.util.List;
import java.util.stream.Collectors;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.task.Task;

/**
 * The "find &lt;keyword&gt;" command: lists every task whose description
 * contains the given keyword (case-insensitive).
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that lists tasks matching the given keyword.
     *
     * @param keyword The text to search for in each task's description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.getTasks().stream()
                .filter(task -> task.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        if (matches.isEmpty()) {
            ui.showMessage("No matches for \"" + keyword + "\". Try a different word?");
            return;
        }
        ui.showMessage("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            ui.showMessage((i + 1) + "." + matches.get(i).toString());
        }
    }
}
