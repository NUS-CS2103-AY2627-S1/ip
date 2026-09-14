package arrodes.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import arrodes.exception.ArrodesException;
import arrodes.task.Deadline;
import arrodes.task.Event;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.task.Todo;


/**
 * Saves Arrodes tasks to a plain-text file.
 *
 * <p>Each line stores the task type, completion status, description, tag, and
 * any type-specific time information.</p>
 */
public class Storage {
    /** Location of the task data relative to the project root. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "arrodes.txt");
    /** Record type used for todo tasks in the storage format. */
    private static final String TODO_RECORD_TYPE = "T";
    /** Record type used for deadline tasks in the storage format. */
    private static final String DEADLINE_RECORD_TYPE = "D";
    /** Record type used for event tasks in the storage format. */
    private static final String EVENT_RECORD_TYPE = "E";
    /** Status value used for completed tasks in the storage format. */
    private static final String COMPLETED_STATUS = "1";
    /** Status value used for incomplete tasks in the storage format. */
    private static final String INCOMPLETE_STATUS = "0";
    /** Number of fields stored for a todo record, including its tag. */
    private static final int TODO_FIELD_COUNT = 4;
    /** Number of fields stored for a deadline record, including its tag. */
    private static final int DEADLINE_FIELD_COUNT = 5;
    /** Number of fields stored for an event record, including its tag. */
    private static final int EVENT_FIELD_COUNT = 6;

    /** File used by this storage instance. */
    private final Path dataFile;

    /** Creates storage using the application's default data file. */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates storage using a specific file.
     *
     * <p>The path overload keeps file-writing tests isolated from the
     * application's real data file.</p>
     *
     * @param dataFile file to write
     * @throws IllegalArgumentException if data file name is empty
     */
    public Storage(Path dataFile) throws IllegalArgumentException {
        if (dataFile == null || dataFile.getFileName() == null) {
            throw new IllegalArgumentException("Storage file path must name a file.");
        }
        this.dataFile = dataFile;
    }

    /**
     * Saves all tasks in the supplied list, replacing the previous snapshot.
     *
     * @param taskList tasks to save
     * @throws ArrodesException if the data directory or file cannot be written
     */
    public void save(TaskList taskList) throws ArrodesException {
        if (taskList == null) {
            throw new ArrodesException(ArrodesException.SAVE_FAILURE);
        }
        writeSnapshot(formatTasks(taskList));
    }

