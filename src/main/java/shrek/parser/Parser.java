package shrek.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import shrek.CommandType;
import shrek.ShrekException;
import shrek.task.Deadline;
import shrek.task.Event;
import shrek.task.EventTime;
import shrek.task.Task;
import shrek.task.Todo;

/**
 * Deals with making sense of the user command.
 */
public class Parser {
    private static final String INVALID_TAG_MESSAGE =
            "OOPS!!! Tags must start with '#' and contain 1-10 letters, digits, hyphens, or underscores.";
    private static final String DUPLICATE_TAG_MESSAGE = "OOPS!!! Duplicate tags are not allowed.";
    private static final String TAG_LIMIT_MESSAGE = "OOPS!!! A task can have at most 3 tags.";

    /**
     * Represents a task description after extracting its trailing tags.
     *
     * @param description the description without tags.
     * @param tags        the normalized tags.
     */
    public record ParsedTaskArgs(String description, List<String> tags) {
    }

    /**
     * Trims a command and treats any run of whitespace as one separator.
     *
     * @param input the raw command entered by the user.
     * @return the normalized command.
     */
    public static String normalizeInput(String input) {
        assert input != null : "The parser requires a command string.";
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Determines the command type from the first word of the input.
     *
     * @param input the raw user input line.
     * @return the matching command type, or UNKNOWN if unrecognized.
     */
    public static CommandType parseCommandType(String input) {
        String normalizedInput = normalizeInput(input);
        if (normalizedInput.isEmpty()) {
            return CommandType.UNKNOWN;
        }
        String commandWord = normalizedInput.split(" ", 2)[0];
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Extracts everything after the first word of the input.
     *
     * @param input the raw user input line.
     * @return the text following the command word, trimmed.
     */
    public static String parseArgs(String input) {
        String normalizedInput = normalizeInput(input);
        if (normalizedInput.isEmpty()) {
            return "";
        }
        String commandWord = normalizedInput.split(" ", 2)[0];
        return normalizedInput.length() > commandWord.length()
                ? normalizedInput.substring(commandWord.length()).trim()
                : "";
    }

    /**
     * Parses a task number argument into a validated zero-based index.
     *
     * @param args the argument text following the command word.
     * @param command the command name, used in error messages.
     * @param taskCount the current number of tasks, for bounds checking.
     * @return the zero-based task index.
     * @throws ShrekException if the argument is missing, non-numeric, or out of range.
     */
    public static int parseTaskIndex(String args, String command, int taskCount) throws ShrekException {
        String normalizedArgs = normalizeInput(args);
        assert command != null && !command.isBlank() : "The command name must be available for errors.";
        assert taskCount >= 0 : "A task count cannot be negative.";
        if (normalizedArgs.isEmpty()) {
            throw new ShrekException("OOPS!!! Please specify which task number to " + command + ".");
        }
        if (!normalizedArgs.matches("\\d+")) {
            throw new ShrekException("OOPS!!! The task number must be a single valid number.");
        }
        int index;
        try {
            index = Integer.parseInt(normalizedArgs) - 1;
        } catch (NumberFormatException e) {
            throw new ShrekException("OOPS!!! The task number must be a valid number.");
        }
        if (index < 0 || index >= taskCount) {
            throw new ShrekException("OOPS!!! That task number doesn't exist. You have "
                    + taskCount + " task(s) in your list.");
        }
        // The range check above establishes the contract expected by TaskList.get/remove.
        assert index >= 0 && index < taskCount : "Parsed task index must be in range.";
        return index;
    }

    /**
     * Extracts a contiguous suffix of tags from task-creation arguments.
     *
     * @param args the full task-creation arguments.
     * @return the description and normalized trailing tags.
     * @throws ShrekException if a tag is invalid, misplaced, duplicated, or excessive.
     */
    public static ParsedTaskArgs parseTaskArgs(String args) throws ShrekException {
        assert args != null : "Task arguments must not be null.";
        String trimmedArgs = normalizeInput(args);
        if (trimmedArgs.isEmpty()) {
            return new ParsedTaskArgs("", List.of());
        }

        String[] tokens = trimmedArgs.split("\\s+");
        int firstTagIndex = -1;
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].startsWith("#")) {
                firstTagIndex = i;
                break;
            }
        }
        if (firstTagIndex == -1) {
            return new ParsedTaskArgs(trimmedArgs, List.of());
        }

