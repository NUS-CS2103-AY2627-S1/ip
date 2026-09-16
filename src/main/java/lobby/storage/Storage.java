package lobby.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lobby.task.Deadline;
import lobby.task.Event;
import lobby.task.Task;
import lobby.task.Todo;

/**
 * Loads tasks from a file and saves the current task list back to that file.
 */
public class Storage {
    private static final int FIELD_TYPE = 0;
    private static final int FIELD_STATUS = 1;
    private static final int FIELD_DESCRIPTION = 2;
    private static final int FIELD_DEADLINE_DATE = 3;
    private static final int FIELD_EVENT_START = 3;
    private static final int FIELD_EVENT_END = 4;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;

    private final Path filePath;
    private final String displayPath;
    private boolean needsRecoveryBackup;

    /**
     * Contains the usable tasks and any warning discovered while loading them.
     *
     * @param tasks tasks reconstructed successfully.
     * @param skippedLines number of malformed non-blank lines ignored.
     * @param readFailed whether the save file could not be read at all.
     */
    public record LoadResult(List<Task> tasks, int skippedLines, boolean readFailed) {
    }

    /**
     * Creates storage that reads from and writes to the given path.
     *
     * @param filePath location of the task data file.
     */
    public Storage(String filePath) {
        this.displayPath = filePath;
        this.filePath = Path.of(filePath).toAbsolutePath();
    }

    public String getDisplayPath() {
        return displayPath;
    }

    public boolean needsRecoveryBackup() {
        return needsRecoveryBackup;
    }

    /**
     * Loads tasks from the save file, or returns an empty list when no save file exists yet.
     * Malformed records are skipped so that valid records can still be recovered.
     *
     * @return the loaded tasks and details of any loading problem
     */
    public LoadResult load() {
        needsRecoveryBackup = false;
        List<Task> tasks = new ArrayList<>();
        try {
            if (Files.notExists(filePath)) {
                return new LoadResult(tasks, 0, false);
            }

            int skippedLines = 0;
            for (String taskLine : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (taskLine.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(createTaskFromData(parseDataFields(taskLine)));
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    skippedLines++;
                }
            }
            needsRecoveryBackup = skippedLines > 0;
            return new LoadResult(tasks, skippedLines, false);
        } catch (IOException | SecurityException e) {
            needsRecoveryBackup = true;
            return new LoadResult(new ArrayList<>(), 0, true);
        }
    }

    /**
     * Rewrites the save file so that it matches the current task list.
     * The parent directory is created automatically on the first save.
     *
     * @param tasks the complete current task list.
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(filePath.getParent());
        preserveOriginal();
        List<String> taskLines = tasks.stream()
                .map(Task::toDataString)
                .toList();
        Path temporaryFile = Files.createTempFile(filePath.getParent(), "lobby-", ".tmp");
        try {
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, filePath,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Preserves the original bytes before replacing data that could not be fully loaded.
     * A unique backup avoids overwriting any earlier recovery copy.
     */
    private void preserveOriginal() throws IOException {
        if (!needsRecoveryBackup) {
            return;
        }
        if (!Files.isRegularFile(filePath)) {
            throw new IOException("The original save path is not a readable regular file.");
        }
        Path backup = Files.createTempFile(filePath.getParent(), filePath.getFileName() + ".recovery-", ".bak");
        try {
            Files.copy(filePath, backup, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException e) {
            Files.deleteIfExists(backup);
            throw e;
        }
        needsRecoveryBackup = false;
    }

    /**
     * Splits a stored line while unescaping literal separators and backslashes.
     * Unescaped backslashes from older save files remain valid.
     *
     * @param taskLine one line from the save file.
     * @return the unescaped fields
     */
    private String[] parseDataFields(String taskLine) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < taskLine.length(); i++) {
            char current = taskLine.charAt(i);
            if (current == '\\' && i + 1 < taskLine.length()) {
                char next = taskLine.charAt(i + 1);
                if (next == '\\' || next == '|') {
                    field.append(next);
                    i++;
                    continue;
                }
            }
            if (current == '|') {
                fields.add(field.toString().strip());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        fields.add(field.toString().strip());
        return fields.toArray(String[]::new);
    }

    /**
     * Reconstructs one task from the fields stored on a line of the save file.
     *
     * @param taskFields the task type, completion flag, and task-specific fields.
     * @return the reconstructed task
     */
    private Task createTaskFromData(String[] taskFields) {
        boolean isDone = parseCompletionStatus(taskFields);
        validateTaskFields(taskFields);

        String description = taskFields[FIELD_DESCRIPTION];
        Task task = switch (taskFields[FIELD_TYPE]) {
            case "T" -> new Todo(description);
            case "D" -> new Deadline(description, LocalDate.parse(taskFields[FIELD_DEADLINE_DATE]));
            case "E" -> new Event(description, taskFields[FIELD_EVENT_START], taskFields[FIELD_EVENT_END]);
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Reads a saved completion flag, rejecting missing or unsupported status values.
     */
    private boolean parseCompletionStatus(String[] taskFields) {
        if (taskFields.length <= FIELD_STATUS) {
            throw new IllegalArgumentException("Invalid task status");
        }
        return switch (taskFields[FIELD_STATUS]) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new IllegalArgumentException("Invalid task status");
        };
    }

    /**
     * Checks that a record has the required nonblank fields for its task type.
     * The completion flag must be validated before calling this method.
     */
    private void validateTaskFields(String[] taskFields) {
        int expectedFieldCount = switch (taskFields[FIELD_TYPE]) {
            case "T" -> FIELD_COUNT_TODO;
            case "D" -> FIELD_COUNT_DEADLINE;
            case "E" -> FIELD_COUNT_EVENT;
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (taskFields.length != expectedFieldCount) {
            throw new IllegalArgumentException("Incorrect number of task fields");
        }
        if (Arrays.stream(taskFields, FIELD_DESCRIPTION, taskFields.length).anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("Task fields cannot be blank");
        }
    }
}
