package chausistant.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import chausistant.task.DeadlineTask;
import chausistant.task.EventTask;
import chausistant.task.Task;
import chausistant.task.TaskList;
import chausistant.task.TodoTask;

/**
 * Handles loading tasks from and saving tasks to the application's data file.
 */
public class Storage {
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int TIME_FIELD_INDEX = 3;
    private static final int EVENT_END_TIME_FIELD_INDEX = 4;
    private static final int MINIMUM_TASK_FIELD_COUNT = 2;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final LocalTime START_OF_DAY = LocalTime.MIDNIGHT;
    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu HHmm")
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu")
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern DATE_TIME_SHAPE = Pattern.compile(
            "^\\d{1,2}/\\d{1,2}/\\d{4} \\d{4}$");
    private static final Pattern DATE_SHAPE = Pattern.compile(
            "^\\d{1,2}/\\d{1,2}/\\d{4}$");

    private final Path saveFile;

    /** Creates storage that reads from and writes to the given relative file path. */
    public Storage(Path saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * Loads saved tasks and any non-fatal warnings encountered along the way.
     *
     * @return the restored task list and warnings for malformed saved lines
     * @throws IOException if an existing save file cannot be read
     */
    public LoadResult load() throws IOException {
        TaskList tasks = new TaskList();
        ArrayList<String> warnings = new ArrayList<>();
        if (Files.notExists(saveFile)) {
            return new LoadResult(tasks, warnings);
        }
        if (!Files.isRegularFile(saveFile)) {
            throw new IOException("The save path is not a regular file.");
        }

        List<String> savedTasks = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
        for (int index = 0; index < savedTasks.size(); index++) {
            String savedTask = savedTasks.get(index);
            if (savedTask.isBlank()) {
                continue;
            }

            try {
                tasks.add(createTaskFromSaveFormat(savedTask));
            } catch (StorageException error) {
                warnings.add("Ignoring malformed task on line " + (index + 1) + ": "
                        + error.getMessage());
            }
        }
        return new LoadResult(tasks, warnings);
    }

    /**
     * Writes all current tasks to the save file.
     *
     * <p>The temporary-file replacement prevents a partially written task
     * list if writing is interrupted.</p>
     *
     * @param tasks the current task list to save
     * @throws IOException if the data directory or save file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        Path dataDirectory = saveFile.getParent();
        if (dataDirectory == null) {
            dataDirectory = Path.of(".");
        }
        Files.createDirectories(dataDirectory);
        List<String> savedTasks = tasks.getTasks().stream().map(Task::toSaveFormat).toList();
        Path temporaryFile = Files.createTempFile(dataDirectory, "chausistant-", ".tmp");
        try {
            Files.write(temporaryFile, savedTasks, StandardCharsets.UTF_8);
            replaceSaveFile(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Recreates a task from one valid line in the save-file format. */
    private static Task createTaskFromSaveFormat(String savedTask) throws StorageException {
        List<String> fields = splitSaveFields(savedTask);
        if (fields.size() < MINIMUM_TASK_FIELD_COUNT) {
            throw new StorageException("the task type or status is missing.");
        }
        if (!fields.get(STATUS_FIELD_INDEX).equals("0") && !fields.get(STATUS_FIELD_INDEX).equals("1")) {
            throw new StorageException("the status must be 0 or 1.");
        }

        Task task = switch (fields.get(TYPE_FIELD_INDEX)) {
            case "T" -> new TodoTask(getSavedField(fields, TODO_FIELD_COUNT, DESCRIPTION_FIELD_INDEX,
                    "todo description"));
            case "D" -> createSavedDeadline(fields);
            case "E" -> createSavedEvent(fields);
            default -> throw new StorageException("unknown task type '" + fields.get(TYPE_FIELD_INDEX) + "'.");
        };
        task.setCompleted("1".equals(fields.get(STATUS_FIELD_INDEX)));
        return task;
    }

    /** Recreates a deadline while preserving whether its saved time was optional. */
    private static DeadlineTask createSavedDeadline(List<String> fields) throws StorageException {
        StoredDateTime deadline = parseSavedDateTime(
                getSavedField(fields, DEADLINE_FIELD_COUNT, TIME_FIELD_INDEX, "deadline"), END_OF_DAY);
        return new DeadlineTask(getSavedField(fields, DEADLINE_FIELD_COUNT, DESCRIPTION_FIELD_INDEX,
                "deadline description"),
                deadline.dateTime, deadline.hasTime);
    }

    /** Recreates an event while preserving whether either saved time was optional. */
    private static EventTask createSavedEvent(List<String> fields) throws StorageException {
        StoredDateTime from = parseSavedDateTime(
                getSavedField(fields, EVENT_FIELD_COUNT, TIME_FIELD_INDEX, "event start time"), START_OF_DAY);
        StoredDateTime to = parseSavedDateTime(
                getSavedField(fields, EVENT_FIELD_COUNT, EVENT_END_TIME_FIELD_INDEX,
                        "event end time"), END_OF_DAY);
        if (!to.dateTime.isAfter(from.dateTime)) {
            throw new StorageException("the event end must be after its start.");
        }
        return new EventTask(getSavedField(fields, EVENT_FIELD_COUNT, DESCRIPTION_FIELD_INDEX,
                "event description"),
                from.dateTime, from.hasTime, to.dateTime, to.hasTime);
    }

    /** Parses a date or date/time from a valid save-file field. */
    private static StoredDateTime parseSavedDateTime(String value, LocalTime defaultTime)
            throws StorageException {
        try {
            if (DATE_SHAPE.matcher(value).matches()) {
                return new StoredDateTime(LocalDate.parse(value, DATE_FORMATTER)
                        .atTime(defaultTime), false);
            }
            if (DATE_TIME_SHAPE.matcher(value).matches()) {
                return new StoredDateTime(LocalDateTime.parse(value, DATE_TIME_FORMATTER), true);
            }
        } catch (DateTimeParseException error) {
            throw new StorageException("the saved date/time is invalid.");
        }
        throw new StorageException("the saved date/time is invalid.");
    }

    /** Returns one required non-empty field from a saved task after validating its field count. */
    private static String getSavedField(List<String> fields, int expectedFieldCount, int fieldIndex,
                                        String fieldName) throws StorageException {
        if (fields.size() != expectedFieldCount) {
            throw new StorageException("the task has an incorrect number of fields.");
        }
        String value = fields.get(fieldIndex);
        if (value.isBlank()) {
            throw new StorageException("the " + fieldName + " is missing.");
        }
        return value;
    }

    /** Splits a save-file line while preserving escaped pipe and backslash characters. */
    private static List<String> splitSaveFields(String savedTask) throws StorageException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        for (int index = 0; index < savedTask.length(); index++) {
            char character = savedTask.charAt(index);
            if (character == '\\') {
                if (index + 1 >= savedTask.length()) {
                    throw new StorageException("the task contains an incomplete escape sequence.");
                }
                char nextCharacter = savedTask.charAt(index + 1);
                if (nextCharacter == '\\' || nextCharacter == '|') {
                    currentField.append(nextCharacter);
                    index++;
                    continue;
                }
                throw new StorageException("the task contains an invalid escape sequence.");
            }

            if (character == '|') {
                fields.add(currentField.toString().strip());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }
        fields.add(currentField.toString().strip());
        return fields;
    }

    /** Replaces the save file without leaving a partially written task list behind. */
    private void replaceSaveFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException error) {
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Holds the successfully loaded task list and any non-fatal loading warnings. */
    public static class LoadResult {
        private final TaskList tasks;
        private final List<String> warnings;

        private LoadResult(TaskList tasks, List<String> warnings) {
            this.tasks = tasks;
            this.warnings = List.copyOf(warnings);
        }

        /** Returns the restored tasks in their saved order. */
        public TaskList getTasks() {
            return tasks;
        }

        /** Returns warnings for malformed lines that were skipped. */
        public List<String> getWarnings() {
            return warnings;
        }
    }

    /** Holds a parsed saved date/time and whether the original field included a time. */
    private static class StoredDateTime {
        private final LocalDateTime dateTime;
        private final boolean hasTime;

        StoredDateTime(LocalDateTime dateTime, boolean hasTime) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
        }
    }

    /** Represents a malformed task entry found in the save file. */
    private static class StorageException extends Exception {
        StorageException(String message) {
            super(message);
        }
    }
}
