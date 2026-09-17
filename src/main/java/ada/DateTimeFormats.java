package ada;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
/**
 * Stores the date and time formats shared by the application.
 */
public final class DateTimeFormats {
    public static final LocalTime DEFAULT_TIME =
            LocalTime.of(23, 59);
    public static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern(
                    "MMM dd yyyy", Locale.ENGLISH);
    public static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern(
                    "h:mm a", Locale.ENGLISH);
    public static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    public static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormats() {
        // Prevent instantiation of this utility class.
    }
}
