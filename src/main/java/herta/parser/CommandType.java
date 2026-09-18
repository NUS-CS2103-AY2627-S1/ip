package herta.parser;

import java.util.Arrays;

/**
 * Represents the commands supported by Herta.
 */
public enum CommandType {
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    LIST("list", false),
    FIND("find", true),
    FILTER("filter", true),
    UPCOMING("upcoming", true),
    SORT("sort", true),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    ARCHIVE("archive", true),
    ARCHIVED("archived", true),
    RESTORE("restore", true),
    BYE("bye", false),
    UNKNOWN("", false);

    private final String keyword;
    private final boolean canAcceptArguments;

    /**
     * Creates a command type.
     *
     * @param keyword the text used to identify the command.
     * @param canAcceptArguments whether the command may be followed by arguments.
     */
    CommandType(String keyword, boolean canAcceptArguments) {
        this.keyword = keyword;
        this.canAcceptArguments = canAcceptArguments;
    }

    /**
     * Identifies the command represented by the user's input.
     *
     * @param input the complete user input.
     * @return the corresponding command type, or {@link #UNKNOWN}.
     */
    public static CommandType fromInput(String input) {
        if (input == null) {
            return UNKNOWN;
        }
        String trimmedInput = input.trim();
        return Arrays.stream(values())
                .filter(command -> command != UNKNOWN)
                .filter(command -> trimmedInput.equals(command.keyword)
                        || (command.canAcceptArguments && hasArgumentSeparator(trimmedInput, command)))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /** Checks for a supported separator without accepting command lookalikes. */
    private static boolean hasArgumentSeparator(String input, CommandType command) {
        return input.startsWith(command.keyword)
                && input.length() > command.keyword.length()
                && CommandTokenizer.isHorizontalWhitespace(input.charAt(command.keyword.length()));
    }

    /**
     * Returns the keyword used to recognize this command.
     *
     * @return the command keyword.
     */
    String getKeyword() {
        return keyword;
    }

    /** Returns whether this command may be followed by arguments. */
    boolean canAcceptArguments() {
        return canAcceptArguments;
    }

    /**
     * Extracts the arguments after this command type's keyword.
     *
     * @param input the complete command input.
     * @return the trimmed command arguments.
     */
    String extractArguments(String input) {
        String trimmedInput = input.trim();
        return trimmedInput.substring(keyword.length()).trim();
    }
}
