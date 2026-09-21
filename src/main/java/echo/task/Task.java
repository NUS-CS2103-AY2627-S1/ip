package echo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import echo.exception.CorruptFormatException;
import echo.exception.StorageException;

/**
 * Represents a task description and whether the task has been completed.
 * Class with Codex contribution.
 */
public abstract class Task {
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private final String description;
    private LocalDateTime completedAt;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text that describes the task
     */
    public Task(String description) {
        Objects.requireNonNull(description, "A task description must not be null.");
        if (description.isBlank()) {
            throw new IllegalArgumentException("A task description must not be blank.");
        }
        if (description.contains("|") || description.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("A task description contains unsupported characters.");
        }
        this.description = description;
        this.completedAt = null;
    }

    /**
     * Gets description of the task.
     * @return description string of the task.
     */
    public String getDescription() {
        return description;
    }

    /** Marks this task as complete. */
    public void markDone() {
        completedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    /** Marks this task as complete at the supplied local date-time. */
    public void markDone(LocalDateTime completionTime) {
        completedAt = Objects.requireNonNull(completionTime).truncatedTo(ChronoUnit.MINUTES);
    }

    /** Marks this task as incomplete. */
    public void markNotDone() {
        completedAt = null;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return completedAt != null;
    }

    /** Returns the local date-time at which this task was last completed. */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    /**
     * Returns this task's details in the pipe-delimited save-file format:
     * its optional completion date-time followed by the description.
     * Subclasses prepend their type letter and append any extra fields.
     */
    public String toSaveFormat() {
        String completionTimeText = completedAt == null ? "" : completedAt.toString();
        return completionTimeText + " | " + description;
    }

    /**
     * Reconstructs a task from one of its save-file lines, e.g.,
     * {@code D | 2026-09-06T18:15 | return book | 2026-09-08T18:00}
     * becomes a completed Deadline.
     * Dispatches on the leading type letter to the matching subclass,
     * which validates and interprets its remaining fields.
     *
     * @param saveFormat one pipe-delimited line written by {@link #toSaveFormat()}
     * @return the reconstructed task, marked done when its flag is {@code 1}
     * @throws StorageException when the line does not follow the save format
     */
    public static Task fromSaveFormat(String saveFormat) throws StorageException {
        String[] fields = saveFormat.split(FIELD_SEPARATOR_REGEX, -1);
        if (!hasValidBaseFields(fields)) {
            throw new CorruptFormatException(saveFormat);
        }
        try {
            Task task = createTask(fields, saveFormat);
            if (!fields[1].isEmpty()) {
                LocalDateTime completionTime = LocalDateTime.parse(fields[1]);
                if (completionTime.getSecond() != 0 || completionTime.getNano() != 0
                        || !fields[1].equals(completionTime.toString())) {
                    throw new IllegalArgumentException("A completion date-time must use minute precision.");
                }
                task.markDone(completionTime);
            }
            return task;
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new CorruptFormatException(saveFormat);
        }
    }

    private static boolean hasValidBaseFields(String[] fields) {
        return fields.length >= 3
                && !fields[2].isEmpty()
                && !fields[0].isEmpty();
    }

    private static Task createTask(String[] fields, String saveFormat) throws CorruptFormatException {
        TaskType type = TaskType.fromString(fields[0]);
        return switch (type) {
            case TODO -> Todo.fromSaveFormat(fields);
            case DEADLINE -> Deadline.fromSaveFormat(fields);
            case EVENT -> Event.fromSaveFormat(fields);
            default -> throw new CorruptFormatException(saveFormat);
        };
    }

    /**
     * Returns the {@link TaskType} of the task
     * @return {@link TaskType} of the task
     */
    public abstract TaskType getTaskType();

    /**
     * Returns whether another task has the same identity-defining details.
     * Completion status is deliberately excluded.
     *
     * @param other Task to compare.
     * @return Whether both tasks have the same type, description, and schedule.
     */
    final boolean hasSameDetails(Task other) {
        return other != null
                && getTaskType() == other.getTaskType()
                && description.equals(other.description)
                && hasSameSchedule(other);
    }

    boolean hasSameSchedule(Task other) {
        return true;
    }

    /**
     * Creates String representation of a task.
     * Contains the doneness and description only.
     * @return String representation of a Task
     */
    @Override
    public String toString() {
        return "[" + (isDone() ? "X" : " ") + "] " + description;
    }
}
