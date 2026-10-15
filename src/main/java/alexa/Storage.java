package alexa;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Loads tasks from and saves tasks to a platform-independent data file. */
public class Storage {
    /** The relative path of the file containing saved tasks. */
    private final Path dataFile;

    /**
     * Creates storage that uses the supplied data-file path.
     *
     * @param dataFile the location of the task data file
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads every saved task, or returns an empty list when the data file does not exist yet.
     *
     * @return The loaded tasks in their saved order.
     * @throws IOException If the data file cannot be read or contains invalid task data.
     */
    public List<Task> load() throws IOException {
        if (Files.notExists(dataFile)) {
            return List.of();
        }
        List<Task> tasks = new ArrayList<>();
        for (String taskLine : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            tasks.add(parseTask(taskLine));
        }
        return tasks;
    }

    /**
     * Writes every task to the data file, creating its parent directory when needed.
     *
     * @param tasks The tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentDirectory = dataFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        List<String> taskLines = tasks.stream().map(Task::toStorageString).toList();
        Files.write(dataFile, taskLines, StandardCharsets.UTF_8);
    }

    /** Recreates one task from its saved text representation. */
    private Task parseTask(String taskLine) throws IOException {
        String[] fields = taskLine.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new IOException("Invalid task data: " + taskLine);
        }
        Task task = switch (fields[0]) {
            case "T" -> createTodo(fields, taskLine);
            case "D" -> createDeadline(fields, taskLine);
            case "E" -> createEvent(fields, taskLine);
            default -> throw new IOException("Unknown task type: " + fields[0]);
        };
        restoreStatus(task, fields[1], taskLine);
        return task;
    }

    /** Recreates a to-do task from saved fields. */
    private Todo createTodo(String[] fields, String taskLine) throws IOException {
        requireFieldCount(fields, 3, taskLine);
        return new Todo(fields[2]);
    }

    /** Recreates a deadline task from saved fields. */
    private Deadline createDeadline(String[] fields, String taskLine) throws IOException {
        requireFieldCount(fields, 4, taskLine);
        return new Deadline(fields[2], parseDate(fields[3], taskLine));
    }

    /** Recreates an event task from saved fields. */
    private Event createEvent(String[] fields, String taskLine) throws IOException {
        requireFieldCount(fields, 5, taskLine);
        LocalDate startDate = parseDate(fields[3], taskLine);
        LocalDate endDate = parseDate(fields[4], taskLine);
        if (!startDate.isBefore(endDate)) {
            throw new IOException("Invalid event date range: " + taskLine);
        }
        return new Event(fields[2], startDate, endDate);
    }

    /** Restores a task's saved completion status. */
    private void restoreStatus(Task task, String status, String taskLine) throws IOException {
        if (status.equals("1")) {
            task.markAsDone();
        } else if (!status.equals("0")) {
            throw new IOException("Invalid task status: " + taskLine);
        }
    }

    /** Ensures a saved task has exactly the expected number of fields. */
    private void requireFieldCount(String[] fields, int expectedCount, String taskLine) throws IOException {
        if (fields.length != expectedCount) {
            throw new IOException("Invalid task data: " + taskLine);
        }
    }

    /** Parses one ISO date from a saved task. */
    private LocalDate parseDate(String dateText, String taskLine) throws IOException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new IOException("Invalid task date: " + taskLine, exception);
        }
    }
}