package arin.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import arin.task.Deadline;
import arin.task.Event;
import arin.task.Task;
import arin.task.Todo;

/**
 * Validates saved records before constructing tasks, even when assertions are disabled.
 */
final class TaskFileParser {
    private TaskFileParser() {
    }

    /**
     * Parses one record without guessing missing or ambiguous fields.
     *
     * @param line A pipe-separated task record.
     * @return The task, with its saved completion state.
     * @throws IllegalArgumentException If the record is malformed.
     */
    static Task parse(String line) {
        String[] fields = Arrays.stream(line.split("\\|", -1)).map(String::trim).toArray(String[]::new);
        if (fields.length < 3 || (!fields[1].isEmpty() && !fields[1].equals("X"))
                || fields[2].isEmpty()) {
            throw new IllegalArgumentException("Expected a task type, completion state, and description.");
        }
        Task task = createTask(fields);
        if (fields[1].equals("X")) {
            task.markTask();
        }
        return task;
    }

    /**
     * Validates type-specific fields before calling task constructors.
     */
    private static Task createTask(String[] fields) {
        switch (fields[0]) {
        case "T":
            requireFields(fields, 3);
            return new Todo(fields[2]);
        case "D":
            requireFields(fields, 4);
            try {
                return new Deadline(fields[2], LocalDate.parse(fields[3]));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Expected a valid deadline date in yyyy-MM-dd format.", e);
            }
        case "E":
            if (fields.length == 4) {
                throw new IllegalArgumentException("Separate the event start and end with |; see the User Guide.");
            }
            requireFields(fields, 5);
            return new Event(fields[2], fields[3], fields[4]);
        default:
            throw new IllegalArgumentException("Unknown task type: " + fields[0] + ".");
        }
    }

    /**
     * Rejects extra fields and missing task details instead of dropping saved data.
     */
    private static void requireFields(String[] fields, int count) {
        if (fields.length != count || Arrays.stream(fields).skip(2).anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Expected " + count + " fields with nonempty task details.");
        }
    }
}
