package swell.parser;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swell.exception.SwellException;
import swell.task.Deadline;
import swell.task.Event;
import swell.task.Task;
import swell.task.TaskDateTime;
import swell.task.Todo;

/**
 * Parses user commands into task objects and command arguments.
 */
public class Parser {
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String FIND_COMMAND = "find";
    private static final String FIND_TAG_COMMAND = "findtag";
    private static final String BY_PREFIX = "/by";
    private static final String FROM_PREFIX = "/from";
    private static final String TO_PREFIX = "/to";
    private static final String DEADLINE_SEPARATOR = "\\s+/by\\s+";
    private static final String EVENT_FROM_SEPARATOR = "\\s+/from\\s+";
    private static final String EVENT_TO_SEPARATOR = "\\s+/to\\s+";
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("[1-9]\\d*");
    private static final Pattern TAG_KEYWORD_PATTERN = Pattern.compile("#?[A-Za-z0-9][A-Za-z0-9_-]*");
    private static final Pattern BY_PREFIX_PATTERN = Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern FROM_PREFIX_PATTERN = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern TO_PREFIX_PATTERN = Pattern.compile("(?<!\\S)/to(?!\\S)");
    private static final Pattern TAG_PATTERN = Pattern.compile("(?<!\\S)#([A-Za-z0-9][A-Za-z0-9_-]*)");
    private static final String TODO_FORMAT_ERROR =
            "A todo needs cargo to carry. Try: todo read book";
    private static final String DEADLINE_FORMAT_ERROR =
            "A deadline needs a task and a /by date. Try: deadline return book /by 2019-10-15 1800";
    private static final String DEADLINE_DATE_ERROR =
            "That date drifted off course. Use yyyy-mm-dd or yyyy-mm-dd HHmm, "
                    + "like: deadline return book /by 2019-10-15 1800";
    private static final String EVENT_FORMAT_ERROR =
            "An event needs a task, /from, and /to so I can plot the route. "
                    + "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600";
    private static final String EVENT_DATE_ERROR =
            "Those event times drifted off course. Use yyyy-mm-dd or yyyy-mm-dd HHmm, "
                    + "like: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600";
    private static final String FIND_FORMAT_ERROR =
            "Give me a keyword to scan the waters. Try: find book";
    private static final String FIND_TAG_FORMAT_ERROR =
            "Give me a tag to follow. Try: findtag followup";
    private static final String TASK_NUMBER_FORMAT_ERROR =
            "Point me to one positive task number. Try: %s 1";

    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Returns the first word of the command.
     *
     * @param command user command to read.
     * @return command word.
     */
    public static String getCommandWord(String command) {
        return command.split("\\s+", 2)[0];
    }

    /**
     * Returns the task described by a todo, deadline, or event command.
     *
     * @param command user command to parse.
     * @return task described by the command.
     * @throws SwellException if the command does not describe a valid task.
     */
    public static Task getTask(String command) throws SwellException {
        String commandWord = getCommandWord(command);
        switch (commandWord) {
            case TODO_COMMAND:
                return createTodo(command);
            case DEADLINE_COMMAND:
                return createDeadline(command);
            case EVENT_COMMAND:
                return createEvent(command);
            default:
                throw new SwellException("That task type is not on my map yet. Try todo, deadline, or event.");
        }
    }

