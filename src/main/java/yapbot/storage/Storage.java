package yapbot.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import yapbot.exception.YapBotException;
import yapbot.task.Deadline;
import yapbot.task.Event;
import yapbot.task.Priority;
import yapbot.task.Task;
import yapbot.task.Todo;

/**
 * Handles saving tasks to, and loading tasks from, the hard disk.
 */
public class Storage {

    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String DATA_FILE_NAME = "yapBot.txt";
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";

    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";

    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";

    private static Path targetPathOverride = null;

    /**
     * Redirects {@link #save} and {@link #load} to the given path instead of
     * the real save file. Exists only so tests can point storage at a
     * temporary file instead of touching {@code data/yapBot.txt}.
     *
     * @param path the path to use in place of the real save file.
     */
    public static void setTargetPathForTesting(Path path) {
        targetPathOverride = path;
    }

    /**
     * Undoes {@link #setTargetPathForTesting}, restoring the real save-file path.
     */
    public static void resetTargetPathForTesting() {
        targetPathOverride = null;
    }

    /**
     * Builds the save-file path in an OS-independent way: the directory and
     * file name are supplied as separate arguments so the {@link Path} API
     * inserts the correct separator for whatever OS the program is run on,
     * rather than a hardcoded {@code "/"} or {@code "\\"} baked into a string.
     *
     * @return the (relative) path to the save file.
     */
    private static Path getTargetPath() {
        if (targetPathOverride != null) {
            return targetPathOverride;
        }
        return Paths.get(DATA_DIRECTORY_NAME, DATA_FILE_NAME);
    }

    /**
     * Writes the given tasks to the save file, creating the parent directory
     * if it does not already exist. The write goes through a temporary file
     * that is atomically moved into place afterwards, so a crash or failure
     * part-way through never leaves a truncated or corrupted save file behind.
     * Any I/O failure is reported but does not crash the program.
     *
     * @param tasks     the current task array.
     * @param taskCount the number of active tasks in the array.
     */
    public static void save(Task[] tasks, int taskCount) {
        Path targetPath = getTargetPath();
        Path parentDir = targetPath.toAbsolutePath().getParent();
        Path tempPath = null;

        try {
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            if (Files.isDirectory(targetPath)) {
                System.out.println("Warning: could not save tasks, "
                        + targetPath + " is a directory, not a file.");
                return;
            }

            tempPath = parentDir != null
                    ? Files.createTempFile(parentDir, "yapBot", ".tmp")
                    : Files.createTempFile("yapBot", ".tmp");
            Files.writeString(tempPath, buildFileContent(tasks, taskCount));
            moveIntoPlace(tempPath, targetPath);
        } catch (IOException e) {
            System.out.println("Warning: could not save tasks to disk: " + e.getMessage());
            deleteQuietly(tempPath);
        }
    }

    /**
     * Builds the full save-file content for the given tasks.
     *
     * @param tasks     the current task array.
     * @param taskCount the number of active tasks in the array.
     * @return the file content, one task per line.
     */
    private static String buildFileContent(Task[] tasks, int taskCount) {
        return Arrays.stream(tasks, 0, taskCount)
                .map(task -> task.toFileFormat() + System.lineSeparator())
                .collect(Collectors.joining());
    }

