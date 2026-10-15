package dude.command.commands;

import dude.command.core.Command;
import dude.command.core.CommandResult;
import dude.command.core.CommandType;
import dude.command.core.Undoable;
import dude.command.support.CommandEnvironment;
import dude.exception.CommandExecutionException;
import dude.exception.UsageException;
import dude.task.Task;

/**
 * Removes a task from the receiver.
 */
public final class DeleteCommand extends Command implements Undoable {
    private final String argument;
    private Task deletedTask;
    private int deletedIndex;

    /**
     * Creates a delete command.
     *
     * @param argument    One-based task number.
     * @param environment Command collaborators and receiver.
     */
    public DeleteCommand(String argument, CommandEnvironment environment) {
        super(environment);
        this.argument = argument;
    }

    @Override
    public CommandResult execute() throws UsageException, CommandExecutionException {
        deletedIndex = parseTaskIndex(CommandType.DELETE, argument);
        deletedTask = receiver().remove(deletedIndex);
        environment().ui().showDeletedTask(deletedTask, tasks().size());
        save();
        return CommandResult.continueRunning();
    }

    @Override
    public void undo() {
        receiver().insert(deletedIndex, deletedTask);
    }

    @Override
    public void redo() {
        receiver().remove(deletedIndex);
    }

    @Override
    public boolean isAvailableForUndo() {
        return deletedTask != null;
    }

    @Override
    public String undoDescription() {
        return "delete task";
    }
}
