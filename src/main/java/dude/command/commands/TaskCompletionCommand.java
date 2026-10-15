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
 * Provides the shared execution and history behaviour for mark and unmark commands.
 */
abstract class TaskCompletionCommand extends Command implements Undoable {
    private final String argument;
    private final CompletionAction action;
    private Task task;
    private boolean wasDone;

    /**
     * Creates a task-completion command.
     *
     * @param argument        One-based task number.
     * @param environment     Command collaborators and receiver.
     * @param action      Completion state and display metadata.
     */
    protected TaskCompletionCommand(String argument, CommandEnvironment environment,
            CompletionAction action) {
        super(environment);
        this.argument = argument;
        this.action = action;
    }

    @Override
    public final CommandResult execute() throws UsageException, CommandExecutionException {
        task = tasks().get(parseTaskIndex(action.commandType(), argument));
        wasDone = task.isDone();
        setDone(action.completed());
        environment().ui().showUpdatedTask(task, action.commandType());
        save();
        return CommandResult.continueRunning();
    }

    @Override
    public final void undo() {
        setDone(wasDone);
    }

    @Override
    public final boolean isAvailableForUndo() {
        return task != null;
    }

    @Override
    public final void redo() {
        setDone(action.completed());
    }

    @Override
    public final String undoDescription() {
        return action.undoDescription();
    }

    /**
     * Applies a completion state to the selected task.
     *
     * @param done Desired completion state.
     */
    private void setDone(boolean done) {
        if (done) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }
}
