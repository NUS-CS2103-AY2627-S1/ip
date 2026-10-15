package dave.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import dave.exception.DaveCommandException;
import dave.parser.DateTimeParser;
import dave.parser.ParsedDateTime;
import dave.task.Deadline;
import dave.task.Event;
import dave.task.Task;
import dave.task.Todo;

/**
 * Manages persistent storage of tasks in a file on disk.
 * Handles loading tasks from and saving tasks to the specified file path.
 */
public class Storage {

    /** Path to the file where tasks are stored. */
    private final Path filePath;
    /** Count of corrupted or unparseable lines encountered during the last load operation. */
    private int corruptedLineCount;

    /**
     * Constructs a new Storage instance with the specified file path.
     *
     * @param filePath Relative or absolute path to the data file.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.trim().isEmpty() : "File path cannot be null or empty";
        this.filePath = Paths.get(filePath);
        this.corruptedLineCount = 0;
    }

    /**
     * Returns the number of corrupted lines encountered during the last load operation.
     *
     * @return Number of unparseable lines skipped during load.
     */
    public int getCorruptedLineCount() {
        return this.corruptedLineCount;
    }

    /**
     * Loads tasks from the persistent storage file.
     *
     * @return List of tasks loaded from the file, or an empty list if the file does not exist.
     * @throws DaveCommandException If an I/O error or security access denial occurs while reading.
     */
    public ArrayList<Task> load() throws DaveCommandException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        this.corruptedLineCount = 0;

        if (!Files.exists(this.filePath)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(this.filePath);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = parseLineToTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                } else {
                    this.corruptedLineCount++;
                }
            }
        } catch (IOException | SecurityException e) {
            throw new DaveCommandException("Unable to load tasks from disk: " + e.getMessage());
        }

        return loadedTasks;
    }

    /**
     * Parses a single serialized line from the data file into a Task object.
     * Returns null if the line cannot be parsed or contains unrecognized task data.
     *
     * @param line Serialized task line from the storage file.
     * @return Task parsed from line, or null if line is unparseable or corrupted.
     */
    private Task parseLineToTask(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");

        try {
            Task task = createTaskFromParts(type, parts);
            if (task != null) {
                task.setDone(isDone);
            }
            return task;
        } catch (DaveCommandException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Instantiates the appropriate Task subtype from split line parts.
     *
     * @param type Task type marker ("T", "D", or "E").
     * @param parts Split parts of the serialized task line.
     * @return Task instance, or null if type is unrecognized.
     * @throws DaveCommandException If date parsing fails for deadline or event.
     */
    private Task createTaskFromParts(String type, String[] parts) throws DaveCommandException {
        switch (type) {
            case "T":
                return new Todo(parts[2]);
            case "D":
                ParsedDateTime deadlineBy = DateTimeParser.parse(parts[3]);
                return new Deadline(parts[2], deadlineBy.getDateTime(), deadlineBy.hasTime());
            case "E":
                ParsedDateTime eventFrom = DateTimeParser.parse(parts[3]);
                ParsedDateTime eventTo = DateTimeParser.parse(parts[4]);
                return new Event(parts[2],
                        eventFrom.getDateTime(), eventFrom.hasTime(),
                        eventTo.getDateTime(), eventTo.hasTime());
            default:
                return null;
        }
    }

    /**
     * Saves the provided list of tasks to the storage file.
     *
     * @param tasks List of tasks to save to disk.
     * @throws DaveCommandException If an I/O error or security access denial occurs while writing.
     */
    public void save(List<Task> tasks) throws DaveCommandException {
        assert tasks != null : "Task list to save cannot be null";
        try {
            if (this.filePath.getParent() != null && !Files.exists(this.filePath.getParent())) {
                Files.createDirectories(this.filePath.getParent());
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileFormat());
            }

            Files.write(this.filePath, lines);
        } catch (IOException | SecurityException e) {
            throw new DaveCommandException("Unable to save tasks to disk: " + e.getMessage());
        }
    }
}
