package zinc.task;

/**
 * The supported categories of tasks and their list display identifiers.
 */
public enum TaskType {
    TODO("[T]", "T"),
    DEADLINE("[D]", "D"),
    EVENT("[E]", "E");

    /** The identifier shown before tasks of this type. */
    private final String displayIdentifier;

    /** The identifier written to task storage. */
    private final String storageIdentifier;

    /**
     * Creates a task type with its display identifier.
     *
     * @param displayIdentifier The prefix shown before a task in the list.
     * @param storageIdentifier The identifier written to task storage.
     */
    TaskType(String displayIdentifier, String storageIdentifier) {
        this.displayIdentifier = displayIdentifier;
        this.storageIdentifier = storageIdentifier;
    }

    /**
     * Returns the prefix displayed before tasks of this type.
     *
     * @return The display identifier.
     */
    public String getDisplayIdentifier() {
        return displayIdentifier;
    }

    /** Returns the identifier used for this type in task storage. */
    String getStorageIdentifier() {
        return storageIdentifier;
    }

    /** Returns the task type represented by a storage identifier. */
    static TaskType fromStorageIdentifier(String storageIdentifier) {
        for (TaskType taskType : values()) {
            if (taskType.storageIdentifier.equals(storageIdentifier)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Unknown task type");
    }
}
