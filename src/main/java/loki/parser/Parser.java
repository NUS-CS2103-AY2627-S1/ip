package loki.parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loki.exception.LokiExceptions;
import loki.model.Deadline;
import loki.model.Event;
import loki.model.Task;
import loki.model.ToDo;

/**
 * Converts raw user commands into command keywords, tasks, and task numbers.
 */
public class Parser {
    private static final String DEADLINE_MARKER = "/by ";
    private static final String EVENT_START_MARKER = "/from ";
    private static final String EVENT_END_MARKER = "/to ";
    private static final Pattern UPDATE_MARKER_PATTERN =
            Pattern.compile("(?i)(?<!\\S)/(title|by|from|to)(?=\\s|$)");
    private static final Pattern UNKNOWN_UPDATE_MARKER_PATTERN =
            Pattern.compile("(?i)(?<!\\S)/[a-z]+(?=\\s|$)");

    /** Creates a parser for user commands. */
    public Parser() {
    }

    /**
     * Extracts and normalizes the command keyword.
     *
     * @param input the raw user command.
     * @return the lower-case command keyword.
     * @throws LokiExceptions if the command is empty.
     */
    public String parseKeyword(String input) throws LokiExceptions {
        if (input == null || input.isBlank()) {
            throw LokiExceptions.emptyInput();
        }
        return input.trim().split("\\s+")[0].toLowerCase(Locale.ROOT);
    }

    /**
     * Parses a task creation command.
     *
     * @param input the raw task command.
     * @return the corresponding task.
     * @throws LokiExceptions if the command is malformed.
     */
    public Task parseTask(String input) throws LokiExceptions {
        String trimmedInput = requireInput(input);
        String keyword = parseKeyword(trimmedInput);

        return switch (keyword) {
            case "todo" -> parseToDo(trimmedInput, keyword);
            case "deadline" -> parseDeadline(trimmedInput, keyword);
            case "event" -> parseEvent(trimmedInput, keyword);
            default -> throw LokiExceptions.unknownCommand();
        };
    }

    /**
     * Parses a one-based task number from a command.
     *
     * @param input the raw command.
     * @return the one-based task number.
     * @throws LokiExceptions if the command does not contain exactly one number.
     */
    public int parseTaskNumber(String input) throws LokiExceptions {
        String[] words = requireInput(input).split("\\s+");
        if (words.length != 2) {
            throw LokiExceptions.invalidTaskNumber();
        }

        try {
            return Integer.parseInt(words[1]);
        } catch (NumberFormatException exception) {
            throw LokiExceptions.invalidTaskNumber();
        }
    }

    /**
     * Parses a task update command into its target number and replacement fields.
     *
     * @param input the raw update command.
     * @return the parsed update.
     * @throws LokiExceptions if the command is malformed.
     */
    public TaskUpdate parseUpdate(String input) throws LokiExceptions {
        String trimmedInput = requireInput(input);
        String keyword = parseKeyword(trimmedInput);
        if (!keyword.equals("update")) {
            throw LokiExceptions.unknownCommand();
        }

        String body = afterKeyword(trimmedInput, keyword);
        int numberSeparator = findWhitespace(body);
        if (numberSeparator < 0) {
            throw LokiExceptions.invalidUpdate();
        }

        int taskNumber = parseUpdateTaskNumber(body.substring(0, numberSeparator));
        String fields = body.substring(numberSeparator).trim();
        Matcher markerMatcher = UPDATE_MARKER_PATTERN.matcher(fields);
        List<MatchResult> markers = new ArrayList<>();
        while (markerMatcher.find()) {
            markers.add(markerMatcher.toMatchResult());
        }
        if (markers.isEmpty() || !fields.substring(0, markers.get(0).start()).isBlank()) {
            throw LokiExceptions.invalidUpdate();
        }
        if (containsUnknownUpdateMarker(fields)) {
            throw LokiExceptions.invalidUpdate();
        }

        Optional<String> title = Optional.empty();
        Optional<LocalDateTime> deadline = Optional.empty();
        Optional<LocalDateTime> eventStart = Optional.empty();
        Optional<LocalDateTime> eventEnd = Optional.empty();
        Set<String> seenFields = new HashSet<>();
        for (int index = 0; index < markers.size(); index++) {
            MatchResult marker = markers.get(index);
            String fieldName = marker.group(1).toLowerCase(Locale.ROOT);
            if (!seenFields.add(fieldName)) {
                throw LokiExceptions.invalidUpdate();
            }

            int valueStart = marker.end();
            int valueEnd = index + 1 < markers.size() ? markers.get(index + 1).start() : fields.length();
            String value = fields.substring(valueStart, valueEnd).trim();
            if (value.isEmpty()) {
                throw LokiExceptions.invalidUpdate();
            }

            try {
                switch (fieldName) {
                    case "title" -> {
                        validateUpdateTitle(value);
                        title = Optional.of(value);
                    }
                    case "by" -> deadline = Optional.of(DateTimeParser.parseUserInput(value));
                    case "from" -> eventStart = Optional.of(DateTimeParser.parseUserInput(value));
                    case "to" -> eventEnd = Optional.of(DateTimeParser.parseUserInput(value));
                    default -> throw new IllegalStateException("Unrecognized update field");
                }
            } catch (IllegalArgumentException exception) {
                throw LokiExceptions.invalidUpdate();
            }
        }

        return new TaskUpdate(taskNumber, title, deadline, eventStart, eventEnd);
    }

