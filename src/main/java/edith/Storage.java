package edith;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import edith.task.DateTimeParser;
import edith.task.Deadline;
import edith.task.Event;
import edith.task.Task;
import edith.task.TaskList;
import edith.task.ToDo;

/** Loads and saves Edith's task list. */
public class Storage {
    private final Path filePath;

    /** Creates storage backed by the given file path. */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "Storage file path must not be blank";
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from disk after validating every saved record.
     *
     * @return task list stored on disk, or an empty list when no save file exists
     * @throws EdithException if the save file cannot be read or contains invalid data
     */
    public TaskList load() throws EdithException {
        if (Files.notExists(filePath)) {
            return new TaskList();
        }
        if (!Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
            throw storageError("the save file cannot be read");
        }

        TaskList tasks = new TaskList();
        Set<Long> insertionOrders = new HashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String savedTask;
            int lineNumber = 0;
            while ((savedTask = reader.readLine()) != null) {
                lineNumber++;
                TaskRecord record = parseRecord(savedTask, lineNumber);
                if (!insertionOrders.add(record.insertionOrder)) {
                    throw storageError("the save file reuses an insertion order on line " + lineNumber);
                }
                if (tasks.hasDuplicate(record.task)) {
                    throw storageError("the save file contains a duplicate task on line " + lineNumber);
                }
                tasks.add(record.task, record.insertionOrder);
            }
        } catch (IOException error) {
            throw storageError("the save file could not be read");
        }
        return tasks;
    }

    /**
     * Saves every task by replacing the previous file only after a complete temporary save succeeds.
     *
     * @param tasks tasks to persist
     * @throws EdithException if the task list cannot be safely saved
     */
    public void save(TaskList tasks) throws EdithException {
        Path absolutePath = filePath.toAbsolutePath();
        Path directory = absolutePath.getParent();
        Path temporaryFile = null;
        try {
            Files.createDirectories(directory);
            if (!Files.isDirectory(directory) || !Files.isWritable(directory)) {
                throw storageError("the save directory cannot be written");
            }
            if (Files.exists(absolutePath) && !Files.isRegularFile(absolutePath)) {
                throw storageError("the save path is not a file");
            }

            temporaryFile = Files.createTempFile(directory, absolutePath.getFileName().toString(), ".tmp");
            try (var writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat() + " | " + task.getInsertionOrder());
                    writer.newLine();
                }
            }
            replaceSaveFile(temporaryFile, absolutePath);
        } catch (IOException error) {
            throw storageError("the task list could not be saved");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Reads and validates one persisted task. */
    private TaskRecord parseRecord(String savedTask, int lineNumber) throws EdithException {
        String[] parts = savedTask.split("\\s*\\|\\s*", -1);
        if (parts.length < 4 || parts[0].isEmpty() || parts[1].isEmpty() || parts[2].isBlank()) {
            throw storageError("the save file has invalid data on line " + lineNumber);
        }
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw storageError("the save file has an invalid task status on line " + lineNumber);
        }

        long insertionOrder = parseInsertionOrder(parts, lineNumber);
        Task task = switch (parts[0]) {
            case "T" -> createToDo(parts, lineNumber);
            case "D" -> createDeadline(parts, lineNumber);
            case "E" -> createEvent(parts, lineNumber);
            default -> throw storageError("the save file has an unknown task type on line " + lineNumber);
        };
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return new TaskRecord(task, insertionOrder);
    }

    /** Parses an insertion order from a task record. */
    private long parseInsertionOrder(String[] parts, int lineNumber) throws EdithException {
        try {
            long insertionOrder = Long.parseLong(parts[parts.length - 1]);
            if (insertionOrder < 0) {
                throw storageError("the save file has an invalid insertion order on line " + lineNumber);
            }
            return insertionOrder;
        } catch (NumberFormatException error) {
            throw storageError("the save file has an invalid insertion order on line " + lineNumber);
        }
    }

    /** Creates a validated to-do record. */
    private Task createToDo(String[] parts, int lineNumber) throws EdithException {
        if (parts.length != 4) {
            throw storageError("the save file has invalid todo data on line " + lineNumber);
        }
        return new ToDo(parts[2]);
    }

    /** Creates a validated deadline record. */
    private Task createDeadline(String[] parts, int lineNumber) throws EdithException {
        if (parts.length != 5 || parseDateTime(parts[3]) == null) {
            throw storageError("the save file has an invalid deadline on line " + lineNumber);
        }
        return new Deadline(parts[2], parts[3]);
    }

    /** Creates a validated event record. */
    private Task createEvent(String[] parts, int lineNumber) throws EdithException {
        if (parts.length != 6) {
            throw storageError("the save file has invalid event data on line " + lineNumber);
        }
        LocalDateTime start = parseDateTime(parts[3]);
        LocalDateTime end = parseEventEnd(parts[4], start);
        if (start == null || end == null || !end.isAfter(start)) {
            throw storageError("the save file has an invalid event time range on line " + lineNumber);
        }
        return new Event(parts[2], parts[3], parts[4]);
    }

    /** Parses a date or date-time in a saved record. */
    private LocalDateTime parseDateTime(String text) {
        return DateTimeParser.parseForSorting(text);
    }

    /** Parses an event end time, permitting a time on the start date. */
    private LocalDateTime parseEventEnd(String text, LocalDateTime start) {
        LocalDateTime dateTime = parseDateTime(text);
        if (dateTime != null || start == null) {
            return dateTime;
        }
        LocalTime time = DateTimeParser.parseTimeOnly(text);
        return time == null ? null : start.toLocalDate().atTime(time);
    }

    /** Moves a fully written temporary file into place. */
    private void replaceSaveFile(Path temporaryFile, Path targetFile) throws IOException {
        try {
            Files.move(temporaryFile, targetFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException error) {
            Files.move(temporaryFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Removes a leftover temporary save file after an unsuccessful save. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            // The next save attempt does not depend on this cleanup succeeding.
        }
    }

    /** Returns a consistent user-facing message for storage failures. */
    private EdithException storageError(String reason) {
        return new EdithException("OOPS!!! Edith could not use its saved tasks because " + reason + ".");
    }

    /** Holds a validated task and the insertion order stored beside it. */
    private record TaskRecord(Task task, long insertionOrder) {
    }
}
