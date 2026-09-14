package morgan.command;

import morgan.exception.MorganException;
import morgan.storage.Storage;
import morgan.task.Task;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Represents a command in the application.
 */
public class MarkCommand extends Command {
    private final int index;
    private final boolean isMark;

    /**
     * Constructs mark command with the given position of the task.
     *
     * @param index The position of the task.
     * @param isMark Whether the task is marked.
     */
    public MarkCommand(int index, boolean isMark) {
        this.index = index;
        this.isMark = isMark;
    }

    /**
     * Executes the mark command using the given tasks, ui, and storage.
     *
     * @param tasks The list of tasks.
     * @param ui The ui.
     * @param storage The storage file.
     * @return False.
     * @throws MorganException If the index is invalid.
     */
    @Override
    public boolean execute(TaskList tasks, Ui ui, Storage storage) throws MorganException {
        Task task = isMark ? tasks.mark(index) : tasks.unmark(index);
        storage.save(tasks.getTasks());
        if (isMark) {
            ui.showToUser(" Meow~ Morgan has just caught a fish!");
        } else {
            ui.showToUser(" A fish has skipped, Meow!ฅ(=T ω T=)ฅ");
        }
        ui.showToUser("    " + task);
        return false;
    }
}
