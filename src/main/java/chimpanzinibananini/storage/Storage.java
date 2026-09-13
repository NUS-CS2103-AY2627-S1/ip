package chimpanzinibananini.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import chimpanzinibananini.task.Deadline;
import chimpanzinibananini.task.Event;
import chimpanzinibananini.task.Task;
import chimpanzinibananini.task.TaskList;
import chimpanzinibananini.task.Todo;

/**
 * Loads tasks from and saves tasks to a data file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager that uses the given file path.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Atomically replaces the data file with the current task list.
     */
    public void saveTasks(TaskList tasks) throws IOException {
        Path dataDirectory = filePath.toAbsolutePath().getParent();
        Files.createDirectories(dataDirectory);
        Path temporaryFile = Files.createTempFile(dataDirectory, "duke-", ".tmp");
        try {
            Files.write(temporaryFile, tasks.asList().stream().map(Task::toDataString).toList());
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads and validates tasks, or returns an empty list on first use.
     */
    public TaskList loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return new TaskList(tasks);
        }
        if (!Files.isRegularFile(filePath)) {
            throw new IOException(filePath + " is not a regular file");
        }

        List<String> lines = Files.readAllLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }
            try {
                tasks.add(parseStoredTask(lines.get(i)));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                throw new IOException("invalid data on line " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        return new TaskList(tasks);
    }

    /** Reconstructs and validates one task from a saved line. */
    private Task parseStoredTask(String line) {
        List<String> fields = splitStoredFields(line);
        validateStoredFields(fields);

        Task task = switch (fields.get(0)) {
            case "T" -> new Todo(fields.get(2));
            case "D" -> new Deadline(fields.get(2), fields.get(3));
            case "E" -> new Event(fields.get(2), fields.get(3), fields.get(4));
            default -> throw new AssertionError("Task type was already validated");
        };
        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Validates the stored task's type, field count, completion status, and required values.
     */
    private void validateStoredFields(List<String> fields) {
        if (fields.size() < 3) {
            throw new IllegalArgumentException("not enough fields");
        }
        int expectedFields = switch (fields.get(0)) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("unknown task type '" + fields.get(0) + "'");
        };
        if (fields.size() != expectedFields) {
            throw new IllegalArgumentException("wrong number of fields for task type " + fields.get(0));
        }
        if (!fields.get(1).equals("0") && !fields.get(1).equals("1")) {
            throw new IllegalArgumentException("completion status must be 0 or 1");
        }
        if (fields.stream().skip(2).anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("task fields cannot be blank");
        }
    }

    /** Splits a stored line while decoding escaped separators and control characters. */
    private List<String> splitStoredFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\\') {
                if (++i >= line.length()) {
                    throw new IllegalArgumentException("unfinished escape sequence");
                }
                char escaped = line.charAt(i);
                current.append(escaped == 'n' ? '\n' : escaped == 'r' ? '\r' : escaped);
            } else if (line.startsWith(" | ", i)) {
                fields.add(current.toString());
                current.setLength(0);
                i += 2;
            } else {
                current.append(line.charAt(i));
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
