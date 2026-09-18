package bibi.command;

import bibi.BibiException;
import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskList;

/**
 * Changes whether one task counts as done.
 *
 * <p>Ticking a task off and reopening it are the same piece of work: look the
 * numbered task up, set its completion, show what it now looks like, and save
 * if that changed anything. Only the state being asked for and the words used
 * to confirm it differ, so those are all a subclass supplies.
 *
 * <p>The task is shown, not only its number, so the user can see at a glance
 * that the task they meant is the one that changed.
 */
public abstract class CompletionCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will set the completion of the given task.
     *
     * @param taskNumber the one-based number shown to the user
     */
    protected CompletionCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Sets the numbered task's completion, confirms it, and saves the list if
     * the task was not already in the state asked for.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BibiException {
        boolean shouldComplete = isCompleteWanted();

        // Read the task before changing it, so the confirmation can tell a real
        // change from a command that asked for the state the task was already
        // in. Reporting the second as though something had happened is how a
        // user comes to believe they ticked off a task they did not.
        boolean wasAlreadyInThatState = tasks.get(taskNumber).isComplete() == shouldComplete;
        Task task = shouldComplete
                ? tasks.markComplete(taskNumber)
                : tasks.markIncomplete(taskNumber);

        ui.showMessage(wasAlreadyInThatState
                ? describeUnchanged(taskNumber)
                : describeChange(taskNumber));
        ui.showDetails(task.toString());

        // A list that did not change has nothing to write. Saving anyway would
        // put the warning about losing unsaved work under a line that has just
        // said nothing happened, which leaves the user with two replies that
        // cannot both be true.
        if (!wasAlreadyInThatState) {
            saveTasks(tasks, ui, storage);
        }
    }

    /**
     * Reports the completion state this command asks for.
     *
     * @return {@code true} to mark the task done, {@code false} to reopen it
     */
    protected abstract boolean isCompleteWanted();

    /**
     * Returns the confirmation for a task whose state this command changed.
     *
     * @param taskNumber the one-based number the user referred to
     * @return the line to show above the task
     */
    protected abstract String describeChange(int taskNumber);

    /**
     * Returns the confirmation for a task that was already in the state asked
     * for, and which this command therefore left as it was.
     *
     * @param taskNumber the one-based number the user referred to
     * @return the line to show above the task
     */
    protected abstract String describeUnchanged(int taskNumber);
}
