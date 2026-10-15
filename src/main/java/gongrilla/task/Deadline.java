package gongrilla.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import lombok.Getter;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu, h:mma", Locale.ENGLISH);

    @Getter
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline with a due date and time.
     *
     * @param name description of the deadline.
     * @param by date and time by which the task must be completed.
     */
    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = Objects.requireNonNull(by, "Where deadline? Gongrilla need.");
    }

    /**
     * Returns this task's category for command confirmations.
     *
     * @return deadline.
     */
    @Override
    public TaskType getType() {
        return TaskType.DEADLINE;
    }

    /**
     * Converts this deadline into one encoded storage record.
     *
     * @return serialized deadline record.
     */
    @Override
    public String toDataString() {
        return "D2 | " + commonDataFields() + " | " + encodeField(by.toString());
    }

    /**
     * Returns the deadline's user-facing representation with its due time.
     *
     * @return formatted deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + by.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
