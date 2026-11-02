package megia.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import megia.exception.ErrorCode;
import megia.exception.UserInputException;
import megia.model.Deadline;
import megia.model.Event;
import megia.model.ParsedCommand;
import megia.model.Task;
import megia.model.Todo;

/** Parses commands and creates strongly typed task objects. */
public final class TaskParser {
    private static final String MARKER_BY = "/by";
    private static final String MARKER_ON = "/on";
    private static final String MARKER_FROM = "/from";
    private static final String MARKER_TO = "/to";
    private static final Pattern BY_PATTERN = markerPattern(MARKER_BY);
    private static final Pattern ON_PATTERN = markerPattern(MARKER_ON);
    private static final Pattern FROM_PATTERN = markerPattern(MARKER_FROM);
    private static final Pattern TO_PATTERN = markerPattern(MARKER_TO);
    private static final DateTimeFormatter ISO_DATE_TIME = strictFormatter("uuuu-MM-dd HHmm");
    private static final DateTimeFormatter SLASH_DATE_TIME = strictFormatter("d/M/uuuu HHmm");
    private static final DateTimeFormatter ISO_DATE = strictFormatter("uuuu-MM-dd");
    private static final DateTimeFormatter SLASH_DATE = strictFormatter("d/M/uuuu");
    private static final DateTimeFormatter TIME = strictFormatter("HHmm");

    /** Creates a parser for task commands. */
    public TaskParser() {
    }

    /**
     * Splits complete input into its command name and body.
     *
     * @param rawCommand Complete user input.
     * @return Parsed command name and body.
     */
    public ParsedCommand parseCommand(String rawCommand) {
        String[] inputParts = rawCommand.strip().split("\\s+", 2);
        return new ParsedCommand(inputParts[0], inputParts.length > 1 ? inputParts[1] : "");
    }

    /**
     * Parses a task creation command.
     *
     * @param command Command to parse.
     * @return Newly created task.
     * @throws UserInputException If the command body is invalid.
     */
    public Task parseNewTask(ParsedCommand command) throws UserInputException {
        return switch (command.commandName()) {
            case "deadline" -> parseDeadline(command.body());
            case "event" -> parseEvent(command.body());
            default -> parseTodo(command.body());
        };
    }

    /**
     * Parses and validates a positive, one-based task ID.
     *
     * @param command Command containing the task ID.
     * @return Parsed task ID.
     * @throws UserInputException If the task ID is invalid.
     */
    public int parseTaskId(ParsedCommand command) throws UserInputException {
        String taskIdText = command.body().strip();
        if (taskIdText.isEmpty()) {
            throw new UserInputException(ErrorCode.TASK_ID_MISSING, command.commandName());
        }
        if (!taskIdText.matches("[+-]?\\d+")) {
            throw new UserInputException(ErrorCode.TASK_ID_NOT_INTEGER, taskIdText);
        }
        if (taskIdText.startsWith("-")) {
            throw new UserInputException(ErrorCode.TASK_ID_NOT_POSITIVE);
        }
        try {
            int taskId = Integer.parseInt(taskIdText);
            if (taskId <= 0) {
                throw new UserInputException(ErrorCode.TASK_ID_NOT_POSITIVE);
            }
            return taskId;
        } catch (NumberFormatException exception) {
            throw new UserInputException(ErrorCode.TASK_ID_TOO_LARGE, taskIdText);
        }
    }

    /**
     * Parses an optional date supplied to the list command.
     *
     * @param command List command to parse.
     * @return Empty when all tasks should be listed, or the requested date.
     * @throws UserInputException If the supplied date is invalid.
     */
    public Optional<LocalDate> parseListDate(ParsedCommand command) throws UserInputException {
        String listDateText = command.body().strip();
        if (listDateText.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(parseDate(listDateText));
        } catch (DateTimeParseException exception) {
            throw new UserInputException(ErrorCode.LIST_DATE_INVALID, listDateText);
        }
    }

