package habpyduck.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import habpyduck.HabpyDuckException;
import habpyduck.command.AddCommand;
import habpyduck.command.Command;
import habpyduck.command.DeleteCommand;
import habpyduck.command.ExitCommand;
import habpyduck.command.FindCommand;
import habpyduck.command.FindTagCommand;
import habpyduck.command.ListCommand;
import habpyduck.command.MarkCommand;
import habpyduck.command.TagCommand;
import habpyduck.command.UnmarkCommand;
import habpyduck.command.UntagCommand;
import habpyduck.task.Deadline;
import habpyduck.task.Event;
import habpyduck.task.Task;
import habpyduck.task.Todo;

/**
 * Makes sense of raw command text entered by the user.
 */
public class Parser {
    private static final int MAX_COMMAND_WORD_SPLIT_PARTS = 2;
    private static final String DEADLINE_COMMAND_PREFIX = "deadline ";
    private static final String EVENT_COMMAND_PREFIX = "event ";
    private static final String FIND_COMMAND_PREFIX = "find ";
    private static final String FINDTAG_COMMAND_PREFIX = "findtag ";
    private static final Pattern BY_MARKER_PATTERN = Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern FROM_MARKER_PATTERN = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern TO_MARKER_PATTERN = Pattern.compile("(?<!\\S)/to(?!\\S)");
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final int TASK_NUMBER_INDEX = 0;
    private static final int FIRST_TAG_INDEX = 1;
    private static final int UNTAG_PART_COUNT = 2;
    private static final String TAG_COMMAND_PREFIX = "tag ";
    private static final String TODO_COMMAND_PREFIX = "todo ";
    private static final String UNTAG_COMMAND_PREFIX = "untag ";
    private static final String UNKNOWN_COMMAND_MESSAGE = "OH NO!!! I don't understand that command friend :(. "
            + "Try todo, deadline, event, list, mark, unmark, delete, find, tag, untag, or findtag!";

    /**
     * Finds the command type for the user's input.
     *
     * @param command the full command entered by the user.
     * @return the matching command type.
     */
    public CommandType getCommandType(String command) {
        String trimmedCommand = command.trim();
        String commandWord = trimmedCommand.split(" ", MAX_COMMAND_WORD_SPLIT_PARTS)[0];
        return CommandType.fromCommandWord(commandWord);
    }

    /**
     * Converts raw user input into a command object that can be executed.
     *
     * @param command the full command entered by the user.
     * @return the command represented by the input.
     * @throws HabpyDuckException if the command is unknown or missing required details.
     */
    public Command parse(String command) throws HabpyDuckException {
        switch (getCommandType(command)) {
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(parseTaskNumber(command, "mark"));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(command, "unmark"));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(command, "delete"));
            case FIND:
                return new FindCommand(parseFindKeyword(command));
            case FINDTAG:
                return new FindTagCommand(parseFindTag(command));
            case TAG:
                return parseTagCommand(command);
            case UNTAG:
                return parseUntagCommand(command);
            case TODO:
                return new AddCommand(new Todo(parseTodoDescription(command)));
            case DEADLINE:
                return new AddCommand(parseDeadline(command));
            case EVENT:
                return new AddCommand(parseEvent(command));
            case BYE:
                return new ExitCommand();
            case UNKNOWN:
                if (command.isBlank()) {
                    throw new HabpyDuckException(
                            "OH NO!!! I didn't catch a command, friend. Please type something for me.");
                }
                throw new HabpyDuckException(UNKNOWN_COMMAND_MESSAGE);
            default:
                throw new HabpyDuckException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /**
     * Converts deadline text entered by the user into a LocalDateTime.
     *
     * @param dateTimeText the date and time entered by the user.
     * @return the parsed date and time.
     * @throws HabpyDuckException if the deadline is not in d/M/yyyy or d/M/yyyy HHmm format.
     */
    public LocalDateTime parseUserDeadlineDateTime(String dateTimeText) throws HabpyDuckException {
        return parseUserDateTime(dateTimeText, "deadline");
    }

