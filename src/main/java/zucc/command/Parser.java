package zucc.command;

import java.util.Arrays;
import java.util.Iterator;

import zucc.ZuccException;

/**
 * Recognizes raw user input and populates the corresponding concrete command.
 */
final class Parser {
    /** Message used when the first word does not identify a supported command. */
    private static final String UNKNOWN_COMMAND_ERROR =
            "Zucc's algorithm doesn't recognize that command. "
                    + "Try todo, deadline, event, list, find, on, mark, unmark, delete, or bye.";

    /** Prevents creation of a utility class that contains only parsing operations. */
    private Parser() {
    }

    /**
     * Parses a complete input line directly into a concrete command.
     * Every space-delimited token beginning with a slash starts an option, whose
     * value continues until the next option separator. Empty tokens preserve
     * repeated spaces inside arguments and option values.
     *
     * @param input complete line entered by the user.
     * @return parsed command.
     * @throws ZuccException if the command is unknown or an option is unsupported
     *         or duplicated.
     */
    static Command parse(String input) throws ZuccException {
        String normalizedInput = input.strip();
        Iterator<String> tokens = Arrays.asList(normalizedInput.split(" ", -1)).iterator();
        Command command = createCommand(tokens.next());
        populateCommand(command, tokens);
        return command;
    }

    /**
     * Populates a command's argument and named options from its remaining tokens.
     *
     * @param command command to populate.
     * @param tokens tokens that follow the command keyword.
     * @throws ZuccException if an option is unsupported or duplicated.
     */
    private static void populateCommand(Command command, Iterator<String> tokens)
            throws ZuccException {
        String currentOption = null;
        StringBuilder currentValue = new StringBuilder();
        boolean hasValueTokens = false;

        while (tokens.hasNext()) {
            String token = tokens.next();
            if (isOption(token)) {
                addValue(command, currentOption, currentValue);
                currentOption = token;
                currentValue.setLength(0);
                hasValueTokens = false;
                continue;
            }

            if (hasValueTokens) {
                currentValue.append(' ');
            }
            currentValue.append(token);
            hasValueTokens = true;
        }

        addValue(command, currentOption, currentValue);
    }

    /**
     * Reports whether a token starts a named command option.
     *
     * @param token token to inspect.
     * @return {@code true} if the token contains a slash followed by an option name.
     */
    private static boolean isOption(String token) {
        return token.startsWith("/") && token.length() > 1;
    }

    /**
     * Adds a completed value as either the main argument or a named option.
     *
     * @param command command to populate.
     * @param option option associated with the value, or {@code null} for the main argument.
     * @param value accumulated raw value.
     * @throws ZuccException if the option is unsupported or duplicated.
     */
    private static void addValue(Command command, String option, StringBuilder value)
            throws ZuccException {
        String completedValue = value.toString().strip();
        if (option == null) {
            command.setArgument(completedValue);
        } else {
            command.addOption(option, completedValue);
        }
    }

    /**
     * Creates the concrete command associated with a recognized keyword.
     *
     * @param keyword first word of the input line.
     * @return empty command ready to receive its raw argument and options.
     * @throws ZuccException if the keyword does not identify a supported command.
     */
    private static Command createCommand(String keyword) throws ZuccException {
        return switch (keyword) {
            case "todo" -> new TodoCommand();
            case "deadline" -> new DeadlineCommand();
            case "event" -> new EventCommand();
            case "list" -> new ListCommand();
            case "find" -> new FindCommand();
            case "on" -> new OnCommand();
            case "mark" -> new MarkCommand();
            case "unmark" -> new UnmarkCommand();
            case "delete" -> new DeleteCommand();
            case "bye" -> new ExitCommand();
            default -> throw new ZuccException(UNKNOWN_COMMAND_ERROR);
        };
    }
}
