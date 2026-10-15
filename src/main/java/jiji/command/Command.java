package jiji.command;

import jiji.exception.JijiException;
import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Abstract base class for all executable commands in Jiji.
 *
 * <p>Implements the Gang of Four Command Pattern by encapsulating all actions,
 * parameters, and execution logic within individual command objects. Rather than
 * having a central controller execute commands via conditional dispatch, the invoker
 * (e.g. {@code Jiji}) supplies subsystem dependencies ({@link TaskList}, {@link Ui},
 * and {@link Storage}) directly to {@link #execute(TaskList, Ui, Storage)}.
 * This decouples invocation from execution, promotes the Open-Closed Principle (OCP),
 * and prevents the main controller from becoming a monolithic God Class.</p>
 */
public abstract class Command {

    /**
     * Constructs a new Command.
     */
    public Command() {
    }

    /**
     * Executes the command with the specified task list, UI, and storage dependencies.
     *
     * @param tasks The task list manipulated by the command.
     * @param ui The UI handler for displaying output to the user.
     * @param storage The storage handler for persisting changes.
     * @return The formatted response message produced by executing the command.
     * @throws JijiException If an error occurs during command execution.
     */
    public abstract String execute(TaskList tasks, Ui ui, Storage storage) throws JijiException;

    /**
     * Indicates whether this command signals the chatbot to exit.
     *
     * @return True if the chatbot should terminate after this command, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
