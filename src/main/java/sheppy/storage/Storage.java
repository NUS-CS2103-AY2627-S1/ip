package sheppy.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sheppy.SheppyException;
import sheppy.task.Deadline;
import sheppy.task.Event;
import sheppy.task.Task;
import sheppy.task.TaskList;
import sheppy.task.Todo;

/** Handles saving and loading Sheppy's tasks from a data file. */
public class Storage {
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());

    /** The path of the file used to store tasks. */
    private final Path filePath;

    /**
     * Creates storage for a relative or absolute path.
     *
     * @param filePath the path of the task data file
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath).toAbsolutePath().normalize();
    }

    /**
     * Saves the current task list.
     *
     * @param tasks the tasks to save
     * @throws SheppyException if the directory or file cannot be written
     */
    public void save(TaskList tasks) throws SheppyException {
        Path temporaryFile = null;
        try {
            Path destination = filePath;
            if (Files.isSymbolicLink(destination)) {
                throw new SheppyException("I couldn't save your tasks: the data file is a symbolic link. "
                        + "Use a regular file to avoid replacing the link.");
            }
            Files.createDirectories(destination.getParent());
            List<String> lines = tasks.asList().stream()
                    .map(Task::toStorageString)
                    .toList();
            temporaryFile = Files.createTempFile(destination.getParent(), "sheppy-", ".tmp");
            Files.write(temporaryFile, lines);
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            throw new SheppyException("I couldn't save your tasks: this location does not support safe file "
                    + "replacement. Move Sheppy to a local folder and try again.", exception);
        } catch (IOException exception) {
            throw new SheppyException("I couldn't save your tasks to " + filePath + ": "
                    + exception.getMessage(), exception);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Loads tasks from the data file.
     *
     * @return the loaded tasks, or an empty task list if the file is absent
     * @throws SheppyException if the file cannot be read or contains invalid data
     */
    public TaskList load() throws SheppyException {
        try {
            return parseStoredLines(Files.readAllLines(filePath));
        } catch (NoSuchFileException exception) {
            if (Files.isSymbolicLink(filePath)) {
                throw new SheppyException("I couldn't load your tasks: the data file is a broken symbolic link.",
                        exception);
            }
            return new TaskList();
        } catch (IOException exception) {
            throw new SheppyException("I couldn't load your tasks from " + filePath + ": "
                    + exception.getMessage(), exception);
        }
    }

    /** Validates every record, reporting its location without returning a partial list. */
    private TaskList parseStoredLines(List<String> lines) throws SheppyException {
        List<Task> tasks = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseStoredTask(line));
            } catch (SheppyException exception) {
                throw new SheppyException(exception.getMessage() + " (line " + (index + 1)
                        + " in " + filePath + ")", exception);
            }
        }
        return new TaskList(tasks);
    }

    /** Cleans up failed saves without hiding the original failure. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Could not remove temporary task file " + temporaryFile, exception);
        }
    }

    /** Parses one task line from the storage format. */
    private Task parseStoredTask(String line) throws SheppyException {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            throw new SheppyException("your task file contains an invalid line: " + line);
        }

        String type = fields[0];
        String status = fields[1];
        String description = fields[2];
        Task task = switch (type) {
            case "T" -> {
                requireFieldCount(fields, 3);
                yield new Todo(description);
            }
            case "D" -> {
                requireFieldCount(fields, 4);
                yield new Deadline(description, parseDate(fields[3]));
            }
            case "E" -> {
                requireFieldCount(fields, 5);
                yield new Event(description, fields[3], fields[4]);
            }
            default -> throw new SheppyException("your task file contains an unknown task type: " + type);
        };

        if (status.equals("1")) {
            task.markAsDone();
        } else if (!status.equals("0")) {
            throw new SheppyException("your task file contains an invalid status: " + status);
        }
        return task;
    }

    /** Checks that a stored task has exactly the expected number of fields. */
    private void requireFieldCount(String[] fields, int expected) throws SheppyException {
        if (fields.length != expected) {
            throw new SheppyException("your task file contains the wrong number of fields.");
        }
    }

    /** Parses a stored date in the Level 8 ISO format. */
    private LocalDate parseDate(String value) throws SheppyException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new SheppyException("please use dates in yyyy-MM-dd format, such as 2019-10-15.");
        }
    }
}
