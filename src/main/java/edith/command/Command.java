package edith.command;

import java.io.IOException;

import edith.exception.EdithException;
import edith.storage.Storage;
import edith.task.TaskList;
import edith.ui.Ui;

/** Represents an executable instruction understood by Edith. */
public abstract class Command {
    /**
     * Performs this command using the current task list and user interface.
     *
     * @param tasks the task list on which the command operates
     * @param ui the user interface used to display the result
     * @throws EdithException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui) throws EdithException;

    /**
     * Returns whether Edith should stop after executing this command.
     *
     * @return {@code true} only for an exit command
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves task changes and translates storage failures into a user-facing application error.
     *
     * @param tasks the task list to save
     * @throws EdithException if the task list cannot be saved
     */
    protected void saveTasks(TaskList tasks) throws EdithException {
        try {
            Storage.saveTasks(tasks.asList());
        } catch (IOException e) {
            throw new EdithException("I could not save your tasks to disk.");
        }
    }

    /**
     * Restores an in-memory change when saving it fails.
     *
     * @param tasks the changed task list to save
     * @param rollback action that restores the list to its previous state
     * @throws EdithException if the task list cannot be saved
     */
    protected void saveTasks(TaskList tasks, Runnable rollback) throws EdithException {
        try {
            saveTasks(tasks);
        } catch (EdithException e) {
            rollback.run();
            throw e;
        }
    }
}
