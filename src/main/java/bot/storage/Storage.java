package bot.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import bot.task.Deadline;
import bot.task.Event;
import bot.task.Task;
import bot.task.TaskList;
import bot.task.Todo;

/**
 * Loads and saves the chatbot's tasks using a file on disk.
 */
public class Storage {
    private static final String FIELD_SEPARATOR_REGEX = "\\s*\\|\\s*";
    private static final String NOT_DONE_STATUS = "0";
    private static final String DONE_STATUS = "1";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";

    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DEADLINE_DATE_FIELD_INDEX = 3;
    private static final int EVENT_START_FIELD_INDEX = 3;
    private static final int EVENT_END_FIELD_INDEX = 4;

    private static final int COMMON_FIELD_COUNT = 3;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private final File dataFile;

    /**
     * Creates storage that writes to the given file.
     *
     * @param dataFile location of the data file.
     */
    public Storage(File dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads tasks from the data file, preserving their types and done statuses.
     *
     * @return tasks stored in the data file, or an empty list if the file does not exist
     * @throws IOException if the data file cannot be read or contains an unknown task type
     */
    public TaskList loadTasks() throws IOException {
        TaskList tasks = new TaskList();
        if (!dataFile.exists()) {
            return tasks;
        }
        if (!dataFile.isFile()) {
            throw new IOException("data path is not a file: " + dataFile);
        }

        try (Scanner scanner = new Scanner(dataFile)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseTask(line, lineNumber);
                if (tasks.containsSameTask(task)) {
                    throw invalidData(lineNumber, "duplicates an earlier task");
                }
                tasks.add(task);
            }
            if (scanner.ioException() != null) {
                throw new IOException("could not finish reading data file: " + dataFile,
                        scanner.ioException());
            }
        }
        return tasks;
    }

    /**
     * Converts one validated data-file line into a task.
     *
     * @param line line read from the data file.
     * @param lineNumber one-based line number used in error messages.
     * @return task represented by the line
     * @throws IOException if the line does not follow the storage format
     */
    private Task parseTask(String line, int lineNumber) throws IOException {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (fields.length < COMMON_FIELD_COUNT) {
            throw invalidData(lineNumber, "every task must contain a type, status, and description");
        }

        boolean isDone = parseDoneStatus(fields[STATUS_FIELD_INDEX], lineNumber);
        String description = fields[DESCRIPTION_FIELD_INDEX];
        if (description.isEmpty()) {
            throw invalidData(lineNumber, "description cannot be empty");
        }

        Task task = createTask(fields, description, lineNumber);
        if (isDone) {
            task.mark();
        }
        return task;
    }

    /**
     * Converts a saved status field into its boolean form.
     *
     * @param status saved status field.
     * @param lineNumber one-based data-file line number.
     * @return {@code true} if the saved task is done
     * @throws IOException if the status is neither 0 nor 1
     */
    private boolean parseDoneStatus(String status, int lineNumber) throws IOException {
        if (DONE_STATUS.equals(status)) {
            return true;
        }
        if (NOT_DONE_STATUS.equals(status)) {
            return false;
        }
        throw invalidData(lineNumber, "status must be 0 or 1");
    }

    /**
     * Creates the task subtype identified by the saved type field.
     *
     * @param fields fields parsed from the saved task.
     * @param description validated task description.
     * @param lineNumber one-based data-file line number.
     * @return task represented by the fields
     * @throws IOException if the type or its fields are invalid
     */
    private Task createTask(String[] fields, String description, int lineNumber) throws IOException {
        String type = fields[TYPE_FIELD_INDEX];
        switch (type) {
            case TODO_TYPE:
                return createTodo(fields, description, lineNumber);
            case DEADLINE_TYPE:
                return createDeadline(fields, description, lineNumber);
            case EVENT_TYPE:
                return createEvent(fields, description, lineNumber);
            default:
                throw invalidData(lineNumber, "unknown task type '" + type + "'");
        }
    }

    /**
     * Creates a todo from its saved fields.
     *
     * @param fields fields parsed from the saved todo.
     * @param description validated task description.
     * @param lineNumber one-based data-file line number.
     * @return saved todo
     * @throws IOException if the field count is invalid
     */
    private Todo createTodo(String[] fields, String description, int lineNumber) throws IOException {
        ensureFieldCount(fields, TODO_FIELD_COUNT, lineNumber, "todo");
        return new Todo(description);
    }

