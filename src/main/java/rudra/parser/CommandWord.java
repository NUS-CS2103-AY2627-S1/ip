package rudra.parser;

import java.util.Optional;

/**
 * Identifies the supported top-level command words.
 */
public enum CommandWord {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    FIND("find"),
    SORT("sort"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event");

    private final String keyword;

    /**
     * Creates a command word enum constant for the given user input keyword.
     *
     * @param keyword Text the user types to trigger the command.
     */
    CommandWord(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Attempts to resolve the first word of user input to a supported command.
     *
     * @param rawKeyword First token from the user's command.
     * @return Matching command when supported, otherwise an empty result.
     */
    public static Optional<CommandWord> from(String rawKeyword) {
        for (CommandWord commandWord : values()) {
            if (commandWord.keyword.equals(rawKeyword)) {
                return Optional.of(commandWord);
            }
        }
        return Optional.empty();
    }
}
