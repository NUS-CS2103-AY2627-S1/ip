package dude.storage.json;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dude.task.CorruptedTask;
import dude.task.Deadline;
import dude.task.Event;
import dude.task.EventPeriod;
import dude.task.Task;
import dude.task.TaskDate;
import dude.task.Todo;

/**
 * Encodes and decodes the JSONL representation used by DUDE storage.
 */
public final class TaskJsonCodec {
    private static final String TYPE_FIELD = "type";
    private static final String DONE_FIELD = "done";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String DATE_FIELD = "date";
    private static final String DATE_TIME_FIELD = "dateTime";
    private static final String BY_FIELD = "by";
    private static final String FROM_FIELD = "from";
    private static final String TO_FIELD = "to";

    /**
     * Creates a task JSON codec.
     */
    public TaskJsonCodec() {
    }

    /**
     * Parses one stored task, preserving malformed records as corrupted tasks.
     *
     * @param line JSONL record to parse.
     * @return Reconstructed task or a corrupted-task representation.
     */
    public Task parseSafely(String line) {
        try {
            return parse(line);
        } catch (IllegalArgumentException exception) {
            return new CorruptedTask(line);
        }
    }

    /**
     * Serialises one task as an escaped JSON object.
     *
     * @param task Task to serialise.
     * @return One JSONL record.
     */
    public String serialize(Task task) {
        StringBuilder json = new StringBuilder("{\"type\":")
                .append(quote(getTaskType(task)))
                .append(",\"done\":")
                .append(task.isDone())
                .append(",\"description\":")
                .append(quote(task.getDescription()));

        if (task instanceof Deadline deadline) {
            appendDateField(json, BY_FIELD, deadline.getBy());
        } else if (task instanceof Event event) {
            appendDateField(json, FROM_FIELD, event.getFrom());
            appendDateField(json, TO_FIELD, event.getTo());
        } else if (task instanceof CorruptedTask corruptedTask) {
            appendField(json, "raw", corruptedTask.getRawContent());
        }
        return json.append("}").toString();
    }

    private static Task parse(String line) {
        Map<String, String> fields = new JsonObjectParser(line).parse();
        String type = requireField(fields, TYPE_FIELD);
        boolean isDone = parseBoolean(requireField(fields, DONE_FIELD));
        String description = requireField(fields, DESCRIPTION_FIELD);

        Task task = switch (type) {
        case "T" -> new Todo(description);
        case "D" -> new Deadline(description, parseDateField(fields, BY_FIELD));
        case "E" -> new Event(description, new EventPeriod(
                parseDateField(fields, FROM_FIELD), parseDateField(fields, TO_FIELD)));
        case "C" -> new CorruptedTask(fields.getOrDefault("raw", description));
        default -> throw new IllegalArgumentException("Unknown task type");
        };

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static void appendField(StringBuilder json, String field, String value) {
        json.append(",\"").append(field).append("\":").append(quote(value));
    }

    private static void appendDateField(StringBuilder json, String field, TaskDate value) {
        appendField(json, field + DATE_FIELD, value.dateValue());
        appendField(json, field + DATE_TIME_FIELD, value.dateTimeValue());
    }

    private static String getTaskType(Task task) {
        if (task instanceof CorruptedTask) {
            return "C";
        } else if (task instanceof Todo) {
            return "T";
        } else if (task instanceof Deadline) {
            return "D";
        } else if (task instanceof Event) {
            return "E";
        }
        throw new IllegalArgumentException("Unsupported task type");
    }

    private static String requireField(Map<String, String> fields, String field) {
        String value = fields.get(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing field: " + field);
        }
        return value;
    }

    private static TaskDate parseDateField(Map<String, String> fields, String field) {
        try {
            return TaskDate.fromStorage(
                    requireField(fields, field + DATE_FIELD), fields.get(field + DATE_TIME_FIELD));
        } catch (DateTimeException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid date field: " + field, exception);
        }
    }

    private static boolean parseBoolean(String value) {
        if ("true".equals(value)) {
            return true;
        } else if ("false".equals(value)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid completion state");
    }

    private static String quote(String value) {
        StringBuilder escaped = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
            case '\\' -> escaped.append("\\\\");
            case '"' -> escaped.append("\\\"");
            case '\n' -> escaped.append("\\n");
            case '\r' -> escaped.append("\\r");
            case '\t' -> escaped.append("\\t");
            default -> escaped.append(character);
            }
        }
        return escaped.append('"').toString();
    }
}
