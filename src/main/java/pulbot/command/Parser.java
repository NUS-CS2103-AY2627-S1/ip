package pulbot.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pulbot.Pulbot;
import pulbot.PulbotException;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Todo;

/** Converts raw user input into executable commands. */
public class Parser {
    private static final String INVALID_COMMAND_MESSAGE =
            "Invalid command. Please read the instructions and try again.";
    private static final String DEADLINE_FORMAT_MESSAGE =
            "Please use: deadline <description> /by <when>.";
    private static final String EVENT_FORMAT_MESSAGE =
            "Please use: event <description> /from <start> /to <end>.";
    private static final Pattern DEADLINE_PATTERN =
            Pattern.compile("^(.+?)\\s+/by\\s+(.+)$");
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("^(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$");
    private static final Pattern BY_MARKER_PATTERN =
            Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern FROM_MARKER_PATTERN =
            Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern TO_MARKER_PATTERN =
            Pattern.compile("(?<!\\S)/to(?!\\S)");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Parses one user input line into a command. */
    public Command parse(String input) throws PulbotException {
        String[] commandParts = splitCommand(input);
        String commandWord = commandParts[0];
        String arguments = commandParts[1];
        switch (commandWord) {
            case "bye":
                requireNoArguments(arguments);
                return new ExitCommand();
            case "list":
                requireNoArguments(arguments);
                return new ListCommand();
            case "mark":
                return new MarkCommand(arguments);
            case "unmark":
                return new UnmarkCommand(arguments);
            case "delete":
                return new DeleteCommand(arguments);
            case "on":
                return new OnCommand(parseDate(arguments));
            case "find":
                return parseFind(arguments);
            case "todo":
                return parseTodo(arguments);
            case "deadline":
                return parseDeadline(arguments);
            case "event":
                return parseEvent(arguments);
            default:
                throw new PulbotException(INVALID_COMMAND_MESSAGE);
        }
    }

    /** Separates a command word from its arguments while tolerating accidental outer whitespace. */
    private String[] splitCommand(String input) throws PulbotException {
        if (input == null || input.isBlank()) {
            throw new PulbotException(INVALID_COMMAND_MESSAGE);
        }
        String[] commandParts = input.strip().split("\\s+", 2);
        String arguments = commandParts.length == 1 ? "" : commandParts[1].strip();
        return new String[] {commandParts[0], arguments};
    }

    private void requireNoArguments(String arguments) throws PulbotException {
        if (!arguments.isEmpty()) {
            throw new PulbotException(INVALID_COMMAND_MESSAGE);
        }
    }

    private Command parseFind(String keyword) throws PulbotException {
        if (keyword.isEmpty()) {
            throw new PulbotException("Please include a keyword to search for.");
        }
        return new FindCommand(keyword);
    }

    private Command parseTodo(String description) throws PulbotException {
        if (description.isEmpty()) {
            throw new PulbotException("Please include a description.");
        }
        return new AddCommand(new Todo(description));
    }

    /** Parses and validates a deadline's description and date-time arguments. */
    private Command parseDeadline(String details) throws PulbotException {
        Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(details);
        if (countMatches(BY_MARKER_PATTERN, details) != 1 || !deadlineMatcher.matches()) {
            throw new PulbotException(DEADLINE_FORMAT_MESSAGE);
        }

        String description = deadlineMatcher.group(1).strip();
        String dateTime = deadlineMatcher.group(2).strip();
        return new AddCommand(new Deadline(description, Pulbot.parseDateTime(dateTime)));
    }

    /** Parses and validates an event's description, start time, and end time. */
    private Command parseEvent(String details) throws PulbotException {
        Matcher eventMatcher = EVENT_PATTERN.matcher(details);
        boolean hasOneFromMarker = countMatches(FROM_MARKER_PATTERN, details) == 1;
        boolean hasOneToMarker = countMatches(TO_MARKER_PATTERN, details) == 1;
        if (!hasOneFromMarker || !hasOneToMarker || !eventMatcher.matches()) {
            throw new PulbotException(EVENT_FORMAT_MESSAGE);
        }

        String description = eventMatcher.group(1).strip();
        String startTime = eventMatcher.group(2).strip();
        String endTime = eventMatcher.group(3).strip();
        return new AddCommand(new Event(description,
                Pulbot.parseDateTime(startTime), Pulbot.parseDateTime(endTime)));
    }

    private int countMatches(Pattern pattern, String value) {
        int matchCount = 0;
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            matchCount++;
        }
        return matchCount;
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use d/M/yyyy for the date, for example 2/12/2019.");
        }
    }
}
