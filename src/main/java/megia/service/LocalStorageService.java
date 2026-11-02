package megia.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import megia.exception.StorageException;
import megia.model.Deadline;
import megia.model.Event;
import megia.model.Task;
import megia.model.TaskStorage;
import megia.model.TaskType;
import megia.model.Todo;

/**
 * Loads and saves tasks using a backward-compatible CSV storage format.
 */
public final class LocalStorageService {
    private final String taskStoragePath;
    private final StorageWriter storageWriter;

    /**
     * Creates a local storage service for the specified task file.
     *
     * @param path Path to the task storage file.
     */
    public LocalStorageService(String path) {
        this(path, LocalStorageService::writeAtomically);
    }

    /**
     * Creates a local storage service with a controlled writer.
     *
     * @param path Path to the task storage file.
     * @param storageWriter Writer used to replace the storage file.
     */
    LocalStorageService(String path, StorageWriter storageWriter) {
        this.taskStoragePath = path;
        this.storageWriter = storageWriter;
    }

    /**
     * Loads tasks from storage.
     *
     * @return Loaded tasks, or an empty optional when the file does not exist.
     * @throws StorageException If a present file contains malformed data.
     */
    public Optional<TaskStorage> loadTaskData() throws StorageException {
        Path storagePath = Path.of(taskStoragePath);
        if (Files.notExists(storagePath)) {
            return Optional.empty();
        }

        TaskStorage taskStorage = new TaskStorage();
        try (BufferedReader reader = Files.newBufferedReader(storagePath)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    taskStorage.addTask(getTaskFromLine(parseCsvLine(line.strip())));
                } catch (RuntimeException exception) {
                    throw new StorageException(taskStoragePath, lineNumber);
                }
            }
        } catch (IOException exception) {
            throw new StorageException(taskStoragePath, 0);
        }
        return Optional.of(taskStorage);
    }

    private static Task getTaskFromLine(String[] data) {
        TaskType taskType = TaskType.valueOf(data[0]);
        int expectedFieldCount = switch (taskType) {
            case TODO -> 3;
            case DEADLINE -> 4;
            case EVENT -> 5;
        };
        if (data.length != expectedFieldCount || data[2].isBlank() || !isBoolean(data[1])) {
            throw new IllegalArgumentException();
        }
        boolean isDone = Boolean.parseBoolean(data[1]);
        return switch (taskType) {
            case DEADLINE -> new Deadline(data[2], isDone, parseDateTime(data[3]));
            case EVENT -> new Event(data[2], isDone, parseDateTime(data[3]), parseDateTime(data[4]));
            case TODO -> new Todo(data[2], isDone);
        };
    }

    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        int index = 0;
        while (index <= line.length()) {
            StringBuilder field = new StringBuilder();
            if (index < line.length() && line.charAt(index) == '"') {
                index = parseQuotedField(line, index + 1, field);
                if (index < line.length() && line.charAt(index) != ',') {
                    throw new IllegalArgumentException();
                }
            } else {
                while (index < line.length() && line.charAt(index) != ',') {
                    field.append(line.charAt(index));
                    index++;
                }
            }
            fields.add(field.toString());
            if (index == line.length()) {
                break;
            }
            index++;
        }
        return fields.toArray(String[]::new);
    }

    private static int parseQuotedField(String line, int index, StringBuilder field) {
        while (index < line.length()) {
            char character = line.charAt(index);
            if (character != '"') {
                field.append(character);
                index++;
                continue;
            }
            if (index + 1 < line.length() && line.charAt(index + 1) == '"') {
                field.append('"');
                index += 2;
                continue;
            }
            return index + 1;
        }
        throw new IllegalArgumentException();
    }

    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    private static boolean isBoolean(String value) {
        return "true".equals(value) || "false".equals(value);
    }

    /**
     * Saves all tasks to the configured storage file.
     *
     * @param taskStorage Tasks to save.
     */
    public void saveTaskData(TaskStorage taskStorage) throws StorageException {
        String output = StreamSupport.stream(taskStorage.spliterator(), false)
                .map(Task::encode)
                .collect(Collectors.joining("\n"));
        Path storagePath = Path.of(taskStoragePath).toAbsolutePath();
        try {
            storageWriter.write(storagePath, output);
        } catch (IOException exception) {
            throw StorageException.createWriteFailure(taskStoragePath);
        }
    }

    private static void writeAtomically(Path storagePath, String output) throws IOException {
        Path temporaryPath = null;
        try {
            temporaryPath = Files.createTempFile(
                    storagePath.getParent(), storagePath.getFileName().toString(), ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(
                    temporaryPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                writer.write(output);
            }
            try {
                Files.move(temporaryPath, storagePath,
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                Files.move(temporaryPath, storagePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            if (temporaryPath != null) {
                try {
                    Files.deleteIfExists(temporaryPath);
                } catch (IOException exception) {
                    // The completed save is still valid when temporary-file cleanup fails.
                }
            }
        }
    }
}
