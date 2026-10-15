package remy.storage;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import remy.parser.DateParser;
import remy.task.Deadline;
import remy.task.Event;
import remy.task.Task;
import remy.task.TaskList;
import remy.task.Todo;

/**
 * Loads tasks from and saves tasks to Remy's task file.
 *
 * @author LEE-wz
 */
public class Storage {
    /** Marker identifying a persisted to-do task. */
    private static final String TODO_TASK_MARKER = "[T]";

    /** Marker identifying a persisted deadline task. */
    private static final String DEADLINE_TASK_MARKER = "[D]";

    /** Marker identifying a persisted event task. */
    private static final String EVENT_TASK_MARKER = "[E]";

    /** Marker identifying an incomplete persisted task. */
    private static final String INCOMPLETE_STATUS_MARKER = "[ ] ";

    /** Marker identifying a completed persisted task. */
    private static final String COMPLETED_STATUS_MARKER = "[X] ";

    /** Index immediately after a persisted task-type marker. */
    private static final int TASK_TYPE_MARKER_END_INDEX = 3;

    /** Index at which persisted task details begin. */
    private static final int TASK_DETAILS_START_INDEX = 7;

    /** File used to persist tasks between chat sessions. */
    private final Path taskFile;

    /**
     * Creates storage that persists tasks at the given file path.
     *
     * @param filePath file path used to persist tasks
     */
    public Storage(String filePath) {
        this.taskFile = Path.of(filePath);
    }

