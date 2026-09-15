package chatty.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import chatty.exception.ChattyException;
import chatty.task.Deadline;
import chatty.task.Event;
import chatty.task.RecurrenceUnit;
import chatty.task.RecurringTask;
import chatty.task.Task;
import chatty.task.Todo;

/**
 * Loads and saves Chatty's tasks using a text file on the local file system.
 */
public class Storage {
    /** Separator placed between fields in each saved task record. */
    private static final String FIELD_SEPARATOR = " | ";

    /** Stored status representing a completed task. */
    private static final String DONE_STATUS = "1";

    /** Stored status representing an incomplete task. */
    private static final String NOT_DONE_STATUS = "0";

    /** Relative path of the file used to store tasks. */
    private final Path filePath;

    /**
     * Creates storage that writes tasks to the given path.
     *
     * @param filePath relative path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file, or returns an empty list when it does not exist.
     *
     * @return tasks restored from the data file.
     * @throws ChattyException if the data file cannot be read or contains an invalid record.
     */
    public List<Task> loadTasks() throws ChattyException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            List<String> taskRecords = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < taskRecords.size(); i++) {
                String record = taskRecords.get(i);
                if (!record.isBlank()) {
                    tasks.add(parseTask(record, i + 1));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new ChattyException("OOPS!!! I couldn't read your tasks from the data file.");
        }
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks tasks to save.
     * @throws ChattyException if the directory or data file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws ChattyException {
        Path temporaryFile = null;
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> taskRecords = new ArrayList<>();
            for (Task task : tasks) {
                taskRecords.add(formatTask(task));
            }

            Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
            temporaryFile = Files.createTempFile(temporaryDirectory, "chatty-", ".tmp");
            Files.write(temporaryFile, taskRecords, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
            temporaryFile = null;
        } catch (IOException exception) {
            deleteTemporaryFile(temporaryFile);
            throw new ChattyException("OOPS!!! I couldn't save your tasks to the data file.");
        }
    }

    /**
     * Replaces the data file with a completely written temporary file.
     *
     * @param temporaryFile temporary file containing every task record.
     * @throws IOException if the replacement fails.
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes a temporary save file after a failed write, if one was created.
     *
     * @param temporaryFile temporary file to remove, or {@code null}.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException exception) {
            // The original data file remains untouched even if temporary cleanup fails.
        }
    }

    /**
     * Restores one task from a delimited storage record.
     *
     * @param record delimited task record.
     * @param lineNumber one-based line number of the record.
     * @return task restored from the record.
     * @throws ChattyException if the record is malformed.
     */
    private Task parseTask(String record, int lineNumber) throws ChattyException {
        String[] fields = record.split("\\s*\\|\\s*", -1);
        if (fields.length < 3 || fields[2].isBlank()) {
            throw invalidRecord(lineNumber);
        }

        boolean isDone = parseCompletionStatus(fields[1], lineNumber);
        Task task = parseTaskFields(fields, isDone, lineNumber);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Restores the completion status from a stored status field.
     *
     * @param status stored completion status.
     * @param lineNumber one-based line number of the record.
     * @return whether the stored task is complete.
     * @throws ChattyException if the status is invalid.
     */
    private boolean parseCompletionStatus(String status, int lineNumber) throws ChattyException {
        if (status.equals(DONE_STATUS)) {
            return true;
        } else if (status.equals(NOT_DONE_STATUS)) {
            return false;
        }
        throw invalidRecord(lineNumber);
    }

    /**
     * Restores a task using the fields required by its stored type.
     *
     * @param fields task storage fields with a validated description and status.
     * @param isDone stored completion status.
     * @param lineNumber one-based line number of the record.
     * @return task before its completion status is applied.
     * @throws ChattyException if the task type or its fields are invalid.
     */
    private Task parseTaskFields(String[] fields, boolean isDone, int lineNumber) throws ChattyException {
        switch (fields[0]) {
            case "T":
                requireFields(fields, 3, lineNumber);
                return new Todo(fields[2]);
            case "D":
                return parseDeadline(fields, lineNumber);
            case "E":
                return parseEvent(fields, lineNumber);
            case "R":
                requireFields(fields, 6, lineNumber);
                return parseRecurringTask(fields, isDone, lineNumber);
            default:
                throw invalidRecord(lineNumber);
        }
    }

    /**
     * Restores a deadline from its storage fields.
     *
     * @param fields deadline storage fields.
     * @param lineNumber one-based line number of the record.
     * @return deadline restored from the record.
     * @throws ChattyException if the field count or due date is invalid.
     */
    private Task parseDeadline(String[] fields, int lineNumber) throws ChattyException {
        requireFields(fields, 4, lineNumber);
        if (fields[3].isBlank()) {
            throw invalidRecord(lineNumber);
        }
        try {
            return new Deadline(fields[2], LocalDate.parse(fields[3]));
        } catch (DateTimeParseException exception) {
            throw invalidRecord(lineNumber);
        }
    }

    /**
     * Restores an event from its storage fields.
     *
     * @param fields event storage fields.
     * @param lineNumber one-based line number of the record.
     * @return event restored from the record.
     * @throws ChattyException if the field count or either time field is invalid.
     */
    private Task parseEvent(String[] fields, int lineNumber) throws ChattyException {
        requireFields(fields, 5, lineNumber);
        if (fields[3].isBlank() || fields[4].isBlank()) {
            throw invalidRecord(lineNumber);
        }
        return new Event(fields[2], fields[3], fields[4]);
    }

    /**
     * Restores a recurring task from its validated storage fields.
     *
     * @param fields recurring-task storage fields.
     * @param isDone stored completion status.
     * @param lineNumber one-based line number of the record.
     * @return recurring task restored from the record.
     * @throws ChattyException if the recurring-task fields are invalid.
     */
    private Task parseRecurringTask(String[] fields, boolean isDone, int lineNumber)
            throws ChattyException {
        if (isDone || fields[3].isBlank() || fields[4].isBlank() || fields[5].isBlank()) {
            throw invalidRecord(lineNumber);
        }

        LocalDate nextOccurrenceDate;
        try {
            nextOccurrenceDate = LocalDate.parse(fields[3]);
        } catch (DateTimeParseException exception) {
            throw invalidRecord(lineNumber);
        }

        int interval;
        try {
            interval = Integer.parseInt(fields[4]);
        } catch (NumberFormatException exception) {
            throw invalidRecord(lineNumber);
        }
        if (interval <= 0) {
            throw invalidRecord(lineNumber);
        }

        RecurrenceUnit recurrenceUnit;
        try {
            recurrenceUnit = RecurrenceUnit.valueOf(fields[5]);
        } catch (IllegalArgumentException exception) {
            throw invalidRecord(lineNumber);
        }
        return new RecurringTask(fields[2], nextOccurrenceDate, interval, recurrenceUnit);
    }

    /**
     * Ensures that a stored task record has the expected number of fields.
     *
     * @param fields fields parsed from the record.
     * @param expectedCount required number of fields.
     * @param lineNumber one-based line number of the record.
     * @throws ChattyException if the field count is incorrect.
     */
    private void requireFields(String[] fields, int expectedCount, int lineNumber) throws ChattyException {
        if (fields.length != expectedCount) {
            throw invalidRecord(lineNumber);
        }
    }

    /**
     * Returns an error describing the location of a malformed stored task.
     *
     * @param lineNumber one-based line number of the malformed record.
     * @return exception describing the malformed record.
     */
    private ChattyException invalidRecord(int lineNumber) {
        return new ChattyException("OOPS!!! The data file is corrupted at line " + lineNumber + ".");
    }

    /**
     * Returns one task encoded as a delimited storage record.
     *
     * @param task task to encode.
     * @return delimited record representing the task.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;
        String record = task.getTypeIcon() + FIELD_SEPARATOR
                + status + FIELD_SEPARATOR + task.getDescription();
        if (task instanceof Deadline deadline) {
            return record + FIELD_SEPARATOR + deadline.getDueDate();
        } else if (task instanceof Event event) {
            return record + FIELD_SEPARATOR + event.getFrom()
                    + FIELD_SEPARATOR + event.getTo();
        } else if (task instanceof RecurringTask recurringTask) {
            return record + FIELD_SEPARATOR + recurringTask.getNextOccurrenceDate()
                    + FIELD_SEPARATOR + recurringTask.getInterval()
                    + FIELD_SEPARATOR + recurringTask.getRecurrenceUnit();
        }
        assert task instanceof Todo : "Only todo tasks can use the base storage format";
        return record;
    }
}