    /** Converts every task in a list into its storage representation. */
    private List<String> formatTasks(TaskList taskList) throws ArrodesException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < taskList.getSize(); i++) {
            try {
                lines.add(formatTask(taskList.getTaskByIndex(i)));
            } catch (RuntimeException exception) {
                throw new ArrodesException(ArrodesException.SAVE_FAILURE);
            }
        }
        return lines;
    }

    /** Writes a complete task snapshot using an atomic replacement when supported. */
    private void writeSnapshot(List<String> lines) throws ArrodesException {
        Path temporaryFile = null;
        try {
            Path targetFile = dataFile.toAbsolutePath().normalize();
            Path parent = targetFile.getParent();
            Files.createDirectories(parent);
            temporaryFile = Files.createTempFile(parent, ".arrodes-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, targetFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            throw new ArrodesException(ArrodesException.SAVE_FAILURE);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // The target was already replaced; a leftover temporary file is harmless.
                }
            }
        }
    }

    /**
     * Loads the saved tasks into a new task list.
     *
     * @param capacity maximum number of tasks the returned list can contain
     * @return saved tasks, or an empty list when no data file exists
     * @throws ArrodesException if the file cannot be read or contains an invalid record
     */
    public TaskList load(int capacity) throws ArrodesException {
        if (capacity < 0) {
            throw new ArrodesException(ArrodesException.LOAD_FAILURE);
        }
        TaskList taskList = new TaskList(capacity);
        try {
            if (Files.notExists(dataFile)) {
                return taskList;
            }
            if (!Files.isRegularFile(dataFile) || !Files.isReadable(dataFile)) {
                throw new IOException("Storage path is not a readable regular file.");
            }
            loadTasks(taskList);
        } catch (IOException | SecurityException exception) {
            throw new ArrodesException(ArrodesException.LOAD_FAILURE);
        } catch (ArrodesException exception) {
            throw new ArrodesException(ArrodesException.LOAD_FAILURE);
        }
        return taskList;
    }

    /** Reads the saved records and adds them to the supplied task list. */
    private void loadTasks(TaskList taskList) throws IOException, ArrodesException {
        if (!Files.isRegularFile(dataFile)) {
            throw new IOException("Storage path is not a regular file.");
        }
        for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            addRecordIfPresent(taskList, line);
        }
    }

    /** Parses and adds a non-blank storage record. */
    private void addRecordIfPresent(TaskList taskList, String line) throws ArrodesException {
        if (!line.isBlank()) {
            taskList.insert(parseTask(line));
        }
    }

    /** Parses one saved line and restores its completion status and tag. */
    private Task parseTask(String line) throws ArrodesException {
        List<String> fields = splitFields(line);
        validateCommonFields(fields);
        Task task = createTask(fields);
        restoreTaskState(task, fields);
        return task;
    }

    /** Validates the fields shared by every task record. */
    private void validateCommonFields(List<String> fields) throws ArrodesException {
        if (fields.size() < 3 || fields.get(0).isBlank() || fields.get(2).isBlank()) {
            throw invalidRecord();
        }
    }

    /** Creates a task from a validated storage record. */
    private Task createTask(List<String> fields) throws ArrodesException {
        Task task;
        switch (fields.get(0)) {
            case TODO_RECORD_TYPE:
                if (fields.size() != TODO_FIELD_COUNT) {
                    throw invalidRecord();
                }
                task = new Todo(fields.get(2));
                break;
            case DEADLINE_RECORD_TYPE:
                if (fields.size() != DEADLINE_FIELD_COUNT || fields.get(3).isBlank()) {
                    throw invalidRecord();
                }
                task = new Deadline(fields.get(2), parseDateTime(fields.get(3)));
                break;
            case EVENT_RECORD_TYPE:
                if (fields.size() != EVENT_FIELD_COUNT
                        || fields.get(3).isBlank() || fields.get(4).isBlank()) {
                    throw invalidRecord();
                }
                try {
                    task = new Event(fields.get(2), parseDateTime(fields.get(3)), parseDateTime(fields.get(4)),
                            fields.get(3).contains("T"), fields.get(4).contains("T"));
                } catch (IllegalArgumentException exception) {
                    throw invalidRecord();
                }
                break;
            default:
                throw invalidRecord();
        }
        return task;
    }

    /** Restores the completion status and tag from a storage record. */
    private void restoreTaskState(Task task, List<String> fields) throws ArrodesException {
        try {
            if (COMPLETED_STATUS.equals(fields.get(1))) {
                task.markAsDone();
            } else if (!INCOMPLETE_STATUS.equals(fields.get(1))) {
                throw invalidRecord();
            }
            task.tagWith(fields.get(fields.size() - 1));
        } catch (IllegalArgumentException exception) {
            throw invalidRecord();
        }
    }

    /** Splits on unescaped separators and decodes escaped field characters. */
    private List<String> splitFields(String line) throws ArrodesException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaping = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (isEscaping) {
                if (current != '\\' && current != '|') {
                    field.append('\\');
                }
                field.append(current);
                isEscaping = false;
            } else if (current == '\\') {
                isEscaping = true;
            } else if (current == '|') {
                fields.add(field.toString().strip());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        if (isEscaping) {
            throw invalidRecord();
        }
        fields.add(field.toString().strip());
        return fields;
    }

    /** Creates the common exception for malformed storage records. */
    private ArrodesException invalidRecord() {
        return new ArrodesException(ArrodesException.LOAD_FAILURE);
    }

    /** Parses an ISO date or date-time, normalising date-only values to midnight. */
    private LocalDateTime parseDateTime(String value) throws ArrodesException {
        try {
            return LocalDateTime.parse(value);
        } catch (RuntimeException exception) {
            try {
                return LocalDate.parse(value).atStartOfDay();
            } catch (RuntimeException ignored) {
                throw invalidRecord();
            }
        }
    }

    /** Converts one task into the storage format. */
    private String formatTask(Task task) {
        validateTask(task);
        String status = task.isDone() ? COMPLETED_STATUS : INCOMPLETE_STATUS;
        return formatTaskByType(task, status);
    }

    /** Validates the fields required to store a task. */
    private void validateTask(Task task) {
        if (task == null || task.getDescription() == null || task.getDescription().isBlank()
                || hasLineBreak(task.getDescription())) {
            throw new IllegalArgumentException("Task description cannot be null or span multiple lines.");
        }
    }

    /** Formats a task according to its concrete task type. */
    private String formatTaskByType(Task task, String status) {
        if (task instanceof Deadline deadline) {
            return String.format("%s | %s | %s | %s | %s", DEADLINE_RECORD_TYPE, status,
                    encode(deadline.getDescription()),
                    encodeRequired(formatDateTime(deadline.getDueBy())), encode(task.getTag()));
        }
        if (task instanceof Event event) {
            return String.format("%s | %s | %s | %s | %s | %s", EVENT_RECORD_TYPE, status,
                    encode(event.getDescription()),
                    encodeRequired(formatDateTime(event.getStartAt())),
                    encodeRequired(formatDateTime(event.getEndAt())), encode(task.getTag()));
        }
        if (!(task instanceof Todo)) {
            throw new IllegalArgumentException("Unknown task type.");
        }
        return String.format("%s | %s | %s | %s", TODO_RECORD_TYPE, status,
                encode(task.getDescription()), encode(task.getTag()));
    }

    /** Escapes characters that have meaning in the storage format. */
    private String encode(String value) {
        if (value == null || hasLineBreak(value)) {
            throw new IllegalArgumentException("Storage fields cannot be null or span multiple lines.");
        }
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /** Encodes a required task-specific field. */
    private String encodeRequired(Object value) {
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Storage fields cannot be blank.");
        }
        return encode(value.toString());
    }

    /** Formats midnight as a date and other values as a local date-time. */
    private String formatDateTime(LocalDateTime value) {
        return value.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? value.toLocalDate().toString()
                : value.toString();
    }

    /** Returns whether a value would corrupt the line-oriented format. */
    private boolean hasLineBreak(String value) {
        return value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0;
    }
}
