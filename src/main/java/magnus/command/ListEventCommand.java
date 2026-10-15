package magnus.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import magnus.exception.CommandSyntaxException;
import magnus.parser.DateRange;
import magnus.parser.DateTimeParser;
import magnus.task.TaskList;

/**
 * Displays event tasks enclosed by a specified date range.
 */
public class ListEventCommand implements Command {
    private static final String USAGE_MESSAGE =
            "\tUsage: list_event <dd/MM/yyyy> <dd/MM/yyyy>";

    private final TaskList tasks;
    private final DateTimeParser dateTimeParser;

    /**
     * Creates a command that filters the specified task list by an inclusive date range.
     *
     * @param tasks The task list to filter and display.
     */
    public ListEventCommand(TaskList tasks) {
        this.tasks = tasks;
        this.dateTimeParser = new DateTimeParser();
    }

    /**
     * Parses the supplied date range and formats all events fully enclosed by it.
     *
     * @param args The command arguments containing the start and end dates.
     * @return A message containing the events within the specified date range.
     * @throws CommandSyntaxException If the dates are missing, malformed, invalid, or out of order.
     */
    @Override
    public String execute(String... args) throws CommandSyntaxException {
        if (args.length == 0 || args[0].isBlank()) {
            throw new CommandSyntaxException("\tThat move is incomplete - please give me a start date and an "
                    + "end date.\n"
                    + USAGE_MESSAGE);
        }

        if (args.length > 1) {
            throw createInvalidDateCountException();
        }

        DateRange dateRange = parseDateRange(args[0]);
        assert dateRange != null
                : "The date range parser must return a start date and an end date";
        LocalDate startDate = dateRange.startDate();
        LocalDate endDate = dateRange.endDate();
        if (startDate.isAfter(endDate)) {
            throw new CommandSyntaxException(
                    "\tThat move reverses the clock - the start date must be before or equal to the end date.");
        }
        assert !startDate.isAfter(endDate)
                : "A validated date range must be ordered before filtering";

        TaskList filteredTasks = this.tasks.filterEventsWithinDateRange(startDate, endDate);
        if (filteredTasks.getTaskCount() == 0) {
            return "\tBoard surveyed - there are no Event tasks in that date range.";
        }
        return "\tBoard surveyed - here are your Event tasks in that date range:\n\n"
                + filteredTasks.formatTasks();
    }

    /**
     * Parses the two dates supplied in one command argument.
     *
     * @param dateRange The start and end date text.
     * @return The parsed date range.
     * @throws CommandSyntaxException If the input does not contain two valid dates.
     */
    private DateRange parseDateRange(String dateRange) throws CommandSyntaxException {
        try {
            return this.dateTimeParser.parseDateRange(dateRange);
        } catch (DateTimeParseException exception) {
            throw new CommandSyntaxException(
                    "\tThe clock rejects those dates - enter both in dd/MM/yyyy format, for example "
                            + "01/09/2026 30/09/2026.");
        } catch (IllegalArgumentException exception) {
            throw createInvalidDateCountException();
        }
    }

    /**
     * Creates the syntax exception used when the command does not contain exactly two dates.
     *
     * @return An exception containing the expected command usage.
     */
    private CommandSyntaxException createInvalidDateCountException() {
        return new CommandSyntaxException(
                "\tThe board needs two dates - the list_event command requires exactly two dates.\n"
                        + USAGE_MESSAGE);
    }
}
