package longfrog.command;

import longfrog.ui.Ui;

/** Represents an action that can update the application state and console UI. */
public interface Command {
    /**
     * Executes the command.
     * @param ui the UI used to display the command result
     * @return whether the chat should be exited after this command is executed.
     */
    boolean execute(Ui ui);
}
