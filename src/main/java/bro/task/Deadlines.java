package bro.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified date and optional time. */
public class Deadlines extends Task {
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu h:mma", Locale.ENGLISH);

    private final LocalDateTime dueDateTime;
    private final boolean hasDueTime;

    /** Creates a date-only deadline task with its due date and description. */
    public Deadlines(LocalDate dueDate, String description) {
        this(dueDate.atStartOfDay(), false, description);
    }

    /** Creates a deadline task with both a due date and a due time. */
    public Deadlines(LocalDateTime dueDateTime, String description) {
        this(dueDateTime, true, description);
    }

    /** Creates a deadline restored from saved data, retaining whether its time was supplied. */
    public Deadlines(LocalDateTime dueDateTime, boolean hasDueTime, String description) {
        super(description, TaskType.DEADLINE);
        this.dueDateTime = dueDateTime;
        this.hasDueTime = hasDueTime;
    }

    /** Returns the typed deadline value used when saving the task. */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    /** Returns whether the user supplied a time as part of this deadline. */
    public boolean hasDueTime() {
        return hasDueTime;
    }

    /** Formats the due value for the chatbot's human-readable task list. */
    private String formatDueDateTime() {
        return hasDueTime
                ? dueDateTime.format(DATE_TIME_DISPLAY_FORMAT)
                : dueDateTime.format(DATE_DISPLAY_FORMAT);
    }

    /** Returns the deadline task in Bro's list format. */
    @Override
    public String toString() {
        return formatTask(getDescription() + "(by: " + formatDueDateTime() + ")");
    }
}
