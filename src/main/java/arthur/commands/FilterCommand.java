package arthur.commands;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import arthur.messages.ListMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;
import arthur.tasks.Task;
import arthur.utils.Utils;

/**
 * Lists tasks that occur on a requested date.
 */
public class FilterCommand extends Command {
    // AI-assisted: Added filtering of dated tasks for the /on argument.
    /**
     * Filters stored deadline and event tasks by the date supplied with {@code /on}.
     *
     * @param context the shared command state.
     * @param arg     command arguments containing {@code /on yyyy-MM-dd}.
     * @return a message containing matching tasks.
     */
    @Override
    public Message handle(CommandContext context, String arg) {
        Map<String, String> args = parseArgs(arg, "/on");
        LocalDate date = Utils.parseDate(args.get("/on").trim());
        List<Task> filteredTasks = context.tasks().stream()
                .filter(task -> task.isOn(date))
                .toList();
        return new ListMessage(filteredTasks);
    }
}
