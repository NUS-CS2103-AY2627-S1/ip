package potatobot.model.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import potatobot.exception.PotatoBotException;

/**
 * Represents a task that occurs between given start and end dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy",
            Locale.ENGLISH);

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event occurring between two dates.
     *
     * @param description Description of the event.
     * @param from        Start date in YYYY-MM-DD format.
     * @param to          End date in YYYY-MM-DD format.
     * @throws PotatoBotException If either date is invalid or the end date occurs
     *                            before the start date.
     */
    public Event(String description, String from, String to)
            throws PotatoBotException {
        super(description);

        try {
            startDate = parseDate(from);
            endDate = parseDate(to);
        } catch (DateTimeParseException exception) {
            throw new PotatoBotException(
                    "Use the date format YYYY-MM-DD, for example 2019-12-02.");
        }

        if (endDate.isBefore(startDate)) {
            throw new PotatoBotException(
                    "The event end date cannot be before its start date.");
        }
    }

    /**
     * Parses either a user-input date or a formatted date read from storage.
     *
     * @param date Date to parse.
     * @return Parsed date.
     * @throws DateTimeParseException If neither supported format matches.
     */
    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            return LocalDate.parse(date, OUTPUT_DATE_FORMAT);
        }
    }

    /**
     * Formats this task with its start and end dates.
     *
     * @return Task description and formatted event dates.
     */
    @Override
    public String toString() {
        return super.toString()
                + " (Event, from: " + startDate.format(OUTPUT_DATE_FORMAT)
                + " to: " + endDate.format(OUTPUT_DATE_FORMAT) + ")";
    }
}
