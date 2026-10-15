package gooble.task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import gooble.GoobleException;
import gooble.storage.Storage;

/**
 * Manages the tasks in Gooble's task list.
 */
public class TaskList {

    /** Location where the current task list is saved between application runs. */
    private static final Path STORAGE_PATH = Path.of("data", "Gooble.txt");
    private static final String GENERIC_TYPE = "G";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final char INCOMPLETE_STATUS = '0';
    private static final char COMPLETE_STATUS = '1';
    private static final char LEGACY_INCOMPLETE_STATUS = ' ';
    private static final char LEGACY_COMPLETE_STATUS = 'X';
    private static final String LEGACY_TODO_PREFIX = "[T][";
    private static final String LEGACY_DEADLINE_PREFIX = "[D][";
    private static final String LEGACY_EVENT_PREFIX = "[E][";
    private static final String LEGACY_DEADLINE_MARKER = " (by: ";
    private static final String LEGACY_EVENT_START_MARKER = " (from: ";
    private static final String LEGACY_EVENT_END_MARKER = " to: ";
    private static final int LEGACY_STATUS_INDEX = 4;
    private static final int LEGACY_TYPED_DESCRIPTION_START = 7;
    private static final int LEGACY_GENERIC_STATUS_INDEX = 1;
    private static final int LEGACY_GENERIC_DESCRIPTION_START = 4;
    private static final int LEGACY_TYPED_MIN_LENGTH = 7;
    private static final int LEGACY_GENERIC_MIN_LENGTH = 5;
    private static final String LEGACY_RECORD_SUFFIX = ")";

    private final ArrayList<Task> tasks;
    private final Storage storage;

    /** Creates a task list backed by the default Gooble storage file. */
    public TaskList() {
        this(new Storage(STORAGE_PATH));
    }

