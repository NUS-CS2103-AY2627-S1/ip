package bean.task;

import java.util.Locale;

import bean.exception.InvalidSyntaxException;

/** Represents a task's priority and its numeric storage level. */
public enum Priority {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    private final int level;

    Priority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Parses a priority name without regard to case.
     *
     * @param value The priority name to parse.
     * @return The matching priority.
     * @throws InvalidSyntaxException If the name is not high, medium, or low.
     */
    public static Priority fromString(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "high" -> Priority.HIGH;
            case "medium" -> Priority.MEDIUM;
            case "low" -> Priority.LOW;
            default -> throw new InvalidSyntaxException(
                    "Uh Oh! Invalid Syntax for adding using priority!\n\n"
                    + "Usage: {command} /priority \"{HIGH | MEDIUM | LOW}\"", value);
        };
    }
}
