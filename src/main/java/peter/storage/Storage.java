package peter.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import peter.PeterException;
import peter.task.Deadline;
import peter.task.Event;
import peter.task.ScheduleDateTime;
import peter.task.Task;
import peter.task.TaskType;
import peter.task.Todo;

/**
 * Loads and saves the task list using the application's data file.
 */
public final class Storage {
    private final Path dataFile;
    private final Path temporaryDataFile;

    /**
     * Creates storage backed by the file at the given path.
     *
     * @param filePath path to the task data file.
     */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath);
        this.temporaryDataFile = dataFile.resolveSibling(dataFile.getFileName() + ".tmp");
    }

    /**
     * Loads tasks from the data file, or returns an empty list when the file
     * does not exist yet.
     *
     * @return tasks stored in the data file.
     * @throws PeterException if the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws PeterException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        try {
            List<String> taskLines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            for (int i = 0; i < taskLines.size(); i++) {
                String taskLine = taskLines.get(i);
                if (i == 0 && taskLine.startsWith("\uFEFF")) {
                    taskLine = taskLine.substring(1);
                }
                if (!taskLine.isBlank()) {
                    tasks.add(parseTask(taskLine, i + 1));
                }
            }
        } catch (IOException | SecurityException e) {
            throw new PeterException(
                    "Oh dear! I couldn't read the task data file. Please check that it is accessible.", e);
        }
        return tasks;
    }

    private static Task parseTask(String taskLine, int lineNumber) throws PeterException {
        String[] taskParts = taskLine.split(Pattern.quote(Task.FIELD_DELIMITER), -1);
        if (taskParts.length < 2 || (!taskParts[1].equals(Task.STATUS_FLAG_NOT_DONE)
                && !taskParts[1].equals(Task.STATUS_FLAG_DONE))) {
            throw invalidDataException(lineNumber);
        }

        TaskType taskType = TaskType.fromIcon(taskParts[0])
                .orElseThrow(() -> invalidDataException(lineNumber));
        if (taskParts.length != taskType.getDataFieldCount()) {
            throw invalidDataException(lineNumber);
        }
        for (int i = 2; i < taskParts.length; i++) {
            if (taskParts[i].isBlank()) {
                throw invalidDataException(lineNumber);
            }
        }

        Task task;
        try {
            task = switch (taskType) {
                case TODO -> new Todo(taskParts[2]);
                case DEADLINE -> new Deadline(taskParts[2],
                        ScheduleDateTime.parseStoredValue(taskParts[3]));
                case EVENT -> new Event(taskParts[2],
                        ScheduleDateTime.parseStoredValue(taskParts[3]),
                        ScheduleDateTime.parseStoredValue(taskParts[4]));
            };
        } catch (DateTimeParseException e) {
            throw invalidDataException(lineNumber);
        }
        if (taskParts[1].equals(Task.STATUS_FLAG_DONE)) {
            task.markAsDone();
        }
        return task;
    }

    private static PeterException invalidDataException(int lineNumber) {
        return new PeterException("Oh dear! The task data file is invalid at line " + lineNumber
                + ". Please fix or remove it before restarting me!");
    }

    /**
     * Rewrites the data file so that it matches the current task list.
     *
     * @param tasks tasks to save in their current order.
     * @throws PeterException if the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws PeterException {
        try {
            Path dataDirectory = dataFile.getParent();
            if (dataDirectory != null) {
                Files.createDirectories(dataDirectory);
            }
            List<String> taskLines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            Files.write(temporaryDataFile, taskLines, StandardCharsets.UTF_8);
            moveTemporaryFileIntoPlace();
        } catch (IOException | SecurityException e) {
            try {
                Files.deleteIfExists(temporaryDataFile);
            } catch (IOException | SecurityException ignored) {
                // Keep the original save error, which is more useful to the user.
            }
            throw new PeterException(
                    "Oh dear! I couldn't save your tasks. Please check that the data folder is writable.", e);
        }
    }

    /**
     * Replaces the data file atomically when supported by the file system.
     */
    private void moveTemporaryFileIntoPlace() throws IOException {
        // Replacing the data file is only safe once the replacement exists in
        // full. Calling this before the temporary file has been written would
        // destroy the saved tasks instead of updating them.
        assert Files.exists(temporaryDataFile) : "the temporary file must be written before it is moved";

        try {
            Files.move(temporaryDataFile, dataFile,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryDataFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
