package notmarth.command;

import java.io.IOException;

import notmarth.exception.NotMarthException;
import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/**
 * Represents one executable NotMarth command.
 */
public abstract class Command {
    /** Creates a command. */
    public Command() {
    }

    /**
     * Executes this command against the application state.
     *
     * @param tasks the current task list.
     * @param ui the console interaction handler.
     * @param storage the task archive handler.
     * @throws NotMarthException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage)
            throws NotMarthException;

    /**
     * Executes a command with no contacts, retained for callers of the original task-only API.
     *
     * @param tasks the current task list.
     * @param ui the console interaction handler.
     * @param storage the task archive handler.
     * @throws NotMarthException if the command cannot be completed.
     */
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NotMarthException {
        execute(tasks, new ContactList(), ui, storage);
    }

    /**
     * Returns whether this command ends the application loop.
     *
     * @return {@code true} only for the exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves a changed task list while keeping a storage failure non-fatal to
     * the current session.
     *
     * @param tasks the changed task list.
     * @param ui the console interaction handler.
     * @param storage the task archive handler.
     */
    protected void saveTasks(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        try {
            storage.save(tasks.asList(), contacts.asList());
        } catch (IOException exception) {
            ui.showError("I couldn't save the battle plan to disk. Your current session is still active.");
        }
    }
}
