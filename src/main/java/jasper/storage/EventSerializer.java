package jasper.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import jasper.JasperException;
import jasper.task.Event;

/**
 * Provides methods to serialize and deserialize Event tasks.
 */
public class EventSerializer {
    /** Number of chunks in saved format that represent different parameters  */
    private static final int NUM_PARTS = 5;

    /**
     * Serializes an Event task into a formatted string.
     *
     * @param event Event task to serialize.
     * @return Formatted string representation of the task.
     */
    public static String serialize(Event event) {
        int status = event.isDone() ? 1 : 0;
        return "E | " + status + " | " + event.getDescription() + " | " + event.getFrom() + " | " + event.getTo();
    }

    /**
     * Deserializes a formatted string into an Event task.
     *
     * @param line String representation of the task to be parsed.
     * @return Deserialized Event task.
     * @throws JasperException If the string format is invalid or cannot be parsed.
     */
    public static Event deserialize(String line) throws JasperException {
        String[] parts = line.split(" \\| ", NUM_PARTS);
        if (parts.length < NUM_PARTS) {
            throw new JasperException("Error reading savefile!");
        }
        Event event;
        try {
            event = new Event(parts[2], LocalDateTime.parse(parts[3]), LocalDateTime.parse(parts[4]));
        } catch (DateTimeParseException e) {
            throw new JasperException("Error reading savefile!");
        }
        if (parts[1].equals("1")) {
            event.markDone();
        }
        return event;
    }
}
