package plana.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import plana.task.Deadline;
import plana.task.Event;
import plana.task.Task;
import plana.task.TaskList;
import plana.task.ToDo;

/**
 * Saves and loads Plana's task list using a file relative to the project root.
 */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String NOT_DONE_STATUS = "0";
    private static final String DONE_STATUS = "1";

    private final Path dataFile;

    /**
     * Creates storage using Plana's default data file.
     */
    public Storage() {
        this(Path.of("data", "plana.txt"));
    }

    /**
     * Creates storage using the supplied data-file path.
     *
     * @param filePath the file used to load and save tasks.
     */
    public Storage(String filePath) {
        this(Path.of(filePath));
    }

    /**
     * Creates storage backed by the supplied path.
     *
     * @param filePath the file used to load and save tasks.
     */
    private Storage(Path filePath) {
        this.dataFile = filePath;
    }

    /**
     * Writes the current task list to disk, creating the data directory when necessary.
     *
     * @param tasks the tasks that should be saved.
     * @return true when every task was written successfully.
     */
    public boolean saveTasks(TaskList tasks) {
        Path temporaryFile = null;
        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            String fileContents = serializeTasks(tasks);
            Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
            temporaryFile = Files.createTempFile(temporaryDirectory, "plana-", ".tmp");
            Files.writeString(temporaryFile, fileContents, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            reportStorageError("save", exception);
            return false;
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException exception) {
                    // The save has already completed or failed; cleanup is best-effort.
                }
            }
        }
        return true;
    }

    /**
     * Converts the supplied task list into file records while ignoring invalid entries.
     *
     * @param tasks the tasks to serialize.
     * @return the complete file contents
     */
    private String serializeTasks(TaskList tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return "";
        }

        StringBuilder fileContents = new StringBuilder();
        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            try {
                String record = task.toStorageString();
                if (record != null && !record.isBlank()) {
                    fileContents.append(record).append(System.lineSeparator());
                }
            } catch (RuntimeException exception) {
                reportStorageError("serialize a task", exception);
            }
        }
        return fileContents.toString();
    }

    /**
     * Loads the saved tasks, returning an empty list when no save file exists.
     * Malformed records are skipped so one corrupted record does not prevent the
     * remaining valid tasks from being loaded.
     *
     * @return the tasks loaded from disk
     */
    public TaskList loadTasks() {
        TaskList tasks = new TaskList();
        try {
            if (Files.notExists(dataFile)) {
                return tasks;
            }
            if (!Files.isRegularFile(dataFile)) {
                reportStorageError("load", new IOException("save path is not a regular file"));
                return tasks;
            }

            try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Task task = parseTask(line);
                    if (task != null && !tasks.containsSameTask(task)) {
                        tasks.add(task);
                    }
                }
            }
        } catch (IOException | SecurityException exception) {
            reportStorageError("load", exception);
        }
        return tasks;
    }

    /**
     * Escapes characters that have a special meaning in a storage record.
     *
     * @param value the field to escape.
     * @return the escaped field, or an empty field for {@code null}
     */
    public static String escapeField(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Reports a storage problem without terminating the chatbot.
     *
     * @param operation the operation that failed.
     * @param exception the failure that occurred.
     */
    private void reportStorageError(String operation, Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        System.err.println("Unable to " + operation + " tasks: " + message);
    }

    /**
     * Converts one saved line into a task.
     *
     * @param line a record from the save file.
     * @return the parsed task, or {@code null} when the record is malformed
     */
    private Task parseTask(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        List<String> parts = splitRecord(line);
        if (parts == null || parts.size() < 3) {
            return null;
        }

        String type = parts.get(0).trim();
        String status = parts.get(1).trim();
        String description = parts.get(2).trim();
        if (!isValidStatus(status) || description.isBlank()) {
            return null;
        }

        return switch (type) {
            case TODO_TYPE -> parseToDo(parts, description, status);
            case DEADLINE_TYPE -> parseDeadline(parts, description, status);
            case EVENT_TYPE -> parseEvent(parts, description, status);
            default -> null;
        };
    }

    /**
     * Checks whether a stored completion status is recognized.
     *
     * @param status the stored completion status.
     * @return true when the status represents a known completion state
     */
    private boolean isValidStatus(String status) {
        return status.equals(NOT_DONE_STATUS) || status.equals(DONE_STATUS);
    }

    /**
     * Parses a stored ToDo record.
     *
     * @param parts the fields in the stored record.
     * @param description the task description.
     * @param status the stored completion status.
     * @return the parsed ToDo, or {@code null} when the record shape is invalid
     */
    private Task parseToDo(List<String> parts, String description, String status) {
        if (parts.size() != 3) {
            return null;
        }
        return restoreCompletionStatus(new ToDo(description), status);
    }

    /**
     * Parses a stored deadline record.
     *
     * @param parts the fields in the stored record.
     * @param description the task description.
     * @param status the stored completion status.
     * @return the parsed deadline, or {@code null} when the record is invalid
     */
    private Task parseDeadline(List<String> parts, String description, String status) {
        if (parts.size() != 4 || parts.get(3).trim().isBlank()) {
            return null;
        }

        LocalDate dueDate = parseStoredDate(parts.get(3));
        if (dueDate == null) {
            return null;
        }
        return restoreCompletionStatus(new Deadline(description, dueDate), status);
    }

    /**
     * Parses a stored event record.
     *
     * @param parts the fields in the stored record.
     * @param description the task description.
     * @param status the stored completion status.
     * @return the parsed event, or {@code null} when the record is invalid
     */
    private Task parseEvent(List<String> parts, String description, String status) {
        if (parts.size() != 5 || parts.get(3).trim().isBlank() || parts.get(4).trim().isBlank()) {
            return null;
        }

        LocalDate startDate = parseStoredDate(parts.get(3));
        LocalDate endDate = parseStoredDate(parts.get(4));
        if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
            return null;
        }
        return restoreCompletionStatus(new Event(description, startDate, endDate), status);
    }

    /**
     * Parses a date from a stored task field.
     *
     * @param dateText the stored date text.
     * @return the parsed date, or {@code null} when the text is invalid
     */
    private LocalDate parseStoredDate(String dateText) {
        try {
            return LocalDate.parse(dateText.trim());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Restores the completion state recorded for a task.
     *
     * @param task the newly parsed task.
     * @param status the stored completion status.
     * @return the task with its stored completion state
     */
    private Task restoreCompletionStatus(Task task, String status) {
        assert task != null : "A valid storage record must produce a task.";
        if (status.equals(DONE_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a record on unescaped pipe characters.
     *
     * @param line a raw storage record.
     * @return the raw fields, or {@code null} when an escape is incomplete
     */
    private List<String> splitRecord(String line) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        boolean isEscaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                currentPart.append('\\').append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                String field = unescapeField(currentPart.toString().trim());
                if (field == null) {
                    return null;
                }
                parts.add(field);
                currentPart.setLength(0);
            } else {
                currentPart.append(character);
            }
        }
        if (isEscaped) {
            return null;
        }

        String field = unescapeField(currentPart.toString().trim());
        if (field == null) {
            return null;
        }
        parts.add(field);
        return parts;
    }

    /**
     * Reverses the escaping applied to a stored field.
     *
     * @param value the escaped field.
     * @return the original field, or {@code null} for an invalid escape sequence
     */
    private String unescapeField(String value) {
        StringBuilder unescaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character != '\\') {
                unescaped.append(character);
                continue;
            }
            if (i + 1 >= value.length()) {
                return null;
            }
            char escapedCharacter = value.charAt(++i);
            switch (escapedCharacter) {
                case '\\', '|' -> unescaped.append(escapedCharacter);
                case 'n' -> unescaped.append('\n');
                case 'r' -> unescaped.append('\r');
                default -> {
                    return null;
                }
            }
        }
        return unescaped.toString();
    }
}
