package com.benthecat.kachow.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.parser.DateTimeParser;
import com.benthecat.kachow.task.Deadline;
import com.benthecat.kachow.task.Event;
import com.benthecat.kachow.task.Task;
import com.benthecat.kachow.task.TaskList;
import com.benthecat.kachow.task.Todo;

/**
 * Loads and saves Kachow tasks in a human-readable text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path dataFile;
    private boolean hasLoaded;
    private boolean hasLoadingFailed;
    // Retains the last disk contents so another process's edits are not silently overwritten.
    private String savedContents;

    /**
     * Creates storage backed by the given data file.
     *
     * @param dataFile Path to the task data file.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads every task from disk. A missing file represents a new user with an empty task list.
     *
     * @return Tasks stored in the data file, in their saved order.
     * @throws KachowException If the file exists but cannot be read or contains invalid task data.
     */
    public List<Task> load() throws KachowException {
        try {
            String contents = readContents();
            TaskList tasks = new TaskList();
            List<String> lines = contents == null ? List.of() : contents.lines().toList();
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    try {
                        tasks.add(parseTask(lines.get(i), i + 1));
                    } catch (IllegalArgumentException | KachowException exception) {
                        throw createInvalidDataException(i + 1);
                    }
                }
            }
            savedContents = contents;
            hasLoaded = true;
            hasLoadingFailed = false;
            return tasks.getTasks();
        } catch (IOException | SecurityException exception) {
            hasLoadingFailed = true;
            throw new KachowException("I couldn't read task data from " + dataFile
                    + ". Check that the path is a readable file.", exception);
        } catch (KachowException exception) {
            hasLoadingFailed = true;
            throw exception;
        }
    }

    /**
     * Saves through a temporary file and atomically replaces the original only after writing succeeds.
     * Refuses writes after a failed load or when another process has changed the stored data.
     *
     * @param tasks Tasks to persist.
     * @throws KachowException If the task data cannot be safely written.
     */
    public void save(List<Task> tasks) throws KachowException {
        if (hasLoadingFailed) {
            throw new KachowException("Saving is disabled because task data could not be loaded. "
                    + "Repair the file or restore a backup, then restart Kachow. Your file has not been changed.");
        }
        if (!hasLoaded) {
            load();
        }
        Path temporaryFile = null;
        try {
            requireUnchangedFile();
            if (Files.exists(dataFile) && !Files.isWritable(dataFile)) {
                throw new IOException("The task file is not writable.");
            }
            Path parent = dataFile.toAbsolutePath().getParent();
            Files.createDirectories(parent);
            String contents = tasks.isEmpty() ? "" : String.join("\n", tasks.stream()
                    .map(this::formatTask).toList()) + "\n";
            temporaryFile = Files.createTempFile(parent, ".kachow-", ".tmp");
            Files.writeString(temporaryFile, contents, StandardCharsets.UTF_8);
            requireUnchangedFile();
            // Do not fall back to truncating the original when atomic replacement is unavailable.
            Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            savedContents = contents;
        } catch (IOException | SecurityException exception) {
            throw new KachowException("I couldn't save task data to " + dataFile
                    + ". No changes were applied. Check file permissions, available disk space, "
                    + "and support for atomic file replacement, then try again.", exception);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Reads the exact UTF-8 contents, distinguishing an absent file from an unreadable one. */
    private String readContents() throws IOException {
        try {
            BasicFileAttributes attributes = Files.readAttributes(
                    dataFile, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            if (!attributes.isRegularFile()) {
                throw new IOException("The task path must be a regular file, not a directory or symbolic link.");
            }
            return Files.readString(dataFile, StandardCharsets.UTF_8);
        } catch (NoSuchFileException exception) {
            requireDirectoryAncestor();
            return null;
        }
    }

    /** Distinguishes missing directories from a file that blocks the configured directory path. */
    private void requireDirectoryAncestor() throws IOException {
        Path parent = dataFile.toAbsolutePath().getParent();
        while (parent != null) {
            try {
                if (!Files.readAttributes(parent, BasicFileAttributes.class).isDirectory()) {
                    throw new IOException("A parent of the task file is not a directory.");
                }
                return;
            } catch (NoSuchFileException ignored) {
                // Check the nearest existing ancestor; genuinely missing directories can be created on save.
                parent = parent.getParent();
            }
        }
    }

    /** Prevents a stale application instance from replacing another writer's changes. */
    private void requireUnchangedFile() throws IOException, KachowException {
        if (!Objects.equals(savedContents, readContents())) {
            throw new KachowException("The task file changed outside Kachow. No changes were applied. "
                    + "Restart Kachow to load the latest tasks.");
        }
    }

    /** Removes an unfinished temporary file without hiding the original save failure. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // A leftover temporary file does not affect the original data or the next save attempt.
        }
    }

    /**
     * Converts one saved line into its corresponding task subtype.
     *
     * @param line Saved task record.
     * @param lineNumber One-based line number used in validation messages.
     * @return Restored task.
     * @throws KachowException If the line does not follow the storage format.
     */
    private Task parseTask(String line, int lineNumber) throws KachowException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw createInvalidDataException(lineNumber);
        }

        boolean isDone;
        if (fields[1].equals("1")) {
            isDone = true;
        } else if (fields[1].equals("0")) {
            isDone = false;
        } else {
            throw createInvalidDataException(lineNumber);
        }

        return switch (fields[0]) {
            case "T" -> {
                requireFieldCount(fields, 3, lineNumber);
                yield new Todo(fields[2], isDone);
            }
            case "D" -> {
                requireFieldCount(fields, 4, lineNumber);
                yield parseDeadline(fields[2], fields[3], isDone, lineNumber);
            }
            case "E" -> {
                requireFieldCount(fields, 5, lineNumber);
                yield parseEvent(fields[2], fields[3], fields[4], isDone, lineNumber);
            }
            default -> throw createInvalidDataException(lineNumber);
        };
    }

    /**
     * Converts a task into one line of the storage format.
     *
     * @param task Task to convert.
     * @return Serialized task record.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        return switch (task) {
            case Todo todo -> String.join(FIELD_SEPARATOR, "T", status, task.getDescription());
            case Deadline deadline -> String.join(FIELD_SEPARATOR, "D", status, task.getDescription(),
                    DateTimeParser.formatForStorage(deadline.getDueDateTime()));
            case Event event -> String.join(FIELD_SEPARATOR, "E", status, task.getDescription(),
                    DateTimeParser.formatForStorage(event.getFrom()),
                    DateTimeParser.formatForStorage(event.getTo()));
            default -> throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
        };
    }

    /** Restores a deadline from its canonical ISO date or date-time representation. */
    private Deadline parseDeadline(String description, String by, boolean isDone, int lineNumber)
            throws KachowException {
        return new Deadline(description, parseDateTime(by, lineNumber), isDone);
    }

    /** Restores an event while enforcing its chronological range invariant. */
    private Event parseEvent(String description, String from, String to, boolean isDone, int lineNumber)
            throws KachowException {
        try {
            return new Event(
                    description,
                    parseDateTime(from, lineNumber),
                    parseDateTime(to, lineNumber),
                    isDone);
        } catch (IllegalArgumentException exception) {
            throw createInvalidDataException(lineNumber);
        }
    }

    /**
     * Parses any stored task date/time through the common parser.
     */
    private DateTimeParser.ParsedDateTime parseDateTime(String value, int lineNumber) throws KachowException {
        try {
            return DateTimeParser.parse(value);
        } catch (DateTimeParseException exception) {
            throw createInvalidDataException(lineNumber);
        }
    }

    /**
     * Ensures a record has exactly the number of fields required by its task type.
     */
    private void requireFieldCount(String[] fields, int expectedCount, int lineNumber) throws KachowException {
        if (fields.length != expectedCount) {
            throw createInvalidDataException(lineNumber);
        }
    }

    /**
     * Creates a consistent exception for malformed saved data.
     */
    private KachowException createInvalidDataException(int lineNumber) {
        return new KachowException("Task data on line " + lineNumber + " of " + dataFile + " is invalid.");
    }
}
