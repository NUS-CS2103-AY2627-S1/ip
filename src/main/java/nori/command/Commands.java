package nori.command;

/**
 * Creates concrete commands from their recognized types and details.
 */
public final class Commands {
    /** Prevents instantiation of this command factory. */
    private Commands() {
    }

    /**
     * Creates the command represented by a recognized type.
     *
     * @param commandType the recognized command type.
     * @param details the text after the command keyword.
     * @return the corresponding executable command.
     */
    public static Command create(CommandType commandType, String details) {
        assert commandType != null : "Unrecognized input becomes a command through createUnknown instead.";
        assert details != null : "The parser always supplies the text after the keyword, empty at the least.";

        switch (commandType) {
            case TODO:
                return new TodoCommand(details);
            case DEADLINE:
                return new DeadlineCommand(details);
            case EVENT:
                return new EventCommand(details);
            case LIST:
                return new ListCommand(details);
            case HELP:
                return new HelpCommand();
            case ON:
                return new OnCommand(details);
            case SCHEDULE:
                return new ScheduleCommand(details);
            case FIND:
                return new FindCommand(details);
            case MARK:
                return new MarkCommand(details);
            case UNMARK:
                return new UnmarkCommand(details);
            case DELETE:
                return new DeleteCommand(details);
            case BYE:
                return new ExitCommand();
            default:
                assert false : "Every command type needs a command, but " + commandType + " has none.";
                return createRejected(CommandSuggestions.UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /**
     * Creates a command that reports unrecognized input, naming a likely correction.
     *
     * @param input the trimmed user input that names no command.
     * @return an unrecognized-command handler.
     */
    public static Command createUnknown(String input) {
        return new UnknownCommand(CommandSuggestions.explain(input));
    }

    /**
     * Creates a command that refuses input for a stated reason.
     *
     * @param message the explanation and correction to show the user.
     * @return a handler that reports that reason.
     */
    public static Command createRejected(String message) {
        assert message != null && !message.isEmpty() : "Refused input is always given a reason.";

        return new UnknownCommand(message);
    }
}