    /**
     * Returns the task number from commands such as mark 1 or delete 1.
     *
     * @param command user command to parse.
     * @param action command action used in error messages.
     * @return task number given in the command.
     * @throws SwellException if the command does not contain a valid task number.
     */
    public static int getTaskNumber(String command, String action) throws SwellException {
        String[] commandParts = command.split("\\s+", 2);
        if (commandParts.length < 2) {
            throw new SwellException(getTaskNumberFormatError(action));
        }

        String taskNumberText = commandParts[1].trim();
        if (!POSITIVE_INTEGER_PATTERN.matcher(taskNumberText).matches()) {
            throw new SwellException(getTaskNumberFormatError(action));
        }
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new SwellException(getTaskNumberFormatError(action));
        }
    }

    /**
     * Returns the keyword used by a find command.
     *
     * @param command user command to parse.
     * @return keyword to search for.
     * @throws SwellException if the find command has no keyword.
     */
    public static String getFindKeyword(String command) throws SwellException {
        return getRequiredCommandBody(command, FIND_COMMAND, FIND_FORMAT_ERROR);
    }

    /**
     * Returns the tag used by a findtag command.
     *
     * @param command user command to parse.
     * @return tag to search for.
     * @throws SwellException if the findtag command has no tag.
     */
    public static String getTagKeyword(String command) throws SwellException {
        String tag = getRequiredCommandBody(command, FIND_TAG_COMMAND, FIND_TAG_FORMAT_ERROR);
        if (!TAG_KEYWORD_PATTERN.matcher(tag).matches()) {
            throw new SwellException(FIND_TAG_FORMAT_ERROR);
        }
        String tagWithoutPrefix = tag.startsWith("#") ? tag.substring(1) : tag;
        if (tagWithoutPrefix.isEmpty()) {
            throw new SwellException(FIND_TAG_FORMAT_ERROR);
        }
        return tagWithoutPrefix;
    }

    private static Task createTodo(String command) throws SwellException {
        String body = getRequiredCommandBody(command, TODO_COMMAND, TODO_FORMAT_ERROR);
        ParsedTaskDetails details = parseTaskDetails(body, TODO_FORMAT_ERROR);
        return new Todo(details.description, details.tags);
    }

    private static Task createDeadline(String command) throws SwellException {
        String body = getCommandBody(command, DEADLINE_COMMAND);
        requireSinglePrefix(body, BY_PREFIX, BY_PREFIX_PATTERN, DEADLINE_FORMAT_ERROR);
        String[] deadlineParts = body.split(DEADLINE_SEPARATOR, 2);
        if (deadlineParts.length < 2) {
            throw new SwellException(DEADLINE_FORMAT_ERROR);
        }

        ParsedTaskDetails details = parseTaskDetails(deadlineParts[0], DEADLINE_FORMAT_ERROR);
        String by = deadlineParts[1].trim();
        requireNonEmptyFields(DEADLINE_FORMAT_ERROR, by);

        try {
            return new Deadline(details.description, TaskDateTime.parse(by), details.tags);
        } catch (DateTimeParseException e) {
            throw new SwellException(DEADLINE_DATE_ERROR);
        }
    }

    private static Task createEvent(String command) throws SwellException {
        String body = getCommandBody(command, EVENT_COMMAND);
        requireSinglePrefix(body, FROM_PREFIX, FROM_PREFIX_PATTERN, EVENT_FORMAT_ERROR);
        requireSinglePrefix(body, TO_PREFIX, TO_PREFIX_PATTERN, EVENT_FORMAT_ERROR);
        String[] eventParts = body.split(EVENT_FROM_SEPARATOR, 2);
        if (eventParts.length < 2) {
            throw new SwellException(EVENT_FORMAT_ERROR);
        }

        ParsedTaskDetails details = parseTaskDetails(eventParts[0], EVENT_FORMAT_ERROR);
        String[] timeParts = eventParts[1].split(EVENT_TO_SEPARATOR, 2);
        if (timeParts.length < 2) {
            throw new SwellException(EVENT_FORMAT_ERROR);
        }

        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        requireNonEmptyFields(EVENT_FORMAT_ERROR, from, to);
        try {
            return new Event(details.description, TaskDateTime.parse(from),
                    TaskDateTime.parse(to), details.tags);
        } catch (DateTimeParseException e) {
            throw new SwellException(EVENT_DATE_ERROR);
        }
    }

    private static String getRequiredCommandBody(String command, String commandWord,
            String errorMessage) throws SwellException {
        String body = getCommandBody(command, commandWord);
        if (body.isEmpty()) {
            throw new SwellException(errorMessage);
        }
        return body;
    }

    private static void requireNonEmptyFields(String errorMessage, String... fields)
            throws SwellException {
        for (String field : fields) {
            if (field.isEmpty()) {
                throw new SwellException(errorMessage);
            }
        }
    }

    private static String getCommandBody(String command, String commandWord) {
        return command.substring(commandWord.length()).trim();
    }

    private static void requireSinglePrefix(String text, String prefix, Pattern prefixPattern,
            String errorMessage) throws SwellException {
        int count = countMatches(prefixPattern, text);
        if (count != 1) {
            throw new SwellException(errorMessage + " Use " + prefix + " exactly once.");
        }
    }

    private static ParsedTaskDetails parseTaskDetails(String text, String errorMessage)
            throws SwellException {
        String description = TAG_PATTERN.matcher(text).replaceAll("").trim().replaceAll("\\s+", " ");
        if (description.isEmpty()) {
            throw new SwellException(errorMessage);
        }
        return new ParsedTaskDetails(description, extractTags(text));
    }

    private static ArrayList<String> extractTags(String text) {
        ArrayList<String> tags = new ArrayList<>();
        Matcher matcher = TAG_PATTERN.matcher(text);
        while (matcher.find()) {
            String tag = matcher.group(1);
            if (!containsTag(tags, tag)) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private static boolean containsTag(ArrayList<String> tags, String tagToFind) {
        return tags.stream().anyMatch(tag -> tag.equalsIgnoreCase(tagToFind));
    }

    private static int countMatches(Pattern pattern, String text) {
        int count = 0;
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            count += 1;
        }
        return count;
    }

    private static String getTaskNumberFormatError(String action) {
        return String.format(TASK_NUMBER_FORMAT_ERROR, action);
    }

    private static class ParsedTaskDetails {
        private final String description;
        private final ArrayList<String> tags;

        private ParsedTaskDetails(String description, ArrayList<String> tags) {
            this.description = description;
            this.tags = tags;
        }
    }
}
