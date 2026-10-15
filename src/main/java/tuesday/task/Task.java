package tuesday.task;

import java.util.Locale;

/**
 * Represents a task entered by the user and whether it has been completed.
 */
public class Task {
    private boolean isDone;
    private final String name;

    /**
     * Creates a task.
     *
     * @param name the name of the task.
     */
    public Task(String name) {
        this.name = name;
        isDone = false;
    }

    /**
     * Marks the task as done.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return true if this task is marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Checks whether the task description contains a keyword.
     *
     * @param keyword the keyword to search for.
     * @return true if the description contains the keyword, ignoring case.
     */
    public boolean matchesDescription(String keyword) {
        return name.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Checks whether another task has the same normalized description.
     *
     * <p>Task type, scheduling information, and completion status are deliberately ignored. Leading and trailing
     * whitespace, repeated internal whitespace, and letter case do not affect the comparison.</p>
     *
     * @param other the task to compare with.
     * @return true if both tasks have the same normalized description.
     */
    public boolean hasSameDescription(Task other) {
        if (other == null) {
            return false;
        }
        return normalizeDescription(name).equals(normalizeDescription(other.name));
    }

    /**
     * Produces the description form used only for duplicate comparisons.
     */
    private static String normalizeDescription(String description) {
        return description.strip()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the task's string representation.
     *
     * @return the task's string representation.
     */
    @Override
    public String toString() {
        if (isDone) {
            return "[X] " + name;
        } else {
            return "[ ] " + name;
        }
    }
}
