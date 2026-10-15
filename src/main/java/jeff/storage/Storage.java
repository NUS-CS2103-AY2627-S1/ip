package jeff.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jeff.task.Deadline;
import jeff.task.Event;
import jeff.task.Task;
import jeff.task.Todo;


/**
 * Loads tasks from and saves tasks to the hard disk.
 */
public class Storage {
    private static final String TODO_TYPE_CODE = "T";
    private static final String DEADLINE_TYPE_CODE = "D";
    private static final String EVENT_TYPE_CODE = "E";

    private static final String INCOMPLETE_STATUS_CODE = "0";
    private static final String COMPLETE_STATUS_CODE = "1";

    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";

    private static final int TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int FIRST_DETAIL_INDEX = 3;
    private static final int SECOND_DETAIL_INDEX = 4;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private final Path filePath;

    /**
     * Creates a storage manager for the specified file.
     *
     * @param filePath Path of the task-storage file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves the supplied tasks to the configured data file.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the tasks cannot be saved.
     */
    public void saveTasks(ArrayList<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            String serializedTask = serializeTask(task);

            if (serializedTask != null) {
                lines.add(serializedTask);
            }
        }

        Files.write(filePath, lines);
    }

    /**
     * Converts a task into its storage representation.
     *
     * @param task Task to serialize.
     * @return Serialized task, or null for an unsupported task type.
     */
    private String serializeTask(Task task) {
        String status = task.isDone()
                ? COMPLETE_STATUS_CODE
                : INCOMPLETE_STATUS_CODE;

        if (task instanceof Todo) {
            return TODO_TYPE_CODE + FIELD_SEPARATOR
                    + status + FIELD_SEPARATOR
                    + task.getDescription();
        }

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return DEADLINE_TYPE_CODE + FIELD_SEPARATOR
                    + status + FIELD_SEPARATOR
                    + deadline.getDescription() + FIELD_SEPARATOR
                    + deadline.getDueDate();
        }

        if (task instanceof Event) {
            Event event = (Event) task;
            return EVENT_TYPE_CODE + FIELD_SEPARATOR
                    + status + FIELD_SEPARATOR
                    + event.getDescription() + FIELD_SEPARATOR
                    + event.getStartTime() + FIELD_SEPARATOR
                    + event.getEndTime();
        }

        return null;
    }

    /**
     * Loads all tasks from the configured data file.
     *
     * @return Tasks loaded from the file.
     * @throws IOException If the file cannot be read or contains invalid records.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (Files.notExists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath);

        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(parseTask(lines.get(i)));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                throw new IOException(
                        "Invalid saved task at line " + (i + 1) + ".",
                        e);
            }
        }

        return tasks;
    }

    private Task parseTask(String line) {
        String[] parts = line.split(FIELD_SEPARATOR_REGEX, -1);
        validateRecord(parts);

        Task task;

        switch (parts[TYPE_INDEX]) {
            case TODO_TYPE_CODE:
                task = new Todo(parts[DESCRIPTION_INDEX]);
                break;
            case DEADLINE_TYPE_CODE:
                task = new Deadline(
                        parts[DESCRIPTION_INDEX],
                        LocalDate.parse(parts[FIRST_DETAIL_INDEX]));
                break;
            case EVENT_TYPE_CODE:
                task = new Event(
                        parts[DESCRIPTION_INDEX],
                        parts[FIRST_DETAIL_INDEX],
                        parts[SECOND_DETAIL_INDEX]);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown saved task type.");
        }

        if (parts[STATUS_INDEX].equals(COMPLETE_STATUS_CODE)) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Checks that a saved record has valid fields before parsing it.
     *
     * @param parts Fields of the saved record.
     * @throws IllegalArgumentException If the record is invalid.
     */
    private void validateRecord(String[] parts) {
        int expectedFieldCount = getExpectedFieldCount(parts[TYPE_INDEX]);

        if (parts.length != expectedFieldCount) {
            throw new IllegalArgumentException(
                    "Incorrect number of saved task fields.");
        }

        for (String part : parts) {
            if (part.isBlank() || part.contains("|")) {
                throw new IllegalArgumentException(
                        "Invalid saved task field.");
            }
        }

        String status = parts[STATUS_INDEX];

        if (!status.equals(INCOMPLETE_STATUS_CODE)
                && !status.equals(COMPLETE_STATUS_CODE)) {
            throw new IllegalArgumentException(
                    "Invalid saved completion status.");
        }
    }

    private int getExpectedFieldCount(String typeCode) {
        switch (typeCode) {
            case TODO_TYPE_CODE:
                return TODO_FIELD_COUNT;
            case DEADLINE_TYPE_CODE:
                return DEADLINE_FIELD_COUNT;
            case EVENT_TYPE_CODE:
                return EVENT_FIELD_COUNT;
            default:
                throw new IllegalArgumentException(
                        "Unknown saved task type.");
        }
    }
}
