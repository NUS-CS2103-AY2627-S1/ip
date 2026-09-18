package bobby.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Pattern;

import bobby.storage.Storage;

/**
 * Represents one task in Bobby's task list.
 */
public class Task {
    private static final DateTimeFormatter COMPLETION_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    protected String description;
    protected boolean isDone;
    private LocalDateTime completionDateTime;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
        this.completionDateTime = null;
    }

    /**
     * Returns the task's status icon for display.
     *
     * @return {@code X} when done, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        if (!isDone) {
            isDone = true;
            completionDateTime = null;
        }
    }

    /**
     * Marks this task as done at the supplied date and time if it is currently incomplete.
     *
     * @param completionDateTime the date and time at which the task was completed.
     */
    public void markAsDone(LocalDateTime completionDateTime) {
        assert completionDateTime != null : "Completion date and time must not be null";
        if (!isDone) {
            isDone = true;
            this.completionDateTime = completionDateTime.truncatedTo(ChronoUnit.SECONDS);
        }
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
        completionDateTime = null;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} when this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task has a known completion date and time.
     *
     * @return {@code true} when a completion date and time is recorded.
     */
    public boolean hasKnownCompletionDateTime() {
        return completionDateTime != null;
    }

    /**
     * Returns the recorded completion date and time, when known.
     *
     * @return the completion date and time, or an empty value for pending and legacy completed tasks.
     */
    public Optional<LocalDateTime> getCompletionDateTime() {
        return Optional.ofNullable(completionDateTime);
    }

    /**
     * Returns whether this task has the same type and defining details as another task.
     * Completion state is deliberately excluded because it does not make a task distinct.
     *
     * @param other the task to compare with.
     * @return {@code true} when both tasks represent the same duty.
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDescription(description).equalsIgnoreCase(
                        normalizeDescription(other.description));
    }

    /**
     * Returns a description with inconsequential whitespace differences removed.
     */
    private static String normalizeDescription(String description) {
        return WHITESPACE_PATTERN.matcher(description.strip()).replaceAll(" ");
    }

    /**
     * Returns whether this task is currently completed within a half-open date-time interval.
     *
     * @param startDateTime the inclusive start of the interval.
     * @param endDateTime the exclusive end of the interval.
     * @return {@code true} when this task has a known completion within the interval.
     */
    public boolean wasCompletedBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        assert startDateTime != null : "Interval start must not be null";
        assert endDateTime != null : "Interval end must not be null";
        assert startDateTime.isBefore(endDateTime) : "Interval start must be before its end";
        return isDone
                && completionDateTime != null
                && !completionDateTime.isBefore(startDateTime)
                && completionDateTime.isBefore(endDateTime);
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task in the on-disk format used by {@link Storage}.
     *
     * @return a line that represents this task in the save file
     */
    public String toFileString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description
                + " | " + getCompletionDateTimeFileValue();
    }

    /**
     * Returns the completion date and time in Bobby's on-disk format.
     *
     * @return the formatted completion date and time, or {@code -} when it is unknown.
     */
    protected String getCompletionDateTimeFileValue() {
        return completionDateTime == null
                ? "-"
                : completionDateTime.format(COMPLETION_DATE_TIME_FORMAT);
    }

    /**
     * Returns this task in the standard list format.
     *
     * @return the formatted task
     */
    @Override
    public String toString() {
        return "[?][" + getStatusIcon() + "] " + description;
    }

}
