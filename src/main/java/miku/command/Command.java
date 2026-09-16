package miku.command;

import java.io.IOException;

import miku.MikuException;

/** Represents an executable user request in Miku's task tracker. */
public abstract class Command {
    /** Carries out this command and returns the response to display to the user. */
    public abstract String execute(CommandContext commandContext) throws MikuException;

    /** Returns whether executing this command should end the application. */
    public boolean isExit() {
        return false;
    }

    /** Saves task changes and turns an unexpected write failure into a command error. */
    protected void saveTasks(CommandContext commandContext) throws MikuException {
        try {
            commandContext.storage().saveTasks(commandContext.tasks().asList());
        } catch (IOException exception) {
            throw new MikuException("Miku could not save your tasks right now. Please try again.");
        }
    }
}
