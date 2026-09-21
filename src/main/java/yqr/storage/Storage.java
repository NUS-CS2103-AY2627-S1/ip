package yqr.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import yqr.exception.YqrException;
import yqr.task.Deadline;
import yqr.task.Event;
import yqr.task.EventTimeValidator;
import yqr.task.Task;
import yqr.task.TaskList;
import yqr.task.Todo;

/**
 * Loads tasks from and saves tasks to a local text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";

    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the given path.
     *
     * @param filePath relative path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "Storage path cannot be null");
    }

    /**
     * Loads all tasks from the data file. A missing file represents an empty list.
     *
     * @return task list reconstructed from the data file.
     * @throws YqrException if the file cannot be read or contains invalid data.
     */
    public TaskList loadTasks() throws YqrException {
        try {
            if (Files.notExists(filePath)) {
                return new TaskList();
            }
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    Task task = parseTask(line, i + 1);
                    if (tasks.stream().anyMatch(task::hasSameDetails)) {
                        throw new YqrException("Duplicate saved task on line " + (i + 1));
                    }
                    tasks.add(task);
                }
            }
            return new TaskList(tasks);
        } catch (IOException | SecurityException e) {
            throw new YqrException("Unable to load saved tasks: " + describe(e));
        }
    }

    /**
     * Saves the current task list, creating its parent directory when necessary.
     *
     * @param taskList task list to save.
     * @throws YqrException if the data file cannot be written.
     */
    public void saveTasks(TaskList taskList) throws YqrException {
        validateTasksForSaving(taskList);
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> lines = taskList.getTasks().stream()
                    .map(this::formatTask)
                    .toList();
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            throw new YqrException("Unable to save tasks: " + describe(e));
        }
    }

    /** Rejects task data that would be ambiguous or invalid when loaded again. */
    private static void validateTasksForSaving(TaskList taskList) throws YqrException {
        if (taskList == null) {
            throw new YqrException("Unable to save tasks: task list is missing");
        }
        List<Task> tasks = taskList.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            validateStoredText(task.getDescription());
            if (!(task instanceof Todo) && !(task instanceof Deadline) && !(task instanceof Event)) {
                throw new YqrException("Unable to save tasks: unsupported task type");
            }
            if (task instanceof Event) {
                Event event = (Event) task;
                validateStoredText(event.getFrom());
                validateStoredText(event.getTo());
                EventTimeValidator.validate(event.getFrom(), event.getTo());
            }
            for (int j = 0; j < i; j++) {
                if (task.hasSameDetails(tasks.get(j))) {
                    throw new YqrException("Unable to save tasks: duplicate task details");
                }
            }
        }
    }

    /** Rejects blank text and characters reserved by the line-based storage format. */
    private static void validateStoredText(String text) throws YqrException {
        if (text.isBlank() || text.indexOf('|') >= 0
                || text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0) {
            throw new YqrException("Unable to save tasks: task details contain unsupported text");
        }
    }

    /**
     * Converts a task into one line of the storage format.
     *
     * @param task task to convert.
     * @return serialized task.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return String.join(FIELD_SEPARATOR, DEADLINE_TYPE, status,
                    deadline.getDescription(), deadline.getBy().toString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return String.join(FIELD_SEPARATOR, EVENT_TYPE, status,
                    event.getDescription(), event.getFrom(), event.getTo());
        }
        return String.join(FIELD_SEPARATOR, TODO_TYPE, status, task.getDescription());
    }

    /**
     * Reconstructs one task from a line of stored data.
     *
     * @param line serialized task.
     * @param lineNumber line number used when reporting malformed data.
     * @return reconstructed task.
     * @throws YqrException if the line is malformed.
     */
    private Task parseTask(String line, int lineNumber) throws YqrException {
        String[] fields = line.split(Pattern.quote(FIELD_SEPARATOR), -1);
        if (fields.length < 3) {
            throw invalidData(lineNumber);
        }
        for (int i = 2; i < fields.length; i++) {
            if (fields[i].indexOf('|') >= 0) {
                throw invalidData(lineNumber);
            }
        }

        Task task;
        switch (fields[0]) {
            case TODO_TYPE:
                if (fields.length != 3 || fields[2].isBlank()) {
                    throw invalidData(lineNumber);
                }
                task = new Todo(fields[2]);
                break;
            case DEADLINE_TYPE:
                if (fields.length != 4 || fields[2].isBlank() || fields[3].isBlank()) {
                    throw invalidData(lineNumber);
                }
                try {
                    task = new Deadline(fields[2], LocalDate.parse(fields[3]));
                } catch (DateTimeParseException e) {
                    throw invalidData(lineNumber);
                }
                break;
            case EVENT_TYPE:
                if (fields.length != 5 || fields[2].isBlank()
                        || fields[3].isBlank() || fields[4].isBlank()) {
                    throw invalidData(lineNumber);
                }
                try {
                    EventTimeValidator.validate(fields[3], fields[4]);
                } catch (YqrException e) {
                    throw invalidData(lineNumber);
                }
                task = new Event(fields[2], fields[3], fields[4]);
                break;
            default:
                throw invalidData(lineNumber);
        }

        if (fields[1].equals(DONE_STATUS)) {
            task.markAsDone();
        } else if (!fields[1].equals(NOT_DONE_STATUS)) {
            throw invalidData(lineNumber);
        }
        return task;
    }

    /** Returns a useful message even when an I/O exception has no detail text. */
    private static String describe(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
                ? exception.getClass().getSimpleName()
                : message;
    }

    /**
     * Creates a consistent error for a malformed line in the data file.
     *
     * @param lineNumber number of the malformed line.
     * @return exception describing the malformed data.
     */
    private YqrException invalidData(int lineNumber) {
        return new YqrException("Invalid saved task on line " + lineNumber);
    }
}