    /**
     * Creates a task list backed by the supplied storage component.
     *
     * @param storage component used to load and save task records
     */
    public TaskList(Storage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Storage cannot be null.");
        }
        this.tasks = new ArrayList<>();
        this.storage = storage;
        load();
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task cannot be null.");
        }
        if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
            throw new IllegalArgumentException("A task with the same details already exists.");
        }
        tasks.add(task);
        save();
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        Task removedTask = tasks.remove(index);
        save();
        return removedTask;
    }


    /**
     * Returns the task at the given index.
     *
     * @param index zero-based index of the task
     * @return the task at that index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public List<Task> findByDescription(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                        || task.getTags().stream().anyMatch(tag -> tag.contains(normalizedKeyword)))
                .collect(Collectors.toList());
    }

    /** Adds a tag to the task at the given index and saves the updated list. */
    public void addTag(int index, String tag) {
        tasks.get(index).addTag(tag);
        save();
    }

    /** Removes all tags from the task at the given index and saves the updated list. */
    public void removeTags(int index) {
        tasks.get(index).removeTags();
        save();
    }

    /**
     * Returns whether the given index refers to a valid task position.
     *
     * @param index zero-based index to check
     * @return {@code true} if the index is within range
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Marks a task as completed and saves the updated list.
     *
     * @param index zero-based index of the task to mark
     */
    public void markAsDone(int index) {
        tasks.get(index).markAsDone();
        save();
    }

    /**
     * Marks a task as incomplete and saves the updated list.
     *
     * @param index zero-based index of the task to unmark
     */
    public void markAsNotDone(int index) {
        tasks.get(index).markAsNotDone();
        save();
    }

    /**
     * Rewrites the storage file with a simple representation of every task.
     */
    private void save() {
        List<String> savedTasks = tasks.stream().map(this::serialize).collect(Collectors.toList());
        // Every in-memory task must have exactly one persisted record.
        assert savedTasks.size() == tasks.size();
        storage.save(savedTasks);
    }

    /**
     * Encodes task fields so separators in user input cannot corrupt the file.
     *
     * @param task task to encode
     * @return one storage record
     */
    private String serialize(Task task) {
        String type = serializedType(task);
        List<String> fields = serializedFields(task);

        StringBuilder record = new StringBuilder(type)
                .append('|').append("X".equals(task.getStatusIcon())
                        ? COMPLETE_STATUS : INCOMPLETE_STATUS);
        for (String field : fields) {
            record.append('|').append(encode(field));
        }
        record.append('|').append(encode(String.join(",", task.getTags())));
        return record.toString();
    }

    private String serializedType(Task task) {
        if (task instanceof Todo) {
            return TODO_TYPE;
        } else if (task instanceof Deadline) {
            return DEADLINE_TYPE;
        } else if (task instanceof Event) {
            return EVENT_TYPE;
        }
        return GENERIC_TYPE;
    }

    private List<String> serializedFields(Task task) {
        List<String> fields = new ArrayList<>();
        fields.add(task.getDescription());
        if (task instanceof Todo) {
            return fields;
        } else if (task instanceof Deadline deadline) {
            fields.add(deadline.getStoredDeadline());
        } else if (task instanceof Event event) {
            fields.add(event.getStartDate());
            fields.add(event.getEndDate());
        }
        return fields;
    }

    /** Encodes one user-provided field for storage. */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes one persisted field, returning null when it is invalid. */
    private String decode(String value) {
        try {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Restores tasks from the storage file when it is available.
     */
    private void load() {
        storage.load().stream()
                .map(this::parseSavedTask)
                .filter(Objects::nonNull)
                .forEach(tasks::add);
    }

    /**
     * Creates a task from the display format used by the storage file.
     *
     * @param savedTask one line from the storage file
     * @return the restored task, or {@code null} when the line is not recognised
     */
    private Task parseSavedTask(String savedTask) {
        Task persistedTask = parsePersistedTask(savedTask);
        if (persistedTask != null) {
            return persistedTask;
        }

        return parseLegacyTask(savedTask);
    }

    /** Parses the current type|status|encoded-fields format. */
    private Task parsePersistedTask(String savedTask) {
        String[] fields = savedTask.split("\\|", -1);
        if (!hasValidPersistedRecord(fields)) {
            return null;
        }

        List<String> decodedFields = decodePersistedFields(fields);
        if (decodedFields == null) {
            return null;
        }
        int expectedFieldCount = expectedFieldCount(fields[0]);
        if (!hasValidFieldCount(decodedFields, expectedFieldCount)) {
            return null;
        }

        String storedTags = decodedFields.size() == expectedFieldCount + 1
                ? decodedFields.remove(decodedFields.size() - 1) : "";
        if (!hasValidDecodedFields(decodedFields) || !restoreTagsIfValid(null, storedTags)) {
            return null;
        }

        return restorePersistedTask(fields, decodedFields, storedTags);
    }

    private boolean hasValidPersistedRecord(String[] fields) {
        return fields.length >= 3 && fields[0].length() == 1 && fields[1].length() == 1
                && (fields[1].charAt(0) == INCOMPLETE_STATUS
                || fields[1].charAt(0) == COMPLETE_STATUS);
    }

    private List<String> decodePersistedFields(String[] fields) {
        List<String> decodedFields = new ArrayList<>();
        for (int i = 2; i < fields.length; i++) {
            String decoded = decode(fields[i]);
            if (decoded == null) {
                return null;
            }
            decodedFields.add(decoded);
        }
        return decodedFields;
    }

    private boolean hasValidFieldCount(List<String> fields, int expectedFieldCount) {
        return expectedFieldCount > 0 && fields.size() >= expectedFieldCount
                && fields.size() <= expectedFieldCount + 1;
    }

    private boolean hasValidDecodedFields(List<String> fields) {
        return !fields.isEmpty() && fields.stream().noneMatch(String::isBlank);
    }

    private Task restorePersistedTask(String[] fields, List<String> decodedFields, String storedTags) {
        // The type-specific parser relies on having valid, non-blank fields.
        assert hasValidDecodedFields(decodedFields);
        try {
            Task task = createPersistedTask(fields[0], decodedFields);
            if (task == null || !restoreTagsIfValid(task, storedTags)) {
                return null;
            }
            return restoreStatus(task, fields[1].charAt(0) == COMPLETE_STATUS
                    ? LEGACY_COMPLETE_STATUS : LEGACY_INCOMPLETE_STATUS);
        } catch (GoobleException | IllegalArgumentException e) {
            return null;
        }
    }

    /** Returns the number of non-tag fields required for a persisted type. */
    private int expectedFieldCount(String type) {
        return switch (type) {
            case GENERIC_TYPE, TODO_TYPE -> 1;
            case DEADLINE_TYPE -> 2;
            case EVENT_TYPE -> 3;
            default -> 0;
        };
    }

    /** Restores valid persisted tags, or validates tags when no task is supplied. */
    private boolean restoreTagsIfValid(Task task, String storedTags) {
        if (storedTags.isEmpty()) {
            return true;
        }
        String[] storedTagValues = storedTags.split(",", -1);
        Set<String> uniqueTags = new HashSet<>();
        for (String tag : storedTagValues) {
            if (!Task.isValidTag(tag) || !uniqueTags.add(tag)) {
                return false;
            }
            if (task != null) {
                task.addTag(tag);
            }
        }
        return true;
    }

    /** Creates a task from a validated persisted type and its decoded fields. */
    private Task createPersistedTask(String type, List<String> fields) throws GoobleException {
        switch (type) {
            case GENERIC_TYPE:
                return fields.size() == 1 ? new Task(fields.get(0)) : null;
            case TODO_TYPE:
                return fields.size() == 1 ? new Todo(fields.get(0)) : null;
            case DEADLINE_TYPE:
                return fields.size() == 2
                        ? new Deadline(fields.get(0), DeadlineDateParser.parse(fields.get(1))) : null;
            case EVENT_TYPE:
                return fields.size() == 3
                        ? new Event(fields.get(0), fields.get(1), fields.get(2)) : null;
            default:
                return null;
        }
    }

    /** Parses the display format written by the first persistence version. */
    private Task parseLegacyTask(String savedTask) {
        if (savedTask.startsWith(LEGACY_TODO_PREFIX)) {
            return parseLegacyTodo(savedTask);
        }
        if (savedTask.startsWith(LEGACY_DEADLINE_PREFIX)) {
            return parseLegacyDeadline(savedTask);
        }
        if (savedTask.startsWith(LEGACY_EVENT_PREFIX)) {
            return parseLegacyEvent(savedTask);
        }
        return parseLegacyGeneric(savedTask);
    }

    /** Parses a legacy todo record. */
    private Task parseLegacyTodo(String savedTask) {
        if (savedTask.length() < LEGACY_TYPED_MIN_LENGTH) {
            return null;
        }
        char status = savedTask.charAt(LEGACY_STATUS_INDEX);
        String description = savedTask.substring(LEGACY_TYPED_DESCRIPTION_START);
        return validStatus(status) && !description.isBlank()
                ? restoreStatus(new Todo(description), status) : null;
    }

    /** Parses a legacy deadline record. */
    private Task parseLegacyDeadline(String savedTask) {
        if (!hasMinimumLegacyLength(savedTask)) {
            return null;
        }
        char status = savedTask.charAt(LEGACY_STATUS_INDEX);
        int deadlineMarker = savedTask.lastIndexOf(LEGACY_DEADLINE_MARKER);
        if (!hasValidLegacyDeadlineStructure(savedTask, status, deadlineMarker)) {
            return null;
        }
        String description = savedTask.substring(LEGACY_TYPED_DESCRIPTION_START, deadlineMarker);
        String deadline = savedTask.substring(deadlineMarker + LEGACY_DEADLINE_MARKER.length(),
                savedTask.length() - LEGACY_RECORD_SUFFIX.length());
        if (description.isBlank() || deadline.isBlank()) {
            return null;
        }
        return parseLegacyDeadlineValue(description, deadline, status);
    }

    private boolean hasMinimumLegacyLength(String savedTask) {
        return savedTask.length() >= LEGACY_TYPED_MIN_LENGTH;
    }

    private boolean hasValidLegacyDeadlineStructure(String savedTask, char status, int deadlineMarker) {
        return validStatus(status) && deadlineMarker > LEGACY_TYPED_DESCRIPTION_START
                && savedTask.endsWith(LEGACY_RECORD_SUFFIX);
    }

    private Task parseLegacyDeadlineValue(String description, String deadline, char status) {
        try {
            return restoreStatus(new Deadline(description, DeadlineDateParser.parse(deadline)), status);
        } catch (GoobleException e) {
            return null;
        }
    }

    /** Parses a legacy event record. */
    private Task parseLegacyEvent(String savedTask) {
        if (!hasMinimumLegacyLength(savedTask)) {
            return null;
        }
        char status = savedTask.charAt(LEGACY_STATUS_INDEX);
        int startMarker = savedTask.lastIndexOf(LEGACY_EVENT_START_MARKER);
        int endMarker = savedTask.lastIndexOf(LEGACY_EVENT_END_MARKER);
        if (!hasValidLegacyEventStructure(savedTask, status, startMarker, endMarker)) {
            return null;
        }
        String description = savedTask.substring(LEGACY_TYPED_DESCRIPTION_START, startMarker);
        String startDate = savedTask.substring(startMarker + LEGACY_EVENT_START_MARKER.length(), endMarker);
        String endDate = savedTask.substring(endMarker + LEGACY_EVENT_END_MARKER.length(),
                savedTask.length() - LEGACY_RECORD_SUFFIX.length());
        return description.isBlank() || startDate.isBlank() || endDate.isBlank()
                ? null : restoreStatus(new Event(description, startDate, endDate), status);
    }

    private boolean hasValidLegacyEventStructure(String savedTask, char status,
                                                 int startMarker, int endMarker) {
        return validStatus(status) && startMarker > LEGACY_TYPED_DESCRIPTION_START
                && endMarker > startMarker && savedTask.endsWith(LEGACY_RECORD_SUFFIX);
    }

    /** Parses a legacy generic task record. */
    private Task parseLegacyGeneric(String savedTask) {
        if (savedTask.length() < LEGACY_GENERIC_MIN_LENGTH || !savedTask.startsWith("[")
                || !validStatus(savedTask.charAt(LEGACY_GENERIC_STATUS_INDEX))
                || savedTask.charAt(2) != ']' || savedTask.charAt(3) != ' ') {
            return null;
        }
        String description = savedTask.substring(LEGACY_GENERIC_DESCRIPTION_START);
        return description.isBlank() ? null
                : restoreStatus(new Task(description), savedTask.charAt(LEGACY_GENERIC_STATUS_INDEX));
    }

    /** Returns whether a legacy record contains a supported status marker. */
    private boolean validStatus(char status) {
        return status == LEGACY_INCOMPLETE_STATUS || status == LEGACY_COMPLETE_STATUS;
    }

    /**
     * Applies the saved completion status to a newly created task.
     *
     * @param task task created from saved details
     * @param status saved status character
     * @return the task with its saved completion status restored
     */
    private Task restoreStatus(Task task, char status) {
        // This helper is called only after parsing has created a valid task and status.
        assert task != null;
        assert validStatus(status);
        if (status == LEGACY_COMPLETE_STATUS) {
            task.markAsDone();
        }
        return task;
    }

}
