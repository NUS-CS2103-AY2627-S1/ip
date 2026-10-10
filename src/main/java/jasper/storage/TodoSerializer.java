package jasper.storage;

import jasper.JasperException;
import jasper.task.Todo;

/**
 * Provides methods to serialize and deserialize Todo tasks.
 */
public class TodoSerializer {
    /** Number of chunks in saved format that represent different parameters */
    private static final int NUM_PARTS = 3;

    /**
     * Serializes a Todo task into a formatted string.
     *
     * @param todo Todo task to serialize.
     * @return Formatted string representation of the task.
     */
    public static String serialize(Todo todo) {
        int status = todo.isDone() ? 1 : 0;
        return "T | " + status + " | " + todo.getDescription();
    }

    /**
     * Deserializes a formatted string into a Todo task.
     *
     * @param line String representation of the task to be parsed.
     * @return Deserialized Todo task.
     * @throws JasperException If the string format is invalid.
     */
    public static Todo deserialize(String line) throws JasperException {
        String[] parts = line.split(" \\| ", NUM_PARTS);
        if (parts.length < NUM_PARTS) {
            throw new JasperException("Error reading savefile!");
        }
        Todo todo = new Todo(parts[2]);
        if (parts[1].equals("1")) {
            todo.markDone();
        }
        return todo;
    }
}
