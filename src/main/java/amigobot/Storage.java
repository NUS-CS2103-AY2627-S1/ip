package amigobot;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import amigobot.task.Deadline;
import amigobot.task.Event;
import amigobot.task.Task;
import amigobot.task.TaskList;
import amigobot.task.Todo;

/**
 * Handles saving and loading tasks to/from a file on disk.
 * Uses a pipe-delimited format: TYPE | DONE | DESCRIPTION | ...extra fields.
 */
public class Storage {

    private final Path filePath;

    /**
     * Constructs a Storage that reads from and writes to the given file path.
     *
     * @param filePath the path to the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Saves all tasks to the file, creating the parent directory if needed.
     * Each task is written in pipe-delimited format, e.g.:
     *   T | 1 | read book
     *   D | 0 | return book | June 6th
     *   E | 0 | project meeting | Mon 2pm | 4pm
     */
    public void save(TaskList tasks) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(toFileFormat(tasks.getTask(i)) + System.lineSeparator());
            }
        }
    }

    /**
     * Loads tasks from the file. Returns an empty list if the file does not exist.
     * Skips corrupted or malformed lines with a warning instead of crashing.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        List<String> lines = Files.readAllLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            try {
                tasks.add(parseLine(line));
            } catch (IllegalArgumentException e) {
                System.out.println("Warning: skipped corrupted line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Parses a single pipe-delimited line into a Task.
     * Handles T (Todo), D (Deadline), and E (Event) types, and restores
     * the done status. Dates stored as ISO format are parsed back into
     * LocalDate; other strings are kept as plain text.
     *
     * @param line the pipe-delimited line to parse.
     * @return the reconstructed Task.
     * @throws IllegalArgumentException if the line is malformed or has an unknown type.
     */
    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new IllegalArgumentException("too few fields");
        }
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        Task task;
        switch (type) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                if (parts.length < 4) {
                    throw new IllegalArgumentException("deadline missing /by field");
                }
                try {
                    task = new Deadline(description, LocalDate.parse(parts[3]));
                } catch (Exception e) {
                    task = new Deadline(description, parts[3]);
                }
                break;
            case "E":
                if (parts.length < 5) {
                    throw new IllegalArgumentException("event missing /from or /to field");
                }
                LocalDate fromDate = null;
                String fromString = null;
                LocalDate toDate = null;
                String toString = null;
                try {
                    fromDate = LocalDate.parse(parts[3]);
                } catch (Exception e) {
                    fromString = parts[3];
                }
                try {
                    toDate = LocalDate.parse(parts[4]);
                } catch (Exception e) {
                    toString = parts[4];
                }
                task = new Event(description, fromDate, fromString, toDate, toString);
                break;
            default:
                throw new IllegalArgumentException("unknown task type '" + type + "'");
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts a Task into its pipe-delimited file format string.
     * Uses instanceof checks to determine the task type and serialize
     * the appropriate fields (dates as ISO format, strings as-is).
     *
     * @param task the task to convert.
     * @return the pipe-delimited string representation.
     */
    private String toFileFormat(Task task) {
        int done = task.isDone() ? 1 : 0;
        if (task instanceof Event) {
            Event e = (Event) task;
            String fromValue = e.getFromDate() != null ? e.getFromDate().toString() : e.getFromString();
            String toValue = e.getToDate() != null ? e.getToDate().toString() : e.getToString();
            return "E | " + done + " | " + e.getDescription() + " | " + fromValue + " | " + toValue;
        } else if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            String byValue = d.getByDate() != null ? d.getByDate().toString() : d.getByString();
            return "D | " + done + " | " + d.getDescription() + " | " + byValue;
        } else {
            return "T | " + done + " | " + task.getDescription();
        }
    }
}
