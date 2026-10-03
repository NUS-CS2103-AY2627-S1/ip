package verity.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import verity.exception.VerityException;
import verity.task.Event;

/**
 * Creates validated task-related values from parsed input.
 */
final class TaskFactory {
    private TaskFactory() {
    }

    /**
     * Parses a date in ISO-8601 format.
     *
     * @param dateText Date text to parse.
     * @return Parsed date.
     * @throws VerityException If the date text is invalid.
     */
    static LocalDate parseDate(String dateText)
            throws VerityException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new VerityException(
                    "Dates must use the format yyyy-MM-dd.");
        }
    }

    /**
     * Creates an event after validating its date range.
     *
     * @param description Description of the event.
     * @param fromDate First date of the event.
     * @param toDate Last date of the event.
     * @return Event with the specified details.
     * @throws VerityException If the date range is invalid.
     */
    static Event createEvent(
            String description, LocalDate fromDate, LocalDate toDate)
            throws VerityException {
        try {
            return new Event(description, fromDate, toDate);
        } catch (IllegalArgumentException exception) {
            throw new VerityException(exception.getMessage());
        }
    }
}
