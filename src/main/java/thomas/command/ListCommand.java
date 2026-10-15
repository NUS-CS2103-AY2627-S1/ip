package thomas.command;

import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Shows every stored task, numbered: the {@code list} command.
 */
public class ListCommand extends Command {
    /**
     * Returns the whole task list.
     * <p>
     * Nothing is saved, because nothing changed.
     *
     * @param tasks The tasks to show.
     * @param ui Used to word them.
     * @param storage Unused.
     * @param history Unused.
     * @return The numbered list.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage, History history) {
        return ui.getTaskListMessage(tasks);
    }
}
