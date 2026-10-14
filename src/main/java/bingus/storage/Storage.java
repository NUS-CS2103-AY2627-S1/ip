package bingus.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import bingus.exception.BingusException;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Task;
import bingus.task.TaskType;
import bingus.task.Todo;

/**
 * Loads and saves tasks in a delimiter-safe text file.
 */
public class Storage {

    private static final int TASK_TYPE_INDEX = 0;
    private static final int COMPLETION_STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DATE_TIME_INDEX = 3;
    private static final int EVENT_END_DATE_TIME_INDEX = 4;

    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private static final String COMPLETED_STATUS = "1";
    private static final String INCOMPLETE_STATUS = "0";

    private final Path saveFile;

    /**
     * Creates storage backed by the specified save-file path.
     *
     * @param filePath path of the save file.
     */
    public Storage(String filePath) {
        this.saveFile = Path.of(filePath);
    }

    /**
     * Loads all tasks from the save file. Returns an empty list if the file does not exist.
     *
     * @return loaded tasks, or an empty list if there is no save file.
     * @throws BingusException if the save file cannot be read or contains invalid data.
     */
    public List<Task> loadTasks() throws BingusException {
        if (Files.notExists(saveFile)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(saveFile, StandardCharsets.UTF_8).stream()
                    .map(Storage::fromSaveRecord)
                    .toList();
        } catch (IOException | DateTimeException | IllegalArgumentException | IllegalStateException
                | SecurityException e) {
            throw new BingusException("I could not load your saved tasks. I have started with an empty list.");
        }
    }

    /**
     * Writes the complete task list to disk. Text fields are Base64 encoded so
     * the record separator cannot conflict with text entered by the user.
     *
     * @param tasks complete task list to save.
     * @throws BingusException if the task list cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws BingusException {
        List<String> savedTasks = tasks.stream()
                .map(Storage::toSaveRecord)
                .toList();

        Path temporaryFile = null;
        try {
            Path saveDirectory = saveFile.getParent();
            if (saveDirectory == null) {
                saveDirectory = Path.of(".");
            }
            Files.createDirectories(saveDirectory);
            temporaryFile = Files.createTempFile(saveDirectory, "bingus-", ".tmp");
            Files.write(temporaryFile, savedTasks, StandardCharsets.UTF_8);
            moveSaveFile(temporaryFile);
        } catch (IOException | SecurityException e) {
            throw new BingusException("I could not save that change. Your task list was not changed.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // A later run can safely remove an unused temporary file.
                }
            }
        }
    }

    /**
     * Replaces the save file with a complete temporary file, avoiding a
     * partially written save file if writing is interrupted.
     *
     * @param temporaryFile complete temporary save file.
     * @throws IOException if the file cannot be replaced.
     */
    private void moveSaveFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, saveFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts one task to a delimiter-safe record for the save file.
     *
     * @param task task to convert.
     * @return save-file record.
     */
    private static String toSaveRecord(Task task) {
        String taskTypeSymbol = task.getType().getSymbol();
        String completionStatus = task.isDone() ? COMPLETED_STATUS : INCOMPLETE_STATUS;
        String description = encode(task.getDescription());
        switch (task.getType()) {
            case TODO:
                assert task instanceof Todo : "TODO task must have type Todo";
                return taskTypeSymbol + "|" + completionStatus + "|" + description;
            case DEADLINE:
                assert task instanceof Deadline : "DEADLINE task must be a Deadline";
                Deadline deadline = (Deadline) task;
                return taskTypeSymbol + "|" + completionStatus + "|" + description
                        + "|" + encode(deadline.getBy().toString());
            case EVENT:
                assert task instanceof Event : "EVENT task must be an Event";
                Event event = (Event) task;
                return taskTypeSymbol + "|" + completionStatus + "|" + description
                        + "|" + encode(event.getFrom().toString()) + "|" + encode(event.getTo().toString());
            default:
                throw new IllegalStateException("Unsupported task type: " + task.getType());
        }
    }

    /**
     * Encodes arbitrary task text for safe storage in a pipe-separated record.
     *
     * @param value text to encode.
     * @return Base64 representation of {@code value}.
     */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes text that was Base64 encoded before being saved.
     *
     * @param value Base64 text from the save file.
     * @return original task text.
     */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /**
     * Reconstructs a task from one delimiter-safe save-file record.
     *
     * @param savedTask task record from the save file.
     * @return reconstructed task.
     */
    private static Task fromSaveRecord(String savedTask) {
        String[] fields = savedTask.split("\\|", -1);
        TaskType taskType = TaskType.fromSymbol(fields[TASK_TYPE_INDEX]);
        Task task = createTaskFromFields(fields, taskType, savedTask);
        restoreCompletionStatus(task, fields[COMPLETION_STATUS_INDEX]);
        return task;
    }

    /**
     * Creates a task using the type-specific fields in a save-file record.
     *
     * @param fields fields in the save-file record.
     * @param taskType type of task represented by the record.
     * @param savedTask original save-file record.
     * @return task reconstructed from the record fields.
     */
    private static Task createTaskFromFields(String[] fields, TaskType taskType, String savedTask) {
        switch (taskType) {
            case TODO:
                requireFieldCount(fields, TODO_FIELD_COUNT, savedTask);
                return new Todo(decode(fields[DESCRIPTION_INDEX]));
            case DEADLINE:
                requireFieldCount(fields, DEADLINE_FIELD_COUNT, savedTask);
                return new Deadline(decode(fields[DESCRIPTION_INDEX]),
                        LocalDateTime.parse(decode(fields[DATE_TIME_INDEX])));
            case EVENT:
                requireFieldCount(fields, EVENT_FIELD_COUNT, savedTask);
                LocalDateTime eventStartDateTime = LocalDateTime.parse(decode(fields[DATE_TIME_INDEX]));
                LocalDateTime eventEndDateTime = LocalDateTime.parse(decode(fields[EVENT_END_DATE_TIME_INDEX]));
                if (!eventStartDateTime.isBefore(eventEndDateTime)) {
                    throw new IllegalStateException("Invalid event range in save file");
                }
                return new Event(decode(fields[DESCRIPTION_INDEX]), eventStartDateTime, eventEndDateTime);
            default:
                throw new IllegalStateException("Unsupported task type: " + taskType);
        }
    }

    /**
     * Restores a task's saved completion status.
     *
     * @param task task whose status should be restored.
     * @param completionStatus status field from the save-file record.
     */
    private static void restoreCompletionStatus(Task task, String completionStatus) {
        if (completionStatus.equals(COMPLETED_STATUS)) {
            task.mark();
        } else if (!completionStatus.equals(INCOMPLETE_STATUS)) {
            throw new IllegalStateException("Invalid completion status in save file: " + completionStatus);
        }
    }

    /**
     * Ensures a record has the expected number of fields before it is decoded.
     *
     * @param fields fields in the record.
     * @param expectedCount expected number of fields.
     * @param savedTask original save-file record.
     */
    private static void requireFieldCount(String[] fields, int expectedCount, String savedTask) {
        if (fields.length != expectedCount) {
            throw new IllegalStateException("Invalid task record in save file: " + savedTask);
        }
    }
}
