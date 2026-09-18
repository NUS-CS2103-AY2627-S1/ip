package altair.task;

import java.util.Locale;

/**
 * The base type for every task in the task list.
 *
 * <p>Subclasses inherit the shared description and completion state while
 * customizing the type marker and any scheduling details they display.</p>
 */
public class Task {
    /** The text entered by the user for this task. */
    protected String description;

    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates an unfinished task.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        // Every caller (Altair.createTask, Storage.taskFromFileLine) rejects an
        // empty description before reaching here, so a blank one signals a bug
        // in the caller rather than bad user input.
        assert description != null && !description.isBlank()
                : "task description should be validated as non-blank before construction";
        this.description = description;
        this.isDone = false;
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
     * Returns the status symbol used when displaying this task.
     *
     * @return {@code X} for a done task, otherwise a blank symbol.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task's description contains the given text,
     * ignoring case.
     *
     * @param keyword the text to search for.
     * @return {@code true} if the description contains {@code keyword}.
     */
    public boolean descriptionContains(String keyword) {
        return description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Reports whether this task should be flagged as a duplicate of another.
     *
     * <p>Two tasks duplicate each other when their descriptions match, ignoring
     * case and whitespace differences. If they are also the same concrete task
     * type, their schedules must match too &mdash; two {@link Deadline}s (or two
     * {@link Event}s) with different dates are different commitments, not
     * duplicates, even though their wording is identical. Completion status
     * never affects the result.</p>
     *
     * @param other the task to compare against.
     * @return {@code true} if {@code other} should be flagged as a duplicate.
     */
    public final boolean isDuplicateOf(Task other) {
        if (!hasSameDescription(other)) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return true;
        }
        return hasSameSchedule(other);
    }

    /** Compares descriptions ignoring case and differences in whitespace. */
    private boolean hasSameDescription(Task other) {
        return normalizedDescription().equals(other.normalizedDescription());
    }

    /** Collapses internal whitespace so accidental extra spaces do not hide a duplicate. */
    private String normalizedDescription() {
        return description.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Reports whether this task's schedule, if any, matches another task's, for
     * two tasks already known to be the same concrete type.
     *
     * <p>The base {@link Task} (and {@link Todo}) carry no schedule, so any two
     * of them match trivially. {@link Deadline} and {@link Event} override this
     * to compare their date fields.</p>
     *
     * @param other another task of this exact same class.
     * @return {@code true} if the schedules match, or this task type has none.
     */
    protected boolean hasSameSchedule(Task other) {
        return true;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the category of this task.
     *
     * @return the task category.
     */
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }

    /**
     * Returns the one-letter marker used for this task type.
     *
     * @return the task type marker.
     */
    protected String getTypeIcon() {
        return getTaskType().getIcon();
    }

    /**
     * Formats this task for the on-disk task list.
     *
     * @return the task type, completion state, and description separated by pipes.
     */
    public String toFileString() {
        return getTypeIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Formats the task for the list and completion messages.
     *
     * @return the task type, completion state, and description.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
