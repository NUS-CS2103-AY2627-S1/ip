package alfred.task;

import java.time.LocalDate;

/**
 * Represents a task entered into Alfred.
 */
public abstract class Task {
    /** Save-file marker for a completed task. */
    private static final String SAVE_STATUS_DONE = "1";

    /** Save-file marker for an incomplete task. */
    private static final String SAVE_STATUS_PENDING = "0";

    /** Number of pipe-delimited fields in a todo save line. */
    private static final int TODO_FIELD_COUNT = 3;

    /** Number of pipe-delimited fields in a deadline save line. */
    private static final int DEADLINE_FIELD_COUNT = 4;

    /** Number of pipe-delimited fields in an event save line. */
    private static final int EVENT_FIELD_COUNT = 5;

    /** Extra save-file field that stores the archive flag. */
    private static final int ARCHIVE_FLAG_OFFSET = 1;

    /** Save-file marker for an archived task. */
    private static final String SAVE_ARCHIVED = "A";

    /** Save-file marker for a live, unarchived task. */
    private static final String SAVE_UNARCHIVED = "UA";

    /** Index of the description field in a save line. */
    private static final int DESCRIPTION_INDEX = 2;

    /** Index of the deadline or event-start field in a save line. */
    private static final int FIRST_DATE_INDEX = 3;

    /** Index of the event-end field in a save line. */
    private static final int EVENT_END_INDEX = 4;

    /** Text describing the task. */
    protected String description;

    /** Completion status of the task. */
    protected TaskStatus status;

    /** {@code true} if this task is archived and hidden from the live list. */
    private boolean isArchived;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        // Parser and save loading always supply a description string.
        assert description != null : "Task description should not be null";
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.isArchived = false;
    }

    /** Returns the display icon for this task's completion status. */
    public String getStatusIcon() {
        assert status != null : "A task always has a completion status after construction";
        return status.getIcon();
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        status = TaskStatus.PENDING;
    }

    /** Archives this task so it is hidden from the live list. */
    public void archive() {
        isArchived = true;
    }

    /** Restores this task to the live list. */
    public void unarchive() {
        isArchived = false;
    }

    /** Returns {@code true} if this task is archived. */
    public boolean isArchived() {
        return isArchived;
    }

    /** Returns {@code true} if this task is marked done. */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns the task formatted with its completion status. */
    public String getDisplayText() {
        return "[" + getType().getIcon() + "][" + getStatusIcon() + "] "
                + description + getDateTimeText();
    }

    /**
     * Returns this task as one save-file line.
     *
     * @return Pipe-delimited save format for this task.
     */
    public abstract String toSaveFormat();

    /**
     * Returns the task represented by a save-file line, or {@code null} if the line
     * is not in the expected format.
     *
     * @param line One line from the save file.
     * @return Parsed task, or {@code null} if the line is corrupted.
     */
    public static Task fromSaveLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < TODO_FIELD_COUNT) {
            return null;
        }
        String typeIcon = parts[0].trim();
        String statusBit = parts[1].trim();
        boolean isDone;
        if (statusBit.equals(SAVE_STATUS_DONE)) {
            isDone = true;
        } else if (statusBit.equals(SAVE_STATUS_PENDING)) {
            isDone = false;
        } else {
            return null;
        }
        TaskType type = TaskType.fromIcon(typeIcon);
        if (type == null) {
            return null;
        }
        Task task = switch (type) {
            case TODO -> parseTodo(parts);
            case DEADLINE -> parseDeadline(parts);
            case EVENT -> parseEvent(parts);
        };
        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns {@code true} if this task occurs on {@code date}.
     * Todos never match; subclasses with dates override this.
     *
     * @param date Calendar date to test.
     * @return {@code true} if this task falls on {@code date}.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** Returns {@code 1} if this task is done, otherwise {@code 0}. */
    protected String getStatusBit() {
        return status == TaskStatus.DONE ? SAVE_STATUS_DONE : SAVE_STATUS_PENDING;
    }

    /** Returns {@code A} if this task is archived, otherwise {@code UA}. */
    protected String getArchiveBit() {
        return isArchived ? SAVE_ARCHIVED : SAVE_UNARCHIVED;
    }

    /**
     * Returns whether {@code parts} encodes an archived task, or {@code null} if the
     * field count or flag is invalid.
     *
     * @param parts Pipe-delimited save-line fields.
     * @param legacyFieldCount Field count used before the archive column existed.
     * @return {@code true} if archived, {@code false} if live, or {@code null} if invalid.
     */
    private static Boolean parseArchiveFlag(String[] parts, int legacyFieldCount) {
        if (parts.length == legacyFieldCount) {
            return Boolean.FALSE;
        }
        if (parts.length == legacyFieldCount + ARCHIVE_FLAG_OFFSET) {
            String flag = parts[parts.length - 1].trim();
            if (SAVE_ARCHIVED.equals(flag)) {
                return Boolean.TRUE;
            }
            if (SAVE_UNARCHIVED.equals(flag)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    private static Task parseTodo(String[] parts) {
        Boolean isArchived = parseArchiveFlag(parts, TODO_FIELD_COUNT);
        if (isArchived == null || parts[DESCRIPTION_INDEX].isEmpty()) {
            return null;
        }
        Task task = new ToDo(parts[DESCRIPTION_INDEX]);
        applyArchiveFlag(task, isArchived);
        return task;
    }

    private static Task parseDeadline(String[] parts) {
        Boolean isArchived = parseArchiveFlag(parts, DEADLINE_FIELD_COUNT);
        if (isArchived == null || parts[DESCRIPTION_INDEX].isEmpty()
                || parts[FIRST_DATE_INDEX].isEmpty()) {
            return null;
        }
        TaskDateTime deadline = TaskDateTime.parseSaved(parts[FIRST_DATE_INDEX]);
        if (deadline == null) {
            return null;
        }
        Task task = new Deadline(parts[DESCRIPTION_INDEX], deadline);
        applyArchiveFlag(task, isArchived);
        return task;
    }

    private static Task parseEvent(String[] parts) {
        Boolean isArchived = parseArchiveFlag(parts, EVENT_FIELD_COUNT);
        if (isArchived == null || parts[DESCRIPTION_INDEX].isEmpty()
                || parts[FIRST_DATE_INDEX].isEmpty() || parts[EVENT_END_INDEX].isEmpty()) {
            return null;
        }
        TaskDateTime from = TaskDateTime.parseSaved(parts[FIRST_DATE_INDEX]);
        TaskDateTime to = TaskDateTime.parseSaved(parts[EVENT_END_INDEX]);
        if (from == null || to == null) {
            return null;
        }
        Task task = new Event(parts[DESCRIPTION_INDEX], from, to);
        applyArchiveFlag(task, isArchived);
        return task;
    }

    private static void applyArchiveFlag(Task task, boolean isArchived) {
        if (isArchived) {
            task.archive();
        }
    }

    /** Returns the type identifying this task. */
    protected abstract TaskType getType();

    /** Returns the date or time details to append to the task description. */
    protected abstract String getDateTimeText();
}
