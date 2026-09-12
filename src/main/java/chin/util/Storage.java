package chin.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import chin.task.Deadline;
import chin.task.Event;
import chin.task.Task;
import chin.task.Todo;

/**
 * Reads and writes the persistent task list at a given file path. Each line
 * is a pipe-separated record produced by {@link Task#serialize()}.
 */
public class Storage {
    private final Path file;

    /** Creates a storage backed by the given file path. */
    public Storage(Path file) {
        assert file != null : "file path must not be null";
        this.file = file;
    }

    /** Loads tasks from disk, or returns an empty list if the file is absent. */
    public ArrayList<Task> load() {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(file).stream()
                    .map(this::deserialize)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println(" (warning: could not load saved tasks: " + e.getMessage() + ")");
            return new ArrayList<>();
        }
    }

    /** Overwrites the file with the current task list, one record per line. */
    public void save(List<Task> tasks) {
        assert tasks != null : "tasks must not be null";
        try {
            Files.createDirectories(file.getParent());
            try (PrintWriter pw = new PrintWriter(file.toFile())) {
                tasks.stream().map(Task::serialize).forEach(pw::println);
            }
        } catch (IOException e) {
            System.out.println(" (warning: could not save tasks: " + e.getMessage() + ")");
        }
    }

    private Task deserialize(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }
        boolean isDone = parts[1].equals("1");
        Task task;
        try {
            switch (parts[0]) {
            case "T":
                task = new Todo(parts[2]);
                break;
            case "D":
                if (parts.length < 4) {
                    return null;
                }
                task = new Deadline(parts[2], parts[3]);
                break;
            case "E":
                if (parts.length < 5) {
                    return null;
                }
                task = new Event(parts[2], parts[3], parts[4]);
                break;
            default:
                return null;
            }
        } catch (ChinException e) {
            return null;
        }
        if (isDone) {
            task.mark();
        }
        return task;
    }
}
