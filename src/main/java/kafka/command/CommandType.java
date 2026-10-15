package kafka.command;

/**
 * Lists every supported command and {@link #UNKNOWN} for unrecognized input.
 */
public enum CommandType {
    /** Adds a task with no date or time attached. */
    TODO("todo", true),
    /** Adds a task that needs to be done by a date or time. */
    DEADLINE("deadline", true),
    /** Adds a task happening between start and end values. */
    EVENT("event", true),
    /** Marks a numbered task as completed. */
    MARK("mark", true),
    /** Marks a numbered task as incomplete. */
    UNMARK("unmark", true),
    /** Removes a numbered task from the list. */
    DELETE("delete", true),
    /** Replaces the name of a numbered task. */
    RENAME("rename", true),
    /** Reschedules a numbered deadline or event. */
    SNOOZE("snooze", true),
    /** Finds tasks containing a supplied keyword. */
    FIND("find", true),
    /** Displays all tasks in list order. */
    LIST("list", false),
    /** Ends the current Kafka session. */
    BYE("bye", false),
    /** Represents input that matches none of Kafka's commands. */
    UNKNOWN("", false);

    private final String keyword;
    private final boolean canAcceptArguments;

    CommandType(String keyword, boolean canAcceptArguments) {
        this.keyword = keyword;
        this.canAcceptArguments = canAcceptArguments;
    }

    /**
     * Returns the keyword used to invoke this command.
     *
     * @return This command's lowercase keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Reports whether this command edits tasks and therefore needs a successful save.
     */
    public boolean isTaskModification() {
        return switch (this) {
            case TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, RENAME, SNOOZE -> true;
            default -> false;
        };
    }

    /**
     * Identifies the command at the start of the user's input.
     * Argument-free commands must appear alone, while the others may be followed
     * by whitespace and their details. Surrounding whitespace is ignored.
     *
     * @param input Complete command entered by the user.
     * @return The matching command, or {@link #UNKNOWN} when nothing matches.
     */
    public static CommandType parseInput(String input) {
        String normalizedInput = input.strip();
        for (CommandType command : values()) {
            if (command == UNKNOWN) {
                continue;
            }
            boolean isKeywordMatch = normalizedInput.equals(command.keyword);
            if (isKeywordMatch || command.hasMatchingArguments(normalizedInput)) {
                return command;
            }
        }
        return UNKNOWN;
    }

    /**
     * Checks that arguments follow this command's keyword with a whitespace separator.
     */
    private boolean hasMatchingArguments(String input) {
        if (!canAcceptArguments || !input.startsWith(keyword) || input.length() <= keyword.length()) {
            return false;
        }
        return Character.isWhitespace(input.charAt(keyword.length()));
    }
}
