package habpyduck.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import habpyduck.HabpyDuckException;
import habpyduck.task.Deadline;
import habpyduck.task.Event;
import habpyduck.task.Task;
import habpyduck.task.Todo;
import habpyduck.ui.Ui;

/**
 * Handles loading tasks from disk and saving tasks to disk.
 */
public class Storage {
    /** Separator used between fields in one saved task line. */
    public static final String FILE_FIELD_SEPARATOR = " | ";
    /** Status code used when saving completed tasks. */
    public static final String DONE_STATUS = "1";
    /** Status code used when saving incomplete tasks. */
    public static final String NOT_DONE_STATUS = "0";
    /** Task type code used when saving deadline tasks. */
    public static final String DEADLINE_TASK_TYPE = "D";
    /** Task type code used when saving event tasks. */
    public static final String EVENT_TASK_TYPE = "E";
    /** Task type code used when saving todo tasks. */
    public static final String TODO_TASK_TYPE = "T";

    private static final int DEADLINE_DATE_TIME_INDEX = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_END_INDEX = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final int EVENT_START_INDEX = 3;
    private static final String FILE_FIELD_SEPARATOR_REGEX = " \\| ";
    private static final int STATUS_INDEX = 1;
    private static final String TAG_SEPARATOR = ",";
    private static final int TASK_DESCRIPTION_INDEX = 2;
    private static final int TASK_TYPE_INDEX = 0;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int TYPE_AND_STATUS_FIELD_COUNT = 2;

    private final Path filePath;

    /**
     * Creates a storage object that reads from and writes to the given file.
     *
     * @param firstPathPart the first part of the relative path to the save file.
     * @param otherPathParts the remaining parts of the relative path to the save file.
     */
    public Storage(String firstPathPart, String... otherPathParts) {
        this.filePath = Path.of(firstPathPart, otherPathParts);
    }

    /**
     * Saves the current tasks to disk, replacing the old file contents.
     *
     * @param tasks the list of tasks to save.
     * @throws HabpyDuckException if the file cannot be written.
     */
    public void saveTasks(ArrayList<Task> tasks) throws HabpyDuckException {
        try {
            Files.createDirectories(filePath.getParent());
            ArrayList<String> lines = tasks.stream()
                    .map(Task::toFileString)
                    .collect(Collectors.toCollection(ArrayList::new));
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new HabpyDuckException("OH NO!!! I could not save your tasks to " + filePath + ".");
        }
    }

    /**
     * Loads saved tasks from disk.
     *
     * @return the tasks stored in the save file, or an empty list if there is no save file yet.
     */
    public ArrayList<Task> loadTasks() {
        return loadTasks(false, null);
    }

    /**
     * Loads saved tasks from disk.
     *
     * @param shouldShowWarnings whether to print warnings for malformed saved tasks.
     * @param ui the UI used to show loading warnings.
     * @return the tasks stored in the save file, or an empty list if there is no save file yet.
     */
    public ArrayList<Task> loadTasks(boolean shouldShowWarnings, Ui ui) {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            ArrayList<String> lines = new ArrayList<>(Files.readAllLines(filePath, StandardCharsets.UTF_8));
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }

