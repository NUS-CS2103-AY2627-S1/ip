package zen.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import zen.ZenException;
import zen.task.Deadline;
import zen.task.Event;
import zen.task.Priority;
import zen.task.Task;
import zen.task.TaskList;
import zen.task.Todo;

/**
 * Saves and loads the task list at the specified file path.
 */
public class Storage {
    private final Path filePath;
    private final Path storageDirectory;

    /**
     * Creates storage backed by a supplied file path.
     *
     * @param filePath the location of the task storage file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
        this.storageDirectory = this.filePath.getParent();
    }

    /**
     * Saves the supplied task list in a pipe-delimited storage format.
     *
     * @param taskList task list whose current state should be written
     * @throws ZenException if the storage directory or file cannot be written
     */
    public void save(TaskList taskList) throws ZenException {
        try {
            Files.createDirectories(storageDirectory);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            Files.writeString(filePath, taskList.toStorageString());
        } catch (IOException exception) {
            throw new ZenException("Unable to save tasks to " + filePath);
        }
    }

    /**
     * Loads tasks from the pipe-delimited storage file.
     *
     * @return a task list containing every task stored in the file
     * @throws ZenException if the storage file cannot be read or contains an invalid record
     */
    public TaskList load() throws ZenException {
        if (!Files.exists(filePath)) {
            TaskList taskList = new TaskList();
            save(taskList);
            return taskList;
        }

        try {
            TaskList taskList = new TaskList();
            List<String> taskRecords = Files.readAllLines(filePath);
            List<Integer> invalidLineNumbers = new ArrayList<>();

            for (int i = 0; i < taskRecords.size(); i++) {
                String taskRecord = taskRecords.get(i);
                if (!taskRecord.isBlank()) {
                    try {
                        taskList.addTask(parseTask(taskRecord));
                    } catch (RuntimeException exception) {
                        invalidLineNumbers.add(i + 1);
                    }
                }
            }

            if (!invalidLineNumbers.isEmpty()) {
                throw invalidRecordException(invalidLineNumbers);
            }

            return taskList;
        } catch (IOException exception) {
            throw new ZenException("Unable to load tasks from " + filePath + ".");
        }
    }

    /**
     * Creates an error explaining that a saved task record cannot be loaded.
     *
     * @param lineNumbers one-based line numbers of invalid records
     * @return an exception explaining the invalid storage record
     */
    private ZenException invalidRecordException(List<Integer> lineNumbers) {
        String formattedLineNumbers = lineNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        String lineLabel = lineNumbers.size() == 1 ? "line" : "lines";
        String verb = lineNumbers.size() == 1 ? "is" : "are";
        return new ZenException("Unable to load tasks because " + lineLabel + " " + formattedLineNumbers
                + " " + verb + " invalid. The file was not changed.");
    }

    /**
     * Recreates one task from its pipe-delimited storage record.
     *
     * @param taskRecord one task record from the storage file
     * @return the reconstructed task, including its completion status
     */
    private Task parseTask(String taskRecord) {
        String[] fields = taskRecord.split("\\s*\\|\\s*", -1);
        Task task = switch (fields[0]) {
            case "T" -> createTodo(fields);
            case "D" -> createDeadline(fields);
            case "E" -> createEvent(fields);
            default -> throw new IllegalArgumentException("Unknown task type: " + fields[0]);
        };

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new IllegalArgumentException("Unknown completion status: " + fields[1]);
        }
        return task;
    }

    /** Creates a todo from a validated storage record. */
    private Todo createTodo(String[] fields) {
        validateFieldCount(fields, 3);
        return new Todo(fields[2], getPriority(fields, 3));
    }

    /** Creates a deadline from a validated storage record. */
    private Deadline createDeadline(String[] fields) {
        validateFieldCount(fields, 4);
        return new Deadline(fields[2], LocalDateTime.parse(fields[3]), getPriority(fields, 4));
    }

    /** Creates an event from a validated storage record. */
    private Event createEvent(String[] fields) {
        validateFieldCount(fields, 4);
        return createEvent(fields[2], fields[3], getPriority(fields, 4));
    }

    /**
     * Separates the event timing field saved by {@link Event#toStorageString()}.
     *
     * @param description event description
     * @param timing saved start and end timing text
     * @return the reconstructed event
     */
    private Event createEvent(String description, String timing, Priority priority) {
        String[] times = timing.split(" to ", 2);
        return new Event(description, LocalDateTime.parse(times[0]), LocalDateTime.parse(times[1]), priority);
    }

    /** Validates that fields contain either the legacy or priority-aware record length. */
    private void validateFieldCount(String[] fields, int legacyFieldCount) {
        if (fields.length != legacyFieldCount && fields.length != legacyFieldCount + 1) {
            throw new IllegalArgumentException("Incorrect number of task fields");
        }
    }

    /**
     * Returns a record's priority, using none for a legacy record without that field.
     *
     * @param fields fields read from a task record
     * @param legacyFieldCount number of fields in the legacy record format
     * @return the parsed priority
     */
    private Priority getPriority(String[] fields, int legacyFieldCount) {
        if (fields.length == legacyFieldCount) {
            return Priority.NONE;
        }
        if (fields.length == legacyFieldCount + 1) {
            return Priority.fromStorageValue(fields[fields.length - 1]);
        }
        throw new IllegalArgumentException("Incorrect number of task fields");
    }
}
