package kiwi.task;

import kiwi.KiwiException;

/**
 * The kind of task Kiwi can store. Each value has a one-letter display icon.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the letter shown in list output, e.g. {@code T} for a to-do.
     *
     * @return type icon letter.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the task type for a one-letter save-file icon such as {@code T}.
     *
     * @param icon type letter from a save line.
     * @return matching task type.
     * @throws KiwiException If {@code icon} is not a known type letter.
     */
    public static TaskType fromIcon(String icon) throws KiwiException {
        for (TaskType taskType : values()) {
            if (taskType.icon.equals(icon)) {
                return taskType;
            }
        }
        throw new KiwiException("unknown task type \"" + icon + "\"");
    }

    /**
     * Returns the bracketed type label, e.g. {@code [T]}.
     *
     * @return formatted type tag.
     */
    @Override
    public String toString() {
        return "[" + icon + "]";
    }
}
