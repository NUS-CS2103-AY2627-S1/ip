package arthur.commands;

import java.time.LocalDateTime;
import java.util.Map;

import arthur.exceptions.InvalidArgumentException;
import arthur.messages.AddMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;
import arthur.tasks.EventTask;
import arthur.tasks.Task;
import arthur.utils.Utils;

/**
 * Adds an event task to the shared task list.
 */
public class EventCommand extends Command {
    @Override
    // AI-assisted: Added handling that creates and stores a task in the shared command context.
    public Message handle(CommandContext context, String arg) {
        Map<String, String> args = parseArgs(arg, "/from", "/to");
        String description = args.get(UNNAMED_ARGUMENT).trim();
        LocalDateTime from = Utils.parseDateTime(args.get("/from").trim());
        LocalDateTime to = Utils.parseDateTime(args.get("/to").trim());
        if (to.isBefore(LocalDateTime.now())) {
            throw new InvalidArgumentException(args.get("/to"), "Event cannot be in the past");
        }
        if (from.isAfter(to)) {
            throw new InvalidArgumentException(args.get("/from"), "Event cannot start after it ends");
        }
        Task task = new EventTask(description, from, to);
        context.tasks().add(task);
        return new AddMessage(task);
    }
}
