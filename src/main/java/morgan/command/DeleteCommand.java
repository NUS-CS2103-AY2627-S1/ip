package morgan.command;

import morgan.exception.MorganException;
import morgan.storage.Storage;
import morgan.task.Task;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Represents a command in the application.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Constructs delete command with the given position of task.
     *
     * @param index The position of the task.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the delete command using the given tasks, ui, and storage.
     *
     * @param tasks The list of tasks.
     * @param ui The ui.
     * @param storage The storage file.
     * @return False.
     * @throws MorganException If the index is invalid.
     */
    @Override
    public boolean execute(TaskList tasks, Ui ui, Storage storage) throws MorganException {
        Task removedTask = tasks.delete(index);
        storage.save(tasks.getTasks());
        ui.showToUser(
                " Meow~ Alright, I will set this fish free.",
                "    " + removedTask,
                " Meow~ There are only " + tasks.size() + " fishes now.(>_<)"
        );
        return false;
    }
}
