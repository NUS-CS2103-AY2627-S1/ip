package blud.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import blud.exception.StorageException;
import blud.exception.TaskTypeException;
import blud.model.Deadline;
import blud.model.Event;
import blud.model.Task;
import blud.model.TaskList;
import blud.model.ToDo;

/** Persists the current task list to the project's data directory. */
public class Storage {
    private final Path taskFile;
    private final Path parentDir;

    /**
     * Creates storage backed by the specified file.
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new StorageException("Unable to use an empty task-storage path.",
                    new IllegalArgumentException("filePath"));
        }
        try {
            this.taskFile = Path.of(filePath);
        } catch (InvalidPathException exception) {
            throw new StorageException("Unable to use task-storage path: " + filePath, exception);
        }
        this.parentDir = this.taskFile.getParent();
    }

    /** Reconstructs a task from one line of the storage format. */
    private static Task mapTaskFromString(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3 || parts[0].isBlank() || parts[2].isBlank()) {
            throw new IllegalArgumentException("stored task has missing fields");
        }
        String taskInput = parts[0];
        TaskType taskType = TaskType.stringToTaskType(taskInput);
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new IllegalArgumentException("stored completion status must be 0 or 1");
        }
        boolean isDone = parts[1].equals("1");
        String mainDescription = parts[2];
        Task newTask;

        switch (taskType) {
            case T:
                requireFieldCount(parts, 3, "todo");
                newTask = new ToDo(new String[] {String.format("TODO %s", mainDescription)});
                break;
            case D:
                requireFieldCount(parts, 4, "deadline");
                String deadline = parts[3];
                newTask = new Deadline(new String[] {
                        String.format("DEADLINE %s", mainDescription),
                        String.format("by %s", deadline)
                });
                break;
            case E:
                requireFieldCount(parts, 5, "event");
                String startDate = parts[3];
                String endDate = parts[4];
                newTask = new Event(new String[] {
                        String.format("EVENT %s", mainDescription),
                        String.format("from %s", startDate),
                        String.format("to %s", endDate)
                });
                break;
            default:
                throw new TaskTypeException(
                        String.format(
                                "invalid task type %s, please use one of todo, event or deadline task types",
                                taskType));
        }
        if (isDone) {
            newTask.mark();
        }
        return newTask;
    }

    /** Validates the number of fields required by a stored task type. */
    private static void requireFieldCount(String[] parts, int expected, String taskType) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("stored " + taskType + " task has an invalid number of fields");
        }
    }

    /**
     * Creates the parent directory and file when they do not exist.
     */
    private boolean handlePath() throws IOException {
        if (this.parentDir != null && !Files.exists(this.parentDir)) {
            Files.createDirectories(this.parentDir);
        }
        if (!Files.exists(this.taskFile)) {
            Files.createFile(this.taskFile);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Writes all tasks to disk, replacing the previous snapshot.
     *
     * @param taskList current tasks in their display order
     */
    public void saveTasks(TaskList taskList) {
        assert taskList != null : "Storage must save a non-null task list";
        assert taskList.getTaskList().stream().allMatch(task -> task != null)
                : "A task list must not contain null tasks";
        try {
            this.handlePath();
            Files.write(this.taskFile, taskList.getTaskList().stream().map(Task::toStorageString).toList());
        } catch (IOException | SecurityException e) {
            throw new StorageException("Unable to save tasks to " + taskFile + ". Check file access permissions.", e);
        }
    }

    private enum TaskType {
        T,
        D,
        E;

        /** Converts a stored task type token to its enum value. */
        public static TaskType stringToTaskType(String taskInput) {
            if (taskInput == null) {
                return null;
            }
            try {
                // Trim whitespace and convert to uppercase to match enum style
                return TaskType.valueOf(taskInput.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new TaskTypeException(
                        String.format("invalid task type %s, please use one of todo, event or deadline task types",
                                taskInput));
            }
        }

    }

    /** Loads all persisted tasks, creating the storage file if necessary. */
    public List<Task> loadTasks() {
        try {
            Boolean fileExists = this.handlePath();
            if (fileExists) {
                List<String> lines = Files.readAllLines(this.taskFile);
                List<Task> tasks = new ArrayList<>();
                for (int i = 0; i < lines.size(); i++) {
                    try {
                        tasks.add(mapTaskFromString(lines.get(i)));
                    } catch (RuntimeException exception) {
                        throw new StorageException(
                                String.format("Unable to load tasks: invalid data in %s at line %d: %s",
                                        taskFile, i + 1, exception.getMessage()), exception);
                    }
                }
                if (tasks.stream().anyMatch(firstTask -> tasks.stream().anyMatch(secondTask ->
                        firstTask != secondTask && firstTask.hasSameDetails(secondTask)))) {
                    throw new StorageException("Unable to load tasks: duplicate task details were found in "
                            + taskFile, null);
                }
                return tasks;
            } else {
                return new ArrayList<>();
            }
        } catch (IOException | SecurityException e) {
            throw new StorageException("Unable to load tasks from " + taskFile
                    + ". Check that the file exists and is readable.", e);
        }
    }
}
