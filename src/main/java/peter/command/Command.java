package peter.command;

import peter.PeterException;
import peter.storage.Storage;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Represents a user command that has been understood and is ready to run.
 *
 * <p>Each subclass carries the data its own command needs and knows how to
 * carry that command out, so the main class only has to execute whatever
 * command the parser produced.
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * @param tasks task list to read or modify.
     * @param ui UI used to report the outcome.
     * @param storage storage used to persist any change.
     * @throws PeterException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PeterException;

    /**
     * Returns whether the program should stop after this command. Only the
     * exit command overrides this.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the task list, undoing the caller's change if saving fails, so a
     * rejected save never leaves an unsaved change in memory.
     *
     * <p>Every modifying command needs this same save-then-undo shape, so it
     * lives here once. Each subclass supplies only the {@code rollback} that
     * reverses its own change.
     *
     * @param tasks task list to save.
     * @param storage storage to save into.
     * @param rollback action that reverses the change just made.
     * @throws PeterException if saving fails, after the rollback has run.
     */
    protected static void saveOrRollback(TaskList tasks, Storage storage, Runnable rollback)
            throws PeterException {
        try {
            storage.save(tasks.asList());
        } catch (PeterException e) {
            rollback.run();
            throw e;
        }
    }
}
