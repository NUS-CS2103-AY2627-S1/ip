package nelson.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import nelson.exception.NelsonException;
import nelson.task.Deadline;
import nelson.task.Event;
import nelson.task.Task;
import nelson.task.TaskList;
import nelson.task.Todo;

/** Loads tasks from and saves tasks to Nelson's storage file. */
public class Storage {
    /** The relative or absolute path supplied for the storage file. */
    private final Path taskFile;

    /**
     * Creates storage backed by the supplied OS-independent path.
     * @param taskFile path of the task data file
     */
    public Storage(Path taskFile) {
        assert taskFile != null : "Storage path must not be null";
        this.taskFile = taskFile;
    }

    /**
     * Loads valid tasks, returning an empty list when storage is unavailable.
     * @return the tasks restored from the storage file
     */
    public TaskList load() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        try {
            if (!Files.isRegularFile(taskFile)) {
                return new TaskList(loadedTasks);
            }
            for (String line : Files.readAllLines(taskFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\s*\\|\\s*", -1);
                if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
                    continue;
                }
                Task task;
                try {
                    task = parseStoredTask(parts);
                } catch (NelsonException exception) {
                    System.out.println("Molo! I found a corrupted saved task and skipped it. "
                            + "Saved dates must use the yyyy-MM-dd format.");
                    continue;
                }
                if (task == null) {
                    continue;
                }
                if (parts[1].equals("1")) {
                    task.markAsDone();
                }
                loadedTasks.add(task);
            }
        } catch (IOException | SecurityException exception) {
            System.err.println("Warning: unable to load tasks from " + taskFile
                    + ". Starting with an empty list.");
        }
        return new TaskList(loadedTasks);
    }

    /**
     * Saves the current task list, creating its parent folder when necessary.
     * @param tasks tasks to write to the storage file
     */
    public void save(TaskList tasks) {
        try {
            if (taskFile.getParent() != null) {
                Files.createDirectories(taskFile.getParent());
            }
            ArrayList<String> savedTasks = new ArrayList<>();
            for (Task task : tasks) {
                savedTasks.add(task.toStorageString());
            }
            Files.write(taskFile, savedTasks, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            System.err.println("Warning: unable to save tasks to " + taskFile + ".");
        }
    }

    /**
     * Builds one task from a validated storage record.
     * @param parts fields from one pipe-delimited storage record
     * @return the reconstructed task, or {@code null} for an unknown record
     * @throws NelsonException if a saved date cannot be parsed
     */
    private Task parseStoredTask(String[] parts) throws NelsonException {
        switch (parts[0]) {
            case "T":
                return parts.length == 3 && !parts[2].isBlank() ? new Todo(parts[2]) : null;
            case "D":
                return parts.length == 4 && !parts[2].isBlank() && !parts[3].isBlank()
                        ? new Deadline(parts[2], parts[3]) : null;
            case "E":
                return parts.length == 5 && !parts[2].isBlank() && !parts[3].isBlank()
                        && !parts[4].isBlank() ? new Event(parts[2], parts[3], parts[4]) : null;
            default:
                return null;
        }
    }
}
