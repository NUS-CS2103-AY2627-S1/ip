package megatron.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import megatron.exception.StorageException;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.task.Todo;

/**
 * Stores and restores Megatron tasks in a CSV file.
 */
public final class TaskStorage {
    private final Path storageFile;

    /**
     * Creates storage that reads and writes the given relative or absolute path.
     *
     * @param fileName The relative or absolute storage path.
     */
    public TaskStorage(String fileName) {
        storageFile = Paths.get(fileName);
    }

    /**
     * Loads all valid tasks. A missing file is treated as an empty task list.
     *
     * @return A task list containing all valid stored tasks.
     * @throws StorageException If the data file cannot be read.
     */
    public TaskList load() throws StorageException {
        List<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(storageFile)) {
                if (Files.notExists(storageFile)) {
                    return new TaskList(tasks);
                }
                throw new IOException("Unable to determine whether the data file exists.");
            }

            try (BufferedReader reader = Files.newBufferedReader(storageFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Task task = loadTask(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
            }
        } catch (IOException | SecurityException exception) {
            throw StorageException.createForLoad(exception);
        }
        return new TaskList(tasks);
    }

    /**
     * Parses and creates one task from a storage row.
     *
     * @param line The CSV row to load.
     * @return The created task, or null if the row is invalid.
     */
    private static Task loadTask(String line) {
        List<String> fields = parseCsvLine(line);
        if (fields == null || fields.size() < 4 || fields.get(0).equals("type")) {
            return null;
        }
        if (!isValid(fields)) {
            return null;
        }

        Task task = createTask(fields);
        if (task == null) {
            return null;
        }
        if ("1".equals(fields.get(1))) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Saves all tasks and creates the data folder when needed.
     *
     * @param tasks The tasks to save.
     * @throws StorageException If the tasks cannot be written to the data file.
     */
    public void save(TaskList tasks) throws StorageException {
        Path absoluteFile = storageFile.toAbsolutePath();
        Path temporaryFile = null;
        try {
            Path directory = absoluteFile.getParent();
            Files.createDirectories(directory);
            temporaryFile = Files.createTempFile(directory, absoluteFile.getFileName() + ".", ".tmp");
            writeTasks(temporaryFile, tasks);
            replaceFile(temporaryFile, absoluteFile);
        } catch (IOException | RuntimeException exception) {
            deleteTemporaryFile(temporaryFile, exception);
            throw new StorageException(exception);
        }
    }

    /**
     * Writes a complete task list to the given file.
     *
     * @param target The file to write.
     * @param tasks The tasks to write.
     * @throws IOException If the task list cannot be written.
     */
    private static void writeTasks(Path target, TaskList tasks) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(target)) {
            writer.write("type,done,description,extra");
            writer.newLine();
            for (Task task : tasks) {
                writer.write(escapeCsvField(task.getTypeCode()) + "," + (task.isDone() ? "1" : "0") + ","
                        + escapeCsvField(task.getDescription()) + "," + escapeCsvField(task.getExtra()));
                writer.newLine();
            }
        }
    }

    /**
     * Replaces the storage file with a complete temporary file.
     *
     * @param temporaryFile The complete temporary file.
     * @param targetFile The storage file to replace.
     * @throws IOException If the temporary file cannot replace the storage file.
     */
    private static void replaceFile(Path temporaryFile, Path targetFile) throws IOException {
        try {
            Files.move(temporaryFile, targetFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes an incomplete temporary file and retains any cleanup error.
     *
     * @param temporaryFile The temporary file to remove, or null if none was created.
     * @param originalException The error that interrupted the save.
     */
    private static void deleteTemporaryFile(Path temporaryFile, Throwable originalException) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException cleanupException) {
            originalException.addSuppressed(cleanupException);
        }
    }

    /**
     * Creates a task from the fields in a valid storage row.
     *
     * @param fields The fields in a valid storage row.
     * @return The created task, or null for an unsupported row.
     */
    private static Task createTask(List<String> fields) {
        assert isValid(fields) : "Task creation requires a validated four-field storage row.";
        if (fields.get(0).equals("T")) {
            return new Todo(fields.get(2));
        } else if (fields.get(0).equals("D")) {
            try {
                return new Deadline(fields.get(2), LocalDateTime.parse(fields.get(3)));
            } catch (DateTimeException | IllegalArgumentException exception) {
                return null;
            }
        } else if (fields.get(0).equals("E")) {
            String[] times = fields.get(3).split("\\|", 2);
            try {
                return times.length == 2 ? new Event(fields.get(2), LocalDateTime.parse(times[0]),
                        LocalDateTime.parse(times[1])) : null;
            } catch (DateTimeException | IllegalArgumentException exception) {
                return null;
            }
        }
        return null;
    }

    /**
     * Checks a row before converting it into a task. Invalid rows are ignored.
     *
     * @param fields The fields in a storage row.
     * @return True if the fields describe a valid task.
     */
    private static boolean isValid(List<String> fields) {
        if (fields.size() != 4 || !(fields.get(1).equals("0") || fields.get(1).equals("1"))
                || fields.get(2).isBlank()) {
            return false;
        }
        if (fields.get(0).equals("T")) {
            return fields.get(3).isEmpty();
        } else if (fields.get(0).equals("D")) {
            return !fields.get(3).isBlank();
        } else if (fields.get(0).equals("E")) {
            String[] times = fields.get(3).split("\\|", 2);
            return times.length == 2 && !times[0].isBlank() && !times[1].isBlank();
        }
        return false;
    }

    /**
     * Escapes and quotes a value for storage in one CSV field.
     *
     * @param value The value to escape.
     * @return The escaped CSV field.
     */
    private static String escapeCsvField(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    /**
     * Splits one CSV row while preserving commas and escaped quotes in quoted fields.
     *
     * @param line The CSV row.
     * @return The parsed CSV fields, or null if the row has malformed quoting.
     */
    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isQuoted = false;
        boolean hasFieldStarted = false;
        boolean isQuoteClosed = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (isQuoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else if (isQuoted) {
                    isQuoted = false;
                    isQuoteClosed = true;
                } else if (!hasFieldStarted) {
                    isQuoted = true;
                    hasFieldStarted = true;
                } else {
                    return null;
                }
            } else if (character == ',' && !isQuoted) {
                fields.add(field.toString());
                field.setLength(0);
                hasFieldStarted = false;
                isQuoteClosed = false;
            } else {
                if (isQuoteClosed) {
                    return null;
                }
                field.append(character);
                hasFieldStarted = true;
            }
        }

        if (isQuoted) {
            return null;
        }

        fields.add(field.toString());
        return fields;
    }
}
