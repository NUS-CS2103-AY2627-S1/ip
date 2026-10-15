package carlo.task;

import carlo.exception.CarloException;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final CarloDateTime dueTime;

    /**
     * Creates an incomplete deadline.
     *
     * <p>{@code dueTime} is parsed as a relative keyword ({@code today},
     * {@code tomorrow}, {@code yesterday}), a date (e.g.
     * {@code 2019-12-02}), or a date and time (e.g. {@code 2019-12-02
     * 1800}).
     *
     * @param description text describing the task
     * @param dueTime the deadline, as entered by the user
     * @throws CarloException if {@code dueTime} could not be understood as a date/time
     */
    public Deadline(String description, String dueTime) throws CarloException {
        super(description);
        this.dueTime = CarloDateTime.parse(dueTime);

        if (!this.dueTime.isParsed()) {
            throw new CarloException(
                    "I couldn't understand '" + dueTime + "' as a date/time! "
                            + CarloDateTime.FORMAT_HELP);
        }
    }

    /**
     * Returns the due time of this deadline as display text.
     *
     * @return the deadline, formatted for display
     */
    public String getDueTime() {
        return dueTime.toDisplayString();
    }

    /**
     * Returns the parsed due date/time of this deadline.
     *
     * @return the due date/time, which may hold free-form text if it could not be parsed
     */
    public CarloDateTime getDueDateTime() {
        return dueTime;
    }

    /**
     * Returns this deadline in the command-line display format.
     *
     * @return the task type, completion status, description, and deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueTime.toDisplayString() + ")";
    }

    /**
     * Returns this deadline in the on-disk save format.
     *
     * @return the type letter, completion flag, description, and due time, separated by " | "
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + dueTime.toFileFormat();
    }
}
