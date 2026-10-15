package tangent.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;
import tangent.task.Deadline;
import tangent.task.Event;
import tangent.task.Task;
import tangent.task.ToDo;

/**
 * Loads tasks from and saves tasks to the data file specified by a file path.
 */
public class Storage {
    /**
     * The specified format of the date to be stored in {@code dataFile}, ensuring that the parsed values are in range.
     */
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    /** The separator to be used in the data file to separate the details of a task. */
    private static final String FIELD_SEPARATOR = " | ";
    /** Record type for ToDo tasks. */
    private static final String TODO_TYPE = "T";
    /** Record type for Deadline tasks. */
    private static final String DEADLINE_TYPE = "D";
    /** Record type for Event tasks. */
    private static final String EVENT_TYPE = "E";
    /** Stored status for incomplete tasks. */
    private static final String INCOMPLETE_STATUS = "0";
    /** Stored status for completed tasks. */
    private static final String COMPLETE_STATUS = "1";
    /** The path to the specified dataFile. */
    private final Path dataFile;

    /**
     * Creates a storage object backed by the data file at the given path.
     *
     * @param filePath The path of the file used to store tasks.
     */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath).toAbsolutePath().normalize();
    }

    /**
     * Creates the data file if required, then returns every task stored in it.
     *
     * @throws TangentException if the data file cannot be created, read or parsed.
     */
    public ArrayList<Task> load() throws TangentException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Files.createDirectories(dataFile.getParent());
            if (Files.isDirectory(dataFile)) {
                throw new TangentException(String.format(ErrorMessages.DATA_PATH_DIRECTORY_MESSAGE, dataFile));
            }
            if (Files.notExists(dataFile)) {
                Files.createFile(dataFile);
            }
            try (BufferedReader reader = Files.newBufferedReader(dataFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    tasks.add(toTask(line));
                }
            }
        } catch (TangentException e) {
            throw e;
        } catch (IOException | SecurityException e) {
            throw storageException("read", e);
        }
        return tasks;
    }

    /**
     * Saves every task as a record in the data file by overwriting existing file contents.
     *
     * @param tasks The list of tasks to save in the data file.
     * @throws TangentException if the data file cannot be written to.
     */
    public void save(List<Task> tasks) throws TangentException {
        if (tasks == null) {
            throw new TangentException(ErrorMessages.MISSING_TASK_LIST_MESSAGE);
        }
        List<String> records = new ArrayList<>();
        for (Task task : tasks) {
            records.add(toRecord(task));
        }
        Path temporaryFile = null;
        try {
            Files.createDirectories(dataFile.getParent());
            if (Files.isDirectory(dataFile)) {
                throw new TangentException(String.format(ErrorMessages.DATA_PATH_DIRECTORY_MESSAGE, dataFile));
            }
            temporaryFile = Files.createTempFile(dataFile.getParent(), dataFile.getFileName().toString(), ".tmp");
            Files.write(temporaryFile, records, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (TangentException e) {
            throw e;
        } catch (IOException | SecurityException e) {
            throw storageException("save", e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // Preserve the original save result; leftover temporary files are harmless.
                }
            }
        }
    }

    /**
     * Converts one saved record into its corresponding task.
     *
     * @throws TangentException if the data file contains an invalid task record.
     */
    private Task toTask(String line) throws TangentException {
        String[] data = line.split(" \\| ", -1);
        boolean hasValidStatus = data.length >= 2
                && (data[1].equals(INCOMPLETE_STATUS) || data[1].equals(COMPLETE_STATUS));
        if (data.length < 3 || !hasValidStatus) {
            throw new TangentException(String.format(ErrorMessages.INVALID_TASK_RECORD_MESSAGE, line));
        }
        if (data[2].trim().isEmpty()) {
            throw new TangentException(String.format(ErrorMessages.INVALID_TASK_RECORD_MESSAGE, line));
        }
        Task task;
        switch (data[0]) {
            case TODO_TYPE:
                requireFieldCount(data, 3, line);
                task = new ToDo(data[2]);
                break;
            case DEADLINE_TYPE:
                requireFieldCount(data, 4, line);
                task = new Deadline(data[2], parseFileDateTime(data[3], line));
                break;
            case EVENT_TYPE:
                requireFieldCount(data, 5, line);
                LocalDateTime from = parseFileDateTime(data[3], line);
                LocalDateTime to = parseFileDateTime(data[4], line);
                if (!to.isAfter(from)) {
                    throw new TangentException(String.format(ErrorMessages.INVALID_STORED_EVENT_RANGE_MESSAGE, line));
                }
                task = new Event(data[2], from, to);
                break;
            default:
                throw new TangentException(String.format(ErrorMessages.UNKNOWN_STORED_TASK_TYPE_MESSAGE, data[0]));
        }
        if (data[1].equals(COMPLETE_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts a task into one saved record of the correct format in the data file.
     */
    private String toRecord(Task task) throws TangentException {
        if (task == null || task.getDescription() == null || task.getDescription().trim().isEmpty()
                || task.getDescription().contains(FIELD_SEPARATOR)) {
            throw new TangentException(ErrorMessages.INVALID_TASK_TO_SAVE_MESSAGE);
        }
        String status = task.isDone() ? COMPLETE_STATUS : INCOMPLETE_STATUS;
        if (task instanceof ToDo) {
            return TODO_TYPE + FIELD_SEPARATOR + status + FIELD_SEPARATOR + task.getDescription();
        }
        if (task instanceof Deadline deadline && deadline.getBy() != null) {
            return DEADLINE_TYPE + FIELD_SEPARATOR + status + FIELD_SEPARATOR + task.getDescription()
                    + FIELD_SEPARATOR + deadline.getBy().format(FILE_DATE_FORMATTER);
        }
        if (task instanceof Event event && event.getFrom() != null && event.getTo() != null
                && event.getTo().isAfter(event.getFrom())) {
            return EVENT_TYPE + FIELD_SEPARATOR + status + FIELD_SEPARATOR + task.getDescription()
                    + FIELD_SEPARATOR + event.getFrom().format(FILE_DATE_FORMATTER)
                    + FIELD_SEPARATOR + event.getTo().format(FILE_DATE_FORMATTER);
        }
        throw new TangentException(ErrorMessages.INVALID_TASK_TO_SAVE_MESSAGE);
    }

    /**
     * Checks that a saved record contains exactly its expected number of fields.
     * {@code ToDo} objects require 3 fields, {@code Deadline} objects require 4 fields
     * and {@code Event} objects require 5 fields.
     *
     * @throws TangentException if the record does not exactly match the number of fields required.
     */
    private void requireFieldCount(String[] data, int expectedCount, String line) throws TangentException {
        if (data.length != expectedCount) {
            throw new TangentException(String.format(ErrorMessages.INVALID_TASK_RECORD_MESSAGE, line));
        }
    }

    /**
     * Parses a date stored in the data file.
     *
     * @throws TangentException if the date stored in the data file does not match {@code FILE_DATE_FORMATTER}.
     */
    private LocalDateTime parseFileDateTime(String input, String line) throws TangentException {
        try {
            return LocalDateTime.parse(input.trim(), FILE_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new TangentException(String.format(ErrorMessages.INVALID_STORED_DATE_MESSAGE, line));
        }
    }

    /** Creates a user-facing storage error while retaining operation context. */
    private TangentException storageException(String operation, Exception exception) {
        String detail = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        return new TangentException(String.format(ErrorMessages.STORAGE_OPERATION_FAILURE_MESSAGE,
                operation, dataFile, detail));
    }
}

