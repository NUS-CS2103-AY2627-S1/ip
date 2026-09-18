package luna.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import luna.LunaException;
import luna.task.Deadline;
import luna.task.Event;
import luna.task.Task;
import luna.task.TaskList;
import luna.task.Todo;

/**
 * Handles loading tasks from disk and saving tasks back to disk.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("./data/luna.txt");
    private static final Path DEFAULT_ARCHIVE_FILE_PATH = Path.of("./data/archive.txt");
    private static final String FIELD_SEPARATOR = " | ";
    private final Path filePath;
    private final Path archiveFilePath;

    /**
     * Creates storage backed by Luna's default data file.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH, DEFAULT_ARCHIVE_FILE_PATH);
    }

    /**
     * Creates storage backed by the given data file.
     *
     * @param filePath File used to save and load tasks.
     */
    Storage(Path filePath) {
        this(filePath, filePath.resolveSibling("archive.txt"));
    }

    /**
     * Creates storage backed by the given active and archive files.
     *
     * @param filePath File used to save and load active tasks.
     * @param archiveFilePath File used to append and load archived tasks.
     */
    Storage(Path filePath, Path archiveFilePath) {
        this.filePath = filePath;
        this.archiveFilePath = archiveFilePath;
    }

    /**
     * Loads all saved tasks from the data file.
     *
     * @return List of tasks loaded from storage.
     * @throws IOException If reading the file fails.
     * @throws LunaException If the saved file format is invalid.
     */
    public List<Task> loadTasks() throws IOException, LunaException {
        return loadTasksFrom(filePath, true);
    }

    /**
     * Loads all archived tasks in the order they were archived.
     *
     * @return List of archived tasks loaded from storage.
     * @throws IOException If reading the archive file fails.
     * @throws LunaException If the archived file format is invalid.
     */
    public List<Task> loadArchivedTasks() throws IOException, LunaException {
        return loadTasksFrom(archiveFilePath, false);
    }

    /**
     * Appends tasks to the archive without replacing earlier archive entries.
     *
     * @param tasks Tasks to append to the archive.
     * @throws IOException If reading or writing the archive file fails.
     * @throws LunaException If the existing archive content is malformed.
     */
    public void archiveTasks(List<Task> tasks) throws IOException, LunaException {
        assert tasks != null : "Archived tasks must be provided as a non-null collection";
        assert tasks.stream().allMatch(task -> task != null)
                : "The archive must not contain null tasks";

        if (tasks.isEmpty()) {
            return;
        }

        List<String> lines = new ArrayList<>();
        if (Files.exists(archiveFilePath)) {
            List<String> existingLines = Files.readAllLines(archiveFilePath);
            validateStorageLines(existingLines, false);
            lines.addAll(existingLines);
        }
        lines.addAll(tasks.stream()
                .map(Task::toStorageString)
                .toList());
        writeLinesAtomically(archiveFilePath, lines);
    }

    /**
     * Loads task records from the given file.
     */
    private List<Task> loadTasksFrom(Path sourcePath, boolean shouldRejectDuplicates)
            throws IOException, LunaException {
        if (!Files.exists(sourcePath)) {
            return new ArrayList<>();
        }

        List<String> lines = Files.readAllLines(sourcePath);
        return validateStorageLines(lines, shouldRejectDuplicates);
    }

    /**
     * Validates and converts storage lines into tasks.
     */
    private List<Task> validateStorageLines(List<String> lines, boolean shouldRejectDuplicates)
            throws LunaException {
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank()) {
                Task task;
                try {
                    task = parseTask(line);
                } catch (LunaException e) {
                    throw new LunaException("Saved data is invalid at line " + (i + 1) + ": " + e.getMessage());
                }

                if (shouldRejectDuplicates && tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                    throw new LunaException("Saved data contains a duplicate task at line " + (i + 1) + ".");
                }
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws IOException If writing the file fails.
     */
    public void saveTasks(TaskList tasks) throws IOException {
        List<String> lines = tasks.asList().stream()
                .map(Task::toStorageString)
                .toList();

        assert lines.size() == tasks.size() : "Every task must produce exactly one storage line";
        writeLinesAtomically(filePath, lines);
    }

    /**
     * Parses one saved task line into an in-memory task object.
     *
     * @param line One non-blank line from the save file.
     * @return Parsed task represented by the line.
     * @throws LunaException If the saved line has an invalid format or status value.
     */
    private Task parseTask(String line) throws LunaException {
        assert line != null && !line.isBlank() : "parseTask expects a non-blank storage line";
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            throw new LunaException("Saved data file is corrupted.");
        }

        assert parts.length >= 3 : "createTask expects the validated base storage fields";
        Task task = createTask(parts);
        assert task != null : "A valid storage record must create a task";
        if ("1".equals(parts[1])) {
            task.markAsDone();
        } else if (!"0".equals(parts[1])) {
            throw new LunaException("Saved task status is invalid.");
        }

        return task;
    }

    /**
     * Creates a task instance from the split save-file fields.
     *
     * @param parts Fields extracted from one saved task line.
     * @return Task instance matching the saved task type.
     * @throws LunaException If the task type, field count, or date values are invalid.
     */
    private Task createTask(String[] parts) throws LunaException {
        assert parts != null && parts.length >= 3
                : "createTask expects a non-null record with base fields";
        String taskType = parts[0];

        try {
            switch (taskType) {
                case "T":
                    return new Todo(joinDescription(parts, 0));
                case "D":
                    return createDeadline(parts);
                case "E":
                    return createEvent(parts);
                default:
                    throw new LunaException("Saved task type is invalid.");
            }
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new LunaException("Saved task details are invalid.");
        }
    }

    /**
     * Creates a deadline from validated storage fields.
     *
     * @param parts Fields extracted from a saved deadline.
     * @return Deadline represented by the fields.
     * @throws LunaException If the field count is invalid.
     */
    private Deadline createDeadline(String[] parts) throws LunaException {
        if (parts.length < 4) {
            throw new LunaException("Saved deadline task is corrupted.");
        }

        LocalDate deadlineDate = LocalDate.parse(parts[parts.length - 1]);
        return new Deadline(joinDescription(parts, 1), deadlineDate);
    }

    /**
     * Creates an event from validated storage fields.
     *
     * @param parts Fields extracted from a saved event.
     * @return Event represented by the fields.
     * @throws LunaException If the field count is invalid.
     */
    private Event createEvent(String[] parts) throws LunaException {
        if (parts.length < 5) {
            throw new LunaException("Saved event task is corrupted.");
        }

        LocalDateTime from = LocalDateTime.parse(parts[parts.length - 2]);
        LocalDateTime to = LocalDateTime.parse(parts[parts.length - 1]);
        return new Event(joinDescription(parts, 2), from, to);
    }

    /**
     * Reconstructs a description while preserving separator text inside it.
     *
     * @param parts Fields extracted from a saved task line.
     * @param trailingFieldCount Number of fields after the description.
     * @return Reconstructed task description.
     */
    private String joinDescription(String[] parts, int trailingFieldCount) {
        int descriptionEnd = parts.length - trailingFieldCount;
        return String.join(FIELD_SEPARATOR, Arrays.copyOfRange(parts, 2, descriptionEnd));
    }

    /**
     * Replaces a storage file only after its complete new content has been written.
     */
    private void writeLinesAtomically(Path targetPath, List<String> lines) throws IOException {
        Path absoluteTarget = targetPath.toAbsolutePath();
        Path parentDirectory = absoluteTarget.getParent();
        Files.createDirectories(parentDirectory);
        String temporaryFilePrefix = absoluteTarget.getFileName().toString();
        if (temporaryFilePrefix.length() < 3) {
            temporaryFilePrefix = "luna-" + temporaryFilePrefix;
        }
        Path temporaryFile = Files.createTempFile(parentDirectory, temporaryFilePrefix, ".tmp");

        try {
            Files.write(temporaryFile, lines);
            try {
                Files.move(temporaryFile, absoluteTarget,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, absoluteTarget, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }
}
