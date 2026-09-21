package goat.command;

import goat.GoatException;
import goat.storage.Storage;
import goat.task.Task;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * Removes one task from the list.
 */
public class DeleteCommand extends Command {

    /** Which task to remove, as the user counts them, from 1. */
    private final int taskNumber;

    /**
     * Creates a command that will remove the numbered task.
     *
     * @param taskNumber the task's position as the user counts it, from 1
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Removes the numbered task, writes the shortened list to disk, and
     * shows the user which task was removed.
     *
     * @throws GoatException if no task has that number, or the list cannot be
     *                       written to disk
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws GoatException {
        // delete() checks that the number refers to a real task, and hands
        // back what it took out so it can be shown to the user.
        Task removed = tasks.delete(taskNumber);
        storage.save(tasks);
        ui.show("Gone:",
                "  " + removed,
                "That leaves " + tasks.size() + ".");
    }
}
