package luckynoslacky.luckytask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents the common state and behavior shared by all task types.
 */
public abstract class Task {
    private static final DateTimeFormatter TASK_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM dd uuuu, h.mma", Locale.ENGLISH);

    /** Identifies the concrete task category used by command validation. */
    public enum TaskType {
        /** A task without a date or time. */
        TODO,
        /** A task with a single completion time. */
        DEADLINE,
        /** A task with a start and end time. */
        EVENT
    }

    /**
     * Represents the completion state of a task.
     */
    public enum TaskStatus {
        /** Represents a task that has not been completed. */
        NOT_DONE,
        /** Represents a task that has been completed. */
        DONE
    }

    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task.
     *
     * @param description task description
     */
    protected Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }

        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void unmarkAsUndone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Checks whether this task is done.
     *
     * @return true if the task is done
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Returns this task's concise command-line representation.
     *
     * <p>Concrete task types prepend their emoji type tag and append any
     * type-specific timing details.
     *
     * @return common status and description text for this task
     */
    @Override
    public String toString() {
        return "[" + getStatusMarker() + "] " + description;
    }

    /**
     * Returns this task's description without display formatting.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether this task's description contains a search query.
     * Matching is case-insensitive and uses substring matching.
     *
     * @param query text to search for in the description
     * @return true if the description contains the query
     */
    public boolean matchesDescription(String query) {
        return description.toLowerCase(Locale.ROOT)
                .contains(query.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the concrete category of this task.
     *
     * @return task category
     */
    public abstract TaskType getTaskType();

    /**
     * Checks whether this task occurs on a date.
     *
     * @param date date to check
     * @return true if this task should appear in a date search
     */
    public final boolean occursOn(LocalDate date) {
        return getTaskTimes().occursOn(date);
    }

    /**
     * Extends this task's ending time by the supplied duration.
     *
     * @param amount duration by which to extend the ending time
     * @throws IllegalArgumentException if this task cannot be snoozed or the
     *                                  duration is invalid
     */
    public void snoozeBy(DurationPeriod amount) {
        reschedule(createSnoozedTimes(amount));
    }

    /**
     * Returns this task's current timing information.
     *
     * @return task timing information
     */
    public abstract TaskTimes getTaskTimes();

    /**
     * Replaces this task's schedule.
     *
     * @param newTimes replacement task timing information
     * @throws IllegalArgumentException if the schedule is invalid for this
     *                                  task type
     */
    public abstract void reschedule(TaskTimes newTimes);

    /**
     * Returns this task's ending time.
     *
     * @return ending time
     * @throws IllegalArgumentException if this task has no ending time
     */
    public abstract LocalDateTime getEndTime();

    /**
     * Verifies that this task supports schedule-changing commands.
     *
     * @throws TaskSchedulingException if this task cannot be scheduled
     */
    abstract void verifyCanBeScheduled();

    /**
     * Creates timing information after applying a snooze duration.
     *
     * @param amount duration by which to extend the ending time
     * @return timing information with the extended ending time
     * @throws IllegalArgumentException if the amount is invalid
     * @throws TaskSchedulingException if this task cannot be snoozed
     */
    abstract TaskTimes createSnoozedTimes(DurationPeriod amount);

    /**
     * Creates timing information after validating a requested schedule.
     *
     * @param startTime requested start time, or {@code null} for a deadline
     * @param endTime requested deadline or event ending time
     * @param currentTime current time used for deadline validation
     * @return validated replacement timing information
     * @throws IllegalArgumentException if required timing information is missing
     * @throws TaskSchedulingException if this task cannot be rescheduled or
     *                                  the requested schedule is invalid
     */
    abstract TaskTimes createRescheduledTimes(
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime currentTime);

    /**
     * Formats a task date and time using the established display convention.
     *
     * @param dateTime date and time to format
     * @return formatted task date and time
     */
    protected static String formatDateTime(LocalDateTime dateTime) {
        return TASK_TIME_FORMATTER.format(dateTime)
                .replace("AM", "am")
                .replace("PM", "pm");
    }

    /** Returns the emoji marker for this task's completion state. */
    private String getStatusMarker() {
        return isDone() ? "✅" : "❗";
    }

}
