package grug.task;

import java.util.List;
import java.util.stream.Stream;

/**
 * Represents the priorities of different tasks.
 */
public enum TaskPriority {
    OPTIONAL("OPT", 0),
    LOW("LOW", 1),
    MEDIUM("MED", 2),
    HIGH("HIG", 3),
    URGENT("URG", 4);

    public static final TaskPriority DEFAULT = MEDIUM;
    private String displayName;
    private int level;

    private TaskPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    /**
     * Converts the {@code displayName} to the appropriate {@link TaskPriority} enum
     * variant. See {@link TaskPriority} for all valid display names.
     *
     * @param displayName The case insensitive display name of the priority to
     *                    convert.
     * @return The appropriate {@link TaskPriority} converted from the display name.
     * @throws IllegalArgumentException If {@code displayName} is null or invalid.
     */
    public static TaskPriority createFromDisplayName(String displayName) {
        if (displayName == null) {
            throw new IllegalArgumentException("`displayName` cannot be null");
        }

        return switch (displayName.toUpperCase()) {
            case "OPT" -> TaskPriority.OPTIONAL;
            case "LOW" -> TaskPriority.LOW;
            case "MED" -> TaskPriority.MEDIUM;
            case "HIG" -> TaskPriority.HIGH;
            case "URG" -> TaskPriority.URGENT;
            default -> throw new IllegalArgumentException(
                    "Invalid priority name `%s`".formatted(displayName));
        };
    }

    public static List<String> getValidPriorityNames() {

        return Stream.of(TaskPriority.values())
                .map(priority -> priority.getDisplayName())
                .toList();
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}
