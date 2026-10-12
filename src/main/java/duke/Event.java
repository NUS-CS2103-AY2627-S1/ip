package duke;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an event task, which has a description and a start and end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final String FORMAT_ERROR =
            "An event needs /from and /to times. e.g. event meeting /from Mon /to Tue";

    private static final String FROM = "/from";
    private static final String TO = "/to";

    private final LocalDate start;
    private final LocalDate end;



    /**
     * Creates an event with the given description, start date, and end date.
     *
     * @param name the task description
     * @param start the start date in ISO format (yyyy-mm-dd)
     * @param end the end date in ISO format (yyyy-mm-dd)
     * @throws DuckeException if either date is not a valid yyyy-mm-dd date
     */
    public Event(String name, String start, String end) throws DuckeException {
        super(name);
        try {
            this.start = LocalDate.parse(start.trim());
            this.end = LocalDate.parse(end.trim());
        } catch (DateTimeParseException e) {
            throw new DuckeException("Please use dates like 2019-10-15.");
        }
    }

    /**
     * Creates an Event from user input of the form "description /from date /to date".
     *
     * @param info the argument typed by the user
     * @return the created Event
     * @throws DuckeException if the description is empty, a /from or /to marker is missing, or a date is invalid
     */
    public static Event of(String info) throws DuckeException {
        String[] descAndTimeRange = info.split(FROM, 2);
        String description = descAndTimeRange[0].trim();

        if (description.isBlank()) {
            throw new DuckeException("Task description cannot be empty.");
        }
        if (descAndTimeRange.length < 2) {
            throw new DuckeException(FORMAT_ERROR);
        }

        String[] timeRange = descAndTimeRange[1].split(TO, 2);
        if (timeRange.length < 2 || timeRange[0].isBlank() || timeRange[1].isBlank()) {
            throw new DuckeException(FORMAT_ERROR);
        }

        return new Event(description, timeRange[0].trim(), timeRange[1].trim());
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String getExtraInfo() {
        return " (from: " + start.format(DISPLAY_FORMAT) + " to: " + end.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + start + " | " + end;
    }
}
