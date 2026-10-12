package echo.command;

import java.time.format.DateTimeParseException;
import java.util.Locale;

import echo.EchoException;
import echo.task.SortCriteria;
import echo.util.DateTimeParser;

/**
 * Converts raw user input into validated {@link Command} objects.
 */
public class CommandParser {
    /** Usage guidance for todo commands. */
    private static final String TODO_FORMAT = "Try: todo <description>.";

    /** Placeholder text for a date and optional time in usage guidance. */

    private static final String DATE_TIME_ARGUMENT = "<" + DateTimeParser.DATE_FORMAT + "> ["
            + DateTimeParser.TIME_FORMAT + "]";

    /** Usage guidance for deadline commands. */
    private static final String DEADLINE_FORMAT = "Try: deadline <description> /by "
            + DATE_TIME_ARGUMENT + ".";

    /** Usage guidance for event commands. */
    private static final String EVENT_FORMAT = "Try: event <description> /from "
            + DATE_TIME_ARGUMENT + " /to " + DATE_TIME_ARGUMENT + ".";

    /**
     * Creates a parser for E.C.H.O. commands.
     */
    public CommandParser() {
    }

    /**
     * Parses one line of user input into an executable command.
     *
     * @param input raw input String line entered by the user.
     * @return parsed and validated {@link Command} object.
     * @throws EchoException if the input is not a supported command.
     */
    public Command parse(String input) throws EchoException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new EchoException("Please enter a command. Try 'list' to view your tasks.");
        }

        String[] commandParts = trimmedInput.split("\\s+");
        String commandName = commandParts[0].toLowerCase(Locale.ROOT);

        return switch (commandName) {
            case "help" -> parseNoArgumentCommand(commandParts, Command.Type.HELP,
                    "'help' does not take any arguments. Type 'help' to view list of operations.");
            case "list" -> parseNoArgumentCommand(commandParts, Command.Type.LIST,
                    "'list' does not take any arguments. Type 'list' to view all your tasks.");
            case "todo" -> parseTodoCommand(trimmedInput);
            case "deadline" -> parseDeadlineCommand(trimmedInput);
            case "event" -> parseEventCommand(trimmedInput);
            case "mark" -> parseTaskNumberCommand(commandParts, Command.Type.MARK);
            case "unmark" -> parseTaskNumberCommand(commandParts, Command.Type.UNMARK);
            case "delete" -> parseTaskNumberCommand(commandParts, Command.Type.DELETE);
            case "find" -> parseFindCommand(commandParts);
            case "sort" -> parseSortCommand(commandParts);
            case "bye" -> parseExitCommand(commandParts);
            default -> throw new EchoException("I do not recognise '" + commandName
                    + "'. Try 'help' to see the available commands.");
        };
    }

    /**
     * Parses a sort command with optional sorting criteria.
     *
     * @param commandParts split tokens of the user input.
     * @return parsed {@link SortCommand} object.
     * @throws EchoException if unexpected or invalid sort criteria are provided.
     */
    private Command parseSortCommand(String[] commandParts) throws EchoException {
        if (commandParts.length == 1) {
            return new SortCommand(SortCriteria.DATE);
        }
        if (commandParts.length == 2) {
            String criterion = commandParts[1].toLowerCase(Locale.ROOT);
            return switch (criterion) {
                case "date" -> new SortCommand(SortCriteria.DATE);
                case "name", "description" -> new SortCommand(SortCriteria.NAME);
                default -> throw new EchoException("I do not recognise '" + commandParts[1]
                        + "' as a sort criterion. Try: 'sort', 'sort date', or 'sort name'.");
            };
        }
        throw new EchoException("Too many arguments for sort. Try: 'sort [date|name]'.");
    }

    /** Parses a find command. */
    private Command parseFindCommand(String[] commandParts) throws EchoException {
        if (commandParts.length != 2) {
            throw new EchoException("Please provide exactly one keyword. Use 'find <keyword>'.");
        }

        return new FindCommand(commandParts[1]);
    }

    /**
     * Parses a command that accepts no additional arguments.
     *
     * @param commandParts split tokens of the user input.
     * @param type the type of zero-argument command to instantiate.
     * @param errorMessage the error message to present if extra arguments are detected.
     * @return parsed and validated {@link Command} object.
     * @throws EchoException if unexpected arguments are provided.
     */
    private Command parseNoArgumentCommand(String[] commandParts, Command.Type type,
            String errorMessage) throws EchoException {
        if (commandParts.length != 1) {
            throw new EchoException(errorMessage);
        }

        return switch (type) {
            case HELP -> new HelpCommand();
            case LIST -> new ListCommand();
            default -> throw new IllegalArgumentException("Unsupported no-argument command: " + type);
        };
    }

    /**
     * Parses the command that terminates the session.
     *
     * @param commandParts split tokens of the user input.
     * @return {@link ExitCommand} object.
     * @throws EchoException if unexpected arguments are provided.
     */
    private Command parseExitCommand(String[] commandParts) throws EchoException {
        if (commandParts.length != 1) {
            throw new EchoException("'bye' does not take any arguments. "
                    + "Type 'bye' when you are ready to disconnect.");
        }

        return new ExitCommand();
    }

    /**
     * Parses a command that targets a specific task by its index.
     *
     * @param commandParts split tokens of the user input.
     * @param type the type of task-index command.
     * @return parsed task-index {@link Command} object.
     * @throws EchoException if unexpected arguments are provided.
     */
    private Command parseTaskNumberCommand(String[] commandParts, Command.Type type)
            throws EchoException {
        String commandName = switch (type) {
            case MARK -> "mark";
            case UNMARK -> "unmark";
            case DELETE -> "delete";
            default -> throw new IllegalArgumentException("Unsupported task command: " + type);
        };
        if (commandParts.length != 2) {
            throw new EchoException("Please provide exactly one task number. Use '"
                    + commandName + " <number>'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(commandParts[1]);
        } catch (NumberFormatException exception) {
            throw new EchoException("'" + commandParts[1] + "' is not a valid task number. "
                    + "Use the number shown by 'list'.");
        }

        if (taskNumber < 1) {
            throw new EchoException("Task numbers start at 1. Use the number shown by 'list'.");
        }

        return switch (type) {
            case MARK -> new MarkCommand(taskNumber);
            case UNMARK -> new UnmarkCommand(taskNumber);
            case DELETE -> new DeleteCommand(taskNumber);
            default -> throw new IllegalArgumentException("Unsupported task command: " + type);
        };
    }

    /**
     * Parses a todo command and extracts its task description.
     *
     * @param input raw user input starting with the todo keyword.
     * @return {@link TodoCommand} object.
     * @throws EchoException if the description is empty, contains forbidden characters, or invalid.
     */
    private Command parseTodoCommand(String input) throws EchoException {
        String description = getCommandContent(input).trim();
        if (description.isEmpty()) {
            throw new EchoException("A todo needs a description. " + TODO_FORMAT);
        }
        validateSafeCharacters(description, "todo description");
        description = description.replaceAll("\\s+", " ");
        return new TodoCommand(description);
    }

    /**
     * Parses a deadline command and extracts its task description and deadline.
     *
     * @param input raw user input starting with the deadline keyword.
     * @return {@link DeadlineCommand} object.
     * @throws EchoException if the description, delimiter, or date are empty, duplicate, or erroneous.
     */
    private Command parseDeadlineCommand(String input) throws EchoException {
        String content = getCommandContent(input);
        if (content.isEmpty()) {
            throw new EchoException("A deadline needs a description and a due date. "
                    + DEADLINE_FORMAT);
        }

        int byCount = countMarkerOccurrences(content, "/by");
        if (byCount > 1) {
            throw new EchoException("Parameter '/by' was specified multiple times. "
                    + "Please provide only one '/by' parameter. " + DEADLINE_FORMAT);
        }

        ArgumentPair arguments = splitByMarker(
                content,
                "/by",
                "A deadline must include a due date using '/by <deadline>'. " + DEADLINE_FORMAT,
                "A deadline needs a description before '/by'. " + DEADLINE_FORMAT,
                "Please provide a due date after '/by'. " + DEADLINE_FORMAT
        );

        String description = arguments.prefix().replaceAll("\\s+", " ");
        String dueDate = arguments.suffix();

        validateSafeCharacters(description, "deadline description");
        validateSafeCharacters(dueDate, "deadline due date");
        validateDateTime(dueDate, "deadline", DEADLINE_FORMAT);

        return new DeadlineCommand(description, dueDate);
    }

    /**
     * Parses an event command and extracts its task description, start date, and end date.
     *
     * @param input raw user input starting with the event keyword.
     * @return {@link EventCommand} object.
     * @throws EchoException if the description, delimiter, or date(s) are empty, duplicate, or erroneous.
     */
    private Command parseEventCommand(String input) throws EchoException {
        String content = getCommandContent(input);
        if (content.isEmpty()) {
            throw new EchoException(
                    "An event needs a description, start date, and end date. " + EVENT_FORMAT);
        }

        int fromCount = countMarkerOccurrences(content, "/from");
        if (fromCount > 1) {
            throw new EchoException("Parameter '/from' was specified multiple times. "
                    + "Please provide only one '/from' parameter. " + EVENT_FORMAT);
        }

        int toCount = countMarkerOccurrences(content, "/to");
        if (toCount > 1) {
            throw new EchoException("Parameter '/to' was specified multiple times. "
                    + "Please provide only one '/to' parameter. " + EVENT_FORMAT);
        }

        if (fromCount == 0) {
            throw new EchoException("An event must include a start date using '/from <date>'. "
                    + EVENT_FORMAT);
        }
        if (toCount == 0) {
            throw new EchoException("An event must include an end date using '/to <date>'. "
                    + EVENT_FORMAT);
        }

        int fromIndex = findMarkerIndex(content, "/from");
        int toIndex = findMarkerIndex(content, "/to");

        String description;
        String startDate;
        String endDate;

        if (fromIndex < toIndex) {
            description = content.substring(0, fromIndex).trim();
            startDate = content.substring(fromIndex + "/from".length(), toIndex).trim();
            endDate = content.substring(toIndex + "/to".length()).trim();
        } else {
            description = content.substring(0, toIndex).trim();
            endDate = content.substring(toIndex + "/to".length(), fromIndex).trim();
            startDate = content.substring(fromIndex + "/from".length()).trim();
        }

        if (description.isBlank()) {
            throw new EchoException("An event needs a description before '/from'. "
                    + EVENT_FORMAT);
        }
        if (startDate.isBlank()) {
            throw new EchoException("Please provide a start date after '/from'. " + EVENT_FORMAT);
        }
        if (endDate.isBlank()) {
            throw new EchoException("Please provide an end date after '/to'. " + EVENT_FORMAT);
        }

        validateSafeCharacters(description, "event description");
        validateSafeCharacters(startDate, "event start date");
        validateSafeCharacters(endDate, "event end date");

        description = description.replaceAll("\\s+", " ");

        validateDateTime(startDate, "event start", EVENT_FORMAT);
        validateDateTime(endDate, "event end", EVENT_FORMAT);

        DateTimeParser.DateTimeValue fromDateTime = DateTimeParser.parse(startDate);
        DateTimeParser.DateTimeValue toDateTime = DateTimeParser.parse(endDate);
        DateTimeParser.validateChronologicalOrder(fromDateTime, toDateTime);

        return new EventCommand(description, startDate, endDate);
    }

    /**
     * Validates date and optional time strings against expected formats.
     *
     * @param date date-time string to validate.
     * @param fieldName descriptive name of the target field for error messaging.
     * @param commandFormat suggested command usage format displayed on error.
     * @throws EchoException if the date-time string does not match accepted format rules.
     */
    private static void validateDateTime(String date, String fieldName, String commandFormat)
            throws EchoException {
        try {
            DateTimeParser.validate(date);
        } catch (DateTimeParseException exception) {
            if (!date.matches("[0-9]{2}-[0-9]{2}-[0-9]{4}( [0-9]{2}:[0-9]{2})?")) {
                throw new EchoException("The " + fieldName + " must use "
                        + DateTimeParser.DATE_FORMAT + " with an optional 24-hour time "
                        + DateTimeParser.TIME_FORMAT + ". " + commandFormat);
            }

            throw new EchoException("'" + date + "' is not a valid date or time "
                    + "(such as a non-existent calendar date like Feb 30). " + commandFormat);
        }
    }

    /**
     * Ensures user text does not contain reserved storage delimiter or newline characters.
     *
     * @param text input string to check.
     * @param fieldName name of the field for error reporting.
     * @throws EchoException if the text contains forbidden characters.
     */
    private static void validateSafeCharacters(String text, String fieldName) throws EchoException {
        if (text.contains("|")) {
            throw new EchoException("The " + fieldName + " cannot contain the '|' character "
                    + "because it is reserved for data storage.");
        }
        if (text.contains("\n") || text.contains("\r")) {
            throw new EchoException("The " + fieldName + " cannot contain newline characters.");
        }
    }

    /**
     * Counts how many times a delimiter marker appears as a distinct whitespace-bounded token.
     *
     * @param text text to search.
     * @param marker delimiter marker such as {@code /by} or {@code /from}.
     * @return number of occurrences.
     */
    private static int countMarkerOccurrences(String text, String marker) {
        String lowerText = text.toLowerCase(Locale.ROOT);
        String lowerMarker = marker.toLowerCase(Locale.ROOT);
        int markerLength = lowerMarker.length();
        int count = 0;
        int searchFrom = 0;

        while (searchFrom < lowerText.length()) {
            int index = lowerText.indexOf(lowerMarker, searchFrom);
            if (index == -1) {
                break;
            }

            boolean isPrecededByBoundary = (index == 0)
                    || Character.isWhitespace(text.charAt(index - 1));
            boolean isFollowedByBoundary = (index + markerLength == text.length())
                    || Character.isWhitespace(text.charAt(index + markerLength));

            if (isPrecededByBoundary && isFollowedByBoundary) {
                count++;
                searchFrom = index + markerLength;
            } else {
                searchFrom = index + 1;
            }
        }

        return count;
    }

    /**
     * Splits text into prefix and suffix segments around a delimiter marker.
     *
     * @param text full text containing the marker.
     * @param marker delimiter marker such as {@code /by} or {@code /from}.
     * @param missingMarkerMessage error message if the marker is not found.
     * @param emptyPrefixMessage error message if the prefix before the marker is blank.
     * @param emptySuffixMessage error message if the suffix after the marker is blank.
     * @return an {@link ArgumentPair} containing the trimmed prefix and suffix.
     * @throws EchoException if the marker is missing or either segment is blank.
     */
    private ArgumentPair splitByMarker(String text, String marker, String missingMarkerMessage,
            String emptyPrefixMessage, String emptySuffixMessage) throws EchoException {
        int markerIndex = findMarkerIndex(text, marker);
        if (markerIndex == -1) {
            throw new EchoException(missingMarkerMessage);
        }

        String prefix = text.substring(0, markerIndex).trim();
        if (prefix.isBlank()) {
            throw new EchoException(emptyPrefixMessage);
        }

        String suffix = text.substring(markerIndex + marker.length()).trim();
        if (suffix.isBlank()) {
            throw new EchoException(emptySuffixMessage);
        }

        return new ArgumentPair(prefix, suffix);
    }

    /**
     * Finds the starting index of a delimiter marker as a distinct token, ignoring case.
     *
     * @param text text to search.
     * @param marker delimiter marker to locate.
     * @return zero-based index of the marker, or -1 if not found.
     */
    private static int findMarkerIndex(String text, String marker) {
        String lowerText = text.toLowerCase(Locale.ROOT);
        String lowerMarker = marker.toLowerCase(Locale.ROOT);
        int markerLength = lowerMarker.length();
        int searchFrom = 0;

        while (searchFrom < lowerText.length()) {
            int index = lowerText.indexOf(lowerMarker, searchFrom);
            if (index == -1) {
                return -1;
            }

            boolean isPrecededByBoundary = (index == 0)
                    || Character.isWhitespace(text.charAt(index - 1));
            boolean isFollowedByBoundary = (index + markerLength == text.length())
                    || Character.isWhitespace(text.charAt(index + markerLength));

            if (isPrecededByBoundary && isFollowedByBoundary) {
                return index;
            }

            searchFrom = index + 1;
        }

        return -1;
    }

    /** Extracts the text following the first whitespace-separated command. */
    private String getCommandContent(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isWhitespace(input.charAt(i))) {
                return input.substring(i + 1).trim();
            }
        }
        return "";
    }

    /**
     * Holds a pair of prefix and suffix argument strings extracted from a command.
     *
     * @param prefix the argument before the marker.
     * @param suffix the argument after the marker.
     */
    private record ArgumentPair(String prefix, String suffix) {
    }
}