                try {
                    tasks.add(parseTaskFromFile(line));
                } catch (HabpyDuckException e) {
                    if (shouldShowWarnings && ui != null) {
                        ui.showError("OH NO!!! I had trouble loading saved task on line "
                                + (i + 1) + ": " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            if (shouldShowWarnings && ui != null) {
                ui.showError("OH NO!!! I could not load tasks from " + filePath + ".");
            }
        }
        return tasks;
    }

    /**
     * Converts one saved text line back into a task object.
     *
     * @param line one line from the save file.
     * @return the task represented by that line.
     * @throws HabpyDuckException if the saved line is not in the expected format.
     */
    private Task parseTaskFromFile(String line) throws HabpyDuckException {
        String[] parts = line.split(FILE_FIELD_SEPARATOR_REGEX, -1);
        validateSavedTaskParts(parts);
        assert parts.length >= TODO_FIELD_COUNT : "Validated saved task should have task details";

        Task task;
        switch (parts[TASK_TYPE_INDEX]) {
            case DEADLINE_TASK_TYPE:
                assert parts.length == DEADLINE_FIELD_COUNT || parts.length == DEADLINE_FIELD_COUNT + 1
                        : "Validated deadline should have expected fields";
                task = new Deadline(unescapeFileField(parts[TASK_DESCRIPTION_INDEX]),
                        parseSavedDeadlineDateTime(unescapeFileField(parts[DEADLINE_DATE_TIME_INDEX])));
                break;
            case EVENT_TASK_TYPE:
                assert parts.length == EVENT_FIELD_COUNT || parts.length == EVENT_FIELD_COUNT + 1
                        : "Validated event should have expected fields";
                task = new Event(unescapeFileField(parts[TASK_DESCRIPTION_INDEX]),
                        parseSavedEventDateTime(unescapeFileField(parts[EVENT_START_INDEX]), "start"),
                        parseSavedEventDateTime(unescapeFileField(parts[EVENT_END_INDEX]), "end"));
                break;
            case TODO_TASK_TYPE:
                assert parts.length == TODO_FIELD_COUNT || parts.length == TODO_FIELD_COUNT + 1
                        : "Validated todo should have expected fields";
                task = new Todo(unescapeFileField(parts[TASK_DESCRIPTION_INDEX]));
                break;
            default:
                throw new HabpyDuckException("unknown task type '" + parts[TASK_TYPE_INDEX] + "'");
        }

        if (parts[STATUS_INDEX].equals(DONE_STATUS)) {
            task.markAsDone();
        }
        loadTags(task, parts);
        return task;
    }

    /**
     * Converts deadline text from the save file into a LocalDateTime.
     *
     * @param dateTimeText the saved date and time text.
     * @return the parsed date and time.
     * @throws HabpyDuckException if the saved value is not an ISO date or date-time.
     */
    private LocalDateTime parseSavedDeadlineDateTime(String dateTimeText) throws HabpyDuckException {
        try {
            return LocalDateTime.parse(dateTimeText);
        } catch (DateTimeParseException dateTimeError) {
            try {
                return LocalDate.parse(dateTimeText).atStartOfDay();
            } catch (DateTimeParseException dateError) {
                throw new HabpyDuckException("saved deadline date and time must use yyyy-MM-ddTHH:mm format");
            }
        }
    }

    /**
     * Converts an event start or end text from the save file into a LocalDateTime.
     *
     * @param dateTimeText the saved date and time text.
     * @param eventBoundary whether the value is an event start or end.
     * @return the parsed date and time.
     * @throws HabpyDuckException if the saved value is not an ISO date-time.
     */
    private LocalDateTime parseSavedEventDateTime(String dateTimeText, String eventBoundary)
            throws HabpyDuckException {
        try {
            return LocalDateTime.parse(dateTimeText);
        } catch (DateTimeParseException e) {
            throw new HabpyDuckException("saved event " + eventBoundary
                    + " date and time must use yyyy-MM-ddTHH:mm format");
        }
    }

    /**
     * Checks that a saved task line has a known type, valid done status, and correct number of fields.
     *
     * @param parts the saved line split into fields.
     * @throws HabpyDuckException if the saved line is malformed.
     */
    private void validateSavedTaskParts(String[] parts) throws HabpyDuckException {
        if (parts.length < TYPE_AND_STATUS_FIELD_COUNT) {
            throw new HabpyDuckException("missing task type or status");
        }
        if (!parts[STATUS_INDEX].equals(NOT_DONE_STATUS) && !parts[STATUS_INDEX].equals(DONE_STATUS)) {
            throw new HabpyDuckException("status must be 0 or 1");
        }

        int expectedPartCount;
        switch (parts[TASK_TYPE_INDEX]) {
            case TODO_TASK_TYPE:
                expectedPartCount = TODO_FIELD_COUNT;
                break;
            case DEADLINE_TASK_TYPE:
                expectedPartCount = DEADLINE_FIELD_COUNT;
                break;
            case EVENT_TASK_TYPE:
                expectedPartCount = EVENT_FIELD_COUNT;
                break;
            default:
                throw new HabpyDuckException("unknown task type '" + parts[TASK_TYPE_INDEX] + "'");
        }
        if (parts.length != expectedPartCount && parts.length != expectedPartCount + 1) {
            throw new HabpyDuckException("expected " + expectedPartCount + " or " + (expectedPartCount + 1)
                    + " fields but found " + parts.length);
        }
        for (int i = TASK_DESCRIPTION_INDEX; i < expectedPartCount; i++) {
            if (unescapeFileField(parts[i]).isBlank()) {
                throw new HabpyDuckException("task details cannot be empty");
            }
        }
        validateSavedTags(parts, expectedPartCount);
    }

    /**
     * Loads saved tags into a task if the saved line has a tag field.
     *
     * @param task the task to tag.
     * @param parts the saved line split into fields.
     * @throws HabpyDuckException if the tag field is malformed.
     */
    private void loadTags(Task task, String[] parts) throws HabpyDuckException {
        int tagFieldIndex = getExpectedPartCount(parts[TASK_TYPE_INDEX]);
        if (parts.length == tagFieldIndex) {
            return;
        }

        String[] savedTags = parts[tagFieldIndex].split(TAG_SEPARATOR, -1);
        for (String savedTag : savedTags) {
            task.addTag(parseSavedTag(savedTag));
        }
    }

    /**
     * Checks the optional saved tag field.
     *
     * @param parts the saved line split into fields.
     * @param expectedPartCount the field count for an untagged task of the same type.
     * @throws HabpyDuckException if the tag field is malformed.
     */
    private void validateSavedTags(String[] parts, int expectedPartCount) throws HabpyDuckException {
        if (parts.length == expectedPartCount) {
            return;
        }

        String[] savedTags = parts[expectedPartCount].split(TAG_SEPARATOR, -1);
        for (String savedTag : savedTags) {
            parseSavedTag(savedTag);
        }
    }

    /**
     * Converts saved tag text into a normalized tag.
     *
     * @param savedTag the saved tag text.
     * @return the normalized tag.
     * @throws HabpyDuckException if the saved tag is malformed.
     */
    private String parseSavedTag(String savedTag) throws HabpyDuckException {
        String tag = Task.normalizeTag(unescapeFileField(savedTag));
        if (!Task.isValidTag(tag)) {
            throw new HabpyDuckException("saved tags must start with # and use valid tag characters");
        }
        return tag;
    }

    /**
     * Returns the field count for an untagged saved task of the given type.
     *
     * @param taskType the saved task type.
     * @return the expected field count.
     */
    private int getExpectedPartCount(String taskType) {
        switch (taskType) {
            case TODO_TASK_TYPE:
                return TODO_FIELD_COUNT;
            case DEADLINE_TASK_TYPE:
                return DEADLINE_FIELD_COUNT;
            case EVENT_TASK_TYPE:
                return EVENT_FIELD_COUNT;
            default:
                assert false : "Task type should be validated before resolving field count";
                return TODO_FIELD_COUNT;
        }
    }

    /**
     * Escapes special characters so user text can be stored safely on one line.
     *
     * @param field the task text to save.
     * @return the escaped text.
     */
    public static String escapeFileField(String field) {
        return field.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("|", "\\|");
    }

    /**
     * Restores special characters that were escaped for the save file.
     *
     * @param field the saved text to restore.
     * @return the unescaped text.
     */
    private String unescapeFileField(String field) {
        StringBuilder result = new StringBuilder();
        boolean isEscaping = false;
        for (int i = 0; i < field.length(); i++) {
            char character = field.charAt(i);
            if (!isEscaping && character == '\\') {
                isEscaping = true;
                continue;
            }
            if (isEscaping) {
                switch (character) {
                    case 'n':
                        result.append('\n');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    default:
                        result.append(character);
                        break;
                }
                isEscaping = false;
                continue;
            }
            result.append(character);
        }
        if (isEscaping) {
            result.append('\\');
        }
        return result.toString();
    }
}
