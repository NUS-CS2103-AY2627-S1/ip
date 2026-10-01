package bany.commands;

import java.io.IOException;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;

/**
 * Represents one executable Bany command.
 *
 * <p>Concrete commands contain the behavior for one user action and return a
 * {@link CommandResult} for the user interface to display.</p>
 */
public abstract class Command {

    /** Creates a command base object for a concrete command implementation. */
    public Command() {
    }

    /**
     * Executes this command using the application's shared components.
     *
     * @param tasks the current task list.
     * @param responder response builder used to create user-facing messages.
     * @param repository the task persistence component.
     * @return outcome of the command execution.
     */
    public abstract CommandResult execute(
            TaskStorage tasks,
            Responder responder,
            TaskFileRepository repository);

    /**
     * Persists the current task list.
     *
     * @param tasks the current task list.
     * @param responder response builder supplied by the execution context.
     * @param repository the task persistence component.
     * @throws IOException if the task list cannot be saved.
     */
    protected void saveTasks(
            TaskStorage tasks,
            Responder responder,
            TaskFileRepository repository
    ) throws IOException {
        repository.save(tasks);
    }
}
