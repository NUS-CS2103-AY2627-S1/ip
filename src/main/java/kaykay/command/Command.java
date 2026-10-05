package kaykay.command;

import java.io.IOException;

import kaykay.exception.KaykayException;
import kaykay.model.ApplicationData;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Represents a parsed command that can be executed by Kaykay.
 */
public abstract class Command {
    /** Creates a command. */
    public Command() {
    }

    /**
     * Executes this command using the supplied application components.
     *
     * @param data application data to read or modify.
     * @param ui UI used to display command results.
     * @param storage storage used to persist task changes.
     * @throws KaykayException if the command cannot be applied to the task list.
     * @throws IOException if a task change cannot be saved.
     */
    public abstract void execute(ApplicationData data, Ui ui, Storage storage)
            throws KaykayException, IOException;

    /**
     * Checks whether this command ends the chatbot session.
     *
     * @return true only for the exit command.
     */
    public boolean isExit() {
        return false;
    }
}
