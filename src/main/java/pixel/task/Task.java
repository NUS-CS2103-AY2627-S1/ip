package pixel.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isDone;


    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description User-visible task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the completion icon for this task.
     *
     * @return {@code X} when done, otherwise a blank icon.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    protected String getDescription() {
        return description;
    }

    protected boolean isDone() {
        return isDone;
    }

    /**
     * Returns a copy of this task with a new description.
     *
     * @param newDescription Replacement description.
     * @return Updated task with the same completion status.
     */
    public Task withDescription(String newDescription) {
        return preserveStatus(new Task(newDescription));
    }

    /**
     * Copies this task's completion status to a replacement task.
     *
     * @param replacement Replacement task.
     * @param <T> Concrete replacement task type.
     * @return Replacement with the same completion status as this task.
     */
    protected <T extends Task> T preserveStatus(T replacement) {
        if (isDone) {
            replacement.markAsDone();
        }
        return replacement;
    }

    /**
     * Checks whether this task occurs on a given date.
     * Tasks without a structured date do not occur on any searchable date.
     *
     * @param date Date to check.
     * @return Whether this task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Checks whether this task's description contains the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Whether the task description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Checks whether another task has the same type and user-entered details.
     * Completion status is intentionally ignored.
     *
     * @param other Task to compare.
     * @return Whether both tasks represent the same list item.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /**
     * Returns the representation used when saving this task to disk.
     *
     * @return Serialized task data.
     */
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + escapeDataField(description);
    }

    /**
     * Recreates a task from one line of saved data.
     *
     * @param data Serialized task data.
     * @return Task represented by the saved data.
     */
    public static Task fromDataString(String data) {
        String[] fields = data.split(" \\| ", -1);
        if (fields.length < 2) {
            throw new IllegalArgumentException("task type or status is missing.");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw new IllegalArgumentException("status must be 0 or 1.");
        }

        Task task;
        switch (fields[0]) {
            case "T":
                requireFieldCount(fields, 3);
                requireNonEmpty(fields[2], "task description");
                task = new Todo(unescapeDataField(fields[2]));
                break;
            case "D":
                requireFieldCount(fields, 4);
                requireNonEmpty(fields[2], "task description");
                requireNonEmpty(fields[3], "deadline");
                task = new Deadline(unescapeDataField(fields[2]), parseDeadline(fields[3]));
                break;
            case "E":
                requireFieldCount(fields, 5);
                requireNonEmpty(fields[2], "task description");
                requireNonEmpty(fields[3], "event start time");
                requireNonEmpty(fields[4], "event end time");
                task = new Event(unescapeDataField(fields[2]), unescapeDataField(fields[3]),
                        unescapeDataField(fields[4]));
                break;
            default:
                throw new IllegalArgumentException("unknown task type '" + fields[0] + "'.");
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses an ISO deadline stored in a saved task record.
     *
     * @param field Escaped deadline field from the data file.
     * @return Parsed deadline.
     * @throws IllegalArgumentException If the field is not a valid ISO date.
     */
    private static LocalDate parseDeadline(String field) {
        try {
            return LocalDate.parse(unescapeDataField(field));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "deadline must be a valid date in YYYY-MM-DD format.", exception);
        }
    }

    /** Validates that a saved record has exactly the fields required by its task type. */
    private static void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("expected " + expectedCount
                    + " fields but found " + fields.length + ".");
        }
    }

    /** Validates that a required saved field contains visible or non-whitespace text. */
    private static void requireNonEmpty(String field, String fieldName) {
        if (field.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    /**
     * Escapes characters that have structural meaning in the save-file format.
     *
     * @param field Field to escape.
     * @return Escaped field.
     */
    protected static String escapeDataField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Restores escaped separators and backslashes from a saved field.
     * Unknown and trailing backslashes are preserved for compatibility with files
     * written before escaping was introduced.
     */
    private static String unescapeDataField(String field) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char character = field.charAt(i);
            if (character == '\\' && i + 1 < field.length()
                    && (field.charAt(i + 1) == '\\' || field.charAt(i + 1) == '|')) {
                result.append(field.charAt(i + 1));
                i++;
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
