package aravii;

import java.util.Locale;

/**
 * Represents the description and completion state shared by every task type.
 */
public abstract class Task {
    private final String description;

    private boolean isCompleted;

    /**
     * Creates an incomplete task with a nonempty, single-line description.
     *
     * @param description The task description.
     */
    protected Task(String description) {
        if (description == null) {
            throw new IllegalArgumentException("The description cannot be null.");
        }
        Parser.validateText(description);
        if (description.isBlank()) {
            throw new IllegalArgumentException("The description cannot be empty.");
        }
        this.description = description.strip();
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        isCompleted = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmark() {
        isCompleted = false;
    }

    /**
     * Returns whether the description, displayed dates, or input dates match.
     *
     * @param keyword The case-insensitive search term.
     * @return Whether the term occurs in this task.
     */
    public boolean matches(String keyword) {
        String searchableText = description + " " + formatDetails() + " " + formatStorageDetails();
        return searchableText.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the task description used for display and sorting.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been marked complete.
     *
     * @return The completion state.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Returns the task category used for its display symbol and saved record.
     *
     * @return The task category.
     */
    abstract TaskType getType();

    /**
     * Formats the task-specific information for display.
     *
     * @return The details, or an empty string for an undated task.
     */
    abstract String formatDetails();

    /**
     * Formats task-specific information using the existing ISO save format.
     *
     * @return The details to store in the fourth save-file column.
     */
    abstract String formatStorageDetails();

    @Override
    public String toString() {
        String details = formatDetails();
        String status = isCompleted ? "[X]" : "[ ]";
        return "[" + getType().getSymbol() + "] " + status + " " + description
                + (details.isEmpty() ? "" : " " + details);
    }
}
