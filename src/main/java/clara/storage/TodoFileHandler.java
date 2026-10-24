package clara.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import clara.exception.ClaraException;
import clara.task.Deadline;
import clara.task.Event;
import clara.task.Priority;
import clara.task.Task;
import clara.task.Todo;

/**
 * Handles saving and loading tasks to and from Clara's task data file.
 */
public class TodoFileHandler {
    private static final Path FILE_PATH = Path.of("data", "todo-list.txt");
    private static final String DATE_TIME_FORMAT = "uuuu-MM-dd HHmm";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final String FIELD_SEPARATOR = "|";
    private static final String FIELD_SEPARATOR_PATTERN = Pattern.quote(FIELD_SEPARATOR);
    private static final int PRESERVE_TRAILING_EMPTY_FIELDS = -1;

    private static final int FIELD_COUNT = 6;
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;

    // NOTE: AI-assisted task priority feature. See CITATIONS.md [C-018].
    private static final int PRIORITY_FIELD_INDEX = 2;
    private static final int TITLE_FIELD_INDEX = 3;
    private static final int FIRST_TIME_FIELD_INDEX = 4;
    private static final int SECOND_TIME_FIELD_INDEX = 5;

    private static final String TODO_TYPE = "t";
    private static final String DEADLINE_TYPE = "d";
    private static final String EVENT_TYPE = "e";

    private static final String DONE_STATUS = "x";
    private static final String NOT_DONE_STATUS = "o";

    private static final String LOW_PRIORITY = "l";
    private static final String MEDIUM_PRIORITY = "m";
    private static final String HIGH_PRIORITY = "h";

    private static final String EMPTY_FIELD = "";

    // NOTE: AI-assisted saved-task format and validation. See CITATIONS.md [C-004], [C-017].
    /**
     * Stores one task per line using the following format:
     *
     * <pre>
     * task     ::= type "|" status "|" priority "|" title "|" time1 "|" time2
     * type     ::= "t" | "d" | "e"
     * status   ::= "x" | "o"
     * priority ::= "l" | "m" | "h" -- corresponding to low, medium, high
     * </pre>
     *
     * <p>Todo tasks leave {@code time1} and {@code time2} empty, while deadline tasks leave {@code
     * time2} empty.
     *
     * @param line the serialized task line to parse
     * @return the task represented by the given line
     * @throws ClaraException if the line is malformed or contains invalid task data
     */
    private static Task parseLine(final String line) throws ClaraException {
        String[] arguments = splitLine(line);
        validateStatus(arguments);
        Priority priority = parsePriority(arguments[PRIORITY_FIELD_INDEX]);
        validateTitle(arguments);
        try {
            Task task = createTask(arguments);
            task.setDone(arguments[STATUS_FIELD_INDEX].equals(DONE_STATUS));
            task.setPriority(priority);
            return task;
        } catch (DateTimeParseException ex) {
            throw new ClaraException("Saved task has an invalid date or time.");
        }
    }

    /**
     * Splits a serialized task line and validates its overall structure.
     *
     * @param line the serialized task line
     * @return the individual serialized fields
     * @throws ClaraException if the line is blank or does not contain six fields
     */
    private static String[] splitLine(String line) throws ClaraException {
        if (line.isBlank()) {
            throw new ClaraException("Saved task line cannot be empty.");
        }
        String[] arguments = line.split(FIELD_SEPARATOR_PATTERN, PRESERVE_TRAILING_EMPTY_FIELDS);
        if (arguments.length != FIELD_COUNT) {
            throw new ClaraException("Saved task has an invalid number of fields.");
        }
        return arguments;
    }

    /**
     * Validates the completion-status field in a serialized task.
     *
     * @param arguments the serialized task fields
     * @throws ClaraException if the status is not a supported code
     */
    private static void validateStatus(String[] arguments) throws ClaraException {
        if (!arguments[STATUS_FIELD_INDEX].equals(DONE_STATUS)
                && !arguments[STATUS_FIELD_INDEX].equals(NOT_DONE_STATUS)) {
            throw new ClaraException("Saved task has an invalid status.");
        }
    }

