package wiz;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Handles loading tasks from and saving tasks to a persistent storage file.
 */
public class Storage {
    private static final DateTimeFormatter FILE_DATE_FORMAT = new DateTimeFormatterBuilder()
            .parseStrict()
            .appendPattern("uuuu-MM-dd HHmm")
            .toFormatter(Locale.US)
            .withResolverStyle(ResolverStyle.STRICT);

    private final String filePath;

    /**
     * Constructs a Storage instance with the given file path.
     *
     * @param filePath The path of the file to store tasks in.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "File path cannot be null or blank";
        this.filePath = filePath;
    }

    /**
     * Saves the provided list of tasks to the storage file.
     *
     * @param tasks The list of tasks to save.
     * @throws IOException If writing to the file fails.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        assert tasks != null : "Task list to save cannot be null";
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            boolean isCreated = parent.mkdirs();
            assert isCreated || parent.exists() : "Parent directories must exist";
        }

        List<String> lines = tasks.stream()
                .map(Task::toFileString)
                .collect(Collectors.toList());

        Files.write(file.toPath(), lines);
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return An ArrayList of tasks loaded from storage.
     * @throws IOException If reading or parsing the file fails.
     */
    public ArrayList<Task> load() throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<String> lines = Files.readAllLines(path);
        ArrayList<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            if (!line.isBlank()) {
                tasks.add(parseTask(line));
            }
        }
        return tasks;
    }

    /**
     * Parses a single line from the data file into a Task object.
     *
     * @param line The serialized task string.
     * @return The parsed Task instance.
     * @throws IOException If the task format or date format is invalid.
     */
    public Task parseTask(String line) throws IOException {
        assert line != null : "Line to parse cannot be null";
        String[] parts = line.split(" \\| ");

        if (parts.length < 3) {
            throw new IOException("Invalid data file format.");
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        try {
            switch (type) {
            case "T":
                task = new ToDo(description);
                break;
            case "D":
                if (parts.length < 4) {
                    throw new IOException("Invalid deadline format in data file.");
                }
                LocalDateTime by = LocalDateTime.parse(parts[3], FILE_DATE_FORMAT);
                task = new Deadline(description, by);
                break;
            case "E":
                if (parts.length < 5) {
                    throw new IOException("Invalid event format in data file.");
                }
                LocalDateTime from = LocalDateTime.parse(parts[3], FILE_DATE_FORMAT);
                LocalDateTime to = LocalDateTime.parse(parts[4], FILE_DATE_FORMAT);
                if (to.isBefore(from)) {
                    throw new IOException("Invalid event dates in data file: end time is before start time.");
                }
                task = new Event(description, from, to);
                break;
            default:
                throw new IOException("Unknown task type: " + type);
            }
        } catch (DateTimeParseException e) {
            throw new IOException("Invalid date format in data file.");
        }

        if (isDone) {
            task.markAsDone();
        }

        assert task != null : "Parsed task should not be null";
        return task;
    }
}