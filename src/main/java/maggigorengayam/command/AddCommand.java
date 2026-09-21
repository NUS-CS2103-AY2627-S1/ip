package maggigorengayam.command;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The `todo`/`deadline`/`event` commands: adds an already-built task to the list. */
public class AddCommand extends Command {
    private final Task taskToAdd;

    /** Wraps the already-built task to be added once {@link #execute} runs. */
    public AddCommand(Task taskToAdd) {
        this.taskToAdd = taskToAdd;
    }

    /**
     * Adds {@code taskToAdd} to {@code tasks}, saves, and returns a message
     * reporting the addition.
     *
     * @throws MaggiGorengAyamException if {@code tasks} already contains a
     *         task with the exact same details (see {@link Task#hasSameDetailsAs}).
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException {
        boolean isDuplicate = tasks.getAll().stream().anyMatch(taskToAdd::hasSameDetailsAs);
        if (isDuplicate) {
            throw new MaggiGorengAyamException(
                    "Eh, you already have this exact task in your list leh, no need add twice.");
        }
        tasks.add(taskToAdd);
        if (saveTasks(storage, tasks)) {
            return ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
        }
        return ui.showSaveError();
    }
}
