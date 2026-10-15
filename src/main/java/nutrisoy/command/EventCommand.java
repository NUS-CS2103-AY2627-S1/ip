package nutrisoy.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Event;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Adds an event task from user-provided command arguments.
 */
public class EventCommand extends Command {
    private final String arguments;

    /**
     * Creates a command with the arguments describing the event.
     *
     * @param arguments event description and date-range information
     */
    public EventCommand(String arguments) {
        assert arguments != null : "Event arguments must not be null";
        this.arguments = arguments;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        if (arguments.isEmpty()) {
            throw new DukeException("The description of an event cannot be empty. "
                    + "Use: event [description] /from [yyyy-MM-dd] /to [yyyy-MM-dd]");
        }
        String usage = "event [description] /from [yyyy-MM-dd] /to [yyyy-MM-dd]";
        int fromIndex = findSingleParameter(arguments, "/from", usage);
        int toIndex = findSingleParameter(arguments, "/to", usage);
        if (fromIndex > toIndex) {
            throw new DukeException("The '/from' parameter must appear before '/to'. Use: " + usage);
        }
        String description = validateDescription(arguments.substring(0, fromIndex), "event");
        String fromString = arguments.substring(fromIndex + 5, toIndex).trim();
        String toString = arguments.substring(toIndex + 3).trim();
        if (fromString.isEmpty() || toString.isEmpty()) {
            throw new DukeException("The start and end dates of an event cannot be empty.");
        }

        try {
            LocalDate fromDate = LocalDate.parse(fromString);
            LocalDate toDate = LocalDate.parse(toString);
            if (!fromDate.isBefore(toDate)) {
                throw new DukeException("The event start date must be earlier than its end date.");
            }
            Task newEvent = new Event(description, fromDate, toDate);
            addUniqueTask(newEvent, tasks, ui);
        } catch (DateTimeParseException e) {
            throw new DukeException("Please provide event dates in yyyy-MM-dd format (e.g., 2019-12-02).");
        }
    }
}