    /**
     * Validates the title field in a serialized task.
     *
     * @param arguments the serialized task fields
     * @throws ClaraException if the title is blank
     */
    private static void validateTitle(String[] arguments) throws ClaraException {
        if (arguments[TITLE_FIELD_INDEX].isBlank()) {
            throw new ClaraException("Saved task needs a title.");
        }
    }

    /**
     * Creates the task represented by the type-specific serialized fields.
     *
     * @param arguments the serialized task fields
     * @return the created task without its status or priority applied
     * @throws ClaraException if the type or its fields are invalid
     */
    private static Task createTask(String[] arguments) throws ClaraException {
        return switch (arguments[TYPE_FIELD_INDEX]) {
            case TODO_TYPE -> createTodo(arguments);
            case DEADLINE_TYPE -> createDeadline(arguments);
            case EVENT_TYPE -> createEvent(arguments);
            default -> throw new ClaraException("Saved task has an unknown type.");
        };
    }

    /**
     * Creates a todo task after validating that it has no time fields.
     *
     * @param arguments the serialized task fields
     * @return the created todo task
     * @throws ClaraException if either time field is present
     */
    private static Todo createTodo(String[] arguments) throws ClaraException {
        if (!arguments[FIRST_TIME_FIELD_INDEX].isEmpty()
                || !arguments[SECOND_TIME_FIELD_INDEX].isEmpty()) {
            throw new ClaraException("Saved todo task must not have times.");
        }
        return new Todo(arguments[TITLE_FIELD_INDEX]);
    }

    /**
     * Creates a deadline task after validating its time fields.
     *
     * @param arguments the serialized task fields
     * @return the created deadline task
     * @throws ClaraException if the deadline fields are invalid
     */
    private static Deadline createDeadline(String[] arguments) throws ClaraException {
        if (arguments[FIRST_TIME_FIELD_INDEX].isBlank()
                || !arguments[SECOND_TIME_FIELD_INDEX].isEmpty()) {
            throw new ClaraException("Saved deadline task has invalid times.");
        }
        return new Deadline(
                arguments[TITLE_FIELD_INDEX], parseDateTime(arguments[FIRST_TIME_FIELD_INDEX]));
    }

    /**
     * Creates an event task after validating its time fields and ordering.
     *
     * @param arguments the serialized task fields
     * @return the created event task
     * @throws ClaraException if either time is missing or the event does not end after it starts
     */
    private static Event createEvent(String[] arguments) throws ClaraException {
        if (arguments[FIRST_TIME_FIELD_INDEX].isBlank()
                || arguments[SECOND_TIME_FIELD_INDEX].isBlank()) {
            throw new ClaraException("Saved event task needs start and end times.");
        }
        LocalDateTime fromTime = parseDateTime(arguments[FIRST_TIME_FIELD_INDEX]);
        LocalDateTime toTime = parseDateTime(arguments[SECOND_TIME_FIELD_INDEX]);
        if (!fromTime.isBefore(toTime)) {
            throw new ClaraException("Saved event must start before it ends.");
        }
        return new Event(arguments[TITLE_FIELD_INDEX], fromTime, toTime);
    }

    /**
     * Converts a priority code in the saved-task format to a task priority.
     *
     * @param priorityCode the priority code to convert
     * @return the matching task priority
     * @throws ClaraException if the priority code is invalid
     */
    private static Priority parsePriority(String priorityCode) throws ClaraException {
        return switch (priorityCode) {
            case LOW_PRIORITY -> Priority.LOW;
            case MEDIUM_PRIORITY -> Priority.MEDIUM;
            case HIGH_PRIORITY -> Priority.HIGH;
            default -> throw new ClaraException("Saved task has an invalid priority.");
        };
    }

    // AI-assisted task serialization. See CITATIONS.md [C-004].

