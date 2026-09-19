package xerxes.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import xerxes.task.Deadline;
import xerxes.task.Event;
import xerxes.task.Task;
import xerxes.task.TaskList;
import xerxes.task.ToDo;

/**
 * Persists task lists to a text file and reconstructs tasks from that file.
 */
public class TaskStorage {
    /** Path of the file used to store task data. */
    private final String saveFilePath;

    /**
     * Creates storage backed by the specified save file.
     *
     * @param saveFilePath Path of the save file.
     */
    public TaskStorage(String saveFilePath) {
        assert saveFilePath != null : "Task storage must have a save-file path";
        this.saveFilePath = saveFilePath;
    }

    /**
     * Writes every task in the given list to the save file.
     *
     * @param tasks Tasks to persist.
     * @throws IOException If the save file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        File saveFile = new File(this.saveFilePath);
        try (BufferedWriter saveFileWriter = new BufferedWriter(new FileWriter(saveFile))) {
            for (Task task : tasks) {
                saveFileWriter.write(encode(task));
                saveFileWriter.newLine();
            }
        }
    }

    /**
     * Creates the save file when necessary and loads each stored task from it.
     *
     * @return Tasks decoded from the save file.
     * @throws IOException If the save file cannot be created or read.
     * @throws IllegalArgumentException If a saved task is malformed.
     */
    public List<Task> load() throws IOException {
        Path filePath = Path.of(this.saveFilePath);
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new IOException("Unable to prepare save file: " + filePath, e);
        }
        List<Task> tasks = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(filePath);

            for (int lineNum = 0; lineNum < lines.size(); lineNum++) {
                String line = lines.get(lineNum);

                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(decode(line));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Malformed save file at line " + (lineNum + 1) + ": "
                                    + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new IOException("Unable to load save file, starting with an empty task list.");
        }
        return tasks;
    }

    /**
     * Escapes characters that have a special meaning in the save-file format.
     *
     * @param field Unescaped field value.
     * @return Escaped field value.
     */
    private String encodeField(String field) {
        return field.replace("\\", "\\\\")
                .replace("|", "\\|");
    }

    /**
     * Converts a task into a single line in the save-file format.
     *
     * @param task Task to encode.
     * @return Encoded task line.
     * @throws IllegalArgumentException If the task type is unsupported.
     */
    private String encode(Task task) {
        String status = task.isCompleted() ? "1" : "0";
        String description = encodeField(task.getTaskName());

        return switch (task) {
            case ToDo ignored -> String.join(
                    "|",
                    "TODO",
                    status,
                    description);
            case Deadline deadline -> String.join(
                    "|",
                    "DEADLINE",
                    status,
                    description,
                    encodeField(deadline.getDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            case Event event -> String.join(
                    "|",
                    "EVENT",
                    status,
                    description,
                    encodeField(event.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                    encodeField(event.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE)));

            case null -> throw new IllegalArgumentException("Task cannot be null");

            default -> throw new IllegalArgumentException(
                    "Unknown task type: " + task.getClass().getName());
        };
    }

    /**
     * Reconstructs a task from one encoded save-file line.
     *
     * @param line Encoded task line.
     * @return Decoded task.
     * @throws IllegalArgumentException If the line has an invalid format.
     */
    private Task decode(String line) {
        List<String> fields = splitEscape(line);
        if (fields.size() < 3) {
            throw new IllegalArgumentException("Not enough fields");
        }

        String type = fields.get(0);
        boolean isCompleted = switch (fields.get(1)) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new IllegalArgumentException("Invalid completion status");
        };

        String description = fields.get(2);

        Task task = switch (type) {
            case "TODO" -> {
                requireFieldCount(fields, 3);
                yield new ToDo(description);
            }
            case "DEADLINE" -> {
                requireFieldCount(fields, 4);
                yield new Deadline(description,
                        parseSavedDate(fields.get(3), "deadline"));
            }
            case "EVENT" -> {
                requireFieldCount(fields, 5);
                yield new Event(description,
                        parseSavedDate(fields.get(3), "event start time"),
                        parseSavedDate(fields.get(4), "event end time"));
            }
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        };

        if (isCompleted) {
            task.markCompleted();
        }

        return task;
    }

    /**
     * Verifies that an encoded task has exactly the expected number of fields.
     *
     * @param fields Decoded fields from the save-file line.
     * @param expected Required number of fields.
     * @throws IllegalArgumentException If the field count differs from {@code expected}.
     */
    private void requireFieldCount(List<String> fields, int expected) {
        if (fields.size() != expected) {
            throw new IllegalArgumentException("Incorrect number of fields");
        }
    }

    /**
     * Splits an encoded save-file line at unescaped separators and unescapes its fields.
     *
     * @param line Encoded task line.
     * @return Decoded fields in their original order.
     * @throws IllegalArgumentException If the line contains an invalid escape sequence.
     */
    public List<String> splitEscape(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaping = false;

        // Handle Escapes
        for (char character : line.toCharArray()) {
            if (isEscaping) {
                if (character != '|' && character != '\\') {
                    throw new IllegalArgumentException(
                            "Invalid Escape sequence : \\" + character);
                }
                currentField.append(character);
                isEscaping = false;
            } else if (character == '\\') {
                isEscaping = true;
            } else if (character == '|') {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }

        if (isEscaping) {
            throw new IllegalArgumentException(
                    "Save-file line ends with an incomplete escape");
        }

        fields.add(currentField.toString());
        return fields;
    }

    /**
     * Parses a date stored using the ISO local-date format.
     *
     * @param value Encoded date value.
     * @param fieldName Descriptive name used in any error message.
     * @return Parsed date.
     * @throws IllegalArgumentException If the value is not a valid ISO local date.
     */
    private LocalDate parseSavedDate(String value, String fieldName) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid " + fieldName + ": " + value, e);
        }
    }
}
