package yawned.task;

/**
 * Represents the completion state of a task.
 */
public enum TaskStatus {
    DONE("[X]", "1"),
    NOT_DONE("[ ]", "0");

    private final String icon;
    private final String storageValue;

    TaskStatus(String icon, String storageValue) {
        this.icon = icon;
        this.storageValue = storageValue;
    }

    /**
     * Returns the icon used to display this status.
     *
     * @return Status icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the value used to save this status to disk.
     *
     * @return {@code "1"} for done tasks, otherwise {@code "0"}.
     */
    public String getStorageValue() {
        return storageValue;
    }
}
