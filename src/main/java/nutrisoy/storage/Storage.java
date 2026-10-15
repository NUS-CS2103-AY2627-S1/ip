package nutrisoy.storage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Deadline;
import nutrisoy.task.Event;
import nutrisoy.task.Task;
import nutrisoy.task.Todo;

/**
 * Loads tasks from and saves tasks to a local file.
 */
public class Storage {
    private final String filePath;
    // A partial or unsuccessful load must never be saved over the original file.
    private String loadWarning = "";

    /**
     * Creates storage that uses the specified file path.
     *
     * @param filePath path to the task data file
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "Storage file path must be valid";
        this.filePath = filePath;
    }

    /**
     * Loads valid tasks from the storage file.
     *
     * @return tasks loaded from the storage file, or an empty list when none can be loaded
     * @throws DukeException if the configured path cannot be read
     */
    public ArrayList<Task> loadTasks() throws DukeException {
        assert filePath != null && !filePath.isBlank() : "Storage file path must be valid";
        loadWarning = "";
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return tasks;
        }
        if (!file.isFile() || !file.canRead()) {
            loadWarning = "The storage path is not a readable file: " + filePath;
            throw new DukeException(loadWarning + " Fix the file and restart NutriSoy.");
        }

        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    Task task = parseLineToTask(line);
                    if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                        throw new DukeException("Duplicate task");
                    }
                    tasks.add(task);
                } catch (DukeException | IllegalArgumentException | DateTimeParseException e) {
                    loadWarning += "Invalid data on line " + lineNumber + ". ";
                }
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        } catch (IOException | SecurityException e) {
            loadWarning = "I couldn't read the task file. Check that it is accessible.";
            throw new DukeException(loadWarning + " Fix the file and restart NutriSoy.");
        }

        return tasks;
    }

    /**
     * Saves the supplied tasks to the storage file.
     *
     * @param tasks tasks to persist
     * @throws DukeException if the tasks cannot be written to the configured path
     */
    public void saveTasks(ArrayList<Task> tasks) throws DukeException {
        assert tasks != null : "Tasks to save must not be null";
        requireSafeSave();
        Path temporaryFile = null;
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new DukeException("I couldn't create the data directory needed to save your tasks.");
            }
            if (parentDir != null && !parentDir.isDirectory()) {
                throw new DukeException("The configured data location is not a directory.");
            }

            Path target = file.toPath().toAbsolutePath();
            if (Files.isDirectory(target) || Files.isSymbolicLink(target)) {
                throw new DukeException("The storage path must be a regular file, not a directory or symbolic link.");
            }
            temporaryFile = Files.createTempFile(target.getParent(), "nutrisoy-", ".tmp");
            try (var writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
            try {
                Files.move(temporaryFile, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                throw new DukeException("This location does not support safe file replacement. "
                        + "Use a local writable folder; the original task file was kept.");
            }
        } catch (IOException | SecurityException e) {
            throw new DukeException("I couldn't save your tasks. Check the data file permissions and try again.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException e) {
                    // A leftover temporary file is safer than touching the original task file.
                }
            }
        }
    }

    /**
     * Returns load diagnostics together with advice for restoring writable storage.
     *
     * @return warning text, or an empty string if all data loaded successfully
     */
    public String getLoadWarning() {
        return loadWarning.isEmpty() ? "" : loadWarning + " Task changes are disabled. "
                + "Back up and repair the task file, then restart NutriSoy. You can still use list, find, and bye.";
    }

    /**
     * Prevents saving a partial or failed load over the user's original data.
     *
     * @throws DukeException if the file needs recovery before changes are safe
     */
    public void requireSafeSave() throws DukeException {
        if (!loadWarning.isEmpty()) {
            throw new DukeException(getLoadWarning());
        }
    }

    private Task parseLineToTask(String line) throws DukeException {
        assert line != null : "Storage line must not be null";
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw new DukeException("Corrupted format");
        }
        assert parts.length >= 3 : "A task line must contain type, status, and description";

        String type = parts[0];
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new DukeException("Invalid completion status");
        }
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        if (description.isBlank()) {
            throw new DukeException("Missing task description");
        }

        Task task;
        int tagFieldIndex;
        switch (type) {
            case "T":
                if (parts.length > 4) {
                    throw new DukeException("Unexpected todo fields");
                }
                task = new Todo(description);
                tagFieldIndex = 3;
                break;
            case "D":
                if (parts.length < 4) {
                    throw new DukeException("Missing deadline date");
                }
                if (parts.length > 5) {
                    throw new DukeException("Unexpected deadline fields");
                }
                assert parts.length >= 4 : "A deadline line must contain a date";
                LocalDate byDate;
                try {
                    byDate = LocalDate.parse(parts[3]);
                    tagFieldIndex = 4;
                } catch (DateTimeParseException e) {
                    if (parts.length < 5) {
                        throw new DukeException("Invalid deadline date");
                    }
                    byDate = LocalDate.parse(parts[4]);
                    tagFieldIndex = 3;
                }
                task = new Deadline(description, byDate);
                break;
            case "E":
                if (parts.length < 5) {
                    throw new DukeException("Missing event timeline");
                }
                if (parts.length > 6) {
                    throw new DukeException("Unexpected event fields");
                }
                assert parts.length >= 5 : "An event line must contain two dates";
                LocalDate fromDate;
                LocalDate toDate;
                try {
                    fromDate = LocalDate.parse(parts[3]);
                    toDate = LocalDate.parse(parts[4]);
                    tagFieldIndex = 5;
                } catch (DateTimeParseException e) {
                    if (parts.length < 6) {
                        throw new DukeException("Invalid event timeline");
                    }
                    fromDate = LocalDate.parse(parts[4]);
                    toDate = LocalDate.parse(parts[5]);
                    tagFieldIndex = 3;
                }
                if (!fromDate.isBefore(toDate)) {
                    throw new DukeException("Event start date must precede its end date");
                }
                task = new Event(description, fromDate, toDate);
                break;
            default:
                throw new DukeException("Unknown task type");
        }

        if (parts.length > tagFieldIndex) {
            addTags(task, parts[tagFieldIndex]);
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private void addTags(Task task, String tagsField) {
        if (tagsField.isEmpty()) {
            return;
        }
        for (String tag : tagsField.split(",", -1)) {
            if (!Task.isValidTagName(tag)) {
                throw new IllegalArgumentException("Invalid stored tag");
            }
            if (task.hasTag(tag)) {
                throw new IllegalArgumentException("Duplicate stored tag");
            }
            task.addTag(tag);
        }
    }
}
