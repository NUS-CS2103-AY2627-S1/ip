package happybot.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task managed by HappyBot.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private LocalDate completionDate;

    /**
     * Creates a task with the specified description.
     *
     * @param description The text entered by the user for this task.
     * @throws IllegalArgumentException If the description is blank or cannot be stored safely.
     */
    public Task(String description) {
        this.description = normalizeDescription(description);
        this.isDone = false;
        this.completionDate = null;
    }

    /**
     * Marks this task as completed without recording a completion date.
     *
     * <p>This method is used when loading legacy saved tasks. HappyBot records a date for every
     * task marked through the user command.
     */
    public void markAsDone() {
        markAsDone(null);
    }

    /**
     * Marks this task as completed on the specified date.
     *
     * <p>Marking an already completed task preserves its existing completion date so that one
     * task never contributes more than once to a week's statistics.
     *
     * @param completionDate The completion date, or null when loading a legacy task whose date
     *         was not saved.
     */
    public void markAsDone(LocalDate completionDate) {
        if (!isDone) {
            this.isDone = true;
            this.completionDate = completionDate;
        }
    }

    /**
     * Marks this task as not completed and clears its completion date.
     */
    public void unmarkAsDone() {
        this.isDone = false;
        this.completionDate = null;
    }

    /**
     * Returns the task description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return True if this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the date this task was most recently marked as completed.
     *
     * @return The completion date, or null when the task is not done or was loaded from legacy
     *         data with no recorded completion date.
     */
    public LocalDate getCompletionDate() {
        return completionDate;
    }

    /**
     * Returns whether this task has the same user-facing details as another task.
     *
     * <p>Completion status deliberately does not take part in this comparison: marking a task
     * done changes its progress, not the task that it represents.
     *
     * @param otherTask The task to compare with this task.
     * @return True if both tasks have the same type and identifying details.
     */
    public boolean hasSameDetails(Task otherTask) {
        return otherTask != null
                && getClass().equals(otherTask.getClass())
                && description.toLowerCase(Locale.ROOT)
                .equals(otherTask.description.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns a marker for display depending on task completion.
     *
     * @return "X" for a completed task, " " for an uncompleted task.
     */
    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns a safely storable, consistently spaced task description.
     */
    private static String normalizeDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("A task description cannot be null.");
        }

        String normalizedDescription = description.strip().replaceAll("\\s+", " ");
        if (normalizedDescription.isEmpty()) {
            throw new IllegalArgumentException("A task description cannot be empty.");
        }

        if (normalizedDescription.contains("|")) {
            throw new IllegalArgumentException("A task description cannot contain '|'.");
        }

        return normalizedDescription;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
