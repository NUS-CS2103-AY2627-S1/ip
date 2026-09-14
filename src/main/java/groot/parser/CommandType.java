package groot.parser;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a command that Groot can process.
 */
public enum CommandType {
    /** Ends the current Groot session. */
    BYE(false, "bye"),
    /** Displays all tasks. */
    LIST(false, "list"),
    /** Displays deadlines by date without changing the main task order. */
    SORT(true, "sort"),
    /** Displays tasks whose descriptions contain a keyword. */
    FIND(true, "find"),
    /** Marks a selected task as completed. */
    MARK(true, "mark"),
    /** Marks a selected task as incomplete. */
    UNMARK(true, "unmark"),
    /** Removes a selected task. */
    DELETE(true, "delete"),
    /** Adds a task without a date or time. */
    TODO(true, "todo"),
    /** Adds a task with a completion date. */
    DEADLINE(true, "deadline"),
    /** Adds a task with a start and end. */
    EVENT(true, "event"),
    /** Displays the available commands and their syntax. */
    HELP(false, "help", "--help", "-h"),
    /** Represents input that does not match a supported command. */
    UNKNOWN(false, "");

    private final boolean canAcceptArguments;
    private final List<String> keywords;

    /**
     * Creates a command type with a canonical keyword and optional aliases.
     *
     * @param canAcceptArguments Whether text may follow the command keyword.
     * @param keywords Canonical keyword followed by any aliases.
     */
    CommandType(boolean canAcceptArguments, String... keywords) {
        this.canAcceptArguments = canAcceptArguments;
        this.keywords = List.of(keywords);
    }

    /**
     * Returns the word used to invoke this command.
     *
     * @return Command keyword.
     */
    public String getKeyword() {
        return keywords.get(0);
    }

    /**
     * Identifies the type of a complete command entered by the user.
     *
     * @param command Trimmed command text.
     * @return Matching command type, or {@link #UNKNOWN} if none matches.
     */
    public static CommandType from(String command) {
        return Arrays.stream(values())
                .filter(type -> type.matches(command))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /**
     * Returns whether the command uses this type's canonical keyword or an alias.
     */
    private boolean matches(String command) {
        if (this == SORT) {
            // Only sort accepts case variations and tab separators; preserve other commands' syntax.
            String keyword = command.trim().split("[ \\t]+", 2)[0];
            return keyword.equalsIgnoreCase(getKeyword());
        }
        return keywords.stream()
                .anyMatch(keyword -> command.equals(keyword)
                        || (canAcceptArguments && command.startsWith(keyword + " ")));
    }
}
