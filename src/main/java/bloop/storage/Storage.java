package bloop.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import bloop.exception.DukeException;
import bloop.task.Deadline;
import bloop.task.Event;
import bloop.task.Priority;
import bloop.task.Task;
import bloop.task.TaskList;
import bloop.task.Todo;

/** Loads and saves task data in the application's line-based storage file. */
public class Storage {
    private static final String VERSION_TWO = "V2";
    private static final String VERSION_TWO_PREFIX = VERSION_TWO + " | ";
    private static final String VERSION_THREE = "V3";
    private static final String VERSION_THREE_PREFIX = VERSION_THREE + " | ";
    private static final String FIELD_SEPARATOR = " \\| ";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String NOT_DONE = "0";
    private static final String DONE = "1";

    private final Path dataFile;

    /** Creates task storage backed by the supplied data file. */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /** Returns all tasks loaded from storage, or an empty list when no file exists. */
    public List<Task> load() throws DukeException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }
        try {
            for (String taskLine : Files.readAllLines(dataFile)) {
                if (!taskLine.isBlank()) {
                    tasks.add(deserializeTask(taskLine));
                }
            }
            return tasks;
        } catch (IOException | SecurityException | DateTimeParseException exception) {
            throw new DukeException("OOPS!!! I could not load your tasks.");
        }
    }

    /** Saves all tasks to storage. */
    public void save(TaskList tasks) throws DukeException {
        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks.asList()) {
            taskLines.add(serializeTask(task));
        }
        try {
            Files.createDirectories(dataFile.getParent());
            Files.write(dataFile, taskLines);
        } catch (IOException | SecurityException exception) {
            throw new DukeException("OOPS!!! I could not save your tasks.");
        }
    }

    /** Returns a task restored from a legacy, version-two, or version-three data record. */
    private Task deserializeTask(String taskLine) throws DukeException {
        if (taskLine.startsWith(VERSION_THREE_PREFIX)) {
            return deserializeVersionThreeTask(taskLine);
        }
        if (taskLine.startsWith(VERSION_TWO_PREFIX)) {
            return deserializeVersionTwoTask(taskLine);
        }
        String[] taskParts = taskLine.split(FIELD_SEPARATOR, -1);
        validateTaskParts(taskParts, false);
        return createTask(taskParts[0], taskParts[1], taskParts[2], Priority.LOW, taskParts);
    }

    /** Returns a task restored from a version-two encoded data record. */
    private Task deserializeVersionTwoTask(String taskLine) throws DukeException {
        String[] taskParts = taskLine.split(FIELD_SEPARATOR, -1);
        validateTaskParts(taskParts, true);
        String[] decodedParts = taskParts.clone();
        for (int i = 3; i < decodedParts.length; i++) {
            decodedParts[i] = decodeText(decodedParts[i]);
        }
        return createTask(decodedParts[1], decodedParts[2], decodedParts[3], Priority.LOW, decodedParts);
    }

    /** Returns a task restored from a version-three encoded data record. */
    private Task deserializeVersionThreeTask(String taskLine) throws DukeException {
        String[] taskParts = taskLine.split(FIELD_SEPARATOR, -1);
        validateVersionThreeTaskParts(taskParts);
        Priority priority = Priority.fromInput(taskParts[3]);
        String[] decodedParts = taskParts.clone();
        for (int i = 4; i < decodedParts.length; i++) {
            decodedParts[i] = decodeText(decodedParts[i]);
        }
        return createTask(decodedParts[1], decodedParts[2], decodedParts[4], priority, decodedParts);
    }

    /** Validates the type, status, and field count of stored task fields. */
    private void validateTaskParts(String[] taskParts, boolean isVersionTwo) throws DukeException {
        int typeIndex = isVersionTwo ? 1 : 0;
        int statusIndex = isVersionTwo ? 2 : 1;
        int expectedTodoParts = isVersionTwo ? 4 : 3;
        int expectedDeadlineParts = isVersionTwo ? 5 : 4;
        int expectedEventParts = isVersionTwo ? 6 : 5;
        if (taskParts.length < expectedTodoParts
                || (!taskParts[statusIndex].equals(NOT_DONE) && !taskParts[statusIndex].equals(DONE))) {
            throw new DukeException("OOPS!!! Your saved task data is invalid.");
        }
        String type = taskParts[typeIndex];
        int expectedParts = type.equals(TODO_TYPE) ? expectedTodoParts
                : type.equals(DEADLINE_TYPE) ? expectedDeadlineParts
                : type.equals(EVENT_TYPE) ? expectedEventParts : -1;
        if (expectedParts != taskParts.length) {
            throw new DukeException("OOPS!!! Your saved task data is invalid.");
        }
    }

    /** Validates the version-three task fields, including priority. */
    private void validateVersionThreeTaskParts(String[] taskParts) throws DukeException {
        if (taskParts.length < 5 || Priority.fromInput(taskParts[3]) == null) {
            throw new DukeException("OOPS!!! Your saved task data is invalid.");
        }
        String[] versionTwoParts = new String[taskParts.length - 1];
        versionTwoParts[0] = VERSION_TWO;
        versionTwoParts[1] = taskParts[1];
        versionTwoParts[2] = taskParts[2];
        System.arraycopy(taskParts, 4, versionTwoParts, 3, taskParts.length - 4);
        validateTaskParts(versionTwoParts, true);
    }

    /** Returns the task subtype created from validated stored fields. */
    private Task createTask(String type, String doneStatus, String description, Priority priority, String[] taskParts) {
        int detailStartIndex = taskParts[0].equals(VERSION_THREE) ? 5
                : taskParts[0].equals(VERSION_TWO) ? 4 : 3;
        Task task;
        switch (type) {
            case DEADLINE_TYPE:
                task = new Deadline(description, LocalDate.parse(taskParts[detailStartIndex]), priority);
                break;
            case EVENT_TYPE:
                task = new Event(description, LocalDateTime.parse(taskParts[detailStartIndex]),
                        LocalDateTime.parse(taskParts[detailStartIndex + 1]), priority);
                break;
            case TODO_TYPE:
            default:
                task = new Todo(description, priority);
                break;
        }
        if (doneStatus.equals(DONE)) {
            task.markAsDone();
        }
        return task;
    }

    /** Returns a version-three data record for a task. */
    private String serializeTask(Task task) {
        String priority = task.getPriority().getLabel();
        String doneStatus = task.isDone() ? DONE : NOT_DONE;
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return VERSION_THREE + " | " + DEADLINE_TYPE + " | " + doneStatus + " | " + priority + " | "
                    + encodeText(task.getDescription()) + " | " + encodeText(deadline.getBy().toString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return VERSION_THREE + " | " + EVENT_TYPE + " | " + doneStatus + " | " + priority + " | "
                    + encodeText(task.getDescription()) + " | " + encodeText(event.getFrom().toString())
                    + " | " + encodeText(event.getTo().toString());
        }
        return VERSION_THREE + " | " + TODO_TYPE + " | " + doneStatus + " | " + priority + " | "
                + encodeText(task.getDescription());
    }

    /** Returns Base64-encoded text that safely preserves storage separators. */
    private String encodeText(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /** Returns decoded Base64 text or reports invalid saved data. */
    private String decodeText(String text) throws DukeException {
        try {
            return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new DukeException("OOPS!!! Your saved task data is invalid.");
        }
    }
}
