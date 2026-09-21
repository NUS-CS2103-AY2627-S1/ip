package quackie.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import quackie.task.Deadline;
import quackie.task.Event;
import quackie.task.Task;
import quackie.task.TaskList;
import quackie.task.ToDo;

/**
 * Persists Quackie's tasks in a local text file.
 *
 * <p>Each record stores a task type, completion status, and Base64-encoded
 * task details separated by pipe characters. Encoding the details keeps
 * descriptions and dates safe when they contain the separator.
 */
public class Storage {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "quackie.txt");
    private static final String FIELD_SEPARATOR = "|";
    private final Path dataFile;

    /** Creates a storage service using Quackie's default data file. */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates a storage service using a caller-supplied file, useful for isolated environments.
     *
     * @param dataFile the file in which tasks should be stored
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Saves the current tasks to the data file.
     *
     * @param tasks the task list to save
     * @throws IOException if the data directory or file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        assert tasks != null : "Task list to save must not be null";

        Path parentDirectory = dataFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(dataFile, StandardCharsets.UTF_8)) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(serialize(tasks.get(i)));
                writer.newLine();
            }
        }
    }

    /**
     * Loads saved tasks into the supplied task list.
     *
     * @param tasks the task list into which loaded tasks are placed
     * @throws IOException if the data file cannot be read
     */
    public void load(TaskList tasks) throws IOException {
        assert tasks != null : "Task list to load into must not be null";

        if (!Files.exists(dataFile)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(deserialize(line));
            }
        }
    }

    /**
     * Converts one task into its storage record.
     *
     * @param task the task to convert
     * @return a pipe-separated record suitable for writing to the data file
     */
    private String serialize(Task task) {
        String status = task.isDone() ? "1" : "0";
        String description = encode(task.getDescription());

        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, "D", status, description,
                    encode(deadline.getBy()));
        }

        if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", status, description,
                    encode(event.getFrom()), encode(event.getTo()));
        }

        return String.join(FIELD_SEPARATOR, "T", status, description);
    }

    /**
     * Encodes text so that separators in user input cannot corrupt a record.
     *
     * @param value the text to encode
     * @return the Base64 representation of the text
     */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Converts one storage record back into a task.
     *
     * @param line the storage record to convert
     * @return the task represented by the record
     */
    private Task deserialize(String line) throws IOException {
        String[] fields = line.split("\\|", -1);
        String taskType = fields.length > 0 ? fields[0] : "";
        int expectedFieldCount = switch (taskType) {
            case "D" -> 4;
            case "E" -> 5;
            case "T" -> 3;
            default -> throw new IOException("Unknown task type: " + taskType);
        };
        if (fields.length != expectedFieldCount) {
            throw new IOException("Malformed task record");
        }
        if (!"0".equals(fields[1]) && !"1".equals(fields[1])) {
            throw new IOException("Invalid task status");
        }

        try {
            Task task = switch (taskType) {
                case "D" -> new Deadline(decode(fields[2]), decode(fields[3]));
                case "E" -> new Event(decode(fields[2]), decode(fields[3]), decode(fields[4]));
                case "T" -> new ToDo(decode(fields[2]));
                default -> throw new IOException("Unknown task type: " + taskType);
            };

            if ("1".equals(fields[1])) {
                task.markAsDone();
            }
            return task;
        } catch (IllegalArgumentException exception) {
            throw new IOException("Malformed task record", exception);
        }
    }

    /**
     * Decodes Base64 text from a storage record.
     *
     * @param value the encoded text
     * @return the decoded text
     */
    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
