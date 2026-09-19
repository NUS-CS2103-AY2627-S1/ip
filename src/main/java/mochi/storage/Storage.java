package mochi.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import mochi.DateTimeUtil;
import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Task;
import mochi.task.Todo;

/**
 * Loads and saves the task list on the hard disk.
 */
public class Storage {
    private final Path filePath;
    private final ArrayList<String> loadWarnings = new ArrayList<>();

    /**
     * Creates a storage object that writes to the given file.
     *
     * @param filePath Path of the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file.
     *
     * @return Tasks reconstructed from the saved lines.
     * @throws IOException If the data file cannot be read.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        loadWarnings.clear();

        if (Files.notExists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException e) {
                loadWarnings.add("Skipped invalid data on line " + (i + 1) + ": " + e.getMessage());
            }
        }

        return tasks;
    }

    /**
     * Returns warnings generated while loading malformed data lines.
     *
     * @return An unmodifiable copy of the warnings.
     */
    public List<String> getLoadWarnings() {
        return List.copyOf(loadWarnings);
    }

    /**
     * Reconstructs one task after validating its saved fields.
     *
     * @param line One line from the data file.
     * @return The reconstructed task.
     * @throws IllegalArgumentException If the line has an invalid format.
     */
    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        validateTaskFields(fields);

        Task task = createTask(fields);
        assert task != null : "A validated task type must create a task";

        if (fields[1].equals("1")) {
            task.mark();
        }
        return task;
    }

    /**
     * Validates the structure and content shared by all stored task types.
     */
    private void validateTaskFields(String[] fields) {
        if (fields.length < 3) {
            throw new IllegalArgumentException("not enough fields");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw new IllegalArgumentException("completion status must be 0 or 1");
        }

        int expectedFieldCount = getExpectedFieldCount(fields[0]);
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException(
                    "wrong number of fields for a " + fields[0] + " task");
        }
        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new IllegalArgumentException("task details cannot be empty");
            }
        }
    }

    /**
     * Returns the expected field count for a supported stored task type.
     */
    private int getExpectedFieldCount(String taskType) {
        switch (taskType) {
            case "T":
                return 3;
            case "D":
                return 4;
            case "E":
                return 5;
            default:
                throw new IllegalArgumentException("unknown task type '" + taskType + "'");
        }
    }

    /**
     * Reconstructs a task after its stored fields have been validated.
     */
    private Task createTask(String[] fields) {
        switch (fields[0]) {
            case "T":
                return new Todo(fields[2]);
            case "D":
                return createDeadline(fields);
            case "E":
                return createEvent(fields);
            default:
                throw new AssertionError("Task type was validated before task creation");
        }
    }

    /**
     * Reconstructs a deadline while converting date parsing failures into data warnings.
     */
    private Deadline createDeadline(String[] fields) {
        assert fields.length == 4 : "A validated deadline must have four fields";

        try {
            return new Deadline(fields[2], DateTimeUtil.parse(fields[3]));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "invalid date/time; expected " + DateTimeUtil.INPUT_FORMAT_DESCRIPTION);
        }
    }

    /**
     * Reconstructs an event while converting date parsing failures into data warnings.
     */
    private Event createEvent(String[] fields) {
        assert fields.length == 5 : "A validated event must have five fields";

        try {
            return new Event(fields[2],
                    DateTimeUtil.parse(fields[3]), DateTimeUtil.parse(fields[4]));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "invalid date/time; expected " + DateTimeUtil.INPUT_FORMAT_DESCRIPTION);
        }
    }

    /**
     * Replaces the data file with the current tasks, creating its directory if needed.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the tasks cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> taskLines = tasks.stream()
                .map(Task::toDataString)
                .toList();
        Files.write(filePath, taskLines, StandardCharsets.UTF_8);
    }
}
