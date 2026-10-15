package vector.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import vector.VectorException;
import vector.parser.DateTimeParser;

/**
 * Represents a Deadline task.
 */
public class Deadline extends Task {

    protected LocalDateTime byTime;

    /**
     * Constructs a Deadline task.
     *
     * @param description The description of the deadline.
     * @param by The date/time by which the task needs to be done.
     * @throws VectorException if the date format is invalid.
     */
    public Deadline(String description, String by) throws VectorException {
        super(description);
        this.byTime = DateTimeParser.parse(by);
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return byTime.toLocalDate().equals(date);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.format(byTime) + ")";
    }

    @Override
    public String toFileFormat() {
        return "D" + super.toFileFormat() + " | " + DateTimeParser.formatForFile(byTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Deadline deadline = (Deadline) obj;
        return byTime.equals(deadline.byTime);
    }
}

