package nyonbot.command;

import java.util.List;

/**
 * Stores the canonical name and aliases for each supported command.
 */
public enum CommandType {
    EXIT("bye", "exit"),
    ECHO("echo", "cat"),
    NYON("nyon"),
    LIST("list", "ls"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete", "del", "rm"),
    FIND("find", "grep"),
    HELP("help", "?", "man"),
    ON("on"),
    UNKNOWN("unknown");

    private final String mainKeyword;
    private final List<String> keywords;

    CommandType(String... keywords) {
        this.mainKeyword = keywords[0];
        this.keywords = List.of(keywords);
    }

    /**
     * Returns the primary user-facing command name.
     *
     * @return canonical command keyword.
     */
    public String keyword() {
        return mainKeyword;
    }

    /**
     * Returns the canonical command keyword and its aliases.
     *
     * @return immutable command keyword list.
     */
    public List<String> keywords() {
        return keywords;
    }

    private boolean matches(String input) {
        return keywords.stream().anyMatch(keyword -> keyword.equalsIgnoreCase(input));
    }

    /**
     * Converts a command keyword or alias to its command type.
     *
     * @param keyword command keyword to parse.
     * @return corresponding command type, or {@link #UNKNOWN} when not recognized.
     */
    public static CommandType toCommandType(String keyword) {
        for (CommandType type : values()) {
            if (type.matches(keyword)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
