package littler.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import littler.command.request.EditRequest;
import littler.command.request.IndicesAndValue;
import littler.command.sort.SortCriteria;
import littler.command.sort.SortOrder;
import littler.command.sort.SortRequest;
import littler.datetime.StringDateTimeConverter;
import littler.datetime.StringDateTimeConverter.ParsedDateTime;
import littler.exception.LittleRException;
import littler.task.Deadline;
import littler.task.Event;
import littler.task.Task;
import littler.task.Todo;

/**
 * Utility class that parses raw user input strings into usable application values and tasks.
 */
public final class Parser {
    private static final Pattern FIELD_PATTERN = buildFieldPattern();

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Parser() {}

    /**
     * Parses an edit command's target index and field updates from user input.
     *
     * @param input the raw user input containing the index and field updates
     * @param command the command keyword to be stripped from the front of the input
     * @return the parsed EditRequest
     * @throws LittleRException if the index is missing/invalid, or no fields were provided
     */
    public static EditRequest parseEdit(String input, Command command) throws LittleRException {
        IndexAndFields parsed = parseIndexAndFields(input, command);
        if (parsed.updates().isEmpty()) {
            throw new LittleRException(
                "Please provide at least one field to update, e.g. " + Task.NAME_DELIMITER + " <new name>.");
        }
        return new EditRequest(parsed.index(), parsed.updates());
    }

    /**
     * Parses a duplicate command's target index and optional field overrides from user input.
     * Unlike parseEdit(), no fields are required -- a plain "duplicate 2" is a valid exact clone.
     *
     * @param input the raw user input containing the index and optional field overrides
     * @param command the command keyword to be stripped from the front of the input
     * @return the parsed EditRequest (reused here to represent "index + field overrides")
     * @throws LittleRException if the index is missing or invalid
     */
    public static EditRequest parseDuplicate(String input, Command command) throws LittleRException {
        IndexAndFields parsed = parseIndexAndFields(input, command);
        return new EditRequest(parsed.index(), parsed.updates());
    }

    /**
     * Parses the target index and any field-delimiter/value pairs from an edit or duplicate
     * command's input, shared by both since they take the same "index + optional fields" shape.
     *
     * @param input the raw user input containing the index and optional field updates
     * @param command the command keyword to be stripped from the front of the input
     * @return the parsed index and field-updates map
     * @throws LittleRException if the index is missing or not a valid integer
     */
    private static IndexAndFields parseIndexAndFields(String input, Command command) throws LittleRException {
        String argsText = input.substring(command.getKeyword().length()).trim();
        String[] indexAndRest = argsText.split("\\s+", 2);

        int index;
        try {
            index = Integer.parseInt(indexAndRest[0]) - 1;
        } catch (NumberFormatException e) {
            throw new LittleRException("Please provide a valid task number.");
        }

        String fieldsText = indexAndRest.length > 1 ? indexAndRest[1] : "";
        Map<String, String> updates = new LinkedHashMap<>();
        Matcher matcher = FIELD_PATTERN.matcher(fieldsText);
        while (matcher.find()) {
            updates.put(matcher.group(1), matcher.group(2).trim());
        }
        return new IndexAndFields(index, updates);
    }

    /**
     * Holds the intermediate result of parsing an edit/duplicate command's arguments,
     * before deciding whether an empty field map is acceptable (parseEdit rejects it,
     * parseDuplicate allows it).
     */
    private record IndexAndFields(int index, Map<String, String> updates) {}

    /**
     * Builds a regex pattern to match task field delimiters and their corresponding values.
     *
     * @return the compiled regex pattern for matching task fields
     */
    private static Pattern buildFieldPattern() {
        String anyDelimiter = String.join("|",
            Pattern.quote(Task.NAME_DELIMITER),
            Pattern.quote(Deadline.INPUT_DELIMITER),
            Pattern.quote(Event.FROM_DELIMITER),
            Pattern.quote(Event.TO_DELIMITER));
        return Pattern.compile("(" + anyDelimiter + ")\\s+(.*?)(?=\\s*(?:" + anyDelimiter + ")|$)");
    }

