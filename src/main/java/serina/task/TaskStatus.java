package serina.task;

import serina.exception.SerinaError;
import serina.exception.SerinaException;

/**
 * Represents whether a task is done and how that status is displayed.
 */
public enum TaskStatus {
    /** A completed task. */
    DONE("X", "1"),
    /** A task that has not been completed. */
    NOT_DONE(" ", "0");

    private final String icon;
    private final String fileValue;

    /**
     * Creates a task status with the given display icon.
     *
     * @param icon Short icon shown beside a task.
     * @param fileValue Value used when saving the status to disk.
     */
    TaskStatus(String icon, String fileValue) {
        this.icon = icon;
        this.fileValue = fileValue;
    }

    /**
     * Returns the icon shown when displaying this task status.
     *
     * @return The display icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the compact value written to the save file.
     *
     * @return The save-file value.
     */
    public String getFileValue() {
        return fileValue;
    }

    /**
     * Returns the status matching a value from the save file.
     *
     * @param value Save-file value to parse.
     * @return The matching status.
     * @throws SerinaException If the value does not match a supported task status.
     */
    public static TaskStatus parseFileValue(String value) throws SerinaException {
        for (TaskStatus status : values()) {
            if (status.fileValue.equals(value.trim())) {
                return status;
            }
        }

        throw new SerinaException(SerinaError.LOAD_FAILED);
    }
}
