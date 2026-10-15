package tasklist;

import cxception.InvalidDateException;
import parser.DateTimeParser;

/**
 * Represents a task that occurs over a time span, from a start to an end date/time.
 */
public class Event extends Task {
    private MyDateTime from;
    private MyDateTime to;

    /**
     * Constructs an {@code Event} task.
     *
     * @param task the task description.
     * @param fromStr the raw start date/time string.
     * @param toStr the raw end date/time string.
     * @throws InvalidDateException if {@code fromStr} or {@code toStr} cannot be parsed.
     */
    public Event(String task, String fromStr, String toStr) throws InvalidDateException {
        super(task);
        super.category = TaskType.EVENT;
        this.from = DateTimeParser.parse(fromStr);
        this.to = DateTimeParser.parse(toStr);
    }

    /**
     * Returns the string representation of this event task for display.
     *
     * @return the formatted display string.
     */
    @Override
    public String toString() {
        return String.format("[E] [%s] %s (FROM: %s, TO: %s)",
                super.done ? "X" : " ", super.objective, from.toDisplayString(), to.toDisplayString());
    }

    /**
     * Returns the string representation of this event task for file storage.
     *
     * @return the formatted save-file string.
     */
    @Override
    public String fileFormatText() {
        return String.format("E | %s | %s | %s", super.objective, from.toFileString(), to.toFileString());
    }
}
