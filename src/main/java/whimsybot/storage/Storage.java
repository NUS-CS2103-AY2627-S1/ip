package whimsybot.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import whimsybot.task.Deadline;
import whimsybot.task.Event;
import whimsybot.task.Task;
import whimsybot.task.Todo;

/** Saves and loads Whimsy Bot tasks using a file relative to the project root. */
public class Storage {
    private static final Path FILE_PATH = Paths.get("data", "whimsybot.txt");

    /** Writes the current tasks, creating the parent directory when necessary.
     *
     * @param tasks the array containing the tasks to save
     * @param taskCount the number of valid tasks in the array
     */
    public void save(Task[] tasks, int taskCount) throws StorageException {
        // TaskList supplies a compact prefix whose count cannot exceed the backing array.
        assert tasks != null : "Tasks array must not be null";
        assert taskCount >= 0 && taskCount <= tasks.length : "Task count must fit in the tasks array";
        try {
            Files.createDirectories(FILE_PATH.getParent());
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                // Every element in the valid prefix is expected to be a real task.
                assert tasks[i] != null : "A saved task entry must not be null";
                lines.add(serialize(tasks[i]));
            }
            Files.write(FILE_PATH, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            throw new StorageException(
                    "Unable to save your tasks. Please check that the data folder is writable.", e);
        }
    }

    private static String serialize(Task task) {
        // Serialization is only meaningful for an existing task object.
        assert task != null : "Cannot serialize a null task";
        String type;
        List<String> fields = new ArrayList<>();
        if (task instanceof Deadline deadline) {
            type = "D";
            fields.add(deadline.getBy());
        } else if (task instanceof Event event) {
            type = "E";
            fields.add(event.getFrom());
            fields.add(event.getTo());
        } else {
            type = "T";
        }
        StringBuilder line = new StringBuilder(type)
                .append("|").append(task.isDone() ? "1" : "0")
                .append("|").append(encode(task.getDescription()));
        for (String field : fields) {
            line.append("|").append(encode(field));
        }
        line.append("|").append(encode(String.join(",", task.getTags())));
        return line.toString();
    }

    /** Loads valid tasks and ignores malformed records so one bad line cannot break startup. */
    public List<Task> load() throws StorageException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8)) {
                Task task = parse(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException | SecurityException e) {
            throw new StorageException(
                    "Unable to load your saved tasks. Please check that the data file is readable.", e);
        }
        return tasks;
    }

    private static Task parse(String line) {
        String[] parts = line.split("\\|", -1);
        try {
            if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
                return null;
            }
            String description = decode(parts[2]);
            Task task;
            switch (parts[0]) {
            case "T":
                if (parts.length != 3 && parts.length != 4) {
                    return null;
                }
                task = new Todo(description);
                break;
            case "D":
                if (parts.length != 4 && parts.length != 5) {
                    return null;
                }
                task = new Deadline(description, decode(parts[3]));
                break;
            case "E":
                if (parts.length != 5 && parts.length != 6) {
                    return null;
                }
                task = new Event(description, decode(parts[3]), decode(parts[4]));
                break;
            default:
                return null;
            }
            if (parts[1].equals("1")) {
                task.markAsDone();
            }
            if ((task instanceof Todo && parts.length == 4)
                    || (task instanceof Deadline && parts.length == 5)
                    || (task instanceof Event && parts.length == 6)) {
                for (String tag : decode(parts[parts.length - 1]).split(",")) {
                    if (!tag.isBlank()) {
                        task.addTag(tag);
                    }
                }
            }
            return task;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
