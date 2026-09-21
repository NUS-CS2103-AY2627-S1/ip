package answerme.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import answerme.exception.AnswerMeException;
import answerme.parser.DateTimeParser;
import answerme.task.Deadline;
import answerme.task.Event;
import answerme.task.Task;
import answerme.task.TaskList;
import answerme.task.ToDo;

/**
 * Represents a Storage Manager for persisting tasks in a text file
 * on disk.
 */
public class Storage {
    private static final String DEFAULT_DATA_FILE_PATH = "data/tasks.txt";
    private static final int PRESERVE_EMPTY_FIELDS = -1;
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int FIRST_DATE_FIELD_INDEX = 3;
    private static final int SECOND_DATE_FIELD_INDEX = 4;

    private static final int MINIMUM_FIELD_COUNT = 3;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private enum TaskType {
        TODO("ToDo", TODO_FIELD_COUNT),
        DEADLINE("Deadline", DEADLINE_FIELD_COUNT),
        EVENT("Event", EVENT_FIELD_COUNT);

        private final String displayName;
        private final int expectedFieldCount;

        TaskType(String displayName, int expectedFieldCount) {
            this.displayName = displayName;
            this.expectedFieldCount = expectedFieldCount;
        }
    }

    private enum TaskStatus {
        COMPLETE,
        INCOMPLETE
    }

    private final File dataFile;
    private boolean isSavingEnabled;

    /**
     * Constructs a new Storage Manager.
     */
    public Storage() {
        this(new File(DEFAULT_DATA_FILE_PATH));
    }

    /**
     * Constructs a storage manager that uses the specified data file.
     *
     * @param dataFile The file used to load and save tasks.
     */
    public Storage(File dataFile) {
        this.dataFile = dataFile;
        this.isSavingEnabled = true;
    }

    /**
     * Saves tasks in the configured data file.
     *
     * @param taskList The list of tasks to be saved in the output file.
     * @throws AnswerMeException If the data file cannot be created or written.
     */
    public void saveTasks(TaskList taskList) throws AnswerMeException {
        if (!isSavingEnabled) {
            throw new AnswerMeException(
                    "Cannot save changes because saved tasks could not be loaded. "
                            + "Please repair the data file and then restart the application.");
        }

        if (!hasDataFile()) {
            createDataFile();
        }

        try (FileWriter writer = new FileWriter(dataFile)) {
            for (Task task : taskList) {
                writer.write(task.toStorageFormat() + "\n");
            }
        } catch (IOException exception) {
            throw new AnswerMeException("Unable to save tasks.");
        }
    }

    /**
     * Disables saving when loading the existing data file fails, preventing
     * later commands from overwriting it.
     */
    public void disableSaving() {
        isSavingEnabled = false;
    }

    /**
     * Loads tasks from the data file into a task list.
     *
     * @return The successfully loaded tasks and any warnings from malformed tasks,
     *         or an empty result if the file does not exist.
     * @throws AnswerMeException If the data file cannot be read or located.
     */
    public TaskLoadResult loadTasks() throws AnswerMeException {
        if (!hasDataFile()) {
            return new TaskLoadResult(List.of(), List.of());
        }

        try (Scanner scanner = new Scanner(dataFile)) {
            return readTasks(scanner);
        } catch (FileNotFoundException exception) {
            throw new AnswerMeException("Unable to open " + dataFile + " for loading.");
        }
    }

