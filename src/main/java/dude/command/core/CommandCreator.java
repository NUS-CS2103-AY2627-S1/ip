package dude.command.core;

import java.util.Objects;

import dude.command.commands.AddCommand;
import dude.command.commands.DeleteCommand;
import dude.command.commands.ExitCommand;
import dude.command.commands.FindCommand;
import dude.command.commands.ListCommand;
import dude.command.commands.MarkCommand;
import dude.command.commands.OnCommand;
import dude.command.commands.UndoCommand;
import dude.command.commands.UnmarkCommand;
import dude.command.duplicate.DuplicateResolutionHandler;
import dude.command.support.CommandEnvironment;
import dude.parser.CommandRequest;
import dude.task.TaskService;
import dude.ui.Ui;

/**
 * Creates receiver-bound commands from parsed requests.
 */
public final class CommandCreator {
    private final TaskService receiver;
    private final DuplicateResolutionHandler resolutionHandler;
    private final CommandQueue queue;

    /**
     * Creates a command creator with the supplied application collaborators.
     *
     * @param receiver           Task receiver used by concrete commands.
     * @param resolutionHandler Duplicate-task interaction handler.
     * @param queue              Invoker used by the undo command.
     */
    public CommandCreator(TaskService receiver, DuplicateResolutionHandler resolutionHandler,
            CommandQueue queue) {
        this.receiver = Objects.requireNonNull(receiver, "receiver");
        this.resolutionHandler = Objects.requireNonNull(resolutionHandler, "resolutionHandler");
        this.queue = Objects.requireNonNull(queue, "queue");
    }

    /**
     * Creates the concrete command represented by a parsed request.
     *
     * @param request Parsed command request.
     * @param ui      Output adapter used by the command.
     * @return Receiver-bound command.
     */
    public Command create(CommandRequest request, Ui ui) {
        Objects.requireNonNull(request, "request");
        CommandEnvironment environment = new CommandEnvironment(receiver,
                Objects.requireNonNull(ui, "ui"), resolutionHandler, queue);
        return switch (request.type()) {
        case BYE -> new ExitCommand(environment);
        case LIST -> new ListCommand(environment);
        case FIND -> new FindCommand(request.argument(), environment);
        case ON -> new OnCommand(request.argument(), environment);
        case MARK -> new MarkCommand(request.argument(), environment);
        case UNMARK -> new UnmarkCommand(request.argument(), environment);
        case DELETE -> new DeleteCommand(request.argument(), environment);
        case TODO, DEADLINE, EVENT -> new AddCommand(request.type(), request.argument(), environment);
        case UNDO -> new UndoCommand(environment);
        };
    }
}
