package dude.command.commands;

import dude.command.support.CommandEnvironment;

/**
 * Marks a selected task as incomplete.
 */
public final class UnmarkCommand extends TaskCompletionCommand {
    /**
     * Creates an unmark command.
     *
     * @param argument    One-based task number.
     * @param environment Command collaborators and receiver.
     */
    public UnmarkCommand(String argument, CommandEnvironment environment) {
        super(argument, environment, CompletionAction.UNMARK);
    }
}
