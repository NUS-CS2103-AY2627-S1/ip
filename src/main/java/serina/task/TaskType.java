package serina.task;

import serina.exception.SerinaError;
import serina.exception.SerinaException;

/**
 * Represents the supported task types and their display icons.
 */
public enum TaskType {
    /** A task without an associated date. */
    TODO("T"),
    /** A task due on a specific date. */
    DEADLINE("D"),
    /** A task occurring over a date range. */
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with the given display icon.
     *
     * @param icon Short icon shown beside a task.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon shown when displaying this task type.
     *
     * @return The display icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the task type matching a value from the save file.
     *
     * @param value Save-file value to parse.
     * @return The matching task type.
     * @throws SerinaException If the value does not match a supported task type.
     */
    public static TaskType parseFileValue(String value) throws SerinaException {
        for (TaskType type : values()) {
            if (type.icon.equals(value.trim())) {
                return type;
            }
        }

        throw new SerinaException(SerinaError.LOAD_FAILED);
    }
}
