package alfred.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import alfred.exception.AlfredException;
import alfred.task.Deadline;
import alfred.task.Event;
import alfred.task.Task;
import alfred.task.TaskDateTime;
import alfred.task.TaskList;
import alfred.task.TaskType;
import alfred.task.Todo;

/**
 * Saves Alfred tasks to, and loads them from, a file relative to the project directory.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "alfred.txt");
    private static final String FIELD_SEPARATOR = "\t";
    private static final String FIELD_SEPARATOR_REGEX = "\\t";
    private static final int PRESERVE_TRAILING_EMPTY_FIELDS = -1;
    private static final String TODO_TYPE_CODE = "T";
    private static final String DEADLINE_TYPE_CODE = "D";
    private static final String EVENT_TYPE_CODE = "E";
    private static final String COMPLETE_STATUS = "1";
    private static final String INCOMPLETE_STATUS = "0";
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int FIRST_DATE_TIME_FIELD_INDEX = 3;
    private static final int SECOND_DATE_TIME_FIELD_INDEX = 4;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private final Path filePath;

    /** Creates storage using Alfred's default task file location. */
    public Storage() {
        this(FILE_PATH);
    }

    /**
     * Creates storage using the specified task file location.
     *
     * @param filePath location of the task file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks. A missing data file represents an empty task list.
     *
     * @return the tasks saved in the data file
     * @throws AlfredException if the data file cannot be read or contains invalid task data
     */
    public ArrayList<Task> load() throws AlfredException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isEmpty()) {
                    tasks.add(deserialise(line));
                }
            }
            return tasks;
        } catch (IOException | IllegalArgumentException e) {
            throw new AlfredException("Alfred could not load the saved tasks.");
        }
    }

    /**
     * Saves the current task list, creating the data directory when necessary.
     *
     * @param tasks tasks to save
     * @throws AlfredException if the task data cannot be saved
     */
    public void save(TaskList tasks) throws AlfredException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(serialise(task));
        }

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AlfredException("Alfred could not save the tasks.");
        }
    }

    /** Serializes one task into an escaped tab-separated storage line. */
    private String serialise(Task task) {
        String status = task.isDone() ? COMPLETE_STATUS : INCOMPLETE_STATUS;
        if (task.getType() == TaskType.TODO) {
            assert task instanceof Todo : "TODO tasks must use the Todo class";
            return TODO_TYPE_CODE + FIELD_SEPARATOR + status + FIELD_SEPARATOR + escape(task.getDescription());
        }
        if (task.getType() == TaskType.DEADLINE) {
            assert task instanceof Deadline : "DEADLINE tasks must use the Deadline class";
            Deadline deadline = (Deadline) task;
            return DEADLINE_TYPE_CODE + FIELD_SEPARATOR + status + FIELD_SEPARATOR + escape(task.getDescription())
                    + FIELD_SEPARATOR + deadline.getBy();
        }
        assert task.getType() == TaskType.EVENT : "Every task must have a supported storage type";
        assert task instanceof Event : "EVENT tasks must use the Event class";
        Event event = (Event) task;
        return EVENT_TYPE_CODE + FIELD_SEPARATOR + status + FIELD_SEPARATOR + escape(task.getDescription())
                + FIELD_SEPARATOR + event.getFrom() + FIELD_SEPARATOR + event.getTo();
    }

    /** Deserializes one task from an escaped tab-separated storage line. */
    private Task deserialise(String line) throws AlfredException {
        String[] parts = line.split(FIELD_SEPARATOR_REGEX, PRESERVE_TRAILING_EMPTY_FIELDS);
        Task task;
        if (parts.length == TODO_FIELD_COUNT && parts[TYPE_FIELD_INDEX].equals(TODO_TYPE_CODE)) {
            task = new Todo(unescape(parts[DESCRIPTION_FIELD_INDEX]));
        } else if (parts.length == DEADLINE_FIELD_COUNT && parts[TYPE_FIELD_INDEX].equals(DEADLINE_TYPE_CODE)) {
            task = new Deadline(unescape(parts[DESCRIPTION_FIELD_INDEX]),
                    TaskDateTime.parseStored(unescape(parts[FIRST_DATE_TIME_FIELD_INDEX])));
        } else if (parts.length == EVENT_FIELD_COUNT && parts[TYPE_FIELD_INDEX].equals(EVENT_TYPE_CODE)) {
            LocalDateTime from = TaskDateTime.parseStored(unescape(parts[FIRST_DATE_TIME_FIELD_INDEX]));
            LocalDateTime to = TaskDateTime.parseStored(unescape(parts[SECOND_DATE_TIME_FIELD_INDEX]));
            if (!from.isBefore(to)) {
                throw new IllegalArgumentException("Invalid event time range");
            }
            task = new Event(unescape(parts[DESCRIPTION_FIELD_INDEX]), from, to);
        } else {
            throw new IllegalArgumentException("Invalid task data");
        }

        if (parts[STATUS_FIELD_INDEX].equals(COMPLETE_STATUS)) {
            task.markAsDone();
        } else if (!parts[STATUS_FIELD_INDEX].equals(INCOMPLETE_STATUS)) {
            throw new IllegalArgumentException("Invalid task status");
        }
        return task;
    }

    /** Escapes backslashes, tabs, and newlines so each task stays on one storage line. */
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
    }

    /** Restores characters escaped by {@link #escape(String)}. */
    private String unescape(String text) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (char character : text.toCharArray()) {
            if (escaping) {
                if (character == 't') {
                    result.append('\t');
                } else if (character == 'n') {
                    result.append('\n');
                } else if (character == '\\') {
                    result.append('\\');
                } else {
                    throw new IllegalArgumentException("Invalid escape sequence");
                }
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else {
                result.append(character);
            }
        }
        if (escaping) {
            throw new IllegalArgumentException("Invalid escape sequence");
        }
        return result.toString();
    }
}
