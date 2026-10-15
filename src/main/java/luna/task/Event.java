package luna.task;

import java.time.LocalDate;

import luna.exception.LunaException;

/**
 * Represents a {@code Task} with an associated start and end date.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event task.
     *
     * @param description Task description.
     * @param startDate Event start date as a {@link LocalDate}.
     * @param endDate Event end date as a {@link LocalDate}.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                TaskDateFormatter.formatForDisplay(startDate),
                TaskDateFormatter.formatForDisplay(endDate));
    }

    @Override
    public String toFileString() {
        return "E | " + formatDoneFlag(isDone()) + " | " + getDescription() + " | "
                + TaskDateFormatter.formatForStorage(startDate) + " | "
                + TaskDateFormatter.formatForStorage(endDate);
    }

    /**
     * Creates an {@link Event} from saved parts of a file line.
     *
     * @param parts Split components of a saved line.
     * @return Parsed event.
     * @throws LunaException If the line format is invalid.
     */
    protected static Event fromFileParts(String[] parts) throws LunaException {
        if (parts.length != 5) {
            throw new LunaException("Invalid event line in data file.");
        }

        LocalDate from = TaskDateFormatter.parse(parts[3]);
        LocalDate to = TaskDateFormatter.parse(parts[4]);
        Event event = new Event(parts[2], from, to);
        if (parseDoneFlag(parts[1])) {
            event.markDone();
        }
        return event;
    }
}
