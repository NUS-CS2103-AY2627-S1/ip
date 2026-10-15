package sine.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import sine.task.Deadline;
import sine.task.Event;
import sine.task.Task;
import sine.task.Todo;

/**
 * Loads and saves tasks using Sine's text-file format.
 */
public class Storage {
    private final Path dataFile;

    /**
     * Creates storage that uses the given OS-independent relative file path.
     *
     * @param filePath Path to the task data file.
     */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath);
    }

    /**
     * Loads tasks from the data file, or returns an empty list if it does not exist yet.
     *
     * @return Tasks stored during the previous run.
     * @throws IOException If the existing data file cannot be read or contains invalid data.
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(dataFile);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank()) {
                tasks.add(parseStoredTask(line, i + 1));
            }
        }
        return tasks;
    }

    /**
     * Rewrites the data file with the current task list.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(dataFile.getParent());
        List<String> lines = tasks.stream()
                .map(this::formatStoredTask)
                .toList();
        Files.write(dataFile, lines);
    }

    /**
     * Formats one task as a storage record, escaping text fields as needed.
     *
     * @param task Task to encode.
     * @return Storage record containing the task's type, status, and details.
     */
    private String formatStoredTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + encodeField(task.getDescription())
                    + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + encodeField(task.getDescription())
                    + " | " + encodeField(event.getFrom())
                    + " | " + encodeField(event.getTo());
        }
        return "T | " + status + " | " + encodeField(task.getDescription());
    }

    /**
     * Validates and converts one storage record into a task.
     *
     * @param line Storage record to parse.
     * @param lineNumber One-based line number used in error messages.
     * @return Task represented by the record.
     * @throws IOException If the record contains invalid task data.
     */
    private Task parseStoredTask(String line, int lineNumber) throws IOException {
        List<String> fields = splitStoredFields(line, lineNumber);
        validateStoredFields(fields, lineNumber);
        Task task = createStoredTask(fields, lineNumber);
        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Validates the status, field count, and required text before constructing a task.
     */
    private void validateStoredFields(List<String> fields, int lineNumber) throws IOException {
        if (fields.size() < 2
                || (!fields.get(1).equals("0") && !fields.get(1).equals("1"))) {
            throw invalidData(lineNumber);
        }

        int expectedFieldCount = getExpectedFieldCount(fields.get(0), lineNumber);
        if (fields.size() != expectedFieldCount) {
            throw invalidData(lineNumber);
        }
        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isBlank()) {
                throw invalidData(lineNumber);
            }
        }
    }

    /**
     * Returns the number of fields required by a supported storage task type.
     */
    private int getExpectedFieldCount(String taskType, int lineNumber) throws IOException {
        switch (taskType) {
            case "T":
                return 3;
            case "D":
                return 4;
            case "E":
                return 5;
            default:
                throw invalidData(lineNumber);
        }
    }

    /**
     * Constructs a task from validated fields, rejecting invalid deadline dates.
     */
    private Task createStoredTask(List<String> fields, int lineNumber) throws IOException {
        switch (fields.get(0)) {
            case "T":
                return new Todo(fields.get(2));
            case "D":
                try {
                    return new Deadline(fields.get(2), LocalDate.parse(fields.get(3)));
                } catch (DateTimeParseException exception) {
                    throw invalidData(lineNumber);
                }
            case "E":
                return new Event(fields.get(2), fields.get(3), fields.get(4));
            default:
                throw new AssertionError("Task type was validated above");
        }
    }

    /**
     * Splits a storage record while decoding escaped pipe and backslash characters.
     *
     * @param line Storage record to split.
     * @param lineNumber One-based line number used in error messages.
     * @return Decoded fields from the record.
     * @throws IOException If the record contains an invalid escape sequence.
     */
    private List<String> splitStoredFields(String line, int lineNumber) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\\') {
                if (i + 1 >= line.length()) {
                    throw invalidData(lineNumber);
                }
                char escapedCharacter = line.charAt(++i);
                if (escapedCharacter != '\\' && escapedCharacter != '|') {
                    throw invalidData(lineNumber);
                }
                field.append(escapedCharacter);
            } else if (line.startsWith(" | ", i)) {
                fields.add(field.toString());
                field.setLength(0);
                i += 2;
            } else {
                field.append(line.charAt(i));
            }
        }
        fields.add(field.toString());
        return fields;
    }

    /**
     * Creates a consistent error for a malformed line in the data file.
     *
     * @param lineNumber One-based line number containing invalid data.
     * @return Error describing the malformed record.
     */
    private IOException invalidData(int lineNumber) {
        return new IOException("Invalid task data on line " + lineNumber);
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     *
     * @param field Task text to encode.
     * @return Text safe to store as one field.
     */
    private String encodeField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }
}
