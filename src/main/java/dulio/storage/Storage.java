package dulio.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dulio.parser.Parser;
import dulio.task.Deadline;
import dulio.task.Event;
import dulio.task.RecurringTask;
import dulio.task.Task;

/**
 * Handles loading tasks from and saving tasks to the data file.
 */
public class Storage {
    private Path filePath;

    /**
     * Creates storage backed by the specified path.
     *
     * @param filePath The relative or absolute data-file path.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage file path must not be null";
        this.filePath = filePath;
    }

    /**
     * Returns valid task records, or an empty list if the file is absent or cannot be read.
     *
     * @return The loaded tasks.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return loadedTasks;
        }
        try {
            for (String line : Files.readAllLines(filePath)) {
                Task task = Parser.parseStoredTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return loadedTasks;
    }

    /**
     * Saves all task records, creating the parent directory when necessary.
     *
     * @param tasks The tasks to persist.
     * @throws IOException If the file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        assert tasks != null : "Task list to save must not be null";
        assert !tasks.contains(null) : "Task list to save must not contain null tasks";
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            String status = task.isDone() ? "1" : "0";
            if (task instanceof Deadline deadline) {
                lines.add("D | " + status + " | " + deadline.getDescription() + " | " + deadline.getBy());
            } else if (task instanceof Event event) {
                lines.add("E | " + status + " | " + event.getDescription() + " | "
                    + event.getFrom() + " | " + event.getTo());
            } else if (task instanceof RecurringTask recurringTask) {
                lines.add("R | " + status + " | " + recurringTask.getDescription() + " | "
                    + recurringTask.getInterval());
            } else {
                lines.add("T | " + status + " | " + task.getDescription());
            }
        }
        Files.write(filePath, lines);
    }
}
