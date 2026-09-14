package kaya.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import kaya.task.Deadline;
import kaya.task.Event;
import kaya.task.Task;
import kaya.task.Todo;

/**
 * Saves and loads Kaya's tasks using a text file on disk.
 *
 * <p>Text fields are Base64 encoded so descriptions containing separators or
 * line breaks can be restored without ambiguity.</p>
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final Path filePath;

    /** Prevents incomplete startup data from replacing the original file. */
    private boolean isWriteBlocked;
    private int skippedRecordCount;

    /**
     * Creates storage that reads from and writes to the given path.
     *
     * @param filePath the data file path
     */
    public Storage(Path filePath) {
        this.filePath = filePath.toAbsolutePath();
    }

    /**
     * Writes a complete temporary file before atomically replacing the saved data.
     *
     * @param tasks the tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        if (isWriteBlocked) {
            throw new IOException("The saved data could not be fully loaded. "
                    + "Fix or restore the data file and restart Kaya.");
        }
        requireRegularFile();
        if (Files.exists(filePath) && !Files.isWritable(filePath)) {
            throw new AccessDeniedException(filePath.toString(), null, "The data file is read-only.");
        }
        Path parent = filePath.getParent();
        Files.createDirectories(parent);

        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(serialize(task));
        }
        Path temporaryFile = Files.createTempFile(parent, "kaya-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    /**
     * Loads valid tasks from disk. A missing file represents an empty task list,
     * while malformed lines are skipped and counted. Changes are blocked after
     * an incomplete load so the original file can be recovered.
     *
     * @return the tasks found in the data file
     * @throws IOException if an existing data file cannot be read
     */
    public List<Task> loadTasks() throws IOException {
        isWriteBlocked = true;
        skippedRecordCount = 0;
        requireRegularFile();
        List<Task> tasks = new ArrayList<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (NoSuchFileException exception) {
            isWriteBlocked = false;
            return tasks;
        }

        for (String line : lines) {
            try {
                tasks.add(deserialize(line));
            } catch (IllegalArgumentException | DateTimeException | CharacterCodingException exception) {
                skippedRecordCount++;
            }
        }
        isWriteBlocked = skippedRecordCount > 0;
        return tasks;
    }

    /**
     * Returns how many malformed records were skipped during the latest load.
     *
     * @return the number of skipped records
     */
    public int getSkippedRecordCount() {
        return skippedRecordCount;
    }

    /**
     * Rejects directories and links that cannot safely be replaced as a data file.
     *
     * @throws IOException if the data path exists but is not a regular file
     */
    private void requireRegularFile() throws IOException {
        if (Files.isSymbolicLink(filePath) || (Files.exists(filePath) && !Files.isRegularFile(filePath))) {
            throw new IOException("The data path must be a regular file: " + filePath);
        }
    }

    /**
     * Converts a task into one line of the storage format.
     */
    private String serialize(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, "D", status,
                    encode(task.getDescription()), encode(deadline.getBy().toString()));
        }
        if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", status,
                    encode(task.getDescription()), encode(event.getFrom().toString()),
                    encode(event.getTo().toString()));
        }
        assert task instanceof Todo : "Only Todo tasks may use the remaining storage format";
        return String.join(FIELD_SEPARATOR, "T", status, encode(task.getDescription()));
    }

    /**
     * Converts one valid storage line back into a task.
     */
    private Task deserialize(String line) throws CharacterCodingException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task record");
        }
        String description = decode(fields[2]);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Empty task description");
        }

        Task task = switch (fields[0]) {
            case "T" -> {
                requireLength(fields, 3);
                yield new Todo(description);
            }
            case "D" -> {
                requireLength(fields, 4);
                yield new Deadline(description, LocalDate.parse(decode(fields[3])));
            }
            case "E" -> {
                requireLength(fields, 5);
                yield new Event(description, LocalDate.parse(decode(fields[3])),
                        LocalDate.parse(decode(fields[4])));
            }
            default -> throw new IllegalArgumentException("Unknown task type");
        };

        if (task instanceof Event event && event.getTo().isBefore(event.getFrom())) {
            throw new IllegalArgumentException("Event ends before it starts");
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Checks a record's field count before returning its parsed task.
     */
    private void requireLength(String[] fields, int expectedLength) {
        if (fields.length != expectedLength) {
            throw new IllegalArgumentException("Incorrect number of fields");
        }
    }

    /**
     * Encodes text as a single safe field in the data file.
     */
    private String encode(String value) {
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a text field from the data file.
     */
    private String decode(String value) throws CharacterCodingException {
        return StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(DECODER.decode(value))).toString();
    }
}
