package peanutbuttercat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to the application's storage file.
 */
public class Storage {
    private static final String LOAD_WARNING = "Heads up: some saved task data could not be read. "
            + "I recovered what I could, so please check your task list.";
    private static final String SAVE_ERROR = "I couldn't save that change. Please check that "
            + "PeanutButterCat can write to its data folder, then try again.";

    private final Path saveFile;
    private String loadWarning;

    /**
     * Creates storage backed by the supplied file path.
     *
     * @param filePath Location of the task data file.
     */
    public Storage(String filePath) {
        this.saveFile = Path.of(filePath);
    }

    /**
     * Saves all tasks using the existing atomic-replacement strategy.
     *
     * @param tasks Tasks to save.
     * @throws StorageException If the tasks cannot be written safely.
     */
    public void save(TaskList tasks) throws StorageException {
        List<String> taskRecords = tasks.asList().stream()
                .map(Task::toFileString)
                .toList();

        try {
            Path storageDirectory = saveFile.toAbsolutePath().getParent();
            assert storageDirectory != null : "An absolute save path must have a parent directory";

            Files.createDirectories(storageDirectory);
            Path temporaryFile = Files.createTempFile(storageDirectory, "peanutbuttercat-", ".tmp");
            try {
                Files.write(temporaryFile, taskRecords);
                try {
                    Files.move(temporaryFile, saveFile.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                    Files.move(temporaryFile, saveFile.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporaryFile);
            }
        } catch (IOException | SecurityException exception) {
            throw new StorageException(SAVE_ERROR, exception);
        }
    }

    /**
     * Loads saved tasks, ignoring blank or malformed records as before.
     *
     * @return The valid loaded tasks, or an empty list when the file is missing or unavailable.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        loadWarning = null;
        try {
            int lineNumber = 0;
            for (String taskRecord : Files.readAllLines(saveFile)) {
                lineNumber++;
                if (taskRecord.isBlank()) {
                    continue;
                }
                try {
                    Task task = createTaskFromRecord(taskRecord, lineNumber);
                    if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                        throw new IllegalArgumentException("Duplicate task record");
                    }
                    tasks.add(task);
                } catch (IllegalArgumentException exception) {
                    System.err.println("Ignoring invalid task record at line " + lineNumber + ".");
                    loadWarning = LOAD_WARNING;
                }
            }
        } catch (NoSuchFileException exception) {
            return tasks;
        } catch (IOException | SecurityException exception) {
            System.err.println("Unable to load tasks: " + exception.getMessage());
            loadWarning = LOAD_WARNING;
        }
        return tasks;
    }

    /** Returns a user-friendly warning after a load problem, or {@code null} if loading was clean. */
    String getLoadWarning() {
        return loadWarning;
    }

    private Task createTaskFromRecord(String taskRecord, int lineNumber) {
        String[] details = splitStorageRecord(taskRecord);
        if (details.length < 3 || (!details[1].equals("0") && !details[1].equals("1"))) {
            throw new IllegalArgumentException("Malformed task record");
        }
        Task task;
        int baseFieldCount;
        switch (details[0]) {
            case "T":
                baseFieldCount = 3;
                requireSupportedFieldCount(details, baseFieldCount);
                task = new Todo(details[2]);
                break;
            case "D":
                baseFieldCount = 4;
                requireSupportedFieldCount(details, baseFieldCount);
                task = new Deadline(details[2], parseStoredDateTime(details[3]));
                break;
            case "E":
                baseFieldCount = 5;
                requireSupportedFieldCount(details, baseFieldCount);
                task = new Event(details[2], parseStoredDateTime(details[3]), parseStoredDateTime(details[4]));
                break;
            default:
                throw new IllegalArgumentException("Unknown task type in saved data: " + details[0]);
        }

        boolean isDone = details[1].equals("1");
        LocalDate completionDate = null;
        if (details.length == baseFieldCount + 1) {
            try {
                completionDate = parseStoredCompletionDate(details[baseFieldCount]);
                if (!isDone) {
                    throw new IllegalArgumentException("Incomplete task has completion metadata");
                }
            } catch (IllegalArgumentException exception) {
                System.err.println("Ignoring invalid completion metadata at line " + lineNumber + ".");
                loadWarning = LOAD_WARNING;
                completionDate = null;
            }
        }
        task.restoreCompletion(isDone, completionDate);
        return task;
    }

    private static void requireSupportedFieldCount(String[] details, int baseFieldCount) {
        if (details.length < baseFieldCount || details.length > baseFieldCount + 1) {
            throw new IllegalArgumentException("Malformed task record");
        }
    }

    /** Splits a record without treating escaped pipe characters as delimiters. */
    private static String[] splitStorageRecord(String record) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < record.length(); i++) {
            char current = record.charAt(i);
            if (escaped) {
                if (current != '|' && current != '\\') {
                    throw new IllegalArgumentException("Invalid escape sequence");
                }
                field.append(current);
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        if (escaped) {
            throw new IllegalArgumentException("Unterminated escape sequence");
        }
        fields.add(field.toString().trim());
        return fields.toArray(String[]::new);
    }

    private static LocalDateTime parseStoredDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid stored date", exception);
        }
    }

    private static LocalDate parseStoredCompletionDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid stored completion date", exception);
        }
    }
}
