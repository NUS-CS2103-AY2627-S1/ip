package charliek.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

import charliek.exception.TaskStorageException;
import charliek.model.Deadline;
import charliek.model.Event;
import charliek.model.Task;
import charliek.model.TaskType;
import charliek.model.ToDo;
import charliek.parser.DateTimeParser;

/**
 * Encapsulates reading and writing CharlieK's task data.
 */
public class Storage {
    /**
     * Marker stored for an incomplete task.
     */
    private static final String INCOMPLETE_STATUS = "0";

    /**
     * Marker stored for a completed task.
     */
    private static final String COMPLETE_STATUS = "1";

    /**
     * Number of CSV fields stored for a to-do task.
     */
    private static final int TODO_FIELD_COUNT = 3;

    /**
     * Number of CSV fields stored for a deadline task.
     */
    private static final int DEADLINE_FIELD_COUNT = 4;

    /**
     * Number of CSV fields stored for an event task.
     */
    private static final int EVENT_FIELD_COUNT = 5;

    /**
     * The normalized absolute path of the task file.
     */
    private final Path taskFile;

    /**
     * Creates a storage service for a task file.
     *
     * @param taskFile the file used for task persistence.
     */
    public Storage(Path taskFile) {
        if (taskFile == null || taskFile.getFileName() == null) {
            throw new IllegalArgumentException("The task file path must name a file.");
        }
        this.taskFile = taskFile.toAbsolutePath().normalize();
    }

    /**
     * Loads all valid tasks from disk.
     *
     * <p>A missing file is treated as a new user's empty task list. Blank and
     * malformed CSV rows are ignored so one damaged record cannot prevent the
     * rest of the task list from loading.</p>
     *
     * @return the tasks found in the file.
     * @throws TaskStorageException when the file cannot be inspected or read.
     */
    public ArrayList<Task> load() throws TaskStorageException {
        try {
            if (Files.notExists(taskFile)) {
                return new ArrayList<>();
            }
            if (!Files.isRegularFile(taskFile)) {
                throw new TaskStorageException(
                        "I couldn't load saved tasks because the task file path is not a regular file.");
            }
            if (!Files.isReadable(taskFile)) {
                throw new TaskStorageException(
                        "I couldn't load saved tasks because the task file is not readable.");
            }

            ArrayList<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(taskFile, StandardCharsets.UTF_8)) {
                Task task = parseTask(parseCsvLineSafely(line));
                if (task != null && tasks.stream().noneMatch(existingTask -> existingTask.hasSameDetailsAs(task))) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (TaskStorageException exception) {
            throw exception;
        } catch (NoSuchFileException exception) {
            // A new user may create the file between the existence check and the read.
            return new ArrayList<>();
        } catch (IOException exception) {
            throw new TaskStorageException(
                    "I couldn't load saved tasks. Please check that data/charliek.csv exists and is readable.",
                    exception);
        } catch (RuntimeException exception) {
            throw new TaskStorageException(
                    "I couldn't load saved tasks because the saved data is invalid.", exception);
        }
    }

    /**
     * Loads saved tasks, creating a starter task file when this is a first run.
     *
     * <p>An existing file, including an intentionally empty one, always takes
     * precedence over the supplied defaults.</p>
     *
     * @param defaultTasks tasks to save for a new user.
     * @return the saved tasks, or the supplied defaults when the file was missing.
     * @throws TaskStorageException when starter tasks cannot be saved or the existing file cannot be loaded.
     */
    public ArrayList<Task> loadOrCreate(List<Task> defaultTasks) throws TaskStorageException {
        if (defaultTasks == null) {
            throw new IllegalArgumentException("The default task list cannot be null.");
        }
        try {
            if (Files.notExists(taskFile)) {
                save(defaultTasks);
                return new ArrayList<>(defaultTasks);
            }
            return load();
        } catch (TaskStorageException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new TaskStorageException(
                    "I couldn't inspect the task file. Please check that the data folder is accessible.", exception);
        }
    }

    /**
     * Saves the supplied task list as CSV rows, creating its parent directory if necessary.
     * A temporary file prevents a failed write from truncating a previously
     * valid task file.
     *
     * @param tasks the complete current task list.
     * @throws TaskStorageException when the path is not writable or the write fails.
     */
    public void save(List<Task> tasks) throws TaskStorageException {
        if (tasks == null) {
            throw new IllegalArgumentException("The task list cannot be null.");
        }

        Path temporaryFile = null;
        try {
            temporaryFile = writeTasksToTemporaryFile(tasks);
            moveIntoPlace(temporaryFile);
            temporaryFile = null;
        } catch (IOException exception) {
            throw new TaskStorageException(
                    "I couldn't save tasks. Please check that the data folder is writable.",
                    exception);
        } catch (RuntimeException exception) {
            throw new TaskStorageException(
                    "I couldn't save tasks. Please check that the data folder is writable.",
                    exception);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Serializes tasks and writes them to a temporary file before replacement.
     *
     * @param tasks the tasks to serialize.
     * @return the completed temporary file.
     * @throws IOException when the temporary file cannot be created or written.
     */
    private Path writeTasksToTemporaryFile(List<Task> tasks) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new IllegalArgumentException("The task list cannot contain null tasks.");
            }
            lines.add(toCsvLine(task));
        }

        Path parent = taskFile.getParent();
        // The constructor normalizes to an absolute file path, so a parent directory must exist conceptually.
        assert parent != null : "An absolute task-file path must have a parent directory.";
        Files.createDirectories(parent);
        if (Files.exists(taskFile) && !Files.isRegularFile(taskFile)) {
            throw new IOException("The task path is not a regular file.");
        }

        Path temporaryFile = Files.createTempFile(parent, "charliek", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return temporaryFile;
        } catch (IOException | RuntimeException exception) {
            deleteTemporaryFile(temporaryFile);
            throw exception;
        }
    }

