package cheecken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Loads and saves task records in the application's persistence file.
 */
public class Storage {
    private final Path file;
    private final Consumer<String> reportError;

    /**
     * Creates storage backed by the supplied relative or absolute path.
     * @param filePath path to the task persistence file
     */
    public Storage(String filePath) {
        this(filePath, System.out::println);
    }

    /**
     * Creates storage with an error destination shared by the console or GUI.
     * @param filePath path to the task persistence file
     * @param reportError destination for storage error messages
     */
    public Storage(String filePath, Consumer<String> reportError) {
        this.file = Path.of(filePath);
        this.reportError = reportError;
    }

    /**
     * Saves all tasks, reporting I/O failures to the user.
     * @param tasks tasks in display or storage order
     */
    public void save(List<Task> tasks) {
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            Files.write(file, tasks.stream().map(Task::toStorageString).toList());
        } catch (IOException | SecurityException e) {
            reportError.accept("Unable to save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads valid task records and skips malformed records.
     * @return valid stored tasks, or an empty list if no tasks can be loaded
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(file)) {
                Task task = parseRecord(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException | SecurityException e) {
            reportError.accept("Unable to load tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Restores one task and its completion state, or returns null for a malformed record.
     * @param line stored task record to parse
     * @return restored task, or null if the record is malformed
     */
    private Task parseRecord(String line) {
        try {
            String[] fields = line.split("\\s*\\|\\s*", -1);
            if (fields.length < 3) {
                return null;
            }
            boolean isValidStatus = fields[1].equals("0") || fields[1].equals("1");
            if (!isValidStatus) {
                return null;
            }
            Task task = switch (fields[0]) {
                case "T" -> new Todo(fields[2]);
                case "D" -> fields.length >= 4
                        ? new Deadline(fields[2], DateTimeValue.parseAbsolute(fields[3], "deadline")) : null;
                case "E" -> fields.length >= 5 ? new Event(fields[2],
                        DateTimeValue.parseAbsolute(fields[3], "event"),
                        DateTimeValue.parseAbsolute(fields[4], "event")) : null;
                default -> null;
            };
            if (task != null && fields[1].equals("1")) {
                task.mark();
            }
            return task;
        } catch (RuntimeException ignored) {
            // A malformed record must not prevent later valid records from loading.
            return null;
        }
    }
}
