package minty.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import minty.exception.MintyException;
import minty.task.Deadline;
import minty.task.Event;
import minty.task.Task;
import minty.task.Todo;

/**
 * Loads and saves Minty's task list in a local text file.
 */
public class Storage {
    private static final int TASK_TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DATE_FIELD_INDEX = 3;
    private static final int EVENT_END_DATE_FIELD_INDEX = 4;

    private static final int MINIMUM_FIELD_COUNT = 2;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";

    private final Path filePath;
    private boolean isSaveBlocked;

    /**
     * Creates storage that reads from and writes to the specified file.
     *
     * @param filePath path to the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the save file.
     *
     * <p>An empty task list is returned when Minty has not created a save file yet.
     *
     * @return tasks reconstructed from the save file.
     * @throws IOException if an existing save file cannot be read.
     * @throws MintyException if the save file contains invalid task data.
     */
    public ArrayList<Task> loadTasks() throws IOException, MintyException {
        try {
            return readTasks();
        } catch (IOException | MintyException | SecurityException exception) {
            isSaveBlocked = true;
            throw exception;
        }
    }

    /**
     * Reads validated task data without treating inaccessible files as missing.
     *
     * @return tasks from the file, or an empty list on first use.
     * @throws IOException if the file cannot be read.
     * @throws MintyException if saved data is malformed.
     */
    private ArrayList<Task> readTasks() throws IOException, MintyException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath) && !Files.isSymbolicLink(filePath)) {
            return tasks;
        }

        if (!Files.isRegularFile(filePath)) {
            throw new IOException("The data path is not a readable regular file: " + filePath);
        }
        int lineNumber = 0;
        for (String taskData : Files.readAllLines(filePath)) {
            lineNumber++;
            if (taskData.isBlank()) {
                continue;
            }
            Task task = parseTask(taskData, lineNumber);
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Replaces the save file with the current task list.
     *
     * @param tasks tasks to save.
     * @throws IOException if the data directory or file cannot be written.
     */
    public void saveTasks(Iterable<Task> tasks) throws IOException {
        if (isSaveBlocked) {
            throw new IOException("Saving is paused to protect unreadable data. "
                    + "Repair or move the data file, then restart Minty.");
        }
        if (Files.isSymbolicLink(filePath)) {
            throw new IOException("The data file is a symbolic link. Use a regular file instead.");
        }
        if (Files.exists(filePath) && (!Files.isRegularFile(filePath) || !Files.isWritable(filePath))) {
            throw new IOException("The data path must be a writable regular file: " + filePath);
        }
        Path parentDirectory = filePath.toAbsolutePath().getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> taskData = new ArrayList<>();
        for (Task task : tasks) {
            taskData.add(task.toDataString());
        }
        // Write completely before replacing the old file, preserving it if writing fails.
        Path temporaryFile = Files.createTempFile(parentDirectory, "minty-", ".tmp");
        try {
            Files.write(temporaryFile, taskData);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Reconstructs and validates a task from one save-file line.
     *
     * @param taskData one line from the save file.
     * @param lineNumber one-based line number used in error messages.
     * @return reconstructed task.
     * @throws MintyException if the saved task is malformed.
     */
    private Task parseTask(String taskData, int lineNumber) throws MintyException {
        ArrayList<String> fields = splitFields(taskData, lineNumber);
        validateFields(fields, lineNumber);

        Task task = createTask(fields, lineNumber);
        if (fields.get(STATUS_FIELD_INDEX).equals(COMPLETE_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Validates the structure and values of all fields in one saved task.
     *
     * @param fields saved task fields.
     * @param lineNumber one-based line number used in error messages.
     * @throws MintyException if any field is missing or invalid.
     */
    private void validateFields(ArrayList<String> fields, int lineNumber) throws MintyException {
        if (fields.size() < MINIMUM_FIELD_COUNT) {
            throw invalidLine(lineNumber, "missing task fields");
        }

        int expectedFieldCount = getExpectedFieldCount(
                fields.get(TASK_TYPE_FIELD_INDEX), lineNumber);
        if (fields.size() != expectedFieldCount) {
            throw invalidLine(lineNumber, "expected " + expectedFieldCount
                    + " fields but found " + fields.size());
        }

        String status = fields.get(STATUS_FIELD_INDEX);
        if (!status.equals(INCOMPLETE_STATUS) && !status.equals(COMPLETE_STATUS)) {
            throw invalidLine(lineNumber, "status must be 0 or 1");
        }

        for (int i = DESCRIPTION_FIELD_INDEX; i < fields.size(); i++) {
            if (fields.get(i).isEmpty()) {
                throw invalidLine(lineNumber, "task details cannot be empty");
            }
        }
    }

    /**
     * Returns the number of fields required by a saved task type.
     *
     * @param taskType saved task type code.
     * @param lineNumber one-based line number used in error messages.
     * @return required number of fields.
     * @throws MintyException if the task type is unknown.
     */
    private int getExpectedFieldCount(String taskType, int lineNumber) throws MintyException {
        switch (taskType) {
            case TODO_TYPE:
                return TODO_FIELD_COUNT;
            case DEADLINE_TYPE:
                return DEADLINE_FIELD_COUNT;
            case EVENT_TYPE:
                return EVENT_FIELD_COUNT;
            default:
                throw invalidLine(lineNumber, "unknown task type '" + taskType + "'");
        }
    }

    /**
     * Creates a task from fields that have passed structural validation.
     *
     * @param fields validated saved task fields.
     * @param lineNumber one-based line number used in error messages.
     * @return reconstructed task.
     * @throws MintyException if a saved date is invalid.
     */
    private Task createTask(ArrayList<String> fields, int lineNumber) throws MintyException {
        switch (fields.get(TASK_TYPE_FIELD_INDEX)) {
            case TODO_TYPE:
                return new Todo(fields.get(DESCRIPTION_FIELD_INDEX));
            case DEADLINE_TYPE:
                return new Deadline(fields.get(DESCRIPTION_FIELD_INDEX),
                        parseDate(fields.get(DATE_FIELD_INDEX), lineNumber));
            case EVENT_TYPE:
                return createEvent(fields, lineNumber);
            default:
                throw new AssertionError("Task type was already validated");
        }
    }

    /**
     * Creates an event after validating the order of its saved dates.
     *
     * @param fields validated saved event fields.
     * @param lineNumber one-based line number used in error messages.
     * @return reconstructed event.
     * @throws MintyException if a saved date is invalid or out of order.
     */
    private Event createEvent(ArrayList<String> fields, int lineNumber) throws MintyException {
        LocalDate from = parseDate(fields.get(DATE_FIELD_INDEX), lineNumber);
        LocalDate to = parseDate(fields.get(EVENT_END_DATE_FIELD_INDEX), lineNumber);
        if (to.isBefore(from)) {
            throw invalidLine(lineNumber, "event end date is before its start date");
        }
        return new Event(fields.get(DESCRIPTION_FIELD_INDEX), from, to);
    }

    /**
     * Parses a saved date in ISO format.
     *
     * @param dateText saved date text.
     * @param lineNumber one-based line number used in error messages.
     * @return parsed date.
     * @throws MintyException if the saved date is invalid.
     */
    private LocalDate parseDate(String dateText, int lineNumber) throws MintyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw invalidLine(lineNumber, "date must use yyyy-MM-dd");
        }
    }

    /**
     * Splits fields at unescaped pipe characters and removes format padding.
     *
     * @param taskData serialized task.
     * @param lineNumber one-based line number used in error messages.
     * @return unescaped task fields.
     * @throws MintyException if an escape sequence is incomplete or unsupported.
     */
    private ArrayList<String> splitFields(String taskData, int lineNumber) throws MintyException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < taskData.length(); i++) {
            char character = taskData.charAt(i);
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw invalidLine(lineNumber, "unsupported escape sequence '\\"
                            + character + "'");
                }
                currentField.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }

        if (isEscaped) {
            throw invalidLine(lineNumber, "unfinished escape character");
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    /**
     * Creates a consistent, user-friendly error for malformed saved data.
     *
     * @param lineNumber line containing the error.
     * @param reason explanation of the invalid data.
     * @return Minty-specific exception.
     */
    private MintyException invalidLine(int lineNumber, String reason) {
        return new MintyException("I couldn't read the saved task on line " + lineNumber + ": " + reason + ".");
    }
}
