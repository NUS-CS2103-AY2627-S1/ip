package tuesday.processor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import tuesday.task.DateTimeFormat;
import tuesday.task.Deadline;
import tuesday.task.Event;
import tuesday.task.Task;
import tuesday.task.Todo;

/**
 * Converts saved text rows into tasks using the existing save-file format.
 */
public final class SavedTaskParser {
    private SavedTaskParser() {
    }

    /**
     * Parses a date-time value stored in the task file's display format.
     */
    private static Event.DateTimeValue parseSavedDateTime(String value) {
        try {
            if (!value.contains(":")) {
                return new Event.DateTimeValue(LocalDate.parse(value, DateTimeFormat.DATE_FORMATTER), null);
            }
            int separator = value.lastIndexOf(' ');
            if (separator < 0) {
                return new Event.DateTimeValue(null, LocalTime.parse(value, DateTimeFormat.TIME_FORMATTER));
            }
            if (separator == 0 || separator == value.length() - 1) {
                return null;
            }
            return new Event.DateTimeValue(
                    LocalDate.parse(value.substring(0, separator), DateTimeFormat.DATE_FORMATTER),
                    LocalTime.parse(value.substring(separator + 1), DateTimeFormat.TIME_FORMATTER));
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Converts saved task data back into its corresponding task object.
     *
     * @param taskData a saved row, or null.
     * @return the parsed task, or null when the row is invalid and should be skipped.
     */
    public static Task parseTask(String taskData) {
        if (taskData == null) {
            return null;
        }

        String data = taskData.trim();
        if (!hasValidSavedTaskStructure(data)) {
            return null;
        }

        char status = data.charAt(4);
        if (!hasValidSavedTaskStatus(status)) {
            return null;
        }

        Task task = parseSavedTask(data.charAt(1), data.substring(7));
        if (task == null) {
            return null;
        }

        if (status == 'X') {
            task.mark();
        }
        return task;
    }

    /**
     * Checks that saved task data has the expected type and status brackets.
     */
    private static boolean hasValidSavedTaskStructure(String data) {
        return data.length() >= 8
                && data.charAt(0) == '['
                && data.charAt(2) == ']'
                && data.charAt(3) == '['
                && data.charAt(5) == ']'
                && data.charAt(6) == ' ';
    }

    /**
     * Checks whether a saved task status represents a completed or incomplete task.
     */
    private static boolean hasValidSavedTaskStatus(char status) {
        return status == 'X' || status == ' ';
    }

    /**
     * Parses saved task details according to their task type.
     */
    private static Task parseSavedTask(char type, String details) {
        return switch (type) {
            case 'T' -> parseSavedTodo(details);
            case 'D' -> parseSavedDeadline(details);
            case 'E' -> parseSavedEvent(details);
            default -> null;
        };
    }

    /**
     * Creates a todo from its saved description.
     */
    private static Todo parseSavedTodo(String details) {
        return new Todo(details);
    }

    /**
     * Parses the description and date-time of a saved deadline.
     */
    private static Deadline parseSavedDeadline(String details) {
        String marker = " (by: ";
        int markerIndex = details.lastIndexOf(marker);
        if (markerIndex < 0 || !details.endsWith(")")) {
            return null;
        }

        String description = details.substring(0, markerIndex);
        String deadlineText = details.substring(markerIndex + marker.length(), details.length() - 1);
        if (description.isEmpty() || deadlineText.isEmpty()) {
            return null;
        }

        Event.DateTimeValue deadline = parseSavedDateTime(deadlineText);
        if (deadline == null) {
            return null;
        }
        return new Deadline(description, deadline.date(), deadline.time());
    }

    /**
     * Parses the description, start, and end of a saved event.
     */
    private static Event parseSavedEvent(String details) {
        String fromMarker = " (from: ";
        String toMarker = " to: ";
        int fromIndex = details.indexOf(fromMarker);
        int toIndex = details.lastIndexOf(toMarker);

        if (fromIndex < 0 || toIndex < fromIndex + fromMarker.length() || !details.endsWith(")")) {
            return null;
        }

        String description = details.substring(0, fromIndex);
        String startTimeText = details.substring(fromIndex + fromMarker.length(), toIndex);
        String endTimeText = details.substring(toIndex + toMarker.length(), details.length() - 1);
        if (description.isEmpty() || startTimeText.isEmpty() || endTimeText.isEmpty()) {
            return null;
        }

        Event.DateTimeValue start = parseSavedDateTime(startTimeText);
        Event.DateTimeValue end = parseSavedDateTime(endTimeText);
        if (start == null || end == null) {
            return null;
        }
        return new Event(description, start, end);
    }
}
