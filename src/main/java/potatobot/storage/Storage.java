package potatobot.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import potatobot.exception.PotatoBotException;
import potatobot.model.task.Deadline;
import potatobot.model.task.Event;
import potatobot.model.task.Task;
import potatobot.model.task.TaskList;
import potatobot.model.task.Todo;

/**
 * Loads tasks from and saves tasks to PotatoBot's data file.
 */
public class Storage {
    private static final String TODO_SUFFIX = " (Todo)";
    private static final String DEADLINE_MARKER = " (Deadline, by: ";
    private static final String EVENT_MARKER = " (Event, from: ";
    private static final String EVENT_TO_MARKER = " to: ";

    private final Path filePath;
    private final String displayFilePath;

    /**
     * Creates a storage manager for the specified data file.
     *
     * @param filePath Path of the data file.
     */
    public Storage(String filePath) {
        this(filePath, filePath);
    }

    /**
     * Creates a storage manager with separate physical and user-visible paths.
     *
     * @param filePath        Physical path of the data file.
     * @param displayFilePath Data-file path shown to the user.
     */
    public Storage(String filePath, String displayFilePath) {
        this.filePath = Path.of(filePath);
        this.displayFilePath = displayFilePath;
    }

    /**
     * Returns the data-file path intended for display to the user.
     *
     * @return User-visible data-file path.
     */
    public String getDisplayFilePath() {
        return displayFilePath;
    }

    /**
     * Loads all tasks from the data file.
     * A missing file represents a new user with an empty task list.
     *
     * @return Tasks reconstructed from the data file.
     * @throws IOException        If the data file cannot be read.
     * @throws PotatoBotException If the stored task data is invalid.
     */
    public List<Task> load() throws IOException, PotatoBotException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        List<Task> savedTasks = new ArrayList<>();
        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            if (!line.isBlank()) {
                savedTasks.add(parseSavedTask(line));
            }
        }

        if (savedTasks.size() > TaskList.MAX_SIZE) {
            throw new PotatoBotException(
                    "The save file contains more than " + TaskList.MAX_SIZE + " tasks.");
        }
        return savedTasks;
    }

    /**
     * Saves all tasks to the data file, replacing its previous contents.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        Path dataDirectory = filePath.getParent();
        if (dataDirectory != null) {
            Files.createDirectories(dataDirectory);
        }

        Files.writeString(
                filePath,
                tasks.toFileContents(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Converts one stored line back into a task.
     *
     * @param line Line containing a saved task.
     * @return Task represented by the line.
     * @throws PotatoBotException If the line does not use a valid storage format.
     */
    private static Task parseSavedTask(String line) throws PotatoBotException {
        boolean isMarked;
        String taskDetails;

        if (line.startsWith("[X] ")) {
            isMarked = true;
            taskDetails = line.substring(4);

        } else if (line.startsWith("[ ] ")) {
            isMarked = false;
            taskDetails = line.substring(4);

        } else {
            throw new PotatoBotException("Invalid completion status in line: " + line);
        }

        Task task = parseTaskDetails(taskDetails);
        if (isMarked) {
            task.markDone();
        }

        // Reconstruction must preserve the saved status for every task subtype.
        assert task.getStatusIcon().equals(isMarked ? "X" : " ") : "Loaded task must retain its saved status";

        return task;
    }

    /**
     * Reconstructs a task and its subtype-specific details from stored text.
     *
     * @param taskDetails Stored task text without its completion status.
     * @return Reconstructed task.
     * @throws PotatoBotException If subtype-specific details are incomplete.
     */
    private static Task parseTaskDetails(String taskDetails) throws PotatoBotException {
        if (taskDetails.endsWith(TODO_SUFFIX)) {
            return parseTodo(taskDetails);
        }

        int deadlineMarkerIndex = taskDetails.lastIndexOf(DEADLINE_MARKER);
        if (deadlineMarkerIndex >= 0 && taskDetails.endsWith(")")) {
            return parseDeadline(taskDetails, deadlineMarkerIndex);
        }

        int eventMarkerIndex = taskDetails.lastIndexOf(EVENT_MARKER);
        if (eventMarkerIndex >= 0 && taskDetails.endsWith(")")) {
            return parseEvent(taskDetails, eventMarkerIndex);
        }

        return new Task(taskDetails);
    }

    /**
     * Reconstructs a todo from its stored text.
     *
     * @param taskDetails Stored todo text.
     * @return Reconstructed todo.
     */
    private static Todo parseTodo(String taskDetails) {
        String description = taskDetails.substring(0, taskDetails.length() - TODO_SUFFIX.length());
        return new Todo(description);
    }

    /**
     * Reconstructs a deadline from its stored text.
     *
     * @param taskDetails Stored deadline text.
     * @param markerIndex Index at which the deadline details begin.
     * @return Reconstructed deadline.
     * @throws PotatoBotException If the stored date is invalid.
     */
    private static Deadline parseDeadline(String taskDetails, int markerIndex)
            throws PotatoBotException {
        String description = taskDetails.substring(0, markerIndex);
        String by = taskDetails.substring(
                markerIndex + DEADLINE_MARKER.length(), taskDetails.length() - 1);
        return new Deadline(description, by);
    }

    /**
     * Reconstructs an event from its stored text.
     *
     * @param taskDetails Stored event text.
     * @param markerIndex Index at which the event details begin.
     * @return Reconstructed event.
     * @throws PotatoBotException If the stored dates are missing or invalid.
     */
    private static Event parseEvent(String taskDetails, int markerIndex)
            throws PotatoBotException {
        String description = taskDetails.substring(0, markerIndex);
        String eventTimes = taskDetails.substring(
                markerIndex + EVENT_MARKER.length(), taskDetails.length() - 1);
        int toMarkerIndex = eventTimes.lastIndexOf(EVENT_TO_MARKER);
        if (toMarkerIndex < 0) {
            throw new PotatoBotException("Invalid event details: " + taskDetails);
        }

        String from = eventTimes.substring(0, toMarkerIndex);
        String to = eventTimes.substring(toMarkerIndex + EVENT_TO_MARKER.length());
        return new Event(description, from, to);
    }
}
