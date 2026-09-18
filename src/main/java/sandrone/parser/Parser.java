package sandrone.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sandrone.SandroneException;
import sandrone.command.AddCommand;
import sandrone.command.Command;
import sandrone.command.CommandType;
import sandrone.command.ExitCommand;
import sandrone.command.FindCommand;
import sandrone.command.ListCommand;
import sandrone.command.MarkCommand;
import sandrone.command.RemoveCommand;
import sandrone.command.StatsCommand;
import sandrone.command.UnmarkCommand;
import sandrone.task.Deadline;
import sandrone.task.Event;
import sandrone.task.Task;
import sandrone.task.Todo;

/**
 * Interprets command text and saved task records as application data.
 */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String REMOVE_COMMAND = "remove";
    private static final String FIND_COMMAND = "find";
    private static final String STATS_COMMAND = "stats";
    private static final DateTimeFormatter LIST_DATE_FORMAT =
        DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String BY_MARKER_PATTERN = "\\s+/by\\s+";
    private static final String FROM_MARKER_PATTERN = "\\s+/from\\s+";
    private static final String TO_MARKER_PATTERN = "\\s+/to\\s+";

    /**
     * Returns the type of command represented by the user's input.
     */
    public CommandType getCommandType(String input) {
        if (input.equals(BYE_COMMAND)) {
            return CommandType.BYE;
        }
        if (isCommand(input, LIST_COMMAND)) {
            return CommandType.LIST;
        }
        if (isCommand(input, MARK_COMMAND)) {
            return CommandType.MARK;
        }
        if (isCommand(input, UNMARK_COMMAND)) {
            return CommandType.UNMARK;
        }
        if (isCommand(input, TODO_COMMAND)
                || isCommand(input, DEADLINE_COMMAND)
                || isCommand(input, EVENT_COMMAND)) {
            return CommandType.ADD;
        }
        if (isCommand(input, REMOVE_COMMAND)) {
            return CommandType.REMOVE;
        }
        if (isCommand(input, FIND_COMMAND)) {
            return CommandType.FIND;
        }
        if (input.equals(STATS_COMMAND)) {
            return CommandType.STATS;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Parses a complete user command into an executable command object.
     */
    public Command parse(String command) throws SandroneException {
        switch (getCommandType(command)) {
            case BYE:
                return new ExitCommand();
            case LIST:
                String dateText = command.substring(LIST_COMMAND.length()).trim();
                return new ListCommand(dateText.isEmpty() ? null : parseListDate(dateText), dateText);
            case MARK:
                return new MarkCommand(parseTaskNumber(command, MARK_COMMAND));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(command, UNMARK_COMMAND));
            case ADD:
                return new AddCommand(parseTask(command), command);
            case REMOVE:
                return new RemoveCommand(parseTaskNumber(command, REMOVE_COMMAND));
            case FIND:
                return new FindCommand(parseFindKeyword(command));
            case STATS:
                return new StatsCommand();
            default:
                throw new SandroneException("Invalid command");
        }
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     */
    public Task parseTask(String command) throws SandroneException {
        if (isCommand(command, TODO_COMMAND)) {
            return parseTodo(command);
        }
        if (isCommand(command, DEADLINE_COMMAND)) {
            return parseDeadline(command);
        }
        if (isCommand(command, EVENT_COMMAND)) {
            return parseEvent(command);
        }
        throw new SandroneException("Invalid command");
    }

    /** Parses the description in a todo command. */
    private Todo parseTodo(String command) throws SandroneException {
        String description = command.substring(TODO_COMMAND.length()).trim();
        validateTaskText(description, "Description");
        return new Todo(description);
    }

    /** Parses the description and due time in a deadline command. */
    private Deadline parseDeadline(String command) throws SandroneException {
        String deadlineDetails = command.substring(DEADLINE_COMMAND.length()).trim();
        int byMarkerCount = countMarkers(deadlineDetails, BY_MARKER_PATTERN);
        if (byMarkerCount > 1) {
            throw new SandroneException("Deadline must contain exactly one /by marker");
        }
        String[] parts = deadlineDetails.split(BY_MARKER_PATTERN, 2);
        if (parts.length != 2) {
            throw new SandroneException("Deadline must include /by followed by a time");
        }
        String description = parts[0].trim();
        String by = parts[1].trim();
        LocalDateTime byDate = parseDateTime(by);
        validateTaskText(description, "Description");
        validateTaskText(by, "Deadline time");
        return new Deadline(description, byDate);
    }

    /** Parses the description, start time, and end time in an event command. */
    private Event parseEvent(String command) throws SandroneException {
        String eventDetails = command.substring(EVENT_COMMAND.length()).trim();
        int fromMarkerCount = countMarkers(eventDetails, FROM_MARKER_PATTERN);
        int toMarkerCount = countMarkers(eventDetails, TO_MARKER_PATTERN);
        if (fromMarkerCount > 1 || toMarkerCount > 1) {
            throw new SandroneException("Event must contain exactly one /from and one /to marker");
        }
        String[] fromParts = eventDetails.split(FROM_MARKER_PATTERN, 2);
        if (fromParts.length != 2) {
            throw new SandroneException("Event must include /from and /to times");
        }
        String description = fromParts[0].trim();
        String[] toParts = fromParts[1].split(TO_MARKER_PATTERN, 2);
        if (toParts.length != 2) {
            throw new SandroneException("Event must include /from and /to times");
        }
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        validateTaskText(description, "Description");
        validateTaskText(from, "Event start time");
        validateTaskText(to, "Event end time");
        LocalDateTime start = parseDateTime(from);
        LocalDateTime end = parseDateTime(to);
        validateEventTimeRange(start, end);
        return new Event(description, start, end);
    }

    /**
     * Recreates one task from its saved pipe-separated representation.
     */
    public Task parseTaskFromFile(String taskLine) throws SandroneException {
        if (taskLine == null) {
            throw new SandroneException("empty task record");
        }
        String[] parts = taskLine.split(" \\| ", -1);
        if (parts.length < 2 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new SandroneException("invalid task status");
        }
        Task task;
        switch (parts[0]) {
            case "T":
                requirePartCount(parts, 3, "todo");
                validateTaskText(parts[2], "Description");
                task = new Todo(parts[2]);
                break;
            case "D":
                requirePartCount(parts, 4, "deadline");
                validateTaskText(parts[2], "Description");
                validateTaskText(parts[3], "Deadline time");
                task = new Deadline(parts[2], parseDateTime(parts[3]));
                break;
            case "E":
                requirePartCount(parts, 5, "event");
                validateTaskText(parts[2], "Description");
                validateTaskText(parts[3], "Event start time");
                validateTaskText(parts[4], "Event end time");
                LocalDateTime start = parseDateTime(parts[3]);
                LocalDateTime end = parseDateTime(parts[4]);
                validateEventTimeRange(start, end);
                task = new Event(parts[2], start, end);
                break;
            default:
                throw new SandroneException("unknown task type");
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the one-based task number without checking whether it exists.
     */
    private int parseTaskNumber(String command, String commandName) throws SandroneException {
        String taskNumberText = command.substring(commandName.length()).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new SandroneException("Task number must be a positive whole number");
        }
    }

    /** Validates that an event ends after it starts. */
    private void validateEventTimeRange(LocalDateTime start, LocalDateTime end)
            throws SandroneException {
        if (!end.isAfter(start)) {
            throw new SandroneException("Event end time must be after its start time");
        }
    }

    /** Counts non-overlapping occurrences of a command-marker pattern. */
    private int countMarkers(String text, String markerPattern) {
        int count = 0;
        Matcher matcher = Pattern.compile(markerPattern).matcher(text);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Parses the required search keyword from a find command.
     */
    private String parseFindKeyword(String command) throws SandroneException {
        String keyword = command.substring(FIND_COMMAND.length()).trim();
        validateTaskText(keyword, "Search keyword");
        return keyword;
    }

    /**
     * Parses the date accepted by a {@code list <date>} command.
     */
    public LocalDate parseListDate(String input) throws SandroneException {
        try {
            return LocalDate.parse(input, LIST_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SandroneException("Invalid list date. Use d/M/yyyy, e.g. 29/8/2026");
        }
    }

    /**
     * Validates a non-empty task field that can be stored in the save format.
     */
    private void validateTaskText(String text, String fieldName) throws SandroneException {
        if (text.isBlank()) {
            throw new SandroneException(fieldName + " cannot be empty");
        }
        if (text.contains("|")) {
            throw new SandroneException(fieldName + " cannot contain |");
        }
    }

    /**
     * Validates the expected number of fields in one saved task record.
     */
    private void requirePartCount(String[] parts, int expectedCount, String taskType)
            throws SandroneException {
        if (parts.length != expectedCount) {
            throw new SandroneException("invalid " + taskType + " record");
        }
    }

    /** Returns whether input is a command keyword followed by an optional argument. */
    private boolean isCommand(String input, String commandName) {
        return input.equals(commandName) || input.startsWith(commandName + " ");
    }

    /**
     * Parses a supported task date and time.
     */
    private LocalDateTime parseDateTime(String input) throws SandroneException {
        DateTimeFormatter[] formats = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d/M/uuuu h:mma")
                .toFormatter().withResolverStyle(ResolverStyle.STRICT)
        };
        for (DateTimeFormatter format : formats) {
            try {
                return LocalDateTime.parse(input, format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new SandroneException("Invalid date, correct examples include\n"
            + "2026-08-29 1430, 29/8/2026 1430, 29/8/2026 2:30PM");
    }
}
