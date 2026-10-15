package xiaozhi.command;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.Task;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Adds a single {@link Task} to the task list.
 * Used for {@code todo}, {@code deadline} and {@code event} commands alike,
 * since all three only differ in which Task subtype was already constructed.
 */
public class AddCommand extends Command {
    private final Task task;
    private int addedIndex = -1;

    /**
     * Creates an AddCommand that will add the given task when executed.
     *
     * @param task The task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds this command's task to {@code tasks}, reports it through
     * {@code ui}, and saves the updated list through {@code storage}.
     *
     * @throws XiaoZhiException If an equivalent task (same type, description, and any
     *         dates, regardless of completion status) already exists in {@code tasks}.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, CommandHistory history) throws XiaoZhiException {
        if (tasks.asList().contains(task)) {
            throw new XiaoZhiException(
                    "This task already lives among your intentions: " + task);
        }
        tasks.add(task);
        addedIndex = tasks.size() - 1;
        ui.showAdded(task, tasks.size());
        storage.save(tasks.asList());
    }

    /**
     * Returns {@code true} -- adding a task can always be undone by removing it again.
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Removes the task this command added, reports the removal through
     * {@code ui}, and saves the updated list through {@code storage}.
     */
    @Override
    public void undo(TaskList tasks, Ui ui, Storage storage) {
        assert addedIndex >= 0 : "undo() should only be called after execute() has run.";
        Task removedTask = tasks.remove(addedIndex);
        ui.showRemoved(removedTask, tasks.size());
        storage.save(tasks.asList());
    }
}
