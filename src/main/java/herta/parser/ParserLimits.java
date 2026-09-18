package herta.parser;

/**
 * Stores bounds and shared numeric validation rules for command parsers.
 */
final class ParserLimits {
    /** Maximum number of characters accepted in one command. */
    static final int MAX_COMMAND_LENGTH = 4_096;

    /** Maximum number of decimal digits accepted for a task-related number. */
    static final int MAX_NUMBER_LENGTH = 64;

    /** Maximum number of days accepted by the upcoming command. */
    static final int MAX_UPCOMING_DAYS = 4_000_000;

    private ParserLimits() {
        // Utility class; do not instantiate.
    }

    /** Checks whether input is a non-negative decimal within the supported length. */
    static boolean isBoundedDecimal(String input) {
        return input != null
                && input.matches("[0-9]+")
                && input.length() <= MAX_NUMBER_LENGTH;
    }
}