    /**
     * Parses a sort command's criteria and order arguments from user input.
     *
     * @param input the raw user input containing the sort criteria and order
     * @param command the command keyword to be stripped from the front of the input
     * @return the parsed SortRequest
     * @throws LittleRException if the criteria or order is missing or not recognized
     */
    public static SortRequest parseSort(String input, Command command) throws LittleRException {
        String argsText = input.substring(command.getKeyword().length()).strip();
        String[] parts = argsText.split(
            Pattern.quote(SortRequest.BY_DELIMITER) + "|" + Pattern.quote(SortRequest.ORDER_DELIMITER));

        String usage = "Invalid sort format. \nUse: sort " + SortRequest.BY_DELIMITER + " <date|name|tag|priority> "
            + SortRequest.ORDER_DELIMITER + " <a|d>";
        if (parts.length < 3) {
            throw new LittleRException(usage);
        }

        SortCriteria criteria = SortCriteria.fromKeyword(parts[1].trim());
        SortOrder order = SortOrder.fromKeyword(parts[2].trim());
        if (criteria == null || order == null) {
            throw new LittleRException(usage);
        }
        return new SortRequest(criteria, order);
    }

    /**
     * Parses one or more leading task indices, followed by a single trailing value
     * (e.g. a tag or priority level) that applies to all of them.
     *
     * @param input the raw user input containing the indices and trailing value
     * @param command the command keyword to be stripped from the front of the input
     * @return the parsed indices and trailing value
     * @throws LittleRException if no indices are given, any index token is invalid,
     *     or no trailing value follows the indices
     */
    public static IndicesAndValue parseIndicesAndValue(String input, Command command) throws LittleRException {
        String[] tokens = input.substring(command.getKeyword().length()).trim().split("\\s+");
        Set<Integer> indices = new LinkedHashSet<>();
        int i = 0;
        while (i < tokens.length) {
            try {
                indices.add(Integer.parseInt(tokens[i]) - 1);
                i++;
            } catch (NumberFormatException e) {
                break;
            }
        }
        if (indices.isEmpty()) {
            throw new LittleRException("Please provide at least one task number.");
        }
        if (i >= tokens.length) {
            throw new LittleRException(command.getErrorMessage());
        }
        String value = String.join(" ", java.util.Arrays.copyOfRange(tokens, i, tokens.length));
        return new IndicesAndValue(new ArrayList<>(indices), value);
    }

    /**
     * Parses one or more whitespace-separated task indices from user input, converting each
     * to a 0-based index. Duplicate indices are removed, preserving the order first seen.
     *
     * @param input the raw user input containing the index arguments
     * @param command the command keyword to be stripped from the front of the input
     * @return the parsed list of 0-based indices
     * @throws LittleRException if no indices are given, or any token is not a valid integer
     */
    public static List<Integer> parseIndices(String input, Command command) throws LittleRException {
        String argsText = input.substring(command.getKeyword().length()).trim();
        if (argsText.isEmpty()) {
            throw new LittleRException("Please provide at least one task number.");
        }
        Set<Integer> indices = new LinkedHashSet<>();
        for (String token : argsText.split("\\s+")) {
            try {
                indices.add(Integer.parseInt(token) - 1);
            } catch (NumberFormatException e) {
                throw new LittleRException("'" + token + "' is not a valid task number.");
            }
        }
        return new ArrayList<>(indices);
    }

    /**
     * Parses a date argument from user input string into a {@link ParsedDateTime} instance.
     *
     * @param input the raw user input containing the date string
     * @param command the command keyword to be stripped from the front of the input
     * @return the parsed date/time object
     * @throws LittleRException if the date string is empty or does not match any accepted format
     */
    public static ParsedDateTime parseDate(String input, Command command) throws LittleRException {
        String dateString = input.substring(command.getKeyword().length()).trim();
        if (dateString.isEmpty()) {
            throw new LittleRException("Please provide a date, e.g. on 2-12-2019");
        }
        return StringDateTimeConverter.parse(dateString);
    }

    /**
     * Parses the search keyword argument from user input.
     *
     * @param input the user input containing the keyword
     * @param command the command keyword to be removed from the input
     * @throws LittleRException if no keyword was provided
     */
    public static String parseKeyword(String input, Command command) throws LittleRException {
        String keyword = input.substring(command.getKeyword().length()).trim();
        if (keyword.isEmpty()) {
            throw new LittleRException("Please provide a keyword to search for.");
        }
        return keyword;
    }

