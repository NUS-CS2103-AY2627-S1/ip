package orbit.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import orbit.exception.OrbitException;
import orbit.task.Deadline;
import orbit.task.Event;
import orbit.task.Task;
import orbit.task.Todo;

/**
 * Loads and saves Orbit tasks in a local text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = "\t";

    private final Path filePath;

    /**
     * Creates storage backed by the given path.
     *
     * @param filePath path to Orbit's data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks in their saved order, creating an empty data file when needed.
     *
     * @return saved tasks
     * @throws OrbitException if the file cannot be read or contains invalid data
     */
    public ArrayList<Task> load() throws OrbitException {
        try {
            createDataFileIfMissing();
            ArrayList<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    tasks.add(parseTask(line, i + 1));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new OrbitException("Could not load tasks from " + filePath + ".");
        }
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks tasks to save
     * @throws OrbitException if the file cannot be written
     */
    public void save(List<Task> tasks) throws OrbitException {
        try {
            createDataFileIfMissing();
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new OrbitException("Could not save tasks to " + filePath + ".");
        }
    }

    private void createDataFileIfMissing() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    private Task parseTask(String line, int lineNumber) throws OrbitException {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        try {
            boolean isDone = parseStatus(fields);
            String description = decode(fields[2]);
            switch (fields[0]) {
                case "T":
                    requireFieldCount(fields, 3);
                    return new Todo(description, isDone);
                case "D":
                    requireFieldCount(fields, 4);
                    return new Deadline(description, LocalDate.parse(decode(fields[3])), isDone);
                case "E":
                    requireFieldCount(fields, 5);
                    return new Event(description, LocalDate.parse(decode(fields[3])),
                            LocalDate.parse(decode(fields[4])), isDone);
                default:
                    throw new IllegalArgumentException("Unknown task type");
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException
                | DateTimeParseException exception) {
            throw new OrbitException("The data file is invalid at line " + lineNumber + ".");
        }
    }

    private boolean parseStatus(String[] fields) {
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid completion status");
        }
        return fields[1].equals("1");
    }

    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Invalid field count");
        }
    }

    private String formatTask(Task task) throws OrbitException {
        String status = task.isDone() ? "1" : "0";
        String commonFields = status + FIELD_SEPARATOR + encode(task.getDescription());
        if (task instanceof Todo) {
            return "T" + FIELD_SEPARATOR + commonFields;
        }
        if (task instanceof Deadline deadline) {
            return "D" + FIELD_SEPARATOR + commonFields + FIELD_SEPARATOR + encode(deadline.getBy().toString());
        }
        if (task instanceof Event event) {
            return "E" + FIELD_SEPARATOR + commonFields + FIELD_SEPARATOR
                    + encode(event.getFrom().toString()) + FIELD_SEPARATOR + encode(event.getTo().toString());
        }
        throw new OrbitException("Could not save an unsupported task type.");
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        byte[] decodedBytes = Base64.getDecoder().decode(value);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
