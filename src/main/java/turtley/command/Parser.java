package turtley.command;

/**
 * Converts raw user input into executable commands.
 */
public class Parser {

    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String FILTER_COMMAND = "filter";
    private static final String HELP_COMMAND = "help";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TAG_COMMAND = "tag";
    private static final String UNTAG_COMMAND = "untag";
    private static final String DELETE_COMMAND = "delete";
    private static final String TIMECHECK_COMMAND = "timecheck";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";

    private Parser() {
        // Utility class; do not create instances.
    }

    /**
     * Parses one line of user input.
     *
     * @param input the raw user input.
     * @return the executable command represented by the input, or an unknown command for malformed input.
     */
    public static Command parse(String input) {
        if (input == null || input.isEmpty()) {
            return new EmptyCommand();
        }
        if (hasInvalidWhitespace(input)) {
            return new UnknownCommand();
        }
        if (input.equals(EXIT_COMMAND)) {
            return new ExitCommand();
        }
        if (input.equals(LIST_COMMAND)) {
            return new ListCommand();
        }
        if (isCommand(input, FIND_COMMAND)) {
            return new FindCommand(extractArgument(input, FIND_COMMAND));
        }
        if (isCommand(input, FILTER_COMMAND)) {
            return new FilterCommand(extractArgument(input, FILTER_COMMAND));
        }
        if (input.equals(HELP_COMMAND)) {
            return new HelpCommand();
        }
        if (hasArgument(input, MARK_COMMAND)) {
            return new MarkCommand(extractArgument(input, MARK_COMMAND));
        }
        if (hasArgument(input, UNMARK_COMMAND)) {
            return new UnmarkCommand(extractArgument(input, UNMARK_COMMAND));
        }
        if (isCommand(input, TAG_COMMAND)) {
            return new TagCommand(extractArgument(input, TAG_COMMAND));
        }
        if (isCommand(input, UNTAG_COMMAND)) {
            return new UntagCommand(extractArgument(input, UNTAG_COMMAND));
        }
        if (isCommand(input, DELETE_COMMAND)) {
            return new DeleteCommand(extractArgument(input, DELETE_COMMAND));
        }
        if (isCommand(input, TIMECHECK_COMMAND)) {
            return new TimecheckCommand(extractArgument(input, TIMECHECK_COMMAND));
        }
        if (isCommand(input, TODO_COMMAND)) {
            return new TodoCommand(extractArgument(input, TODO_COMMAND));
        }
        if (hasArgument(input, DEADLINE_COMMAND)) {
            return new DeadlineCommand(extractArgument(input, DEADLINE_COMMAND));
        }
        if (hasArgument(input, EVENT_COMMAND)) {
            return new EventCommand(extractArgument(input, EVENT_COMMAND));
        }
        return new UnknownCommand();
    }

    /**
     * Detects whitespace that makes command token boundaries ambiguous.
     *
     * @param input the raw user input.
     * @return {@code true} if the input has outer, repeated, or non-space whitespace.
     */
    private static boolean hasInvalidWhitespace(String input) {
        return !input.equals(input.strip())
                || input.contains("  ")
                || input.codePoints().anyMatch(character -> Character.isWhitespace(character)
                        && character != ' ');
    }

    /**
     * Returns whether the input is a command with an optional argument.
     *
     * @param input the raw user input.
     * @param command the command word.
     * @return {@code true} when the input is the command or starts with the command and a space.
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || hasArgument(input, command);
    }

    /**
     * Returns whether the input starts with the command and a separating space.
     *
     * @param input the raw user input.
     * @param command the command word.
     * @return {@code true} when the input has an argument after the command.
     */
    private static boolean hasArgument(String input, String command) {
        return input.startsWith(command + " ");
    }

    /**
     * Returns the trimmed argument after a command word.
     *
     * @param input the raw user input.
     * @param command the command word.
     * @return the command argument, or an empty string when no argument is supplied.
     */
    private static String extractArgument(String input, String command) {
        return input.substring(command.length()).trim();
    }
}
