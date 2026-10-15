package kpei.storage;

import kpei.datatypes.Deadline;
import kpei.datatypes.Event;
import kpei.datatypes.Task;
import kpei.exceptions.BertException;
import kpei.utility.DateTimeParser;

/**
 * Parses raw parameters into specific {@link Task} objects.
 */
public class StorageParser {

    /**
     * Checks whether a storage line contains all fields required for a supported task type.
     *
     * @param line A line read from a storage file.
     * @return {@code true} if the line represents a supported task, {@code false} otherwise.
     */
    public boolean isStoredTask(String line) {
        if (line.isBlank()) {
            return false;
        }

        String[] fields = line.split("\\s*\\|\\s*");
        if (fields.length < 3) {
            return false;
        }

        return switch (fields[0].trim()) {
            case "todo" -> true;
            case "deadline" -> fields.length >= 5;
            case "event" -> fields.length >= 7;
            default -> false;
        };
    }

    /**
     * Parses a supported storage line into its corresponding task.
     *
     * @param line A line read from a storage file.
     * @return The task represented by the storage line.
     * @throws BertException If the stored task has invalid field values or an unsupported type.
     */
    public Task parseStoredTask(String line) throws BertException {
        if (!isStoredTask(line)) {
            throw new BertException("Unsupported or incomplete stored task");
        }

        String[] fields = line.split("\\s*\\|\\s*");
        String type = fields[0].trim();
        boolean isMarked = Boolean.parseBoolean(fields[1].trim()) || fields[1].trim().equals("1");
        String description = fields[2].trim();

        return switch (type) {
            case "todo" -> parseTask(isMarked, description);
            case "deadline" -> parseDeadline(isMarked, description, fields[3].trim(), fields[4].trim());
            case "event" -> parseEvent(isMarked, description, fields[3].trim(), fields[4].trim(),
                    fields[5].trim(), fields[6].trim());
            default -> throw new BertException("Unsupported stored task type: " + type);
        };
    }

    /**
     * Creates a {@link Task} with the specified completion status and description.
     *
     * @param isMarked Whether the task is marked as completed.
     * @param description The description of the task.
     * @return The created {@link Task} instance.
     * @throws BertException If the description is blank.
     */
    private Task parseTask(boolean isMarked, String description) throws BertException {
        if (description.isBlank()) {
            throw new BertException("Failed to create task. Some fields are invalid");
        }
        return new Task(isMarked, description);
    }

    /**
     * Creates a {@link Deadline} task with the specified completion status, description, due date, and due time.
     *
     * @param isMarked Whether the deadline task is marked as completed.
     * @param description The description of the deadline task.
     * @param byDate The date by which the task must be completed.
     * @param byTime The time by which the task must be completed.
     * @return The created {@link Deadline} instance.
     * @throws BertException If any field is invalid or date parsing fails.
     */
    private Deadline parseDeadline(boolean isMarked, String description, String byDate, String byTime)
            throws BertException {
        if (description.isBlank() || byDate.isBlank() || byTime.isBlank()) {
            throw new BertException("Failed to create deadline. Some fields are invalid");
        }
        return new Deadline(isMarked, description, DateTimeParser.parseDate(byDate), DateTimeParser.parseTime(byTime));
    }

    /**
     * Creates an {@link Event} task with the specified completion status, description, start date and time,
     * and end date and time.
     *
     * @param isMarked Whether the event task is marked as completed.
     * @param description The description of the event task.
     * @param fromDate The starting date of the event.
     * @param fromTime The starting time of the event.
     * @param toDate The ending date of the event.
     * @param toTime The ending time of the event.
     * @return The created {@link Event} instance.
     * @throws BertException If any field is invalid or date parsing fails.
     */
    private Event parseEvent(boolean isMarked, String description, String fromDate, String fromTime,
                                    String toDate, String toTime) throws BertException {
        if (description.isBlank() || fromDate.isBlank() || fromTime.isBlank()
                || toDate.isBlank() || toTime.isBlank()) {
            throw new BertException("Failed to create event. Some fields are invalid");
        }
        return new Event(isMarked, description, DateTimeParser.parseDate(fromDate), DateTimeParser.parseTime(fromTime),
                DateTimeParser.parseDate(toDate), DateTimeParser.parseTime(toTime));
    }
}
