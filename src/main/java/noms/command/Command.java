package noms.command;

import java.io.IOException;

import noms.exception.NomsException;
import noms.exception.StorageException;
import noms.storage.Storage;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Represents a single user command that Noms can carry out, such as adding
 * or deleting a task.
 *
 * Each concrete subclass knows how to perform one kind of command in its
 * {@link #execute} method, so the main loop can run any command through the
 * same call without a large {@code if}/{@code else} chain. This is the
 * Command design pattern: behavior that used to be one branch per command is
 * now one class per command.
 */
public abstract class Command {
    /**
     * Carries out this command against the given task list, showing the
     * result (or any error) through the ui and persisting changes through
     * storage as needed.
     *
     * @param tasks the task list to read or modify
     * @param ui the ui used to show responses
     * @param storage the storage used to save changes
     * @throws NomsException if the command's arguments are invalid
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NomsException;

    /**
     * Returns whether Noms should stop reading commands after this one.
     * Only the exit command returns {@code true}; every other command
     * inherits this {@code false} default.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the current task list to disk and rolls back its pending change
     * if persistence fails. Shared here so every task-changing command keeps
     * memory and disk consistent through the same transaction boundary.
     *
     * @param tasks the task list to save
     * @param storage the storage to save to
     * @param rollbackAction the action that restores the task list before the change
     * @throws StorageException if the task list cannot be saved
     */
    protected void save(TaskList tasks, Storage storage, Runnable rollbackAction)
            throws StorageException {
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            rollbackAction.run();
            throw new StorageException(e);
        }
    }
}
