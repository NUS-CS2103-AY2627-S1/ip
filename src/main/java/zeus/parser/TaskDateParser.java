package zeus.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import zeus.exception.ZeusException;

/** Parses and validates dates used by dated tasks. */
public final class TaskDateParser {
    /** Prevents creation of a stateless task date parser. */
    private TaskDateParser() {
    }

    /**
     * Parses a date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText Date text to parse.
     * @param fieldName Name used to identify an invalid field.
     * @return Parsed date.
     * @throws ZeusException If the text is not a valid ISO date.
     */
    public static LocalDate parse(String dateText, String fieldName) throws ZeusException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new ZeusException("The " + fieldName
                    + " date must use yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    /**
     * Ensures that an event does not finish before it starts.
     *
     * @param startDate Event start date.
     * @param endDate Event end date.
     * @throws ZeusException If the end date precedes the start date.
     */
    public static void validateEventDates(LocalDate startDate, LocalDate endDate)
            throws ZeusException {
        if (endDate.isBefore(startDate)) {
            throw new ZeusException("The event end date cannot be before its start date.");
        }
    }
}
