package fox.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import fox.task.Deadline;
import fox.task.Event;
import fox.task.FoxDate;
import fox.task.Task;
import fox.task.Todo;

/**
 * Reads and writes Fox's tasks from a local text file.
 *
 * <p>Each task is stored on one line as {@code type|done|field...}; task
 * fields are Base64 encoded so that descriptions and times may contain any
 * normal text, including the separator character.</p>
 */
public class Storage {
    /**
     * The default data file resolved once at startup, so later working-directory
     * changes cannot silently redirect Fox's persistence.
     */
    public static final Path DEFAULT_FILE = Path.of(System.getProperty("user.dir"),
            "data", "fox.txt").toAbsolutePath().normalize();
    private final Path dataFile;
    private final TaskStorageCodec taskCodec;

    /** Creates storage using Fox's startup-directory data-file path. */
    public Storage() {
        this(DEFAULT_FILE);
    }

    /**
     * Creates storage using a caller-supplied path.
     *
     * @param dataFile the file used for persistence; its parent directory is created when needed
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
        this.taskCodec = new TaskStorageCodec();
    }

    /**
     * Loads all saved tasks, creating the parent directory if it is absent.
     *
     * @return loaded tasks, or an empty array when the file is absent or empty
     * @throws StorageException if the file cannot be read or is invalid
     */
    public Task[] load() throws StorageException {
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(dataFile)) {
                return new Task[0];
            }
            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
                String line = lines.get(lineNumber);
                if (line.isBlank()) {
                    continue;
                }
                tasks.add(parseLine(line, lineNumber + 1, dataFile));
            }
            return tasks.toArray(new Task[0]);
        } catch (IOException exception) {
            throw new StorageException("Could not read data file '" + dataFile + "'.", exception);
        }
    }

    /**
     * Saves tasks atomically, preserving the previous file if writing fails.
     *
     * @param tasks the task array; entries from index {@code 0} through {@code taskCount - 1} must be non-null
     * @param taskCount number of occupied entries in the array, from {@code 0} through {@code tasks.length}
     * @throws StorageException if the data cannot be written
     */
    public void save(Task[] tasks, int taskCount) throws StorageException {
        if (tasks == null) {
            throw new StorageException("Could not write tasks because the task collection is missing.");
        }
        if (taskCount < 0 || taskCount > tasks.length) {
            throw new StorageException("Could not write tasks because the task count is invalid.");
        }
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                lines.add(taskCodec.encode(tasks[i]));
            }
            Path temporaryFile = Files.createTempFile(parent == null ? Path.of(".") : parent,
                    dataFile.getFileName().toString(), ".tmp");
            try {
                Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
                try {
                    Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException atomicMoveFailure) {
                    Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporaryFile);
            }
        } catch (IOException exception) {
            throw new StorageException("Could not write data file '" + dataFile + "'.", exception);
        }
    }

    /** Parses one persisted record and reports its source line in any format error. */
    private static Task parseLine(String line, int lineNumber, Path dataFile) throws StorageException {
        String[] parts = line.split("\\|", -1);
        try {
            if (parts.length < 3 || !parts[1].equals("true") && !parts[1].equals("false")) {
                throw new IllegalArgumentException("invalid status or field count");
            }
            boolean isDone = Boolean.parseBoolean(parts[1]);
            Task task;
            switch (parts[0]) {
            case "T":
                requireFieldCount(parts, 3);
                String todoDescription = decode(parts[2]);
                requireNonBlank(todoDescription);
                task = new Todo(todoDescription);
                break;
            case "D":
                requireFieldCount(parts, 4);
                String deadlineDescription = decode(parts[2]);
                String deadlineTime = decode(parts[3]);
                requireNonBlank(deadlineDescription);
                requireNonBlank(deadlineTime);
                task = parseDeadlineValue(deadlineTime, lineNumber)
                        .toDeadline(deadlineDescription);
                break;
            case "E":
                requireFieldCount(parts, 5);
                String eventDescription = decode(parts[2]);
                String eventFrom = decode(parts[3]);
                String eventTo = decode(parts[4]);
                requireNonBlank(eventDescription);
                requireNonBlank(eventFrom);
                requireNonBlank(eventTo);
                task = new Event(eventDescription, eventFrom, eventTo);
                break;
            default:
                throw new IllegalArgumentException("unknown task type");
            }
            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (IllegalArgumentException exception) {
            throw new StorageException("Invalid data on line " + lineNumber
                    + " in '" + dataFile + "'.", exception);
        }
    }

    /**
     * Loads ISO dates as typed values while retaining older free-form Level-7 values.
     */
    private static DeadlineValue parseDeadlineValue(String value, int lineNumber)
            throws StorageException {
        if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            if (value.matches("\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2}")) {
                throw new StorageException("Invalid data on line " + lineNumber
                        + ": deadline date is not valid.");
            }
            return new DeadlineValue(value);
        }
        try {
            return new DeadlineValue(LocalDate.parse(value, FoxDate.INPUT_FORMAT));
        } catch (DateTimeParseException exception) {
            throw new StorageException("Invalid data on line " + lineNumber
                    + ": deadline date is not valid.", exception);
        }
    }

    /** Small carrier used to keep legacy and typed deadline loading explicit. */
    private static final class DeadlineValue {
        private final LocalDate date;
        private final String text;

        /** Creates a carrier for a typed deadline date. */
        DeadlineValue(LocalDate date) {
            this.date = date;
            this.text = null;
        }

        /** Creates a carrier for a legacy free-form deadline value. */
        DeadlineValue(String text) {
            this.date = null;
            this.text = text;
        }

        /** Creates a deadline using whichever representation this carrier contains. */
        Deadline toDeadline(String description) {
            return date == null ? new Deadline(description, text) : new Deadline(description, date);
        }
    }

    /** Checks that a decoded record has the expected number of fields. */
    private static void requireFieldCount(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("invalid field count");
        }
    }

    /** Checks that a decoded task field contains visible content. */
    private static void requireNonBlank(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("a task field cannot be empty");
        }
    }

    /** Decodes one Base64-encoded persisted field as UTF-8 text. */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /** A user-facing storage or data-format failure. */
    public static class StorageException extends Exception {
        private static final long serialVersionUID = 1L;
        /**
         * Creates a storage failure with a concise message.
         *
         * @param message the explanation of the storage failure
         */
        public StorageException(String message) {
            super(message);
        }

        /**
         * Creates a storage failure while retaining the underlying cause.
         *
         * @param message the explanation of the storage failure
         * @param cause the underlying failure that caused this exception
         */
        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
