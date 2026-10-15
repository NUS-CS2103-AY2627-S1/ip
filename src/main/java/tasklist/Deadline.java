package tasklist;

import cxception.InvalidDateException;
import parser.DateTimeParser;

/**
 * Represents a task with a deadline date/time.
 */
public class Deadline extends Task {
    private MyDateTime by;

    /**
     * Constructs a {@code Deadline} task.
     *
     * @param task the task description.
     * @param byStr the raw deadline date/time string.
     * @throws InvalidDateException if {@code byStr} cannot be parsed.
     */
    public Deadline(String task, String byStr) throws InvalidDateException {
        super(task);
        super.category = TaskType.DEADLINE;
        this.by = DateTimeParser.parse(byStr);
    }

    /**
     * Returns the string representation of this deadline task for display.
     *
     * @return the formatted display string.
     */
    @Override
    public String toString() {
        return String.format("[D] [%s] %s (By: %s)", super.done ? "X" : " ", super.objective, by.toDisplayString());
    }

    /**
     * Returns the string representation of this deadline task for file storage.
     *
     * @return the formatted save-file string.
     */
    @Override
    public String fileFormatText() {
        return String.format("D | %s | %s", super.objective, by.toFileString());
    }
}
