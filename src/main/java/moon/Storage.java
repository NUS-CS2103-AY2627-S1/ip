package moon;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads Moon's tasks from a file in the project directory.
 */
public class Storage {
    /** The relative, operating-system-independent location of Moon's data file. */
    private static final Path FILE_PATH = Path.of("data", "moon.txt");
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";

    /**
     * Saves every task, replacing the previous saved task list.
     *
     * @param tasks the tasks to save
     * @throws IOException if the data folder or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        List<String> savedTasks = tasks.stream()
                .map(Task::toSaveFormat)
                .toList();
        Files.write(FILE_PATH, savedTasks, StandardCharsets.UTF_8);
    }

    /**
     * Loads the tasks saved by Moon, or returns an empty list on Moon's first run.
     *
     * @return the tasks stored in Moon's data file
     * @throws IOException if the data file cannot be read or has an invalid format
     */
    public List<Task> load() throws IOException {
        if (!Files.exists(FILE_PATH)) {
            return new ArrayList<>();
        }

        return loadFromSaveFormats(Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8));
    }

    /** Recreates tasks from their saved data lines. */
    List<Task> loadFromSaveFormats(List<String> savedTasks) throws IOException {
        if (savedTasks == null) {
            throw new IOException("The data file contents are missing.");
        }
        List<Task> tasks = new ArrayList<>();
        for (String savedTask : savedTasks) {
            Task task = createTask(savedTask);
            boolean duplicateTask = tasks.stream()
                    .anyMatch(existingTask -> existingTask.hasSameDetailsAs(task));
            if (duplicateTask) {
                throw new IOException("The data file contains a duplicate task.");
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Recreates one task from a line in Moon's data-file format.
     *
     * @param savedTask one saved task line
     * @return the recreated task
     */
    private Task createTask(String savedTask) throws IOException {
        if (savedTask == null || savedTask.isBlank()) {
            throw new IOException("The data file contains a blank task entry.");
        }
        String[] parts = savedTask.split(FIELD_SEPARATOR_REGEX, -1);
        if (parts.length < 3 || !isValidStatus(parts[1])) {
            throw new IOException("The data file has an invalid task format.");
        }
        Task task = switch (parts[0]) {
        case TODO_TYPE -> createToDo(parts);
        case DEADLINE_TYPE -> createDeadline(parts);
        case EVENT_TYPE -> createEvent(parts);
        default -> throw new IOException("The data file has an unknown task type.");
        };
        assert task != null : "A recognized task type must produce a task.";
        if (parts[1].equals(COMPLETE_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Checks whether a saved completion status is valid.
     *
     * @param status the saved completion status
     * @return whether the status is {@code 0} or {@code 1}
     */
    private boolean isValidStatus(String status) {
        return status.equals(INCOMPLETE_STATUS) || status.equals(COMPLETE_STATUS);
    }

    /**
     * Recreates a to-do from its saved fields.
     *
     * @param parts the fields in the saved task line
     * @return the recreated to-do
     * @throws IOException if the saved fields are invalid
     */
    private ToDo createToDo(String[] parts) throws IOException {
        if (parts.length != 3 || parts[2].isBlank()) {
            throw new IOException("The data file has an invalid to-do format.");
        }
        return new ToDo(parts[2]);
    }

    /**
     * Recreates a deadline from its saved fields.
     *
     * @param parts the fields in the saved task line
     * @return the recreated deadline
     * @throws IOException if the saved fields are invalid
     */
    private Deadline createDeadline(String[] parts) throws IOException {
        if (parts.length != 4 || parts[2].isBlank() || parts[3].isBlank()) {
            throw new IOException("The data file has an invalid deadline format.");
        }
        return new Deadline(parts[2], parseSavedDate(parts[3]));
    }

    /**
     * Recreates an event from its saved fields.
     *
     * @param parts the fields in the saved task line
     * @return the recreated event
     * @throws IOException if the saved fields are invalid
     */
    private Event createEvent(String[] parts) throws IOException {
        if (parts.length != 5 || parts[2].isBlank() || parts[3].isBlank() || parts[4].isBlank()) {
            throw new IOException("The data file has an invalid event format.");
        }
        LocalDate from = parseSavedDate(parts[3]);
        LocalDate to = parseSavedDate(parts[4]);
        if (!from.isBefore(to)) {
            throw new IOException("The data file contains an event with an invalid date range.");
        }
        return new Event(parts[2], from, to);
    }

    /**
     * Parses one ISO-8601 date stored in Moon's data file.
     *
     * @param dateText the saved date
     * @return the parsed date
     * @throws IOException if the saved date is invalid
     */
    private LocalDate parseSavedDate(String dateText) throws IOException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new IOException("The data file has an invalid date.", exception);
        }
    }
}
