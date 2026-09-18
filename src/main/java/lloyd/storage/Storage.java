package lloyd.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import lloyd.task.Deadline;
import lloyd.task.Event;
import lloyd.task.Task;
import lloyd.task.Todo;

/**
 * Saves tasks to and loads tasks from a plain-text file.
 *
 * <p>Each field is separated by {@code " | "}. The first field identifies the
 * task type, and the second is {@code 1} for a completed task or {@code 0} for
 * an incomplete task.</p>
 */
public class Storage {
    private static final String DELIMITER = " | ";
    private static final String DELIMITER_REGEX = " \\| ";

    private final Path filePath;

    /**
     * Creates storage at the supplied path.
     *
     * @param filePath Path of the text file used to store tasks.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks in their saved order.
     *
     * <p>If the storage file does not exist yet, its parent directory and an
     * empty file are created.</p>
     *
     * @return Tasks read from the file.
     * @throws IOException If the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (Files.notExists(filePath)) {
            createParentDirectory();
            Files.createFile(filePath);
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            tasks.add(parseTask(line, i + 1));
        }
        return tasks;
    }

    /**
     * Replaces the storage file with the current ordered task list.
     *
     * @param tasks Current tasks to save.
     * @throws IOException If a directory or file cannot be written.
     * @throws IllegalArgumentException If the task list contains unsupported data.
     */
    public void save(List<Task> tasks) throws IOException {
        if (tasks == null) {
            throw new IllegalArgumentException("Task list cannot be null");
        }

        List<String> lines = tasks.stream()
                .map(this::formatTask)
                .toList();

        createParentDirectory();
        Path parentDirectory = filePath.toAbsolutePath().getParent();
        Path temporaryFile = Files.createTempFile(
                parentDirectory, filePath.getFileName().toString(), ".tmp");

        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceStorageFile(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Creates the storage file's parent directory when necessary.
     *
     * @throws IOException If the directory cannot be created.
     */
    private void createParentDirectory() throws IOException {
        Path parentDirectory = filePath.toAbsolutePath().getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }

    /**
     * Replaces the old file atomically when the operating system supports it.
     *
     * @param temporaryFile Fully written temporary file to move into place.
     * @throws IOException If the storage file cannot be replaced.
     */
    private void replaceStorageFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, filePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts one task to its storage representation.
     *
     * @param task Task to convert.
     * @return One line in the storage file format.
     * @throws IllegalArgumentException If the task or one of its fields is invalid.
     */
    private String formatTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task list cannot contain null tasks");
        }

        String status = task.isDone() ? "1" : "0";
        String description = validateField(task.getDescription());

        if (task instanceof Todo) {
            return String.join(DELIMITER, "T", status, description);
        }
        if (task instanceof Deadline deadline) {
            return String.join(DELIMITER, "D", status, description,
                    validateField(deadline.getBy().toString()));
        }
        if (task instanceof Event event) {
            return String.join(DELIMITER, "E", status, description,
                    validateField(event.getFrom().toString()),
                    validateField(event.getTo().toString()));
        }

        throw new IllegalArgumentException(
                "Unsupported task type: " + task.getClass().getSimpleName());
    }

    /**
     * Recreates one task from a line in the storage file.
     *
     * @param line Saved task line to parse.
     * @param lineNumber One-based source line number used in error messages.
     * @return Task represented by the line.
     * @throws IOException If the line does not contain valid task data.
     */
    private Task parseTask(String line, int lineNumber) throws IOException {
        String[] fields = line.split(DELIMITER_REGEX, -1);

        try {
            Task task = switch (fields[0]) {
                case "T" -> {
                    requireFieldCount(fields, 3);
                    yield new Todo(validateField(fields[2]));
                }
                case "D" -> {
                    requireFieldCount(fields, 4);
                    yield new Deadline(validateField(fields[2]),
                            LocalDate.parse(validateField(fields[3])));
                }
                case "E" -> {
                    requireFieldCount(fields, 5);
                    yield new Event(validateField(fields[2]),
                            LocalDateTime.parse(validateField(fields[3])),
                            LocalDateTime.parse(validateField(fields[4])));
                }
                default -> throw new IllegalArgumentException("unknown task type");
            };

            if (fields[1].equals("1")) {
                task.mark();
            } else if (!fields[1].equals("0")) {
                throw new IllegalArgumentException("status must be 0 or 1");
            }
            return task;
        } catch (DateTimeParseException | IllegalArgumentException
                | ArrayIndexOutOfBoundsException e) {
            throw new IOException("Invalid task data on line " + lineNumber + ": " + line, e);
        }
    }

    /**
     * Ensures that a saved line has the correct number of fields.
     *
     * @param fields Fields parsed from the line.
     * @param expectedCount Required number of fields.
     * @throws IllegalArgumentException If the field count is incorrect.
     */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("incorrect number of fields");
        }
    }

    /**
     * Ensures that task data cannot break the line-oriented storage format.
     *
     * @param field Field to validate.
     * @return The unchanged valid field.
     * @throws IllegalArgumentException If the field is empty or contains a reserved character.
     */
    private String validateField(String field) {
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException("Task fields cannot be empty");
        }
        if (field.contains("|") || field.contains("\n") || field.contains("\r")) {
            throw new IllegalArgumentException(
                    "Task fields cannot contain | or line-break characters");
        }
        return field;
    }
}
