package ducky;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles saving tasks to and loading tasks from the hard disk.
 */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String COMPLETE_STATUS = "1";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final String TAG_SEPARATOR = ",";
    private static final String NO_TAGS = "-";

    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DEADLINE_DATE_FIELD_INDEX = 3;
    private static final int EVENT_START_FIELD_INDEX = 3;
    private static final int EVENT_END_FIELD_INDEX = 4;
    private static final int TODO_TAGS_FIELD_INDEX = 3;
    private static final int DEADLINE_TAGS_FIELD_INDEX = 4;
    private static final int EVENT_TAGS_FIELD_INDEX = 5;

    private static final int MINIMUM_FIELD_COUNT = 3;
    private static final int LEGACY_TODO_FIELD_COUNT = 3;
    private static final int LEGACY_DEADLINE_FIELD_COUNT = 4;
    private static final int LEGACY_EVENT_FIELD_COUNT = 5;
    private static final int TODO_FIELD_COUNT = 4;
    private static final int DEADLINE_FIELD_COUNT = 5;
    private static final int EVENT_FIELD_COUNT = 6;

    private final Path filePath;

    /**
     * Creates storage that uses the given data file.
     *
     * @param filePath the path of the data file.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads all saved tasks from the data file.
     *
     * @return the saved tasks, or an empty list when the file does not exist.
     * @throws DuckyException if the file cannot be read or contains invalid data.
     */
    public List<Task> load() throws DuckyException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            TaskList tasks = new TaskList();
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    Task task = parseTask(line);
                    if (tasks.containsEquivalent(task)) {
                        throw new DuckyException("Sorry, your save file contains duplicate tasks.");
                    }
                    tasks.add(task);
                }
            }
            return tasks.getTasks();
        } catch (IOException | SecurityException e) {
            throw new DuckyException("Sorry, I could not load your saved tasks.");
        }
    }

    /**
     * Saves all current tasks to the data file.
     *
     * @param taskList the tasks to save.
     * @throws DuckyException if the directory or file cannot be written.
     */
    public void save(TaskList taskList) throws DuckyException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> lines = new ArrayList<>();
            for (Task task : taskList.getTasks()) {
                lines.add(formatTask(task));
            }
            Files.write(filePath, lines);
        } catch (IOException | SecurityException e) {
            throw new DuckyException("Sorry, I could not save your tasks.");
        }
    }

    /**
     * Converts one saved line into a task object.
     *
     * @param line the saved task line.
     * @return the task represented by the line.
     * @throws DuckyException if the line is invalid.
     */
    private Task parseTask(String line) throws DuckyException {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (fields.length < MINIMUM_FIELD_COUNT) {
            throw new DuckyException("Sorry, your save file contains invalid task data.");
        }

        Task task;
        switch (fields[TYPE_FIELD_INDEX]) {
            case TODO_TYPE:
                task = parseTodoTask(fields);
                break;
            case DEADLINE_TYPE:
                task = parseDeadlineTask(fields);
                break;
            case EVENT_TYPE:
                task = parseEventTask(fields);
                break;
            default:
                throw new DuckyException("Sorry, your save file contains an unknown task type.");
        }

        restoreCompletionStatus(task, fields[STATUS_FIELD_INDEX]);
        return task;
    }

    /**
     * Creates a to-do task from its saved fields.
     *
     * @param fields the fields in a saved to-do record.
     * @return the task represented by the fields.
     * @throws DuckyException if the record has an invalid number of fields.
     */
    private Task parseTodoTask(String[] fields) throws DuckyException {
        if (fields.length != LEGACY_TODO_FIELD_COUNT && fields.length != TODO_FIELD_COUNT) {
            throw new DuckyException("Sorry, your save file contains invalid todo data.");
        }
        List<String> tags = parseTags(fields, TODO_TAGS_FIELD_INDEX);
        validateDescription(fields[DESCRIPTION_FIELD_INDEX]);
        return new ToDo(fields[DESCRIPTION_FIELD_INDEX], tags);
    }

    /**
     * Creates a deadline task from its saved fields.
     *
     * @param fields the fields in a saved deadline record.
     * @return the task represented by the fields.
     * @throws DuckyException if the record structure or deadline date is invalid.
     */
    private Task parseDeadlineTask(String[] fields) throws DuckyException {
        if (fields.length != LEGACY_DEADLINE_FIELD_COUNT && fields.length != DEADLINE_FIELD_COUNT) {
            throw new DuckyException("Sorry, your save file contains invalid deadline data.");
        }
        try {
            String description = fields[DESCRIPTION_FIELD_INDEX];
            validateDescription(description);
            LocalDate deadline = LocalDate.parse(fields[DEADLINE_DATE_FIELD_INDEX]);
            List<String> tags = parseTags(fields, DEADLINE_TAGS_FIELD_INDEX);
            return new Deadline(description, deadline, tags);
        } catch (DateTimeParseException e) {
            throw new DuckyException("Sorry, your save file contains an invalid deadline date.");
        }
    }

    /**
     * Creates an event task from its saved fields.
     *
     * @param fields the fields in a saved event record.
     * @return the task represented by the fields.
     * @throws DuckyException if the record structure or event times are invalid.
     */
    private Task parseEventTask(String[] fields) throws DuckyException {
        if (fields.length != LEGACY_EVENT_FIELD_COUNT && fields.length != EVENT_FIELD_COUNT) {
            throw new DuckyException("Sorry, your save file contains invalid event data.");
        }
        try {
            String description = fields[DESCRIPTION_FIELD_INDEX];
            validateDescription(description);
            LocalDateTime start = LocalDateTime.parse(fields[EVENT_START_FIELD_INDEX]);
            LocalDateTime end = LocalDateTime.parse(fields[EVENT_END_FIELD_INDEX]);
            if (!end.isAfter(start)) {
                throw new DuckyException("Sorry, your save file contains an invalid event time range.");
            }
            List<String> tags = parseTags(fields, EVENT_TAGS_FIELD_INDEX);
            return new Event(description, start, end, tags);
        } catch (DateTimeParseException e) {
            throw new DuckyException("Sorry, your save file contains invalid event times.");
        }
    }

    private void validateDescription(String description) throws DuckyException {
        if (description.isBlank()) {
            throw new DuckyException("Sorry, your save file contains an empty task description.");
        }
    }

    /**
     * Parses the optional tag field of a saved task.
     *
     * @param fields the fields in a saved task record.
     * @param tagsFieldIndex the expected index of the tag field.
     * @return the stored tags, or an empty list for a legacy or untagged record.
     */
    private List<String> parseTags(String[] fields, int tagsFieldIndex) {
        if (fields.length <= tagsFieldIndex || NO_TAGS.equals(fields[tagsFieldIndex])) {
            return List.of();
        }
        return Arrays.stream(fields[tagsFieldIndex].split(TAG_SEPARATOR))
                .filter(tag -> !tag.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Restores the completion status stored in a task line.
     *
     * @param task the task to update.
     * @param status the saved completion status.
     * @throws DuckyException if the status is invalid.
     */
    private void restoreCompletionStatus(Task task, String status) throws DuckyException {
        if (COMPLETE_STATUS.equals(status)) {
            task.markAsDone();
        } else if (!INCOMPLETE_STATUS.equals(status)) {
            throw new DuckyException("Sorry, your save file contains an invalid task status.");
        }
    }

    /**
     * Converts a task object into one line for the data file.
     *
     * @param task the task to format.
     * @return the serialized task.
     * @throws DuckyException if the task type is unsupported.
     */
    private String formatTask(Task task) throws DuckyException {
        String status = task.isDone() ? COMPLETE_STATUS : INCOMPLETE_STATUS;
        String tags = formatTags(task);
        if (task instanceof ToDo) {
            return String.join(FIELD_SEPARATOR, TODO_TYPE, status, task.getDescription(), tags);
        } else if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, DEADLINE_TYPE, status,
                    task.getDescription(), deadline.getBy().toString(), tags);
        } else if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, EVENT_TYPE, status,
                    task.getDescription(), event.getStart().toString(), event.getEnd().toString(), tags);
        }
        throw new DuckyException("Sorry, I could not save an unsupported task type.");
    }

    /**
     * Converts a task's tags into their storage representation.
     *
     * @param task the task whose tags should be formatted.
     * @return the comma-separated tags, or {@code -} when the task has no tags.
     */
    private String formatTags(Task task) {
        if (task.getTags().isEmpty()) {
            return NO_TAGS;
        }
        return String.join(TAG_SEPARATOR, task.getTags());
    }
}
