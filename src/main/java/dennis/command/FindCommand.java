package dennis.command;

import dennis.storage.Storage;
import dennis.task.Task;
import dennis.task.TaskList;
import dennis.ui.Ui;

/**
 * Lists every task whose description contains a given keyword.
 *
 * <p>Each match is shown at its real position in the list (so the number
 * still works with {@code mark}/{@code unmark}/{@code delete}), the same
 * convention {@link OnCommand} uses. The search is case-sensitive and looks
 * at the description text only.</p>
 */
public class FindCommand extends Command {
    /** The keyword to search task descriptions for. */
    private final String keyword;

    /**
     * Creates a command that searches for the given keyword.
     *
     * @param keyword the text to look for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Purr-fect, found these matching tasks:");

        boolean hasMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.matches(keyword)) {
                hasMatch = true;
                ui.showMessage((i + 1) + "." + task);
            }
        }

        if (!hasMatch) {
            ui.showMessage("No matching tasks found.");
        }
    }
}
