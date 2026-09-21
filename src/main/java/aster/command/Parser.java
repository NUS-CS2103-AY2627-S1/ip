package aster.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import aster.exception.AsterException;
import aster.task.Deadline;
import aster.task.Event;
import aster.task.Task;

/**
 * Parses user-entered command text into structured command data.
 */
public final class Parser {
    private static final List<DateTimeFormatter> ONDATE_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    );

    private Parser() {
        // utility class
    }

    /**
     * Parses a full command line into command type and argument text.
     *
     * @param input full command line
     * @return parsed command
     * @throws AsterException when the command is empty or unknown
     */
    public static ParsedCommand parse(String input) throws AsterException {
        if (input == null || input.trim().isEmpty()) {
            throw new AsterException("No command was entered.",
                    "Enter a command such as: todo read a book");
        }

        String trimmed = input.trim();
        Command command = Command.fromInput(trimmed);
        if (command == null) {
            throw new AsterException("I don't recognise that command.",
                    "Use todo, deadline, event, list, ondate, mark, unmark, delete, find, tag, or bye.");
        }

        String keyword = command.getKeyword();
        String arguments = trimmed.equals(keyword) ? "" : trimmed.substring(keyword.length()).trim();
        return new ParsedCommand(command, arguments);
    }

    /**
     * Validates and extracts a to-do description.
     *
     * @param arguments text after the todo keyword
     * @return description text
     * @throws AsterException when description is missing
     */
    public static String parseTodoDescription(String arguments) throws AsterException {
        if (arguments.isEmpty()) {
            throw new AsterException("A to-do needs a description.", "Enter: todo read a book");
        }
        return arguments;
    }

    /**
     * Validates and extracts deadline details.
     *
     * @param arguments text after the deadline keyword
     * @return parsed deadline details
     * @throws AsterException when description or /by value is missing
     */
    public static DeadlineDetails parseDeadlineDetails(String arguments) throws AsterException {
        int byMarkerIndex = findUniqueMarker(arguments, "/by", "deadline");
        if (byMarkerIndex < 0) {
            throw new AsterException("A deadline needs a /by date or time.",
                    "Enter: deadline return book /by 2019-12-02");
        }

        String description = arguments.substring(0, byMarkerIndex).trim();
        String by = arguments.substring(byMarkerIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new AsterException("The deadline description is missing.",
                    "Enter: deadline return book /by 2019-12-02");
        }
        if (by.isEmpty()) {
            throw new AsterException("The deadline date or time is missing.",
                    "Add a value after /by, for example: deadline return book /by 2019-12-02");
        }
        if (!Deadline.isParseable(by)) {
            throw new AsterException("The deadline date or time is not recognised.",
                    "Enter a date such as: deadline return book /by 2019-12-02");
        }
        return new DeadlineDetails(description, by);
    }

    /**
     * Validates and extracts event details.
     *
     * @param arguments text after the event keyword
     * @return parsed event details
     * @throws AsterException when required fields are missing or malformed
     */
    public static EventDetails parseEventDetails(String arguments) throws AsterException {
        int fromMarkerIndex = findUniqueMarker(arguments, "/from", "event");
        int toMarkerIndex = findUniqueMarker(arguments, "/to", "event");
        if (fromMarkerIndex < 0) {
            throw new AsterException("An event needs a /from start time.",
                    "Enter: event project meeting /from Mon 2pm /to 4pm");
        }
        if (toMarkerIndex < 0) {
            throw new AsterException("An event needs a /to end time.",
                    "Enter: event project meeting /from Mon 2pm /to 4pm");
        }
        if (toMarkerIndex < fromMarkerIndex) {
            throw new AsterException("The /from time must come before the /to time.",
                    "Enter: event project meeting /from Mon 2pm /to 4pm");
        }

        String description = arguments.substring(0, fromMarkerIndex).trim();
        String from = arguments.substring(fromMarkerIndex + "/from".length(), toMarkerIndex).trim();
        String to = arguments.substring(toMarkerIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new AsterException("The event description is missing.",
                    "Enter: event project meeting /from Mon 2pm /to 4pm");
        }
        if (from.isEmpty()) {
            throw new AsterException("The event start time is missing.", "Add a value after /from.");
        }
        if (to.isEmpty()) {
            throw new AsterException("The event end time is missing.", "Add a value after /to.");
        }
        validateEventDateOrder(from, to);
        return new EventDetails(description, from, to);
    }

    /**
     * Validates and parses an ondate argument into a date.
     *
     * @param arguments text after the ondate keyword
     * @return parsed date
     * @throws AsterException when date is missing or invalid
     */
    public static LocalDate parseOnDate(String arguments) throws AsterException {
        if (arguments.isEmpty()) {
            throw new AsterException("A date is required.", "Enter: ondate 2019-12-02");
        }

        return ONDATE_FORMATTERS.stream()
                .map(formatter -> tryParseOnDate(arguments, formatter))
                .flatMap(Optional::stream)
                .findFirst()
                .orElseThrow(() -> new AsterException("The date must be in yyyy-mm-dd format.",
                        "Enter: ondate 2019-12-02"));
    }

    /**
     * Validates and parses a one-based task number.
     *
     * @param numberText user-entered number text
     * @param taskCount number of tasks in the list
     * @param commandName command name used in recovery messages
     * @return parsed one-based task number
     * @throws AsterException when number is missing, malformed, or out of range
     */
    public static int parseTaskNumber(String numberText, int taskCount, String commandName) throws AsterException {
        if (taskCount == 0) {
            throw new AsterException("There are no tasks to " + commandName + ".",
                    "Add a task first, for example: todo read a book");
        }

        if (numberText.isEmpty()) {
            throw new AsterException("A task number is required.", "Enter: " + commandName + " 1");
        }
        String[] numberParts = numberText.trim().split("\\s+");
        if (numberParts.length > 1) {
            throw new AsterException("Only one task number is allowed.",
                    "Enter: " + commandName + " 1");
        }
        String normalizedNumberText = numberParts[0];

        int taskNumber = 0;
        for (int i = 0; i < normalizedNumberText.length(); i++) {
            char character = normalizedNumberText.charAt(i);
            if (!Character.isDigit(character)) {
                throw new AsterException("The task number must contain digits only.",
                        "Enter: " + commandName + " 1");
            }
            if (taskNumber > (Integer.MAX_VALUE - (character - '0')) / 10) {
                throw new AsterException("That task number is not in the list.",
                        "Enter a number from 1 to " + taskCount + ".");
            }
            taskNumber = taskNumber * 10 + (character - '0');
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new AsterException("That task number is not in the list.",
                    "Enter a number from 1 to " + taskCount + ".");
        }
        return taskNumber;
    }

    /**
     * Finds the index of a whitespace-delimited marker within the given text.
     *
     * @param details text to search
     * @param marker marker token to find, for example {@code "/by"}
     * @return index of the marker, or {@code -1} when it is absent or not whitespace-delimited
     */
    private static int findMarker(String details, String marker) {
        int searchFrom = 0;
        while (searchFrom < details.length()) {
            int markerIndex = details.indexOf(marker, searchFrom);
            if (markerIndex < 0) {
                return -1;
            }
            int markerEndIndex = markerIndex + marker.length();
            boolean hasWhitespaceBefore = markerIndex == 0 || Character.isWhitespace(details.charAt(markerIndex - 1));
            boolean hasWhitespaceAfter = markerEndIndex == details.length()
                    || Character.isWhitespace(details.charAt(markerEndIndex));
            if (hasWhitespaceBefore && hasWhitespaceAfter) {
                return markerIndex;
            }
            searchFrom = markerIndex + marker.length();
        }
        return -1;
    }

    /**
     * Finds a unique whitespace-delimited marker in command details.
     *
     * @param details text to search
     * @param marker marker token to find
     * @param commandName command keyword used in recovery messages
     * @return index of the marker, or {@code -1} when no delimited marker is present
     * @throws AsterException when the marker appears more than once
     */
    private static int findUniqueMarker(String details, String marker, String commandName) throws AsterException {
        int markerCount = countDelimitedMarkers(details, marker);
        if (markerCount == 0) {
            return -1;
        }
        if (markerCount > 1) {
            throw new AsterException("The " + marker + " marker should only appear once.",
                    "Use exactly one " + marker + " in: " + commandName + " ... " + marker + " ...");
        }
        return findMarker(details, marker);
    }

    /**
     * Validates that a parseable event start date/time is before its parseable end date/time.
     *
     * @param from raw event start text
     * @param to raw event end text
     * @throws AsterException when both values are parseable and the start is not earlier than the end
     */
    private static void validateEventDateOrder(String from, String to) throws AsterException {
        Optional<LocalDateTime> parsedStart = Event.tryParseDateTime(from);
        Optional<LocalDateTime> parsedEnd = Event.tryParseDateTime(to);
        if (parsedStart.isEmpty() || parsedEnd.isEmpty()) {
            return;
        }
        if (!parsedStart.get().isBefore(parsedEnd.get())) {
            throw new AsterException("The event start date/time must be earlier than the end date/time.",
                    "Use: event project meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00");
        }
    }

    /**
     * Counts the number of times a marker appears as a whitespace-delimited token.
     *
     * @param details text to inspect
     * @param marker marker token to count
     * @return number of valid marker occurrences
     */
    private static int countDelimitedMarkers(String details, String marker) {
        int count = 0;
        int searchFrom = 0;
        while (searchFrom < details.length()) {
            int markerIndex = details.indexOf(marker, searchFrom);
            if (markerIndex < 0) {
                break;
            }
            int markerEndIndex = markerIndex + marker.length();
            boolean hasWhitespaceBefore = markerIndex == 0 || Character.isWhitespace(details.charAt(markerIndex - 1));
            boolean hasWhitespaceAfter = markerEndIndex == details.length()
                    || Character.isWhitespace(details.charAt(markerEndIndex));
            if (hasWhitespaceBefore && hasWhitespaceAfter) {
                count++;
            }
            searchFrom = markerIndex + marker.length();
        }
        return count;
    }

    /**
     * Tries to parse an ondate value with a single formatter.
     *
     * @param arguments text to parse
     * @param formatter formatter to try
     * @return parsed date when the formatter matches, otherwise {@link Optional#empty()}
     */
    private static Optional<LocalDate> tryParseOnDate(String arguments, DateTimeFormatter formatter) {
        try {
            return Optional.of(LocalDate.parse(arguments, formatter));
        } catch (DateTimeParseException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Validates and extracts a find keyword.
     *
     * @param arguments text after the find keyword
     * @return trimmed keyword text
     * @throws AsterException when the keyword is missing
     */
    public static String parseFindKeyword(String arguments) throws AsterException {
        if (arguments.isEmpty()) {
            throw new AsterException("A find command needs a keyword.", "Enter: find book");
        }
        return arguments.trim();
    }

    /**
     * Validates and extracts a tag command's task number and tag.
     *
     * @param arguments text after the tag keyword
     * @return parsed tag details
     * @throws AsterException when the task number or tag is missing, or the tag format is invalid
     */
    public static TagDetails parseTagDetails(String arguments) throws AsterException {
        if (arguments.isEmpty()) {
            throw new AsterException("A tag command needs a task number and tag.", "Enter: tag 1 #fun");
        }

        String[] parts = arguments.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new AsterException("A tag value is required.", "Enter: tag 1 #fun");
        }

        String tag = parts[1].trim();
        if (!Task.isValidTag(tag)) {
            throw new AsterException("Tags must start with # and use letters, digits, - or _.",
                    "Enter: tag 1 #fun");
        }
        return new TagDetails(parts[0].trim(), tag);
    }

    /**
     * Immutable parsed command data.
     */
    public static final class ParsedCommand {
        private final Command command;
        private final String arguments;

        private ParsedCommand(Command command, String arguments) {
            this.command = command;
            this.arguments = arguments;
        }

        public Command getCommand() {
            return command;
        }

        public String getArguments() {
            return arguments;
        }
    }

    /**
     * Immutable deadline input data.
     */
    public static final class DeadlineDetails {
        private final String description;
        private final String by;

        private DeadlineDetails(String description, String by) {
            this.description = description;
            this.by = by;
        }

        public String getDescription() {
            return description;
        }

        public String getBy() {
            return by;
        }
    }

    /**
     * Immutable event input data.
     */
    public static final class EventDetails {
        private final String description;
        private final String from;
        private final String to;

        private EventDetails(String description, String from, String to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }

        public String getDescription() {
            return description;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }

    /**
     * Immutable tag-command input data.
     */
    public static final class TagDetails {
        private final String taskNumberText;
        private final String tag;

        private TagDetails(String taskNumberText, String tag) {
            this.taskNumberText = taskNumberText;
            this.tag = tag;
        }

        public String getTaskNumberText() {
            return taskNumberText;
        }

        public String getTag() {
            return tag;
        }
    }
}
