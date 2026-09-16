package olaf;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses user input into validated commands that Olaf can execute.
 */
final class Parser {
    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\\R");
    private static final Pattern EDGE_WHITESPACE_PATTERN =
            Pattern.compile("(?U)^[\\s\\p{javaWhitespace}]+|[\\s\\p{javaWhitespace}]+$");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("[+-]?\\p{javaDigit}+");
    private static final String COMMAND_EXIT = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_RESCHEDULE = "reschedule";
    private static final String MARKER_BY = "/by";
    private static final String MARKER_FROM = "/from";
    private static final String MARKER_TO = "/to";
    private static final String INVALID_MARK_COMMAND_MESSAGE =
            "Use 'mark <task number>' to mark a task as done.";
    private static final String INVALID_UNMARK_COMMAND_MESSAGE =
            "Use 'unmark <task number>' to mark a task as not done.";
    private static final String INVALID_DELETE_COMMAND_MESSAGE =
            "Use 'delete <task number>' to delete a task.";
    private static final String INVALID_TODO_COMMAND_MESSAGE =
            "Use 'todo <description>' to add a ToDo.";
    private static final String INVALID_DEADLINE_COMMAND_MESSAGE =
            "Use 'deadline <description> /by <yyyy-MM-dd>' to add a deadline.";
    private static final String INVALID_EVENT_COMMAND_MESSAGE =
            "Use 'event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>' to add an event.";
    private static final String INVALID_FIND_COMMAND_MESSAGE =
            "Use 'find <keyword>' to find matching tasks.";
    private static final String INVALID_RESCHEDULE_COMMAND_MESSAGE =
            "Use 'reschedule <task number> /by <yyyy-MM-dd>' for a deadline or "
                    + "'reschedule <task number> /from <yyyy-MM-dd> /to <yyyy-MM-dd>' for an event.";
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Unknown command. Use todo, deadline, event, find, list, mark, unmark, "
                    + "delete, reschedule, or bye.";

    /**
     * Parses one line of user input.
     *
     * @param input raw command entered by the user.
     * @return validated command and its required data
     * @throws CommandParseException if the input is unknown or malformed
     */
    ParsedCommand parse(String input) throws CommandParseException {
        if (LINE_BREAK_PATTERN.matcher(input).find()) {
            throw new CommandParseException("Enter one command on a single line; line breaks are not allowed.");
        }
        String trimmedInput = stripWhitespace(input);
        if (trimmedInput.isEmpty()) {
            throw new CommandParseException("Please enter a command, such as 'list' or 'todo read book'.");
        }
        if (trimmedInput.equalsIgnoreCase(COMMAND_EXIT)) {
            return ParsedCommand.createWithoutPayload(ParsedCommand.Action.EXIT);
        }
        if (trimmedInput.equalsIgnoreCase(COMMAND_LIST)) {
            return ParsedCommand.createWithoutPayload(ParsedCommand.Action.LIST);
        }

        String[] commandParts = trimmedInput.split("(?U)[\\s\\p{javaWhitespace}]+", 2);
        String commandWord = commandParts[0].toLowerCase(Locale.ROOT);
        String arguments = commandParts.length == 2 ? commandParts[1] : "";
        return switch (commandWord) {
            case COMMAND_LIST, COMMAND_EXIT -> throw new CommandParseException(
                    "'" + commandWord + "' accepts no arguments. Use '" + commandWord + "' on its own.");
            case COMMAND_MARK -> parseTaskNumberCommand(
                    arguments, ParsedCommand.Action.MARK, INVALID_MARK_COMMAND_MESSAGE);
            case COMMAND_UNMARK -> parseTaskNumberCommand(
                    arguments, ParsedCommand.Action.UNMARK, INVALID_UNMARK_COMMAND_MESSAGE);
            case COMMAND_DELETE -> parseTaskNumberCommand(
                    arguments, ParsedCommand.Action.DELETE, INVALID_DELETE_COMMAND_MESSAGE);
            case COMMAND_TODO -> parseTodoCommand(arguments);
            case COMMAND_DEADLINE -> parseDeadlineCommand(arguments);
            case COMMAND_EVENT -> parseEventCommand(arguments);
            case COMMAND_FIND -> parseFindCommand(arguments);
            case COMMAND_RESCHEDULE -> parseRescheduleCommand(arguments);
            default -> throw new CommandParseException(UNKNOWN_COMMAND_MESSAGE);
        };
    }

    private ParsedCommand parseTaskNumberCommand(String arguments, ParsedCommand.Action action,
            String invalidCommandMessage) throws CommandParseException {
        return ParsedCommand.createForTaskNumber(action, parseTaskNumber(arguments, invalidCommandMessage));
    }

