package altair.command;

import java.util.Locale;

/**
 * The commands that Altair understands at the start of a user input line.
 */
public enum CommandType {
    /** Ends the application. */
    BYE("bye"),

    /** Displays all tasks. */
    LIST("list"),

    /** Displays the tasks whose description contains a keyword. */
    FIND("find"),

    /** Marks a task as completed. */
    MARK("mark"),

    /** Marks a task as not completed. */
    UNMARK("unmark"),

    /** Removes a task. */
    DELETE("delete"),

    /** Creates a basic task. */
    TODO("todo"),

    /** Creates a deadline task. */
    DEADLINE("deadline"),

    /** Creates an event task. */
    EVENT("event"),

    /** Represents an input that does not begin with a known command. */
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Identifies the command at the start of an input line.
     *
     * @param command the complete input line.
     * @return the matching command type, or {@link #UNKNOWN}.
     */
    public static CommandType from(String command) {
        String trimmed = command == null ? "" : command.trim().toLowerCase(Locale.ROOT);
        for (CommandType type : values()) {
            boolean hasCommandWord = !type.keyword.isEmpty() && trimmed.startsWith(type.keyword);
            boolean endsAfterCommand = trimmed.length() == type.keyword.length();
            boolean hasWhitespaceAfterCommand = hasCommandWord
                    && trimmed.length() > type.keyword.length()
                    && Character.isWhitespace(trimmed.charAt(type.keyword.length()));
            if (hasCommandWord && (endsAfterCommand || hasWhitespaceAfterCommand)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