    /**
     * Parses the task description and optional date parameters
     * from user input and constructs a concrete {@link Task} instance.
     *
     * @param input the raw user input containing the task description and optional date parameters
     * @param type the type of task command being processed (TODO, DEADLINE, or EVENT)
     * @return the constructed concrete {@link Task} instance (Todo, Deadline, or Event)
     * @throws LittleRException if the task arguments are empty, missing required delimiters, or malformed
     */
    public static Task parseTask(String input, Command type) throws LittleRException {
        assert type == Command.DEADLINE || type == Command.EVENT || type == Command.TODO
            : "parseTask should only be called for task-creation commands";

        String taskText = input.substring(type.getKeyword().length()).strip();

        switch (type) {
            case DEADLINE:
                return parseDeadline(taskText);
            case EVENT:
                return parseEvent(taskText);
            case TODO:
                return parseTodo(taskText);
            default:
                throw new LittleRException("Unrecognized task type.");
        }
    }

    /**
     * Parses a deadline task from the provided task text.
     *
     * @param taskText the raw user input containing the task description and due date
     * @return the constructed {@link Deadline} instance
     * @throws LittleRException if the task text is malformed or missing required delimiters
     */
    private static Task parseDeadline(String taskText) throws LittleRException {
        String[] deadlineParts = taskText.split(Deadline.INPUT_DELIMITER);
        if (deadlineParts.length < 2) {
            throw new LittleRException(
                "Invalid deadline format. \nUse: deadline <task description> "
                + Deadline.INPUT_DELIMITER + " <due date>");
        }
        if (deadlineParts[0].trim().isEmpty()) {
            throw new LittleRException("Please provide a task description before "
                + Deadline.INPUT_DELIMITER + ".");
        }
        if (deadlineParts[1].trim().isEmpty()) {
            throw new LittleRException("Please provide a due date after "
            + Deadline.INPUT_DELIMITER + ", e.g. /by 2-12-2019.");
        }
        return new Deadline(deadlineParts[0].trim(), StringDateTimeConverter.parse(deadlineParts[1]));
    }

    /**
     * Parses an event task from the provided task text.
     *
     * @param taskText the raw user input containing the task description and start/end dates
     * @return the constructed {@link Event} instance
     * @throws LittleRException if the task text is malformed or missing required delimiters
     */
    private static Task parseEvent(String taskText) throws LittleRException {
        int fromIdx = taskText.indexOf(Event.FROM_DELIMITER);
        int toIdx = taskText.indexOf(Event.TO_DELIMITER);
        boolean hasFrom = fromIdx != -1;
        boolean hasTo = toIdx != -1;

        if (!hasFrom || !hasTo) {
            throw new LittleRException(
                "Invalid event format."
                + "\nUse: event <task description> "
                + Event.FROM_DELIMITER + " <start datetime> " + Event.TO_DELIMITER + " <end datetime>");
        }
        if (toIdx < fromIdx) {
            throw new LittleRException(
                Event.FROM_DELIMITER + " must come before " + Event.TO_DELIMITER + " in an event.");
        }

        String[] eventParts = taskText.split(
            Pattern.quote(Event.FROM_DELIMITER) + "|" + Pattern.quote(Event.TO_DELIMITER));
        if (eventParts.length != 3) {
            throw new LittleRException(
                "Invalid event format."
                + "\nUse: event <task description> "
                + Event.FROM_DELIMITER + " <start datetime> " + Event.TO_DELIMITER + " <end datetime>");
        }
        return new Event(
            eventParts[0].trim(),
            StringDateTimeConverter.parse(eventParts[1]),
            StringDateTimeConverter.parse(eventParts[2]));
    }

    /**
     * Parses a todo task from the provided task text.
     *
     * @param taskText the raw user input containing the task description
     * @return the constructed {@link Todo} instance
     * @throws LittleRException if the task text is empty
     */
    private static Task parseTodo(String taskText) throws LittleRException {
        if (taskText.isEmpty()) {
            throw new LittleRException("The description of a todo cannot be empty.");
        }
        return new Todo(taskText);
    }
}