    /**
     * Parses a nonempty description query supplied to the find command.
     *
     * @param command Find command to parse.
     * @return Query text with leading and trailing whitespace removed.
     * @throws UserInputException If the query is empty.
     */
    public String parseFindQuery(ParsedCommand command) throws UserInputException {
        String query = command.body().strip();
        if (query.isEmpty()) {
            throw new UserInputException(ErrorCode.FIND_QUERY_MISSING);
        }
        return query;
    }

    private Todo parseTodo(String body) throws UserInputException {
        String description = body.strip();
        if (description.isBlank()) {
            throw new UserInputException(ErrorCode.TODO_DESCRIPTION_MISSING);
        }
        validateDescription(description);
        return new Todo(description);
    }

    private Deadline parseDeadline(String body) throws UserInputException {
        MarkerLocation byMarker = findSingleMarker(body, BY_PATTERN, MARKER_BY,
                ErrorCode.DEADLINE_BY_MARKER_MISSING);
        String description = body.substring(0, byMarker.start()).strip();
        String value = body.substring(byMarker.end()).strip();
        if (description.isBlank()) {
            throw new UserInputException(ErrorCode.DEADLINE_DESCRIPTION_MISSING);
        }
        validateDescription(description);
        if (value.isBlank()) {
            throw new UserInputException(ErrorCode.DEADLINE_BY_VALUE_MISSING);
        }
        try {
            return new Deadline(description, parseDateTime(value));
        } catch (DateTimeParseException exception) {
            throw new UserInputException(ErrorCode.DEADLINE_DATE_INVALID);
        }
    }

    private Event parseEvent(String body) throws UserInputException {
        EventMarkers markers = findAndValidateEventMarkers(body);
        EventText eventText = extractEventText(body, markers);
        return markers.onMarker() == null
                ? parseCompleteDateTimeEvent(eventText)
                : parseSameDayEvent(eventText);
    }

    private static EventMarkers findAndValidateEventMarkers(String body)
            throws UserInputException {
        MarkerLocation onMarker = findOptionalMarker(body, ON_PATTERN, MARKER_ON);
        MarkerLocation fromMarker = findOptionalMarker(body, FROM_PATTERN, MARKER_FROM);
        MarkerLocation toMarker = findOptionalMarker(body, TO_PATTERN, MARKER_TO);
        if (fromMarker == null) {
            throw new UserInputException(ErrorCode.EVENT_FROM_MARKER_MISSING);
        }
        if (toMarker == null) {
            throw new UserInputException(ErrorCode.EVENT_TO_MARKER_MISSING);
        }
        boolean areRequiredMarkersOutOfOrder = fromMarker.start() > toMarker.start();
        boolean isDateMarkerOutOfOrder = onMarker != null && onMarker.start() > fromMarker.start();
        if (areRequiredMarkersOutOfOrder || isDateMarkerOutOfOrder) {
            throw new UserInputException(ErrorCode.EVENT_MARKERS_OUT_OF_ORDER);
        }
        assert fromMarker.start() < toMarker.start()
                : "Validated /from marker must precede /to marker";
        assert onMarker == null || onMarker.start() < fromMarker.start()
                : "Validated /on marker must precede /from marker";
        return new EventMarkers(onMarker, fromMarker, toMarker);
    }

    private static EventText extractEventText(String body, EventMarkers markers)
            throws UserInputException {
        MarkerLocation firstMarker = markers.onMarker() == null
                ? markers.fromMarker()
                : markers.onMarker();
        String description = body.substring(0, firstMarker.start()).strip();
        if (description.isBlank()) {
            throw new UserInputException(ErrorCode.EVENT_DESCRIPTION_MISSING);
        }
        validateDescription(description);
        String dateText = markers.onMarker() == null
                ? null
                : body.substring(markers.onMarker().end(), markers.fromMarker().start()).strip();
        String startText = body.substring(
                markers.fromMarker().end(), markers.toMarker().start()).strip();
        String endText = body.substring(markers.toMarker().end()).strip();
        if (startText.isBlank()) {
            throw new UserInputException(ErrorCode.EVENT_FROM_VALUE_MISSING);
        }
        if (endText.isBlank()) {
            throw new UserInputException(ErrorCode.EVENT_TO_VALUE_MISSING);
        }
        return new EventText(description, dateText, startText, endText);
    }

