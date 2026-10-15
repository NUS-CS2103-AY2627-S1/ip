package jonk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from a data file and saves tasks back to that file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads all tasks, or returns an empty list if the data file does not exist.
     *
     * @return Tasks stored during the previous run.
     * @throws JonkException If the file cannot be read or contains invalid task data.
     */
    public List<Task> load() throws JonkException {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            throw new JonkException("Could not load tasks from " + filePath
                    + ". Starting with an empty task list.", e);
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseTask(line));
            } catch (JonkException | DateTimeParseException e) {
                throw new JonkException("Could not load tasks from " + filePath
                        + ": invalid data at line " + (i + 1) + " (" + e.getMessage()
                        + "). Starting with an empty task list.", e);
            }
        }
        return tasks;
    }

    /**
     * Rewrites the data file so it represents the supplied tasks.
     *
     * @param tasks Current tasks to save.
     * @throws JonkException If the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws JonkException {
        Path temporaryFile = null;

        try {
            Files.createDirectories(filePath.getParent());
            temporaryFile = Files.createTempFile(filePath.getParent(), "jonk-", ".tmp");
            Files.write(temporaryFile, tasks.stream().map(Task::toFileString).toList(),
                    StandardCharsets.UTF_8);

            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            deleteTemporaryFile(temporaryFile);
            throw new JonkException("Could not save tasks to " + filePath
                    + ". Your latest change was not applied.", e);
        }
    }

    /**
     * Removes a temporary save file after a failed write without hiding the original error.
     *
     * @param temporaryFile Temporary file to remove, or {@code null} if none was created.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original save error is more useful to the user than a cleanup error.
        }
    }

    /**
     * Recreates one task from its pipe-separated data-file representation.
     *
     * @param line Saved task data.
     * @return Recreated task.
     * @throws JonkException If any saved field is missing or invalid.
     */
    private Task parseTask(String line) throws JonkException {
        List<String> fields = splitFileFields(line);
        validateTaskFields(fields);
        return createTask(fields);
    }

    /**
     * Validates the saved task's type, field count, completion status, and required details.
     *
     * @param fields Decoded fields, including at least the task type field.
     * @throws JonkException If any saved field is missing or invalid.
     */
    private void validateTaskFields(List<String> fields) throws JonkException {
        String taskType = fields.getFirst();
        int expectedFieldCount = switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new JonkException("unknown task type '" + taskType + "'");
        };

        if (fields.size() != expectedFieldCount) {
            throw new JonkException("task type " + taskType + " requires "
                    + expectedFieldCount + " fields, but found " + fields.size());
        }

        String status = fields.get(1);
        if (!status.equals("0") && !status.equals("1")) {
            throw new JonkException("completion status must be 0 or 1");
        }

        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isBlank()) {
                throw new JonkException("task details cannot be empty");
            }
        }
    }

    /**
     * Creates a task and restores its completion status from structurally validated fields.
     *
     * @param fields Saved fields that have passed {@link #validateTaskFields(List)}.
     * @return Recreated task.
     * @throws DateTimeParseException If a saved date is invalid.
     */
    private Task createTask(List<String> fields) {
        String taskType = fields.getFirst();
        Task task = switch (taskType) {
            case "T" -> new Todo(fields.get(2));
            case "D" -> new Deadline(fields.get(2), fields.get(3));
            case "E" -> new Event(fields.get(2), fields.get(3), fields.get(4));
            default -> throw new AssertionError("Task type must be validated before construction");
        };

        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits saved data at unescaped pipe characters and restores escaped text.
     *
     * @param line Saved task data.
     * @return Decoded fields without separator padding.
     */
    private List<String> splitFileFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '\\' && i + 1 < line.length()
                    && (line.charAt(i + 1) == '\\' || line.charAt(i + 1) == '|')) {
                currentField.append(line.charAt(++i));
            } else if (current == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(current);
            }
        }

        fields.add(currentField.toString().trim());
        return fields;
    }
}
