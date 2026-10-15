package amy.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a task that must be completed by a specified date and time.
 */
public class Deadline extends Task {
    public static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    public static final DateTimeFormatter SAVE_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = new DateTimeFormatterBuilder()
            .appendText(ChronoField.MONTH_OF_YEAR, Map.ofEntries(
                    Map.entry(1L, "Jan"), Map.entry(2L, "Feb"), Map.entry(3L, "Mar"),
                    Map.entry(4L, "Apr"), Map.entry(5L, "May"), Map.entry(6L, "Jun"),
                    Map.entry(7L, "Jul"), Map.entry(8L, "Aug"), Map.entry(9L, "Sept"),
                    Map.entry(10L, "Oct"), Map.entry(11L, "Nov"), Map.entry(12L, "Dec")))
            .appendPattern(" d yyyy, h:mm")
            .appendText(ChronoField.AMPM_OF_DAY, Map.of(0L, "am", 1L, "pm"))
            .toFormatter(Locale.ENGLISH);
    protected LocalDateTime by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description the deadline description
     * @param by the deadline date and time, in the format d/M/yyyy HHmm (e.g. 2/12/2019 1800)
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, INPUT_FORMAT);
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String getDisplayText() {
        return description + " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns the deadline date and time.
     *
     * @return the deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public Task copy() {
        Deadline copy = new Deadline(description, by.format(INPUT_FORMAT));
        if (isDone) {
            copy.markAsDone();
        }
        return copy;
    }
}
