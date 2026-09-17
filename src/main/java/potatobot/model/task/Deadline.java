package potatobot.model.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import potatobot.exception.PotatoBotException;

/**
 * Represents a task that must be completed by a given date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy",
            Locale.ENGLISH);

    private final LocalDate deadlineDate;

    /**
     * Creates a task that must be completed by a given date.
     *
     * @param description Description of the task.
     * @param by          Deadline date in YYYY-MM-DD format.
     * @throws PotatoBotException If the deadline date is invalid.
     */
    public Deadline(String description, String by) throws PotatoBotException {
        super(description);

        try {
            deadlineDate = parseDate(by);
        } catch (DateTimeParseException exception) {
            throw new PotatoBotException(
                    "Use the date format YYYY-MM-DD, for example 2019-12-02.");
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
     * Formats this task with its deadline date.
     *
     * @return Task description and formatted deadline date.
     */
    @Override
    public String toString() {
        return super.toString()
                + " (Deadline, by: " + deadlineDate.format(OUTPUT_DATE_FORMAT) + ")";
    }
}
