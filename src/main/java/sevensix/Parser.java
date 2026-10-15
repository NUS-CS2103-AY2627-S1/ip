package sevensix;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Recognizes command keywords and turns command text into the values the chatbot acts on.
 *
 * <p>Every rule about the shape of a command lives here, so the chatbot itself only has to
 * decide what to do once a command has been understood. The class is a collection of static
 * helpers because it holds no state of its own.
 */
public class Parser {
    /** Command keyword for ending the conversation. */
    public static final String COMMAND_BYE = "bye";
    /** Command keyword for listing every stored task. */
    public static final String COMMAND_LIST = "list";
    /** Command keyword for creating a to-do task. */
    public static final String COMMAND_TODO = "todo";
    /** Command keyword for creating a deadline task. */
    public static final String COMMAND_DEADLINE = "deadline";
    /** Command keyword for creating an event task. */
    public static final String COMMAND_EVENT = "event";
    /** Command keyword for marking a task as done. */
    public static final String COMMAND_MARK = "mark";
    /** Command keyword for marking a task as not done. */
    public static final String COMMAND_UNMARK = "unmark";
    /** Command keyword for deleting a task. */
    public static final String COMMAND_DELETE = "delete";
    /** Command keyword for searching task descriptions. */
    public static final String COMMAND_FIND = "find";
    /** Command keyword for undoing the most recent task-changing command. */
    public static final String COMMAND_UNDO = "undo";
    /** Command keyword for displaying usage instructions. */
    public static final String COMMAND_HELP = "help";

    /** Marker separating a deadline's description from its due time. */
    private static final String DEADLINE_BY_MARKER = "/by";
    /** Marker separating an event's description from its start. */
    private static final String EVENT_FROM_MARKER = "/from";
    /** Marker separating an event's start from its end. */
    private static final String EVENT_TO_MARKER = "/to";
    /** Recognizes parameter tokens without mistaking dates or paths for parameters. */
    private static final Pattern PARAMETER = Pattern.compile("(?<!\\S)/(?:by|from|to)(?!\\S)");

    /** Prevents instantiation of this helper class. */
    private Parser() {
    }

    /** Turns raw input into a command with typed arguments.
     *
     * @param input the raw command text.
     * @return the parsed command ready to execute.
     * @throws SevenSixException if its syntax or arguments are invalid.
     */
    public static Command parse(String input) throws SevenSixException {
        return parseNormalizedCommand(normalize(input));
    }

    /** Selects a command implementation after whitespace normalization.
     *
     * @param command the normalized input.
     * @return a command containing parsed task details, a number, or a keyword.
     * @throws SevenSixException if the command is unknown or malformed.
     */
    private static Command parseNormalizedCommand(String command) throws SevenSixException {
        return switch (parseCommandKeyword(command)) {
        case COMMAND_BYE -> new ByeCommand();
        case COMMAND_HELP -> new HelpCommand();
        case COMMAND_LIST -> new ListCommand();
        case COMMAND_UNDO -> new UndoCommand();
        case COMMAND_TODO -> new AddCommand(new Todo(parseTodoDescription(command)));
        case COMMAND_DEADLINE -> new AddCommand(parseDeadline(command));
        case COMMAND_EVENT -> new AddCommand(parseEvent(command));
        case COMMAND_MARK -> new StatusCommand(parseTaskNumber(command, COMMAND_MARK), true);
        case COMMAND_UNMARK -> new StatusCommand(parseTaskNumber(command, COMMAND_UNMARK), false);
        case COMMAND_DELETE -> new DeleteCommand(parseTaskNumber(command, COMMAND_DELETE));
        case COMMAND_FIND -> new FindCommand(parseFindKeyword(command));
        default -> throw new SevenSixException(Ui.getUnknownCommandMessage());
        };
    }

    /** Normalizes horizontal whitespace and rejects empty or unsafe input.
     *
     * @param command the raw command, possibly null.
     * @return the command with single spaces between words.
     * @throws SevenSixException if the command is empty or contains control characters.
     */
    public static String normalize(String command) throws SevenSixException {
        if (command == null || command.isBlank()) {
            throw new SevenSixException(Ui.EMPTY_COMMAND);
        }
        if (command.codePoints().anyMatch(Parser::isInvalidCommandCharacter)) {
            throw new SevenSixException(Ui.INVALID_CHARACTERS);
        }
        String normalizedCommand = command.replaceAll("\\h+", " ").strip();
        if (normalizedCommand.isEmpty()) {
            throw new SevenSixException(Ui.EMPTY_COMMAND);
        }
        return normalizedCommand;
    }

    /** Rejects control characters and Unicode line breaks before trimming can hide them.
     *
     * @param character the Unicode code point to inspect.
     * @return true for a line break or a control character other than a horizontal tab.
     */
    private static boolean isInvalidCommandCharacter(int character) {
        return (Character.isISOControl(character) && character != '\t')
                || Character.getType(character) == Character.LINE_SEPARATOR
                || Character.getType(character) == Character.PARAGRAPH_SEPARATOR;
    }

    /** Extracts the routing keyword and validates commands that accept no arguments.
     *
     * @param command the normalized command.
     * @return the command keyword.
     * @throws SevenSixException if an argument-free command includes extra text.
     */
    public static String parseCommandKeyword(String command) throws SevenSixException {
        String keyword = command.split(" ", 2)[0];
        if (List.of(COMMAND_LIST, COMMAND_UNDO, COMMAND_BYE, COMMAND_HELP).contains(keyword)
                && !command.equals(keyword)) {
            throw new SevenSixException(Ui.UNEXPECTED_ARGUMENTS);
        }
        return keyword;
    }

