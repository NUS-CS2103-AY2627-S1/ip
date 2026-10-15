package spoon.task;

import java.time.LocalDate;

import spoon.exception.InvalidFormatException;
import spoon.util.DateFormat;

/**
 * Represents a Deadline (subclass of Task) with additional field deadline.
 */
public class Deadline extends Task {
    private final DateFormat.ParseResult deadline;

    // Constructor
    public Deadline(String name, String deadline) throws InvalidFormatException {
        super(name);
        this.deadline = DateFormat.parse(deadline);
    }

    // Methods
    @Override
    public boolean isDueOn(LocalDate date) {
        return DateFormat.isDueOn(deadline.dateTime(), date);
    }

    @Override
    public boolean isDueBy(LocalDate date) {
        return DateFormat.isDueBy(deadline.dateTime(), date);
    }

    @Override
    public String format() {
        return "D | " + super.format()
                + " | " + DateFormat.toStorage(deadline);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + DateFormat.toDisplay(deadline) + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Deadline otherDeadline)) {
            return false;
        }
        return super.equals(object) && java.util.Objects.equals(this.deadline, otherDeadline.deadline);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), this.deadline);
    }
}
