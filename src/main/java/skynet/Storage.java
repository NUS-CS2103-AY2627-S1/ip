package skynet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading of a TaskList.
 */
public class Storage {
    private static final Path DEFAULT_SAVE_FILE_PATH = Path.of("data", "saveFile.txt");
    private static final String DELIMITER = " | ";
    private static final String DELIMITER_REGEX = " \\| ";
    private final Path saveFilePath;

    /**
     * Creates storage using the application's default save-file location.
     */
    public Storage() {
        this(DEFAULT_SAVE_FILE_PATH);
    }

    /**
     * Creates storage using a specified save-file location.
     *
     * @param saveFilePath path of the save file
     */
    Storage(Path saveFilePath) {
        if (saveFilePath == null) {
            throw new IllegalArgumentException("Save-file path cannot be null.");
        }
        this.saveFilePath = saveFilePath;
    }

    /**
     * Saves the chatbot's tasks to a file on disk.
     *
     * @param tasks the list of tasks
     * @throws IOException if the save file cannot be written
     */
    void save(List<Task> tasks) throws IOException {
        if (tasks == null) {
            throw new IllegalArgumentException("Task list passed to save cannot be null.");
        }

        List<String> lines = tasks.stream().map(this::convertTaskToLine).toList();

        if (saveFilePath.getParent() != null) {
            Files.createDirectories(saveFilePath.getParent());
        }
        Path temporaryPath = saveFilePath.resolveSibling(saveFilePath.getFileName() + ".tmp");
        Files.write(temporaryPath, lines);
        Files.move(temporaryPath, saveFilePath,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Loads the previously saved target list from disk.
     *
     * @return a list of saved tasks, or an empty list when no save file exists
     * @throws IOException if the save file cannot be read
     */
    List<Task> load() throws IOException {
        if (Files.notExists(saveFilePath)) {
            return List.of();
        }

        List<String> lines = Files.readAllLines(saveFilePath);
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(convertLineToTask(lines.get(i)));
            } catch (RuntimeException e) {
                throw new IOException("Save file contains invalid data on line " + (i + 1)
                        + ".", e);
            }
        }
        return tasks;
    }

    private String convertTaskToLine(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task should not be null during save.");
        }
        String status = task.isDone() ? "1" : "0";

        return switch (task) {
            case Todo todo -> "T" + DELIMITER + status + DELIMITER + todo.getDescription();
            case Deadline deadline -> "D" + DELIMITER + status + DELIMITER
                    + deadline.getDescription() + DELIMITER + deadline.getBy();
            case Event event -> "E" + DELIMITER + status + DELIMITER
                    + event.getDescription() + DELIMITER + event.getFrom()
                    + DELIMITER + event.getTo();
            default -> throw new IllegalArgumentException("Unknown target type");
        };
    }

    private Task convertLineToTask(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Save file contains a blank line.");
        }

        String[] parts = line.split(DELIMITER_REGEX, -1);
        if (parts.length < 2
                || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task status in save file.");
        }

        Task task = switch (parts[0]) {
            case "T" -> {
                requireFieldCount(parts, 3);
                yield new Todo(parts[2]);
            }
            case "D" -> {
                requireFieldCount(parts, 4);
                yield new Deadline(parts[2], LocalDateTime.parse(parts[3]));
            }
            case "E" -> {
                requireFieldCount(parts, 5);
                yield new Event(parts[2], LocalDateTime.parse(parts[3]),
                        LocalDateTime.parse(parts[4]));
            }
            default -> throw new IllegalArgumentException("Unknown task type in save file.");
        };

        if (parts[1].equals("1")) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Ensures that a serialized task has exactly the expected number of fields.
     *
     * @param parts serialized task fields
     * @param expectedCount expected number of fields
     */
    private void requireFieldCount(String[] parts, int expectedCount) {
        if (parts.length != expectedCount) {
            throw new IllegalArgumentException("Incorrect number of fields in save file.");
        }
    }
}
