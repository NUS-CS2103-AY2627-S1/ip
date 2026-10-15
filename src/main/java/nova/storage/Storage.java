package nova.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

/**
 * Saves Nova's task list to a file on the hard disk.
 */
public class Storage {
    private final Path filePath;
    private int skippedRecordCount;

    /**
     * Creates storage that writes tasks to the given path.
     *
     * @param filePath relative or absolute path of the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file.
     * Returns an empty list when the data file does not exist and skips malformed records.
     *
     * @return tasks stored in the data file.
     * @throws IOException if an existing data file cannot be read.
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        skippedRecordCount = 0;
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                } else {
                    skippedRecordCount++;
                }
            }
        } catch (NoSuchFileException exception) {
            return tasks;
        }
        return tasks;
    }

    /**
     * Returns the number of malformed records skipped by the most recent load.
     *
     * @return number of skipped records
     */
    public int getSkippedRecordCount() {
        return skippedRecordCount;
    }

    /**
     * Replaces the data file contents with the current task list.
     * Creates the parent directory and file when they do not exist.
     *
     * @param tasks current task list.
     * @throws IOException if the tasks cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path absoluteFilePath = filePath.toAbsolutePath();
        Path parentDirectory = absoluteFilePath.getParent();
        Files.createDirectories(parentDirectory);

        List<String> taskLines = tasks.stream()
                .map(Task::toDataString)
                .toList();
        Path temporaryFile = Files.createTempFile(parentDirectory, "nova-", ".tmp");
        try {
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile, absoluteFilePath);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Returns the task represented by a data-file record, or {@code null} if it is malformed.
     *
     * @param line data-file record to parse
     * @return parsed task, or {@code null} if the record is malformed
     */
    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        try {
            if (fields.length > 0 && fields[0].equals("V2")) {
                return parseVersionTwoTask(fields);
            }
            return parseLegacyTask(fields);
        } catch (IllegalArgumentException | DateTimeException exception) {
            return null;
        }
    }

    /**
     * Returns a task from the original plain-text format, or {@code null} if invalid.
     *
     * @param fields fields from a legacy data-file record
     * @return parsed task, or {@code null} if the fields are invalid
     */
    private Task parseLegacyTask(String[] fields) {
        if (fields.length < 2 || !hasValidStatus(fields[1])) {
            return null;
        }
        return createTask(fields[0], fields[1], fields, 2);
    }

    /**
     * Returns a task from the version-two encoded format, or {@code null} if invalid.
     *
     * @param fields fields from a version-two data-file record
     * @return parsed task, or {@code null} if the fields are invalid
     */
    private Task parseVersionTwoTask(String[] fields) {
        if (fields.length < 3 || !hasValidStatus(fields[2])) {
            return null;
        }
        String[] decodedFields = fields.clone();
        for (int i = 3; i < decodedFields.length; i++) {
            decodedFields[i] = decodeDataField(decodedFields[i]);
        }
        return createTask(decodedFields[1], decodedFields[2], decodedFields, 3);
    }

    /**
     * Returns a task built from validated fields, or {@code null} if the record shape is invalid.
     *
     * @param type task type identifier
     * @param status serialized completion state
     * @param fields decoded data-file fields
     * @param textStart index of the first task-specific text field
     * @return parsed task, or {@code null} if the record shape is invalid
     */
    private Task createTask(String type, String status, String[] fields, int textStart) {
        assert hasValidStatus(status) : "Task creation requires a validated completion status";

        Task task;
        switch (type) {
            case "T":
                task = fields.length == textStart + 1 && hasText(fields[textStart])
                        ? new Todo(fields[textStart]) : null;
                break;
            case "D":
                task = fields.length == textStart + 2
                        && hasText(fields[textStart]) && hasText(fields[textStart + 1])
                        ? new Deadline(fields[textStart], LocalDate.parse(fields[textStart + 1])) : null;
                break;
            case "E":
                task = fields.length == textStart + 3
                        && hasText(fields[textStart]) && hasText(fields[textStart + 1])
                        && hasText(fields[textStart + 2])
                        ? new Event(fields[textStart], fields[textStart + 1], fields[textStart + 2]) : null;
                break;
            default:
                task = null;
                break;
        }

        if (task != null && status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns a decoded version-two text field.
     *
     * @param value Base64-encoded text field
     * @return decoded text
     */
    private String decodeDataField(String value) {
        byte[] decodedBytes = Base64.getDecoder().decode(value);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Returns whether a record has a supported completion state.
     *
     * @param status serialized completion state
     * @return {@code true} if the status is supported
     */
    private boolean hasValidStatus(String status) {
        return status.equals("0") || status.equals("1");
    }

    /**
     * Returns whether a required task text field contains non-whitespace characters.
     *
     * @param value required task text field
     * @return {@code true} if the field contains non-whitespace characters
     */
    private boolean hasText(String value) {
        return !value.isBlank();
    }

    /**
     * Atomically replaces the data file when the file system supports atomic moves.
     *
     * @param temporaryFile temporary file containing the new task data
     * @param destination data file to replace
     * @throws IOException if the data file cannot be replaced
     */
    private void replaceDataFile(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
