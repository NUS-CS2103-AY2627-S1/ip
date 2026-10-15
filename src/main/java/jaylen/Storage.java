package jaylen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes Jaylen tasks in a human-readable text file. */
public class Storage {
    private static final String STORAGE_PROPERTY = "jaylen.storage";
    private static final Path DEFAULT_PATH = Path.of("data", "jaylen.txt");

    private final Path filePath;

    /** Creates storage using the configured path or {@code ./data/jaylen.txt}. */
    public Storage() {
        String configuredPath = System.getProperty(STORAGE_PROPERTY);
        filePath = configuredPath == null || configuredPath.isBlank()
                ? DEFAULT_PATH
                : Path.of(configuredPath);
    }

    /**
     * Loads all saved tasks, returning an empty list when the file is absent.
     *
     * @return the saved tasks
     * @throws JaylenException if the file cannot be read or is malformed
     */
    public ArrayList<Task> load() {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            ArrayList<Task> tasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    tasks.add(parseTask(lines.get(i), i + 1));
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new JaylenException("I couldn't read the save file.");
        }
    }

    /**
     * Saves all tasks and creates the parent directory when necessary.
     *
     * @param tasks the tasks to save
     * @throws JaylenException if the file cannot be written
     */
    public void save(List<Task> tasks) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = tasks.stream()
                    .map(this::formatTask)
                    .toList();
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new JaylenException("I couldn't save your tasks.");
        }
    }

    private Task parseTask(String line, int lineNumber) {
        String[] fields = line.split(" \\| ", -1);
        try {
            Task task = createTask(fields, lineNumber);
            restoreCompletionStatus(task, fields[1]);
            return task;
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw malformedLine(lineNumber);
        }
    }

    private Task createTask(String[] fields, int lineNumber) {
        TaskType type = TaskType.fromCode(fields[0]);
        return switch (type) {
            case TODO -> {
                requireFieldCount(fields, 3, lineNumber);
                yield new Todo(fields[2]);
            }
            case DEADLINE -> {
                requireFieldCount(fields, 4, lineNumber);
                yield new Deadline(fields[2], fields[3]);
            }
            case EVENT -> {
                requireFieldCount(fields, 5, lineNumber);
                yield new Event(fields[2], fields[3], fields[4]);
            }
            case GENERIC -> throw new JaylenException("The save file contains an invalid task type.");
        };
    }

    private void restoreCompletionStatus(Task task, String status) {
        if ("1".equals(status)) {
            task.markAsDone();
        } else if (!"0".equals(status)) {
            throw new JaylenException("The save file contains an invalid completion status.");
        }
    }

    private void requireFieldCount(String[] fields, int expectedCount, int lineNumber) {
        if (fields.length != expectedCount || fields[2].isBlank()) {
            throw malformedLine(lineNumber);
        }
    }

    private JaylenException malformedLine(int lineNumber) {
        return new JaylenException("The save file is malformed on line " + lineNumber + ".");
    }

    private String formatTask(Task task) {
        StringBuilder line = new StringBuilder()
                .append(task.getType().getCode())
                .append(" | ")
                .append(task.isDone() ? "1" : "0")
                .append(" | ")
                .append(task.getDescription());
        if (task instanceof Deadline deadline) {
            String by = deadline.includesTime()
                    ? DateTimeParser.formatStorageDateTime(deadline.getBy())
                    : DateTimeParser.formatStorageDate(deadline.getBy().toLocalDate());
            line.append(" | ").append(by);
        } else if (task instanceof Event event) {
            line.append(" | ").append(DateTimeParser.formatStorageDateTime(event.getFrom()))
                    .append(" | ").append(DateTimeParser.formatStorageDateTime(event.getTo()));
        }
        return line.toString();
    }
}
