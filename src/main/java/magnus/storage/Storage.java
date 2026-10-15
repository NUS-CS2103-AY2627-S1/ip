package magnus.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import magnus.exception.StorageException;
import magnus.task.Task;

/**
 * Loads and saves Magnus tasks in a UTF-8 data file.
 */
public class Storage {
    private final Path filePath;
    private final TaskDataParser taskDataParser;

    /**
     * Creates a storage service that uses the specified data file.
     *
     * @param filePath The path of the task data file.
     * @throws NullPointerException If {@code filePath} is {@code null}.
     */
    public Storage(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath);
        this.taskDataParser = new TaskDataParser();
    }

    /**
     * Loads every non-blank task record from the data file.
     *
     * @return The loaded tasks, or an empty list if the data file does not exist.
     * @throws StorageException If the file cannot be read or contains a malformed task record.
     */
    public List<Task> loadTasks() throws StorageException {
        List<Task> tasks = new ArrayList<>();

        if (Files.notExists(this.filePath)) {
            return tasks;
        }

        try (BufferedReader reader = Files.newBufferedReader(this.filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseTaskRecord(line, lineNumber);
                if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                    throw new StorageException(
                            "\tThe score sheet contains an invalid position - duplicate task at line "
                                    + lineNumber + ".",
                            new IllegalArgumentException("duplicate task"));
                }
                tasks.add(task);
            }
            return tasks;
        } catch (IOException exception) {
            throw new StorageException(
                    "\tScore-sheet problem - I could not read the data file: " + this.filePath,
                    exception);
        }
    }

    /**
     * Saves all tasks by atomically replacing the existing data file when supported.
     *
     * @param tasks The tasks to save.
     * @throws StorageException If the tasks cannot be serialized or the data file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws StorageException {
        String fileContents = serializeTasks(tasks);
        try {
            writeTaskData(fileContents);
        } catch (IOException exception) {
            throw new StorageException(
                    "\tScore-sheet problem - I could not save your tasks to: " + this.filePath,
                    exception);
        }
    }

    /**
     * Parses one task record and adds its line number to malformed-data errors.
     *
     * @param line The serialized task record.
     * @param lineNumber The one-based line number of the record.
     * @return The parsed task.
     * @throws StorageException If the record is malformed.
     */
    private Task parseTaskRecord(String line, int lineNumber) throws StorageException {
        try {
            return this.taskDataParser.parseTask(line);
        } catch (IllegalArgumentException exception) {
            throw new StorageException(
                    "\tThe score sheet contains an invalid position - the data file is corrupted at line "
                            + lineNumber
                            + ": " + exception.getMessage(),
                    exception);
        }
    }

    /**
     * Serializes all tasks into the complete contents of the data file.
     *
     * @param tasks The tasks to serialize.
     * @return The serialized file contents.
     * @throws StorageException If a task cannot be serialized.
     */
    private String serializeTasks(List<Task> tasks) throws StorageException {
        try {
            return tasks.stream()
                    .map(this::serializeTask)
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (RuntimeException exception) {
            throw new StorageException(
                    "\tScore-sheet problem - I could not prepare the tasks for saving.", exception);
        }
    }

    /**
     * Writes complete task data through a temporary file before replacing the data file.
     *
     * @param fileContents The complete serialized task data.
     * @throws IOException If the data file cannot be replaced.
     */
    private void writeTaskData(String fileContents) throws IOException {
        Path parentDirectory = this.filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
        Path temporaryFile = Files.createTempFile(temporaryDirectory, "magnus-", ".tmp");
        try {
            Files.writeString(temporaryFile, fileContents, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Deletes a temporary file without replacing the result of the original save operation.
     *
     * @param temporaryFile The temporary file to delete.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            // The original save result is more important than temporary-file cleanup.
        }
    }

    /**
     * Serializes and validates one task before it is written.
     *
     * @param task The task to serialize.
     * @return The validated serialized task record.
     * @throws NullPointerException If {@code task} is {@code null}.
     * @throws IllegalArgumentException If the serialized record cannot be parsed.
     */
    private String serializeTask(Task task) {
        String taskData = Objects.requireNonNull(task).serialize();
        assert taskData != null && !taskData.isBlank()
                : "A task must serialize to a non-blank storage record";
        this.taskDataParser.parseTask(taskData);
        return taskData;
    }

    /**
     * Replaces the data file with a temporary file, using an atomic move when available.
     *
     * @param temporaryFile The temporary file containing the complete task data.
     * @throws IOException If the temporary file cannot replace the data file.
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, this.filePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, this.filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