    private static void validateDescription(String description) throws UserInputException {
        if (description.contains("\r") || description.contains("\n")) {
            throw new UserInputException(ErrorCode.DESCRIPTION_LINE_BREAK);
        }
    }

    private static Event parseSameDayEvent(EventText eventText) throws UserInputException {
        if (eventText.dateText().isBlank()) {
            throw new UserInputException(ErrorCode.EVENT_DATE_INVALID);
        }
        LocalDate date;
        try {
            date = parseDate(eventText.dateText());
        } catch (DateTimeParseException exception) {
            throw new UserInputException(ErrorCode.EVENT_DATE_INVALID);
        }
        LocalTime startTime = parseTime(
                eventText.startText(), ErrorCode.EVENT_START_TIME_INVALID);
        LocalTime endTime = parseTime(eventText.endText(), ErrorCode.EVENT_END_TIME_INVALID);
        return createEvent(
                eventText.description(),
                LocalDateTime.of(date, startTime),
                LocalDateTime.of(date, endTime));
    }

    private static Event parseCompleteDateTimeEvent(EventText eventText)
            throws UserInputException {
        LocalDateTime start = parseEventDateTime(
                eventText.startText(), ErrorCode.EVENT_START_TIME_INVALID);
        LocalDateTime end = parseEventDateTime(
                eventText.endText(), ErrorCode.EVENT_END_TIME_INVALID);
        return createEvent(eventText.description(), start, end);
    }

    private static Event createEvent(String description, LocalDateTime start, LocalDateTime end)
            throws UserInputException {
        if (!end.isAfter(start)) {
            throw new UserInputException(ErrorCode.EVENT_END_NOT_AFTER_START);
        }
        return new Event(description, start, end);
    }

    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, ISO_DATE_TIME);
        } catch (DateTimeParseException exception) {
            return LocalDateTime.parse(value, SLASH_DATE_TIME);
        }
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, ISO_DATE);
        } catch (DateTimeParseException exception) {
            return LocalDate.parse(value, SLASH_DATE);
        }
    }

    private static LocalDateTime parseEventDateTime(String value, ErrorCode timeError)
            throws UserInputException {
        String[] parts = value.split("\\s+", -1);
        if (parts.length != 2) {
            throw new UserInputException(ErrorCode.EVENT_DATE_INVALID);
        }

        LocalDate date;
        try {
            date = parseDate(parts[0]);
        } catch (DateTimeParseException exception) {
            throw new UserInputException(ErrorCode.EVENT_DATE_INVALID);
        }
        return LocalDateTime.of(date, parseTime(parts[1], timeError));
    }

    private static LocalTime parseTime(String value, ErrorCode errorCode) throws UserInputException {
        try {
            return LocalTime.parse(value, TIME);
        } catch (DateTimeParseException exception) {
            throw new UserInputException(errorCode);
        }
    }

    private static MarkerLocation findSingleMarker(String body, Pattern pattern, String marker,
            ErrorCode missingMarkerError) throws UserInputException {
        MarkerLocation location = findOptionalMarker(body, pattern, marker);
        if (location == null) {
            throw new UserInputException(missingMarkerError);
        }
        return location;
    }

    private static MarkerLocation findOptionalMarker(String body, Pattern pattern, String marker)
            throws UserInputException {
        Matcher matcher = pattern.matcher(body);
        if (!matcher.find()) {
            return null;
        }
        MarkerLocation location = new MarkerLocation(matcher.start(), matcher.end());
        if (matcher.find()) {
            throw new UserInputException(ErrorCode.DUPLICATE_MARKER, marker);
        }
        return location;
    }

    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    private static Pattern markerPattern(String marker) {
        return Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
    }

    private record EventMarkers(
            MarkerLocation onMarker,
            MarkerLocation fromMarker,
            MarkerLocation toMarker) {
    }

    private record EventText(
            String description,
            String dateText,
            String startText,
            String endText) {
    }

    private record MarkerLocation(int start, int end) {
    }
}