    /**
     * Creates a deadline from its saved fields.
     *
     * @param fields fields parsed from the saved deadline.
     * @param description validated task description.
     * @param lineNumber one-based data-file line number.
     * @return saved deadline
     * @throws IOException if the deadline fields are invalid
     */
    private Deadline createDeadline(String[] fields, String description, int lineNumber) throws IOException {
        ensureFieldCount(fields, DEADLINE_FIELD_COUNT, lineNumber, "deadline");
        String deadlineDate = fields[DEADLINE_DATE_FIELD_INDEX];
        if (deadlineDate.isEmpty()) {
            throw invalidData(lineNumber, "deadline date cannot be empty");
        }
        return new Deadline(description, parseDeadlineDate(deadlineDate, lineNumber));
    }

    /**
     * Creates an event from its saved fields.
     *
     * @param fields fields parsed from the saved event.
     * @param description validated task description.
     * @param lineNumber one-based data-file line number.
     * @return saved event
     * @throws IOException if the event fields are invalid
     */
    private Event createEvent(String[] fields, String description, int lineNumber) throws IOException {
        ensureFieldCount(fields, EVENT_FIELD_COUNT, lineNumber, "event");
        String savedStart = fields[EVENT_START_FIELD_INDEX];
        String savedEnd = fields[EVENT_END_FIELD_INDEX];
        if (savedStart.isEmpty() || savedEnd.isEmpty()) {
            throw invalidData(lineNumber, "event start and end times cannot be empty");
        }

        LocalDateTime start = parseEventDateTime(savedStart, lineNumber);
        LocalDateTime end = parseEventDateTime(savedEnd, lineNumber);
        if (!end.isAfter(start)) {
            throw invalidData(lineNumber, "event end must be after its start");
        }
        return new Event(description, start, end);
    }

    /**
     * Parses a deadline date stored in ISO format.
     *
     * @param value saved date text.
     * @param lineNumber one-based data-file line number.
     * @return parsed deadline date
     * @throws IOException if the saved date is invalid
     */
    private LocalDate parseDeadlineDate(String value, int lineNumber) throws IOException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException error) {
            throw invalidData(lineNumber, "deadline date must use yyyy-MM-dd");
        }
    }

    /**
     * Parses an event date and time stored in ISO format.
     *
     * @param value saved date-time text.
     * @param lineNumber one-based data-file line number.
     * @return parsed event date and time
     * @throws IOException if the saved date and time are invalid
     */
    private LocalDateTime parseEventDateTime(String value, int lineNumber) throws IOException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException error) {
            throw invalidData(lineNumber, "event date and time must use ISO format");
        }
    }

    /**
     * Checks that a saved task contains exactly the fields required by its type.
     *
     * @param fields fields parsed from the saved task.
     * @param expectedCount required number of fields.
     * @param lineNumber one-based data-file line number.
     * @param taskType name of the task type for the error message.
     * @throws IOException if the field count is incorrect
     */
    private void ensureFieldCount(String[] fields, int expectedCount, int lineNumber, String taskType)
            throws IOException {
        if (fields.length != expectedCount) {
            throw invalidData(lineNumber, taskType + " must contain " + expectedCount + " fields");
        }
    }

    /**
     * Creates a consistently formatted exception for invalid saved data.
     *
     * @param lineNumber one-based data-file line number.
     * @param reason explanation of the format problem.
     * @return exception describing the malformed line
     */
    private IOException invalidData(int lineNumber, String reason) {
        return new IOException("invalid data on line " + lineNumber + " (" + reason + ")");
    }

    /**
     * Overwrites the data file with the current task list.
     *
     * @param tasks tasks to save.
     * @throws IOException if the directory or file cannot be written
     */
    public void saveTasks(TaskList tasks) throws IOException {
        File parentDirectory = dataFile.getParentFile();
        if (parentDirectory != null) {
            if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
                throw new IOException("Could not create data directory: " + parentDirectory);
            }
            if (!parentDirectory.isDirectory()) {
                throw new IOException("Data directory path is not a directory: " + parentDirectory);
            }
        }
        if (dataFile.exists() && !dataFile.isFile()) {
            throw new IOException("Data file path is not a file: " + dataFile);
        }

        try (FileWriter writer = new FileWriter(dataFile)) {
            for (Task task : tasks) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        }
    }
}
