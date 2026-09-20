package duchess.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;

import duchess.task.Deadline;
import duchess.task.Event;
import duchess.task.Task;
import duchess.task.TaskList;
import duchess.task.Todo;

/**
 * Saves Duchess tasks in a file relative to the project root.
 *
 * <p>Text fields are Base64-encoded so descriptions and details may contain
 * the record separator without making the file ambiguous.</p>
 */
public class Storage {
    /** The relative location used for Duchess's saved task list. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "duchess.txt");

    /** The file used to persist this storage service's task list. */
    private final Path dataFile;

    /** Prevents replacing saved data that could not be fully restored. */
    private boolean hasLoadFailure;

    /** Creates a storage service that uses Duchess's default data file. */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates a storage service that uses the supplied data file.
     *
     * <p>Allowing the path to be supplied keeps production storage convenient
     * while making isolated persistence tests possible.</p>
     *
     * @param dataFile the file used to load and save tasks
     */
    public Storage(Path dataFile) {
        assert dataFile != null : "Storage requires a data-file path";
        this.dataFile = dataFile;
    }

    /**
     * Loads valid task records from disk.
     *
     * <p>A missing file means Duchess has no saved tasks yet. Invalid lines
     * are ignored individually, allowing valid records in a partially
     * corrupted file to remain usable. Saving is disabled after an incomplete
     * load to avoid overwriting records that were not restored.</p>
     *
     * @return the restored tasks, or an empty task list when the file is absent
     *         or cannot be read
     */
    public TaskList loadTasks() {
        hasLoadFailure = false;
        try {
            TaskList tasks = new TaskList();
            for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
                Task task = deserialize(line);
                if (task == null && !line.isBlank()) {
                    hasLoadFailure = true;
                }
                if (task != null && !tasks.containsEquivalent(task)) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (NoSuchFileException exception) {
            return new TaskList();
        } catch (IOException | SecurityException exception) {
            // A damaged or inaccessible file should not prevent Duchess from starting.
            hasLoadFailure = true;
            return new TaskList();
        }
    }

    /**
     * Returns a recovery warning when saved data could not be fully loaded.
     *
     * @return the warning, or an empty string after a successful load or first run.
     */
    public String getLoadWarning() {
        return hasLoadFailure ? "OOPS!!! I couldn't fully load your saved tasks from " + dataFile + ".\n"
                + "Saving is disabled to protect that file. Back it up, repair the file or its permissions, "
                + "then restart Duchess.\nChanges made in this session stay in memory and will be lost on exit." : "";
    }

    /**
     * Writes the current task list to disk.
     *
     * <p>The parent directory is created when needed. Writing through a
     * temporary file prevents a failed write from leaving a half-written task
     * list.</p>
     *
     * @param tasks the task list to save
     * @throws IOException if saved data was not fully loaded or the path cannot be written.
     */
    public void saveTasks(TaskList tasks) throws IOException {
        assert tasks != null : "Storage requires a task list to save";
        if (hasLoadFailure) {
            throw new IOException("Saving disabled because saved tasks could not be fully loaded");
        }
        try {
            Path parent = dataFile.getParent();
            if (parent == null) {
                parent = Path.of(".");
            }
            Files.createDirectories(parent);
            Path temporaryFile = Files.createTempFile(parent, "duchess", ".tmp");

            try {
                ArrayList<String> lines = new ArrayList<>();
                for (int i = 0; i < tasks.size(); i++) {
                    lines.add(serialize(tasks.get(i)));
                }
                Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
                try {
                    Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException exception) {
                    Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporaryFile);
            }
        } catch (SecurityException exception) {
            throw new IOException("Permission denied while saving tasks", exception);
        }
    }

    /**
     * Converts one task into a type, status, and encoded field record.
     *
     * @param task the task to convert
     * @return one line suitable for the data file
     */
    private String serialize(Task task) {
        assert task != null : "A saved task must not be null";
        assert task.getType() != null && task.getDescription() != null
                : "A saved task must have a type and description";
        String type = task.getType().getIcon();
        String status = task.isDone() ? "1" : "0";
        String description = encode(task.getDescription());
        String completionTime = task.getCompletedAt() == null
                ? "" : encode(task.getCompletedAt().toString());

        if (task instanceof Deadline deadline) {
            return type + "|" + status + "|" + description + "|" + encode(deadline.getBy().toString())
                    + "|" + completionTime;
        }
        if (task instanceof Event event && event.hasDateRange()) {
            return type + "|" + status + "|" + description + "|" + encode(event.getFrom().toString())
                    + "|" + encode(event.getTo().toString()) + "|" + completionTime;
        }
        if (task instanceof Event event) {
            return type + "|" + status + "|" + description + "|" + encode(event.getAt())
                    + "|" + completionTime;
        }
        return type + "|" + status + "|" + description + "|" + completionTime;
    }

    /** Encodes a text field without introducing record separators. */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Converts one saved line back into a task, returning null for bad data.
     *
     * @param line one line from the data file
     * @return the restored task, or null when the record is malformed
     */
    private Task deserialize(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] fields = line.split("\\|", -1);
        if (fields.length < 3 || (!fields[0].equals("T") && !fields[0].equals("D")
                && !fields[0].equals("E")) || (!fields[1].equals("0") && !fields[1].equals("1"))) {
            return null;
        }

        boolean isDone = fields[1].equals("1");
        String description = decode(fields[2]);
        if (description == null || description.isBlank()) {
            return null;
        }

        Task task;
        String completionField;
        if (fields[0].equals("T") && (fields.length == 3 || fields.length == 4)) {
            task = new Todo(description);
            completionField = fields.length == 4 ? fields[3] : "";
        } else if (fields[0].equals("D") && (fields.length == 4 || fields.length == 5)) {
            String by = decode(fields[3]);
            if (by == null || by.isBlank()) {
                return null;
            }
            try {
                task = new Deadline(description, by);
            } catch (RuntimeException exception) {
                return null;
            }
            completionField = fields.length == 5 ? fields[4] : "";
        } else if (fields[0].equals("E") && fields.length == 6) {
            String from = decode(fields[3]);
            String to = decode(fields[4]);
            if (from == null || to == null) {
                return null;
            }
            try {
                task = new Event(description, from, to);
            } catch (IllegalArgumentException | DateTimeParseException exception) {
                return null;
            }
            completionField = fields[5];
        } else if (fields[0].equals("E") && (fields.length == 4 || fields.length == 5)) {
            String at = decode(fields[3]);
            if (at == null || at.isBlank()) {
                return null;
            }
            task = new Event(description, at);
            completionField = fields.length == 5 ? fields[4] : "";
        } else {
            return null;
        }

        assert task != null : "A valid record must produce a task";
        if (isDone) {
            task.markAsDone(decodeCompletionTime(completionField));
        }
        return task;
    }

    /** Decodes a completion timestamp, returning null when it is absent or invalid. */
    private Instant decodeCompletionTime(String value) {
        String decodedValue = decode(value);
        if (decodedValue == null || decodedValue.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(decodedValue);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /** Decodes a saved field, returning null when it is not valid Base64. */
    private String decode(String value) {
        try {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
