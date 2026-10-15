package luna.task;

import java.time.LocalDate; // Represents a date without the time

import luna.exception.LunaException;

/**
 * Represents a {@code Task} with an associated deadline.
 */
public class Deadline extends Task {
    private final LocalDate deadline;

    /**
     * Creates a deadline task.
     *
     * @param description Task description.
     * @param deadline Deadline as a {@link LocalDate}.
     */
    public Deadline(String description, LocalDate deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(),
                TaskDateFormatter.formatForDisplay(deadline));
    }

    @Override
    public String toFileString() {
        return "D | " + formatDoneFlag(isDone()) + " | " + getDescription() + " | "
                + TaskDateFormatter.formatForStorage(deadline);
    }

    /**
     * Creates a {@link Deadline} from saved parts of a file line.
     *
     * @param parts Split components of a saved line.
     * @return Parsed deadline.
     * @throws LunaException If the line format is invalid.
     */
    protected static Deadline fromFileParts(String[] parts) throws LunaException {
        if (parts.length != 4) {
            throw new LunaException("Invalid deadline line in data file");
        }

        LocalDate date = TaskDateFormatter.parse(parts[3]);
        Deadline deadline = new Deadline(parts[2], date);
        if (parseDoneFlag(parts[1])) {
            deadline.markDone();
        }
        return deadline;
    }
}
