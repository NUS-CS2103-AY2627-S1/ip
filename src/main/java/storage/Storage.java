package storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import exception.CbtException;
import parser.Parser;
import task.Task;
import task.TaskList;

/** Loads tasks from and saves tasks to the application's data file. */
public class Storage {
    private final Path filePath;
    private String loadWarning;

    /**
     * Creates a storage manager for the specified data file.
     *
     * @param filePath path to the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads all valid tasks from the data file.
     *
     * @return loaded tasks, or an empty list if the file does not exist or cannot be read.
     */
    public TaskList loadTasks() {
        TaskList tasks = new TaskList();
        loadWarning = null;
        try {
            if (Files.notExists(filePath)) {
                return tasks;
            }
            if (!Files.isRegularFile(filePath)) {
                loadWarning = "CBT could not read the saved task file because it is not a regular file.";
                return tasks;
            }
            int skippedTasks = 0;
            for (String line : Files.readAllLines(filePath)) {
                if (line.isBlank()) {
                    continue;
                }
                Task task = Parser.parseLineToTask(line);
                if (task == null) {
                    skippedTasks++;
                    continue;
                }
                try {
                    tasks.addTaskIfUnique(task);
                } catch (CbtException exception) {
                    skippedTasks++;
                }
            }
            if (skippedTasks > 0) {
                loadWarning = "CBT skipped " + skippedTasks
                        + " malformed or duplicate saved task" + (skippedTasks == 1 ? "." : "s.");
            }
        } catch (IOException | SecurityException exception) {
            loadWarning = "CBT could not read the saved task file. Check that it exists and is readable.";
        }
        return tasks;
    }

    /**
     * Returns a warning produced while loading saved tasks.
     *
     * @return warning text, or an empty value when loading completed normally.
     */
    public Optional<String> getLoadWarning() {
        return Optional.ofNullable(loadWarning);
    }

    /**
     * Writes all tasks to the data file, replacing its previous contents.
     *
     * @param tasks tasks to persist.
     * @throws CbtException if the data file cannot be replaced safely.
     */
    public void saveTasks(TaskList tasks) throws CbtException {
        Path temporaryFile = null;
        try {
            Path absoluteFilePath = filePath.toAbsolutePath();
            Path parent = absoluteFilePath.getParent();
            assert parent != null : "An absolute data path must have a parent directory";
            Files.createDirectories(parent);
            StringBuilder savedTasks = new StringBuilder();
            for (int i = 0; i < tasks.getSize(); i++) {
                savedTasks.append(tasks.getTask(i).toFileFormat()).append(System.lineSeparator());
            }
            temporaryFile = Files.createTempFile(parent, absoluteFilePath.getFileName().toString(), ".tmp");
            Files.writeString(temporaryFile, savedTasks.toString(), StandardOpenOption.TRUNCATE_EXISTING);
            replaceDataFile(temporaryFile, absoluteFilePath);
            temporaryFile = null;
        } catch (IOException | SecurityException exception) {
            throw new CbtException("CBT could not save your changes. Check access to the task data file.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Replaces the data file atomically when the file system supports it. */
    private void replaceDataFile(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Removes a temporary save file after an unsuccessful write. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original save error is more useful to the user than a cleanup failure.
        }
    }
}
