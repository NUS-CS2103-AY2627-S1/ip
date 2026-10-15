package jelly.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.util.ArrayList;

import jelly.model.Deadline;
import jelly.model.Event;
import jelly.model.Task;
import jelly.model.TaskList;
import jelly.model.Todo;
import jelly.util.DateTimeParser;

/**
 * Saves and loads Jelly tasks from local data file.
 */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";

    /**
     * Location of the tasks' data file, relative to project root.
     */
    private final Path filePath;

    /** Creates storage using a default data file. */
    public Storage() {
        this(Path.of("data", "jelly.txt"));
    }

    /** Creates storage using the supplied data file. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file.
     *
     * @return the saved tasks, or an empty list if the file does not exist.
     * @throws IOException if the file cannot be read.
     */
    public TaskList load() throws IOException {
        TaskList tasks = new TaskList();

        // If the data file does not exist, handle it as an empty list.
        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath)) {
            Task task = parseLine(line);
            if (task != null) {
                tasks.addTask(task);
            }
        }

        return tasks;
    }

    /** Parses one saved line into a task, or returns {@code null} if the line is malformed. */
    private static Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        Task task;
        try {
            task = createTask(parts[0], parts[2], parts);
        } catch (DateTimeException | IllegalArgumentException exception) {
            return null;
        }
        if (task == null) {
            return null;
        }

        if (!parts[1].equals(DONE_STATUS) && !parts[1].equals(NOT_DONE_STATUS)) {
            return null;
        }
        if (parts[1].equals(DONE_STATUS)) {
            task.markAsDone();
        }

        // The tag is stored as the final field when present.
        if (parts.length >= expectedFieldCount(parts[0])
                && !parts[parts.length - 1].isBlank()) {
            try {
                task.setTag(parts[parts.length - 1]);
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }

        return task;
    }

    /** Creates the task matching the type code, or returns {@code null} if it is unrecognized. */
    private static Task createTask(String type, String description, String[] parts) {
        if (type.equals(TODO_TYPE)) {
            return new Todo(description);
        } else if (type.equals(DEADLINE_TYPE) && parts.length >= 4) {
            return new Deadline(description, DateTimeParser.parseStored(parts[3]));
        } else if (type.equals(EVENT_TYPE) && parts.length >= 5) {
            return new Event(description, DateTimeParser.parseStored(parts[3]), DateTimeParser.parseStored(parts[4]));
        }
        return null;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks tasks to save.
     * @throws IOException if the directory or file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        assert tasks != null : "Tasklist should not be null";

        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks.asList()) {
            String line = formatLine(task);
            if (line != null) {
                lines.add(line);
            }
        }

        Files.write(filePath, lines);
    }

    /** Formats one task as a storage line, or returns {@code null} if its type is unrecognized. */
    private static String formatLine(Task task) {
        if (task instanceof Todo) {
            return TODO_TYPE + " | " + statusFor(task)
                    + task.getDescription()
                    + " | " + tagFor(task);
        } else if (task instanceof Deadline deadline) {
            return DEADLINE_TYPE + " | " + statusFor(task)
                    + task.getDescription()
                    + " | " + deadline.getBy()
                    + " | " + tagFor(task);
        } else if (task instanceof Event event) {
            return EVENT_TYPE + " | " + statusFor(task)
                    + task.getDescription()
                    + " | " + event.getFrom()
                    + " | " + event.getTo()
                    + " | " + tagFor(task);
        }
        return null;
    }

    /**
     * Returns the task tag for storage.
     *
     * @param task the task whose tag should be serialized.
     * @return the tag, or an empty string if the task has no tag.
     */
    private static String tagFor(Task task) {
        return task.hasTag() ? task.getTag() : "";
    }

    /** Returns the serialized completion status and its field separator. */
    private static String statusFor(Task task) {
        return (task.isDone() ? DONE_STATUS : NOT_DONE_STATUS) + " | ";
    }

    /**
     * Returns the minimum number of fields expected for a task type.
     *
     * @param type the task type code.
     * @return the minimum number of serialized fields.
     */
    private static int expectedFieldCount(String type) {
        return switch (type) {
            case TODO_TYPE -> 4;
            case DEADLINE_TYPE -> 5;
            case EVENT_TYPE -> 6;
            default -> Integer.MAX_VALUE;
        };

    }
}
