package jaylen;

/** The finite set of task categories supported by Jaylen. */
public enum TaskType {
    /** A task without a date or time. */
    TODO("[T]"),
    /** A task that must be completed by a date or time. */
    DEADLINE("[D]"),
    /** A task that happens between a start and end time. */
    EVENT("[E]"),
    /** A generic task retained for compatibility with the original model. */
    GENERIC("");

    private final String displayIcon;

    TaskType(String displayIcon) {
        this.displayIcon = displayIcon;
    }

    /**
     * Returns the marker printed before a task's status icon.
     *
     * @return this type's display marker
     */
    public String getDisplayIcon() {
        return displayIcon;
    }

    /**
     * Returns the compact code used in the save file.
     *
     * @return this type's save-file code
     */
    public String getCode() {
        return name().substring(0, 1);
    }

    /**
     * Converts a save-file code into a task type.
     *
     * @param code the one-letter task type code
     * @return the matching task type
     * @throws JaylenException if the code is unknown
     */
    public static TaskType fromCode(String code) {
        return switch (code) {
            case "T" -> TODO;
            case "D" -> DEADLINE;
            case "E" -> EVENT;
            default -> throw new JaylenException("The save file contains an unknown task type.");
        };
    }
}