    /**
     * Parses the title in a to-do command.
     *
     * @param input the raw task command
     * @param keyword the already-parsed command keyword
     * @return the parsed to-do task
     * @throws LokiExceptions if the title is missing
     */
    private Task parseToDo(String input, String keyword) throws LokiExceptions {
        String title = afterKeyword(input, keyword);
        if (title.isEmpty()) {
            throw LokiExceptions.invalidToDo();
        }
        return new ToDo(title);
    }

    /**
     * Parses the title and due date in a deadline command.
     *
     * @param input the raw task command
     * @param keyword the already-parsed command keyword
     * @return the parsed deadline task
     * @throws LokiExceptions if the command or due date is malformed
     */
    private Task parseDeadline(String input, String keyword) throws LokiExceptions {
        String body = afterKeyword(input, keyword);
        String lowerBody = body.toLowerCase(Locale.ROOT);
        int byIndex = lowerBody.indexOf(DEADLINE_MARKER);
        if (byIndex < 0) {
            throw LokiExceptions.invalidDeadline();
        }

        String title = body.substring(0, byIndex).trim();
        String due = body.substring(byIndex + DEADLINE_MARKER.length()).trim();
        if (title.isEmpty() || due.isEmpty()) {
            throw LokiExceptions.invalidDeadline();
        }

        try {
            return new Deadline(title, DateTimeParser.parseUserInput(due));
        } catch (IllegalArgumentException exception) {
            throw LokiExceptions.invalidDeadline();
        }
    }

    /**
     * Parses the title, start time, and end time in an event command.
     *
     * @param input the raw task command
     * @param keyword the already-parsed command keyword
     * @return the parsed event task
     * @throws LokiExceptions if the command or either time is malformed
     */
    private Task parseEvent(String input, String keyword) throws LokiExceptions {
        String body = afterKeyword(input, keyword);
        String lowerBody = body.toLowerCase(Locale.ROOT);
        int fromIndex = lowerBody.indexOf(EVENT_START_MARKER);
        int toIndex = lowerBody.indexOf(EVENT_END_MARKER, fromIndex + EVENT_START_MARKER.length());
        if (fromIndex < 0 || toIndex < 0) {
            throw LokiExceptions.invalidEvent();
        }

        String title = body.substring(0, fromIndex).trim();
        String from = body.substring(fromIndex + EVENT_START_MARKER.length(), toIndex).trim();
        String to = body.substring(toIndex + EVENT_END_MARKER.length()).trim();
        if (title.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw LokiExceptions.invalidEvent();
        }

        try {
            LocalDateTime start = DateTimeParser.parseUserInput(from);
            LocalDateTime end = DateTimeParser.parseUserInput(to);
            return new Event(title, start, end);
        } catch (IllegalArgumentException exception) {
            throw LokiExceptions.invalidEvent();
        }
    }

    /**
     * Returns the portion of a command after its keyword.
     *
     * @param input the trimmed command
     * @param keyword the command keyword
     * @return the trimmed command body
     */
    private String afterKeyword(String input, String keyword) {
        // Only internal parsing methods call this helper, after validating and trimming the input.
        assert input != null && input.equals(input.trim()) : "Command must already be trimmed";
        // The keyword must come from this command so its length safely locates the command body.
        assert keyword != null && !keyword.isEmpty()
                && keyword.equals(input.split("\\s+")[0].toLowerCase(Locale.ROOT))
                : "Keyword must match the normalized first word of the command";
        return input.substring(keyword.length()).trim();
    }

    /**
     * Parses the task number at the start of an update command.
     *
     * @param value the task number text.
     * @return the parsed task number.
     * @throws LokiExceptions if the value is not an integer.
     */
    private int parseUpdateTaskNumber(String value) throws LokiExceptions {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw LokiExceptions.invalidUpdate();
        }
    }

    /**
     * Returns the index of the first whitespace character in a string.
     *
     * @param value the string to inspect.
     * @return the first whitespace index, or {@code -1} if there is none.
     */
    private int findWhitespace(String value) {
        for (int index = 0; index < value.length(); index++) {
            if (Character.isWhitespace(value.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Checks for slash-prefixed words that are not supported update markers.
     *
     * @param fields the update field text.
     * @return true if an unknown marker is present.
     */
    private boolean containsUnknownUpdateMarker(String fields) {
        Matcher unknownMarkerMatcher = UNKNOWN_UPDATE_MARKER_PATTERN.matcher(fields);
        while (unknownMarkerMatcher.find()) {
            String marker = unknownMarkerMatcher.group().toLowerCase(Locale.ROOT);
            if (!marker.equals("/title") && !marker.equals("/by")
                    && !marker.equals("/from") && !marker.equals("/to")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates a title supplied in an update command.
     *
     * @param title the title to validate.
     * @throws IllegalArgumentException if the title violates storage restrictions.
     */
    private void validateUpdateTitle(String title) {
        if (title.isBlank() || title.indexOf('|') >= 0 || title.indexOf('\n') >= 0
                || title.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Invalid update title");
        }
    }

    /**
     * Validates and trims raw command input.
     *
     * @param input the raw command
     * @return the trimmed command
     * @throws LokiExceptions if the command is null or blank
     */
    private String requireInput(String input) throws LokiExceptions {
        if (input == null || input.isBlank()) {
            throw LokiExceptions.emptyInput();
        }
        return input.trim();
    }
}
