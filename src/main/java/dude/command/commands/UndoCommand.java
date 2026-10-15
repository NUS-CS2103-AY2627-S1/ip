package dude.command.commands;

import java.util.Optional;

import dude.command.core.Command;
import dude.command.core.CommandResult;
import dude.command.support.CommandEnvironment;
import dude.exception.CommandExecutionException;

/**
 * Asks the command queue to reverse its most recent undoable command.
 */
public final class UndoCommand extends Command {
    /**
     * Creates an undo command.
     *
     * @param environment Command collaborators and invoker.
     */
    public UndoCommand(CommandEnvironment environment) {
        super(environment);
    }

    @Override
    public CommandResult execute() throws CommandExecutionException {
        Optional<String> description = environment().queue().undo();
        if (description.isPresent()) {
            environment().ui().showUndo(description.get());
        } else {
            environment().ui().showUndoUnavailable();
        }
        return CommandResult.continueRunning();
    }
}
