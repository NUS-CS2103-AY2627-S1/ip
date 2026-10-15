package apollo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A task that must be completed by a particular date and time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mma");

    protected LocalDateTime by;

    /**
     * Creates a deadline with its due date and time.
     *
     * @param description description of the task
     * @param by date and time by which the task is due
     */
    public Deadline(String description, LocalDateTime by) {
        this.by = by;
        super(description);
    }

    @Override
    public String getIcon() {
        return "D";
    }

    /** Returns the date and time by which this task is due. */
    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (by: %s)", this.getIcon(), this.getStatusIcon(),
                this.description, this.by.format(DISPLAY_FORMATTER));
    }

    @Override
    public String toFileString() {
        return String.format("D | %d | %s | %s", this.isDone ? 1 : 0, this.description,
                this.by.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
