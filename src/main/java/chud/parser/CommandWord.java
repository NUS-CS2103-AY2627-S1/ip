package chud.parser;

/**
 * The set of command words the chatbot understands. UNKNOWN represents any
 * other word the user might type.
 */
public enum CommandWord {
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    ON,
    FIND,
    BYE,
    UNKNOWN;

    /**
     * Matches a typed command word (exact, lowercase, e.g. "todo") to its CommandWord,
     * or UNKNOWN if it doesn't match any recognized command word.
     */
    public static CommandWord fromCommandWord(String commandWord) {
        for (CommandWord word : values()) {
            if (word != UNKNOWN && word.name().toLowerCase().equals(commandWord)) {
                return word;
            }
        }
        return UNKNOWN;
    }
}
