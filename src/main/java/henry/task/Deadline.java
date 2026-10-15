package henry.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d uuuu h:mm a", Locale.US);
    private final LocalDateTime deadline;

    /**
     * Creates a deadline task that is initially not done.
     *
     * @param description description of the task.
     * @param deadline date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    /**
     * Parses a deadline entered as either {@code d/M/yyyy HHmm} or {@code yyyy-MM-dd}.
     * A date without a time is stored at the start of that date.
     *
     * @param input user-entered deadline date and optional time.
     * @return parsed deadline.
     * @throws DateTimeParseException if the input is not a supported date format.
     */
    public static LocalDateTime parseBy(String input) throws DateTimeParseException {
        try {
            return LocalDateTime.parse(input, INPUT_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(input).atStartOfDay();
        }
    }

    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + deadline.format(OUTPUT_FORMATTER) + ")";
    }
}
