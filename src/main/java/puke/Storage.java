package puke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Persists tasks to and loads tasks from a local file. */
public class Storage {
    private final Path path;

    /**
     * Creates storage backed by the given file.
     *
     * @param fileName Storage file path.
     */
    public Storage(String fileName) {
        path = Path.of(fileName);
    }

    /** Loads all valid tasks from disk. */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(path)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(path)) {
                String[] fields = line.split("\\|", -1);
                if (fields.length < 3) {
                    continue;
                }
                try {
                    Task task = TaskFactory.fromStorageFields(fields);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (RuntimeException e) {
                    // Ignore malformed records and continue loading valid tasks.
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return tasks;
    }

    /**
     * Saves the first {@code count} tasks to disk.
     *
     * @param tasks Tasks to save.
     * @param count Number of tasks to save.
     */
    public boolean save(Task[] tasks, int count) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Task task = tasks[i];
                String done = task.getStatusIcon().equals("X") ? "1" : "0";
                if (task instanceof Deadline deadline) {
                    lines.add(String.join("|", "D", done, deadline.getDesc(), deadline.getDeadline()));
                } else if (task instanceof Event event) {
                    lines.add(String.join("|", "E", done, event.getDesc(), event.getFrom(), event.getTo()));
                } else {
                    lines.add(String.join("|", "T", done, task.getDesc()));
                }
            }
            Files.write(path, lines);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
