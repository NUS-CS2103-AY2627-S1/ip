package duke;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Interprets command arguments and date/time values entered by the user. */
public class Parser {
    /** Format accepted for deadline and event date/time arguments. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Parses a supported date/time string. */
    public LocalDateTime parseDate(String text) throws DateTimeParseException {
        return LocalDateTime.parse(text, DATE_TIME_FORMAT);
    }

    /** Extracts the argument following a command prefix. */
    public String argument(String command, int prefixLength) {
        return command.substring(prefixLength).trim();
    }

    /** Normalizes leading, trailing, and repeated whitespace in a command. */
    public String normalizeCommand(String command) {
        return command.trim().replaceAll("\\s+", " ");
    }
}