    private int parseTaskNumber(String text, String invalidCommandMessage) throws CommandParseException {
        String numberText = requireText(text, "task number", invalidCommandMessage);
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            String reason = INTEGER_PATTERN.matcher(numberText).matches()
                    ? "Task number is too large or too small to read."
                    : "Task number must be a whole number.";
            throw new CommandParseException(reason + " " + invalidCommandMessage);
        }
    }

    private ParsedCommand parseTodoCommand(String arguments) throws CommandParseException {
        String description = requireText(arguments, "description", INVALID_TODO_COMMAND_MESSAGE);
        return ParsedCommand.add(new Todo(description));
    }

    private ParsedCommand parseDeadlineCommand(String arguments) throws CommandParseException {
        String trimmedArguments = stripWhitespace(arguments);
        int byMarkerIndex = findMarker(trimmedArguments, MARKER_BY);
        if (byMarkerIndex < 0) {
            throw new CommandParseException("Missing /by date field. " + INVALID_DEADLINE_COMMAND_MESSAGE);
        }

        String description = requireText(trimmedArguments.substring(0, byMarkerIndex), "description",
                INVALID_DEADLINE_COMMAND_MESSAGE);
        LocalDate dueDate = parseDate(trimmedArguments.substring(byMarkerIndex + MARKER_BY.length()),
                "deadline date", INVALID_DEADLINE_COMMAND_MESSAGE);
        return ParsedCommand.add(new Deadline(description, dueDate));
    }

    private ParsedCommand parseEventCommand(String arguments) throws CommandParseException {
        String trimmedArguments = stripWhitespace(arguments);
        int fromMarkerIndex = findMarker(trimmedArguments, MARKER_FROM);
        if (fromMarkerIndex < 0) {
            throw new CommandParseException("Missing /from date field. " + INVALID_EVENT_COMMAND_MESSAGE);
        }
        String description = requireText(trimmedArguments.substring(0, fromMarkerIndex), "description",
                INVALID_EVENT_COMMAND_MESSAGE);
        EventDates dates = parseEventDates(trimmedArguments, INVALID_EVENT_COMMAND_MESSAGE);
        if (dates.end().isBefore(dates.start())) {
            throw new CommandParseException(Event.INVALID_DATE_RANGE_MESSAGE + " " + INVALID_EVENT_COMMAND_MESSAGE);
        }
        return ParsedCommand.add(new Event(description, dates.start(), dates.end()));
    }

    private ParsedCommand parseFindCommand(String arguments) throws CommandParseException {
        String keyword = requireText(arguments, "search keyword", INVALID_FIND_COMMAND_MESSAGE);
        return ParsedCommand.find(keyword);
    }

    private ParsedCommand parseRescheduleCommand(String arguments) throws CommandParseException {
        String[] parts = stripWhitespace(arguments).split("(?U)[\\s\\p{javaWhitespace}]+", 2);
        if (parts[0].startsWith("/")) {
            throw new CommandParseException("Missing task number. " + INVALID_RESCHEDULE_COMMAND_MESSAGE);
        }
        int taskNumber = parseTaskNumber(parts[0], INVALID_RESCHEDULE_COMMAND_MESSAGE);
        String schedule = parts.length == 2 ? parts[1] : "";
        if (findMarker(schedule, MARKER_BY) == 0) {
            LocalDate dueDate = parseDate(schedule.substring(MARKER_BY.length()),
                    "deadline date", INVALID_RESCHEDULE_COMMAND_MESSAGE);
            return ParsedCommand.rescheduleDeadline(taskNumber, dueDate);
        }
        if (findMarker(schedule, MARKER_FROM) == 0) {
            EventDates dates = parseEventDates(schedule, INVALID_RESCHEDULE_COMMAND_MESSAGE);
            return ParsedCommand.rescheduleEvent(taskNumber, dates.start(), dates.end());
        }
        throw new CommandParseException("Expected /by or /from and /to date fields after the task number. "
                + INVALID_RESCHEDULE_COMMAND_MESSAGE);
    }

    private EventDates parseEventDates(String text, String usage) throws CommandParseException {
        int fromMarkerIndex = findMarker(text, MARKER_FROM);
        int toMarkerIndex = findMarker(text, MARKER_TO);
        if (toMarkerIndex < 0) {
            throw new CommandParseException("Missing /to date field. " + usage);
        }
        if (toMarkerIndex < fromMarkerIndex) {
            throw new CommandParseException("The /from date field must come before /to. " + usage);
        }
        LocalDate startDate = parseDate(text.substring(fromMarkerIndex + MARKER_FROM.length(), toMarkerIndex),
                "event start date", usage);
        LocalDate endDate = parseDate(text.substring(toMarkerIndex + MARKER_TO.length()), "event end date", usage);
        return new EventDates(startDate, endDate);
    }

    private String requireText(String text, String fieldName, String invalidCommandMessage)
            throws CommandParseException {
        String trimmedText = stripWhitespace(text);
        if (trimmedText.isEmpty()) {
            throw new CommandParseException("Missing " + fieldName + ". " + invalidCommandMessage);
        }
        return trimmedText;
    }

    private LocalDate parseDate(String text, String fieldName, String invalidCommandMessage)
            throws CommandParseException {
        String dateText = requireText(text, fieldName, invalidCommandMessage);
        if (!TaskDateFormat.hasInputFormat(dateText)) {
            throw new CommandParseException("Invalid " + fieldName
                    + ". Use yyyy-MM-dd (for example, 2026-09-20). " + invalidCommandMessage);
        }
        try {
            return TaskDateFormat.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new CommandParseException("Invalid " + fieldName + ": '" + dateText
                    + "' is not a valid calendar date. " + invalidCommandMessage);
        }
    }

    private String stripWhitespace(String text) {
        return EDGE_WHITESPACE_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * Finds the first case-insensitive marker bounded by whitespace or the start/end of the text.
     * These boundaries prevent words such as {@code /bylaws} from being treated as markers.
     */
    private int findMarker(String text, String marker) {
        Pattern markerPattern = Pattern.compile(
                "(?iU)(?<![^\\s\\p{javaWhitespace}])" + Pattern.quote(marker)
                        + "(?![^\\s\\p{javaWhitespace}])");
        Matcher matcher = markerPattern.matcher(text);
        return matcher.find() ? matcher.start() : -1;
    }

    /** Holds parsed dates until the command's date-order validation is applied. */
    private record EventDates(LocalDate start, LocalDate end) {
    }
}
