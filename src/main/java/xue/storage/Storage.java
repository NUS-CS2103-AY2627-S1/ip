package xue.storage;

import xue.XueException;
import xue.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Saves the current task list in a simple, consistent text format. */
public class Storage {
    /** Location of the file used to persist tasks between runs. */
    private final Path filePath;
    /** Warning from the most recent load when one or more saved records were invalid. */
    private String loadWarning;

    /** Creates storage using the standard data file. */
    public Storage() {
        this(Path.of("data", "duke.txt"));
    }

    /** Creates storage using a custom file for isolated tests. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /** Loads valid saved tasks. Malformed records are ignored so one bad line does not
     * prevent the remaining tasks from being restored. */
    public List<Task> load() {
        loadWarning = null;
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                Task task;
                try {
                    task = parseLine(line);
                } catch (XueException e) {
                    task = null;
                }
                if (task != null) {
                    tasks.add(task);
                } else if (!line.isBlank()) {
                    loadWarning = "Some saved tasks were invalid and were skipped.";
                }
            }
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new XueException("I could not read your saved tasks.");
        }
    }

    /** Returns a warning for skipped records, or null when the most recent load had none. */
    public String getLoadWarning() {
        return loadWarning;
    }

    /** Converts one storage record into a task, or returns null for an invalid record. */
    private Task parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3 || fields[1].length() != 1
                || !(fields[0].equals("T") || fields[0].equals("D") || fields[0].equals("E"))
                || !(fields[1].equals("0") || fields[1].equals("1"))
                || fields[2].trim().isEmpty()) {
            return null;
        }
        if (fields[0].equals("T") && fields.length != 3) {
            return null;
        }
        if (fields[0].equals("D") && (fields.length != 4 || fields[3].trim().isEmpty())) {
            return null;
        }
        if (fields[0].equals("E") && (fields.length != 5
                || fields[3].trim().isEmpty() || fields[4].trim().isEmpty())) {
            return null;
        }
        boolean isDone = fields[1].equals("1");
        String from = fields[0].equals("E") ? fields[3].trim() : null;
        String to = fields[0].equals("T") ? null : fields[fields[0].equals("E") ? 4 : 3].trim();
        return new Task(fields[0], fields[2].trim(), from, to, isDone);
    }

    /** Writes all current tasks to disk, creating the data folder if needed. */
    public void save(Task[] tasks, int taskCount) {
        // The caller owns exactly taskCount entries; iterating beyond it would serialize stale data.
        assert tasks != null;
        assert taskCount >= 0 && taskCount <= tasks.length;
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                Task task = tasks[i];
                String line = task.getType() + " | " + (task.isDone() ? "1" : "0")
                        + " | " + task.getDescription();
                if ("D".equals(task.getType())) {
                    line += " | " + task.getTo();
                } else if ("E".equals(task.getType())) {
                    line += " | " + task.getFrom() + " | " + task.getTo();
                }
                lines.add(line);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new XueException("I could not save your tasks.");
        }
    }

    /** Writes a managed task list to disk. */
    public void save(List<Task> tasks) {
        save(tasks.toArray(new Task[0]), tasks.size());
    }
}
