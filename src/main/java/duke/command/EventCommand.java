package duke.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Adds an event task.
 *
 * <p>Expected user input format: {@code event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>}</p>
 */
public class EventCommand extends Command {
    /** Raw description string provided by the user. */
    private final String description;
    /** Raw start date string provided by the user. */
    private final String start;
    /** Raw end date string provided by the user. */
    private final String end;

    /**
     * Creates an event command with the given description and date strings.
     *
     * @param description Raw description.
     * @param start Raw start date string (expected to be {@code YYYY-MM-DD}).
     * @param end Raw end date string (expected to be {@code YYYY-MM-DD}).
     */
    public EventCommand(String description, String start, String end) {
        this.description = description;
        this.start = start;
        this.end = end;
    }

    /**
     * Adds an event task to the task list if the description and dates are valid.
     *
     * @param tasks Task list to add into.
     * @param ui UI used to show success/error messages.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        if (description.trim().isEmpty() || start.trim().isEmpty() || end.trim().isEmpty()) {
            ui.showError("""
                    The Event description and dates cannot be empty!
                    Use ' /from ' and ' /to ' to indicate start and end dates!
                    """);
            return;
        }

        try {
            LocalDate startDate = LocalDate.parse(start.trim());
            LocalDate endDate = LocalDate.parse(end.trim());
            if (!startDate.isBefore(endDate)) {
                ui.showError("The event start date must be before the end date.");
                return;
            }

            tasks.addEvent(description.trim(), startDate, endDate);
            ui.showTaskAdded(tasks.getLast(), tasks.size());
        } catch (DateTimeParseException e) {
            ui.showError("Please provide valid start and end dates in the format YYYY-MM-DD.");
        }
    }
}
