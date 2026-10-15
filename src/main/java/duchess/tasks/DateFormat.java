package duchess.tasks;

import java.time.format.DateTimeFormatter;

/**
 * Standardizes the date formats for display
 * and parsing.
 */
public final class DateFormat {

    /** Represents the expected date format as text for user-facing messages. */
    public static final String PARSE_FORMAT_STRING = "yyyy-MM-dd";

    /** Represents an example date in the parse format. */
    public static final String PARSE_FORMAT_EXAMPLE = "2001-09-11";

    /** Represents the date in creation and storage. */
    public static final DateTimeFormatter PARSE_FORMAT =
            DateTimeFormatter.ofPattern(PARSE_FORMAT_STRING);

    /** Represents the date when displayed to the user. */
    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // DateFormat should not be initializable.
    private DateFormat() {
    }
}
