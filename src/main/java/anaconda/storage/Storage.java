package anaconda.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import anaconda.task.Deadline;
import anaconda.task.Event;
import anaconda.task.Task;
import anaconda.task.ToDo;

/**
 * Loads tasks from and saves tasks to a file on the user's computer.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String DONE_STATUS = "1";
    private static final String UNDONE_STATUS = "0";

    private static final int TYPE_FIELD = 0;
    private static final int STATUS_FIELD = 1;
    private static final int DESCRIPTION_FIELD = 2;
    private static final int DEADLINE_DATE_FIELD = 3;
    private static final int EVENT_START_FIELD = 3;
    private static final int EVENT_END_FIELD = 4;

    private final Path filePath;

    /**
     * Creates a storage manager that uses the given data file.
     *
     * @param filePath Relative or absolute path to the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file. An empty list is returned when the file
     * does not exist yet, which is expected when Anaconda is run for the first time.
     *
     * @return Mutable list of tasks stored in the data file, in their saved order.
     * @throws IOException If the existing data file cannot be read or contains malformed tasks.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            return Files.readAllLines(filePath, StandardCharsets.UTF_8).stream()
                    .map(this::parseTask)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IllegalArgumentException | IndexOutOfBoundsException | DateTimeException exception) {
            throw new IOException("The saved task list contains malformed data.", exception);
        }
    }

    /**
     * Replaces the data file contents with the current task list, creating the
     * parent folder first when necessary.
     *
     * @param tasks Current tasks to save.
     * @throws IOException If the tasks cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = tasks.stream()
                .map(this::formatTask)
                .toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * Converts one task into its storage-file representation.
     *
     * @param task Task to convert.
     * @return Storage-file representation of the task.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? DONE_STATUS : UNDONE_STATUS;
        if (task instanceof Deadline deadline) {
            return DEADLINE_TYPE + FIELD_SEPARATOR + status + FIELD_SEPARATOR
                    + task.getDescription() + FIELD_SEPARATOR + deadline.getBy();
        }
        if (task instanceof Event event) {
            return EVENT_TYPE + FIELD_SEPARATOR + status + FIELD_SEPARATOR
                    + task.getDescription() + FIELD_SEPARATOR + event.getFrom()
                    + FIELD_SEPARATOR + event.getTo();
        }
        // Parser and loader create only these three task types; a new type needs its own storage format.
        assert task instanceof ToDo : "Only ToDo tasks may use the T storage format";
        return TODO_TYPE + FIELD_SEPARATOR + status + FIELD_SEPARATOR + task.getDescription();
    }

    /**
     * Converts one storage-file line back into a task.
     *
     * @param line Storage-file line to convert.
     * @return Task represented by the line.
     */
    private Task parseTask(String line) {
        // Retain trailing empty fields, including an empty todo description.
        String[] fields = line.split(Pattern.quote(FIELD_SEPARATOR), -1);
        int expectedFields = switch (fields[TYPE_FIELD]) {
            case TODO_TYPE -> 3;
            case DEADLINE_TYPE -> 4;
            case EVENT_TYPE -> 5;
            default -> throw new IllegalArgumentException("Unknown task type: " + fields[TYPE_FIELD]);
        };
        if (fields.length != expectedFields
                || !(fields[STATUS_FIELD].equals(DONE_STATUS) || fields[STATUS_FIELD].equals(UNDONE_STATUS))) {
            throw new IllegalArgumentException("Invalid task fields or completion status.");
        }
        Task task = switch (fields[TYPE_FIELD]) {
            case TODO_TYPE -> new ToDo(fields[DESCRIPTION_FIELD]);
            case DEADLINE_TYPE -> new Deadline(fields[DESCRIPTION_FIELD], LocalDate.parse(fields[DEADLINE_DATE_FIELD]));
            case EVENT_TYPE -> new Event(fields[DESCRIPTION_FIELD],
                    LocalDate.parse(fields[EVENT_START_FIELD]), LocalDate.parse(fields[EVENT_END_FIELD]));
            default -> throw new IllegalArgumentException("Unknown task type: " + fields[TYPE_FIELD]);
        };

        if (fields[STATUS_FIELD].equals(DONE_STATUS)) {
            task.markAsDone();
        }
        return task;
    }
}