    /**
     * Moves the temp file into place as the save file, preferring an atomic
     * move but falling back to a non-atomic replace if the filesystem does
     * not support atomic moves (e.g. some network drives).
     *
     * @param tempPath   the temp file containing the new content.
     * @param targetPath the final save-file location.
     * @throws IOException if the move fails outright.
     */
    private static void moveIntoPlace(Path tempPath, Path targetPath) throws IOException {
        try {
            Files.move(tempPath, targetPath,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Deletes the given path if it exists, ignoring any failure. Used for
     * best-effort cleanup of a leftover temp file after a save error.
     *
     * @param path the path to delete, may be {@code null}.
     */
    private static void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // Best-effort cleanup; nothing more we can do if this fails too.
        }
    }

    /**
     * Loads tasks from the save file into the given array, starting at index 0.
     * The following situations are all handled without crashing the program:
     * the save file does not exist yet (0 tasks are loaded); the file exists
     * but is empty or contains only blank lines; a line has the wrong number
     * of fields, an unrecognized task type, a status other than "0"/"1", or a
     * blank description/date field (that line is skipped with a warning); the
     * file contains more tasks than {@code tasks} can hold (the extra lines
     * are skipped with a warning); or the file cannot be read at all, e.g. due
     * to permissions (0 tasks are loaded and a warning is printed).
     *
     * @param tasks the array to populate with loaded tasks.
     * @return the number of tasks successfully loaded.
     */
    public static int load(Task[] tasks) {
        Path path = getTargetPath();
        if (!Files.exists(path)) {
            return 0;
        }
        if (Files.isDirectory(path)) {
            System.out.println("Warning: could not load tasks, "
                    + path + " is a directory, not a file.");
            return 0;
        }

        int taskCount = 0;
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                if (taskCount >= tasks.length) {
                    System.out.println("Warning: save file has more tasks than can be "
                            + "loaded (max " + tasks.length + "); ignoring the rest.");
                    break;
                }
                taskCount = tryLoadLine(tasks, taskCount, line);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not load tasks from disk: " + e.getMessage());
            return 0;
        }
        return taskCount;
    }

    /**
     * Attempts to parse and store one save-file line at the given index.
     * On failure, the line is skipped with a warning and the count is
     * returned unchanged.
     *
     * @param tasks     the array being populated.
     * @param taskCount the number of tasks loaded so far.
     * @param line      the save-file line to parse.
     * @return the updated task count.
     */
    private static int tryLoadLine(Task[] tasks, int taskCount, String line) {
        try {
            tasks[taskCount] = parseTask(line);
            return taskCount + 1;
        } catch (YapBotException e) {
            System.out.println("Warning: skipping corrupted save-file line ("
                    + e.getMessage() + "): " + line);
            return taskCount;
        }
    }

