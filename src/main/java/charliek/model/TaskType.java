package charliek.model;

import java.util.Optional;

/**
 * Represents the supported kinds of task and their display icons.
 */
public enum TaskType {
    /**
     * A task without a date or time.
     */
    TODO("T"),

    /**
     * A task with a completion deadline.
     */
    DEADLINE("D"),

    /**
     * A task with a start and end date or time.
     */
    EVENT("E");

    /**
     * The icon used when displaying this task type.
     */
    private final String icon;

    /**
     * Creates a task type.
     *
     * @param icon the display icon for the task type.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the display icon for this task type.
     *
     * @return the task type icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the task type represented by a stored type marker.
     *
     * @param typeMarker the marker read from storage.
     * @return the matching task type, or an empty optional when the marker is unsupported.
     */
    public static Optional<TaskType> fromIcon(String typeMarker) {
        for (TaskType taskType : values()) {
            if (taskType.icon.equals(typeMarker)) {
                return Optional.of(taskType);
            }
        }
        return Optional.empty();
    }
}
