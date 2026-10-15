package wodan.command;

import wodan.storage.Storage;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Shows every task in the list, grouped by category, with original 1-based numbers.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that lists every task.
     */
    public ListCommand() {
    }

    /**
     * Shows every task in {@code tasks}, grouped by category.
     * Untagged tasks appear under {@code general} first; other categories follow A–Z.
     * Numbers match list order so {@code mark 2} still means the second task.
     *
     * @param tasks The task list to show.
     * @param ui The user interface for the reply.
     * @param storage Unused.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.show("    The ravens have given these quests.\n");
        if (tasks.isEmpty()) {
            return;
        }

        for (String category : tasks.categoryNames()) {
            ui.show("    " + category);
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getCategory().equals(category)) {
                    ui.showNumberedTask(i + 1, tasks.get(i));
                }
            }
            ui.show("");
        }
    }
}
