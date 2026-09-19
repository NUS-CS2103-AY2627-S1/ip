package carl.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a generic task in the application.
 */
public class Task {
    /** Current completion state. */
    protected TaskStatus status;

    /** Task category. */
    protected final TaskType type;

    /** User-provided task description. */
    protected final String name;

    /**
     * Constructs a Task with the specified item and type.
     * The task is initialized as not done by default.
     *
     * @param name The description or name of the task.
     * @param type The type of the task (e.g., TODO, DEADLINE).
     */
    public Task(String name, TaskType type) {
        this.name = Objects.requireNonNull(name).strip();
        this.status = TaskStatus.NOT_DONE;
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Constructs a Task with the specified item, status, and type.
     *
     * @param name   The description or name of the task.
     * @param status The current completion status of the task.
     * @param type   The type of the task.
     */
    public Task(String name, TaskStatus status, TaskType type) {
        this.name = Objects.requireNonNull(name).strip();
        this.status = Objects.requireNonNull(status);
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public String toString() {
        return String.format("%s %s", status, this.name);
    }

    /**
     * Marks the task as done.
     *
     * @return true if the task was successfully marked as done, false if it was already done.
     */
    public boolean tryMarkAsDone() {
        if (this.status == TaskStatus.DONE) {
            return false;
        }

        this.status = TaskStatus.DONE;
        return true;
    }

    /**
     * Marks the task as not done.
     *
     * @return true if the task was successfully marked as not done, false if it was already not done.
     */
    public boolean tryUnmarkAsDone() {
        if (this.status == TaskStatus.NOT_DONE) {
            return false;
        }

        this.status = TaskStatus.NOT_DONE;
        return true;
    }

    /**
     * Formats the task into a string representation suitable for saving to storage.
     *
     * @return The formatted string for file saving.
     */
    public String toSaveFormat() {
        return String.format("%s | %d | %s", type, status.toInt(), name);
    }

    /**
     * Creates a specific Task instance based on provided TaskData.
     *
     * @param data The data object containing task details.
     * @return A specific Task object (Todo, Event, or Deadline).
     */
    public static Task createFromData(TaskData data) {
        return switch (data.type) {
            case TODO -> new Todo(data.name, data.status);
            case EVENT -> new Event(data.name, data.status, data.from, data.to);
            case DEADLINE -> new Deadline(data.name, data.status, data.from);
        };
    }

    /**
     * Checks if the task's name matches the given keyword.
     *
     * @param keyword The keyword to search for.
     * @return true if the name contains the keyword, false otherwise.
     */
    public boolean hasNameMatch(String keyword) {
        return this.name.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Checks whether another task contains the same user-defined details, ignoring completion status.
     *
     * @param other task to compare against.
     * @return true when the tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null && getIdentity().equals(other.getIdentity());
    }

    /**
     * Returns a normalized identity used to detect duplicate task details.
     *
     * @return normalized task identity.
     */
    protected String getIdentity() {
        return type + "|" + name.toLowerCase(Locale.ROOT);
    }

    /**
     * Checks if the task is due on the specified target date.
     *
     * @param targetDate The date to check against.
     * @return true if the task is due on the target date, false otherwise.
     */
    public boolean isDueOn(LocalDate targetDate) {
        return isOnDate(targetDate);
    }

    /**
     * Helper method to determine if the task falls on a specific date.
     * Overridden by subclasses that have specific dates.
     *
     * @param targetDate The date to check against.
     * @return true if the task is on the target date, false otherwise.
     */
    protected boolean isOnDate(LocalDate targetDate) {
        return false;
    }

    /**
     * A Data Transfer Object used for parsing and instantiating specific Task types.
     */
    public static class TaskData {
        private final TaskType type;
        private final String name;
        private final TaskStatus status;
        private final LocalDateTime from;
        private final LocalDateTime to;

        /**
         * Constructs a TaskData object with all necessary fields for any task type.
         *
         * @param type   The type of the task.
         * @param status The completion status of the task.
         * @param name   The description of the task.
         * @param from   The start or deadline time (if applicable).
         * @param to     The end time (if applicable).
         */
        public TaskData(TaskType type, TaskStatus status, String name, LocalDateTime from, LocalDateTime to) {
            this.type = type;
            this.name = name;
            this.status = status;
            this.from = from;
            this.to = to;
        }
    }
}
