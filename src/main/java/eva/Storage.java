package eva;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import eva.task.Deadline;
import eva.task.Event;
import eva.task.Task;
import eva.task.Todo;

/**
 * Loads tasks from and saves tasks to a local data file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that uses the specified file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @return Tasks loaded from the file, or an empty list if it does not exist.
     * @throws EvaException If the task data cannot be loaded.
     */
    public ArrayList<Task> load() throws EvaException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (Files.notExists(filePath)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    tasks.add(parseStoredTask(line));
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new EvaException(
                    "I could not load the saved tasks: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws EvaException If the tasks cannot be saved.
     */
    public void save(TaskList tasks) throws EvaException {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            ArrayList<String> lines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                lines.add(tasks.getTaskAt(i).toFileString());
            }

            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new EvaException(
                    "I could not save the tasks: " + e.getMessage());
        }
    }

    private Task parseStoredTask(String line) {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid task data.");
        }

        Task task;

        switch (parts[0]) {
            case "T":
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid todo data.");
                }
                task = new Todo(parts[2]);
                break;
            case "D":
                if (parts.length != 4) {
                    throw new IllegalArgumentException("Invalid deadline data.");
                }
                task = new Deadline(parts[2], parts[3]);
                break;
            case "E":
                if (parts.length != 5) {
                    throw new IllegalArgumentException("Invalid event data.");
                }
                task = new Event(parts[2], parts[3], parts[4]);
                break;
            default:
                throw new IllegalArgumentException("Unknown task type.");
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        } else if (!parts[1].equals("0")) {
            throw new IllegalArgumentException("Invalid task status.");
        }

        return task;
    }
}
