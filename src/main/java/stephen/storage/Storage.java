package stephen.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import stephen.task.Deadline;
import stephen.task.Event;
import stephen.task.Task;
import stephen.task.Todo;

/** Loads and saves the task list on disk. */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that writes to the given path.
     *
     * @param filePath location of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads every valid task from the data file.
     * Missing files, empty files, blank lines, and individual malformed lines are
     * treated as recoverable conditions.
     *
     * @return valid tasks in file order
     * @throws IOException if an existing file cannot be read
     */
    public List<Task> load() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        return lines.stream()
                .filter(line -> !line.isBlank())
                .map(this::parseTaskIfValid)
                .flatMap(Optional::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Tries to parse one saved task while treating a malformed record as absent.
     *
     * @param line serialized task record
     * @return parsed task, or an empty optional if the record is malformed
     */
    private Optional<Task> parseTaskIfValid(String line) {
        try {
            return Optional.of(parseTask(line));
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Replaces the data file with the current serialized task list.
     * The parent directory is created on the first save when necessary.
     *
     * @param tasks tasks to save in list order
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = tasks.stream().map(Task::toDataString).toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * Converts one save-file line back into its concrete task type.
     *
     * @param line serialized task record
     * @return task represented by the record
     * @throws IllegalArgumentException if the task type, field count, status, or escapes are invalid
     * @throws DateTimeParseException if a stored date is invalid
     */
    private Task parseTask(String line) {
        List<String> fields = splitEscapedFields(line);
        validateCommonFields(fields);
        String type = fields.get(0);
        String status = fields.get(1);
        String description = fields.get(2);
        Task task = createTask(fields, type, description);
        setCompletionStatus(task, status);
        return task;
    }

    /** Checks the fields shared by every serialized task type. */
    private void validateCommonFields(List<String> fields) {
        if (fields.size() < 3) {
            throw new IllegalArgumentException("Too few task fields");
        }
        String status = fields.get(1);
        if (fields.get(2).isEmpty() || (!status.equals("0") && !status.equals("1"))) {
            throw new IllegalArgumentException("Invalid task description or status");
        }
    }

    /** Creates the concrete task represented by the type-specific fields. */
    private Task createTask(List<String> fields, String type, String description) {
        if (type.equals("T") && fields.size() == 3) {
            return new Todo(description);
        } else if (type.equals("D") && fields.size() == 4 && !fields.get(3).isEmpty()) {
            return new Deadline(description,
                    LocalDate.parse(fields.get(3), Task.INPUT_DATE_FORMAT));
        } else if (type.equals("E") && fields.size() == 5
                && !fields.get(3).isEmpty() && !fields.get(4).isEmpty()) {
            return new Event(description,
                    LocalDate.parse(fields.get(3), Task.INPUT_DATE_FORMAT),
                    LocalDate.parse(fields.get(4), Task.INPUT_DATE_FORMAT));
        }
        throw new IllegalArgumentException("Invalid task type or field count");
    }

    /** Applies the serialized completion status to a newly created task. */
    private void setCompletionStatus(Task task, String status) {
        if (status.equals("1")) {
            task.markAsDone();
        }
    }

    /**
     * Splits pipe-delimited fields while decoding the save format's escapes.
     *
     * @param line serialized task record
     * @return decoded and trimmed record fields
     * @throws IllegalArgumentException if an escape sequence is unknown or incomplete
     */
    private List<String> splitEscapedFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                appendEscapedCharacter(field, character);
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

    /** Decodes one character following a storage escape marker. */
    private void appendEscapedCharacter(StringBuilder field, char character) {
        if (character == 'n') {
            field.append('\n');
        } else if (character == 'r') {
            field.append('\r');
        } else if (character == '|' || character == '\\') {
            field.append(character);
        } else {
            throw new IllegalArgumentException("Unknown escape sequence");
        }
    }
}
