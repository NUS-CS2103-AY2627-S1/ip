package pebby.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import pebby.task.Deadline;
import pebby.task.Event;
import pebby.task.Task;
import pebby.task.Todo;

/**
 * Saves Pebby's tasks to, and restores them from, a local text file.
 */
public class Storage {
    /** The default relative path used to store Pebby's saved tasks. */
    public static final Path DEFAULT_FILE_PATH = Path.of("data", "pebby.txt");
    private static final String FIELD_SEPARATOR = " | ";
    private final Path filePath;

    /**
     * Creates storage using Pebby's default data file or a test-supplied path.
     */
    public Storage() {
        this(Path.of(System.getProperty("pebby.storage.path", DEFAULT_FILE_PATH.toString())));
    }

    /**
     * Creates storage using the given file path.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads every valid saved task. Invalid lines are skipped so one damaged record
     * does not prevent the chatbot from starting.
     */
    public LoadResult load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return new LoadResult(tasks, 0);
        }
        if (!Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
            throw new IOException("The data path is not a readable file: " + filePath);
        }

        int skippedRecords = 0;
        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            try {
                Task task = parseTask(line);
                if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                    throw new IllegalArgumentException("Duplicate saved task");
                }
                tasks.add(task);
            } catch (IllegalArgumentException exception) {
                skippedRecords++;
            }
        }
        return new LoadResult(tasks, skippedRecords);
    }

    /**
     * Writes all tasks atomically when the file system supports atomic moves.
     */
    public void save(List<Task> tasks) throws IOException {
        Path absolutePath = filePath.toAbsolutePath();
        Path parent = absolutePath.getParent();
        assert parent != null : "An absolute storage file path must have a parent directory.";
        Files.createDirectories(parent);

        List<String> records = new ArrayList<>();
        for (Task task : tasks) {
            records.add(formatTask(task));
        }
        Path temporaryFile = Files.createTempFile(parent, "pebby-", ".tmp");
        try {
            Files.write(temporaryFile, records, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, absolutePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, absolutePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Recreates one task from its serialized record, rejecting malformed data. */
    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || fields[0].length() != 1
                || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid saved task");
        }

        Task task;
        switch (fields[0]) {
            case "T":
                requireFieldCount(fields, 3);
                task = new Todo(decode(fields[2]));
                break;
            case "D":
                requireFieldCount(fields, 4);
                task = new Deadline(decode(fields[2]), decode(fields[3]));
                break;
            case "E":
                requireFieldCount(fields, 5);
                task = new Event(decode(fields[2]), decode(fields[3]), decode(fields[4]));
                break;
            default:
                throw new IllegalArgumentException("Unknown task type");
        }
        task.setDone(fields[1].equals("1"));
        return task;
    }

    /** Converts a task into the record format used in Pebby's data file. */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, "T", status, encode(task.getDescription()));
        }
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return String.join(FIELD_SEPARATOR, "D", status, encode(task.getDescription()),
                    encode(deadline.getBy()));
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return String.join(FIELD_SEPARATOR, "E", status, encode(task.getDescription()),
                    encode(event.getFrom()), encode(event.getTo()));
        }
        throw new IllegalArgumentException("Cannot save an unknown task type");
    }

    /** Ensures a serialized record has the expected number of fields. */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Wrong number of saved fields");
        }
    }

    /** Encodes a text field so separators and newlines cannot corrupt a record. */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes a text field from the Base64 representation stored in a record. */
    private String decode(String value) {
        try {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid encoded value", exception);
        }
    }

    /**
     * Holds the tasks recovered at startup and the number of damaged records ignored.
     */
    public static class LoadResult {
        /** The valid tasks recovered from the data file. */
        public final List<Task> tasks;
        /** The number of invalid records skipped while loading. */
        public final int skippedRecords;

        /** Creates a result containing recovered tasks and the damaged-record count. */
        LoadResult(List<Task> tasks, int skippedRecords) {
            this.tasks = tasks;
            this.skippedRecords = skippedRecords;
        }
    }
}
