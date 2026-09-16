package panda.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import panda.model.Deadline;
import panda.model.Event;
import panda.model.Task;
import panda.model.Todo;

/**
 * Saves Panda's task list to a fixed file on disk.
 */
public class Storage {
    private static final Path DEFAULT_SAVE_FILE = Path.of("data", "panda.txt");
    private static final String SAVE_FILE_PROPERTY = "panda.save.path";

    /**
     * Writes the current task list to disk, creating the data directory when needed.
     *
     * @param tasks tasks to save
     */
    public static void saveTasks(List<Task> tasks) {
        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(task.toFileString());
        }

        Path temporaryFile = null;
        try {
            Path saveFile = getSaveFile();
            Files.createDirectories(saveFile.getParent());
            temporaryFile = Files.createTempFile(saveFile.getParent(), "panda-", ".tmp");
            Files.write(temporaryFile, taskLines);
            moveIntoPlace(temporaryFile, saveFile);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save tasks to " + getSaveFile(), e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // A future save can safely replace an abandoned temporary file.
                }
            }
        }
    }

    /**
     * Loads tasks saved by {@link #saveTasks(List)}. A missing save file represents
     * a new user with an empty task list.
     *
     * @return the tasks stored in the save file
     */
    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Path saveFile = getSaveFile();
        if (!Files.exists(saveFile)) {
            return tasks;
        }

        try {
            for (String taskLine : Files.readAllLines(saveFile)) {
                if (!taskLine.isBlank()) {
                    try {
                        tasks.add(createTask(taskLine));
                    } catch (RuntimeException ignored) {
                        // Ignore malformed records and keep loading valid tasks.
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load tasks from " + saveFile, e);
        }
        return tasks;
    }

    /**
     * Recreates one task from a line in Panda's save-file format.
     *
     * @param taskLine one saved task line
     * @return the recreated task
     */
    private static Task createTask(String taskLine) {
        String[] taskDetails = taskLine.split("\\s*\\|\\s*", -1);
        if (taskDetails.length < 3 || taskDetails[1].isBlank() || taskDetails[2].isBlank()) {
            throw new IllegalArgumentException("Malformed task record");
        }
        Task task;
        switch (taskDetails[0]) {
        case "T":
            if (taskDetails.length != 3) {
                throw new IllegalArgumentException("Malformed todo record");
            }
            task = new Todo(taskDetails[2]);
            break;
        case "D":
            if (taskDetails.length != 4 || taskDetails[3].isBlank()) {
                throw new IllegalArgumentException("Malformed deadline record");
            }
            task = new Deadline(taskDetails[2], taskDetails[3]);
            break;
        case "E":
            if (taskDetails.length != 5 || taskDetails[3].isBlank() || taskDetails[4].isBlank()) {
                throw new IllegalArgumentException("Malformed event record");
            }
            task = new Event(taskDetails[2], taskDetails[3], taskDetails[4]);
            break;
        default:
            throw new IllegalArgumentException("Unknown task type in save file: " + taskDetails[0]);
        }

        if (taskDetails[1].equals("1")) {
            task.markAsDone();
        } else if (!taskDetails[1].equals("0")) {
            throw new IllegalArgumentException("Malformed task status");
        }
        return task;
    }

    /**
     * Replaces the save file after the complete replacement has been written.
     *
     * @param temporaryFile completed temporary save file
     * @throws IOException if the file cannot be moved into place
     */
    private static void moveIntoPlace(Path temporaryFile, Path saveFile) throws IOException {
        try {
            Files.move(temporaryFile, saveFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Returns the save-file path, optionally overridden for isolated automated tests.
     *
     * @return the configured save-file path
     */
    private static Path getSaveFile() {
        String configuredPath = System.getProperty(SAVE_FILE_PROPERTY);
        return configuredPath == null ? DEFAULT_SAVE_FILE : Path.of(configuredPath);
    }
}
