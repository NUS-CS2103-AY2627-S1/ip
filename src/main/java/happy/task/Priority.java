package happy.task;

/**
 * Represents the priority level of a task.
 */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW,
    NONE;

    /**
     * Parses a string representation into a Priority enum.
     *
     * @param input Raw priority string.
     * @return Corresponding Priority enum.
     */
    public static Priority parse(String input) {
        if (input == null) {
            return NONE;
        }

        //already handles lower case characters
        switch (input.trim().toUpperCase()) {
        case "HIGH":
        case "H":
            return HIGH;
        case "MEDIUM":
        case "MED":
        case "M":
            return MEDIUM;
        case "LOW":
        case "L":
            return LOW;
        default:
            return NONE;
        }
    }
}
