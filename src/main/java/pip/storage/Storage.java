package pip.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import pip.task.Deadline;
import pip.task.Event;
import pip.task.Task;
import pip.task.TaskList;
import pip.task.Todo;


/**
 * Handles loading and saving tasks from/to the storage file.
 */
public class Storage {
    /** Storage marker for a todo task. */
    private static final String TODO_MARKER = "T";

    /** Storage marker for a deadline task. */
    private static final String DEADLINE_MARKER = "D";

    /** Storage marker for an event task. */
    private static final String EVENT_MARKER = "E";

    /** Storage marker for a completed task. */
    private static final String COMPLETED_MARKER = "1";

    /** Storage marker for an incomplete task. */
    private static final String INCOMPLETE_MARKER = "0";

    private final Path storagePath;

    /**
     * Creates a Storage bound to the given file path.
     *
     * @param filePath The path of the file to load tasks from and save tasks to.
     */
    public Storage(String filePath) {
        this.storagePath = Path.of(filePath);
    }

    /** 
     * Loads valid saved tasks from the storage file when it exists. 
     * 
     * @return List of loaded tasks.
     * @throws IOException If an I/O error occurs reading from the file.
     */
    public List<Task> load() throws IOException {
        if (!Files.isRegularFile(storagePath)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(storagePath).stream()
                .map(savedTask -> savedTask.split(" \\| ", -1))
                .map(this::createTask)
                .filter(Objects::nonNull)
                .toList();
    }

    /** 
     * Saves the current task list in a structured format to disk. 
     * 
     * @param tasks The TaskList to save.
     * @throws IOException If an I/O error occurs writing to the file.
     */
    public void save(TaskList tasks) throws IOException {
        Path parentDirectory = storagePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        List<String> savedTasks = StreamSupport.stream(tasks.spliterator(), false)
                .map(this::formatTaskForStorage)
                .filter(Objects::nonNull)
                .toList();
        Files.write(storagePath, savedTasks);
    }

    /** Creates a task from one valid saved line, or returns null when the line is malformed. */
    private Task createTask(String[] parts) {
        if (parts.length < 3) {
            return null;
        }

        try {
            Task task = createTaskByType(parts);
            if (task == null) {
                return null;
            }
            if (!applyCompletionStatus(task, parts[1])) {
                return null;
            }
            return task;
        } catch (IllegalArgumentException | DateTimeException e) {
            return null;
        }
    }

    /** Creates a task from the type marker and its encoded fields. */
    private Task createTaskByType(String[] parts) {
        switch (parts[0]) {
            case TODO_MARKER:
                return parts.length == 3 ? new Todo(decode(parts[2])) : null;
            case DEADLINE_MARKER:
                return parts.length == 4
                        ? new Deadline(decode(parts[2]), LocalDate.parse(decode(parts[3]))) : null;
            case EVENT_MARKER:
                return parts.length == 5
                        ? new Event(decode(parts[2]), LocalDate.parse(decode(parts[3])),
                        LocalDate.parse(decode(parts[4]))) : null;
            default:
                return null;
        }
    }

    /** Applies the persisted completion marker to a task. */
    private boolean applyCompletionStatus(Task task, String status) {
        if (status.equals(COMPLETED_MARKER)) {
            task.markDone();
            return true;
        }
        return status.equals(INCOMPLETE_MARKER);
    }

    /** Converts a task into one line of the storage format. */
    private String formatTaskForStorage(Task task) {
        String status = task.isCompleted() ? COMPLETED_MARKER : INCOMPLETE_MARKER;
        if (task instanceof Todo) {
            return String.format("%s | %s | %s", TODO_MARKER, status, encode(task.getName()));
        }
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return String.format("%s | %s | %s | %s", DEADLINE_MARKER, status, encode(task.getName()),
                    encode(deadline.getDeadline().toString()));
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return String.format("%s | %s | %s | %s | %s", EVENT_MARKER, status, encode(task.getName()),
                    encode(event.getStart().toString()), encode(event.getEnd().toString()));
        }
        return null;
    }

    /** Encodes text as Base64 so it cannot conflict with the storage delimiter. */
    private String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes a Base64-encoded storage field as UTF-8 text. */
    private String decode(String encodedField) {
        return new String(Base64.getDecoder().decode(encodedField), StandardCharsets.UTF_8);
    }
}