    /**
     * Converts event text entered by the user into a LocalDateTime.
     *
     * @param dateTimeText the date and time entered by the user.
     * @return the parsed date and time.
     * @throws HabpyDuckException if the event is not in d/M/yyyy or d/M/yyyy HHmm format.
     */
    public LocalDateTime parseUserEventDateTime(String dateTimeText) throws HabpyDuckException {
        return parseUserDateTime(dateTimeText, "event");
    }

    /**
     * Converts a user-entered deadline or event date into a LocalDateTime.
     *
     * @param dateTimeText the date and optional time entered by the user.
     * @param taskType the task type to use in an error message.
     * @return the parsed date and time.
     * @throws HabpyDuckException if the text is not in d/M/yyyy or d/M/yyyy HHmm format.
     */
    private LocalDateTime parseUserDateTime(String dateTimeText, String taskType) throws HabpyDuckException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException dateTimeError) {
            try {
                return LocalDate.parse(dateTimeText, INPUT_DATE_FORMAT).atStartOfDay();
            } catch (DateTimeParseException dateError) {
                throw new HabpyDuckException(
                        "OH NO!!! Please enter the " + taskType + " date in DD/MM/YYYY format, "
                                + "or include time like: 25/8/2026 1800");
            }
        }
    }

    /**
     * Converts a user-facing task number into an array index.
     *
     * @param command the full mark or unmark command.
     * @param commandWord the command word, such as mark, unmark, or delete.
     * @param taskCount the number of tasks currently stored.
     * @return the zero-based array index of the requested task.
     * @throws HabpyDuckException if the task number is missing or invalid.
     */
    public int parseTaskIndex(String command, String commandWord, int taskCount) throws HabpyDuckException {
        int taskNumber = parseTaskNumber(command, commandWord);
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new HabpyDuckException("OH NO!!! Task " + taskNumber + " does not exist in your list.");
        }
        return taskIndex;
    }

    /**
     * Converts the task number part of a command into an integer.
     *
     * @param command the full mark, unmark, or delete command.
     * @param commandWord the command word, such as mark, unmark, or delete.
     * @return the task number entered by the user.
     * @throws HabpyDuckException if the task number is missing or not a number.
     */
    private int parseTaskNumber(String command, String commandWord) throws HabpyDuckException {
        String taskNumberText = command.substring(commandWord.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new HabpyDuckException("OH NO!!! Please tell me which task to " + commandWord + ", like: "
                    + commandWord + " 2");
        }

        return parseTaskNumberText(taskNumberText, commandWord);
    }

    /**
     * Converts task number text into an integer.
     *
     * @param taskNumberText the task number text entered by the user.
     * @param commandWord the command word, such as mark, tag, or untag.
     * @return the task number entered by the user.
     * @throws HabpyDuckException if the task number is not a number.
     */
    private int parseTaskNumberText(String taskNumberText, String commandWord) throws HabpyDuckException {
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new HabpyDuckException("OH NO!!! Please use a number after " + commandWord + ", like: "
                    + commandWord + " 2");
        }
    }

    /**
     * Converts tag text into a normalized tag.
     *
     * @param tagText the tag text entered by the user.
     * @param exampleCommand an example command to include in the error message.
     * @return the normalized tag.
     * @throws HabpyDuckException if the tag is missing or has invalid characters.
     */
    private String parseSingleTag(String tagText, String exampleCommand) throws HabpyDuckException {
        String tag = Task.normalizeTag(requireText(tagText,
                "OH NO!!! Please tell me which tag to use, like: " + exampleCommand));
        if (!Task.isValidTag(tag)) {
            throw new HabpyDuckException("OH NO!!! Tags must start with # and use only letters, numbers, "
                    + "hyphens, or underscores. Try something like: " + exampleCommand);
        }
        return tag;
    }

    /**
     * Extracts the description from a todo command.
     *
     * @param command the full todo command.
     * @return the todo description.
     * @throws HabpyDuckException if the description is blank.
     */
    private String parseTodoDescription(String command) throws HabpyDuckException {
        String description = command.length() > TODO_COMMAND_PREFIX.length()
                ? command.substring(TODO_COMMAND_PREFIX.length()).trim()
                : "";
        return requireText(description,
                "OH NO!!! A todo needs a description, friend. Try something like: todo read book");
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command the full find command.
     * @return the keyword to search for.
     * @throws HabpyDuckException if the keyword is blank.
     */
    private String parseFindKeyword(String command) throws HabpyDuckException {
        String keyword = command.length() > FIND_COMMAND_PREFIX.length()
                ? command.substring(FIND_COMMAND_PREFIX.length()).trim()
                : "";
        return requireText(keyword,
                "OH NO!!! Please tell me what keyword to find, like: find book");
    }

    /**
     * Extracts and validates the tag from a findtag command.
     *
     * @param command the full findtag command.
     * @return the normalized tag to search for.
     * @throws HabpyDuckException if the tag is missing or invalid.
     */
    private String parseFindTag(String command) throws HabpyDuckException {
        String tag = command.length() > FINDTAG_COMMAND_PREFIX.length()
                ? command.substring(FINDTAG_COMMAND_PREFIX.length()).trim()
                : "";
        return parseSingleTag(tag, "findtag #fun");
    }

    /**
     * Creates a tag command from user input.
     *
     * @param command the full tag command.
     * @return the command represented by the input.
     * @throws HabpyDuckException if the task number or tags are missing or invalid.
     */
    private TagCommand parseTagCommand(String command) throws HabpyDuckException {
        String commandDetails = command.length() > TAG_COMMAND_PREFIX.length()
                ? command.substring(TAG_COMMAND_PREFIX.length()).trim()
                : "";
        String[] parts = commandDetails.split("\\s+");
        if (commandDetails.isBlank() || parts.length < 2) {
            throw new HabpyDuckException("OH NO!!! Please use this format for tags: tag TASK_NUMBER #TAG...");
        }

        int taskNumber = parseTaskNumberText(parts[TASK_NUMBER_INDEX], "tag");
        ArrayList<String> tags = new ArrayList<>();
        for (int i = FIRST_TAG_INDEX; i < parts.length; i++) {
            tags.add(parseSingleTag(parts[i], "tag 2 #fun"));
        }
        return new TagCommand(taskNumber, tags);
    }

    /**
     * Creates an untag command from user input.
     *
     * @param command the full untag command.
     * @return the command represented by the input.
     * @throws HabpyDuckException if the task number or tag is missing or invalid.
     */
    private UntagCommand parseUntagCommand(String command) throws HabpyDuckException {
        String commandDetails = command.length() > UNTAG_COMMAND_PREFIX.length()
                ? command.substring(UNTAG_COMMAND_PREFIX.length()).trim()
                : "";
        String[] parts = commandDetails.split("\\s+");
        if (commandDetails.isBlank() || parts.length != UNTAG_PART_COUNT) {
            throw new HabpyDuckException("OH NO!!! Please use this format for untagging: untag TASK_NUMBER #TAG");
        }

        int taskNumber = parseTaskNumberText(parts[TASK_NUMBER_INDEX], "untag");
        String tag = parseSingleTag(parts[FIRST_TAG_INDEX], "untag 2 #fun");
        return new UntagCommand(taskNumber, tag);
    }

    /**
     * Creates a deadline task from a deadline command.
     *
     * @param command the full deadline command.
     * @return the deadline task represented by the command.
     * @throws HabpyDuckException if the command is missing required parts.
     */
    private Deadline parseDeadline(String command) throws HabpyDuckException {
        String taskDetails = command.length() > DEADLINE_COMMAND_PREFIX.length()
                ? command.substring(DEADLINE_COMMAND_PREFIX.length())
                : "";
        ArrayList<MarkerMatch> byMarkers = findMarkers(taskDetails, BY_MARKER_PATTERN);
        if (byMarkers.isEmpty()) {
            throw new HabpyDuckException(
                    "OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY "
                            + "or DD/MM/YYYY HHmm :)");
        }
        if (byMarkers.size() > 1) {
            throw new HabpyDuckException(
                    "OH NO!!! A deadline should have only one /by marker, friend.");
        }

        MarkerMatch byMarker = byMarkers.get(0);
        String description = requireText(taskDetails.substring(0, byMarker.getStartIndex()).trim(),
                "OH NO!!! A deadline needs a description, friend. Try again!");
        String by = requireText(taskDetails.substring(byMarker.getEndIndex()).trim(),
                "OH NO!!! A deadline needs a date, friend. Try something like: 25/8/2026");
        return new Deadline(description, parseUserDeadlineDateTime(by));
    }

    /**
     * Creates an event task from an event command.
     *
     * @param command the full event command.
     * @return the event task represented by the command.
     * @throws HabpyDuckException if the command is missing required parts.
     */
    private Event parseEvent(String command) throws HabpyDuckException {
        String taskDetails = command.length() > EVENT_COMMAND_PREFIX.length()
                ? command.substring(EVENT_COMMAND_PREFIX.length())
                : "";
        ArrayList<MarkerMatch> fromMarkers = findMarkers(taskDetails, FROM_MARKER_PATTERN);
        ArrayList<MarkerMatch> toMarkers = findMarkers(taskDetails, TO_MARKER_PATTERN);
        if (fromMarkers.isEmpty() || toMarkers.isEmpty()) {
            throw new HabpyDuckException(
                    "OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)");
        }
        if (fromMarkers.size() > 1 || toMarkers.size() > 1) {
            throw new HabpyDuckException(
                    "OH NO!!! An event should have one /from marker and one /to marker, friend.");
        }

        MarkerMatch fromMarker = fromMarkers.get(0);
        MarkerMatch toMarker = toMarkers.get(0);
        if (fromMarker.getStartIndex() > toMarker.getStartIndex()) {
            throw new HabpyDuckException("OH NO!!! Please put /from before /to for events, friend.");
        }

        String description = requireText(taskDetails.substring(0, fromMarker.getStartIndex()).trim(),
                "OH NO!!! An event needs a description, friend. Try again!");
        String from = requireText(taskDetails.substring(fromMarker.getEndIndex(), toMarker.getStartIndex()).trim(),
                "OH NO!!! An event needs a start time, friend. Try again!");
        String to = requireText(taskDetails.substring(toMarker.getEndIndex()).trim(),
                "OH NO!!! An event needs an end time, friend. Try again!");
        LocalDateTime startDateTime = parseUserEventDateTime(from);
        LocalDateTime endDateTime = parseUserEventDateTime(to);
        if (!startDateTime.isBefore(endDateTime)) {
            throw new HabpyDuckException("OH NO!!! An event must end after it starts, friend.");
        }
        return new Event(description, startDateTime, endDateTime);
    }

    /**
     * Finds command marker tokens, such as /by, while allowing flexible surrounding spaces.
     *
     * @param text the command details to search.
     * @param markerPattern the marker token pattern.
     * @return the matching marker positions.
     */
    private ArrayList<MarkerMatch> findMarkers(String text, Pattern markerPattern) {
        Matcher matcher = markerPattern.matcher(text);
        ArrayList<MarkerMatch> markers = new ArrayList<>();
        while (matcher.find()) {
            markers.add(new MarkerMatch(matcher.start(), matcher.end()));
        }
        return markers;
    }

    /**
     * Checks that a required piece of user input is not blank.
     *
     * @param text the text to check.
     * @param errorMessage the message to show if the text is blank.
     * @return the text, if it is not blank.
     * @throws HabpyDuckException if the text is blank.
     */
    private String requireText(String text, String errorMessage) throws HabpyDuckException {
        if (text.isBlank()) {
            throw new HabpyDuckException(errorMessage);
        }
        return text;
    }

    /**
     * Records the start and end indexes of one command marker in the user's input.
     */
    private static class MarkerMatch {
        private final int startIndex;
        private final int endIndex;

        /**
         * Creates a marker position record.
         *
         * @param startIndex the index where the marker starts.
         * @param endIndex the index just after the marker.
         */
        MarkerMatch(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        /**
         * Returns where the marker starts.
         *
         * @return the start index.
         */
        int getStartIndex() {
            return startIndex;
        }

        /**
         * Returns the index just after the marker.
         *
         * @return the end index.
         */
        int getEndIndex() {
            return endIndex;
        }
    }
}
