package fifi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import fifi.task.Deadline;
import fifi.task.Event;
import fifi.task.Task;
import fifi.task.ToDo;

/**
 * Handles loading and saving chatbot tasks on the hard disk.
 */
public class Storage {
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final String MARKED_STATUS = "1";
    private static final String UNKNOWN_TIMESTAMP = "-";
    private static final String TODO_TASK_CODE = "T";
    private static final String DEADLINE_TASK_CODE = "D";
    private static final String EVENT_TASK_CODE = "E";

    private static final int TASK_TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int DATE_INDEX = 3;
    private static final int END_DATE_INDEX = 4;
    private static final int TODO_CREATED_AT_INDEX = 3;
    private static final int TODO_LAST_MARKED_AT_INDEX = 4;
    private static final int DEADLINE_CREATED_AT_INDEX = 4;
    private static final int DEADLINE_LAST_MARKED_AT_INDEX = 5;
    private static final int EVENT_CREATED_AT_INDEX = 5;
    private static final int EVENT_LAST_MARKED_AT_INDEX = 6;

    private final Path filePath;

    /**
     * Creates a storage handler for the given task data file.
     *
     * @param filePath the path of the task data file
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads the saved task list from the hard disk.
     *
     * @return the tasks saved in the data file, or an empty list if the file does not exist
     * @throws IOException if the file cannot be read
     */
    public List<Task> loadTasks() throws IOException {
        List<Task> tasks = new ArrayList<>();
        List<String> savedTasks;
        try {
            savedTasks = Files.readAllLines(filePath);
        } catch (NoSuchFileException exception) {
            return tasks;
        }
        for (int line = 0; line < savedTasks.size(); line++) {
            String savedTask = savedTasks.get(line);
            if (savedTask.isBlank()) {
                continue;
            }
            try {
                if (tasks.size() >= 100) {
                    throw new IOException("The saved task list exceeds the limit of 100 tasks.");
                }
                tasks.add(parseTask(savedTask));
            } catch (IOException exception) {
                throw new IOException("Invalid task data at line " + (line + 1) + ": "
                        + exception.getMessage(), exception);
            }
        }
        return tasks;
    }

    /**
     * Saves the full task list to the hard disk.
     *
     * @param tasks the current tasks in the chatbot
     * @throws IOException if the file or its parent directory cannot be written
     */
    public void saveTasks(TaskList tasks) throws IOException {
        Path parentDirectory = filePath.toAbsolutePath().getParent();
        Files.createDirectories(parentDirectory);

        StringJoiner savedTasks = new StringJoiner(System.lineSeparator());
        for (Task task : tasks.asList()) {
            savedTasks.add(task.toFileString());
        }

        Path temporaryFile = Files.createTempFile(parentDirectory, ".fifi-", ".tmp");
        try {
            Files.writeString(temporaryFile, savedTasks.toString());
            replaceSavedFile(temporaryFile);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    /**
     * Atomically replaces the save file with a complete file in the same directory.
     * Fails safely when the environment cannot perform an atomic replacement.
     *
     * @param temporaryFile the complete candidate save file
     * @throws IOException if replacement is denied or atomic moves are unsupported
     */
    protected void replaceSavedFile(Path temporaryFile) throws IOException {
        Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Converts one saved line from the data file back into the correct task type.
     *
     * @param savedTask one line from the save file
     * @return the task represented by that line
     */
    private Task parseTask(String savedTask) throws IOException {
        String[] parts = savedTask.split(FIELD_SEPARATOR_REGEX, -1);
        try {
            int legacyFieldCount = switch (parts[TASK_TYPE_INDEX]) {
                case TODO_TASK_CODE -> 3;
                case DEADLINE_TASK_CODE -> 4;
                case EVENT_TASK_CODE -> 5;
                default -> throw new IOException("Unsupported task type: " + parts[TASK_TYPE_INDEX]);
            };
            if (parts.length != legacyFieldCount && parts.length != legacyFieldCount + 2) {
                throw new IOException("Expected " + legacyFieldCount + " or "
                        + (legacyFieldCount + 2) + " fields.");
            }
            if (!parts[STATUS_INDEX].equals("0") && !parts[STATUS_INDEX].equals(MARKED_STATUS)) {
                throw new IOException("Task status must be 0 or 1.");
            }
            boolean isMarked = parts[STATUS_INDEX].equals(MARKED_STATUS);
            return switch (parts[TASK_TYPE_INDEX]) {
                case TODO_TASK_CODE -> new ToDo(isMarked, parts[NAME_INDEX],
                        parseOptionalTimestamp(parts, TODO_CREATED_AT_INDEX),
                        parseOptionalTimestamp(parts, TODO_LAST_MARKED_AT_INDEX));
                case DEADLINE_TASK_CODE -> new Deadline(isMarked, parts[NAME_INDEX],
                        Parser.parseDate(parts[DATE_INDEX]),
                        parseOptionalTimestamp(parts, DEADLINE_CREATED_AT_INDEX),
                        parseOptionalTimestamp(parts, DEADLINE_LAST_MARKED_AT_INDEX));
                case EVENT_TASK_CODE -> new Event(isMarked, parts[NAME_INDEX],
                        Parser.parseDate(parts[DATE_INDEX]), Parser.parseDate(parts[END_DATE_INDEX]),
                        parseOptionalTimestamp(parts, EVENT_CREATED_AT_INDEX),
                        parseOptionalTimestamp(parts, EVENT_LAST_MARKED_AT_INDEX));
                default -> throw new IOException("Unsupported task type: " + parts[TASK_TYPE_INDEX]);
            };
        } catch (DateTimeException | IllegalArgumentException exception) {
            throw new IOException("Invalid task description, dates, or timestamps.", exception);
        }
    }

    private LocalDateTime parseOptionalTimestamp(String[] parts, int index) {
        if (index >= parts.length || parts[index].equals(UNKNOWN_TIMESTAMP)) {
            return null;
        }
        return LocalDateTime.parse(parts[index]);
    }
}
