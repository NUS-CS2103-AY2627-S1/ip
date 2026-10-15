package rocky.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import rocky.task.Deadline;
import rocky.task.Event;
import rocky.task.Task;
import rocky.task.Todo;

/**
 * Handles loading tasks from and saving tasks to a file.
 */
public class Storage {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private final Path taskFile;

    /**
     * Creates storage for the supplied task file path.
     *
     * @param filePath the path of the task file.
     */
    public Storage(String filePath) {
        this.taskFile = Path.of(filePath);
    }

    /**
     * Saves all tasks, creating the parent directory when necessary.
     *
     * @param tasks the tasks to persist.
     */
    public void save(List<Task> tasks) {
        Path temporaryFile = null;
        try {
            Path parent = taskFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            StringBuilder contents = new StringBuilder();
            for (Task task : tasks) {
                contents.append(task.toStorageString()).append(System.lineSeparator());
            }
            Path directory = parent == null ? Path.of(".") : parent;
            temporaryFile = Files.createTempFile(directory, "rocky", ".tmp");
            Files.writeString(temporaryFile, contents.toString());
            try {
                Files.move(temporaryFile, taskFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            System.out.println("Rocky cannot save to the file...: " + exception.getMessage());
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // Keep the original save error as the useful message.
                }
            }
        }
    }

    /**
     * Loads valid tasks, returning an empty list when no readable file exists.
     *
     * @return the valid tasks reconstructed from the task file.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        try {
            if (Files.notExists(taskFile)) {
                return tasks;
            }
            if (!Files.isRegularFile(taskFile) || !Files.isReadable(taskFile)) {
                System.out.println("Rocky cannot load the task file because it is not a readable file.");
                return tasks;
            }
            for (String line : Files.readAllLines(taskFile)) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException | SecurityException exception) {
            System.out.println("Rocky cannot load from the file...: " + exception.getMessage());
        }
        return tasks;
    }

    /** Reconstructs one task from its stored pipe-delimited representation.
     *
     * @param line the stored task line.
     * @return the reconstructed task, or {@code null} when the line is invalid.
     */
    private Task parseTask(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] fields = line.split("\\s*\\|\\s*", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        if (!hasValidStatus(fields)) {
            return null;
        }
        try {
            Task task = createTask(fields);
            if (task == null) {
                return null;
            }
            restoreCompletionStatus(task, fields[1]);
            return task;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    /** Checks the fields shared by every stored task record. */
    private boolean hasValidStatus(String[] fields) {
        return fields.length >= 2 && !fields[0].isEmpty() && !fields[1].isEmpty()
                && (fields[1].equals("0") || fields[1].equals("1"));
    }

    /** Creates a task from its type-specific storage fields. */
    private Task createTask(String[] fields) {
        return switch (fields[0]) {
        case "T" -> parseTodo(fields);
        case "D" -> parseDeadline(fields);
        case "E" -> parseEvent(fields);
        default -> null;
        };
    }

    /** Parses a stored to-do record. */
    private Task parseTodo(String[] fields) {
        if (fields.length != 3 || fields[2].isEmpty()) {
            return null;
        }
        return new Todo(fields[2]);
    }

    /** Parses a stored deadline record. */
    private Task parseDeadline(String[] fields) {
        if (fields.length != 4 || fields[2].isEmpty()) {
            return null;
        }
        ParsedDateTime dueDateTime = parseDateTime(fields[3]);
        if (dueDateTime == null) {
            return null;
        }
        return dueDateTime.hasTime ? new Deadline(fields[2], dueDateTime.value)
                : new Deadline(fields[2], dueDateTime.value.toLocalDate());
    }

    /** Parses a stored event record. */
    private Task parseEvent(String[] fields) {
        if (fields.length != 5 || fields[2].isEmpty()) {
            return null;
        }
        ParsedDateTime startDateTime = parseDateTime(fields[3]);
        ParsedDateTime endDateTime = parseDateTime(fields[4]);
        if (startDateTime == null || endDateTime == null
                || startDateTime.hasTime != endDateTime.hasTime
                || endDateTime.value.isBefore(startDateTime.value)) {
            return null;
        }
        return startDateTime.hasTime ? new Event(fields[2], startDateTime.value, endDateTime.value)
                : new Event(fields[2], startDateTime.value.toLocalDate(), endDateTime.value.toLocalDate());
    }

    /** Restores the completion state encoded in a stored status field. */
    private void restoreCompletionStatus(Task task, String status) {
        if (status.equals("1")) {
            task.markAsDone();
        }
    }

    /** Parses a stored date or date-time value into the internal representation.
     *
     * @param text the stored date or date-time text
     * @return the parsed value, or {@code null} when the text is invalid
     */
    private ParsedDateTime parseDateTime(String text) {
        try {
            if (text.contains("T")) {
                return new ParsedDateTime(LocalDateTime.parse(text), true);
            }
            if (text.contains(" ")) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");
                return new ParsedDateTime(LocalDateTime.parse(text, format), true);
            }
            return new ParsedDateTime(LocalDate.parse(text, INPUT_DATE_FORMAT).atStartOfDay(), false);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /** Holds a parsed date-time and whether its stored value included a time. */
    private static class ParsedDateTime {
        private final LocalDateTime value;
        private final boolean hasTime;

        /** Creates a parsed date-time and records whether the input included a time.
         *
         * @param value the parsed date-time value
         * @param hasTime whether the original input included a time
         */
        ParsedDateTime(LocalDateTime value, boolean hasTime) {
            assert value != null : "Stored date-time values must not be null";
            this.value = value;
            this.hasTime = hasTime;
        }
    }
}
