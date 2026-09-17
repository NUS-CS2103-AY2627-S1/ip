package nova.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.TaskDateTime;
import nova.task.Todo;

/**
 * Saves Nova's tasks to a text file on disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that writes to the given file.
     *
     * @param filePath path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file in their saved order.
     *
     * @return the saved tasks, or an empty list if no data file exists yet.
     * @throws NovaException if the data file cannot be read or contains invalid data.
     */
    public List<Task> load() throws NovaException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                tasks.add(parseTaskAtLine(line, i + 1));
            }
        } catch (IOException e) {
            throw new NovaException("I couldn't read the task data file. "
                    + "Starting with an empty task list.", e);
        }
        return tasks;
    }

    /**
     * Parses one storage line and adds its location to any validation error.
     *
     * @param line storage line to parse.
     * @param lineNumber one-based location in the data file.
     * @return task reconstructed from the line.
     * @throws NovaException if the line contains invalid task data.
     */
    private Task parseTaskAtLine(String line, int lineNumber) throws NovaException {
        try {
            return parseTask(line);
        } catch (NovaException e) {
            throw new NovaException("I couldn't load your saved tasks because line "
                    + lineNumber + " is invalid: " + e.getMessage()
                    + " Starting with an empty task list.", e);
        }
    }

    /**
     * Rewrites the data file so that it reflects the current task list.
     *
     * @param tasks current tasks in list order.
     * @throws NovaException if the directory or data file cannot be written.
     */
    public void save(List<Task> tasks) throws NovaException {
        Path temporaryFile = null;
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            } else {
                parentDirectory = Path.of(".");
            }

            List<String> taskLines = tasks.stream()
                    .map(Task::toFileString)
                    .toList();
            temporaryFile = Files.createTempFile(parentDirectory, "nova-", ".tmp");
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
            temporaryFile = null;
        } catch (IOException e) {
            throw new NovaException("I couldn't save the task data file. "
                    + "Your latest change was not kept.", e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // The original save error is more useful to the user than a cleanup error.
                }
            }
        }
    }

    /**
     * Reconstructs one task from its pipe-separated storage representation.
     *
     * @param line one line from the data file.
     * @return the reconstructed task.
     */
    private Task parseTask(String line) throws NovaException {
        List<String> fields = splitFields(line);
        validateTaskFields(fields);

        String taskType = fields.get(0);
        Task task = createTask(taskType, fields);
        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Validates the structure and common values of stored task fields.
     *
     * @param fields unescaped fields from one storage line.
     * @throws NovaException if required fields are missing or invalid.
     */
    private void validateTaskFields(List<String> fields) throws NovaException {
        if (fields.size() < 2) {
            throw new NovaException("the task type or completion state is missing.");
        }
        if (!fields.get(1).equals("0") && !fields.get(1).equals("1")) {
            throw new NovaException("the completion state must be 0 or 1.");
        }

        String taskType = fields.get(0);
        int expectedFieldCount = switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new NovaException("'" + taskType + "' is not a known task type.");
        };
        if (fields.size() != expectedFieldCount) {
            throw new NovaException("task type " + taskType + " needs "
                    + expectedFieldCount + " fields, but found " + fields.size() + ".");
        }
        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isBlank()) {
                throw new NovaException("task details cannot be empty.");
            }
        }
    }

    /**
     * Creates a task from fields whose common structure has been validated.
     *
     * @param taskType stored task type symbol.
     * @param fields validated task fields.
     * @return task reconstructed from the fields.
     * @throws NovaException if a stored date or time is invalid.
     */
    private Task createTask(String taskType, List<String> fields) throws NovaException {
        return switch (taskType) {
            case "T" -> new Todo(fields.get(2));
            case "D" -> createDeadline(fields);
            case "E" -> createEvent(fields);
            default -> {
                assert false : "Task type must have been validated before reconstruction";
                throw new IllegalStateException("Task type was already validated: " + taskType);
            }
        };
    }

    /**
     * Creates a deadline from validated storage fields.
     *
     * @param fields validated deadline fields.
     * @return reconstructed deadline.
     * @throws NovaException if the stored due value is invalid.
     */
    private Task createDeadline(List<String> fields) throws NovaException {
        TaskDateTime.ParsedValue due = TaskDateTime.parse(fields.get(3), "stored /by");
        return new Deadline(fields.get(2), due.dateTime(), due.hasTime());
    }

    /**
     * Creates an event from validated storage fields.
     *
     * @param fields validated event fields.
     * @return reconstructed event.
     * @throws NovaException if a stored endpoint is invalid or the event ends before it starts.
     */
    private Task createEvent(List<String> fields) throws NovaException {
        TaskDateTime.ParsedValue start = TaskDateTime.parse(fields.get(3), "stored /from");
        TaskDateTime.ParsedValue end = TaskDateTime.parse(fields.get(4), "stored /to");
        if (end.dateTime().isBefore(start.dateTime())) {
            throw new NovaException("the stored event ends before it starts.");
        }
        return new Event(fields.get(2), start.dateTime(), start.hasTime(),
                end.dateTime(), end.hasTime());
    }

    /**
     * Splits a storage line while preserving escaped pipes and backslashes in task text.
     *
     * @param line one line from the data file.
     * @return unescaped fields without separator padding.
     */
    private List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\\') {
                if (i + 1 >= line.length()) {
                    field.append(character);
                    continue;
                }
                char nextCharacter = line.charAt(i + 1);
                if (nextCharacter == '\\' || nextCharacter == '|') {
                    field.append(nextCharacter);
                    i++;
                } else {
                    field.append(character);
                }
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Replaces the old data file atomically when the file system supports it.
     *
     * @param temporaryFile completely written replacement data.
     * @throws IOException if the replacement cannot be moved into place.
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, filePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
