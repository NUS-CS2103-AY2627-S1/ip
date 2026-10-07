package mary.command;

import mary.storage.Storage;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Displays all tasks currently stored by MARY.
 */
public class ListCommand extends Command {
    /**
     * Displays tasks with one-based numbers, or an empty-list message.
     *
     * @param tasks current in-memory task list.
     * @param ui user-facing message handler.
     * @param storage persistence service (unused by read-only commands).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.showMessage(" Nothing to chase yet! Add a task with 'todo description'.");
            return;
        }
        ui.showMessage(" Here's your task lineup:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage(" " + (i + 1) + "." + tasks.get(i));
        }
    }
}
