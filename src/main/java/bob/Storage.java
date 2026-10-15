package bob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bob.task.Deadline;
import bob.task.Event;
import bob.task.Priority;
import bob.task.Task;
import bob.task.Todo;

/**
 * Loads and saves Bob's tasks using a file on the local hard disk.
 */
public class Storage {
    private static final DateTimeFormatter EVENT_STORAGE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private final Path filePath;

    /**
     * Creates storage that writes to the given file.
     *
     * @param filePath location of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file.
     * An empty task list is returned when Bob has not created the file yet.
     *
     * @return tasks stored in the data file, in their original order
     * @throws StorageException if an existing data file cannot be read or is malformed
     */
    public List<Task> load() throws StorageException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        final List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException exception) {
            throw new StorageException("couldn't read the data file", exception);
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                Task task = parseTask(line);
                if (new bob.task.TaskList(tasks).containsDetails(task)) {
                    throw new IllegalArgumentException("Duplicate task");
                }
                tasks.add(task);
            } catch (IllegalArgumentException exception) {
                throw new StorageException("invalid data on line " + (i + 1), exception);
            }
        }
        return tasks;
    }

    /**
     * Reconstructs one task from its pipe-delimited storage fields.
     *
     * @param line stored task record
     * @return task reconstructed from the record
     * @throws IllegalArgumentException if the record is malformed
     */
    private Task parseTask(String line) {
        List<String> fieldList = splitFields(line);
        String[] fields = fieldList.toArray(String[]::new);
        if (fields.length < 3 || (!fields[1].equals("0") && !fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid completion status");
        }

        Task task = switch (fields[0]) {
            case "T" -> {
                Priority priority = parsePriority(fields, 3);
                requireText(fields[2]);
                yield new Todo(fields[2], priority);
            }
            case "D" -> {
                Priority priority = parsePriority(fields, 4);
                requireText(fields[2], fields[3]);
                yield new Deadline(fields[2], LocalDate.parse(fields[3]), priority);
            }
            case "E" -> {
                Priority priority = parsePriority(fields, 5);
                requireText(fields[2], fields[3], fields[4]);
                LocalDateTime start = LocalDateTime.parse(fields[3], EVENT_STORAGE_FORMAT);
                LocalDateTime end = LocalDateTime.parse(fields[4], EVENT_STORAGE_FORMAT);
                if (!end.isAfter(start)) {
                    throw new IllegalArgumentException("Event must end after it starts");
                }
                yield new Event(fields[2], start, end, priority);
            }
            default -> throw new IllegalArgumentException("Unknown task type: " + fields[0]);
        };

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a record while decoding escaped pipes and backslashes.
     *
     * @param line stored task record
     * @return decoded fields in their original order
     * @throws IllegalArgumentException if an escape sequence is invalid
     */
    private List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                if (character != '|' && character != '\\') {
                    throw new IllegalArgumentException("Invalid escape sequence");
                }
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            throw new IllegalArgumentException("Incomplete escape sequence");
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Reads an optional priority field while accepting legacy records.
     *
     * @param fields fields in the stored task record
     * @param expectedCount required field count
     * @return stored priority or NONE for a legacy record
     * @throws IllegalArgumentException if the field count or priority is invalid
     */
    private Priority parsePriority(String[] fields, int expectedCount) {
        if (fields.length == expectedCount) {
            return Priority.NONE;
        }
        if (fields.length != expectedCount + 1) {
            throw new IllegalArgumentException("Wrong number of fields");
        }
        return Priority.fromLabel(fields[expectedCount]);
    }

    /**
     * Validates that required stored fields contain non-whitespace text.
     *
     * @param values stored field values
     * @throws IllegalArgumentException if any value is blank
     */
    private void requireText(String... values) {
        if (Arrays.stream(values).anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("Task fields cannot be empty");
        }
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks current tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent == null) {
            parent = Path.of(".");
        }
        Files.createDirectories(parent);
        List<String> lines = tasks.stream()
                .map(Task::toStorageString)
                .toList();
        Path temporaryFile = Files.createTempFile(parent, "bob-", ".tmp");
        try {
            Files.write(temporaryFile, lines);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }
}
