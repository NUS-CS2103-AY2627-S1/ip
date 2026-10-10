package jasper.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import jasper.JasperException;
import jasper.task.Deadline;

/**
 * Provides methods to serialize and deserialize Deadline tasks.
 */
public class DeadlineSerializer {
    /** Number of chunks in saved format that represent different parameters  */
    private static final int NUM_PARTS = 4;

    /**
     * Serializes a Deadline task into a formatted string.
     *
     * @param deadline Deadline task to serialize.
     * @return Formatted string representation of the task.
     */
    public static String serialize(Deadline deadline) {
        int status = deadline.isDone() ? 1 : 0;
        return "D | " + status + " | " + deadline.getDescription() + " | " + deadline.getBy();
    }

    /**
     * Deserializes a formatted string into a Deadline task.
     *
     * @param line String representation of the task to be parsed.
     * @return Deserialized Deadline task.
     * @throws JasperException If the string format is invalid or cannot be parsed.
     */
    public static Deadline deserialize(String line) throws JasperException {
        String[] parts = line.split(" \\| ", NUM_PARTS);
        if (parts.length < NUM_PARTS) {
            throw new JasperException("Error reading savefile!");
        }
        Deadline deadline;
        try {
            deadline = new Deadline(parts[2], LocalDateTime.parse(parts[3]));
        } catch (DateTimeParseException e) {
            throw new JasperException("Error reading savefile!");
        }
        if (parts[1].equals("1")) {
            deadline.markDone();
        }
        return deadline;
    }
}
