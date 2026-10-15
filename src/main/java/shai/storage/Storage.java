package shai.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import shai.exception.ShaiException;
import shai.parser.DateTimeParser;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.Reminder;
import shai.task.Task;
import shai.task.TaskList;
import shai.task.ToDo;

/**
 * Saves and loads the current task list on the hard disk.
 *
 * <p>The file is rewritten after every successful task-list mutation so that it
 * always reflects the current in-memory list.</p>
 */
public class Storage {
    private final Path taskFile;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath path to the task file
     * @throws IllegalArgumentException if the path is blank or invalid
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("The task file path must not be blank.");
        }
        try {
            taskFile = Path.of(filePath);
        } catch (InvalidPathException exception) {
            throw new IllegalArgumentException("The task file path is invalid.", exception);
        }
        if (taskFile.getFileName() == null) {
            throw new IllegalArgumentException("The task file path must identify a file.");
        }
    }

    /**
     * Saves all tasks to the configured task file.
     *
     * @param tasks the current task list
     * @throws ShaiException if the directory or file cannot be written
     */
    public void saveTasks(TaskList tasks) throws ShaiException {
        if (tasks == null) {
            throw new ShaiException("I couldn't save your lineup, King. Check the task file.");
        }

        Path temporaryFile = taskFile.resolveSibling(taskFile.getFileName() + ".tmp");
        try {
            if (taskFile.getParent() != null) {
                Files.createDirectories(taskFile.getParent());
            }
            List<String> lines = StreamSupport.stream(tasks.spliterator(), false)
                    .map(Storage::formatTask)
                    .toList();
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceTaskFile(temporaryFile);
        } catch (IOException | SecurityException | IllegalArgumentException e) {
            deleteTemporaryFile(temporaryFile);
            throw new ShaiException("I couldn't save your lineup, King. Check the task file.");
        }
    }

    /**
     * Loads tasks from the configured task file.
     *
     * @return the tasks stored on disk, or an empty list if the file does not exist
     * @throws ShaiException if the file cannot be read or contains invalid data
     */
    public TaskList loadTasks() throws ShaiException {
        try {
            TaskList tasks = new TaskList();
            List<String> lines = Files.readAllLines(taskFile, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    tasks.add(parseTask(line, i + 1));
                }
            }
            return tasks;
        } catch (NoSuchFileException e) {
            return new TaskList();
        } catch (IOException | SecurityException e) {
            throw new ShaiException("I couldn't load your lineup, King. Check the task file.");
        }
    }

    /** Replaces the old task file only after the new contents have been written successfully. */
    private void replaceTaskFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, taskFile,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException | AccessDeniedException e) {
            Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Removes a failed temporary save without hiding the original save error. */
    private static void deleteTemporaryFile(Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original save error is the useful message for the user.
        }
    }

    /** Converts a task into the simple line format used by the data file. */
    private static String formatTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task list cannot contain null tasks.");
        }
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return joinFields(
                    "D",
                    status,
                    escapeField(task.getDescription()),
                    escapeField(DateTimeParser.formatForStorage(deadline.getBy())),
                    String.valueOf(deadline.getReminderMinutesBefore()));
        }
        if (task instanceof Event event) {
            return joinFields(
                    "E",
                    status,
                    escapeField(task.getDescription()),
                    escapeField(DateTimeParser.formatForStorage(event.getFrom())),
                    escapeField(DateTimeParser.formatForStorage(event.getTo())),
                    String.valueOf(event.getReminderMinutesBefore()));
        }
        return joinFields("T", status, escapeField(task.getDescription()));
    }

    /** Joins a variable number of task fields using the storage separator. */
    private static String joinFields(String... fields) {
        assert fields != null && fields.length > 0 : "Storage fields must be provided.";
        assert Arrays.stream(fields).allMatch(Objects::nonNull) : "Storage fields must not be null.";
        return String.join(" | ", fields);
    }

    /** Escapes characters that have a special meaning in the task file format. */
    private static String escapeField(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Task fields cannot be null.");
        }
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /** Parses one persisted task line. */
    private static Task parseTask(String line, int lineNumber) throws ShaiException {
        List<String> fields = splitFields(line, lineNumber);
        validateCommonFields(fields, lineNumber);
        Task task = switch (fields.get(0)) {
            case "T" -> parseToDo(fields, lineNumber);
            case "D" -> parseDeadline(fields, lineNumber);
            case "E" -> parseEvent(fields, lineNumber);
            default -> throw invalidData(lineNumber);
        };
        restoreCompletionStatus(task, fields.get(1));
        return task;
    }

    /** Validates the fields shared by every persisted task type. */
    private static void validateCommonFields(List<String> fields, int lineNumber) throws ShaiException {
        if (fields.size() < 3) {
            throw invalidData(lineNumber);
        }

        String status = fields.get(1);
        String description = fields.get(2);
        boolean isValidStatus = status.equals("0") || status.equals("1");
        if (description.isBlank() || !isValidStatus) {
            throw invalidData(lineNumber);
        }
    }

    /** Creates a ToDo from its persisted fields. */
    private static Task parseToDo(List<String> fields, int lineNumber) throws ShaiException {
        if (fields.size() != 3) {
            throw invalidData(lineNumber);
        }
        return new ToDo(fields.get(2));
    }

    /** Creates a Deadline from its persisted fields. */
    private static Task parseDeadline(List<String> fields, int lineNumber) throws ShaiException {
        if ((fields.size() != 4 && fields.size() != 5) || fields.get(3).isBlank()) {
            throw invalidData(lineNumber);
        }
        long reminderMinutesBefore = fields.size() == 4
                ? Reminder.DEFAULT_MINUTES_BEFORE
                : parseReminderMinutes(fields.get(4), lineNumber);
        return new Deadline(fields.get(2), parseDateTime(fields.get(3), lineNumber), reminderMinutesBefore);
    }

    /** Creates an Event from its persisted fields. */
    private static Task parseEvent(List<String> fields, int lineNumber) throws ShaiException {
        if ((fields.size() != 5 && fields.size() != 6)
                || fields.get(3).isBlank() || fields.get(4).isBlank()) {
            throw invalidData(lineNumber);
        }
        LocalDateTime from = parseDateTime(fields.get(3), lineNumber);
        LocalDateTime to = parseDateTime(fields.get(4), lineNumber);
        if (!from.isBefore(to)) {
            throw invalidData(lineNumber);
        }
        long reminderMinutesBefore = fields.size() == 5
                ? Reminder.DEFAULT_MINUTES_BEFORE
                : parseReminderMinutes(fields.get(5), lineNumber);
        return new Event(fields.get(2), from, to, reminderMinutesBefore);
    }

    /** Restores the completion status encoded in a persisted task line. */
    private static void restoreCompletionStatus(Task task, String status) {
        assert task != null : "A valid task record must create a task.";
        if (status.equals("1")) {
            task.markAsDone();
        }
    }

    /** Parses a date or time restored from the task file. */
    private static LocalDateTime parseDateTime(String value, int lineNumber) throws ShaiException {
        try {
            return DateTimeParser.parse(value);
        } catch (DateTimeParseException e) {
            throw invalidData(lineNumber);
        }
    }

    /** Parses a persisted reminder lead time. */
    private static long parseReminderMinutes(String value, int lineNumber) throws ShaiException {
        try {
            long minutesBefore = Long.parseLong(value);
            if (!Reminder.isValidMinutesBefore(minutesBefore)) {
                throw new NumberFormatException();
            }
            return minutesBefore;
        } catch (NumberFormatException e) {
            throw invalidData(lineNumber);
        }
    }

    /** Splits a persisted line while preserving escaped separators and special characters. */
    private static List<String> splitFields(String line, int lineNumber) throws ShaiException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else if (character == '\\' && i + 1 < line.length()) {
                char escaped = line.charAt(i + 1);
                if (escaped == '\\' || escaped == '|' || escaped == 'n' || escaped == 'r') {
                    field.append(unescapeCharacter(escaped));
                    i++;
                } else {
                    throw invalidData(lineNumber);
                }
            } else if (character == '\\') {
                throw invalidData(lineNumber);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /** Converts one escaped character into its stored value. */
    private static char unescapeCharacter(char escaped) {
        return switch (escaped) {
            case 'n' -> '\n';
            case 'r' -> '\r';
            default -> escaped;
        };
    }

    /** Creates a consistent error for a malformed persisted line. */
    private static ShaiException invalidData(int lineNumber) {
        return new ShaiException("I couldn't load your lineup, King. The play on line "
                + lineNumber + " is invalid.");
    }
}
