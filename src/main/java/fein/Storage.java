package fein;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import fein.task.Deadline;
import fein.task.Event;
import fein.task.Task;
import fein.task.TaskList;
import fein.task.Todo;

/** Loads and saves Fein's task list using a text file. */
public class Storage {
    /** The file used for this Fein instance's saved task list. */
    private final Path taskFile;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        // Every Fein instance needs a concrete file location for persistence.
        assert filePath != null && !filePath.isBlank() : "Storage must have a non-blank file path";
        taskFile = Path.of(filePath);
    }

    /** Loads valid saved tasks, treating a missing file as an empty list. */
    public List<Task> load() throws FeinException {
        try {
            if (!Files.exists(taskFile)) {
                return new ArrayList<>();
            }
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(taskFile, StandardCharsets.UTF_8)) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new FeinException("Fein couldn't load your saved tasks. Starting with an empty list.");
        }
    }

    /** Replaces the saved task list with the tasks currently in memory. */
    public void save(TaskList tasks) throws FeinException {
        // Saving null would violate the storage contract and cannot represent task state.
        assert tasks != null : "A task list must be present before saving";
        try {
            Path parent = taskFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            StringBuilder savedTasks = new StringBuilder();
            for (Task task : tasks.asList()) {
                savedTasks.append(formatTask(task)).append(System.lineSeparator());
            }
            Files.writeString(taskFile, savedTasks.toString(), StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new FeinException("Fein couldn't save your changes. Please try again.");
        }
    }

    /** Converts one task into the simple line format used by the save file. */
    private String formatTask(Task task) {
        // TaskList guarantees that its defensive copy contains only actual tasks.
        assert task != null : "A persisted task entry must not be null";
        String type;
        String details = task.getDescription();
        if (task instanceof Deadline deadline) {
            type = "D";
            details += " | " + deadline.getByText();
        } else if (task instanceof Event event) {
            type = "E";
            details += " | " + event.getFrom() + " | " + event.getTo();
        } else {
            type = "T";
        }
        return type + " | " + (task.isDone() ? "1" : "0") + " | " + details;
    }

    /** Converts one saved line into a task, or returns null for a malformed line. */
    private Task parseTask(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            return null;
        }
        String description = fields[2].trim();
        if (description.isEmpty()) {
            return null;
        }
        Task task;
        if (fields[0].equals("T") && fields.length == 3) {
            task = new Todo(description);
        } else if (fields[0].equals("D") && fields.length == 4 && !fields[3].isBlank()) {
            task = new Deadline(description, fields[3].trim());
        } else if (fields[0].equals("E") && fields.length == 5
                && !fields[3].isBlank() && !fields[4].isBlank()) {
            task = new Event(description, fields[3].trim(), fields[4].trim());
        } else {
            return null;
        }
        // A structurally valid record must have produced one of Fein's task types.
        assert task != null : "A valid saved record must decode into a task";
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
