package grug.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;

/**
 * Represents a task, storing the task `details` and whether the task has been
 * completed or not.
 */
public abstract class Task {
    public static final String DATE_TIME_INPUT_PATTERN = "[yyyy-]MM-dd[ HH[:]mm]";
    public static final DateTimeFormatter DATE_TIME_INPUT_FORMATTER = new DateTimeFormatterBuilder()
            .optionalStart()
            .appendValue(ChronoField.YEAR, 4)
            .appendLiteral('-')
            .optionalEnd()
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .optionalStart()
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .optionalStart()
            .appendLiteral(':')
            .optionalEnd()
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalEnd()
            .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    protected static final String DATE_TIME_DISPLAY_PATTERN = "MMM dd yyyy HHmm";
    protected static final DateTimeFormatter DATE_TIME_DISPLAY_FORMATTER = DateTimeFormatter
            .ofPattern(DATE_TIME_DISPLAY_PATTERN);

    private String details;
    private boolean isCompleted;
    private TaskPriority priority;

    /**
     * Creates a new *incomplete* `Task`.
     *
     * @param details the details of the task.
     */
    public Task(String details) {
        this(details, false);
    }

    /**
     * Creates a new `Task`.
     *
     * @param details     the details of the task.
     * @param isCompleted whether the task has been completed.
     */
    public Task(String details, boolean isCompleted) {
        this(details, isCompleted, TaskPriority.DEFAULT);
    }

    /**
     * Creates a new `Task`.
     *
     * @param details     the details of the task.
     * @param isCompleted whether the task has been completed.
     * @param priority    the priority of the task.
     */
    public Task(String details, boolean isCompleted, TaskPriority priority) {
        this.details = details;
        this.isCompleted = isCompleted;
        this.priority = priority;
    }

    /**
     * Marks the task as complete.
     */
    public void markComplete() {
        this.isCompleted = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void markIncomplete() {
        this.isCompleted = false;
    }

    public String getDetails() {
        return this.details;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public TaskPriority getPriority() {
        return this.priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * Determines whether the task falls on / coincides with the given date.
     *
     * @param date The target date that the task may coincide with.
     * @return {@code true} if the task coincides with the date, {@code false}
     *         otherwise.
     */
    public abstract boolean doesOccurOn(LocalDate date);

    @Override
    public String toString() {
        String checkbox = this.isCompleted ? "[X]" : "[ ]";
        String priorityBox = "[" + this.priority.getDisplayName() + "]";
        return "%s%s %s".formatted(checkbox, priorityBox, details);
    }
}
