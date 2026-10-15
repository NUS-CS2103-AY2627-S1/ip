package zikiai.task;

/**
 * Represents the priority assigned to a task.
 */
public enum Priority {
    NONE("", ""),
    LOW("[LOW]", "[L]"),
    MEDIUM("[MEDIUM]", "[M]"),
    HIGH("[HIGH]", "[H]");

    private final String displayTag;
    private final String storageTag;

    Priority(String displayTag, String storageTag) {
        this.displayTag = displayTag;
        this.storageTag = storageTag;
    }

    String getDisplayTag() {
        return displayTag;
    }

    String getStorageTag() {
        return storageTag;
    }
}
