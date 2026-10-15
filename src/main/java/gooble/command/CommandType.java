package gooble.command;

/**
 * Represents the type of command a user can enter in Gooble.
 */
public enum CommandType {
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    FIND,
    TAG,
    UNTAG,
    ADD,
    HELP,
    BYE,
    UNKNOWN;

    /** Returns the handler for commands migrated to the handler architecture. */
    public Command handler(String input) {
        return switch (this) {
            case LIST -> new ListCommand(input);
            case MARK -> new MarkCommand(input);
            case UNMARK -> new UnmarkCommand(input);
            case DELETE -> new DeleteCommand(input);
            case TODO -> new TodoCommand(input);
            case ADD -> new AddCommand(input);
            case HELP -> new HelpCommand(input);
            case DEADLINE -> new DeadlineCommand(input);
            case EVENT -> new EventCommand(input);
            case FIND -> new FindCommand(input);
            case TAG -> new TagCommand(input);
            case UNTAG -> new UntagCommand(input);
            case BYE -> new ByeCommand(input);
            default -> null;
        };
    }

    /**
     * Returns the CommandType matching the given command word.
     *
     * @param commandWord the first word of user input
     * @return the matching CommandType, or UNKNOWN if unrecognized
     */
    public static CommandType fromString(String commandWord) {
        if (commandWord == null || commandWord.isBlank()) {
            return UNKNOWN;
        }
        try {
            return CommandType.valueOf(commandWord.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
