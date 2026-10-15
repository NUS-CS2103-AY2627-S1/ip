package gnaix;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gnaix.task.Deadline;
import gnaix.task.Event;
import gnaix.task.Task;
import gnaix.task.Todo;

/**
 * Parses user input into structured commands.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static final Pattern TRAILING_TAG_PATTERN =
            Pattern.compile("\\s+(#[A-Za-z0-9_-]+)$");

    private static final Pattern TAG_PATTERN =
            Pattern.compile("#[A-Za-z0-9_-]+");

    private static final String INVALID_COMMAND_MESSAGE =
            "I don't know what that means."
                    + System.lineSeparator()
                    + "Try a valid command.";
    private static final String INVALID_TASK_INDEX_MESSAGE =
            "Which task?"
                    + System.lineSeparator()
                    + "You'll need to give me a task number.";
    private static final String INVALID_DATE_MESSAGE =
            "That date doesn't work."
                    + System.lineSeparator()
                    + "Use yyyy-MM-dd.";
    private static final String INVALID_EVENT_TIME_MESSAGE =
            "That date doesn't work."
                    + System.lineSeparator()
                    + "Use yyyy-MM-dd HHmm.";

    /** Message shown when an event ends before it starts. */
    private static final String INVALID_EVENT_RANGE_MESSAGE =
            "That event's end time is before its start time."
                    + System.lineSeparator()
                    + "Use an end time at or after its start time.";
    private static final String INVALID_TAG_MESSAGE =
            "That tag format won't work."
                    + System.lineSeparator()
                    + "Try something like #school.";

    /**
     * Parses a full user command into a structured result.
     *
     * @param fullCommand Complete command entered by the user.
     * @return Parsed representation of the command or an error.
     */
    public static ParsedCommand parse(String fullCommand) {
        String trimmed = fullCommand.trim();
        if (trimmed.isEmpty()) {
            return ParsedCommand.error("You'll need to type a command.");
        }

        String[] parts = trimmed.split("\\s+", 2);
        Command command = Command.fromString(parts[0]);
        String args = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case BYE:
            case LIST:
                return parseNoArgumentCommand(command, args);
            case MARK:
            case UNMARK:
            case DELETE:
                return parseIndex(command, args);
            case TODO:
                return parseTodo(args);
            case DEADLINE:
                return parseDeadline(args);
            case EVENT:
                return parseEvent(args);
            case DATE:
                return parseDate(args);
            case FIND:
                return parseFind(args);
            case TAG:
                return parseTagSearch(args);
            default:
                return ParsedCommand.error(INVALID_COMMAND_MESSAGE);
        }
    }

    /**
     * Parses a command that does not accept arguments.
     *
     * @param command Command that must be entered on its own.
     * @param args Text supplied after the command word.
     * @return Parsed command, or an error if arguments were supplied.
     */
    private static ParsedCommand parseNoArgumentCommand(
            Command command, String args) {
        if (!args.isEmpty()) {
            String commandWord = command.name().toLowerCase(Locale.ROOT);

            return ParsedCommand.error(
                    "The " + commandWord + " command doesn't take arguments."
                            + System.lineSeparator()
                            + "Try " + commandWord + " on its own.");
        }

        return ParsedCommand.of(command);
    }

    /**
     * Parses a task index from the supplied command arguments.
     *
     * @param command Command containing the index.
     * @param args User-supplied index arguments.
     * @return Parsed command containing the task index or an error.
     */
    private static ParsedCommand parseIndex(Command command, String args) {
        try {
            return ParsedCommand.forIndex(command, Integer.parseInt(args.trim()));
        } catch (NumberFormatException e) {
            return ParsedCommand.error(INVALID_TASK_INDEX_MESSAGE);
        }
    }

    /**
     * Parses a todo task from the supplied arguments.
     *
     * @param args User-supplied todo description and optional tags.
     * @return Parsed command containing the todo task or an error.
     */
    private static ParsedCommand parseTodo(String args) {
        TaggedText taggedText = extractTrailingTags(args);

        if (taggedText.text().isEmpty()) {
            return ParsedCommand.error("That task needs a description.");
        }

        Todo todo = new Todo(taggedText.text());
        addTags(todo, taggedText.tags());

        return ParsedCommand.forTask(Command.TODO, todo);
    }

    /**
     * Parses a deadline task from the supplied arguments.
     *
     * @param args User-supplied deadline description, date, and optional tags.
     * @return Parsed command containing the deadline task or an error.
     */
    private static ParsedCommand parseDeadline(String args) {
        TaggedText taggedText = extractTrailingTags(args);
        String[] segments = taggedText.text().split(" /by ", 2);

        if (segments.length < 2) {
            return ParsedCommand.error(
                    "That deadline needs a description and a /by date.");
        }

        String info = segments[0].trim();
        String by = segments[1].trim();

        if (info.isEmpty() || by.isEmpty()) {
            return ParsedCommand.error(
                    "That deadline needs a description and a /by date.");
        }

        try {
            LocalDate doBy = LocalDate.parse(by, INPUT_DATE_FORMAT);
            Deadline deadline = new Deadline(info, doBy);
            addTags(deadline, taggedText.tags());

            return ParsedCommand.forTask(Command.DEADLINE, deadline);
        } catch (DateTimeParseException e) {
            return ParsedCommand.error(INVALID_DATE_MESSAGE);
        }
    }

    /**
     * Parses an event task from the supplied arguments.
     * The end time may equal the start time but must not precede it.
     *
     * @param args User-supplied event description, timings, and optional tags.
     * @return Parsed command containing the event task, or an error if its
     *         required fields, date-times, or time range are invalid.
     */
    private static ParsedCommand parseEvent(String args) {
        TaggedText taggedText = extractTrailingTags(args);
        String[] parts = taggedText.text().split(" /from ", 2);

        if (parts.length < 2) {
            return ParsedCommand.error(
                    "That event needs a description, /from time, and /to time.");
        }

        String info = parts[0].trim();
        String[] times = parts[1].split(" /to ", 2);

        if (info.isEmpty() || times.length < 2) {
            return ParsedCommand.error(
                    "That event needs a description, /from time, and /to time.");
        }

        String from = times[0].trim();
        String to = times[1].trim();

        if (from.isEmpty() || to.isEmpty()) {
            return ParsedCommand.error(
                    "That event needs a /from time and /to time.");
        }

        try {
            LocalDateTime fromDateTime =
                    LocalDateTime.parse(from, INPUT_DATE_TIME_FORMAT);
            LocalDateTime toDateTime =
                    LocalDateTime.parse(to, INPUT_DATE_TIME_FORMAT);

            if (toDateTime.isBefore(fromDateTime)) {
                return ParsedCommand.error(INVALID_EVENT_RANGE_MESSAGE);
            }

            Event event = new Event(info, fromDateTime, toDateTime);
            addTags(event, taggedText.tags());

            return ParsedCommand.forTask(Command.EVENT, event);
        } catch (DateTimeParseException e) {
            return ParsedCommand.error(INVALID_EVENT_TIME_MESSAGE);
        }
    }

    /**
     * Parses a date from the supplied arguments.
     *
     * @param args User-supplied date.
     * @return Parsed command containing the date or an error.
     */
    private static ParsedCommand parseDate(String args) {
        if (args.isEmpty()) {
            return ParsedCommand.error(INVALID_DATE_MESSAGE);
        }

        try {
            return ParsedCommand.forDate(
                    LocalDate.parse(args, INPUT_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            return ParsedCommand.error(INVALID_DATE_MESSAGE);
        }
    }

    /**
     * Parses a keyword used to search task descriptions.
     *
     * @param args User-supplied search keyword.
     * @return Parsed command containing the keyword or an error.
     */
    private static ParsedCommand parseFind(String args) {
        if (args.isEmpty()) {
            return ParsedCommand.error(
                    "Search for what? Give me a keyword.");
        }

        return ParsedCommand.forKeyword(args);
    }

    /**
     * Parses one or more tags used to search tasks.
     *
     * @param args User-supplied tags.
     * @return Parsed command containing the tags or an error.
     */
    private static ParsedCommand parseTagSearch(String args) {
        if (args.isEmpty()) {
            return ParsedCommand.error(INVALID_TAG_MESSAGE);
        }

        String[] tokens = args.split("\\s+");
        Set<String> tags = new LinkedHashSet<>();

        for (String token : tokens) {
            if (!TAG_PATTERN.matcher(token).matches()) {
                return ParsedCommand.error(INVALID_TAG_MESSAGE);
            }

            tags.add(normaliseTag(token));
        }

        return ParsedCommand.forTags(tags);
    }

    /**
     * Extracts valid tags from the end of task arguments.
     *
     * @param args Task arguments that may end with tags.
     * @return Task text and extracted tags.
     */
    private static TaggedText extractTrailingTags(String args) {
        String remaining = args.trim();
        Deque<String> tags = new ArrayDeque<>();

        while (!remaining.isEmpty()) {
            Matcher matcher = TRAILING_TAG_PATTERN.matcher(remaining);

            if (!matcher.find()) {
                break;
            }

            tags.addFirst(normaliseTag(matcher.group(1)));
            remaining = remaining.substring(0, matcher.start()).trim();
        }

        return new TaggedText(remaining, new LinkedHashSet<>(tags));
    }

    /**
     * Adds all parsed tags to a task.
     *
     * @param task Task receiving the tags.
     * @param tags Tags to add.
     */
    private static void addTags(Task task, Set<String> tags) {
        tags.forEach(task::addTag);
    }

    /**
     * Normalises a tag for storage and comparison.
     *
     * @param tag Tag including its leading '#'.
     * @return Lowercase tag without '#'.
     */
    private static String normaliseTag(String tag) {
        return tag.substring(1).toLowerCase(Locale.ROOT);
    }

    /**
     * Holds task text together with tags extracted from its end.
     *
     * @param text Task text with trailing tags removed.
     * @param tags Extracted tags.
     */
    private record TaggedText(String text, Set<String> tags) {
    }
}
