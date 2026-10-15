package odysseus.task;

import odysseus.OdysseusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** A task that must be done by a specific date/time. */
public class Deadline extends Task {
    protected LocalDate by;
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    /**
     * Constructs the deadline task.
     *
     * @param description string description of the task
     * @param by deadline of the task in format YYYY-MM-DD
     * @throws OdysseusException if deadline date provided cannot be parsed
     */
    public Deadline(String description, String by) throws OdysseusException {
        super(description);
        try {
            this.by = LocalDate.parse(by);
        } catch (DateTimeParseException e) {
            throw new OdysseusException("Invalid by input, please give in the format of YYYY-MM-DD...");
        }
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return this.by.isEqual(date);
    }

    /**
     * {@inheritDoc}
     *
     * @return the deadline encoded as a save-file line
     */
    @Override
    public String toSaveFormat() {
        return String.format("%s | %d | %s | %s",
                "D",
                this.isDone ? 1 : 0,
                this.description,
                this.by);
    }

    @Override
    public boolean isSameTask(Task other) {
        if (!super.isSameTask(other)) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return this.by.equals(otherDeadline.by);
    }

    @Override
    public void update(String flag, String value) throws OdysseusException {
        if (flag.equals("/by")) {
            try {
                this.by = LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                throw new OdysseusException("That date won't hold. Use YYYY-MM-DD.");
            }
        } else {
            super.update(flag, value);
        }
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() +
                " (by: " + this.by.format(DISPLAY_FORMAT) + ")";
    }
}
