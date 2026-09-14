package arthur.commands;

import java.time.LocalDateTime;
import java.util.Map;

import arthur.exceptions.InvalidArgumentException;
import arthur.messages.AddMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;
import arthur.tasks.DeadlineTask;
import arthur.tasks.Task;
import arthur.utils.Utils;

/**
 * Adds a deadline task to the shared task list.
 */
public class DeadlineCommand extends Command {
    @Override
    // AI-assisted: Added handling that creates and stores a task in the shared command context.
    public Message handle(CommandContext context, String arg) {
        Map<String, String> args = parseArgs(arg, "/by");
        String description = args.get(UNNAMED_ARGUMENT).trim();
        LocalDateTime by = Utils.parseDateTime(args.get("/by").trim());
        if (by.isBefore(LocalDateTime.now())) {
            throw new InvalidArgumentException(args.get("/by"), "Deadline cannot be in the past");
        }
        Task task = new DeadlineTask(description, by);
        context.tasks().add(task);
        return new AddMessage(task);
    }
}
