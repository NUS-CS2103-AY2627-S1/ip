package downtowngurl.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import downtowngurl.exception.DowntownGurlException;
import downtowngurl.task.Deadline;
import downtowngurl.task.Event;
import downtowngurl.task.RecurrenceFrequency;
import downtowngurl.task.Task;
import downtowngurl.task.TaskDateTime;
import downtowngurl.task.Todo;

/**
 * Handles loading tasks from disk and saving tasks back to disk.
 */
public class Storage {
    private static final String STORAGE_SEPARATOR = " * ";
    private static final String STORAGE_DONE_STATUS = "Done";
    private static final String STORAGE_NOT_DONE_STATUS = "Not done";
    private static final String LOAD_ERROR_MESSAGE = "Oops, I couldn't load your tasks from disk.";
    private static final String SAVE_ERROR_MESSAGE = "Oops, I couldn't save your tasks to disk.";

    private final Path taskFilePath;
    private final ArrayList<Integer> corruptedLineNumbers;

    /**
     * Creates a storage helper for the given task file.
     *
     * @param taskFilePath Path to the file used to persist tasks.
     */
    public Storage(Path taskFilePath) {
        this.taskFilePath = taskFilePath;
        this.corruptedLineNumbers = new ArrayList<>();
    }

    /**
     * Loads tasks from the data file if it already exists.
     *
     * @return Task list from the data file, or an empty list if the file does not exist.
     * @throws DowntownGurlException If the data file cannot be read.
     */
    public ArrayList<Task> loadTasks() throws DowntownGurlException {
        ArrayList<Task> tasks = new ArrayList<>();
        this.corruptedLineNumbers.clear();
        if (!Files.exists(this.taskFilePath)) {
            return tasks;
        }

        try {
            ArrayList<String> taskLines = new ArrayList<>(Files.readAllLines(this.taskFilePath));
            for (int i = 0; i < taskLines.size(); i++) {
                String taskLine = taskLines.get(i);
                if (taskLine.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(createTaskFromStorageLine(taskLine));
                } catch (DowntownGurlException e) {
                    this.corruptedLineNumbers.add(i + 1);
                    // Keep loading valid tasks even if one saved line is no longer readable.
                }
            }
        } catch (IOException e) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }
        return tasks;
    }

    /**
     * Returns line numbers skipped during the most recent load because they could not be read as tasks.
     *
     * @return One-based corrupted line numbers.
     */
    public ArrayList<Integer> getCorruptedLineNumbers() {
        return new ArrayList<>(this.corruptedLineNumbers);
    }

    /**
     * Saves all current tasks to the data file.
     *
     * @param tasks Current task list.
     * @throws DowntownGurlException If the data file cannot be written.
     */
    public void saveTasks(Iterable<Task> tasks) throws DowntownGurlException {
        ArrayList<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(task.toStorageString());
        }

        Path tempFilePath = null;
        try {
            Path storageDirectory = this.taskFilePath.getParent();
            if (storageDirectory != null) {
                Files.createDirectories(storageDirectory);
            } else {
                storageDirectory = Path.of(".");
            }
            tempFilePath = Files.createTempFile(storageDirectory, "downtownGurl", ".tmp");
            Files.write(tempFilePath, taskLines);
            Files.move(tempFilePath, this.taskFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException e) {
            throw new DowntownGurlException(SAVE_ERROR_MESSAGE);
        } finally {
            deleteTempFile(tempFilePath);
        }
    }

    /**
     * Creates a task from one line in the data file.
     *
     * @param taskLine One saved task line.
     * @return Task represented by the saved line.
     * @throws DowntownGurlException If the saved line is not in the expected format.
     */
    private Task createTaskFromStorageLine(String taskLine) throws DowntownGurlException {
        String[] parts = taskLine.split("\\Q" + STORAGE_SEPARATOR + "\\E", -1);
        if (parts.length < 3 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }

        Task task = switch (parts[0]) {
            case "T" -> createTodoFromStorageParts(parts);
            case "D" -> createDeadlineFromStorageParts(parts);
            case "E" -> createEventFromStorageParts(parts);
            default -> throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        };
        assert task != null : "Recognized storage type should create a task.";

        if (parts[1].equals(STORAGE_DONE_STATUS)) {
            task.markAsDone();
            assert task.isDone() : "Done storage status should mark the task as done.";
        } else if (!parts[1].equals(STORAGE_NOT_DONE_STATUS)) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        } else {
            assert !task.isDone() : "Not done storage status should leave the task unmarked.";
        }
        return task;
    }

    /**
     * Creates a todo from saved fields.
     *
     * @param parts Saved task fields.
     * @return Todo represented by the saved fields.
     * @throws DowntownGurlException If the fields are not in the expected format.
     */
    private Todo createTodoFromStorageParts(String[] parts) throws DowntownGurlException {
        if (parts.length != 3 || parts[2].isBlank()) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }
        return new Todo(unescapeStorageField(parts[2]));
    }

    /**
     * Creates a deadline from saved fields.
     *
     * @param parts Saved task fields.
     * @return Deadline represented by the saved fields.
     * @throws DowntownGurlException If the fields are not in the expected format.
     */
    private Deadline createDeadlineFromStorageParts(String[] parts) throws DowntownGurlException {
        if (parts.length == 4 || parts.length == 5) {
            String description = unescapeStorageField(parts[2]);
            String by = unescapeStorageField(parts[3]);
            if (description.isBlank() || by.isBlank()) {
                throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
            }
            assert !description.isBlank() && !by.isBlank() : "Modern deadline fields should be present.";
            Deadline deadline = new Deadline(description, TaskDateTime.parseFromStorage(by));
            setRecurrenceFromStoragePart(deadline, parts, 4);
            return deadline;
        }
        if (parts.length != 3 || parts[2].isBlank()) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }
        return createDeadlineFromOldStorageDetails(parts[2]);
    }

    /**
     * Creates a deadline from old saved details in this form: DESCRIPTION, BY.
     *
     * @param details Old saved deadline details.
     * @return Deadline represented by the old saved details.
     * @throws DowntownGurlException If the old details are not in the expected format.
     */
    private Deadline createDeadlineFromOldStorageDetails(String details) throws DowntownGurlException {
        int separatorIndex = details.lastIndexOf(", ");
        if (separatorIndex == -1 || details.substring(0, separatorIndex).isBlank()
                || details.substring(separatorIndex + 2).isBlank()) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }
        return new Deadline(details.substring(0, separatorIndex),
                TaskDateTime.parseFromStorage(details.substring(separatorIndex + 2)));
    }

    /**
     * Creates an event from saved fields.
     *
     * @param parts Saved task fields.
     * @return Event represented by the saved fields.
     * @throws DowntownGurlException If the fields are not in the expected format.
     */
    private Event createEventFromStorageParts(String[] parts) throws DowntownGurlException {
        if (parts.length == 5 || parts.length == 6) {
            String description = unescapeStorageField(parts[2]);
            String from = unescapeStorageField(parts[3]);
            String to = unescapeStorageField(parts[4]);
            if (description.isBlank() || from.isBlank() || to.isBlank()) {
                throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
            }
            assert !description.isBlank() && !from.isBlank() && !to.isBlank()
                    : "Modern event fields should be present.";
            Event event = new Event(description, TaskDateTime.parseFromStorage(from),
                    TaskDateTime.parseFromStorage(to));
            setRecurrenceFromStoragePart(event, parts, 5);
            return event;
        }
        if (parts.length != 3 || parts[2].isBlank()) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }
        return createEventFromOldStorageDetails(parts[2]);
    }

    /**
     * Creates an event from old saved details in this form: DESCRIPTION, FROM-TO.
     *
     * @param details Old saved event details.
     * @return Event represented by the old saved details.
     * @throws DowntownGurlException If the old details are not in the expected format.
     */
    private Event createEventFromOldStorageDetails(String details) throws DowntownGurlException {
        int detailsSeparatorIndex = details.lastIndexOf(", ");
        int timeSeparatorIndex = details.lastIndexOf("-");
        if (detailsSeparatorIndex == -1 || timeSeparatorIndex == -1
                || timeSeparatorIndex <= detailsSeparatorIndex + 2) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }
        String description = details.substring(0, detailsSeparatorIndex);
        String from = details.substring(detailsSeparatorIndex + 2, timeSeparatorIndex);
        String to = details.substring(timeSeparatorIndex + 1);
        if (description.isBlank() || from.isBlank() || to.isBlank()) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }
        return new Event(description, TaskDateTime.parseFromStorage(from), TaskDateTime.parseFromStorage(to));
    }

    /**
     * Sets recurrence from an optional storage field.
     *
     * @param task Task to update.
     * @param parts Saved task fields.
     * @param recurrenceIndex Index of the optional recurrence field.
     * @throws DowntownGurlException If the recurrence field is invalid.
     */
    private void setRecurrenceFromStoragePart(Task task, String[] parts, int recurrenceIndex)
            throws DowntownGurlException {
        if (parts.length == recurrenceIndex) {
            return;
        }
        task.setRecurrenceFrequency(RecurrenceFrequency.parseFromStorage(parts[recurrenceIndex]));
    }

    /**
     * Restores special characters from a saved field.
     *
     * @param field Saved field.
     * @return Unescaped field.
     * @throws DowntownGurlException If the saved field has an invalid escape sequence.
     */
    private String unescapeStorageField(String field) throws DowntownGurlException {
        StringBuilder unescapedField = new StringBuilder();
        boolean isEscaping = false;
        for (int i = 0; i < field.length(); i++) {
            char character = field.charAt(i);
            if (isEscaping) {
                switch (character) {
                    case '\\' -> unescapedField.append('\\');
                    case 'r' -> unescapedField.append('\r');
                    case 'n' -> unescapedField.append('\n');
                    case '*' -> unescapedField.append('*');
                    default -> throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
                }
                isEscaping = false;
            } else if (character == '\\') {
                isEscaping = true;
            } else {
                unescapedField.append(character);
            }
        }
        if (isEscaping) {
            throw new DowntownGurlException(LOAD_ERROR_MESSAGE);
        }
        return unescapedField.toString();
    }

    /**
     * Deletes a temporary save file if one was left behind.
     *
     * @param tempFilePath Temporary file to delete.
     */
    private void deleteTempFile(Path tempFilePath) {
        if (tempFilePath == null) {
            return;
        }
        try {
            Files.deleteIfExists(tempFilePath);
        } catch (IOException e) {
            // The main save result has already been reported, so this cleanup error can be ignored.
        }
    }
}
