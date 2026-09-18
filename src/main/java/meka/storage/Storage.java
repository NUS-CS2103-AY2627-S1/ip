package meka.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import meka.exception.DataFileException;
import meka.task.Deadline;
import meka.task.Event;
import meka.task.Task;
import meka.task.TaskList;
import meka.task.Todo;

/**
 * Loads tasks from and saves tasks to the application's data file.
 */
public class Storage {
    /** Location of the task data file. */
    private final Path filePath;

    /** Location of the file that retains archived task records. */
    private final Path archiveFilePath;

    /** Whether the data file remains safe and available for saving. */
    private boolean isAvailable;

    /**
     * Creates a storage component that uses the given data file.
     *
     * @param filePath location of the task data file.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage should be configured with a data file path";

        this.filePath = filePath;
        this.archiveFilePath = deriveArchiveFilePath(filePath);
        this.isAvailable = true;
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @return the saved tasks, or an empty list if the data file does not exist.
     * @throws IOException if the data file cannot be read.
     * @throws DataFileException if the data file contains an invalid record.
     */
    public TaskList load() throws IOException, DataFileException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return new TaskList(tasks);
        }

        int lineNumber = 0;
        for (String originalLine : Files.readAllLines(filePath)) {
            lineNumber++;
            String line = originalLine;
            if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }
            if (!line.isBlank()) {
                tasks.add(parseTask(line, lineNumber));
            }
        }
        return new TaskList(tasks);
    }

    /**
     * Saves all current tasks to the data file.
     *
     * @param tasks task list to save.
     * @throws IOException if the data directory or file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        if (!isAvailable) {
            throw new IOException("Task storage is unavailable");
        }
        Files.createDirectories(filePath.toAbsolutePath().getParent());

        ArrayList<String> taskData = new ArrayList<>();
        for (Task task : tasks) {
            taskData.add(task.toDataString());
        }
        Files.write(filePath, taskData);
    }

    /**
     * Appends all active tasks to the archive and clears the saved active-task file.
     *
     * The supplied in-memory task list is not modified. If clearing the active-task
     * file fails, this method attempts to restore the archive to its previous state.
     *
     * @param tasks active tasks to archive.
     * @throws IOException if the archive or active-task file cannot be updated.
     */
    public void archiveAll(TaskList tasks) throws IOException {
        if (!isAvailable) {
            throw new IOException("Task storage is unavailable");
        }

        boolean didArchiveExist = Files.exists(archiveFilePath);
        byte[] originalArchiveData = didArchiveExist
                ? Files.readAllBytes(archiveFilePath)
                : new byte[0];
        ArrayList<String> archivedTaskData = didArchiveExist
                ? new ArrayList<>(Files.readAllLines(archiveFilePath))
                : new ArrayList<>();
        for (Task task : tasks) {
            archivedTaskData.add(task.toDataString());
        }

        Files.createDirectories(archiveFilePath.toAbsolutePath().getParent());
        Files.write(archiveFilePath, archivedTaskData);
        try {
            save(new TaskList());
        } catch (IOException exception) {
            rollbackArchive(didArchiveExist, originalArchiveData, exception);
            throw exception;
        } catch (SecurityException exception) {
            rollbackArchive(didArchiveExist, originalArchiveData, exception);
            throw exception;
        }
    }

    /**
     * Prevents later save attempts after loading or saving has failed.
     */
    public void markUnavailable() {
        isAvailable = false;
    }

    /**
     * Derives an archive path by inserting {@code -archive} before the data-file extension.
     *
     * @param activeFilePath active task data path.
     * @return neighboring archive data path.
     */
    private Path deriveArchiveFilePath(Path activeFilePath) {
        String fileName = activeFilePath.getFileName().toString();
        int extensionIndex = fileName.lastIndexOf('.');
        String archiveFileName = extensionIndex > 0
                ? fileName.substring(0, extensionIndex) + "-archive" + fileName.substring(extensionIndex)
                : fileName + "-archive";
        return activeFilePath.resolveSibling(archiveFileName);
    }

    /**
     * Attempts to restore the archive after the active-task file could not be cleared.
     *
     * @param didArchiveExist whether the archive existed before the attempted operation.
     * @param originalArchiveData exact original archive contents.
     * @param originalException failure that caused the rollback.
     */
    private void rollbackArchive(boolean didArchiveExist, byte[] originalArchiveData,
            Exception originalException) {
        try {
            if (didArchiveExist) {
                Files.write(archiveFilePath, originalArchiveData);
            } else {
                Files.deleteIfExists(archiveFilePath);
            }
        } catch (IOException | SecurityException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }

    /**
     * Converts one saved data line back into its corresponding task object.
     *
     * @param line pipe-separated task data.
     * @param lineNumber line number used to identify invalid data.
     * @return the reconstructed task.
     * @throws DataFileException if the line contains invalid task data.
     */
    private Task parseTask(String line, int lineNumber) throws DataFileException {
        String[] fields = line.split(" \\| ", -1);
        validateFields(fields, lineNumber);

        Task task = createTask(fields, lineNumber);
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Validates the structure and common fields of one saved task record.
     *
     * @param fields fields obtained from the saved record.
     * @param lineNumber one-based line number of the record.
     * @throws DataFileException if the record has an invalid structure or common field.
     */
    private void validateFields(String[] fields, int lineNumber) throws DataFileException {
        int expectedFieldCount = getExpectedFieldCount(fields[0], lineNumber);
        if (fields.length != expectedFieldCount) {
            throw invalidData(lineNumber, "incorrect number of fields");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw invalidData(lineNumber, "invalid completion status");
        }
        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw invalidData(lineNumber, "empty task detail");
            }
        }
    }

    /**
     * Returns the number of fields required by a saved task type.
     *
     * @param taskType letter identifying the saved task type.
     * @param lineNumber one-based line number of the record.
     * @return required number of fields.
     * @throws DataFileException if the task type is unknown.
     */
    private int getExpectedFieldCount(String taskType, int lineNumber) throws DataFileException {
        int expectedFieldCount;
        switch (taskType) {
            case "T":
                expectedFieldCount = 3;
                break;
            case "D":
                expectedFieldCount = 4;
                break;
            case "E":
                expectedFieldCount = 5;
                break;
            default:
                throw invalidData(lineNumber, "unknown task type");
        }
        return expectedFieldCount;
    }

    /**
     * Creates a task from fields whose structure has already been validated.
     *
     * @param fields validated fields from the saved record.
     * @param lineNumber one-based line number of the record.
     * @return reconstructed task.
     * @throws DataFileException if a date-time field is invalid.
     */
    private Task createTask(String[] fields, int lineNumber) throws DataFileException {
        Task task;
        try {
            switch (fields[0]) {
                case "T":
                    task = new Todo(fields[2]);
                    break;
                case "D":
                    task = new Deadline(fields[2], LocalDateTime.parse(fields[3]));
                    break;
                case "E":
                    LocalDateTime start = LocalDateTime.parse(fields[3]);
                    LocalDateTime end = LocalDateTime.parse(fields[4]);
                    if (!end.isAfter(start)) {
                        throw invalidData(lineNumber, "event end must be after its start");
                    }
                    task = new Event(fields[2], start, end);
                    break;
                default:
                    throw invalidData(lineNumber, "unknown task type");
            }
        } catch (DateTimeParseException exception) {
            throw invalidData(lineNumber, "invalid date and time");
        }
        return task;
    }

    /**
     * Creates a data-file exception that identifies the invalid line.
     *
     * @param lineNumber one-based line number.
     * @param reason reason the record is invalid.
     * @return an exception describing the invalid data.
     */
    private DataFileException invalidData(int lineNumber, String reason) {
        return new DataFileException("Invalid data on line " + lineNumber + ": " + reason);
    }
}
