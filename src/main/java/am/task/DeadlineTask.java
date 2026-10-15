package am.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import am.storage.CorruptedDataException;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class DeadlineTask extends Task {
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a");
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param name task description
     * @param by deadline date and time
     */
    public DeadlineTask(String name, LocalDateTime by) {
        super(name);
        assert by != null : "A deadline task must have a deadline";
        this.by = by;
    }

    /**
     * Creates an incomplete deadline task from a date or date-time string.
     *
     * @param name task description
     * @param by deadline in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format
     */
    public DeadlineTask(String name, String by) {
        this(name, parseDateTime(by));
    }

    private DeadlineTask(String name, LocalDateTime by, boolean done) {
        super(name, done);
        this.by = by;
    }

    public LocalDateTime getBy() {
        return by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toSerialized() {
        return String.format("D | %s | %s | %s", getSerializedStatus(), getName(), by);
    }

    /**
     * Reconstructs a deadline task from its persisted representation.
     *
     * @param line serialized deadline task data
     * @return reconstructed deadline task
     * @throws CorruptedDataException if the line has an invalid format
     */
    public static DeadlineTask fromSerialized(String line) throws CorruptedDataException {
        if (line == null || line.isBlank()) {
            throw new CorruptedDataException("Deadline task data is empty");
        }

        String[] parts = line.trim().split("\\s*\\|\\s*", -1);
        if (parts.length != 4 || !parts[0].equals("D")) {
            throw new CorruptedDataException("Malformed deadline task data: " + line);
        }

        boolean done = parseCompletionStatus(parts[1], "deadline");
        return new DeadlineTask(parts[2], LocalDateTime.parse(parts[3]), done);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(),
                by.format(DISPLAY_FORMAT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPast(LocalDateTime datetime) {
        return by.isBefore(datetime);
    }

    /** Parses a supported date or date-time string with a suitable fallback time. */
    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, INPUT_FORMAT);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(value, DATE_FORMAT).atTime(LocalTime.of(23, 59));
        }
    }
}
