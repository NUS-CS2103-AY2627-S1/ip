package anniechat.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import anniechat.task.Deadline;
import anniechat.task.Event;
import anniechat.task.Task;
import anniechat.task.ToDo;

/** Manages saving tasks to and loading tasks from the data file. */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager for the specified file path.
     *
     * @param filePath path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves every task as one line in the data file.
     *
     * @param tasks tasks to save.
     * @throws IOException if the data file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            lines.add(task.toSaveFormat());
        }

        Files.write(filePath, lines);
    }

    /**
     * Loads tasks from the data file.
     *
     * @return tasks reconstructed from the saved lines.
     * @throws IOException if the data file cannot be read.
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath)) {
            if (!line.isBlank()) {
                tasks.add(createTask(line));
            }
        }

        return tasks;
    }

    /**
     * Reconstructs one task from its saved representation.
     *
     * @param line saved task representation.
     * @return reconstructed task.
     */
    private Task createTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        Task task = switch (parts[0]) {
        case "T" -> createToDo(parts, line);
        case "D" -> createDeadline(parts, line);
        case "E" -> createEvent(parts, line);
        default -> throw new IllegalArgumentException("Unknown task type: " + parts[0]);
        };

        if ("1".equals(parts[1])) {
            task.markDone();
        } else if (!"0".equals(parts[1])) {
            throw new IllegalArgumentException("Invalid task status: " + parts[1]);
        }

        return task;
    }

    /** Creates a to-do task after validating its saved representation. */
    private Task createToDo(String[] parts, String line) {
        validatePartCount(parts, 3, line);
        return new ToDo("todo " + parts[2]);
    }

    /** Creates a deadline task after validating its saved representation. */
    private Task createDeadline(String[] parts, String line) {
        validatePartCount(parts, 4, line);
        return new Deadline("deadline " + parts[2] + " /by " + parts[3]);
    }

    /** Creates an event task after validating its saved representation. */
    private Task createEvent(String[] parts, String line) {
        validatePartCount(parts, 5, line);
        return new Event("event " + parts[2] + " /from " + parts[3]
                + "/to " + parts[4]);
    }

    /** Ensures a saved task line has exactly the expected number of fields. */
    private void validatePartCount(String[] parts, int expectedCount, String line) {
        if (parts.length != expectedCount) {
            throw new IllegalArgumentException("Invalid task data: " + line);
        }
    }
}
