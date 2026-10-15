package nexus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Saves the current task list to a file on disk. */
public class Storage {
    private final Path filePath;

    /** Creates storage using a path relative to the project root. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /** Loads tasks from disk; an absent file represents an empty task list. */
    public List<Task> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try {
            List<Task> loaded = new ArrayList<>();
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    loaded.add(parseRecord(line));
                }
            }
            return loaded;
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println("OOPS!!! I couldn't read your saved tasks. Starting with an empty list.");
            return new ArrayList<>();
        }
    }

    /** Parses the current human-readable task format, rejecting corrupt records. */
    private Task parseRecord(String line) {
        if (line.startsWith("[T][ ] ") || line.startsWith("[T][X] ")) {
            Todo todo = new Todo(line.substring(7));
            if (line.startsWith("[T][X] ")) {
                todo.markAsDone();
            }
            return todo;
        }
        throw new IllegalArgumentException("Unrecognised task record.");
    }

    /**
     * Writes all tasks to disk, creating the parent directory and file when
     * they do not exist yet.
     */
    public void save(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks must not be null.");
        }
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            String content = tasks.stream()
                    .map(Task::toString)
                    .reduce((first, second) -> first + System.lineSeparator() + second)
                    .orElse("");
            Files.writeString(filePath, content);
        } catch (IOException | SecurityException exception) {
            throw new IllegalStateException(
                    "I couldn't save your tasks. Check file permissions and try again.", exception);
        }
    }
}
