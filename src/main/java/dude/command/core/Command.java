package dude.command.core;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import dude.command.support.CommandEnvironment;
import dude.exception.CommandExecutionException;
import dude.exception.UsageException;
import dude.parser.Parser;
import dude.task.Task;
import dude.task.TaskService;

/**
 * A receiver-bound command that can be executed by the command queue.
 */
public abstract class Command {
    /** Collaborators shared by concrete commands. */
    private final CommandEnvironment environment;

    /**
     * Creates a command bound to its application collaborators.
     *
     * @param environment Command collaborators and receiver.
     */
    protected Command(CommandEnvironment environment) {
        this.environment = Objects.requireNonNull(environment, "environment");
    }

    /**
     * Executes this command.
     *
     * @return Command result.
     * @throws UsageException If user input is invalid.
     * @throws CommandExecutionException If the changed state cannot be saved.
     */
    public abstract CommandResult execute() throws UsageException, CommandExecutionException;

    /**
     * Parses a one-based task number into a zero-based index.
     *
     * @param commandType Command whose argument is being parsed.
     * @param argument    Raw task-number argument.
     * @return Zero-based task index.
     * @throws UsageException If the argument is invalid.
     */
    protected int parseTaskIndex(CommandType commandType, String argument)
            throws UsageException {
        return Parser.parseTaskIndex(commandType, argument, tasks().size());
    }

    /**
     * Returns the task receiver.
     *
     * @return Task receiver.
     */
    protected final TaskService receiver() {
        return environment.receiver();
    }

    /**
     * Returns the collaborators shared by this command.
     *
     * @return Command environment.
     */
    protected final CommandEnvironment environment() {
        return environment;
    }

    /**
     * Returns the receiver's current task list.
     *
     * @return Current tasks.
     */
    protected final List<Task> tasks() {
        return receiver().tasks();
    }

    /**
     * Persists the receiver's current state.
     *
     * @throws CommandExecutionException If persistence fails.
     */
    protected final void save() throws CommandExecutionException {
        try {
            receiver().saveTasks();
        } catch (IOException exception) {
            throw new CommandExecutionException(exception);
        }
    }

    /**
     * Persists the state restored by the invoker during undo.
     *
     * <p>
     * This package-private bridge keeps persistence with the receiver-bound command while
     * allowing the invoker to remain independent of the receiver's concrete type.
     *
     * @throws CommandExecutionException If persistence fails.
     */
    final void saveState() throws CommandExecutionException {
        save();
    }
}
