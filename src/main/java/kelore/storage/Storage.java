package kelore.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import kelore.exception.CorruptedDataException;
import kelore.exception.StorageException;
import kelore.task.Deadline;
import kelore.task.Event;
import kelore.task.Task;
import kelore.task.TaskList;
import kelore.task.Todo;

/** Loads and saves Kelore tasks in a human-readable text file. */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private final Path filePath;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Path of the data file.
     */
    public Storage(Path filePath) {
        assert filePath != null : "The data file path must not be null";
        this.filePath = filePath;
    }

    /**
     * Returns tasks loaded from the data file, or an empty list if the file is absent.
     *
     * @return Tasks represented by the data file.
     * @throws StorageException If the file cannot be read or contains corrupted data.
     */
    public TaskList load() throws StorageException {
        if (Files.notExists(filePath)) {
            return new TaskList();
        }
        ArrayList<Task> tasks = new ArrayList<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new StorageException("The data file could not be read.", e);
        }
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                Task task = parseTask(lines.get(i), i + 1);
                if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                    throw corruptedFileError(i + 1);
                }
                tasks.add(task);
            }
        }
        return new TaskList(tasks);
    }

    /**
     * Saves all tasks to the data file, creating its parent directory when needed.
     *
     * @param taskList Tasks to save.
     * @throws StorageException If the data file cannot be written.
     */
    public void save(TaskList taskList) throws StorageException {
        assert taskList != null : "The task list to save must not be null";
        Path absoluteFilePath = filePath.toAbsolutePath();
        Path parentDirectory = absoluteFilePath.getParent();
        Path temporaryFile = null;
        try {
            Files.createDirectories(parentDirectory);
            temporaryFile = Files.createTempFile(parentDirectory, ".kelore-", ".tmp");
            Files.write(temporaryFile, taskList.toStorageLines());
            moveIntoPlace(temporaryFile, absoluteFilePath);
            temporaryFile = null;
        } catch (IOException e) {
            throw new StorageException("The data file could not be saved.", e);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    private void moveIntoPlace(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException e) {
            // The original save error is more useful than a temporary-file cleanup error.
        }
    }

    /**
     * Returns the task represented by one data-file record.
     *
     * @param line Record to parse.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed task.
     * @throws CorruptedDataException If the record is malformed.
     */
    private Task parseTask(String line, int lineNumber) throws CorruptedDataException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw corruptedFileError(lineNumber);
        }
        if (fields[2].isBlank()
                || fields[2].chars().anyMatch(Character::isISOControl)) {
            throw corruptedFileError(lineNumber);
        }
        Task task;
        switch (fields[0]) {
            case "T":
                requireFieldCount(fields, 3, lineNumber);
                task = new Todo(fields[2]);
                break;
            case "D":
                requireFieldCount(fields, 4, lineNumber);
                task = new Deadline(fields[2], parseDateTime(fields[3], lineNumber));
                break;
            case "E":
                requireFieldCount(fields, 5, lineNumber);
                LocalDateTime from = parseDateTime(fields[3], lineNumber);
                LocalDateTime to = parseDateTime(fields[4], lineNumber);
                if (!to.isAfter(from)) {
                    throw corruptedFileError(lineNumber);
                }
                task = new Event(fields[2], from, to);
                break;
            default:
                throw corruptedFileError(lineNumber);
        }
        assert task != null : "A recognized task type must produce a task";
        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw corruptedFileError(lineNumber);
        }
        return task;
    }

    /**
     * Returns a date and time parsed from its stored ISO-8601 representation.
     *
     * @param value Stored date and time to parse.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed date and time.
     * @throws CorruptedDataException If the value is not a valid date and time.
     */
    private LocalDateTime parseDateTime(String value, int lineNumber)
            throws CorruptedDataException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw corruptedFileError(lineNumber);
        }
    }

    /**
     * Ensures that a stored task record contains the expected number of fields.
     *
     * @param fields Fields in the stored record.
     * @param expected Required number of fields.
     * @param lineNumber One-based line number used in error messages.
     * @throws CorruptedDataException If the field count differs from the expected count.
     */
    private void requireFieldCount(String[] fields, int expected, int lineNumber)
            throws CorruptedDataException {
        if (fields.length != expected) {
            throw corruptedFileError(lineNumber);
        }
    }

    /**
     * Returns an exception identifying a corrupted data-file record.
     *
     * @param lineNumber One-based number of the corrupted line.
     * @return Exception describing the corrupted line.
     */
    private CorruptedDataException corruptedFileError(int lineNumber) {
        return new CorruptedDataException(lineNumber);
    }

    /**
     * Returns fields joined using the delimiter understood by the storage parser.
     *
     * @param fields Fields to join.
     * @return Delimited storage record.
     */
    public static String joinFields(String... fields) {
        assert fields != null : "Storage fields must not be null";
        for (String field : fields) {
            assert field != null : "A storage field must not be null";
        }
        return String.join(FIELD_SEPARATOR, fields);
    }
}
