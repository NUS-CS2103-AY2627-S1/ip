package jasper.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jasper.JasperException;
import jasper.task.Deadline;
import jasper.task.Event;
import jasper.task.Task;
import jasper.task.TaskList;
import jasper.task.Todo;

/**
 * Handles the loading and saving of tasks to a persistent storage file.
 */
public class Storage {
    /** File path used for storage */
    private final Path path;

    /**
     * Constructs a Storage instance with the specified directory and filename.
     *
     * @param parent Parent directory path for the storage file.
     * @param filename Name of the storage file.
     */
    public Storage(String parent, String filename) {
        path = Paths.get(parent, filename);
    }

    /**
     * Loads tasks from the storage file into a list.
     *
     * @return List containing the loaded tasks.
     * @throws JasperException If an error occurs during reading or parsing the file.
     */
    public TaskList load() throws JasperException {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                return new TaskList();
            }
            // try-with stmt ensures stream's open file reference is closed after completion
            // any IOExceptions can be caught by outer catch
            try (Stream<String> lines = Files.lines(path)) {
                return new TaskList(lines
                        .map(String::trim)
                        .map(Storage::parseTaskFromLine)
                        .flatMap(Optional::stream)
                        .toList()
                );
            }
        } catch (IOException e) {
            throw new JasperException("Error loading saved tasks: " + e.getMessage());
        }
    }

    /**
     * Creates a backup savefile.
     *
     * @return Name of backup savefile.
     * @throws JasperException If an error occurs when creating backup savefile.
     */
    public String backup() throws JasperException {
        String backupName = path.getFileName() + ".bak";
        Path backupPath = path.resolveSibling(backupName);
        try {
            if (Files.exists(path)) {
                Files.move(path, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new JasperException("Failed to back up corrupted savefile: " + e.getMessage());
        }
        return backupName;
    }

    /**
     * Parses a string line from the save file into a Task object.
     *
     * @param line String representation of a task from the save file.
     * @return An Optional describing the parsed Task, or an empty Optional if the line is improperly formatted.
     */
    private static Optional<Task> parseTaskFromLine(String line) {
        assert line != null : "Invalid element from lines stream";
        if (line.isEmpty()) {
            return Optional.empty();
        }
        try {
            Task task = switch (line.charAt(0)) {
                case 'T' -> TodoSerializer.deserialize(line);
                case 'D' -> DeadlineSerializer.deserialize(line);
                case 'E' -> EventSerializer.deserialize(line);
                default -> throw new JasperException("Error reading savefile!");
            };
            return Optional.of(task);
        } catch (JasperException e) {
            return Optional.empty(); // skip lines in wrong format
        }
    }

    /**
     * Saves the current list of tasks to the storage file.
     *
     * @param tasks List of tasks to save.
     * @throws JasperException If an error occurs while writing to the file.
     */
    public void save(TaskList tasks) throws JasperException {
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = tasks.stream()
                    .map(Storage::parseLineFromTask)
                    .toList();
            Files.write(path, lines);
        } catch (IOException e) {
            throw new JasperException("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Parses a Task object into its corresponding string representation for saving.
     *
     * @param task Task to be serialized.
     * @return Serialized string representation of the task.
     * @throws IllegalStateException If support for the task type is not implemented.
     */
    private static String parseLineFromTask(Task task) {
        return switch (task) {
            case Todo t -> TodoSerializer.serialize(t);
            case Deadline d -> DeadlineSerializer.serialize(d);
            case Event e -> EventSerializer.serialize(e);
            default -> throw new IllegalStateException("Unsupported task type!");
        };
    }
}
