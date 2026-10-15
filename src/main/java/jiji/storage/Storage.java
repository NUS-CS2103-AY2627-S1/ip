package jiji.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jiji.exception.JijiStorageException;
import jiji.task.Deadline;
import jiji.task.Event;
import jiji.task.Task;
import jiji.task.TaskList;
import jiji.task.TaskType;
import jiji.task.Todo;

/**
 * Manages loading tasks from and saving tasks to persistent disk storage.
 */
public class Storage {

    private static final String FILE_DELIMITER_REGEX = " \\| ";
    private static final String STATUS_DONE = "1";
    private static final int MIN_PARTS_COUNT = 3;
    private static final int DEADLINE_PARTS_COUNT = 4;
    private static final int EVENT_PARTS_COUNT = 5;

    private final Path filePath;

    /**
     * Constructs a Storage instance with the given relative or absolute file path.
     *
     * @param filePath Path string to the storage file.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "Storage filePath cannot be null or blank";
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads the list of tasks from the storage file. If the file does not exist,
     * an empty task list is returned. Corrupted or malformed lines are skipped gracefully.
     *
     * @return A list of {@link Task} objects loaded from disk.
     * @throws JijiStorageException If an I/O error occurs while reading the file.
     */
    public List<Task> load() throws JijiStorageException {
        List<Task> tasks = new ArrayList<>();
        try {
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                return tasks;
            }
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                Task task = parseTaskFromLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new JijiStorageException("Could not load tasks from storage: " + e.getMessage());
        }
        assert tasks != null : "Loaded task list cannot be null";
        return tasks;
    }

    /**
     * Parses a single serialized task line from the storage file into a {@link Task} object.
     * Corrupted or unrecognized lines return {@code null}.
     *
     * @param line The raw line from the storage file.
     * @return The deserialized {@link Task}, or {@code null} if the line is invalid.
     */
    private Task parseTaskFromLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] parts = trimmed.split(FILE_DELIMITER_REGEX);
        if (parts.length < MIN_PARTS_COUNT) {
            return null;
        }

        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals(STATUS_DONE);
        String description = parts[2].trim();

        TaskType taskType = TaskType.fromCode(type);
        if (taskType == null) {
            return null;
        }

        Task task = createTask(taskType, description, parts);
        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Constructs a task instance corresponding to the given task type and arguments.
     *
     * @param taskType The recognized task type.
     * @param description The task description.
     * @param parts The split line tokens.
     * @return A new {@link Task} instance, or {@code null} if required arguments are missing.
     */
    private Task createTask(TaskType taskType, String description, String[] parts) {
        switch (taskType) {
            case TODO:
                return new Todo(description);
            case DEADLINE:
                if (parts.length >= DEADLINE_PARTS_COUNT) {
                    String by = parts[3].trim();
                    return new Deadline(description, by);
                }
                return null;
            case EVENT:
                if (parts.length >= EVENT_PARTS_COUNT) {
                    String from = parts[3].trim();
                    String to = parts[4].trim();
                    return new Event(description, from, to);
                }
                return null;
            default:
                return null;
        }
    }

    /**
     * Saves the current list of tasks to the storage file.
     *
     * @param taskList The {@link TaskList} containing tasks to persist.
     * @throws JijiStorageException If an I/O error occurs while writing to the file.
     */
    public void save(TaskList taskList) throws JijiStorageException {
        assert taskList != null : "TaskList to save cannot be null";
        assert filePath != null : "Storage filePath cannot be null";
        try {
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            List<String> lines = taskList.getAllTasks().stream()
                    .map(Task::toFileFormat)
                    .collect(Collectors.toList());
            Files.write(filePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new JijiStorageException("Could not save tasks to storage: " + e.getMessage());
        }
    }
}
