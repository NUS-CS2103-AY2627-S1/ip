package larry.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import larry.task.Deadline;
import larry.task.Event;
import larry.task.Task;
import larry.task.TaskDateTime;
import larry.task.TaskList;
import larry.task.Todo;

/**
 * Loads and saves Larry's task list on disk.
 */
public class Storage {
    private final Path filePath;
    private final ArrayList<String> loadWarnings;

    /** Prevents a failed load from being overwritten by an empty task list. */
    private boolean isSaveBlocked;

    /**
     * Creates storage that reads from and writes to the given file path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
        this.loadWarnings = new ArrayList<>();
        this.isSaveBlocked = false;
    }

    /**
     * Loads valid tasks from the data file, or returns an empty list if it does not exist.
     * Malformed records are skipped and reported to the error stream.
     *
     * @return Tasks stored in the data file.
     * @throws IOException If the data file cannot be read.
     */
    public TaskList loadTasks() throws IOException {
        TaskList tasks = new TaskList();
        loadWarnings.clear();
        try {
            if (!Files.exists(filePath)) {
                isSaveBlocked = false;
                return tasks;
            }

            try (BufferedReader reader = openReader()) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                        line = line.substring(1);
                    }
                    if (line.isBlank()) {
                        continue;
                    }

                    try {
                        tasks.add(deserialize(line));
                    } catch (IllegalArgumentException e) {
                        loadWarnings.add("Rejected line "
                                + lineNumber + ": " + e.getMessage());
                    }
                }
            }
            isSaveBlocked = false;
            return tasks;
        } catch (IOException | SecurityException e) {
            isSaveBlocked = true;
            throw e;
        }
    }

    /**
     * Returns warnings produced while loading the current task list.
     *
     * @return Read-only copy of the loading warnings.
     */
    public List<String> getLoadWarnings() {
        return List.copyOf(loadWarnings);
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks Current tasks to save.
     * @throws IOException If the directory or data file cannot be written.
     */
    public void saveTasks(TaskList tasks) throws IOException {
        if (isSaveBlocked) {
            throw new IOException("Saving is disabled because the task file could not be loaded.");
        }

        Path absoluteFilePath = filePath.toAbsolutePath();
        Path parentDirectory = absoluteFilePath.getParent();
        Files.createDirectories(parentDirectory);

        List<String> taskLines = tasks.stream()
                .map(this::serialize)
                .toList();

        Path temporaryFile = Files.createTempFile(parentDirectory, "tasks-", ".tmp");
        try {
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            moveIntoPlace(temporaryFile, absoluteFilePath);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Opens the data file with strict UTF-8 validation.
     *
     * @return Reader for the task data file.
     * @throws IOException If the file cannot be opened.
     */
    private BufferedReader openReader() throws IOException {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        return new BufferedReader(new InputStreamReader(Files.newInputStream(filePath), decoder));
    }

    /**
     * Converts a data-file line to its corresponding task.
     *
     * @param line Serialized task data.
     * @return Task represented by the data-file line.
     * @throws IllegalArgumentException If the line contains invalid task data.
     */
    private Task deserialize(String line) {
        List<String> fields = parseFields(line);
        validateTaskFields(fields);
        Task task = createTask(fields);
        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Validates the structure and required values of a serialized task record.
     *
     * @param fields Unescaped fields from a serialized task record.
     * @throws IllegalArgumentException If the fields do not form a valid task record.
     */
    private void validateTaskFields(List<String> fields) {
        if (fields.size() < 2) {
            throw new IllegalArgumentException("missing task type or status");
        }

        String taskType = fields.get(0);
        int expectedFieldCount = switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("unknown task type '" + taskType + "'");
        };
        if (fields.size() != expectedFieldCount) {
            throw new IllegalArgumentException("wrong number of fields for task type " + taskType);
        }

        String status = fields.get(1);
        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("status must be 0 or 1");
        }
        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isBlank()) {
                throw new IllegalArgumentException("task details cannot be blank");
            }
        }
    }

    /**
     * Constructs a task from fields that have already passed record validation.
     *
     * @param fields Validated fields from a serialized task record.
     * @return Task represented by the fields.
     */
    private Task createTask(List<String> fields) {
        return switch (fields.get(0)) {
            case "T" -> new Todo(fields.get(2));
            case "D" -> new Deadline(fields.get(2),
                    TaskDateTime.fromStorageString(fields.get(3)));
            case "E" -> new Event(fields.get(2),
                    TaskDateTime.fromStorageString(fields.get(3)),
                    TaskDateTime.fromStorageString(fields.get(4)));
            default -> throw new AssertionError("Task type was already validated");
        };
    }

    /**
     * Splits a serialized record while unescaping backslashes and pipe characters.
     *
     * @param line Serialized task data.
     * @return Unescaped fields from the record.
     */
    private ArrayList<String> parseFields(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    field.append('\\');
                }
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (isFieldSeparator(line, i)) {
                field.setLength(field.length() - 1);
                fields.add(field.toString());
                field.setLength(0);
                i++;
            } else {
                field.append(character);
            }
        }

        if (isEscaped) {
            field.append('\\');
        }
        fields.add(field.toString());
        return fields;
    }

    /**
     * Checks whether a pipe character is the separator between two stored fields.
     *
     * @param line Serialized task data.
     * @param position Position of the character to check.
     * @return True when the character begins a field separator.
     */
    private boolean isFieldSeparator(String line, int position) {
        return line.charAt(position) == '|'
                && position > 0
                && position + 1 < line.length()
                && line.charAt(position - 1) == ' '
                && line.charAt(position + 1) == ' ';
    }

    /**
     * Moves a completed temporary data file over the previous data file.
     *
     * @param temporaryFile Completed temporary data file.
     * @param destination Destination of the task data.
     * @throws IOException If neither an atomic nor a regular replacement succeeds.
     */
    private void moveIntoPlace(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts a task to the compact line format stored in the data file.
     *
     * @param task Task to serialize.
     * @return One line containing the task type, status, and details.
     */
    private String serialize(Task task) {
        assert task instanceof Todo || task instanceof Deadline || task instanceof Event
                : "Only supported task types can be serialized";
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + escape(task.getDescription()) + " | "
                    + escape(deadline.getDueDateTime().toStorageString());
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + escape(task.getDescription()) + " | "
                    + escape(event.getStartDateTime().toStorageString()) + " | "
                    + escape(event.getEndDateTime().toStorageString());
        }
        return "T | " + status + " | " + escape(task.getDescription());
    }

    /**
     * Escapes characters that have structural meaning in the data format.
     *
     * @param value Field value to escape.
     * @return Escaped value safe to store in one record.
     */
    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }
}
