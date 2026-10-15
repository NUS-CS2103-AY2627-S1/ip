package windy.command;

import java.time.LocalDate;
import java.util.List;

import windy.task.Task;

/**
 * Displays incomplete tasks relevant on a specified date.
 */
public class DateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that finds tasks relevant on the specified date.
     *
     * @param date the date to search for.
     */
    public DateCommand(LocalDate date) {
        this.date = date;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(CommandContext context) {
        List<Task> foundTasks = context.getTasks().findTasksByDate(date);
        context.getUi().showFoundTasks(foundTasks,
                "No incomplete tasks relevant on " + date + ".");
    }
}
