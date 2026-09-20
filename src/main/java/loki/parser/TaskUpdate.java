package loki.parser;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Stores the validated values supplied by one task update command.
 *
 * <p>Absent optional values represent fields that the command did not supply.
 * The value object is immutable so that validation can finish before a task
 * list is changed.</p>
 */
public final class TaskUpdate {
    private final int taskNumber;
    private final Optional<String> title;
    private final Optional<LocalDateTime> deadline;
    private final Optional<LocalDateTime> eventStart;
    private final Optional<LocalDateTime> eventEnd;

    /**
     * Creates a parsed task update.
     *
     * @param taskNumber the one-based task number.
     * @param title the replacement title, if supplied.
     * @param deadline the replacement deadline, if supplied.
     * @param eventStart the replacement event start, if supplied.
     * @param eventEnd the replacement event end, if supplied.
     * @throws IllegalArgumentException if no field is supplied.
     */
    public TaskUpdate(int taskNumber, Optional<String> title, Optional<LocalDateTime> deadline,
            Optional<LocalDateTime> eventStart, Optional<LocalDateTime> eventEnd) {
        this.taskNumber = taskNumber;
        this.title = Objects.requireNonNull(title);
        this.deadline = Objects.requireNonNull(deadline);
        this.eventStart = Objects.requireNonNull(eventStart);
        this.eventEnd = Objects.requireNonNull(eventEnd);
        if (title.isEmpty() && deadline.isEmpty() && eventStart.isEmpty() && eventEnd.isEmpty()) {
            throw new IllegalArgumentException("Task update must contain at least one field");
        }
    }

    /**
     * Returns the one-based task number targeted by this update.
     *
     * @return the task number.
     */
    public int getTaskNumber() {
        return taskNumber;
    }

    /**
     * Returns the replacement title, if one was supplied.
     *
     * @return the optional replacement title.
     */
    public Optional<String> getTitle() {
        return title;
    }

    /**
     * Returns the replacement deadline, if one was supplied.
     *
     * @return the optional replacement deadline.
     */
    public Optional<LocalDateTime> getDeadline() {
        return deadline;
    }

    /**
     * Returns the replacement event start, if one was supplied.
     *
     * @return the optional replacement event start.
     */
    public Optional<LocalDateTime> getEventStart() {
        return eventStart;
    }

    /**
     * Returns the replacement event end, if one was supplied.
     *
     * @return the optional replacement event end.
     */
    public Optional<LocalDateTime> getEventEnd() {
        return eventEnd;
    }
}
