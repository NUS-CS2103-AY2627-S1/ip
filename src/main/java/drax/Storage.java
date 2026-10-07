package drax;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading, serializing, and saving drax.Drax tasks.
 */
public class Storage {
    private static final Path SAVE_FILE = Path.of("data", "drax.txt");
    private static final Path TEMP_FILE = Path.of("data", "drax.txt.tmp");

    /**
     * Loads saved tasks and reports malformed records as warnings.
     *
     * @return successfully loaded tasks together with warnings for skipped records
     * @throws IOException if the save file cannot be read
     */
    public LoadResult load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<String> warnings = new ArrayList<>();
        if (!Files.exists(SAVE_FILE)) {
            return new LoadResult(tasks, warnings);
        }
        List<String> lines = Files.readAllLines(SAVE_FILE);
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String currentLine = lines.get(lineNumber);
            if (currentLine.isBlank()) {
                continue;
            }
            try {
                tasks.add(deserialize(currentLine));
            } catch (IllegalArgumentException e) {
                warnings.add("Saved task on line " + (lineNumber + 1)
                        + " was ignored: " + e.getMessage());
            }
        }
        return new LoadResult(tasks, warnings);
    }

    /**
     * Saves tasks atomically using drax.Drax's established file format.
     *
     * @param tasks tasks to persist in their current order.
     * @throws IOException if the temporary or save file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(TEMP_FILE)) {
            writeSerializedTasks(tasks, writer);
        }
        replaceSaveFile();
    }

    /**
     * Serializes each task and writes it as a separate line.
     *
     * @param tasks tasks to write in their current order.
     * @param writer destination for the serialized task records.
     * @throws IOException if a task record cannot be written
     */
    private void writeSerializedTasks(TaskList tasks, BufferedWriter writer) throws IOException {
        for (Task task : tasks) {
            writer.write(serialize(task));
            writer.newLine();
        }
    }

    /**
     * Converts one task to an escaped, pipe-delimited save-file record to be saved.
     *
     * @param task task to store.
     * @return one save-file line representing the task
     * @throws IllegalArgumentException if the task subtype cannot be stored
     */
    private String serialize(Task task) {
        String isTaskDone = task.isDone() ? "1" : "0";
        return switch (task) {
            case Deadline deadline -> String.format("D | %s | %s | %s", isTaskDone,
                    escape(deadline.getTask()), escape(deadline.getDeadline().toString()));
            case Event event -> String.format("E | %s | %s | %s | %s", isTaskDone,
                    escape(event.getTask()), escape(event.getFrom().toString()), escape(event.getTo().toString()));
            case Todo todo -> String.format("T | %s | %s", isTaskDone, escape(todo.getTask()));
            default -> throw new IllegalArgumentException("Unsupported task type");
        };
    }

    /**
     * Reconstructs a task from one save-file record and restores its completion state.
     *
     * @param line one escaped, pipe-delimited save-file record.
     * @return the reconstructed task
     * @throws IllegalArgumentException if the record is malformed or unsupported
     */
    private Task deserialize(String line) {
        List<String> taskComponents = splitFields(line);
        String taskType = extractValue(taskComponents, 0, "task type");
        String taskStatus = extractValue(taskComponents, 1, "completion status");
        boolean isStatusValid = taskStatus.equals("0") || taskStatus.equals("1");
        if (!isStatusValid) {
            throw new IllegalArgumentException("completion status must be 0 or 1");
        }

        Task task = getTaskFromSaveFile(taskType, taskComponents);

        if (taskStatus.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates the task subtype identified by a parsed save-file record.
     *
     * @param taskType saved type identifier for the task.
     * @param taskComponents fields parsed from the save-file record.
     * @return a task reconstructed from the parsed fields
     * @throws IllegalArgumentException if the task type or its fields are invalid
     */
    private Task getTaskFromSaveFile(String taskType, List<String> taskComponents) {
        switch (taskType) {
            case "T" -> {
                checkFieldCount(taskComponents, 3);
                return new Todo(extractValue(taskComponents, 2, "task description"));
            }
            case "D" -> {
                checkFieldCount(taskComponents, 4);
                return new Deadline(extractValue(taskComponents, 2, "task description"),
                        ScheduleDateTime.parse(extractValue(taskComponents, 3, "deadline")));
            }
            case "E" -> {
                checkFieldCount(taskComponents, 5);
                return new Event(extractValue(taskComponents, 2, "task description"),
                        ScheduleDateTime.parse(extractValue(taskComponents, 3, "start time")),
                        ScheduleDateTime.parse(extractValue(taskComponents, 4, "end time")));
            }
            default -> throw new IllegalArgumentException("unknown task type " + taskType);
        }
    }

    /**
     * Escapes field separators and escape characters before a value is written to disk.
     *
     * @param value unescaped field value.
     * @return value safe to include in a pipe-delimited record
     */
    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Splits a save-file record into fields while preserving escaped separators.
     *
     * @param line escaped, pipe-delimited save-file record.
     * @return the unescaped fields in record order
     */
    private List<String> splitFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isPreviousCharBackslash = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (isPreviousCharBackslash) {
                appendEscapedCharacter(character, field);
                isPreviousCharBackslash = false;
            } else if (character == '\\') {
                isPreviousCharBackslash = true;
            } else if (character == '|') {
                assert isPreviousCharBackslash == false : "The previous character should not be a backslash";
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (isPreviousCharBackslash) {
            field.append('\\');
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Appends a character following an escape marker to the current field.
     *
     * @param character character following the escape marker.
     * @param field field being reconstructed.
     */
    private static void appendEscapedCharacter(char character, StringBuilder field) {
        if (character == '|' || character == '\\') {
            field.append(character);
        } else {
            field.append('\\').append(character);
        }
    }

    /**
     * Validates that a record contains the field count required for its task type.
     *
     * @param fields fields extracted from a record.
     * @param expectedCount required number of fields.
     * @throws IllegalArgumentException if the count differs
     */
    private void checkFieldCount(List<String> fields, int expectedCount) {
        if (fields.size() != expectedCount) {
            throw new IllegalArgumentException("expected " + expectedCount + " fields");
        }
    }

    /**
     * Extracts a required non-blank field from a parsed record.
     *
     * @param fields fields extracted from a record.
     * @param index zero-based index of the required field.
     * @param fieldName name used in a validation error.
     * @return the requested non-blank field value
     * @throws IllegalArgumentException if the field is absent or blank
     */
    private String extractValue(List<String> fields, int index, String fieldName) {
        if (index >= fields.size() || fields.get(index).isBlank()) {
            throw new IllegalArgumentException(fieldName + " is missing");
        }
        return fields.get(index);
    }

    /**
     * Contains loaded tasks together with warnings for skipped records.
     *
     * @param tasks successfully reconstructed tasks.
     * @param warnings explanations for records that could not be loaded.
     */
    public record LoadResult(ArrayList<Task> tasks, ArrayList<String> warnings) {
    }

    /**
     * Replaces the save file with the completed temporary file, preferring an atomic move.
     *
     * @throws IOException if the completed temporary file cannot replace the save file
     */
    private void replaceSaveFile() throws IOException {
        try {
            Files.move(TEMP_FILE, SAVE_FILE, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(TEMP_FILE, SAVE_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
