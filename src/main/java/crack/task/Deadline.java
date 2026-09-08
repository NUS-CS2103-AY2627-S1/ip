package crack.task;

import java.time.LocalDate;

import crack.CrackException;

/**
 * A task that has to be done by a certain date, and maybe by a certain time.
 */
public class Deadline extends Task {
    private final TaskDate by;

    private Deadline(String description, TaskDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Creates a deadline from a date written in any of the formats we accept.
     *
     * @param description What the user wants to get done.
     * @param by When it is due, as the user typed it.
     * @return The new deadline.
     * @throws CrackException If the date cannot be read.
     */
    public static Deadline of(String description, String by) throws CrackException {
        return new Deadline(description, TaskDate.parse(by));
    }

    /**
     * Returns whether this deadline falls due on the given day.
     *
     * @param date The day being asked about.
     * @return True if it is due that day.
     */
    @Override
    public boolean isOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + by.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