    /**
     * Converts a task into its serialized file representation.
     *
     * @param task the task to serialize
     * @return a single line representing the task in the saved-task format
     * @throws IllegalArgumentException if the task is not a supported subclass of {@link Task}
     */
    private static String taskToFileLine(final Task task) {
        return switch (task) {
            case Todo todo -> String.join(FIELD_SEPARATOR, TODO_TYPE, getStatus(todo),
                    getPriority(todo), todo.getTaskName(), EMPTY_FIELD, EMPTY_FIELD);
            case Deadline deadline -> String.join(FIELD_SEPARATOR, DEADLINE_TYPE, getStatus(deadline),
                    getPriority(deadline), deadline.getTaskName(),
                    deadline.getDeadlineTime().format(DATE_TIME_FORMATTER), EMPTY_FIELD);
            case Event event -> String.join(FIELD_SEPARATOR, EVENT_TYPE, getStatus(event),
                    getPriority(event), event.getTaskName(),
                    event.getFromTime().format(DATE_TIME_FORMATTER),
                    event.getToTime().format(DATE_TIME_FORMATTER));
            default -> throw new IllegalArgumentException("Unknown subclass of Task encountered");
        };
    }

    /**
     * Parses a date and time in the format used by Clara's save file.
     *
     * @param dateTimeText the saved date and time
     * @return the parsed date and time
     * @throws DateTimeParseException if the date or time is invalid
     */
    private static LocalDateTime parseDateTime(final String dateTimeText) throws DateTimeParseException {
        return LocalDateTime.parse(dateTimeText, DATE_TIME_FORMATTER);
    }

    private static String getStatus(Task task) {
        return task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;
    }

    private static String getPriority(Task task) {
        return switch (task.getPriority()) {
            case LOW -> LOW_PRIORITY;
            case MEDIUM -> MEDIUM_PRIORITY;
            case HIGH -> HIGH_PRIORITY;
        };
    }

    // NOTE: AI-assisted buffered file saving. See CITATIONS.md [C-004], [C-017].
    /**
     * Saves all tasks to the task data file, replacing any previously saved tasks.
     *
     * @param tasks the list of tasks to save
     * @throws ClaraException if the directory cannot be created or the tasks cannot be written to the
     *                        file
     */
    public static void flushTasksToDisk(final List<Task> tasks) throws ClaraException {
        Path temporaryFile = null;
        try {
            Files.createDirectories(FILE_PATH.getParent());
            temporaryFile = Files.createTempFile(FILE_PATH.getParent(), "todo-list-", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile)) {
                for (Task task : tasks) {
                    writer.write(taskToFileLine(task));
                    writer.newLine();
                }
            }
            moveIntoPlace(temporaryFile);
            temporaryFile = null;
        } catch (IOException ex) {
            throw new ClaraException("Unable to write saved tasks");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // The original write failure is more useful to the user than a cleanup failure.
                }
            }
        }
    }

    /**
     * Replaces the task file with a completed temporary file.
     *
     * @param temporaryFile the completed replacement file
     * @throws IOException if the replacement cannot be completed
     */
    private static void moveIntoPlace(final Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, FILE_PATH,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporaryFile, FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // AI-assisted buffered file loading. See CITATIONS.md [C-004], [C-017].

    /**
     * Loads saved tasks from the task data file into the given list.
     *
     * <p>If the data file does not exist, the list is left unchanged. Otherwise, the existing
     * contents of the list are cleared before the saved tasks are loaded.
     *
     * @param tasks the list into which saved tasks are loaded
     * @throws ClaraException if the saved task file cannot be read
     */
    public static void loadTasksFromDisk(List<Task> tasks) throws ClaraException {
        if (!Files.exists(FILE_PATH)) {
            return;
        }

        List<Task> loadedTasks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;

            while ((line = reader.readLine()) != null) {
                loadedTasks.add(parseLine(line));
            }
        } catch (IOException exception) {
            throw new ClaraException("Unable to load saved tasks");
        }

        tasks.clear();
        tasks.addAll(loadedTasks);
    }
}
