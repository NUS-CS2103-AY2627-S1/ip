package serina.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import serina.exception.SerinaError;
import serina.exception.SerinaException;
import serina.parser.DateParser;
import serina.task.Deadline;
import serina.task.Event;
import serina.task.Task;
import serina.task.TaskStatus;
import serina.task.TaskType;
import serina.task.Todo;

/**
 * Handles loading and saving Serina's tasks on the hard disk.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "serina.txt");
    private static final int MAX_TASKS = 100;
    private static final int MIN_TASK_FIELD_COUNT = 3;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final int TASK_TYPE_FIELD_INDEX = 0;
    private static final int TASK_STATUS_FIELD_INDEX = 1;
    private static final int TASK_DESCRIPTION_FIELD_INDEX = 2;
    private static final int DEADLINE_DATE_FIELD_INDEX = 3;
    private static final int EVENT_START_DATE_FIELD_INDEX = 3;
    private static final int EVENT_END_DATE_FIELD_INDEX = 4;
    private static final char FILE_ESCAPE_MARKER = '\\';
    private static final char FILE_FIELD_DELIMITER = '|';

    private final Path filePath;

    /**
     * Creates storage that uses Serina's default save-file location.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage that reads and writes the specified save file.
     *
     * @param filePath Location of the save file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from Serina's save file.
     *
     * @return Saved tasks, or an empty list if there is no save file yet.
     * @throws SerinaException If Serina is unable to read the save file.
     */
    public List<Task> loadTasks() throws SerinaException {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }
            if (!Files.isRegularFile(filePath)) {
                throw new SerinaException(SerinaError.LOAD_FAILED, "The save location is not a regular file.");
            }

            List<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(filePath);
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (tasks.size() >= MAX_TASKS) {
                    throw new SerinaException(SerinaError.LOAD_TOO_MANY_TASKS);
                }
                Task task;
                try {
                    task = parseTask(line);
                    if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameIdentity(task))) {
                        throw new SerinaException(SerinaError.LOAD_FAILED, "A duplicate task was found.");
                    }
                } catch (SerinaException e) {
                    throw new SerinaException(SerinaError.LOAD_FAILED,
                            "Invalid record on line " + (lineIndex + 1) + ".");
                }
                assert task != null : "Non-empty save-file lines should parse into tasks or throw.";

                tasks.add(task);
                assert tasks.size() <= MAX_TASKS : "Loaded task count should stay within the supported maximum.";
            }
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new SerinaException(SerinaError.LOAD_FAILED, e);
        }
    }

    /**
     * Saves all current tasks to Serina's save file.
     *
     * @param tasks Tasks to write to disk.
     * @throws SerinaException If Serina is unable to create or write the save file.
     */
    public void saveTasks(List<Task> tasks) throws SerinaException {
        assert tasks != null : "Storage should save task lists supplied by Serina.";

        Path temporaryFile = null;
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
            temporaryFile = Files.createTempFile(temporaryDirectory, "serina-", ".tmp");
            Files.write(temporaryFile, toFileLines(tasks));
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException e) {
            throw new SerinaException(SerinaError.SAVE_FAILED, e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // The original save error remains the useful failure to report.
                }
            }
        }
    }

    /**
     * Converts tasks into the lines used by Serina's save-file format.
     *
     * @param tasks Tasks to serialize.
     * @return One serialized line for each task.
     */
    private static List<String> toFileLines(List<Task> tasks) {
        assert tasks.stream().noneMatch(task -> task == null) : "Only real tasks should be serialized.";

        List<String> lines = tasks.stream()
                .map(Task::toFileString)
                .collect(Collectors.toCollection(ArrayList::new));
        assert lines.size() == tasks.size() : "Each task should become exactly one save-file line.";
        return lines;
    }

    /**
     * Reconstructs a task from one line of the save file.
     *
     * @param line Serialized task data.
     * @return The reconstructed task.
     * @throws SerinaException If the line does not follow the expected format.
     */
    private static Task parseTask(String line) throws SerinaException {
        assert line != null : "Save-file parsing should receive lines read from disk.";

        List<String> parts = splitFileLine(line);
        if (parts.size() < MIN_TASK_FIELD_COUNT) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }
        assert parts.size() >= MIN_TASK_FIELD_COUNT
                : "Validated save lines should have type, status, and description fields.";

        TaskType type = TaskType.parseFileValue(parts.get(TASK_TYPE_FIELD_INDEX));
        TaskStatus status = TaskStatus.parseFileValue(parts.get(TASK_STATUS_FIELD_INDEX));
        assert type != null : "Task type parsing should return a type or throw a SerinaException.";
        assert status != null : "Task status parsing should return a status or throw a SerinaException.";

        String description = parts.get(TASK_DESCRIPTION_FIELD_INDEX);
        if (description.isBlank()) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }
        assert !description.isBlank() : "Loaded task descriptions should be validated before task construction.";

        switch (type) {
            case TODO:
                validateFieldCount(parts, TODO_FIELD_COUNT);
                return new Todo(description, status);
            case DEADLINE:
                validateFieldCount(parts, DEADLINE_FIELD_COUNT);
                validateNonEmptyField(parts, DEADLINE_DATE_FIELD_INDEX);
                return new Deadline(description, DateParser.parseFileDate(parts.get(DEADLINE_DATE_FIELD_INDEX)),
                        status);
            case EVENT:
                validateFieldCount(parts, EVENT_FIELD_COUNT);
                validateNonEmptyField(parts, EVENT_START_DATE_FIELD_INDEX);
                validateNonEmptyField(parts, EVENT_END_DATE_FIELD_INDEX);
                return parseEvent(description, parts.get(EVENT_START_DATE_FIELD_INDEX),
                        parts.get(EVENT_END_DATE_FIELD_INDEX), status);
            default:
                throw new SerinaException(SerinaError.LOAD_FAILED);
        }
    }

    /**
     * Checks that a save-file record has the expected number of fields.
     *
     * @param parts Decoded save-file fields.
     * @param expectedFieldCount Number of fields expected for the task type.
     * @throws SerinaException If the record has a different number of fields.
     */
    private static void validateFieldCount(List<String> parts, int expectedFieldCount) throws SerinaException {
        if (parts.size() != expectedFieldCount) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }

        assert parts.size() == expectedFieldCount : "Save-file records should have the expected field count.";
    }

    /**
     * Checks that a required save-file field is present and not empty.
     *
     * @param parts Decoded save-file fields.
     * @param fieldIndex Index of the required field.
     * @throws SerinaException If the required field is empty.
     */
    private static void validateNonEmptyField(List<String> parts, int fieldIndex) throws SerinaException {
        if (parts.get(fieldIndex).isEmpty()) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }

        assert !parts.get(fieldIndex).isBlank() : "Required save-file fields should not be blank.";
    }

    /**
     * Reconstructs an event and validates that its end date is not before its start date.
     *
     * @param description Event description.
     * @param startDateText Event start date in save-file format.
     * @param endDateText Event end date in save-file format.
     * @param status Saved completion status.
     * @return The reconstructed event.
     * @throws SerinaException If either date is invalid or the date range is reversed.
     */
    private static Event parseEvent(String description, String startDateText, String endDateText, TaskStatus status)
            throws SerinaException {
        LocalDate startDate = DateParser.parseFileDate(startDateText);
        LocalDate endDate = DateParser.parseFileDate(endDateText);
        if (!endDate.isAfter(startDate)) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }

        assert endDate.isAfter(startDate) : "Loaded event end dates should be after start dates.";
        return new Event(description, startDate, endDate, status);
    }

    /**
     * Splits a save-file line on unescaped delimiters and restores escaped characters.
     *
     * @param line Serialized task data.
     * @return The decoded fields in the line.
     */
    private static List<String> splitFileLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == FILE_ESCAPE_MARKER
                    && i + 1 < line.length()
                    && isEscapedCharacter(line.charAt(i + 1))) {
                currentPart.append(line.charAt(i + 1));
                i++;
            } else if (current == FILE_FIELD_DELIMITER) {
                parts.add(currentPart.toString().trim());
                currentPart.setLength(0);
            } else {
                currentPart.append(current);
            }
        }

        parts.add(currentPart.toString().trim());
        assert !parts.isEmpty() : "Splitting a save-file line should always produce at least one field.";
        return parts;
    }

    /**
     * Checks whether a character can be escaped in the save-file format.
     *
     * @param character Character following an escape marker.
     * @return {@code true} for a backslash or field delimiter.
     */
    private static boolean isEscapedCharacter(char character) {
        return character == FILE_ESCAPE_MARKER || character == FILE_FIELD_DELIMITER;
    }
}
