package dude.command.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

import dude.exception.CommandExecutionException;
import dude.exception.UsageException;

/**
 * Invokes commands polymorphically and stores completed undoable commands.
 */
public final class CommandQueue {
    private final Deque<Command> history = new ArrayDeque<>();

    /**
     * Creates an empty command invoker.
     */
    public CommandQueue() {
    }

    /**
     * Executes a command without inspecting its concrete type.
     *
     * @param command Command to execute.
     * @return Result requested by the command.
     * @throws UsageException If user input is invalid.
     * @throws CommandExecutionException If the changed state cannot be persisted.
     */
    public CommandResult execute(Command command) throws UsageException, CommandExecutionException {
        Objects.requireNonNull(command, "command");
        CommandResult result = command.execute();
        if (command instanceof Undoable undoable && undoable.isAvailableForUndo()) {
            history.push(command);
        }
        return result;
    }

    /**
     * Undoes the most recently executed undoable command.
     *
     * @return Description of the undone command, or empty when history is empty.
     * @throws CommandExecutionException If persisting the restored state fails.
     */
    public Optional<String> undo() throws CommandExecutionException {
        if (history.isEmpty()) {
            return Optional.empty();
        }

        Command command = history.peek();
        Undoable undoable = (Undoable) command;
        undoable.undo();
        try {
            command.saveState();
            history.pop();
            return Optional.of(undoable.undoDescription());
        } catch (CommandExecutionException exception) {
            undoable.redo();
            throw exception;
        }
    }

    /**
     * Returns the number of commands currently available to undo.
     *
     * @return Number of undoable commands.
     */
    public int size() {
        return history.size();
    }
}
