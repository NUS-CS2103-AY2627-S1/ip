package arthur.tasks;

/**
 * Identifies the supported task categories.
 */
public enum TaskType {
    EVENT("[E]"),
    TODO("[T]"),
    DEADLINE("[D]");


    private final String displayName;

    TaskType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
