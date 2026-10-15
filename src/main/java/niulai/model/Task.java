package niulai.model;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Represents a task in the NiuLai task list.
 */
public class Task {
    /** Matches horizontal whitespace that should be stored as one regular space. */
    private static final Pattern HORIZONTAL_WHITESPACE_PATTERN =
            Pattern.compile("[\\p{Zs}\\t]+");

    /** The text describing the task. */
    private final String description;

    /** The completion state of the task. */
    private TaskStatus status;

    /**
     * Creates a new incomplete task.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank.");
        }

        this.description = normalizeField(description);
        this.status = TaskStatus.PENDING;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} if the task is completed, otherwise a blank space
     */
    public String getStatusIcon() {
        return status == TaskStatus.COMPLETED ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        status = TaskStatus.COMPLETED;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        status = TaskStatus.PENDING;
    }

    /**
     * Returns the completion state of this task.
     *
     * @return the task status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the one-letter type marker used when displaying this task.
     *
     * @return the task type marker
     */
    public String getTypeIcon() {
        return getType().getIcon();
    }

    /** @return the finite type represented by this task */
    public TaskType getType() {
        return TaskType.TODO;
    }

    /**
     * Checks whether another task has the same type and normalized details.
     *
     * @param other the task to compare
     * @return whether the tasks represent the same unique task
     */
    public boolean hasSameIdentity(Task other) {
        return other != null && getType() == other.getType()
                && getIdentityFields().equals(other.getIdentityFields());
    }

    /**
     * Returns the line used to save this task to disk.
     *
     * @return the task type, completion state, and description
     */
    public String toStorageString() {
        return getTypeIcon() + " | " + status.getStorageValue() + " | "
                + escapeStorageField(description);
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     *
     * @param value the field to escape
     * @return the escaped field
     */
    protected static String escapeStorageField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Normalizes a user-provided task field and rejects unsafe control characters.
     *
     * @param value the field value
     * @return the trimmed value with horizontal whitespace collapsed
     * @throws IllegalArgumentException if the field is blank or contains a control character
     */
    protected static String normalizeField(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Task fields cannot be blank.");
        }

        String strippedValue = value.strip();
        for (int i = 0; i < strippedValue.length(); i++) {
            char character = strippedValue.charAt(i);
            if (Character.isISOControl(character) && character != '\t') {
                throw new IllegalArgumentException(
                        "Task fields cannot contain control characters."
                );
            }
        }
        return HORIZONTAL_WHITESPACE_PATTERN.matcher(strippedValue).replaceAll(" ");
    }

    /** Returns normalized fields used to decide whether two tasks are duplicates. */
    protected List<String> getIdentityFields() {
        return List.of(normalizeIdentityValue(description));
    }

    /** Returns a case-insensitive representation of a normalized identity field. */
    protected static String normalizeIdentityValue(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the display representation of this task.
     *
     * @return the status icon and task description
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }

    /** Represents the task types supported by the storage and display formats. */
    public enum TaskType {
        /** A task without an attached date or time. */
        TODO("T"),

        /** A task with a completion deadline. */
        DEADLINE("D"),

        /** A task with a start and end time. */
        EVENT("E");

        /** The marker used to persist and display this task type. */
        private final String icon;

        /** Creates a task type with its storage and display marker. */
        TaskType(String icon) {
            this.icon = icon;
        }

        /** @return the marker used to persist and display this task type */
        public String getIcon() {
            return icon;
        }

        /**
         * Converts a persisted type marker into a task type.
         *
         * @param icon the persisted task-type marker
         * @return the corresponding task type
         * @throws IllegalArgumentException if the marker is not supported
         */
        public static TaskType fromIcon(String icon) {
            for (TaskType type : values()) {
                if (type.icon.equals(icon)) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Unknown task type icon: " + icon);
        }
    }
}
