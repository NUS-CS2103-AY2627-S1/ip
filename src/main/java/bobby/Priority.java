package bobby;

import java.util.Locale;

/** Represents the priority assigned to a task. */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW,
    NONE;

    /**
     * Parses a priority name or numeric alias entered by a user.
     *
     * @param input priority name or numeric alias
     * @return parsed priority
     * @throws BobbyException if the input is not a supported priority
     */
    public static Priority fromUserInput(String input) throws BobbyException {
        assert input != null : "Priority input must not be null";
        switch (input.toLowerCase(Locale.ROOT)) {
            case "high":
            case "1":
                return HIGH;
            case "medium":
            case "2":
                return MEDIUM;
            case "low":
            case "3":
                return LOW;
            case "none":
                return NONE;
            default:
                throw new BobbyException("Error! The priority level must be high, medium, "
                        + "low, none, 1, 2, or 3.");
        }
    }
}
