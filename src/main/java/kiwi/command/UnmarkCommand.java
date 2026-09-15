package kiwi.command;

import kiwi.KiwiException;
import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * Marks the task at a given 0-based index as not done and saves.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that marks the task at the given index as not done.
     *
     * @param index 0-based index of the task to unmark.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the task as not done, saves the list, and shows a confirmation.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiwiException {
        requireValidIndex(tasks, index);
        assert tasks.isValidIndex(index) : "Index is in range after the user-input check";
        boolean wasDone = tasks.get(index).isDone();
        tasks.markNotDone(index);
        saveOrRollback(tasks, storage, () -> {
            if (wasDone) {
                tasks.markDone(index);
            }
        });
        ui.showUnmarked(toDisplayNumber(index), tasks.get(index));
    }
}
