package kia.command;

import java.util.ArrayList;

import kia.exception.KiaException;
import kia.task.Task;
import kia.ui.Ui;

/**
 * A unit of work produced from a user command.
 *
 * <p>Concrete commands will progressively take over the command-specific
 * branches currently in {@link kia.Kia}. This common contract lets the main loop
 * execute commands without knowing their individual behavior.</p>
 */
public abstract class Command {
    /** Creates a command ready for execution. */
    protected Command() {
    }

    /**
     * Executes this command using the current task list and user interface.
     *
     * @param tasks the current in-memory tasks
     * @param ui the user-interface boundary used for output
     * @throws KiaException if command execution cannot be completed
     */
    public abstract void execute(ArrayList<Task> tasks, Ui ui) throws KiaException;

    /**
     * Indicates whether executing this command should end the application.
     *
     * @return {@code true} only for the exit command
     */
    public boolean isExit() {
        return false;
    }
}
