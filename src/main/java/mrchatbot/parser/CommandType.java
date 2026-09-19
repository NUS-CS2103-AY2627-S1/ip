package mrchatbot.parser;

/**
 * Represents the command words accepted by the chatbot.
 */
public enum CommandType {
    TODO("todo"),
    RECURRING("recurring"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    FIND("find"),
    BYE("bye"),
    HELP("help"),
    UNKNOWN("");

    final String word;

    /**
     * Creates a command type for the given command word.
     */
    CommandType(String word) {
        this.word = word;
    }

    /**
     * Returns true if the lower-case input belongs to this command type.
     */
    boolean matches(String lowerCaseInput) {
        if (requiresExactMatch()) {
            return lowerCaseInput.equals(word);
        }
        return lowerCaseInput.equals(word) || lowerCaseInput.startsWith(word + " ");
    }

    /**
     * Returns the command word followed by one space for prefix parsing.
     */
    String withTrailingSpace() {
        return word + " ";
    }

    /**
     * Returns true if this command must not accept trailing arguments.
     */
    private boolean requiresExactMatch() {
        return this == LIST || this == HELP || this == BYE;
    }

    /**
     * Finds the command type that matches the user's raw input.
     */
    static CommandType from(String input) {
        String lowerCaseInput = input.toLowerCase();
        for (CommandType commandType : values()) {
            if (commandType != UNKNOWN && commandType.matches(lowerCaseInput)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
