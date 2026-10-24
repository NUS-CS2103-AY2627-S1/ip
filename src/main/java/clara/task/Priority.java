package clara.task;

// NOTE: AI-assisted task priority feature. See CITATIONS.md [C-018].

/**
 * The importance level assigned to a task.
 */
public enum Priority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
