package duke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Loads tasks from and saves tasks to the chatbot's data file. */
public class Storage {

    private final Path file;

    /** Creates storage backed by the specified file. */
    public Storage(Path file) {
        this.file = file;
    }

    /** Loads all valid tasks, preserving their completion status. */
    public List<Task> load() throws IOException, DateTimeParseException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }
        for (String line : Files.readAllLines(file)) {
            String[] parts = line.split(" \\| ", -1);
            Task task = parseTask(parts);
            if (task != null && (parts[1].equals("0") || parts[1].equals("1"))) {
                if (parts[1].equals("1")) {
                    task.markAsDone();
                }
                tasks.add(task);
            }
        }
        return tasks;
    }

    /** Saves all tasks, creating the parent directory when necessary. */
    public void save(List<Task> tasks) throws IOException {
        assert tasks != null : "Tasks to save must not be null";
        Files.createDirectories(file.getParent());
        List<String> lines = tasks.stream()
                .map(Task::toSaveString)
                .collect(Collectors.toList());
        Files.write(file, lines);
    }

    /** Reconstructs a task from one serialized storage line. */
    private Task parseTask(String[] parts) {
        if (parts.length == 3 && parts[0].equals("T")) {
            return new Todo(parts[2]);
        }
        if (parts.length == 4 && parts[0].equals("D")) {
            return new Deadline(parts[2], LocalDateTime.parse(parts[3]));
        }
        if (parts.length == 4 && parts[0].equals("E")) {
            String[] times = parts[3].split(" -> ", 2);
            if (times.length == 2) {
                return new Event(parts[2], LocalDateTime.parse(times[0]),
                        LocalDateTime.parse(times[1]));
            }
        }
        if (parts.length == 4 && parts[0].equals("R")) {
            return new Reminder(parts[2], LocalDateTime.parse(parts[3]));
        }
        return null;
    }
}
