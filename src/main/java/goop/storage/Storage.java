package goop.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import goop.task.Deadline;
import goop.task.Event;
import goop.task.Priority;
import goop.task.Task;
import goop.task.TaskList;
import goop.task.Todo;

/**
 * Loads tasks from and saves tasks to a text file on the local hard disk.
 */
public class Storage {
    private static final String PRIORITY_PREFIX = "priority=";
    private static final String FIELD_SEPARATOR = " | ";
    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";

    /** Positions and record lengths in the persisted task format. */
    private static final int TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_INDEX = 3;
    private static final int EVENT_START_INDEX = 3;
    private static final int EVENT_END_INDEX = 4;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private final Path filePath;

    /** Prevents replacing unreadable or invalid data with a partially loaded list. */
    private boolean isSavingBlocked;

    /**
     * Creates storage that uses the given OS-independent path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks. A missing file represents an empty task list.
     *
     * @return Tasks reconstructed from the data file.
     * @throws IOException If the file cannot be read or contains invalid data.
     */
    public List<Task> loadTasks() throws IOException {
        isSavingBlocked = true;
        List<Task> tasks = readTasks();
        isSavingBlocked = false;
        return tasks;
    }

    /**
     * Loads a complete valid snapshot before allowing saves.
     */
    private List<Task> readTasks() throws IOException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new IOException("I couldn't read saved tasks from " + filePath + ".", error);
        }

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            Task task = parseTask(lines.get(i), i + 1);
            if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                throw invalidData(i + 1, "duplicate task details");
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Replaces the data file with a representation of the current task list,
     * creating its parent folder when necessary.
     *
     * @param tasks Current task list to save.
     * @throws IOException If the folder or data file cannot be written.
     */
    public void saveTasks(TaskList tasks) throws IOException {
        if (isSavingBlocked) {
            throw new IOException("Saving is disabled because saved tasks could not be loaded. "
                    + "Fix or move the data file and restart Goop.");
        }
        Path target = filePath.toAbsolutePath();
        Path parentDirectory = target.getParent();
        Path temporaryFile = null;
        try {
            Files.createDirectories(parentDirectory);
            if (Files.isSymbolicLink(target)
                    || Files.exists(target) && (!Files.isRegularFile(target) || !Files.isWritable(target))) {
                throw new IOException("The destination is not a writable regular file.");
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks.getTasks()) {
                String line = formatTask(task);
                parseTask(line, lines.size() + 1);
                lines.add(line);
            }
            temporaryFile = Files.createTempFile(parentDirectory, ".goop-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            // No non-atomic fallback: a failed replacement must leave the previous file intact.
            Files.move(temporaryFile, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException error) {
            throw new IOException("I couldn't save tasks to " + filePath + ".", error);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException error) {
                    // A leftover temporary file must not hide the original failure or undo a successful save.
                }
            }
        }
    }

    /**
     * Converts one task into the line format used by the data file.
     */
    private String formatTask(Task task) throws IOException {
        String record = formatTaskDetails(task);
        if (task.getPriority() == Priority.NONE) {
            return record;
        }
        return record + FIELD_SEPARATOR + PRIORITY_PREFIX + task.getPriority().getLabel();
    }

    /**
     * Formats the original task fields, preserving compatibility for unprioritized tasks.
     */
    private String formatTaskDetails(Task task) throws IOException {
        String status = task.isDone() ? STATUS_DONE : STATUS_NOT_DONE;
        String description = escape(task.getDescription());

        if (task instanceof Todo) {
            return "T" + FIELD_SEPARATOR + status + FIELD_SEPARATOR + description;
        }
        if (task instanceof Deadline deadline) {
            return "D" + FIELD_SEPARATOR + status + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR
                    + deadline.getBy().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (task instanceof Event event) {
            return "E" + FIELD_SEPARATOR + status + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR + escape(event.getFrom())
                    + FIELD_SEPARATOR + escape(event.getTo());
        }
        throw new IOException("I couldn't save an unsupported task type.");
    }

    /**
     * Reconstructs one task from a data-file line and validates every field.
     */
    private Task parseTask(String line, int lineNumber) throws IOException {
        List<String> fields = splitFields(line, lineNumber);
        if (fields.size() < TODO_FIELD_COUNT) {
            throw invalidData(lineNumber, "not enough fields");
        }

        boolean isDone = parseStatus(fields.get(STATUS_INDEX), lineNumber);
        String description = requireText(fields.get(DESCRIPTION_INDEX), lineNumber, "description");
        int fieldCount = getTaskFieldCount(fields.get(TYPE_INDEX), lineNumber);
        Priority priority = readPriority(fields, fieldCount, lineNumber);
        Task task = createTask(fields.subList(0, fieldCount), description, lineNumber);
        task.setPriority(priority);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns the number of fields in a task record before the optional priority.
     */
    private int getTaskFieldCount(String type, int lineNumber) throws IOException {
        switch (type) {
            case "T":
                return TODO_FIELD_COUNT;
            case "D":
                return DEADLINE_FIELD_COUNT;
            case "E":
                return EVENT_FIELD_COUNT;
            default:
                throw invalidData(lineNumber, "unknown task type");
        }
    }

    /**
     * Reads an optional priority field while rejecting malformed or extra fields.
     */
    private Priority readPriority(List<String> fields, int fieldCount, int lineNumber) throws IOException {
        if (fields.size() == fieldCount) {
            return Priority.NONE;
        }
        if (fields.size() != fieldCount + 1 || !fields.get(fieldCount).startsWith(PRIORITY_PREFIX)) {
            throw invalidData(lineNumber, "wrong number of fields for this task type");
        }
        String label = fields.get(fieldCount).substring(PRIORITY_PREFIX.length());
        try {
            return Priority.valueOf(label.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException error) {
            throw invalidData(lineNumber, "priority must be high, medium, low, or none");
        }
    }

    /**
     * Validates the stored completion status without accepting other numeric values.
     */
    private boolean parseStatus(String status, int lineNumber) throws IOException {
        switch (status) {
            case STATUS_DONE:
                return true;
            case STATUS_NOT_DONE:
                return false;
            default:
                throw invalidData(lineNumber, "completion status must be 0 or 1");
        }
    }

    /**
     * Validates the record shape and reconstructs the appropriate task type.
     */
    private Task createTask(List<String> fields, String description, int lineNumber) throws IOException {
        switch (fields.get(TYPE_INDEX)) {
            case "T":
                requireFieldCount(fields, TODO_FIELD_COUNT, lineNumber);
                return new Todo(description);
            case "D":
                requireFieldCount(fields, DEADLINE_FIELD_COUNT, lineNumber);
                String deadline = requireText(fields.get(DEADLINE_INDEX), lineNumber, "deadline");
                return new Deadline(description, parseDeadline(deadline, lineNumber));
            case "E":
                requireFieldCount(fields, EVENT_FIELD_COUNT, lineNumber);
                String from = requireText(fields.get(EVENT_START_INDEX), lineNumber, "event start");
                String to = requireText(fields.get(EVENT_END_INDEX), lineNumber, "event end");
                try {
                    return new Event(description, from, to);
                } catch (IllegalArgumentException error) {
                    throw invalidData(lineNumber, error.getMessage());
                }
            default:
                throw invalidData(lineNumber, "unknown task type");
        }
    }

    /**
     * Parses the canonical ISO date-time stored for a deadline.
     */
    private LocalDateTime parseDeadline(String text, int lineNumber) throws IOException {
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException error) {
            throw invalidData(lineNumber, "deadline is not a valid ISO date-time");
        }
    }

    /**
     * Splits fields at unescaped pipe characters and restores escaped text.
     */
    private List<String> splitFields(String line, int lineNumber) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\\') {
                i++;
                field.append(readEscapedCharacter(line, i, lineNumber));
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Reads the character following a backslash, rejecting incomplete or unknown escapes.
     */
    private char readEscapedCharacter(String line, int position, int lineNumber) throws IOException {
        if (position == line.length()) {
            throw invalidData(lineNumber, "unfinished escape sequence");
        }
        char character = line.charAt(position);
        if (character != '\\' && character != '|') {
            throw invalidData(lineNumber, "invalid escape sequence");
        }
        return character;
    }

    /**
     * Escapes characters that have special meaning in the data-file format.
     */
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Ensures a task record contains exactly the fields required by its type.
     */
    private void requireFieldCount(List<String> fields, int expected, int lineNumber)
            throws IOException {
        if (fields.size() != expected) {
            throw invalidData(lineNumber, "wrong number of fields for this task type");
        }
    }

    /**
     * Ensures a required text field is not blank.
     */
    private String requireText(String text, int lineNumber, String fieldName)
            throws IOException {
        if (text.isBlank()) {
            throw invalidData(lineNumber, fieldName + " cannot be blank");
        }
        if (text.codePoints().anyMatch(character -> Character.isISOControl(character) && character != '\t'
                || character == 0x2028 || character == 0x2029)) {
            throw invalidData(lineNumber, fieldName + " contains control characters");
        }
        return text;
    }

    /**
     * Creates a consistent error for a corrupted line in the data file.
     */
    private IOException invalidData(int lineNumber, String reason) {
        return new IOException("Saved task data is invalid at line " + lineNumber
                + " (" + reason + ").");
    }
}