    /**
     * Parses a single save-file line into the matching Task subclass, applying
     * strict validation: the field count must exactly match the task type, the
     * status must be "0" or "1", and no required field may be blank.
     *
     * @param line one line from the save file, e.g. {@code "T | 1 | read book"}.
     * @return the parsed task, with its done status applied.
     * @throws YapBotException if the line is not in a recognized, well-formed format.
     */
    private static Task parseTask(String line) throws YapBotException {
        String[] parts = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (parts.length < 3) {
            throw new YapBotException("too few fields");
        }

        String type = parts[0].trim();
        String statusValue = parts[1].trim();
        String description = parts[2].trim();

        if (description.isEmpty()) {
            throw new YapBotException("description is empty");
        }
        if (!statusValue.equals(STATUS_DONE) && !statusValue.equals(STATUS_NOT_DONE)) {
            throw new YapBotException("status must be '0' or '1', was '" + statusValue + "'");
        }

        Task task = buildTask(type, description, parts);
        if (statusValue.equals(STATUS_DONE)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Builds the task-specific object (Todo/Deadline/Event) for a parsed line,
     * dispatching to the builder for that type.
     *
     * @param type        the single-letter task type ("T", "D", or "E").
     * @param description the (already-validated, non-blank) task description.
     * @param parts       the full set of pipe-separated fields from the line.
     * @return the constructed task, not yet marked as done.
     * @throws YapBotException if the type is unrecognized or a field is invalid.
     */
    private static Task buildTask(String type, String description, String[] parts) throws YapBotException {
        switch (type) {
            case TYPE_TODO:
                return buildTodo(description, parts);
            case TYPE_DEADLINE:
                return buildDeadline(description, parts);
            case TYPE_EVENT:
                return buildEvent(description, parts);
            default:
                throw new YapBotException("unknown task type '" + type + "'");
        }
    }

    /**
     * Builds a {@link Todo} from a parsed line's fields, validating the
     * field count and, if present, the trailing priority field.
     *
     * @param description the already-validated, non-blank task description.
     * @param parts       the full set of pipe-separated fields from the line.
     * @return the constructed task.
     * @throws YapBotException if the field count is wrong or the priority is invalid.
     */
    private static Task buildTodo(String description, String[] parts) throws YapBotException {
        final int baseFieldCount = 3;
        if (parts.length != baseFieldCount && parts.length != baseFieldCount + 1) {
            throw new YapBotException("todo line must have " + baseFieldCount + " or " + (baseFieldCount + 1)
                    + " fields");
        }
        Task task = new Todo(description);
        applyOptionalPriority(task, parts, baseFieldCount);
        return task;
    }

    /**
     * Builds a {@link Deadline} from a parsed line's fields, validating the
     * field count, that the 'by' field is a non-blank, valid date, and, if
     * present, the trailing priority field.
     *
     * @param description the already-validated, non-blank task description.
     * @param parts       the full set of pipe-separated fields from the line.
     * @return the constructed task.
     * @throws YapBotException if the field count is wrong, 'by' is invalid,
     *         or the priority is invalid.
     */
    private static Task buildDeadline(String description, String[] parts) throws YapBotException {
        final int baseFieldCount = 4;
        if (parts.length != baseFieldCount && parts.length != baseFieldCount + 1) {
            throw new YapBotException("deadline line must have " + baseFieldCount
                    + " or " + (baseFieldCount + 1) + " fields");
        }
        String byRaw = parts[3].trim();
        if (byRaw.isEmpty()) {
            throw new YapBotException("deadline 'by' field is empty");
        }
        Task task;
        try {
            task = new Deadline(description, LocalDate.parse(byRaw));
        } catch (DateTimeParseException e) {
            throw new YapBotException("deadline 'by' field is not a valid date: '" + byRaw + "'");
        }
        applyOptionalPriority(task, parts, baseFieldCount);
        return task;
    }

    /**
     * Builds an {@link Event} from a parsed line's fields, validating the
     * field count, that the 'from'/'to' fields are non-blank, and, if
     * present, the trailing priority field.
     *
     * @param description the already-validated, non-blank task description.
     * @param parts       the full set of pipe-separated fields from the line.
     * @return the constructed task.
     * @throws YapBotException if the field count is wrong, a field is blank,
     *         or the priority is invalid.
     */
    private static Task buildEvent(String description, String[] parts) throws YapBotException {
        final int baseFieldCount = 5;
        if (parts.length != baseFieldCount && parts.length != baseFieldCount + 1) {
            throw new YapBotException("event line must have " + baseFieldCount + " or " + (baseFieldCount + 1)
                    + " fields");
        }
        String from = parts[3].trim();
        String to = parts[4].trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new YapBotException("event 'from'/'to' field is empty");
        }
        Task task = new Event(description, from, to);
        applyOptionalPriority(task, parts, baseFieldCount);
        return task;
    }

    /**
     * Applies the optional trailing priority field to an already-built task,
     * if the line carried one (i.e. exactly one field beyond the type's base
     * count). Leaves the task's priority unset otherwise.
     *
     * @param task           the already-built task to apply the priority to.
     * @param parts          the full set of pipe-separated fields from the line.
     * @param baseFieldCount the number of fields this task type has without a priority.
     * @throws YapBotException if the trailing field is not a valid priority.
     */
    private static void applyOptionalPriority(Task task, String[] parts, int baseFieldCount)
            throws YapBotException {
        if (parts.length == baseFieldCount + 1) {
            task.setPriority(Priority.fromInput(parts[baseFieldCount].trim()));
        }
    }
}