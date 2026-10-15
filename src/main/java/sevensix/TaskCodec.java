package sevensix;

import java.util.ArrayList;
import java.util.List;

/**
 * Encodes and validates the pipe-separated task records used by TaskStorage.
 */
public class TaskCodec {
    /**
     * Converts a task into one line of the storage format.
     *
     * @param task the task to format.
     * @return the serialized task.
     */
    public String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return String.join(" | ", "D", status, escape(deadline.getDescription()),
                    escape(DateTimeParser.formatForStorage(deadline.getBy(), deadline.getByTime())));
        }
        if (task instanceof Event event) {
            return String.join(" | ", "E", status, escape(event.getDescription()),
                    escape(DateTimeParser.formatForStorage(event.getFrom(), event.getFromTime())),
                    escape(DateTimeParser.formatForStorage(event.getTo(), event.getToTime())));
        }
        return String.join(" | ", "T", status, escape(task.getDescription()));
    }

    /**
     * Converts one storage line into a task, returning {@code null} for malformed data.
     *
     * @param line the line read from disk.
     * @return the parsed task, or {@code null} when the line is invalid.
     */
    public Task parseTask(String line) {
        if (line.isBlank()) {
            return null;
        }

        List<String> fields = splitFields(line);
        if (!hasValidStatus(fields)) {
            return null;
        }

        Task task = parseTaskFields(fields);
        if (task == null) {
            return null;
        }
        try {
            TaskValidation.validate(task);
            restoreStatus(task, fields.get(1));
            return task;
        } catch (SevenSixException exception) {
            return null;
        }
    }

    /** Checks whether a storage record has a valid status field.
     *
     * @param fields the decoded storage fields.
     * @return {@code true} when the record contains a valid status.
     */
    private boolean hasValidStatus(List<String> fields) {
        return fields.size() >= 3 && (fields.get(1).equals("0") || fields.get(1).equals("1"));
    }

    /** Parses the task-specific fields in a storage record.
     *
     * @param fields the decoded storage fields.
     * @return the parsed task, or {@code null} when the type is invalid.
     */
    private Task parseTaskFields(List<String> fields) {
        switch (fields.get(0)) {
        case "T":
            return parseTodo(fields);
        case "D":
            return parseDeadline(fields);
        case "E":
            return parseEvent(fields);
        default:
            return null;
        }
    }

    /** Parses a to-do storage record.
     *
     * @param fields the decoded storage fields.
     * @return the parsed to-do, or {@code null} when the fields are invalid.
     */
    private Task parseTodo(List<String> fields) {
        if (fields.size() != 3 || fields.get(2).isBlank()) {
            return null;
        }
        return new Todo(fields.get(2));
    }

    /** Parses a deadline storage record.
     *
     * @param fields the decoded storage fields.
     * @return the parsed deadline, or {@code null} when the fields are invalid.
     */
    private Task parseDeadline(List<String> fields) {
        if (fields.size() != 4 || fields.get(2).isBlank() || fields.get(3).isBlank()) {
            return null;
        }
        try {
            DateTimeParser.ParsedDateTime parsedBy = DateTimeParser.parse(fields.get(3));
            return new Deadline(fields.get(2), parsedBy.getDate(), parsedBy.getTime());
        } catch (SevenSixException exception) {
            return null;
        }
    }

    /** Parses an event storage record.
     *
     * @param fields the decoded storage fields.
     * @return the parsed event, or {@code null} when the fields are invalid.
     */
    private Task parseEvent(List<String> fields) {
        if (fields.size() != 5 || fields.get(2).isBlank() || fields.get(3).isBlank()
                || fields.get(4).isBlank()) {
            return null;
        }
        try {
            DateTimeParser.ParsedDateTime parsedFrom = DateTimeParser.parse(fields.get(3));
            DateTimeParser.ParsedDateTime parsedTo = DateTimeParser.parse(fields.get(4));
            return new Event(fields.get(2), parsedFrom.getDate(), parsedFrom.getTime(),
                    parsedTo.getDate(), parsedTo.getTime());
        } catch (SevenSixException exception) {
            return null;
        }
    }

    /** Restores the completion status stored in a task record.
     *
     * @param task the task whose status is restored.
     * @param status the serialized completion status.
     */
    private void restoreStatus(Task task, String status) {
        if (status.equals("1")) {
            task.markAsDone();
        }
    }

    /**
     * Splits a storage line at unescaped pipe characters and removes field padding.
     *
     * @param line the serialized task line.
     * @return the decoded fields.
     */
    private List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\\') {
                i++;
                if (i == line.length() || (line.charAt(i) != '|' && line.charAt(i) != '\\')) {
                    return List.of();
                }
                field.append(line.charAt(i));
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     *
     * @param value the text to escape.
     * @return the escaped text.
     */
    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }
}
