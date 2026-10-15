package dingleberry.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import dingleberry.model.Deadlines;
import dingleberry.model.Events;
import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.model.Todo;

/**
 * Loads and saves the task list to a plain-text file at a relative path, using
 * one pipe-delimited line per task (e.g. "T | 0 | read book"). Creates the
 * containing folder and file automatically if they don't exist yet.
 */
public class Storage {
    /** Identifies the type field in persisted task records. */
    private static final int FIELD_TYPE_INDEX = 0;
    /** Identifies the completion flag in persisted task records. */
    private static final int FIELD_COMPLETION_FLAG_INDEX = 1;
    /** Identifies the description field in persisted task records. */
    private static final int FIELD_DESCRIPTION_INDEX = 2;
    /** Identifies the first date/time field in persisted task records. */
    private static final int FIELD_FIRST_DATE_TIME_INDEX = 3;
    /** Identifies the second date/time field in persisted event records. */
    private static final int FIELD_SECOND_DATE_TIME_INDEX = 4;
    /** Defines the strict format used for persisted date/time values. */
    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    /** Defines the UTF-8 escape used for a newline in a description. */
    private static final char NEWLINE_ESCAPE = 'n';
    /** Defines the UTF-8 escape used for a carriage return in a description. */
    private static final char CARRIAGE_RETURN_ESCAPE = 'r';

    /** Stores the data file path. */
    private final Path filePath;

    /** Contains successfully loaded tasks and recoverable record warnings. */
    public static final class LoadResult {
        /** Stores the valid tasks recovered from the data file. */
        private final ArrayList<Task> tasks;
        /** Stores warnings for records skipped during recovery. */
        private final List<String> warnings;

        private LoadResult(final ArrayList<Task> loadedTasks,
                           final List<String> loadWarnings) {
            this.tasks = new ArrayList<>(loadedTasks);
            this.warnings = List.copyOf(loadWarnings);
        }

        /**
         * Returns the valid tasks recovered from storage.
         *
         * @return a copy of the recovered tasks.
         */
        public ArrayList<Task> getTasks() {
            return new ArrayList<>(tasks);
        }

        /**
         * Returns warnings describing skipped corrupted records.
         *
         * @return the immutable recovery warnings.
         */
        public List<String> getWarnings() {
            return warnings;
        }
    }

    /**
     * Creates storage backed by the given relative file path.
     *
     * @param relativeFilePath the path used for persisted task data.
     */
    public Storage(final String relativeFilePath) {
        try {
            this.filePath = Path.of(relativeFilePath);
        } catch (InvalidPathException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Invalid storage path: " + relativeFilePath, e);
        }
    }

    /**
     * Reads the data file into a task list, creating an empty file (and its
     * parent folder) first if it doesn't already exist. Lines that don't match
      * the expected format are skipped with a warning rather than aborting
      * load.
      *
      * @return the tasks loaded from disk.
     */
    public ArrayList<Task> load() throws IOException {
        LoadResult result = loadWithReport();
        for (String warning : result.getWarnings()) {
            System.err.println(warning);
        }
        return result.getTasks();
    }

