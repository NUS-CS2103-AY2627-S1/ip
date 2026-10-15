package lumi.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import lumi.task.Deadline;
import lumi.task.Event;
import lumi.task.Task;
import lumi.task.Todo;

/**
 * Loads and saves Lumi tasks in a readable, pipe-delimited text file.
 * Backslashes and pipe characters inside task details are escaped so that user
 * text cannot accidentally change the file structure.
 */
public class Storage {
    /** Location of the task data file. */
    private final Path dataFile;

    /**
     * Creates storage backed by the given file.
     *
     * @param dataFile Path of the task data file.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads all valid tasks from disk. A missing file represents an empty task
     * list, while malformed non-empty lines are counted and skipped.
     *
     * @return The valid tasks and number of malformed lines skipped.
     * @throws IOException If the existing file cannot be read.
     */
    public LoadResult load() throws IOException {
        if (Files.notExists(dataFile)) {
            return new LoadResult(List.of(), 0);
        }

        ArrayList<Task> tasks = new ArrayList<>();
        int skippedLineCount = 0;
        for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException error) {
                skippedLineCount++;
            }
        }
        return new LoadResult(List.copyOf(tasks), skippedLineCount);
    }

    /**
     * Saves the complete task list, creating the parent directory when needed.
     * Rewriting the complete list keeps the file synchronized after additions,
     * status changes, and deletions.
     *
     * @param tasks Current task list.
     * @throws IOException If the directory or data file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentDirectory = dataFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }
        Files.write(dataFile, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    /**
     * Converts one task to its disk representation.
     *
     * @param task Task to serialize.
     * @return Pipe-delimited task record.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return String.join(" | ", "T", status, escape(task.getDescription()));
        }
        if (task instanceof Deadline deadline) {
            return String.join(" | ", "D", status, escape(task.getDescription()),
                    escape(deadline.getBy().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        }
        if (task instanceof Event event) {
            return String.join(" | ", "E", status, escape(task.getDescription()),
                    escape(event.getFrom().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
                    escape(event.getTo().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }

    /**
     * Parses one task record and rejects invalid types, statuses, field counts,
     * empty required fields, and malformed escape sequences.
     *
     * @param line One non-empty line from the data file.
     * @return Task represented by the line.
     * @throws IllegalArgumentException If the line is malformed.
     */
    private Task parseTask(String line) {
        List<String> fields = splitFields(line);
        if (fields.size() < 3) {
            throw new IllegalArgumentException("A task record needs at least three fields");
        }

        boolean isDone = parseDoneStatus(fields.get(1));
        String description = requireText(fields.get(2));
        Task task = createTask(fields, description);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /** Converts a stored status field into its boolean representation. */
    private boolean parseDoneStatus(String status) {
        switch (status) {
            case "1":
                return true;
            case "0":
                return false;
            default:
                throw new IllegalArgumentException("Invalid task status");
        }
    }

    /** Creates the task represented by validated record fields. */
    private Task createTask(List<String> fields, String description) {
        switch (fields.get(0)) {
            case "T":
                requireFieldCount(fields, 3);
                return new Todo(description);
            case "D":
                requireFieldCount(fields, 4);
                return new Deadline(description, parseStoredDateTime(fields.get(3)));
            case "E":
                requireFieldCount(fields, 5);
                return new Event(description, parseStoredDateTime(fields.get(3)),
                        parseStoredDateTime(fields.get(4)));
            default:
                throw new IllegalArgumentException("Invalid task type");
        }
    }

    /**
     * Escapes characters that have structural meaning in the storage format.
     *
     * @param value Task text to escape.
     * @return Safely encoded field text.
     */
    private String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Splits a record at unescaped pipe characters and decodes escaped text.
     *
     * @param line Task record to split.
     * @return Decoded fields with surrounding format spaces removed.
     * @throws IllegalArgumentException If an escape sequence is incomplete or unknown.
     */
    private List<String> splitFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                appendEscapedCharacter(field, character);
                isEscaped = false;
                continue;
            }

            switch (character) {
                case '\\':
                    isEscaped = true;
                    break;
                case '|':
                    fields.add(field.toString().trim());
                    field.setLength(0);
                    break;
                default:
                    field.append(character);
            }
        }

        if (isEscaped) {
            throw new IllegalArgumentException("Incomplete escape sequence");
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /** Appends the character represented by a supported escape sequence. */
    private void appendEscapedCharacter(StringBuilder field, char character) {
        switch (character) {
            case '\\':
            case '|':
                field.append(character);
                break;
            case 'n':
                field.append('\n');
                break;
            case 'r':
                field.append('\r');
                break;
            default:
                throw new IllegalArgumentException("Unknown escape sequence");
        }
    }

    /** Ensures a parsed task record has exactly the required number of fields. */
    private void requireFieldCount(List<String> fields, int expectedCount) {
        if (fields.size() != expectedCount) {
            throw new IllegalArgumentException("Unexpected field count");
        }
    }

    /** Returns non-blank task text or rejects the malformed field. */
    private String requireText(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("Required task text is empty");
        }
        return value;
    }

    /**
     * Parses the canonical ISO date-time format used in saved task records.
     *
     * @param value Stored date-time field.
     * @return Parsed date and time.
     * @throws IllegalArgumentException If the field is empty or invalid.
     */
    private LocalDateTime parseStoredDateTime(String value) {
        try {
            return LocalDateTime.parse(requireText(value), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("Invalid stored date and time", error);
        }
    }

    /**
     * Contains the successfully loaded tasks and corruption information.
     *
     * @param tasks Valid tasks loaded from the data file.
     * @param skippedLineCount Number of malformed non-empty lines skipped.
     */
    public record LoadResult(List<Task> tasks, int skippedLineCount) {
    }
}
