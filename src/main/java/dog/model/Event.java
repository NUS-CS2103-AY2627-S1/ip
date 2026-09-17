package dog.model;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dog.exceptions.DogException;
import dog.storage.DateUtils;

/**
 * Represents an event task that occurs within a specific time period.
 */
public class Event extends Task {
    static final String TASK_ICON = "E";

    protected LocalDate fromDate;
    protected LocalDate toDate;

    /**
     * Creates an event task with the specified description and date range.
     *
     * @param description the task description.
     * @param fromDate    the start date of the event.
     * @param toDate      the end date of the event.
     */
    public Event(String description, LocalDate fromDate, LocalDate toDate) {
        super(description);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Creates an event task with the specified description, date range, and completion status.
     *
     * @param description the task description.
     * @param fromDate    the start date of the event.
     * @param toDate      the end date of the event.
     * @param isDone      true if the task is completed, false otherwise.
     */
    public Event(String description, LocalDate fromDate, LocalDate toDate, boolean isDone) {
        super(description, isDone);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Parses an event task from its saved string format.
     *
     * @param saveString the saved string in format "E | [X or space] | from | to | description".
     * @return the parsed Event, or null if the format is invalid.
     */
    public static Event fromSaveFormat(String saveString) {
        if (saveString == null) {
            return null;
        }

        String[] parts = saveString.split(" \\| ", 5);
        if (parts.length < 5) {
            return null;
        }

        String taskIcon = parts[0];
        String statusIcon = parts[1];
        String fromStr = parts[2];
        String toStr = parts[3];
        String description = parts[4];

        if (!taskIcon.equals(TASK_ICON)) {
            return null;
        }
        if (isInvalidStatusIcon(statusIcon)) {
            return null;
        }

        boolean isDone = isCompletedStatusIcon(statusIcon);

        try {
            LocalDate fromDate = DateUtils.parse(fromStr);
            LocalDate toDate = DateUtils.parse(toStr);
            return new Event(description, fromDate, toDate, isDone);
        } catch (DogException e) {
            System.out.println("Error loading event: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses an event task from user input.
     *
     * @param input the user input string.
     * @return the parsed Event.
     * @throws DogException if the input format is invalid.
     */
    public static Event parse(String input) throws DogException {
        final String badInputMessage = "ARF ARF Event tasks must have a description, a start date, and an end date!\n"
                + "Expected: event <description> /from <start date> /to <end date>";

        // Pattern: "<description> /from <from> /to <to>"
        Pattern pattern = Pattern.compile("^\\s*(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+?)\\s*$");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new DogException(badInputMessage);
        }

        assert matcher.groupCount() == 3 : "Todo regex group count should be 3";
        String description = matcher.group(1).trim();
        String fromStr = matcher.group(2).trim();
        String toStr = matcher.group(3).trim();

        if (description.isEmpty() || fromStr.isEmpty() || toStr.isEmpty()) {
            throw new DogException(badInputMessage);
        }

        LocalDate fromDate = DateUtils.parse(fromStr);
        LocalDate toDate = DateUtils.parse(toStr);
        assert fromDate != null : "parsed start (from) LocalDate should not be null";
        assert toDate != null : "parsed end (to) LocalDate should not be null";
        return new Event(description, fromDate, toDate);
    }

    /**
     * Returns a string representation of the event task for saving to file.
     *
     * @return the event in save format.
     */
    @Override
    public String toSaveFormat() {
        return String.format(TASK_ICON + " | %s | %s | %s | %s", getStatusIcon(),
                fromDate.toString(), toDate.toString(), description);
    }

    /**
     * Returns a string representation of the event task for display.
     *
     * @return the event in display format.
     */
    @Override
    public String toString() {
        return "[" + TASK_ICON + "]" + super.toString() + " (from: " + DateUtils.format(this.fromDate)
                + " to: " + DateUtils.format(this.toDate) + ")";
    }
}