        int firstTagPosition = findTokenStart(trimmedArgs, tokens, firstTagIndex);
        ArrayList<String> tags = parseTagTokens(tokens, firstTagIndex, true);
        String description = trimmedArgs.substring(0, firstTagPosition).trim();
        return new ParsedTaskArgs(description, tags);
    }

    /**
     * Parses a space-separated list of standalone command tags.
     *
     * @param args the tag arguments.
     * @return the normalized tags.
     * @throws ShrekException if a tag is invalid, duplicated, or excessive.
     */
    public static ArrayList<String> parseTags(String args) throws ShrekException {
        assert args != null : "Tag arguments must not be null.";
        String trimmedArgs = normalizeInput(args);
        if (trimmedArgs.isEmpty()) {
            throw new ShrekException("OOPS!!! Please specify at least one tag.");
        }
        String[] tokens = trimmedArgs.split("\\s+");
        return parseTagTokens(tokens, 0, false);
    }

    private static ArrayList<String> parseTagTokens(String[] tokens, int startIndex,
            boolean tagsMustBeAtEnd) throws ShrekException {
        ArrayList<String> tags = new ArrayList<>();
        for (int i = startIndex; i < tokens.length; i++) {
            String token = tokens[i];
            if (!Task.isValidTag(token)) {
                if (tagsMustBeAtEnd && !token.startsWith("#")) {
                    throw new ShrekException("OOPS!!! Tags must appear at the end of the task description.");
                }
                throw new ShrekException(INVALID_TAG_MESSAGE);
            }
            String normalizedTag = Task.normalizeTag(token);
            if (tags.contains(normalizedTag)) {
                throw new ShrekException(DUPLICATE_TAG_MESSAGE);
            }
            tags.add(normalizedTag);
        }
        if (tags.size() > Task.MAX_TAG_COUNT) {
            throw new ShrekException(TAG_LIMIT_MESSAGE);
        }
        return tags;
    }

    private static int findTokenStart(String input, String[] tokens, int tokenIndex) {
        int searchFrom = 0;
        for (int i = 0; i <= tokenIndex; i++) {
            int tokenStart = input.indexOf(tokens[i], searchFrom);
            if (i == tokenIndex) {
                return tokenStart;
            }
            searchFrom = tokenStart + tokens[i].length();
        }
        return -1;
    }

    /**
     * Parses a todo command's arguments into a Todo task.
     *
     * @param args the argument text following "todo".
     * @return the constructed Todo task.
     * @throws ShrekException if the description is empty.
     */
    public static Todo parseTodo(String args) throws ShrekException {
        ParsedTaskArgs parsedArgs = parseTaskArgs(args);
        if (parsedArgs.description().isEmpty()) {
            throw new ShrekException("OOPS!!! The description of a todo cannot be empty.");
        }
        return new Todo(parsedArgs.description(), parsedArgs.tags());
    }

    /**
     * Parses a deadline command's arguments into a Deadline task.
     *
     * @param args the argument text following "deadline".
     * @return the constructed Deadline task.
     * @throws ShrekException if the description, date is missing, or the date is malformed.
     */
    public static Deadline parseDeadline(String args) throws ShrekException {
        ParsedTaskArgs parsedArgs = parseTaskArgs(args);
        if (parsedArgs.description().isEmpty()) {
            throw new ShrekException("OOPS!!! The description of a deadline cannot be empty.");
        }
        int byMarkerCount = countOccurrences(parsedArgs.description(), "/by");
        if (byMarkerCount == 0) {
            throw new ShrekException("OOPS!!! A deadline needs a '/by' followed by the due date.");
        }
        if (byMarkerCount > 1) {
            throw new ShrekException("OOPS!!! A deadline can contain only one '/by' marker.");
        }
        String[] parts = parsedArgs.description().split("/by", 2);
        // The delimiter check guarantees two sections for the positive-limit split.
        assert parts.length == 2 : "A deadline must split into description and due date.";
        String description = parts[0].trim();
        String byText = parts[1].trim();
        if (description.isEmpty()) {
            throw new ShrekException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (byText.isEmpty()) {
            throw new ShrekException("OOPS!!! The due date of a deadline cannot be empty.");
        }
        LocalDate by;
        try {
            by = LocalDate.parse(byText);
        } catch (DateTimeParseException e) {
            throw new ShrekException(
                    "OOPS!!! Please enter the deadline date in yyyy-mm-dd format, e.g. 2019-10-15.");
        }
        return new Deadline(description, by, parsedArgs.tags());
    }

    /**
     * Parses an event command's arguments into an Event task.
     *
     * @param args the argument text following "event".
     * @return the constructed Event task.
     * @throws ShrekException if the description or either date/time is missing.
     */
    public static Event parseEvent(String args) throws ShrekException {
        ParsedTaskArgs parsedArgs = parseTaskArgs(args);
        if (parsedArgs.description().isEmpty()) {
            throw new ShrekException("OOPS!!! The description of an event cannot be empty.");
        }
        int fromMarkerCount = countOccurrences(parsedArgs.description(), "/from");
        int toMarkerCount = countOccurrences(parsedArgs.description(), "/to");
        if (fromMarkerCount == 0) {
            throw new ShrekException("OOPS!!! An event needs a '/from' followed by the start date/time.");
        }
        if (fromMarkerCount > 1) {
            throw new ShrekException("OOPS!!! An event can contain only one '/from' marker.");
        }
        String[] fromSplit = parsedArgs.description().split("/from", 2);
        // The delimiter check guarantees two sections for the positive-limit split.
        assert fromSplit.length == 2 : "An event must split at its start time.";
        String description = fromSplit[0].trim();
        if (description.isEmpty()) {
            throw new ShrekException("OOPS!!! The description of an event cannot be empty.");
        }
        if (toMarkerCount == 0) {
            throw new ShrekException("OOPS!!! An event needs a '/to' followed by the end date/time.");
        }
        if (toMarkerCount > 1) {
            throw new ShrekException("OOPS!!! An event can contain only one '/to' marker.");
        }
        String[] toSplit = fromSplit[1].split("/to", 2);
        // The delimiter check guarantees two sections for the positive-limit split.
        assert toSplit.length == 2 : "An event must split into start and end times.";
        String from = toSplit[0].trim();
        String to = toSplit[1].trim();
        if (from.isEmpty()) {
            throw new ShrekException("OOPS!!! The start date/time of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new ShrekException("OOPS!!! The end date/time of an event cannot be empty.");
        }
        EventTime fromTime = parseEventTime(from, "start");
        EventTime toTime = parseEventTime(to, "end");
        if (fromTime.hasDate() != toTime.hasDate()) {
            throw new ShrekException("OOPS!!! Event start and end must use the same date/time format.");
        }
        if (fromTime.compareTo(toTime) >= 0) {
            throw new ShrekException("OOPS!!! The event end must be later than its start.");
        }
        return new Event(description, fromTime.toStorageFormat(), toTime.toStorageFormat(), parsedArgs.tags());
    }

    private static EventTime parseEventTime(String value, String label) throws ShrekException {
        try {
            return EventTime.parse(value);
        } catch (IllegalArgumentException e) {
            throw new ShrekException("OOPS!!! Please enter a valid event " + label
                    + " time (e.g. 2pm, 14:00, or 2026-09-13T14:00).");
        }
    }

    private static int countOccurrences(String input, String marker) {
        int count = 0;
        int searchFrom = 0;
        int markerPosition;
        while ((markerPosition = input.indexOf(marker, searchFrom)) >= 0) {
            count++;
            searchFrom = markerPosition + marker.length();
        }
        return count;
    }
}
