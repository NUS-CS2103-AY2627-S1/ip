package turtley.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import turtley.exception.TurtleyException;
import turtley.model.Deadline;
import turtley.model.Event;
import turtley.model.TagValidator;
import turtley.model.Task;
import turtley.model.TaskList;
import turtley.model.TaskType;
import turtley.model.ToDo;
import turtley.util.DateTimeParser;

// Written with AI assistance.
/**
 * Loads and saves Turtley's task list to the local file system.
 */
public class Storage {

    private static final int MAX_TASK_COUNT = 100;
    private static final String TASK_LIMIT_MESSAGE = "task list exceeds " + MAX_TASK_COUNT + " tasks.";
    private final Path dataFile;
    private final Path tempDataFile;

    /**
     * Creates a storage service for the supplied save-file path.
     *
     * @param filePath the save-file path.
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new TurtleyException("Unable to use an empty save-file path.");
        }
        try {
            dataFile = Path.of(filePath);
            tempDataFile = Path.of(filePath + ".tmp");
        } catch (InvalidPathException | SecurityException exception) {
            throw new TurtleyException("Unable to use save-file path.", exception);
        }
    }

    /**
     * Replaces the saved task list with the supplied tasks.
     *
     * @param tasks the current task list.
     * @throws TurtleyException if the task list is invalid or cannot be written.
     */
    public void save(List<Task> tasks) {
        validateTasksForSaving(tasks);
        String fileContents = serializeTasks(tasks);

        try {
            prepareParentDirectory();
            if (tasks.isEmpty()) {
                deleteDataFile();
                return;
            }
            writeTasks(fileContents);
        } catch (IOException | SecurityException exception) {
            throw new TurtleyException("Unable to save tasks to disk.", exception);
        } finally {
            deleteTempDataFile();
        }
    }

    /**
     * Validates the task list before serializing it.
     *
     * @param tasks the task list to validate.
     * @throws TurtleyException if the task list is null or exceeds capacity.
     */
    private static void validateTasksForSaving(List<Task> tasks) {
        if (tasks == null) {
            throw new TurtleyException("Unable to save tasks: task list is null.");
        }
        if (tasks.size() > MAX_TASK_COUNT) {
            throw new TurtleyException("Unable to save tasks: " + TASK_LIMIT_MESSAGE);
        }
    }

    /**
     * Serializes all tasks into the save-file representation.
     *
     * @param tasks the tasks to serialize.
     * @return the serialized task records separated by the platform line separator.
     */
    private static String serializeTasks(List<Task> tasks) {
        StringBuilder fileContents = new StringBuilder();
        for (Task task : tasks) {
            if (fileContents.length() > 0) {
                fileContents.append(System.lineSeparator());
            }
            fileContents.append(serialize(task));
        }
        return fileContents.toString();
    }

