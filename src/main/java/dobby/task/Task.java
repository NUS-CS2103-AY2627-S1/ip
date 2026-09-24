package dobby.task;

import java.util.Locale;

import dobby.exception.DobbyException;
import dobby.util.DateTimeUtil;
import dobby.util.DobbyUtil;

/**
 * Represents a task with a description, type, and completion status.
 */
public class Task {
    private static final String FIELD_SEPARATOR = " \\| ";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";
    private static final int TYPE_FIELD = 0;
    private static final int STATUS_FIELD = 1;
    private static final int DESCRIPTION_FIELD = 2;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final int DEADLINE_DATE_FIELD = 3;
    private static final int EVENT_START_DATE_FIELD = 3;
    private static final int EVENT_END_DATE_FIELD = 4;

    private final String description;
    private final String type;
    private boolean isDone;

    /**
     * Creates a task with its description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this(description, " ");
    }

    /**
     * Creates a task with a description and task type.
     *
     * @param description Description of the task.
     * @param type Type of task.
     */
    public Task(String description, String type) {
        assert description != null && !description.isBlank() : "A task must have a description";

        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed. */
    public void markNotDone() {
        this.isDone = false;
    }

    /** Returns whether this task is completed. */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code "X"} when complete; otherwise, a blank space
     */
    public String getStatusIcon() {
        return (this.isDone ? "X" : " ");
    }

    /**
     * Returns this task's type code.
     *
     * @return the task type code
     */
    public String getType() {
        return type;
    }

    /**
     * Returns whether this task's description contains the given text, ignoring letter case.
     *
     * @param keyword Text to look for.
     * @return whether the description contains the text
     */
    public boolean hasDescriptionContaining(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task in the format used by the task data file.
     *
     * @return task type, status, and description separated by pipes
     */
    public String toFileString() {
        return this.type + " | " + (this.isDone ? "1" : "0") + " | " + this.description;
    }

    /**
     * Reconstructs a task from one line in the task data file.
     *
     * @param line Saved task data.
     * @return the reconstructed task
     * @throws DobbyException if the line does not match the save format
     */
    public static Task fromFileString(String line) throws DobbyException {
        assert line != null : "A saved task line must come from the storage reader";

        String[] fields = line.split(FIELD_SEPARATOR, -1);
        validateCommonFields(fields, line);

        Task task = createTask(fields, line);
        if (fields[STATUS_FIELD].equals(COMPLETE_STATUS)) {
            task.markDone();
        }
        return task;
    }

    /** Validates fields shared by every saved task type. */
    private static void validateCommonFields(String[] fields, String line) throws DobbyException {
        boolean hasRequiredFields = fields.length >= TODO_FIELD_COUNT;
        boolean hasDescription = hasRequiredFields && !fields[DESCRIPTION_FIELD].isBlank();
        boolean hasValidStatus = hasRequiredFields
                && (fields[STATUS_FIELD].equals(INCOMPLETE_STATUS)
                        || fields[STATUS_FIELD].equals(COMPLETE_STATUS));
        if (!hasDescription || !hasValidStatus) {
            throw new DobbyException("Invalid saved task: " + line);
        }
    }

    /** Creates the task subtype identified by the saved type field. */
    private static Task createTask(String[] fields, String line) throws DobbyException {
        return switch (fields[TYPE_FIELD]) {
            case "T" -> createTodo(fields, line);
            case "D" -> createDeadline(fields, line);
            case "E" -> createEvent(fields, line);
            default -> throw new DobbyException("Unknown saved task type: " + fields[TYPE_FIELD]);
        };
    }

    /** Creates a todo from validated common fields. */
    private static Task createTodo(String[] fields, String line) throws DobbyException {
        if (fields.length != TODO_FIELD_COUNT) {
            throw new DobbyException("Invalid saved todo: " + line);
        }
        return new ToDo(fields[DESCRIPTION_FIELD]);
    }

    /** Creates a deadline from validated common fields. */
    private static Task createDeadline(String[] fields, String line) throws DobbyException {
        if (fields.length != DEADLINE_FIELD_COUNT || fields[DEADLINE_DATE_FIELD].isBlank()) {
            throw new DobbyException("Invalid saved deadline: " + line);
        }
        DateTimeUtil.ParsedDateTime deadlineDateTime = parseSavedDate(fields[DEADLINE_DATE_FIELD], line);
        return new Deadline(fields[DESCRIPTION_FIELD], deadlineDateTime.getValue(), deadlineDateTime.hasTime());
    }

    /** Creates an event from validated common fields. */
    private static Task createEvent(String[] fields, String line) throws DobbyException {
        if (fields.length != EVENT_FIELD_COUNT || fields[EVENT_START_DATE_FIELD].isBlank()
                || fields[EVENT_END_DATE_FIELD].isBlank()) {
            throw new DobbyException("Invalid saved event: " + line);
        }
        DateTimeUtil.ParsedDateTime startDateTime = parseSavedDate(fields[EVENT_START_DATE_FIELD], line);
        DateTimeUtil.ParsedDateTime endDateTime = parseSavedDate(fields[EVENT_END_DATE_FIELD], line);
        if (endDateTime.getValue().isBefore(startDateTime.getValue())) {
            throw new DobbyException("Invalid saved event order: " + line);
        }
        return new Event(fields[DESCRIPTION_FIELD], startDateTime.getValue(), startDateTime.hasTime(),
                endDateTime.getValue(), endDateTime.hasTime());
    }

    /** Parses a date from saved data while preserving the context of the invalid record. */
    private static DateTimeUtil.ParsedDateTime parseSavedDate(String dateText, String line) throws DobbyException {
        try {
            return DateTimeUtil.parse(dateText);
        } catch (DobbyException e) {
            throw new DobbyException("Invalid saved date: " + line, e);
        }
    }

    /** Returns a displayable representation of this task. */
    @Override
    public String toString() {
        return DobbyUtil.encloseBracket(this.getType())
                + DobbyUtil.encloseBracket(this.getStatusIcon()) + " "
                + this.description;
    }
}
