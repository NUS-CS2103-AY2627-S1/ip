package dave.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Represents a task that must be completed before a specified deadline.
 */
public class Deadline extends Task {

    /** Formatter for displaying date-only deadlines. */
    private static final DateTimeFormatter DATE_ONLY_OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    /** Formatter for displaying date and time deadlines. */
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
    /** Formatter for serializing date-time to persistent storage. */
    private static final DateTimeFormatter DATE_TIME_FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Deadline date and optional time. */
    private LocalDateTime by;
    /** Indicates whether time of day was explicitly specified. */
    private boolean hasTime;

    /**
     * Constructs a new Deadline task with description, deadline date-time, and time presence flag.
     *
     * @param description Description of the task.
     * @param by Deadline date and time.
     * @param hasTime True if time was specified, false if date only.
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Constructs a new Deadline task with description and deadline date-time with time of day.
     *
     * @param description Description of the task.
     * @param by Deadline date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, by, true);
    }

    /**
     * Constructs a new Deadline task with description and date-only deadline.
     *
     * @param description Description of the task.
     * @param by Deadline date.
     */
    public Deadline(String description, LocalDate by) {
        this(description, by.atStartOfDay(), false);
    }

    @Override
    public boolean canSnooze() {
        return true;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return Deadline LocalDateTime object.
     */
    public LocalDateTime getBy() {
        return this.by;
    }

    /**
     * Returns whether time of day was explicitly specified for the deadline.
     *
     * @return True if time was specified, false otherwise.
     */
    public boolean hasTime() {
        return this.hasTime;
    }

    /**
     * Updates the deadline date and time to a new target.
     *
     * @param by New deadline date and time.
     * @param hasTime True if time was specified, false if date only.
     */
    public void snoozeTo(LocalDateTime by, boolean hasTime) {
        assert by != null : "Deadline date-time cannot be null when snoozing";
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Postpones the deadline by the specified duration.
     *
     * @param amount Amount of time units to add.
     * @param unit Unit of time to add.
     */
    public void snoozeBy(long amount, ChronoUnit unit) {
        assert unit != null : "ChronoUnit cannot be null when snoozing";
        this.by = this.by.plus(amount, unit);
    }

    @Override
    public boolean isSameTask(Task other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Deadline)) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return this.getDescription().equalsIgnoreCase(otherDeadline.getDescription())
                && this.by.equals(otherDeadline.by);
    }

    @Override
    public String toFileFormat() {
        String byString = this.hasTime
                ? this.by.format(DATE_TIME_FILE_FORMATTER)
                : this.by.toLocalDate().toString();
        return String.format("D | %d | %s | %s", this.isDone() ? 1 : 0, this.getDescription(), byString);
    }

    @Override
    public String toString() {
        String byString = this.hasTime
                ? this.by.format(DATE_TIME_OUTPUT_FORMATTER)
                : this.by.format(DATE_ONLY_OUTPUT_FORMATTER);
        return String.format("[D]%s (by: %s)", super.toString(), byString);
    }
}