    /**
     * Saves tasks to the task file using an atomic replacement when supported.
     *
     * @param tasks tasks to save
     * @throws StorageException if the task file cannot be written
     */
    public void save(TaskList tasks) throws StorageException {
        assert tasks != null : "Task list to save must not be null";

        Path temporaryFile = null;
        try {
            Path absoluteTaskFile = taskFile.toAbsolutePath();
            Path parent = absoluteTaskFile.getParent();
            assert parent != null : "Absolute task-file path must have a parent";

            Files.createDirectories(parent);

            String taskData = tasks.getTasks().stream()
                    .map(Task::toString)
                    .collect(Collectors.joining(System.lineSeparator()));

            temporaryFile = Files.createTempFile(parent, "remy-", ".tmp");
            Files.writeString(temporaryFile, taskData, StandardCharsets.UTF_8);

            try {
                Files.move(temporaryFile, absoluteTaskFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, absoluteTaskFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            deleteTemporaryFile(temporaryFile);
            throw createSaveException(e);
        }
    }

    /**
     * Loads valid tasks from the task file.
     *
     * @return recovered tasks, or an empty task list when the file does not exist
     * @throws StorageException if the task file cannot be read
     */
    public TaskList load() throws StorageException {
        return loadWithRecoveryDetails().getTasks();
    }

    /**
     * Loads valid unique tasks and identifies lines containing invalid or duplicate data.
     *
     * @return recovered tasks and invalid line numbers, or an empty result when the file does not exist
     * @throws StorageException if the task file cannot be read
     */
    public StorageLoadResult loadWithRecoveryDetails() throws StorageException {
        try {
            if (Files.notExists(taskFile)) {
                return createEmptyLoadResult();
            }
            if (Files.isDirectory(taskFile)) {
                throw new StorageException(
                        "Unable to load tasks: \"" + taskFile + "\" is a directory, not a file.", null);
            }
            if (!Files.isReadable(taskFile)) {
                throw new AccessDeniedException(taskFile.toString());
            }

            List<String> lines = Files.readAllLines(taskFile, StandardCharsets.UTF_8);
            TaskList loadedTasks = new TaskList();
            List<Integer> invalidLineNumbers = new ArrayList<>();
            for (int index = 0; index < lines.size(); index++) {
                Task task = parseTask(lines.get(index));
                if (task == null || !loadedTasks.add(task)) {
                    invalidLineNumbers.add(index + 1);
                }
            }
            return new StorageLoadResult(loadedTasks, invalidLineNumbers);
        } catch (NoSuchFileException e) {
            // The file may be removed after the existence check; treat that race as a first run.
            return createEmptyLoadResult();
        } catch (StorageException e) {
            throw e;
        } catch (IOException | SecurityException e) {
            throw createLoadException(e);
        }
    }

    /**
     * Returns the configured task-file path for user-facing storage messages.
     *
     * @return configured task-file path
     */
    public String getTaskFilePath() {
        return taskFile.toString();
    }

    /** Returns an empty task-file load result. */
    private StorageLoadResult createEmptyLoadResult() {
        return new StorageLoadResult(new TaskList(), List.of());
    }

    /**
     * Creates an actionable exception for a task-file read failure.
     *
     * @param cause failure reported by the environment
     * @return storage exception containing a user-facing explanation
     */
    private StorageException createLoadException(Throwable cause) {
        String message;
        if (cause instanceof AccessDeniedException) {
            message = "Unable to load tasks from \"" + taskFile
                    + "\": access was denied. Check that Remy has permission to read the file.";
        } else if (cause instanceof MalformedInputException) {
            message = "Unable to load tasks from \"" + taskFile
                    + "\": the file is not valid UTF-8 text.";
        } else {
            message = "Unable to load tasks from \"" + taskFile
                    + "\". Check that the path is a readable task file.";
        }
        return new StorageException(message, cause);
    }

    /**
     * Creates an actionable exception for a task-file write failure.
     *
     * @param cause failure reported by the environment
     * @return storage exception containing a user-facing explanation
     */
    private StorageException createSaveException(Throwable cause) {
        String message = "Unable to save tasks to \"" + taskFile + "\". ";
        if (cause instanceof AccessDeniedException) {
            message += "Access was denied; check that Remy has permission to write to the file and its folder. ";
        } else {
            message += "Check that the path is writable and its parent is a folder. ";
        }
        message += "Your changes are kept only for this session.";
        return new StorageException(message, cause);
    }

    /**
     * Deletes a temporary save file, preserving the original save failure if cleanup fails.
     *
     * @param temporaryFile temporary file to delete
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original save error is more useful to the user.
        }
    }

    /**
     * Parses a line from the saved task file and creates its corresponding task.
     *
     * @param line line from the saved task file
     * @return the parsed task, or null when the line is invalid
     */
    private Task parseTask(String line) {
        if (!hasValidTaskStructure(line)) {
            return null;
        }

        String taskTypeMarker = line.substring(0, TASK_TYPE_MARKER_END_INDEX);
        String taskStatusMarker = line.substring(TASK_TYPE_MARKER_END_INDEX, TASK_DETAILS_START_INDEX);
        String taskDetails = line.substring(TASK_DETAILS_START_INDEX);
        if (taskDetails.strip().isEmpty()) {
            return null;
        }

        Task task;
        switch (taskTypeMarker) {
            case TODO_TASK_MARKER -> task = new Todo(taskDetails);
            case DEADLINE_TASK_MARKER -> task = parseDeadlineTask(taskDetails);
            case EVENT_TASK_MARKER -> task = parseEventTask(taskDetails);
            default -> {
                return null;
            }
        }

        if (task == null) {
            return null;
        }

        if (taskStatusMarker.equals(COMPLETED_STATUS_MARKER)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns whether a saved line has enough content and a recognized completion marker.
     *
     * @param line saved task line to inspect
     * @return true when the line can be split safely into its persisted components
     */
    private boolean hasValidTaskStructure(String line) {
        if (line == null || line.length() <= TASK_DETAILS_START_INDEX) {
            return false;
        }

        String taskStatusMarker = line.substring(TASK_TYPE_MARKER_END_INDEX, TASK_DETAILS_START_INDEX);
        return taskStatusMarker.equals(INCOMPLETE_STATUS_MARKER)
                || taskStatusMarker.equals(COMPLETED_STATUS_MARKER);
    }

    /**
     * Parses a saved deadline task.
     *
     * @param taskDetails description and deadline from the saved task
     * @return the parsed deadline task, or null when its format is invalid
     */
    private Deadline parseDeadlineTask(String taskDetails) {
        String marker = " (by: ";
        if (!taskDetails.endsWith(")") || !taskDetails.contains(marker)) {
            return null;
        }

        int markerIndex = taskDetails.lastIndexOf(marker);
        if (markerIndex == 0 || markerIndex + marker.length() == taskDetails.length() - 1) {
            return null;
        }

        String description = taskDetails.substring(0, markerIndex);
        String deadline = taskDetails.substring(markerIndex + marker.length(), taskDetails.length() - 1);
        return parseSavedDeadline(description, deadline);
    }

    /**
     * Parses a saved event task.
     *
     * @param taskDetails description and endpoints from the saved task
     * @return the parsed event task, or null when its format is invalid
     */
    private Event parseEventTask(String taskDetails) {
        String startMarker = " (from: ";
        String endMarker = " to: ";
        if (!taskDetails.endsWith(")") || !taskDetails.contains(startMarker)
                || !taskDetails.contains(endMarker)) {
            return null;
        }

        int startIndex = taskDetails.lastIndexOf(startMarker);
        int endIndex = taskDetails.lastIndexOf(endMarker);
        if (endIndex < startIndex) {
            return null;
        }
        if (startIndex == 0 || startIndex + startMarker.length() == endIndex
                || endIndex + endMarker.length() == taskDetails.length() - 1) {
            return null;
        }

        String description = taskDetails.substring(0, startIndex);
        String start = taskDetails.substring(startIndex + startMarker.length(), endIndex);
        String end = taskDetails.substring(endIndex + endMarker.length(), taskDetails.length() - 1);
        return parseSavedEvent(description, start, end);
    }

    /**
     * Parses a deadline from its displayed or legacy persisted representation.
     *
     * @param description description of the deadline
     * @param deadline deadline string from the task file
     * @return the parsed deadline, or null when the date is invalid
     */
    private Deadline parseSavedDeadline(String description, String deadline) {
        LocalDateTime deadlineDateTime = DateParser.parseDateTime(deadline);
        if (deadlineDateTime != null) {
            return new Deadline(description, deadlineDateTime);
        }

        LocalDate deadlineDate = DateParser.parseDate(deadline);
        if (deadlineDate != null) {
            return new Deadline(description, deadlineDate);
        }

        return null;
    }

    /**
     * Parses event endpoints from their displayed or legacy persisted representation.
     *
     * @param description description of the event
     * @param start starting endpoint string from the task file
     * @param end ending endpoint string from the task file
     * @return the parsed event, or null when the endpoints are invalid
     */
    private Event parseSavedEvent(String description, String start, String end) {
        LocalDateTime startDateTime = DateParser.parseDateTime(start);
        LocalDateTime endDateTime = DateParser.parseDateTime(end);
        if (startDateTime != null && endDateTime != null && startDateTime.isBefore(endDateTime)) {
            return new Event(description, startDateTime, endDateTime);
        }

        LocalDate startDate = DateParser.parseDate(start);
        LocalDate endDate = DateParser.parseDate(end);
        if (startDate != null && endDate != null && startDate.isBefore(endDate)) {
            return new Event(description, startDate, endDate);
        }
        return null;
    }
}
