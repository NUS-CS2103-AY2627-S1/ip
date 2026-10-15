package dude.command.support;

import java.util.Objects;

import dude.command.core.CommandQueue;
import dude.command.duplicate.DuplicateResolutionHandler;
import dude.task.TaskService;
import dude.ui.Ui;

/**
 * Groups the collaborators required by receiver-bound commands.
 *
 * @param receiver           Task receiver.
 * @param ui                 Command output adapter.
 * @param resolutionHandler Duplicate-task interaction handler.
 * @param queue              Command invoker.
 */
public record CommandEnvironment(TaskService receiver, Ui ui,
        DuplicateResolutionHandler resolutionHandler, CommandQueue queue) {
    /**
     * Validates that every command collaborator is available.
     *
     * @param receiver           Task receiver.
     * @param ui                 Command output adapter.
     * @param resolutionHandler Duplicate-task interaction handler.
     * @param queue              Command invoker.
     */
    public CommandEnvironment {
        Objects.requireNonNull(receiver, "receiver");
        Objects.requireNonNull(ui, "ui");
        Objects.requireNonNull(resolutionHandler, "resolutionHandler");
        Objects.requireNonNull(queue, "queue");
    }
}
