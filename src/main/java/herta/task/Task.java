package herta.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides common behavior for tasks in Herta's task list.
 *
 * <p>Todo, Deadline, and Event inherit the common completion and description
 * behavior from this abstract base class.</p>
 */
public abstract class Task {
    /** Separates fields in serialized task records. */
    protected static final String STORAGE_FIELD_SEPARATOR = " | ";

    private static final int INCOMPLETE_STATUS_CODE = 0;
    private static final int COMPLETED_STATUS_CODE = 1;
    private static final String INCOMPLETE_STATUS_ICON = " ";
    private static final String COMPLETED_STATUS_ICON = "X";

    private final String description;
    private boolean isCompleted;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description.
     */
    public Task(String description) {
        this.description = Objects.requireNonNull(description,
                "A task description cannot be null.");
        TaskDescriptionValidator.validate(description);
        this.isCompleted = false;
    }

    /**
     * Returns the icon used to display this task's completion status.
     *
     * @return {@code X} if the task is done, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return isCompleted ? COMPLETED_STATUS_ICON : INCOMPLETE_STATUS_ICON;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isCompleted = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isCompleted = false;
    }

    /**
     * Returns whether this task is currently complete.
     *
     * @return {@code true} if the task is complete.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Determines whether this task occurs on a particular calendar date.
     * Tasks without date/time information do not occur on any date.
     *
     * @param date the date to check.
     * @return {@code true} if this task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date/time used when arranging tasks chronologically or
     * finding upcoming tasks. Tasks without date/time information return an
     * empty value.
     *
     * @return the task's relevant date/time, if it has one.
     */
    public Optional<LocalDateTime> getScheduledDateTime() {
        return Optional.empty();
    }

    /**
     * Determines whether the task's scheduled date/time falls in a future
     * time window. The start is inclusive and the end is exclusive.
     *
     * @param now the beginning of the time window.
     * @param until the exclusive end of the time window.
     * @return {@code true} if the task is scheduled within the window.
     */
    public boolean isUpcoming(LocalDateTime now, LocalDateTime until) {
        Objects.requireNonNull(now, "An upcoming window needs a start.");
        Objects.requireNonNull(until, "An upcoming window needs an end.");
        if (until.isBefore(now)) {
            throw new IllegalArgumentException(
                    "An upcoming window must end at or after it starts.");
        }
        return getScheduledDateTime()
                .map(dateTime -> !dateTime.isBefore(now) && dateTime.isBefore(until))
                .orElse(false);
    }

    /**
     * Returns this task in the format used by Herta's data file.
     *
     * @return the serialized task text.
     */
    public abstract String toStorageString();

    /**
     * Returns the numeric completion status used in the data file.
     *
     * @return {@code 1} when done, or {@code 0} otherwise.
     */
    protected int getCompletionStatusCode() {
        return isCompleted ? COMPLETED_STATUS_CODE : INCOMPLETE_STATUS_CODE;
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task formatted with its completion status and description.
     *
     * @return the formatted task text.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
