package lebron.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lebron.task.Deadline;
import lebron.task.Event;
import lebron.task.RecurrenceInterval;
import lebron.task.RecurringTask;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.task.Todo;

/**
 * Class responsible for saving and loading a TaskList to and from disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Constructs a Storage that reads/writes the default save location, {@code data/LeBron.txt}.
     */
    public Storage() {
        this(Paths.get("data", "LeBron.txt"));
    }

    /**
     * Constructs a Storage that reads/writes the given file path.
     * Mainly useful for testing against a temporary file instead of the real save data.
     *
     * @param filePath The file to save to and load from.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given TaskList to disk.
     *
     * @param tasks TaskList to be saved.
     * @throws IOException if the save directory or file cannot be written to.
     */
    public void save(TaskList tasks) throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, tasks.reformat());
    }

    /**
     * Loads a TaskList from disk. If no save file exists yet (e.g. first run),
     * an empty TaskList is returned.
     *
     * @return The loaded TaskList.
     * @throws IOException if the save file exists but cannot be read, or its content is
     *         corrupted/unrecognisable (e.g. hand-edited into an invalid state).
     */
    public TaskList load() throws IOException {
        if (!Files.exists(filePath)) {
            return new TaskList(new ArrayList<>());
        }

        List<String> lines = Files.readAllLines(filePath);
        try {
            ArrayList<Task> loadedTasks = lines.stream()
                    .filter(line -> !line.isBlank())
                    .map(this::parseTask)
                    .collect(Collectors.toCollection(ArrayList::new));
            return new TaskList(loadedTasks);
        } catch (RuntimeException | AssertionError e) {
            // parseTask only throws unchecked exceptions (IllegalArgumentException for an
            // unknown type, DateTimeParseException for a bad date, ArrayIndexOutOfBoundsException
            // for a line missing fields, or AssertionError from its own assertions when
            // assertions are enabled, e.g. under `gradlew test`). Translating them into
            // IOException here means the caller (LeBron's constructor) only needs its
            // existing IOException handling to
            // fall back to an empty TaskList, instead of a corrupted save file crashing the
            // whole app at startup.
            throw new IOException("Save file is corrupted: " + e.getMessage(), e);
        }
    }

    /**
     * Parses a single save-format line (as written by {@link Task#reformat()})
     * back into the corresponding Task subclass.
     *
     * @param line The save-format line, e.g. "D | 0 | return book | June 6th".
     * @return The reconstructed Task.
     */
    private Task parseTask(String line) {
        String[] parts = line.split(" \\| ");
        assert parts.length >= 3
                : "Every line should have been written by Task#reformat(), so it should have at least "
                + "type | done-status | description";
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        switch (type) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                assert parts.length >= 4 : "A Deadline line should also carry its due date/time";
                task = new Deadline(description, LocalDateTime.parse(parts[3]));
                break;
            case "E":
                assert parts.length >= 5 : "An Event line should also carry its start and end date/time";
                task = new Event(description, LocalDateTime.parse(parts[3]),
                        LocalDateTime.parse(parts[4]));
                break;
            case "R":
                assert parts.length >= 5 : "A RecurringTask line should also carry its due date/time and interval";
                task = new RecurringTask(description, LocalDateTime.parse(parts[3]),
                        RecurrenceInterval.valueOf(parts[4]));
                break;
            default:
                throw new IllegalArgumentException("Unknown task type in save file: " + line);
        }

        task.setStatus(isDone);
        return task;
    }
}
