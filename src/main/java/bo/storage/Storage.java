package bo.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import bo.command.CommandException;
import bo.task.Deadline;
import bo.task.Event;
import bo.task.Task;
import bo.task.Todo;

/**
 * Saves Bo's tasks to a text file.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "bo.txt");

    private final Path filePath;

    /**
     * Creates storage that saves to Bo's default data file
     * ({@code data/bo.txt}).
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage that saves to a specific file. Intended mainly for
     * tests, where a temporary file should be used instead of Bo's real
     * save file.
     *
     * @param filePath The file to read from and write to.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Creates the data directory and an empty save file if they do not exist.
     * Existing save data is not changed.
     *
     * @throws IOException If the directory or save file cannot be created.
     */
    public void initialize() throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }
        try {
            Files.createFile(filePath);
        } catch (FileAlreadyExistsException exception) {
            // The existing save file must be preserved.
        }
    }

    /**
     * Saves every task in a simple, line-based format.
     *
     * @param tasks The tasks to save.
     * @throws IOException If the data directory or save file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        initialize();
        List<String> taskLines = tasks.stream()
                .map(Task::toFileString)
                .toList();
        Path parentDir = filePath.getParent() != null ? filePath.getParent() : Path.of(".");
        Path temporaryFile = Files.createTempFile(parentDir, "bo-", ".tmp");
        try {
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads tasks from the save file.
     *
     * @return The tasks stored in the save file.
     * @throws IOException If the save file cannot be read.
     * @throws CommandException If a saved task has an invalid format.
     */
    public List<Task> load() throws IOException, CommandException {
        initialize();
        List<String> taskLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < taskLines.size(); i++) {
            String taskLine = taskLines.get(i).trim();
            if (!taskLine.isEmpty()) {
                tasks.add(parseTask(taskLine, i + 1));
            }
        }
        return tasks;
    }

    /**
     * Converts one save-file line into a task object.
     *
     * @param taskLine One line from the save file.
     * @param lineNumber The line number used in error reporting.
     * @return The task represented by the line.
     * @throws CommandException If the line has an invalid format.
     */
    private Task parseTask(String taskLine, int lineNumber) throws CommandException {
        String[] fields = taskLine.split("\\s*\\|\\s*", -1);
        assert fields.length > 0 && !fields[0].isEmpty()
                : "A non-empty saved line must contain a task type field";
        Task task;
        try {
            task = switch (fields[0]) {
                case "T" -> createTodo(fields, lineNumber);
                case "D" -> createDeadline(fields, lineNumber);
                case "E" -> createEvent(fields, lineNumber);
                default -> throw invalidSavedTask(lineNumber);
            };
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException exception) {
            throw invalidSavedTask(lineNumber);
        }

        if (fields.length < 2 || fields[1].isEmpty()) {
            throw invalidSavedTask(lineNumber);
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw invalidSavedTask(lineNumber);
        }
        return task;
    }

    /**
     * Creates a to-do from save-file fields.
     *
     * @param fields The fields stored for the task.
     * @param lineNumber The line number used in error reporting.
     * @return The created to-do.
     * @throws CommandException If the fields are invalid.
     */
    private Todo createTodo(String[] fields, int lineNumber) throws CommandException {
        validateFieldCount(fields, 3, lineNumber);
        validateNonEmptyFields(fields, lineNumber, 2);
        return new Todo(fields[2]);
    }

    /**
     * Creates a deadline from save-file fields.
     *
     * @param fields The fields stored for the task.
     * @param lineNumber The line number used in error reporting.
     * @return The created deadline.
     * @throws CommandException If the fields are invalid.
     */
    private Deadline createDeadline(String[] fields, int lineNumber) throws CommandException {
        validateFieldCount(fields, 4, lineNumber);
        validateNonEmptyFields(fields, lineNumber, 2, 3);
        return new Deadline(fields[2], LocalDate.parse(fields[3]));
    }

    /**
     * Creates an event from save-file fields.
     *
     * @param fields The fields stored for the task.
     * @param lineNumber The line number used in error reporting.
     * @return The created event.
     * @throws CommandException If the fields are invalid.
     */
    private Event createEvent(String[] fields, int lineNumber) throws CommandException {
        validateFieldCount(fields, 5, lineNumber);
        validateNonEmptyFields(fields, lineNumber, 2, 3, 4);
        return new Event(fields[2],
                LocalDateTime.parse(fields[3]), LocalDateTime.parse(fields[4]));
    }

    /**
     * Checks that a saved task has exactly the expected number of fields.
     *
     * @param fields The fields stored for the task.
     * @param expectedCount The required number of fields.
     * @param lineNumber The line number used in error reporting.
     * @throws CommandException If the field count is incorrect.
     */
    private void validateFieldCount(String[] fields, int expectedCount, int lineNumber)
            throws CommandException {
        if (fields.length != expectedCount) {
            throw invalidSavedTask(lineNumber);
        }
    }

    /**
     * Checks that the specified saved task fields are not empty.
     *
     * @param fields The fields stored for the task.
     * @param lineNumber The line number used in error reporting.
     * @param fieldIndexes The indexes of required non-empty fields.
     * @throws CommandException If any required field is empty.
     */
    private void validateNonEmptyFields(String[] fields, int lineNumber, int... fieldIndexes)
            throws CommandException {
        for (int fieldIndex : fieldIndexes) {
            if (fields[fieldIndex].isEmpty()) {
                throw invalidSavedTask(lineNumber);
            }
        }
    }

    /**
     * Creates a consistent error for malformed save-file data.
     *
     * @param lineNumber The invalid line number, or zero when it is not available.
     * @return The loading exception.
     */
    private CommandException invalidSavedTask(int lineNumber) {
        if (lineNumber > 0) {
            return new CommandException("The saved task on line " + lineNumber + " is invalid.");
        }
        return new CommandException("A saved task has an invalid format.");
    }
}