    /**
     * Removes a temporary file when it is no longer needed.
     *
     * @param temporaryFile the temporary file to remove, or {@code null}.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original save error is more useful to the user.
        }
    }

    /**
     * Moves a fully written temporary file into the configured task-file path.
     */
    private void moveIntoPlace(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, taskFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException | AccessDeniedException exception) {
            Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts one task into type, status, and task-specific CSV columns.
     */
    private String toCsvLine(Task task) {
        ArrayList<String> fields = new ArrayList<>();
        fields.add(task.getStorageType());
        fields.add(task.isDone() ? COMPLETE_STATUS : INCOMPLETE_STATUS);
        fields.addAll(task.getStorageFields());

        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(escapeCsv(fields.get(i)));
        }
        return line.toString();
    }

    /**
     * Parses one CSV row, returning {@code null} for blank or malformed input.
     */
    private Task parseTask(List<String> fields) {
        if (fields == null || fields.size() < TODO_FIELD_COUNT) {
            return null;
        }

        TaskType type = TaskType.fromIcon(fields.get(0)).orElse(null);
        String status = fields.get(1);
        String description = fields.get(2);
        if (!(INCOMPLETE_STATUS.equals(status) || COMPLETE_STATUS.equals(status)) || description.isBlank()) {
            return null;
        }
        if (type == null) {
            return null;
        }

        try {
            Task task;
            switch (type) {
                case TODO:
                    if (fields.size() != TODO_FIELD_COUNT) {
                        return null;
                    }
                    task = new ToDo(description);
                    break;
                case DEADLINE:
                    if (fields.size() != DEADLINE_FIELD_COUNT || fields.get(3).isEmpty()) {
                        return null;
                    }
                    task = new Deadline(description, DateTimeParser.parseStored(fields.get(3)));
                    break;
                case EVENT:
                    if (fields.size() != EVENT_FIELD_COUNT || fields.get(3).isEmpty() || fields.get(4).isEmpty()) {
                        return null;
                    }
                    task = new Event(description,
                            DateTimeParser.parseStored(fields.get(3)),
                            DateTimeParser.parseStored(fields.get(4)));
                    break;
                default:
                    return null;
            }
            return restoreStatus(task, status);
        } catch (DateTimeException | IllegalArgumentException exception) {
            // A dated task with a malformed persisted value is not loadable.
            return null;
        }
    }

    /**
     * Restores completion status after constructing a task from its CSV row.
     */
    private Task restoreStatus(Task task, String status) {
        if (COMPLETE_STATUS.equals(status)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns a parsed row, or {@code null} when the CSV syntax is invalid.
     */
    private List<String> parseCsvLineSafely(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        try {
            return parseCsvLine(line);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Parses one CSV row, including quoted fields containing commas or quotes.
     */
    private List<String> parseCsvLine(String line) {
        CsvParserState state = new CsvParserState();
        for (int i = 0; i < line.length(); i++) {
            i = state.consume(line, i);
        }
        return state.finish();
    }

    /**
     * Holds mutable state while one CSV row is parsed.
     */
    private static final class CsvParserState {
        private final ArrayList<String> fields = new ArrayList<>();
        private final StringBuilder field = new StringBuilder();
        private boolean isInQuotes;
        private boolean hasClosedQuote;

        /**
         * Consumes the character at the given index.
         *
         * @param line the CSV row being parsed.
         * @param index the current character index.
         * @return the current index, or the index of a consumed escaped quote.
         */
        private int consume(String line, int index) {
            char current = line.charAt(index);
            if (isInQuotes) {
                return consumeQuotedCharacter(line, index, current);
            }
            if (current == ',') {
                fields.add(field.toString());
                field.setLength(0);
                hasClosedQuote = false;
            } else if (current == '"') {
                startQuotedField();
            } else {
                appendUnquotedCharacter(current);
            }
            return index;
        }

        /**
         * Consumes a character from a quoted field.
         */
        private int consumeQuotedCharacter(String line, int index, char current) {
            if (current != '"') {
                field.append(current);
                return index;
            }
            if (index + 1 < line.length() && line.charAt(index + 1) == '"') {
                field.append('"');
                return index + 1;
            }
            isInQuotes = false;
            hasClosedQuote = true;
            return index;
        }

        /**
         * Starts a quoted field after validating its position.
         */
        private void startQuotedField() {
            if (field.length() != 0 || hasClosedQuote) {
                throw new IllegalArgumentException("Unexpected quote in CSV row.");
            }
            isInQuotes = true;
        }

        /**
         * Appends an unquoted character after validating content following a quoted field.
         */
        private void appendUnquotedCharacter(char current) {
            if (hasClosedQuote && !Character.isWhitespace(current)) {
                throw new IllegalArgumentException("Unexpected content after quoted CSV field.");
            }
            field.append(current);
        }

        /**
         * Completes parsing and validates that all quoted fields are closed.
         *
         * @return the parsed fields.
         */
        private List<String> finish() {
            if (isInQuotes) {
                throw new IllegalArgumentException("Unclosed quoted CSV field.");
            }
            fields.add(field.toString());
            return fields;
        }
    }

    /**
     * Escapes a value when CSV syntax requires quoting.
     */
    private String escapeCsv(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A task CSV field cannot be null.");
        }
        if (value.indexOf(',') < 0 && value.indexOf('"') < 0
                && value.indexOf('\n') < 0 && value.indexOf('\r') < 0) {
            return value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
