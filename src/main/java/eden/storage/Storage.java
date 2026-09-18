package eden.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import eden.exception.EdenException;
import eden.task.Deadline;
import eden.task.Event;
import eden.task.Task;
import eden.task.Todo;

/**
 * Loads tasks from and saves tasks to a text file on disk.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates a storage manager for the given file.
     *
     * @param filePath relative or absolute path to the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file. A missing file represents an empty task list,
     * and blank lines in an existing file are ignored.
     *
     * @return tasks reconstructed from the file.
     * @throws EdenException if the file cannot be read or contains invalid data.
     */
    public List<Task> load() throws EdenException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    tasks.add(parseTask(lines.get(i), i + 1));
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new EdenException("OOPS!!! I couldn't read the task data from "
                    + filePath + ".", exception);
        }
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks tasks to persist.
     * @throws EdenException if the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws EdenException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> lines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new EdenException("OOPS!!! I couldn't save the task data to "
                    + filePath + ".", exception);
        }
    }

    /**
     * Reconstructs one task from its stored representation.
     */
    private Task parseTask(String line, int lineNumber) throws EdenException {
        List<String> fields = splitFields(line, lineNumber);
        if (fields.size() < 3) {
            throw invalidData(lineNumber);
        }

        boolean isMarked = parseStatus(fields.get(1), lineNumber);
        Task task;
        switch (fields.get(0)) {
            case "T":
                requireFieldCount(fields, 3, lineNumber);
                task = new Todo(requireText(fields.get(2), lineNumber));
                break;
            case "D":
                requireFieldCount(fields, 4, lineNumber);
                task = new Deadline(requireText(fields.get(2), lineNumber),
                        parseDeadlineDate(requireText(fields.get(3), lineNumber), lineNumber));
                break;
            case "E":
                requireFieldCount(fields, 5, lineNumber);
                task = new Event(requireText(fields.get(2), lineNumber),
                        requireText(fields.get(3), lineNumber),
                        requireText(fields.get(4), lineNumber));
                break;
            default:
                throw invalidData(lineNumber);
        }

        if (isMarked) {
            task.mark();
        }
        assert task.isMarked() == isMarked
                : "A loaded task must retain its stored completion status";
        return task;
    }

    /**
     * Splits a stored line at unescaped pipes and restores escaped pipes and backslashes.
     */
    private List<String> splitFields(String line, int lineNumber) throws EdenException {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw invalidData(lineNumber);
                }
                currentField.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }

        if (isEscaped) {
            throw invalidData(lineNumber);
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    /**
     * Parses a stored deadline date in ISO format.
     *
     * @param dateText stored deadline date.
     * @param lineNumber physical line containing the date.
     * @return parsed date.
     * @throws EdenException if the stored date is invalid.
     */
    private LocalDate parseDeadlineDate(String dateText, int lineNumber) throws EdenException {
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException exception) {
            throw invalidDeadlineDate(lineNumber);
        }
    }

    /**
     * Converts the stored 0/1 completion flag to a boolean.
     */
    private boolean parseStatus(String status, int lineNumber) throws EdenException {
        if ("1".equals(status)) {
            return true;
        }
        if ("0".equals(status)) {
            return false;
        }
        throw invalidData(lineNumber);
    }

    /**
     * Checks that a stored task has exactly the fields required by its task type.
     */
    private void requireFieldCount(List<String> fields, int expected, int lineNumber)
            throws EdenException {
        if (fields.size() != expected) {
            throw invalidData(lineNumber);
        }
    }

    /**
     * Checks that a required stored field contains text.
     */
    private String requireText(String text, int lineNumber) throws EdenException {
        if (text.isBlank()) {
            throw invalidData(lineNumber);
        }
        return text;
    }

    /**
     * Creates a consistent error for a malformed line in the data file.
     */
    private EdenException invalidData(int lineNumber) {
        return new EdenException("OOPS!!! The task data in " + filePath
                + " is invalid at line " + lineNumber + ". Expected fields separated by '"
                + FIELD_SEPARATOR + "'.");
    }

    /**
     * Creates a consistent error for an invalid stored deadline date.
     */
    private EdenException invalidDeadlineDate(int lineNumber) {
        return new EdenException("OOPS!!! The deadline date in " + filePath
                + " is invalid at line " + lineNumber + ". Expected yyyy-MM-dd.");
    }
}
