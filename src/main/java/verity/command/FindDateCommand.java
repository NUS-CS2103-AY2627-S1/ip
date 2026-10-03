package verity.command;

import java.time.LocalDate;
import java.util.List;

import verity.task.Task;

/**
 * Represents a command that finds tasks occurring on a date.
 */
public class FindDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a date-search command.
     *
     * @param date Date to search for.
     */
    public FindDateCommand(LocalDate date) {
        super(false);
        this.date = date;
    }

    /**
     * Finds and returns tasks occurring on the date.
     *
     * @param context Command execution context.
     * @return User-facing response after execution.
     */
    @Override
    public String execute(CommandContext context) {
        List<Task> matchingTasks = context.getTasks().findOn(date);
        return context.getUi().getTasksOnMessage(date, matchingTasks);
    }
}
