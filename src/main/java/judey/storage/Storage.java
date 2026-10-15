package judey.storage;

import judey.task.Task;
import judey.task.Todo;
import judey.exception.JudeyException;
import judey.task.Deadline;
import judey.task.Event;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Handles reading from and writing task data to a storage file on disk. */
public class Storage {
    private static final String NOT_DONE_STATUS = "0";
    private static final String DONE_STATUS = "1";
    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the disk storage file.
     *
     * @return list of loaded tasks
     * @throws JudeyException if the file cannot be read
     */
    public List<Task> load() throws JudeyException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    Task task = parseTaskLine(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (JudeyException e) {
                    System.out.println("Warning: Skipping corrupted save line: " + line);
                }
            }
        } catch (IOException e) {
            throw new JudeyException("Could not read save file at " + filePath);
        }
        return tasks;
    }

    /**
     * Parses a single formatted string line from the storage file into a corresponding Task object.
     *
     * @param line The string line read from the save file.
     * @return The reconstructed {@code Task} instance with its saved completion status.
     * @throws JudeyException If the line format is malformed, missing required task details,
     *                        or contains an unrecognized task type.
     */
    private Task parseTaskLine(String line) throws JudeyException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new JudeyException("Invalid line format.");
        }

        String type = parts[0];
        String status = parts[1];
        if (!status.equals(NOT_DONE_STATUS) && !status.equals(DONE_STATUS)) {
            throw new JudeyException("Invalid task status.");
        }
        boolean isDone = status.equals(DONE_STATUS);
        String description = parts[2];

        Task task;
        switch (type) {
            case Task.TYPE_TODO:
                task = new Todo(description);
                break;
            case Task.TYPE_DEADLINE:
                if (parts.length < 4) {
                    throw new JudeyException("Missing due date for deadline.");
                }
                task = new Deadline(description, parts[3]);
                break;
            case Task.TYPE_EVENT:
                if (parts.length < 5) {
                    throw new JudeyException("Missing time details for event.");
                }
                task = new Event(description, parts[3], parts[4]);
                break;
            default:
                throw new JudeyException("Unknown task type.");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Saves the given task list to disk.
     *
     * @param tasks list of tasks to save
     * @throws JudeyException if writing to the file fails
     */
    public void save(List<Task> tasks) throws JudeyException {
        List<String> taskRecords = new ArrayList<>();
        for (Task task : tasks) {
            taskRecords.add(task.toFileString());
        }

        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            Files.write(filePath, taskRecords, StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new JudeyException("I couldn't save your tasks to " + filePath + ".");
        }
    }
}
