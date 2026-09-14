package arthur.commands;

import java.util.List;

import arthur.messages.ListMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;
import arthur.tasks.Task;

/**
 * Lists tasks that contain a keyword.
 */
public class FindCommand extends Command {
    /**
     * Finds tasks that contain the keyword supplied through {@code arg}.
     *
     * @param context the shared command state.
     * @param arg command arguments containing the keyword.
     * @return a message containing matching tasks.
     */
    @Override
    public Message handle(CommandContext context, String arg) {
        String keyword = arg.trim();
        List<Task> filteredTasks = context.tasks().stream()
                .filter(task -> task.contains(keyword))
                .toList();
        return new ListMessage(filteredTasks);
    }
}
