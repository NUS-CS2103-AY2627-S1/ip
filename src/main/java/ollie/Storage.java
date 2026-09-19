package ollie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to a local data file.
 */
public class Storage {
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DATE_FIELD_INDEX = 3;
    private static final int END_DATE_FIELD_INDEX = 4;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";

    private final Path filePath;

    /**
     * Creates a storage manager for the given file path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file.
     *
     * @return Tasks stored in the data file, or an empty list if the file does not exist.
     * @throws OllieException If the file cannot be read or contains invalid task data.
     */
    public List<Task> load() throws OllieException {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            } else if (Files.isDirectory(filePath)) {
                throw new OllieException("The task data path points to a folder instead of a file: "
                        + filePath + ".");
            }

            List<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    Task task = parseTask(lines.get(i), i + 1);
                    if (tasks.stream().anyMatch(task::hasSameDetails)) {
                        throw new OllieException("The saved task on line " + (i + 1)
                                + " duplicates an earlier task.");
                    }
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new OllieException("I couldn't read the task file at " + filePath
                    + ". Check that the file is accessible.");
        }
    }

    /**
     * Saves the given tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws OllieException If the data file cannot be written or a task type is unsupported.
     */
    public void save(List<Task> tasks) throws OllieException {
        try {
            if (Files.isDirectory(filePath)) {
                throw new OllieException("The task data path points to a folder instead of a file: "
                        + filePath + ".");
            }

            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> lines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if (task == null) {
                    throw new OllieException("I couldn't save an empty task entry.");
                }
                ensureTaskIsUnique(tasks, task, i);
                lines.add(formatTask(task));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            throw new OllieException("I couldn't write the task file at " + filePath
                    + ". Check that its folder is accessible and writable.");
        }
    }

    /**
     * Converts a task into its storage representation.
     *
     * @param task Task to convert.
     * @return Serialized task.
     * @throws OllieException If the task type is unsupported.
     */
    private String formatTask(Task task) throws OllieException {
        validateStoredDescription(task.getDescription());
        String status = task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;
        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, TODO_TYPE, status, task.getDescription());
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            if (deadline.getDueDate() == null) {
                throw new OllieException("I couldn't save a deadline without a due date.");
            }
            return String.join(FIELD_SEPARATOR, DEADLINE_TYPE, status, deadline.getDescription(),
                    deadline.getDueDate().toString());
        } else if (task instanceof Event) {
            Event event = (Event) task;
            if (event.getStartDate() == null || event.getEndDate() == null
                    || !event.getStartDate().isBefore(event.getEndDate())) {
                throw new OllieException("I couldn't save an event with an invalid date range.");
            }
            return String.join(FIELD_SEPARATOR, EVENT_TYPE, status, event.getDescription(),
                    event.getStartDate().toString(), event.getEndDate().toString());
        }

        throw new OllieException("I couldn't save an unsupported task type.");
    }

    /**
     * Converts one line of stored data into a task.
     *
     * @param line Stored task data.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed task.
     * @throws OllieException If the stored task data is invalid.
     */
    private Task parseTask(String line, int lineNumber) throws OllieException {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (fields.length < TODO_FIELD_COUNT
                || fields[DESCRIPTION_FIELD_INDEX].isBlank()
                || fields[DESCRIPTION_FIELD_INDEX].contains("|")) {
            throw invalidData(lineNumber);
        }

        Task task = switch (fields[TYPE_FIELD_INDEX]) {
            case TODO_TYPE -> parseTodo(fields, lineNumber);
            case DEADLINE_TYPE -> parseDeadline(fields, lineNumber);
            case EVENT_TYPE -> parseEvent(fields, lineNumber);
            default -> throw invalidData(lineNumber);
        };
        restoreStatus(task, fields[STATUS_FIELD_INDEX], lineNumber);
        return task;
    }

    /**
     * Parses a stored todo after the common task fields have been validated.
     *
     * @param fields Stored task fields.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed todo.
     * @throws OllieException If the todo has an invalid number of fields.
     */
    private Todo parseTodo(String[] fields, int lineNumber) throws OllieException {
        if (fields.length != TODO_FIELD_COUNT) {
            throw invalidData(lineNumber);
        }
        return new Todo(fields[DESCRIPTION_FIELD_INDEX]);
    }

    /**
     * Parses a stored deadline after the common task fields have been validated.
     *
     * @param fields Stored task fields.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed deadline.
     * @throws OllieException If deadline-specific fields are invalid.
     */
    private Deadline parseDeadline(String[] fields, int lineNumber) throws OllieException {
        if (fields.length != DEADLINE_FIELD_COUNT || fields[DATE_FIELD_INDEX].isBlank()) {
            throw invalidData(lineNumber);
        }
        return new Deadline(fields[DESCRIPTION_FIELD_INDEX],
                parseDate(fields[DATE_FIELD_INDEX], lineNumber));
    }

    /**
     * Parses a stored event after the common task fields have been validated.
     *
     * @param fields Stored task fields.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed event.
     * @throws OllieException If event-specific fields are invalid.
     */
    private Event parseEvent(String[] fields, int lineNumber) throws OllieException {
        if (fields.length != EVENT_FIELD_COUNT) {
            throw invalidData(lineNumber);
        }
        if (fields[DATE_FIELD_INDEX].isBlank() || fields[END_DATE_FIELD_INDEX].isBlank()) {
            throw invalidData(lineNumber);
        }
        LocalDate startDate = parseDate(fields[DATE_FIELD_INDEX], lineNumber);
        LocalDate endDate = parseDate(fields[END_DATE_FIELD_INDEX], lineNumber);
        if (!startDate.isBefore(endDate)) {
            throw invalidData(lineNumber);
        }
        return new Event(fields[DESCRIPTION_FIELD_INDEX], startDate, endDate);
    }

    /**
     * Restores a task's completion state from its stored status field.
     *
     * @param task Task whose state is restored.
     * @param status Stored completion status.
     * @param lineNumber One-based line number used in error messages.
     * @throws OllieException If the status is not recognized.
     */
    private void restoreStatus(Task task, String status, int lineNumber) throws OllieException {
        if (status.equals(DONE_STATUS)) {
            task.mark();
        } else if (!status.equals(NOT_DONE_STATUS)) {
            throw invalidData(lineNumber);
        }
    }

    /**
     * Parses a stored ISO date.
     *
     * @param dateText Date text to parse.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed date.
     * @throws OllieException If the date is invalid.
     */
    private LocalDate parseDate(String dateText, int lineNumber) throws OllieException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw invalidData(lineNumber);
        }
    }

    /**
     * Creates an exception describing invalid saved data.
     *
     * @param lineNumber One-based line number containing invalid data.
     * @return Exception containing a user-friendly error message.
     */
    private OllieException invalidData(int lineNumber) {
        return new OllieException("The saved task on line " + lineNumber + " is invalid.");
    }

    private void ensureTaskIsUnique(List<Task> tasks, Task task, int taskIndex) throws OllieException {
        for (int i = 0; i < taskIndex; i++) {
            if (task.hasSameDetails(tasks.get(i))) {
                throw new OllieException("I couldn't save the same task more than once.");
            }
        }
    }

    private void validateStoredDescription(String description) throws OllieException {
        if (description == null || description.isBlank()
                || description.contains("|")
                || description.contains("\n")
                || description.contains("\r")) {
            throw new OllieException("I couldn't save a task with an invalid description.");
        }
    }
}
