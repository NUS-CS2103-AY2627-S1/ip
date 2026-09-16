package alice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles the loading and saving of tasks to a file on the hard disk.
 * The data is stored in a pipe-separated (|) format for easy parsing.
 */
public class Storage {
    private final String filePath;

    /**
     * Constructs a Storage instance with the specified file path.
     *
     * @param filePath The path where the task data will be read from and written to.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given list of tasks to the storage file.
     * Creates the necessary directories if they do not exist.
     *
     * @param tasks The list of tasks to save.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        assert tasks != null : "Tasks to save cannot be null";
        Path directory = Paths.get(filePath).getParent();
        if (directory != null && !Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        try (FileWriter fw = new FileWriter(filePath)) {
            for (Task task : tasks) {
                fw.write(taskToFileFormat(task) + System.lineSeparator());
            }
        }
    }

    /**
     * Loads the task list from the storage file.
     * If the file does not exist, an empty list is returned.
     *
     * @return An ArrayList of Tasks loaded from the file.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            Task task = fileFormatToTask(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Converts a Task object into its file format representation.
     *
     * @param task The task to convert.
     * @return A pipe-separated string representing the task.
     */
    private String taskToFileFormat(Task task) {
        assert task != null : "Task to serialize cannot be null";
        String type = "";
        String isDone = Objects.equals(task.getStatusIcon(), "X") ? "1" : "0";
        String description = task.getDescription();

        if (task instanceof ToDos) {
            type = "T";
            return type + " | " + isDone + " | " + description;
        } else if (task instanceof Deadlines) {
            type = "D";
            Deadlines d = (Deadlines) task;
            return type + " | " + isDone + " | " + description + " | " + d.getByForFile();
        } else if (task instanceof Events) {
            type = "E";
            Events e = (Events) task;
            return type + " | " + isDone + " | " + description + " | " + e.getFromForFile() + " | " + e.getToForFile();
        }
        return "";
    }

    /**
     * Converts a file format line back into a Task object.
     *
     * @param line The line of text read from the file.
     * @return The reconstructed Task, or {@code null} if the line format is corrupted or invalid.
     */
    private Task fileFormatToTask(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task = null;

        try {
            switch (type) {
                case "T":
                    task = new ToDos(description);
                    break;
                case "D":
                    if (parts.length < 4) {
                        return null;
                    }
                    LocalDate by = LocalDate.parse(parts[3]); // yyyy-MM-dd
                    task = new Deadlines(description, by);
                    break;
                case "E":
                    if (parts.length < 5) {
                        return null;
                    }
                    LocalDate from = LocalDate.parse(parts[3]);
                    LocalDate to = LocalDate.parse(parts[4]);
                    task = new Events(description, from, to);
                    break;
                default:
                    return null;
            }
        } catch (Exception e) {
            // If date parsing fails, return null (corrupted data)
            return null;
        }

        assert task != null : "A valid task record must produce a task";
        if (isDone) {
            task.toggleStatus();
        }
        return task;
    }
}
