package wodan.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import wodan.WodanException;
import wodan.task.Deadline;
import wodan.task.Event;
import wodan.task.Task;
import wodan.task.TaskDateTime;
import wodan.task.Todo;

/**
 * Reads and writes the task list on the hard disk.
 * Invalid save-file lines are skipped so that readable tasks are not lost.
 * The save location is supplied by the caller, typically
 * {@code Path.of("data", "wodan.txt")} so it works on every operating system.
 */
public class Storage {
    private static final int TODO_FIELD_COUNT = 3;
    private static final int TODO_FIELD_COUNT_WITH_TAG = 4;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int DEADLINE_FIELD_COUNT_WITH_TAG = 5;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final int EVENT_FIELD_COUNT_WITH_TAG = 6;

    private final Path filePath;
    private String loadWarning;

    /**
     * Creates storage at {@code filePath}, relative to the working directory if the path is relative.
     *
     * @param filePath Save-file path, for example {@code data/wodan.txt}.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
        this.loadWarning = null;
    }

    /**
     * Returns a warning about skipped save-file lines from the most recent load, or {@code null}.
     *
     * @return The warning text, or {@code null} if nothing was skipped.
     */
    public String getLoadWarning() {
        return loadWarning;
    }

    /**
     * Returns the tasks stored in the save file.
     * If the file does not exist or is empty, an empty list is returned.
     * Corrupted lines are skipped and recorded as a warning.
     *
     * @return Tasks loaded from disk, or an empty list if there is no save file.
     * @throws WodanException If the path exists but cannot be read as a file.
     */
    public ArrayList<Task> load() throws WodanException {
        loadWarning = null;
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        if (!Files.isRegularFile(filePath)) {
            throw new WodanException(
                    "The save path is not a file. The ravens could not recall the quests.");
        }
        if (!Files.isReadable(filePath)) {
            throw new WodanException(
                    "The ravens were denied access to the save file. "
                            + "They could not recall the quests.");
        }

        try {
            List<String> lines = new ArrayList<>(
                    Files.readAllLines(filePath, StandardCharsets.UTF_8));
            stripBom(lines);
            List<Task> parsed = lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::parseTask)
                    .collect(Collectors.toList());

            ArrayList<Task> loaded = parsed.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
            int skipped = parsed.size() - loaded.size();
            if (skipped == 1) {
                loadWarning = "The ravens skipped 1 corrupted quest in the save file.";
            } else if (skipped > 1) {
                loadWarning = "The ravens skipped " + skipped + " corrupted quests in the save file.";
            }
            return loaded;
        } catch (AccessDeniedException e) {
            throw new WodanException(
                    "The ravens were denied access to the save file. "
                            + "They could not recall the quests.");
        } catch (IOException e) {
            throw new WodanException("The ravens could not recall the quests.");
        }
    }

    /**
     * Writes the given tasks to the save file, creating the parent directory if it does not exist.
     *
     * @param tasks Tasks to persist.
     * @throws WodanException If the file cannot be created or written.
     */
    public void save(ArrayList<Task> tasks) throws WodanException {
        if (Files.exists(filePath) && !Files.isRegularFile(filePath)) {
            throw new WodanException(
                    "The save path is not a file. The ravens could not record the quests.");
        }
        if (Files.exists(filePath) && !Files.isWritable(filePath)) {
            throw new WodanException(
                    "The ravens were denied access to the save file. "
                            + "They could not record the quests.");
        }

        try {
            createParentDirectory();

            List<String> lines = tasks.stream()
                    .map(Task::toStorageString)
                    .collect(Collectors.toList());
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (AccessDeniedException e) {
            throw new WodanException(
                    "The ravens were denied access to the save file. "
                            + "They could not record the quests.");
        } catch (IOException e) {
            throw new WodanException("The ravens could not record the quests.");
        }
    }

    /**
     * Creates the save file's parent folder if it does not already exist.
     *
     * @throws IOException If the folder cannot be created.
     */
    private void createParentDirectory() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Returns the task encoded in {@code line}, or {@code null} if the line is not valid.
     *
     * @param line One save-file line.
     * @return The task, or {@code null} if the line is corrupt.
     */
    private Task parseTask(String line) {
        String[] parts = splitFields(line);
        if (parts.length < TODO_FIELD_COUNT) {
            return null;
        }

        String type = parts[0];
        String doneFlag = parts[1];
        if (!doneFlag.equals(Task.STORAGE_NOT_DONE) && !doneFlag.equals(Task.STORAGE_DONE)) {
            return null;
        }
        boolean isDone = doneFlag.equals(Task.STORAGE_DONE);
        String description = parts[2];
        if (description.isEmpty()) {
            return null;
        }

        Task task = parseTaskByType(type, description, parts);
        if (task == null) {
            return null;
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns a task of {@code type}, or {@code null} if the fields do not match that type.
     *
     * @param type Save-file type code.
     * @param description Task description field.
     * @param parts All fields from the line.
     * @return The task, or {@code null} if the line is corrupt.
     */
    private Task parseTaskByType(String type, String description, String[] parts) {
        switch (type) {
            case Task.STORAGE_TYPE_TODO:
                return parseStoredTodo(description, parts);
            case Task.STORAGE_TYPE_DEADLINE:
                return parseStoredDeadline(description, parts);
            case Task.STORAGE_TYPE_EVENT:
                return parseStoredEvent(description, parts);
            default:
                return null;
        }
    }

    /**
     * Returns a todo if {@code parts} has the todo fields, with an optional category.
     *
     * @param description Task description field.
     * @param parts All fields from the line.
     * @return The todo, or {@code null} if the field count is wrong.
     */
    private Todo parseStoredTodo(String description, String[] parts) {
        if (parts.length != TODO_FIELD_COUNT && parts.length != TODO_FIELD_COUNT_WITH_TAG) {
            return null;
        }
        Todo todo = new Todo(description);
        applyStoredCategory(todo, parts, TODO_FIELD_COUNT);
        return todo;
    }

    /**
     * Returns a deadline if {@code parts} has a parseable due date.
     *
     * @param description Task description field.
     * @param parts All fields from the line.
     * @return The deadline, or {@code null} if the due date is missing or invalid.
     */
    private Deadline parseStoredDeadline(String description, String[] parts) {
        if ((parts.length != DEADLINE_FIELD_COUNT && parts.length != DEADLINE_FIELD_COUNT_WITH_TAG)
                || parts[3].isEmpty()) {
            return null;
        }
        TaskDateTime dueAt = TaskDateTime.parseStorage(parts[3]);
        if (dueAt == null) {
            return null;
        }
        Deadline deadline = new Deadline(description, dueAt);
        applyStoredCategory(deadline, parts, DEADLINE_FIELD_COUNT);
        return deadline;
    }

    /**
     * Returns an event if {@code parts} has a valid start and end.
     *
     * @param description Task description field.
     * @param parts All fields from the line.
     * @return The event, or {@code null} if the times are missing, invalid, or reversed.
     */
    private Event parseStoredEvent(String description, String[] parts) {
        if ((parts.length != EVENT_FIELD_COUNT && parts.length != EVENT_FIELD_COUNT_WITH_TAG)
                || parts[3].isEmpty() || parts[4].isEmpty()) {
            return null;
        }
        TaskDateTime startAt = TaskDateTime.parseStorage(parts[3]);
        TaskDateTime endAt = TaskDateTime.parseStorage(parts[4]);
        if (startAt == null || endAt == null) {
            return null;
        }
        if (!Event.isValidRange(startAt, endAt)) {
            return null;
        }
        Event event = new Event(description, startAt, endAt);
        applyStoredCategory(event, parts, EVENT_FIELD_COUNT);
        return event;
    }

    /**
     * Sets the category from the optional last save-file field.
     * Old files without that field stay in {@code general}.
     *
     * @param task Task just parsed.
     * @param parts All fields from the line.
     * @param tagIndex Index of the category field.
     */
    private void applyStoredCategory(Task task, String[] parts, int tagIndex) {
        if (parts.length > tagIndex && !parts[tagIndex].isEmpty()) {
            task.setCategory(parts[tagIndex]);
        }
    }

    /**
     * Splits a save-file line on {@code |} and trims each field.
     *
     * @param line One save-file line.
     * @return The fields in order.
     */
    private String[] splitFields(String line) {
        return Arrays.stream(line.split("\\|", -1))
                .map(String::trim)
                .toArray(String[]::new);
    }

    /**
     * Removes a UTF-8 BOM from the first line if present.
     *
     * @param lines Lines read from the save file.
     */
    private void stripBom(List<String> lines) {
        if (lines.isEmpty()) {
            return;
        }
        String first = lines.get(0);
        if (first.startsWith("\uFEFF")) {
            lines.set(0, first.substring(1));
        }
    }
}
