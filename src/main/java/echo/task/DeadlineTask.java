package echo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import echo.util.DateTimeParser;

/**
 * Represents a task with a due date and optional due time.
 */
public class DeadlineTask extends Task {
    /** Due date stored as a Java date value. */
    private final LocalDate by;

    /** Optional due time stored as a Java time value. */
    private final LocalTime byTime;

    /**
     * Creates a deadline task.
     *
     * @param description task description.
     * @param by due date with an optional time.
     */
    public DeadlineTask(String description, String by) {
        super(description);
        DateTimeParser.DateTimeValue dateTime = DateTimeParser.parse(by);
        this.by = dateTime.date();
        this.byTime = dateTime.time();
    }

    @Override
    public Optional<LocalDateTime> getDateTime() {
        return Optional.of(LocalDateTime.of(by, byTime != null ? byTime : LocalTime.MIDNIGHT));
    }

    @Override
    public String toFileFormat() {
        return "D | " + getDoneFlag() + " | " + getDescription() + " | "
                + DateTimeParser.formatForStorage(by, byTime);
    }

    @Override
    public boolean isDuplicate(Task other) {
        if (!super.isDuplicate(other) || !(other instanceof DeadlineTask otherDeadline)) {
            return false;
        }
        return by.equals(otherDeadline.by) && Objects.equals(byTime, otherDeadline.byTime);
    }

    @Override
    public String toString() {
        return "[Deadline]" + super.toString() + " (by: "
                + DateTimeParser.formatForDisplay(by, byTime) + ")";
    }
}