    /** Recognizes an exit command using the same whitespace rules as command processing.
     *
     * @param command the raw input.
     * @return true only for a valid, argument-free bye command.
     */
    public static boolean isExitCommand(String command) {
        try {
            return normalize(command).equals(COMMAND_BYE);
        } catch (SevenSixException exception) {
            return false;
        }
    }

    /**
     * Checks whether a command is exactly a keyword or starts with that keyword and a space.
     *
     * @param command the normalized command to inspect.
     * @param commandKeyword the command keyword to match.
     * @return {@code true} when the command uses the supplied keyword.
     */
    public static boolean isCommand(String command, String commandKeyword) {
        return command.equals(commandKeyword) || command.startsWith(commandKeyword + " ");
    }

    /**
     * Parses the description of a to-do command.
     *
     * @param command the complete to-do command.
     * @return the to-do description.
     * @throws SevenSixException if the to-do description is empty.
     */
    public static String parseTodoDescription(String command) throws SevenSixException {
        assert isCommand(command, COMMAND_TODO) : "getResponse routes only todo commands here, so the cut is safe";
        String description = removeKeyword(command, COMMAND_TODO);
        if (description.isBlank()) {
            throw new SevenSixException(Ui.MISSING_TODO_DESCRIPTION);
        }
        return description;
    }

    /**
     * Parses a deadline command into a deadline task.
     *
     * @param command the complete deadline command.
     * @return the parsed deadline task.
     * @throws SevenSixException if the deadline format or its details are invalid.
     */
    public static Deadline parseDeadline(String command) throws SevenSixException {
        assert isCommand(command, COMMAND_DEADLINE)
                : "getResponse routes only deadline commands here, so the cut is safe";
        String[] fields = parseDateParameters(command, COMMAND_DEADLINE, List.of(DEADLINE_BY_MARKER));
        String description = fields[0].strip();
        String by = fields[1].strip();
        if (description.isBlank() || by.isBlank()) {
            throw new SevenSixException(Ui.MISSING_DEADLINE_DETAILS);
        }
        DateTimeParser.ParsedDateTime parsedBy = DateTimeParser.parse(by);
        return new Deadline(description, parsedBy.getDate(), parsedBy.getTime());
    }

    /**
     * Parses an event command into an event task.
     *
     * @param command the complete event command.
     * @return the parsed event task.
     * @throws SevenSixException if the event format or its details are invalid.
     */
    public static Event parseEvent(String command) throws SevenSixException {
        assert isCommand(command, COMMAND_EVENT) : "getResponse routes only event commands here, so the cut is safe";
        String[] fields = parseDateParameters(command, COMMAND_EVENT, List.of(EVENT_FROM_MARKER, EVENT_TO_MARKER));
        String description = fields[0].strip();
        String from = fields[1].strip();
        String to = fields[2].strip();
        if (description.isBlank() || from.isBlank() || to.isBlank()) {
            throw new SevenSixException(Ui.MISSING_EVENT_DETAILS);
        }
        DateTimeParser.ParsedDateTime parsedFrom = DateTimeParser.parse(from);
        DateTimeParser.ParsedDateTime parsedTo = DateTimeParser.parse(to);
        Event event = new Event(description, parsedFrom.getDate(), parsedFrom.getTime(),
                parsedTo.getDate(), parsedTo.getTime());
        TaskValidation.validate(event);
        return event;
    }

    /** Splits date parameters only after checking their count and order.
     *
     * @param command the complete command.
     * @param keyword the command keyword.
     * @param expectedMarkers the permitted markers in their required order.
     * @return description and date fields, including empty fields for validation.
     * @throws SevenSixException if markers are missing, repeated, or misplaced.
     */
    private static String[] parseDateParameters(String command, String keyword, List<String> expectedMarkers)
            throws SevenSixException {
        String details = removeKeyword(normalize(command), keyword);
        List<String> markers = PARAMETER.matcher(details).results().map(result -> result.group()).toList();
        if (!markers.equals(expectedMarkers)) {
            throw new SevenSixException(Ui.INVALID_PARAMETERS);
        }
        return PARAMETER.split(details, -1);
    }

    /**
     * Parses the search keyword of a find command.
     *
     * @param command the complete find command.
     * @return the keyword to search for.
     * @throws SevenSixException if the search keyword is empty.
     */
    public static String parseFindKeyword(String command) throws SevenSixException {
        assert isCommand(command, COMMAND_FIND) : "getResponse routes only find commands here, so the cut is safe";
        String keyword = removeKeyword(command, COMMAND_FIND);
        if (keyword.isBlank()) {
            throw new SevenSixException(Ui.MISSING_FIND_KEYWORD);
        }
        return keyword;
    }

    /**
     * Parses a one-based task number from a command.
     *
     * @param command the command containing a task number.
     * @param commandKeyword the keyword at the start of the command.
     * @return the parsed task number.
     * @throws SevenSixException if the task number is not an integer.
     */
    public static int parseTaskNumber(String command, String commandKeyword) throws SevenSixException {
        assert isCommand(command, commandKeyword) : "Each caller passes the keyword that its command starts with";
        String argument = removeKeyword(command, commandKeyword);
        try {
            if (!argument.matches("[0-9]+")) {
                throw new NumberFormatException();
            }
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new SevenSixException(Ui.INVALID_TASK_NUMBER);
        }
    }

    /**
     * Removes a command's keyword, leaving the command details.
     *
     * @param command the complete command.
     * @param commandKeyword the keyword at the start of the command.
     * @return the command details without surrounding spaces.
     */
    private static String removeKeyword(String command, String commandKeyword) {
        return command.substring(commandKeyword.length()).trim();
    }
}
