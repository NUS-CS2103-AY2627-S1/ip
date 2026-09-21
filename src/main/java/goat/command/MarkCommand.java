package goat.command;

import goat.GoatException;
import goat.storage.Storage;
import goat.task.Task;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * Marks one task as done, or as not done again.
 *
 * "mark" and "unmark" share this one class because they differ only in the
 * status they store and the wording of the reply -- the work of finding the
 * task, saving the list and confirming the change is identical. Two classes
 * would duplicate that skeleton to express a single differing value.
 *
 * The alternative worth knowing about: if the two ever grew genuinely
 * different behavior, they would become two subclasses of a shared abstract
 * parent, and the boolean below would disappear. A boolean parameter that
 * selects between two unrelated behaviors is usually a sign that should have
 * happened already; here it is one piece of data, "which status to set".
 */
public class MarkCommand extends Command {

    /** Which task to change, as the user counts them, from 1. */
    private final int taskNumber;

    /** The status to store: true for done, false for not done. */
    private final boolean isDone;

    /**
     * Creates a command that will set the numbered task's done status.
     *
     * @param taskNumber the task's position as the user counts it, from 1
     * @param isDone     the status to store
     */
    public MarkCommand(int taskNumber, boolean isDone) {
        this.taskNumber = taskNumber;
        this.isDone = isDone;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Sets the numbered task's done status, writes the list to disk, and
     * confirms the change in the wording that matches the new status.
     *
     * @throws GoatException if no task has that number, or the list cannot be
     *                       written to disk
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws GoatException {
        // get() checks that the number refers to a task that exists.
        Task task = tasks.get(taskNumber);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks);
        ui.show(isDone ? "Done. One less to climb:"
                        : "Back on the list:",
                "  " + task);
    }
}
