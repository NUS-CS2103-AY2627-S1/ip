package dude.command.commands;

import dude.command.support.CommandEnvironment;

/**
 * Marks a selected task as completed.
 */
public final class MarkCommand extends TaskCompletionCommand {
    /**
     * Creates a mark command.
     *
     * @param argument    One-based task number.
     * @param environment Command collaborators and receiver.
     */
    public MarkCommand(String argument, CommandEnvironment environment) {
        super(argument, environment, CompletionAction.MARK);
    }
}
