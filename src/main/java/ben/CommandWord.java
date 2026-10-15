package ben;

/**
 * The recognized command keywords, used to dispatch each line of input
 * via a {@code switch} instead of a chain of string checks.
 * <p>
 * A-Enums: introduced to replace repeated {@code .equals}/{@code .startsWith}
 * calls with a single type-checked switch.
 */
enum CommandWord {
    LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, TAG, BYE, UNKNOWN;

    /** Maps the first word of a line of input to a {@link CommandWord}. */
    static CommandWord fromString(String word) {
        try {
            return CommandWord.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