    /**
     * Creates the parent directory for the save file when one is required.
     *
     * @throws IOException if the directory cannot be created.
     */
    private void prepareParentDirectory() throws IOException {
        Path parent = dataFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Deletes the save file when the task list becomes empty.
     *
     * @throws IOException if the file cannot be deleted.
     */
    private void deleteDataFile() throws IOException {
        Files.deleteIfExists(dataFile);
    }

    /**
     * Writes serialized tasks through a temporary file before replacing the save file.
     *
     * @param fileContents the serialized task records.
     * @throws IOException if the temporary file cannot be written or moved.
     */
    private void writeTasks(String fileContents) throws IOException {
        Files.writeString(tempDataFile, fileContents, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Files.move(tempDataFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Removes the temporary save file after a save attempt.
     */
    private void deleteTempDataFile() {
        try {
            Files.deleteIfExists(tempDataFile);
        } catch (IOException | SecurityException ignored) {
            // The next save overwrites the temporary file if cleanup is unavailable.
        }
    }

    /**
     * Reads all saved tasks. A missing save file represents an empty task list.
     *
     * @return the tasks found in the save file.
     * @throws TurtleyException if the file cannot be read or contains invalid data.
     */
    public List<Task> load() {
        try {
            if (Files.notExists(dataFile)) {
                return new ArrayList<>();
            }
            if (!Files.isRegularFile(dataFile)) {
                throw new TurtleyException("Unable to load tasks from disk: save path is not a file.");
            }

            List<Task> tasks = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.isBlank()) {
                        continue;
                    }
                    if (tasks.size() >= MAX_TASK_COUNT) {
                        throw new TurtleyException("Unable to load tasks from disk: " + TASK_LIMIT_MESSAGE);
                    }
                    tasks.add(deserialize(line, lineNumber));
                }
            }
            try {
                new TaskList(tasks);
            } catch (TurtleyException exception) {
                throw new TurtleyException("Unable to load tasks from disk: duplicate task data.", exception);
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new TurtleyException("Unable to load tasks from disk.", exception);
        }
    }

    /**
     * Converts one save-file line back into a task.
     *
     * @param line the serialized task line.
     * @param lineNumber the line number used in error messages.
     * @return the reconstructed task.
     * @throws TurtleyException if the line does not match the save format.
     */
    private static Task deserialize(String line, int lineNumber) {
        List<String> fields;
        try {
            fields = splitFields(line);
        } catch (TurtleyException exception) {
            throw new TurtleyException("Invalid task data on line " + lineNumber
                    + ": malformed escape sequence.", exception);
        }
        if (fields.size() < 3) {
            throw invalidLine(lineNumber, line);
        }

        boolean isDone = parseStatus(fields.get(1), lineNumber, line);
        Task task = switch (fields.get(0)) {
            case "T" -> deserializeToDo(fields, lineNumber, line);
            case "D" -> deserializeDeadline(fields, lineNumber, line);
            case "E" -> deserializeEvent(fields, lineNumber, line);
            default -> throw invalidLine(lineNumber, line);
        };

        assert task != null : "Every supported save-file record must create a task.";
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Reconstructs a to-do task from decoded save-file fields.
     *
     * @param fields the decoded save-file fields.
     * @param lineNumber the source line number.
     * @param line the complete source line.
     * @return the reconstructed to-do task.
     */
    private static Task deserializeToDo(List<String> fields, int lineNumber, String line) {
        requireFieldCount(fields, 3, 4, lineNumber, line);
        return new ToDo(requireLoadedField(fields.get(2), "description", lineNumber),
                deserializeTags(fields, 3, lineNumber, line));
    }

    /**
     * Reconstructs a deadline task from decoded save-file fields.
     *
     * @param fields the decoded save-file fields.
     * @param lineNumber the source line number.
     * @param line the complete source line.
     * @return the reconstructed deadline task.
     */
    private static Task deserializeDeadline(List<String> fields, int lineNumber, String line) {
        requireFieldCount(fields, 4, 5, lineNumber, line);
        return new Deadline(requireLoadedField(fields.get(2), "description", lineNumber),
                parseDateTime(fields.get(3), "deadline", lineNumber),
                deserializeTags(fields, 4, lineNumber, line));
    }

    /**
     * Reconstructs an event task from decoded save-file fields.
     *
     * @param fields the decoded save-file fields.
     * @param lineNumber the source line number.
     * @param line the complete source line.
     * @return the reconstructed event task.
     */
    private static Task deserializeEvent(List<String> fields, int lineNumber, String line) {
        requireFieldCount(fields, 5, 6, lineNumber, line);
        String description = requireLoadedField(fields.get(2), "description", lineNumber);
        Temporal from = parseDateTime(fields.get(3), "start time", lineNumber);
        Temporal to = parseDateTime(fields.get(4), "end time", lineNumber);
        List<String> tags = deserializeTags(fields, 5, lineNumber, line);
        try {
            return new Event(description, from, to, tags);
        } catch (TurtleyException exception) {
            throw new TurtleyException("Invalid task data on line " + lineNumber
                    + ": event start time must be before end time.", exception);
        }
    }

    /**
     * Requires a decoded record to contain the expected number of fields.
     *
     * @param fields the decoded save-file fields.
     * @param expectedCount the required field count.
     * @param lineNumber the source line number.
     * @param line the complete source line.
     */
    private static void requireFieldCount(List<String> fields, int legacyCount, int taggedCount,
            int lineNumber, String line) {
        if (fields.size() != legacyCount && fields.size() != taggedCount) {
            throw invalidLine(lineNumber, line);
        }
    }

    /**
     * Reconstructs and validates the optional tags field of a saved task.
     *
     * @param fields the decoded save-file fields.
     * @param tagFieldIndex the index of the optional tags field.
     * @param lineNumber the source line number.
     * @param line the complete source line.
     * @return the loaded tags, or an empty list for a legacy record.
     */
    private static List<String> deserializeTags(List<String> fields, int tagFieldIndex,
            int lineNumber, String line) {
        if (fields.size() == tagFieldIndex) {
            return List.of();
        }
        String tagField = fields.get(tagFieldIndex);
        if (tagField.isBlank()) {
            return List.of();
        }
        List<String> tags = List.of(tagField.split("\\s+"));
        try {
            TagValidator.validateTags(tags);
        } catch (TurtleyException exception) {
            throw new TurtleyException("Invalid task data on line " + lineNumber
                    + ": tags contain an invalid tag.", exception);
        }
        if (tags.size() > TagValidator.MAX_TAG_COUNT) {
            throw new TurtleyException("Invalid task data on line " + lineNumber
                    + ": task contains too many tags.");
        }
        return tags;
    }

    /**
     * Splits a record while respecting escaped separators and special characters.
     *
     * @param line the serialized record.
     * @return the decoded fields.
     */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaping = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaping) {
                switch (character) {
                    case '\\', '|' -> field.append(character);
                    case 'n' -> field.append('\n');
                    case 'r' -> field.append('\r');
                    default -> throw new TurtleyException("Malformed escape sequence.");
                }
                isEscaping = false;
            } else if (character == '\\') {
                isEscaping = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (isEscaping) {
            throw new TurtleyException("Malformed escape sequence.");
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Parses the completion flag stored in a save-file line.
     *
     * @param status the stored completion flag.
     * @param lineNumber the line number used in error messages.
     * @param line the complete line.
     * @return whether the task is complete.
     */
    private static boolean parseStatus(String status, int lineNumber, String line) {
        if ("1".equals(status)) {
            return true;
        }
        if ("0".equals(status)) {
            return false;
        }
        throw invalidLine(lineNumber, line);
    }

    /**
     * Ensures that a required field loaded from disk is present and non-blank.
     *
     * @param value the field value.
     * @param fieldName the field's human-readable name.
     * @param lineNumber the line number used in error messages.
     * @return the valid field value.
     */
    private static String requireLoadedField(String value, String fieldName, int lineNumber) {
        if (value == null || value.isBlank()) {
            throw new TurtleyException("Invalid task data on line " + lineNumber + ": "
                    + fieldName + " is blank.");
        }
        return value;
    }

    /**
     * Parses a date/time field loaded from disk and adds line context to failures.
     *
     * @param value the stored date/time text.
     * @param fieldName the field's human-readable name.
     * @param lineNumber the line number used in error messages.
     * @return the parsed date or date-time.
     */
    private static java.time.temporal.Temporal parseDateTime(String value, String fieldName, int lineNumber) {
        try {
            return DateTimeParser.parse(requireLoadedField(value, fieldName, lineNumber));
        } catch (TurtleyException exception) {
            throw new TurtleyException("Invalid task data on line " + lineNumber + ": "
                    + fieldName + " has an invalid date/time format.", exception);
        }
    }

    /**
     * Creates a consistent exception for malformed save-file entries.
     *
     * @param lineNumber the malformed line's number.
     * @param line the malformed line.
     * @return the resulting exception.
     */
    private static TurtleyException invalidLine(int lineNumber, String line) {
        return new TurtleyException("Invalid task data on line " + lineNumber + ": " + line);
    }

    /**
     * Converts a task into the line format used by the save file.
     *
     * @param task the task to serialize.
     * @return one save-file line for the task.
     */
    private static String serialize(Task task) {
        if (task == null) {
            throw new TurtleyException("Unable to save tasks: task list contains a null task.");
        }

        String description = requireField(task.getDescription(), "description");
        TaskType taskType = task.getTaskType();
        if (taskType == null) {
            throw new TurtleyException("Unable to save tasks: task type is invalid.");
        }
        StringBuilder line = new StringBuilder()
                .append(task.getTypeIcon())
                .append(" | ")
                .append(task.isDone() ? "1" : "0")
                .append(" | ")
                .append(escape(description));

        switch (taskType) {
            case TODO:
                break;
            case DEADLINE:
                if (!(task instanceof Deadline deadline)) {
                    throw new TurtleyException("Unable to save tasks: deadline has no deadline value.");
                }
                line.append(" | ").append(escape(DateTimeParser.format(deadline.getBy())));
                break;
            case EVENT:
                if (!(task instanceof Event event)) {
                    throw new TurtleyException("Unable to save tasks: event has no time range.");
                }
                line.append(" | ").append(escape(DateTimeParser.format(event.getFrom())))
                    .append(" | ").append(escape(DateTimeParser.format(event.getTo())));
                break;
            default:
                throw new TurtleyException("Unable to save tasks: task type is invalid.");
        }

        line.append(" | ").append(escape(String.join(" ", task.getTags())));
        return line.toString();
    }

    /**
     * Escapes characters that have a special meaning in the save-file format.
     *
     * @param value the field value.
     * @return the escaped field value.
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Ensures that a required save-file field is present and non-blank.
     *
     * @param value the field value.
     * @param fieldName the field's human-readable name.
     * @return the valid field value.
     */
    private static String requireField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new TurtleyException("Unable to save tasks: " + fieldName + " is blank.");
        }
        return value;
    }
}
