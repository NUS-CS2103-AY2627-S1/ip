package grug.storage.serde;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import grug.task.DeadlineTask;
import grug.task.EventTask;
import grug.task.Task;
import grug.task.TaskPriority;
import grug.task.TodoTask;

/**
 * Contains static methods to deserialize tasks that were serialized and saved
 * to disk.
 */
public class TaskDeserializer {
    private static final Pattern SERIALIZED_HEADER_PATTERN = Pattern.compile("\\[\\s*(.*?)\\s*\\]");

    /**
     * Deserializes multiple task blocks from a serialized task list.
     * <p>
     * Each task block starts with a {@code [task-type]} header. Blank lines
     * between blocks are ignored, while the contents of each block are delegated
     * to {@link #deserializeOne(String)}.
     * <p>
     *
     * @param serializedTasks the serialized task list.
     * @return the deserialized tasks, in their serialized order.
     * @throws TaskDeserializerException if the input contains data before the
     *                                   first task header or if a task block is
     *                                   invalid.
     */
    public static List<Task> deserializeMany(String serializedTasks) throws TaskDeserializerException {
        if (serializedTasks == null) {
            throw new TaskDeserializerException("Serialized task list cannot be null");
        }

        // list of non-blank stripped lines (every line here has content)
        List<String> lines = serializedTasks.lines()
                .filter(line -> !line.isBlank())
                .map(line -> line.strip())
                .toList();

        List<String> serializedTaskBlocks = new ArrayList<>();
        StringBuilder currentBlock = new StringBuilder();

        for (String line : lines) {
            // note that a malformed line header like aaa[task-type]a is NOT counted as a
            // header, but a property instead (as part of the current task block)
            // this means a malformed header will cause the current task block to error
            // instead of starting a new task block
            boolean isHeader = isLineHeader(line);
            if (isHeader) {
                if (!currentBlock.isEmpty()) {
                    serializedTaskBlocks.add(currentBlock.toString());
                }
                currentBlock.setLength(0);
            }
            currentBlock.append(line).append("\n");
        }

        if (!currentBlock.isEmpty()) {
            serializedTaskBlocks.add(currentBlock.toString());
        }

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < serializedTaskBlocks.size(); i++) {
            String serializedTask = serializedTaskBlocks.get(i);
            tasks.add(deserializeOne(serializedTask));
        }
        return tasks;
    }

    /**
     * Checks if a given line matches the [task-type] header given by the
     * {@link #SERIALIZED_HEADER_PATTERN}.
     *
     * @param line The line to check.
     * @return {@code true} if the line matches the pattern, {@code false}
     *         otherwise.
     */
    private static boolean isLineHeader(String line) {
        return SERIALIZED_HEADER_PATTERN.matcher(line).matches();
    }

    /**
     * Parses out the task type from the header line {@code [task-type]}.
     *
     * @param header The line to parse.
     * @return The inner task-type string.
     * @throws TaskDeserializerException If the header does not match the header
     *                                   pattern. This can be checked using
     *                                   {@link #isLineHeader(String)}.
     */
    private static String parseHeaderTaskType(String header) throws TaskDeserializerException {
        Matcher matcher = SERIALIZED_HEADER_PATTERN.matcher(header);

        // the header must occupy the entire line. Using find() would accept a
        // task type embedded in malformed text, such as "aaa[ todo]completed=1"
        // so we use matches() instead.
        if (!matcher.matches()) {
            throw new TaskDeserializerException("Missing header [task-type]");
        }

        // guaranteed not to throw since there is exactly 1 capture group (apart from
        // the entire matching subsequence) if found
        // note: group 0 is not included in groupCount(), but is included in group()
        assert matcher.groupCount() == 1
                : "The serialized task is valid, but does not contain exactly 1 capture group";
        return matcher.group(1).toLowerCase();
    }

    /**
     * Parses out the body properties from the body lines {@code property = value}.
     * Note that if there is at least 1 malformed line, the whole body is considered
     * invalid.
     *
     * @param bodyLines the body lines to iterate through and parse.
     * @return A map of {@code property} to {@code value}.
     * @throws TaskDeserializerException If there is at least 1 malformed line.
     */
    private static Map<String, String> parseBodyProperties(Iterable<String> bodyLines)
            throws TaskDeserializerException {
        HashMap<String, String> properties = new HashMap<>();

        for (String line : bodyLines) {
            line = line.strip();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("=", 2);
            if (parts.length != 2) {
                throw new TaskDeserializerException(
                        "Property `%s` does not specify a value with an =".formatted(line));
            }

            String property = parts[0].strip().toLowerCase();
            if (property.contains("[") || property.contains("]")) {
                throw new TaskDeserializerException(
                        "Property `%s` contains illegal characters [ and ]".formatted(property));
            }
            String value = parts[1].strip().replace("\\n", "\n");
            properties.put(property, value);
        }

        return properties;
    }

    /**
     * Takes in a serialized task and converts it into its appropriate task object.
     * <p>
     *
     * ### Deserialization rules
     * The following are rules for deserialization that the serialized string must
     * follow (not task specific):
     * - The header {@code [task-type]} and properties {@code prop = value} must all
     * be on separate lines and follow that format exactly.
     * - The header must come before all properties.
     * - Properties can be in any order.
     * - Each property occupies exactly 1 line (i.e. cannot be split into multiple
     * lines).
     * - Property names must not contain {@code [} or {@code ]} since these are
     * reserved for headers
     * - String values are not wrapped in double quotes.
     * - Line terminators may be {@code \n}, {@code \r} or {@code \r\n}
     *
     * ### Example
     *
     * <pre>
     * [todo]
     * completed = 1
     * details = run 10km
     * </pre>
     *
     * Should be converted into
     *
     * <pre>{@code
     * // TodoTask { details: "run 10km", isCompleted: true }
     * TodoTask task = new TodoTask("run 10km");
     * task.markComplete();
     * }</pre>
     *
     * @param serializedTask The serialized task data to deserialize.
     * @return The appropriate deserialized task.
     * @throws TaskDeserializerException If the serialized task data is formatted
     *                                   wrongly or is missing properties required
     *                                   by the appropriate task type.
     */
    public static Task deserializeOne(String serializedTask) throws TaskDeserializerException {
        List<String> lines = serializedTask.lines()
                .filter(line -> !line.isBlank())
                .map(line -> line.strip())
                .toList();

        if (lines.isEmpty()) {
            throw new TaskDeserializerException("Nothing to deserialize");
        }

        String header = lines.get(0);
        String taskType = parseHeaderTaskType(header);

        var properties = parseBodyProperties(lines.subList(1, lines.size()));

        Task task = switch (taskType) {
            case "todo" -> createTodoFromProperties(properties);
            case "deadline" -> createDeadlineFromProperties(properties);
            case "event" -> createEventFromProperties(properties);
            default -> throw new TaskDeserializerException(
                    "Invalid task type [%s] found".formatted(taskType));
        };
        return task;
    }

    private static boolean isDateTimeTextValid(CharSequence text, DateTimeFormatter formatter) {
        try {
            formatter.parse(text);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Retrieves the "priority" property from the serialized {@code properties}.
     *
     * Returns the appropriate {@link TaskPriority} enum if valid priority provided,
     * or {@link TaskPriority#DEFAULT} otherwise.
     *
     * @param properties The map of property to value.
     * @return The appropriate {@link TaskPriority} enum if a valid priority was
     *         found, or {@link TaskPriority#DEFAULT} otherwise.
     */
    private static TaskPriority parsePriorityFromProperties(Map<String, String> properties) {
        String priorityDisplayName = properties.get("priority");

        // the null case ("priority" property doesn't exist) is handled here too
        try {
            return TaskPriority.createFromDisplayName(priorityDisplayName);
        } catch (IllegalArgumentException e) {
            return TaskPriority.DEFAULT;
        }
    }

    /**
     * Converts a property:value map into a {@link TodoTask}.
     *
     * @param properties The property:value map to convert.
     * @return The task based on the property:value map.
     * @throws TaskDeserializerException If certain properties required by the task
     *                                   are not present.
     */
    private static TodoTask createTodoFromProperties(Map<String, String> properties)
            throws TaskDeserializerException {
        // empty string if details not provided
        String details = properties.get("details");
        if (details == null) {
            throw new TaskDeserializerException("[todo] task requires non-empty `details` property");
        }

        TaskPriority priority = parsePriorityFromProperties(properties);
        TodoTask task = new TodoTask(details);
        task.setPriority(priority);

        // only set isCompleted to true if `completed = 1`
        if ("1".equals(properties.get("completed"))) {
            task.markComplete();
        }
        return task;
    }

    /**
     * Converts a property:value map into a {@link DeadlineTask}.
     *
     * @param properties The property:value map to convert.
     * @return The task based on the property:value map.
     * @throws TaskDeserializerException If certain properties required by the task
     *                                   are not present.
     */
    private static DeadlineTask createDeadlineFromProperties(Map<String, String> properties)
            throws TaskDeserializerException {
        // empty string if details not provided
        String details = properties.get("details");
        if (details == null) {
            throw new TaskDeserializerException("[deadline] task requires non-empty `details` property");
        }

        String by = properties.get("by");
        if (by == null) {
            throw new TaskDeserializerException("[deadline] task requires non-empty `by` property");
        }

        TaskPriority priority = parsePriorityFromProperties(properties);

        DeadlineTask task;
        try {
            task = new DeadlineTask(details, by);
        } catch (DateTimeParseException e) {
            throw new TaskDeserializerException(
                    "[deadline] property by = `%s` does not match format `%s`"
                            .formatted(by, DeadlineTask.DATE_TIME_INPUT_PATTERN),
                    e);
        }

        // only set isCompleted to true if `completed = 1`
        if ("1".equals(properties.get("completed"))) {
            task.markComplete();
        }
        task.setPriority(priority);
        return task;
    }

    /**
     * Converts a property:value map into a {@link EventTask}.
     *
     * @param properties The property:value map to convert.
     * @return The task based on the property:value map.
     * @throws TaskDeserializerException If certain properties required by the task
     *                                   are not present.
     */
    private static EventTask createEventFromProperties(Map<String, String> properties)
            throws TaskDeserializerException {
        // empty string if details not provided
        String details = properties.get("details");
        if (details == null) {
            throw new TaskDeserializerException("[event] task requires non-empty `details` property");
        }

        String start = properties.get("start");
        if (start == null) {
            throw new TaskDeserializerException("[event] task requires non-empty `start` property");
        }

        String end = properties.get("end");
        if (end == null) {
            throw new TaskDeserializerException("[event] task requires non-empty `end` property");
        }

        TaskPriority priority = parsePriorityFromProperties(properties);

        // we are validating start and end here instead of catching the error from
        // EventTask constructor to have appropriate error messages for start and end,
        // rather than lumping them together
        if (!isDateTimeTextValid(start, EventTask.DATE_TIME_INPUT_FORMATTER)) {
            throw new TaskDeserializerException(
                    "[event] property start = `%s` does not match format `%s`"
                            .formatted(start, EventTask.DATE_TIME_INPUT_PATTERN));
        }

        if (!isDateTimeTextValid(end, EventTask.DATE_TIME_INPUT_FORMATTER)) {
            throw new TaskDeserializerException(
                    "[event] property end = `%s` does not match format `%s`"
                            .formatted(end, EventTask.DATE_TIME_INPUT_PATTERN));
        }

        // guaranteed not to throw DateTimeParseException now that we validated start
        // and end
        EventTask task = null;
        try {
            task = new EventTask(details, start, end);
        } catch (IllegalArgumentException e) {
            throw new TaskDeserializerException(
                    "[event] property start `%s` must be a date/time occuring before or on end `%s`"
                            .formatted(start, end),
                    e);
        }

        // only set isCompleted to true if `completed = 1`
        if ("1".equals(properties.get("completed"))) {
            task.markComplete();
        }
        task.setPriority(priority);
        return task;
    }
}
