package morgan.command;

import java.util.List;

import morgan.storage.Storage;
import morgan.task.Task;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Represents a command in the application.
 */
public class FindKeywordCommand extends Command {
    private final String keyword;

    /**
     * Constructs find command with the given keyword.
     *
     * @param keyword The keyword entered by the user.
     */
    public FindKeywordCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the find command using the given tasks, ui, and storage.
     *
     * @param tasks The list of tasks.
     * @param ui The ui.
     * @param storage The storage file.
     * @return False.
     */
    @Override
    public boolean execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findTaskWithKeyword(keyword);
        if (matchingTasks.isEmpty()) {
            ui.showToUser(" No matching fish found, Meow! (=T w T=)");
        } else {
            ui.showToUser(" Meow~ Here are the matching fishes in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                ui.showToUser(String.format(" %d.%s", i + 1, matchingTasks.get(i)));
            }
        }
        return false;
    }
}
