package jarvis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Loads and saves Jarvis tasks in a file relative to the project directory. */
public class TaskStorage {
    /** Location used by the application when no storage location is provided. */
    private static final Path DEFAULT_DATA_FILE = Paths.get("data", "jarvis.txt");
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String STATUS_NOT_DONE = "0";
    private static final String STATUS_DONE = "1";
    private static final String COMPLETED_ICON = "X";
    private static final int PART_TYPE_INDEX = 0;
    private static final int PART_STATUS_INDEX = 1;
    private static final int PART_DESCRIPTION_INDEX = 2;
    private static final int PART_DETAILS_INDEX = 3;
    private static final int MIN_PART_COUNT = 3;
    private static final int TYPED_TASK_PART_COUNT = 4;
    private static final int EVENT_TASK_PART_COUNT = 5;

    /** Location of the task database used by this storage instance. */
    private final Path dataFile;

    /** Creates storage using the application's default task database. */
    public TaskStorage() {
        this(DEFAULT_DATA_FILE);
    }

    /** Creates storage using the specified task database. */
    TaskStorage(Path dataFile) {
        assert dataFile != null : "Task storage path must not be null";
        this.dataFile = dataFile;
    }

    /** Loads all valid tasks from the data file, returning an empty list if it is unavailable. */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
                Task task = parseLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException exception) {
            // Jarvis can still start with an empty list when the file cannot be read.
        }
        return tasks;
    }

    /** Saves the current tasks, creating the data directory when necessary. */
    public void save(List<Task> tasks) throws JarvisException {
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Files.write(dataFile, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new JarvisException("I couldn't save your tasks. Check that the data file is writable.");
        }
    }

    /** Converts a task into one line of the storage format. */
    private String formatTask(Task task) {
        String type = task.getTypeIcon();
        String status = COMPLETED_ICON.equals(task.getStatusIcon()) ? STATUS_DONE : STATUS_NOT_DONE;
        String prefix = type + " | " + status + " | " + task.getDescription();
        if (task instanceof Deadline deadline) {
            return prefix + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            return prefix + " | " + event.getFrom() + " | " + event.getTo();
        }
        return prefix;
    }

    /** Converts one storage line into a task, or returns {@code null} when invalid. */
    private Task parseLine(String line) {
        try {
            String[] parts = line.split("\\s*\\|\\s*", -1);
            if (!isValidRecord(parts)) {
                return null;
            }

            Task task = createTask(parts);
            if (task == null) {
                return null;
            }
            task.restoreStatus(parts[PART_STATUS_INDEX].equals(STATUS_DONE));
            return task;
        } catch (IllegalArgumentException | DateTimeException exception) {
            return null;
        }
    }

    /** Returns whether a split storage record has valid common fields. */
    private boolean isValidRecord(String[] parts) {
        if (parts.length < MIN_PART_COUNT) {
            return false;
        }

        boolean isSupportedType = TYPE_TODO.equals(parts[PART_TYPE_INDEX])
                || TYPE_DEADLINE.equals(parts[PART_TYPE_INDEX])
                || TYPE_EVENT.equals(parts[PART_TYPE_INDEX]);
        boolean isSupportedStatus = STATUS_NOT_DONE.equals(parts[PART_STATUS_INDEX])
                || STATUS_DONE.equals(parts[PART_STATUS_INDEX]);
        boolean hasDescription = !parts[PART_DESCRIPTION_INDEX].trim().isEmpty();
        return isSupportedType && isSupportedStatus && hasDescription;
    }

    /** Creates a task from a storage record whose common fields are valid. */
    private Task createTask(String[] parts) {
        String type = parts[PART_TYPE_INDEX];
        String description = parts[PART_DESCRIPTION_INDEX].trim();
        if (TYPE_DEADLINE.equals(type) && parts.length == TYPED_TASK_PART_COUNT) {
            String dateText = parts[PART_DETAILS_INDEX].trim();
            if (dateText.isEmpty()) {
                return null;
            }
            return new Deadline(description, LocalDate.parse(dateText));
        }
        if (TYPE_EVENT.equals(type) && parts.length == EVENT_TASK_PART_COUNT) {
            String from = parts[PART_DETAILS_INDEX].trim();
            String to = parts[PART_DETAILS_INDEX + 1].trim();
            if (from.isEmpty() || to.isEmpty()) {
                return null;
            }
            return new Event(description, from, to);
        }
        if (TYPE_EVENT.equals(type) && parts.length == TYPED_TASK_PART_COUNT) {
            return createLegacyEvent(description, parts[PART_DETAILS_INDEX]);
        }
        if (TYPE_TODO.equals(type) && parts.length == MIN_PART_COUNT) {
            return new Todo(description);
        }
        return null;
    }

    /** Creates an event from the legacy format that stores both times in one field. */
    private Task createLegacyEvent(String description, String details) {
        String eventDetails = details.trim();
        int separator = eventDetails.lastIndexOf(' ');
        if (separator <= 0 || separator == eventDetails.length() - 1) {
            return null;
        }

        String from = eventDetails.substring(0, separator);
        String to = eventDetails.substring(separator + 1);
        return new Event(description, from, to);
    }
}
