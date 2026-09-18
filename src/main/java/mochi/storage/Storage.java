package mochi.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import mochi.exception.MochiException;
import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Task;
import mochi.task.TaskList;
import mochi.task.Todo;

/**
 * Loads and saves Mochi tasks using a local text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR_PATTERN = " \\| ";

    private final Path filePath;

    /**
     * Creates storage that uses the given OS-independent path.
     *
     * @param filePath location of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, returning an empty list when the data file does not exist yet.
     *
     * @return tasks restored from disk
     * @throws MochiException if the data file cannot be read or contains invalid data
     */
    public ArrayList<Task> load() throws MochiException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                tasks.add(parseStoredTask(lines.get(i), i + 1));
            }
            return tasks;
        } catch (NoSuchFileException e) {
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new MochiException("I couldn't read your saved tasks: " + e.getMessage());
        }
    }

    /**
     * Saves all tasks, creating the data directory and file when necessary.
     *
     * @param tasks tasks to persist
     * @throws MochiException if the tasks cannot be written
     */
    public void save(TaskList tasks) throws MochiException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileString());
        }
        Path temporaryFile = null;
        try {
            Path destination = filePath.toAbsolutePath();
            Path parent = destination.getParent();
            Files.createDirectories(parent);
            if (Files.isDirectory(destination)) {
                throw new IOException("The task file path points to a folder.");
            }
            // Finish writing before replacing the previous save, avoiding a half-written task file.
            temporaryFile = Files.createTempFile(parent, "mochi-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, destination,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            throw new MochiException("I couldn't save your tasks: " + e.getMessage());
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException e) {
                    // A leftover temporary file must not hide the original save failure.
                }
            }
        }
    }

    /**
     * Converts one line of stored data into a task.
     *
     * @param line line read from the data file
     * @param lineNumber one-based line number used for error reporting
     * @return restored task
     * @throws MochiException if the line is malformed
     */
    private Task parseStoredTask(String line, int lineNumber) throws MochiException {
        String[] fields = line.split(FIELD_SEPARATOR_PATTERN, -1);
        try {
            Task task;
            switch (fields[0]) {
                case "T":
                    requireFieldCount(fields, 3);
                    task = new Todo(fields[2]);
                    break;
                case "D":
                    requireFieldCount(fields, 4);
                    task = new Deadline(fields[2], LocalDate.parse(fields[3]));
                    break;
                case "E":
                    requireFieldCount(fields, 5);
                    task = new Event(fields[2], fields[3], fields[4]);
                    break;
                default:
                    throw new IllegalArgumentException("unknown task type");
            }
            if (fields[1].equals("1")) {
                task.markAsDone();
            } else if (!fields[1].equals("0")) {
                throw new IllegalArgumentException("invalid task status");
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | DateTimeParseException e) {
            throw new MochiException("Saved task data is invalid at line " + lineNumber + ".");
        }
    }

    /**
     * Ensures a saved record has exactly the fields expected for its task type.
     *
     * @param fields fields parsed from a saved record
     * @param expectedCount expected number of fields
     */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount || containsBlankField(fields)) {
            throw new IllegalArgumentException("invalid field count");
        }
        for (String field : fields) {
            if (field.contains("|") || field.chars().anyMatch(Character::isISOControl)) {
                throw new IllegalArgumentException("unsafe field content");
            }
        }
    }

    private boolean containsBlankField(String[] fields) {
        for (String field : fields) {
            if (field.isBlank()) {
                return true;
            }
        }
        return false;
    }
}