    /**
     * Loads tasks and returns recoverable corruption warnings to the caller.
     *
     * @return the valid tasks and warnings for skipped records.
     * @throws IOException if the storage file cannot be accessed.
     */
    public LoadResult loadWithReport() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<String> warnings = new ArrayList<>();
        ensureParentDirectory();

        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                throw storageException("create storage file", e);
            }
            return new LoadResult(tasks, warnings);
        }

        final List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw storageException("load tasks", e);
        }
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                Task task = parseLine(line);
                tasks.add(task);
            } catch (IllegalArgumentException e) {
                warnings.add("Skipping corrupted task record: " + line
                        + " (" + e.getMessage() + ")");
                continue;
            }
        }
        return new LoadResult(tasks, warnings);
    }

    /**
      * Overwrites the data file with the current task list, one task per line.
      *
      * @param tasks the task list to write to disk.
     */
    public void save(final TaskList tasks) throws IOException {
        ensureParentDirectory();
        Path absoluteFilePath = filePath.toAbsolutePath();
        Path temporaryFile;
        try {
            temporaryFile = Files.createTempFile(
                    absoluteFilePath.getParent(), ".dingleberry-", ".tmp");
        } catch (IOException e) {
            throw storageException("create temporary storage file", e);
        }

        try (var writer = Files.newBufferedWriter(temporaryFile,
                StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(tasks.get(i).toSaveFormat());
                writer.newLine();
            }
        } catch (IOException | RuntimeException e) {
            deleteTemporaryFile(temporaryFile);
            if (e instanceof IOException ioException) {
                throw storageException("save tasks", ioException);
            }
            throw e;
        }

        try {
            moveTemporaryFile(temporaryFile, absoluteFilePath);
        } catch (IOException e) {
            deleteTemporaryFile(temporaryFile);
            throw storageException("replace storage file", e);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    private void ensureParentDirectory() throws IOException {
        Path parent = filePath.toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }
        if (Files.exists(parent) && !Files.isDirectory(parent)) {
            throw new IOException("Storage parent path is not a directory: "
                    + parent);
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw storageException("create storage directory " + parent, e);
        }
    }

    /**
     * Parses a stored record, validating common fields before task details.
     *
     * @param line the pipe-delimited record to parse.
     * @return the task with its stored completion status restored.
     * @throws IllegalArgumentException if any record field is invalid.
     */
    private Task parseLine(final String line) {
        List<String> parts = splitEscapedFields(line);
        String type = requireField(parts, FIELD_TYPE_INDEX, "task type");
        String completionFlag = requireField(parts, FIELD_COMPLETION_FLAG_INDEX,
                "completion flag");
        String description = requireField(parts, FIELD_DESCRIPTION_INDEX,
                "description");
        boolean isDone = parseCompletionFlag(completionFlag);
        Task task = createTask(type, description, parts);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the two completion flags supported by stored records.
     *
     * @param completionFlag the stored completion flag.
     * @return true for a completed task, or false for an incomplete task.
     * @throws IllegalArgumentException if the flag is neither zero nor one.
     */
    private static boolean parseCompletionFlag(final String completionFlag) {
        if ("0".equals(completionFlag)) {
            return false;
        }
        if ("1".equals(completionFlag)) {
            return true;
        }
        throw new IllegalArgumentException("invalid completion flag");
    }

    /**
     * Creates an incomplete task after validating its type-specific fields.
     *
     * @param type the stored task type marker.
     * @param description the validated task description.
     * @param parts the decoded record fields.
     * @return the reconstructed incomplete task.
     * @throws IllegalArgumentException if the type or task details are invalid.
     */
    private static Task createTask(final String type, final String description,
            final List<String> parts) {
        return switch (type) {
            case "T" -> {
                requireFieldCount(parts, FIELD_DESCRIPTION_INDEX + 1);
                yield new Todo(description);
            }
            case "D" -> {
                requireFieldCount(parts, FIELD_FIRST_DATE_TIME_INDEX + 1);
                yield new Deadlines(description, parseDateTime(
                        requireField(parts, FIELD_FIRST_DATE_TIME_INDEX,
                                "deadline")));
            }
            case "E" -> parseEvent(description, parts);
            default -> throw new IllegalArgumentException("unknown task type");
        };
    }

    /**
     * Parses an event record and validates its chronological order.
     *
     * @param description the validated event description.
     * @param parts the decoded record fields.
     * @return the reconstructed incomplete event.
     * @throws IllegalArgumentException if fields or the time range are invalid.
     */
    private static Events parseEvent(final String description,
            final List<String> parts) {
        requireFieldCount(parts, FIELD_SECOND_DATE_TIME_INDEX + 1);
        LocalDateTime from = parseDateTime(requireField(parts,
                FIELD_FIRST_DATE_TIME_INDEX, "event start"));
        LocalDateTime to = parseDateTime(requireField(parts,
                FIELD_SECOND_DATE_TIME_INDEX, "event end"));
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException(
                    "event end must be after event start");
        }
        return new Events(description, from, to);
    }

    private static String requireField(final List<String> fields,
                                       final int index,
                                       final String fieldName) {
        if (index >= fields.size()) {
            throw new IllegalArgumentException("missing " + fieldName);
        }
        String value = fields.get(index).trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("blank " + fieldName);
        }
        return value;
    }

    private static void requireFieldCount(final List<String> fields,
                                          final int expectedCount) {
        if (fields.size() != expectedCount) {
            throw new IllegalArgumentException("expected " + expectedCount
                    + " fields but found " + fields.size());
        }
    }

    private static LocalDateTime parseDateTime(final String value) {
        try {
            return LocalDateTime.parse(value, STORAGE_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("invalid date/time", e);
        }
    }

    /**
     * Splits a record at unescaped pipes and decodes escaped field contents.
     *
     * @param line the pipe-delimited record to split.
     * @return the decoded fields with surrounding whitespace removed.
     * @throws IllegalArgumentException if an escape sequence is invalid.
     */
    private static List<String> splitEscapedFields(final String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\\') {
                if (i + 1 >= line.length()) {
                    throw new IllegalArgumentException(
                            "incomplete escape sequence");
                }
                field.append(decodeEscapedCharacter(line.charAt(++i)));
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Decodes a character following the escape marker in a stored field.
     *
     * @param escaped the character following the backslash.
     * @return the literal character represented by the escape sequence.
     * @throws IllegalArgumentException if the escape is unsupported.
     */
    private static char decodeEscapedCharacter(final char escaped) {
        if (escaped == '|' || escaped == '\\') {
            return escaped;
        }
        if (escaped == NEWLINE_ESCAPE) {
            return '\n';
        }
        if (escaped == CARRIAGE_RETURN_ESCAPE) {
            return '\r';
        }
        throw new IllegalArgumentException(
                "unknown escape sequence \\" + escaped);
    }

    private void moveTemporaryFile(final Path temporaryFile,
                                   final Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, destination,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteTemporaryFile(final Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            // The original file is already safe if temporary cleanup fails.
        }
    }

    private IOException storageException(final String action,
                                         final IOException cause) {
        return new IOException("Could not " + action + " at " + filePath
                + ": " + cause.getMessage(), cause);
    }
}