    private TaskLoadResult readTasks(Scanner scanner) throws AnswerMeException {
        TaskList taskList = new TaskList();
        List<String> loadingWarnings = new ArrayList<>();

        for (int lineNumber = 1; scanner.hasNextLine(); lineNumber++) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }
            try {
                taskList.add(parseTask(line, lineNumber));
            } catch (AnswerMeException exception) {
                loadingWarnings.add(exception.getMessage());
            }
        }

        if (scanner.ioException() != null) {
            throw new AnswerMeException("Unable to read saved tasks from "
                    + dataFile + ".");
        }
        return new TaskLoadResult(taskList, loadingWarnings);
    }

    /**
     * Checks whether the configured data file exists.
     *
     * @return {@code true} if the file exists and {@code false} if not.
     */
    private boolean hasDataFile() {
        return dataFile.exists();
    }

    /**
     * Creates the task data file and any missing parent directory if they
     * do not exist.
     *
     * @throws AnswerMeException If the data file cannot be created.
     */
    private void createDataFile() throws AnswerMeException {
        File parentDirectory = dataFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new AnswerMeException("Unable to create data directory.");
        }
        try {
            dataFile.createNewFile();
        } catch (IOException exception) {
            throw new AnswerMeException("File: " + dataFile + " cannot be created!");
        }
    }

    /**
     * Converts individual lines into corresponding {@code Task} objects.
     *
     * @param line The serialized task data to parse.
     * @param lineNumber The current line number being parsed, used in error messages.
     * @return The task represented by the given line.
     * @throws AnswerMeException If the task has an invalid format, status or type.
     */
    private Task parseTask(String line, int lineNumber) throws AnswerMeException {
        String[] fields = parseFields(line, lineNumber);
        TaskType taskType = parseTaskType(fields[TYPE_FIELD_INDEX], lineNumber);
        TaskStatus taskStatus = parseTaskStatus(fields[STATUS_FIELD_INDEX], lineNumber);
        Task task = createTask(taskType, fields, lineNumber);

        if (taskStatus == TaskStatus.COMPLETE) {
            task.setCompletionStatus(true);
        }
        return task;
    }

    private String[] parseFields(String line, int lineNumber) throws AnswerMeException {
        String[] fields = line.split("\\s*\\|\\s*", PRESERVE_EMPTY_FIELDS);
        if (fields.length < MINIMUM_FIELD_COUNT) {
            throw new AnswerMeException("Invalid task found on line " + lineNumber);
        }
        return fields;
    }

    private TaskType parseTaskType(String type, int lineNumber) throws AnswerMeException {
        try {
            return TaskType.valueOf(type.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new AnswerMeException("Unknown task type on line " + lineNumber);
        }
    }

    private TaskStatus parseTaskStatus(String value, int lineNumber)
            throws AnswerMeException {
        try {
            return TaskStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new AnswerMeException("Invalid task status on line "
                    + lineNumber + ".");
        }
    }

    private LocalDateTime parseStoredDateTime(String value, int lineNumber, TaskType taskType)
            throws AnswerMeException {
        try {
            return DateTimeParser.parseDateTime(value);
        } catch (AnswerMeException exception) {
            throw new AnswerMeException("Invalid " + taskType.displayName + " datetime on line "
                    + lineNumber);
        }
    }

    private void checkFieldCount(TaskType taskType, String[] fields, int lineNumber)
            throws AnswerMeException {
        if (fields.length != taskType.expectedFieldCount) {
            throw new AnswerMeException("Invalid " + taskType.displayName
                    + " on line " + lineNumber);
        }
    }

    private Task createTask(TaskType taskType, String[] fields, int lineNumber)
            throws AnswerMeException {
        checkFieldCount(taskType, fields, lineNumber);
        String taskDescription = fields[DESCRIPTION_FIELD_INDEX];

        if (taskDescription.isBlank()) {
            throw new AnswerMeException(
                    "Task description cannot be empty on line " + lineNumber + ".");
        }

        switch (taskType) {
            case TODO:
                return new ToDo(taskDescription);

            case DEADLINE:
                return new Deadline(taskDescription,
                        parseStoredDateTime(fields[FIRST_DATE_FIELD_INDEX], lineNumber, taskType));

            case EVENT:
                LocalDateTime eventStart = parseStoredDateTime(fields[FIRST_DATE_FIELD_INDEX],
                                                lineNumber, taskType);
                LocalDateTime eventEnd = parseStoredDateTime(fields[SECOND_DATE_FIELD_INDEX],
                                                lineNumber, taskType);

                if (eventStart.isAfter(eventEnd)) {
                    throw new AnswerMeException("Event start cannot occur after its end on line "
                            + lineNumber + ".");
                }

                return new Event(taskDescription, eventStart, eventEnd);

            default:
                throw new AnswerMeException("Unknown Task type " + taskType
                        + " on line " + lineNumber);
        }
    }
}
