package alpha.storage;

import alpha.task.Deadline;
import alpha.task.Event;
import alpha.task.Task;
import alpha.task.TaskList;
import alpha.task.Todo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/** Handles loading tasks from disk and saving tasks to disk. */
public class Storage {
    private final Path filePath;

    /** Creates storage using a relative or absolute path supplied by the caller. */
    public Storage(String filePath) {
        this(Path.of(filePath));
    }

    /** Creates storage using the supplied path. */
    public Storage(Path filePath) {
        assert filePath != null : "Storage must have a file path";
        this.filePath = filePath;
    }

    /** Loads all valid task records, returning an empty list when the file does not exist. */
    public List<Task> load() throws StorageException {
        if (Files.notExists(this.filePath)) {
            return new ArrayList<>();
        }

        try {
            ArrayList<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(this.filePath, StandardCharsets.UTF_8)) {
                Task task = this.parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new StorageException("Could not load tasks from " + this.filePath + ".", exception);
        }
    }

    /** Saves the task list, creating its parent directory when necessary. */
    public void save(TaskList taskList) throws StorageException {
        assert taskList != null : "Storage can only save a non-null task list";
        try {
            Path parent = this.filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(this.filePath, this.serializeTasks(taskList), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException | SecurityException exception) {
            throw new StorageException("Could not save tasks to " + this.filePath + ".", exception);
        }
    }

    /** Converts each task into one encoded record for the storage file. */
    private List<String> serializeTasks(TaskList taskList) {
        assert taskList != null : "Task serialization requires a task list";
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : taskList.getTasks()) {
            assert task != null : "A task list must not contain null tasks";
            String type;
            String extraFields = "";
            if (task instanceof Deadline deadline) {
                type = "D";
                extraFields = "|" + encode(deadline.getBy().toString());
            } else if (task instanceof Event event) {
                type = "E";
                extraFields = "|" + encode(event.getFrom().toString()) + "|"
                        + encode(event.getTo().toString());
            } else {
                type = "T";
            }

            lines.add(type + "|" + (task.isDone() ? "1" : "0") + "|"
                    + encode(task.getDescription()) + extraFields);
        }
        return lines;
    }

    /** Recreates a task from a storage record, or returns null for malformed records. */
    private Task parseTask(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            return null;
        }

        try {
            String description = decode(parts[2]);
            Task task;
            switch (parts[0]) {
                case "T":
                    if (parts.length != 3) {
                        return null;
                    }
                    task = new Todo(description);
                    break;
                case "D":
                    if (parts.length != 4) {
                        return null;
                    }
                    task = new Deadline(description, LocalDateTime.parse(decode(parts[3])));
                    break;
                case "E":
                    if (parts.length != 5) {
                        return null;
                    }
                    task = new Event(description, LocalDateTime.parse(decode(parts[3])),
                            LocalDateTime.parse(decode(parts[4])));
                    break;
                default:
                    return null;
            }

            if (parts[1].equals("1")) {
                task.markDone();
            }
            return task;
        } catch (IllegalArgumentException | DateTimeException exception) {
            return null;
        }
    }

    /** Encodes text so separators and whitespace in user input remain unchanged. */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes text previously written by {@link #encode(String)}. */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
