package billy.command;

import java.util.List;

import billy.storage.Storage;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Shows every task in the list, numbered as the user refers to them.
 */
public class ListCommand extends Command {

    /**
     * Shows every task, numbered the way the user refers to them.
     *
     * <p>An empty list gets a remark of its own rather than a heading with
     * nothing under it, which would read as though something had gone wrong.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.show("Nothing. Nada. An empty list. Suspicious.");
            return;
        }

        List<String> lines = numberMatching(tasks, task -> true);
        ui.show("Behold, your list:", String.join("\n", lines));
    }
}
