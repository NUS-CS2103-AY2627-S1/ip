package nico;

import java.util.List;

/**
 * Represents a user command that can be executed by the chatbot.
 */
public abstract class Command {
    /**
     * Executes this command using the current task list and UI.
     *
     * @param tasks Tasks currently managed by the chatbot.
     * @param ui User interface used to show command output.
     * @throws NicoException If the command cannot be completed.
     */
    public abstract void execute(List<Task> tasks, Ui ui) throws NicoException;

    /**
     * Returns whether this command should exit the chatbot.
     */
    public boolean isExit() {
        return false;
    }
}
