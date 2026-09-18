package vega;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Saves Vega tasks to, and loads them from, a text file. */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that uses the given relative or absolute file path.
     *
     * @param filePath Location of Vega's task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads saved tasks, or returns an empty list when no save file exists yet.
     *
     * @return Tasks reconstructed from the data file.
     * @throws VegaException If the data file cannot be read or contains invalid data.
     */
    public ArrayList<Task> loadTasks() throws VegaException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(parseTask(line));
                }
            }
            return tasks;
        } catch (IOException | IllegalArgumentException e) {
            throw new VegaException("I could not load your saved tasks.");
        }
    }

    /**
     * Saves all tasks, creating the data directory when necessary.
     *
     * @param tasks Tasks to write to the data file.
     * @throws VegaException If the task data cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws VegaException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new VegaException("I could not save your tasks.");
        }
    }

    /** Converts one saved line back into the appropriate kind of task. */
    private Task parseTask(String line) {
        String[] parts = line.split("\\t", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid saved task");
        }

        Task task;
        switch (parts[0]) {
            case "T":
                task = new Todo(parts[2]);
                break;
            case "D":
                if (parts.length != 4) {
                    throw new IllegalArgumentException("Invalid saved deadline");
                }
                task = new Deadline(parts[2], LocalDate.parse(parts[3]));
                break;
            case "E":
                if (parts.length != 5) {
                    throw new IllegalArgumentException("Invalid saved event");
                }
                task = new Event(parts[2], parts[3], parts[4]);
                break;
            default:
                throw new IllegalArgumentException("Unknown saved task type");
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        } else if (!parts[1].equals("0")) {
            throw new IllegalArgumentException("Invalid saved task status");
        }
        return task;
    }

    /** Converts a task into one tab-separated line for the save file. */
    private String formatTask(Task task) {
        assert task != null : "Task to save must not be null";
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return task.getType() + "\t" + status + "\t" + task.getDescription() + "\t" + deadline.getBy();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return task.getType() + "\t" + status + "\t" + task.getDescription()
                    + "\t" + event.getFrom() + "\t" + event.getTo();
        }
        return task.getType() + "\t" + status + "\t" + task.getDescription();
    }
}
