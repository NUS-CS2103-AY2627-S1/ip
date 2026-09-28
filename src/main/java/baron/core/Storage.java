package baron.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import baron.core.exception.BaronException;
import baron.core.task.Deadline;
import baron.core.task.Event;
import baron.core.task.Task;
import baron.core.task.TaskList;
import baron.core.task.TaskType;
import baron.core.task.Todo;

/**
 * Reads and writes Baron tasks in a text file.
 */
class Storage {
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());

    static {
        StorageWarningHandler.initialize();
    }

    private static final String FIELD_SEPARATOR = " \\| ";
    private static final String TASK_UUID_SEPARATOR = ", ";
    private static final int TASK_UUID_FIELD_INDEX = 0;
    private static final int TASK_TYPE_FIELD_INDEX = 1;
    private static final int TASK_STATUS_FIELD_INDEX = 2;
    private static final int TASK_DESCRIPTION_FIELD_INDEX = 3;
    private static final int REQUIRED_TASKS_FIELD_INDEX = 4;
    private static final int DEADLINE_FIELD_INDEX = 5;
    private static final int EVENT_START_FIELD_INDEX = 5;
    private static final int EVENT_END_FIELD_INDEX = 6;

    private final Path filePath;
    private boolean available;

    /**
     * Creates storage for the specified task file, creating it if necessary.
     *
     * @param filePath The path to the task data file.
     */
    public Storage(Path filePath) {
        Objects.requireNonNull(filePath, "The task file path must not be null");
        this.filePath = filePath;
        available = true;
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            available = false;
            LOGGER.warning("Could not initialize the task file: " + e.getMessage());
        }
    }

    /** Returns whether the task file can be safely read and written. */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Reads saved tasks and adds them to Baron's list of tasks.
     */
    public TaskList readTasks() {
        String taskStrings = "";
        try {
            taskStrings = Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            available = false;
            LOGGER.warning("Could not read the task file: " + e.getMessage());
        }
        return loadTasks(taskStrings);
    }

    /** Loads tasks from the supplied serialized task contents. */
    public TaskList loadTasks(String taskStrings) {
        TaskList tasks = new TaskList();
        Map<UUID, Task> uuidToTask = new HashMap<>();
        for (String taskString : taskStrings.split(System.lineSeparator())) {
            if (taskString.isBlank()) {
                continue;
            }
            try {
                Task task = parseTaskString(taskString);
                if (uuidToTask.containsKey(task.getUuid())) {
                    throw new BaronException("Duplicate task ID in record '" + taskString + "'");
                }
                uuidToTask.put(task.getUuid(), task);
                tasks.addTask(task);
            } catch (BaronException e) {
                LOGGER.warning(e.getMessage());
            }
        }

        for (String taskString : taskStrings.split(System.lineSeparator())) {
            if (!taskString.isBlank()) {
                establishRequirements(tasks, uuidToTask, taskString);
            }
        }
        return tasks;
    }

    /**
     * Returns a task reconstructed from one saved task record.
     *
     * @param taskString The saved task record.
     * @return The reconstructed task, or {@code null} for a blank record.
     * @throws BaronException If the record has an invalid format.
     */
    private Task parseTaskString(String taskString) throws BaronException {
        String[] taskFields = taskString.split(FIELD_SEPARATOR, -1);
        try {
            TaskType taskType = TaskType.fromFileCode(taskFields[TASK_TYPE_FIELD_INDEX]);
            Task task = switch (taskType) {
                case TODO -> new Todo(
                        UUID.fromString(taskFields[TASK_UUID_FIELD_INDEX]),
                        taskFields[TASK_DESCRIPTION_FIELD_INDEX]);
                case DEADLINE -> new Deadline(
                        UUID.fromString(taskFields[TASK_UUID_FIELD_INDEX]),
                        taskFields[TASK_DESCRIPTION_FIELD_INDEX],
                        LocalDateTime.parse(taskFields[DEADLINE_FIELD_INDEX]));
                case EVENT -> new Event(
                        UUID.fromString(taskFields[TASK_UUID_FIELD_INDEX]),
                        taskFields[TASK_DESCRIPTION_FIELD_INDEX],
                        LocalDateTime.parse(taskFields[EVENT_START_FIELD_INDEX]),
                        LocalDateTime.parse(taskFields[EVENT_END_FIELD_INDEX]));
            };
            switch (taskFields[TASK_STATUS_FIELD_INDEX]) {
                case "1" -> task.markAsDone();
                case "0" -> task.markAsNotDone();
                default -> throw new BaronException("Unknown task status");
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException
                | DateTimeParseException
                | IllegalArgumentException e) {
            throw new BaronException("Invalid task '" + taskString + "'");
        }
    }

    private void establishRequirements(TaskList tasks, Map<UUID, Task> uuidToTask, String taskString) {
        try {
            String[] taskFields = taskString.split(FIELD_SEPARATOR, -1);
            Task task = uuidToTask.get(UUID.fromString(taskFields[TASK_UUID_FIELD_INDEX]));
            if (task == null || taskFields[REQUIRED_TASKS_FIELD_INDEX].isBlank()) {
                return;
            }
            List<Task> requiredTasksFromFile = Arrays.stream(
                    taskFields[REQUIRED_TASKS_FIELD_INDEX].split(TASK_UUID_SEPARATOR))
                    .map(String::trim)
                    .map(uuid -> findTask(uuid, uuidToTask))
                    .filter(Objects::nonNull)
                    .toList();
            TaskList requiredTasks = new TaskList(requiredTasksFromFile);
            task.setRequiredTasks(tasks, requiredTasks);
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | BaronException e) {
            LOGGER.warning("Could not restore relationships for task record '" + taskString + "'.");
        }
    }

    /** Returns the task identified by the UUID, recording a warning if it is missing. */
    private Task findTask(String uuidString, Map<UUID, Task> uuidToTask) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            Task task = uuidToTask.get(uuid);
            if (task == null) {
                LOGGER.warning("Could not restore relationship to missing task '" + uuidString + "'.");
            }
            return task;
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Could not restore relationship to invalid task ID '" + uuidString + "'.");
            return null;
        }
    }

    /**
     * Replaces the saved tasks with the current contents of Baron's task list.
     */
    public void writeTasks(TaskList tasks) throws BaronException {
        if (!available) {
            throw new BaronException("Could not access the task file. Your tasks were not changed.");
        }
        Path temporaryFile = null;
        try {
            Path parent = filePath.getParent() == null ? Path.of(".") : filePath.getParent();
            temporaryFile = Files.createTempFile(parent, filePath.getFileName().toString(), ".tmp");
            Files.writeString(temporaryFile, tasks.toFileString(), StandardCharsets.UTF_8);
            moveTemporaryFile(temporaryFile);
        } catch (IOException e) {
            throw new BaronException("Could not save tasks. Please check that the task file is writable.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Replaces the task file with a successfully written temporary file.
     *
     * @param temporaryFile The temporary file containing the new task state.
     * @throws IOException If the file cannot be moved.
     */
    private void moveTemporaryFile(Path temporaryFile) throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    filePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Deletes the temporary file after a save attempt.
     *
     * @param temporaryFile The temporary file to delete, if it was created.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException e) {
            // Cleanup failure must not hide the original save result.
        }
    }
}
