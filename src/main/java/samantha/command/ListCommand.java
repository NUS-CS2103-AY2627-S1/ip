package samantha.command;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import samantha.model.Task;

/**
 * Represents the command that displays tasks, optionally filtered by date.
 */
public class ListCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a list command with an optional date filter.
     *
     * @param date date to filter by, or {@code null} to show all tasks
     */
    public ListCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays the matching tasks.
     *
     * @param context current application state and persistence handlers
     * @return matching task list
     */
    @Override
    public String execute(CommandContext context) {
        List<Task> taskSnapshot = context.getTasks().asList();
        String matchingTasks = IntStream.range(0, taskSnapshot.size())
                .filter(index -> date == null || taskSnapshot.get(index).isOnDate(date))
                .mapToObj(index -> String.format("%d. %s", index + 1, taskSnapshot.get(index)))
                .collect(Collectors.joining("\n"));
        String message = "Here are the tasks in your list:\n" + matchingTasks;
        return message.stripTrailing();
    }
}
