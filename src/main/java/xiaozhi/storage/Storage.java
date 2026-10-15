package xiaozhi.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.task.Deadline;
import xiaozhi.task.Event;
import xiaozhi.task.Task;
import xiaozhi.task.Todo;
import xiaozhi.util.Dates;

/**
 * Handles loading tasks from, and saving tasks to, a file on the hard disk.
 * <p>
 * Tasks are stored as plain text, one task per line, in the format
 * {@code type | isDone | description | ...extra fields}, e.g.
 * {@code D | 0 | return book | June 6th}. The storage file and its parent
 * folder are created automatically the first time tasks are saved, so a
 * missing file or folder on a fresh machine is not treated as an error.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a Storage that reads from and writes to the given file path.
     *
     * @param filePath Relative path (e.g. "./data/xiaozhi.txt") to the save file.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "Save file path should never be null or blank.";
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads the saved tasks from disk.
     * Returns an empty list if the save file does not exist yet (e.g. on
     * the very first run on a new machine) or if it could not be read.
     *
     * @return The list of tasks read from the save file.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseTask(line));
                } catch (XiaoZhiException e) {
                    System.out.println("Skipping corrupted line in save file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read the save file. Starting with an empty task list.");
        }
        return tasks;
    }

    /**
     * Saves the given tasks to disk, overwriting any previous save file.
     * Creates the parent folder first if it does not already exist.
     *
     * @param tasks The current list of tasks to save.
     */
    public void save(ArrayList<Task> tasks) {
        assert tasks != null : "Task list to save should never be null; XiaoZhi always passes tasks.asList().";
        try {
            Path parentDir = filePath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.toSaveFormat()).append(System.lineSeparator());
            }
            Files.writeString(filePath, content.toString());
        } catch (IOException e) {
            System.out.println("Could not save tasks to disk: " + e.getMessage());
        }
    }

    /**
     * Parses one line of the save file back into a Task.
     *
     * @param line A single line from the save file.
     * @return The Task the line represents.
     * @throws XiaoZhiException If the line is missing fields or has an unknown type.
     */
    private Task parseTask(String line) throws XiaoZhiException {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new XiaoZhiException("Line has too few fields.");
        }

        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String description = parts[2].trim();

        Task task = switch (type) {
            case "T" -> new Todo(description);
            case "D" -> {
                if (parts.length < 4) {
                    throw new XiaoZhiException("Deadline is missing its /by field.");
                }
                yield new Deadline(description, Dates.parse(parts[3].trim()));
            }
            case "E" -> {
                if (parts.length < 5) {
                    throw new XiaoZhiException("Event is missing its /from or /to field.");
                }
                yield new Event(description, Dates.parse(parts[3].trim()), Dates.parse(parts[4].trim()));
            }
            default -> throw new XiaoZhiException("Unknown task type: " + type);
        };
        assert task != null : "Every branch of the switch above either yields a Task or throws.";

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
