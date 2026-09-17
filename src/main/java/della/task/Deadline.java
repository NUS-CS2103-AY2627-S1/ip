package della.task;

import java.time.LocalDateTime;

import della.util.DateParser;

/**
 * Represents a task that must be completed by a specified date and time.
 */
public class Deadline extends Task {
    private final LocalDateTime deadline;

    /**
     * Creates an incomplete deadline task with the specified name and deadline.
     *
     * @param name Description of the deadline task.
     * @param by Date and time by which the task is due.
     */
    public Deadline(String name, LocalDateTime by) {
        super(name);
        assert by != null : "Deadline timestamp must not be null";
        this.deadline = by;
    }

    /**
     * Creates a deadline task with the specified name, completion status, and deadline.
     *
     * @param name Description of the deadline task.
     * @param isDone Completion status of the deadline task.
     * @param by Date and time by which the task is due.
     */
    public Deadline(String name, boolean isDone, LocalDateTime by) {
        super(name);
        assert by != null : "Deadline timestamp must not be null";
        this.isDone = isDone;
        this.deadline = by;
    }

    @Override
    public String formatForStorage() {
        return String.format("%s|%s|%s|%s", "D", super.isDone ? "1" : "0", this.name,
                DateParser.printDateTime(this.deadline));
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by %s)", super.toString(), DateParser.printDateTime(this.deadline));
    }
}
