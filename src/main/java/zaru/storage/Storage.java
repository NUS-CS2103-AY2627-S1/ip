package zaru.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import zaru.exception.ZaruException;
import zaru.parser.DateTimeParser;
import zaru.task.Deadline;
import zaru.task.Event;
import zaru.task.Task;
import zaru.task.ToDo;

/**
 * Handles loading tasks from and saving tasks to the hard disk.
 */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String COMPLETED_MARKER = "1";
    private static final String INCOMPLETE_MARKER = "0";
    private static final String FIELD_SEPARATOR_REGEX = "\\s*\\|\\s*";
    private static final int TODO_PART_COUNT = 3;
    private static final int DEADLINE_PART_COUNT = 4;
    private static final int EVENT_PART_COUNT = 5;

    private final Path filePath;

    /**
     * Creates a storage helper that reads from and writes to the given file path.
     *
     * @param filePath Location of the save file.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage requires a save-file path.";

        this.filePath = filePath;
    }

    /**
     * Loads saved tasks from the hard disk.
     *
     * @return Task list restored from the save file, or an empty list if the file does not exist.
     * @throws ZaruException If the file cannot be read or contains invalid task data.
     */
    public List<Task> load() throws ZaruException {
        List<Task> tasks = new ArrayList<>();
        List<String> lines;

        try {
            if (!Files.exists(filePath)) {
                return tasks;
            }
            if (!Files.isRegularFile(filePath)) {
                throw new ZaruException("The save-file path is not a regular file: %s".formatted(filePath));
            }

            lines = Files.readAllLines(filePath);
        } catch (IOException | SecurityException e) {
            throw new ZaruException("Unable to read the save file: %s".formatted(filePath));
        }

        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex);
            if (line.isBlank()) {
                continue;
            }

            try {
                Task task = convertLineToTask(line);
                if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                    throw new ZaruException("Duplicate task.");
                }
                tasks.add(task);
            } catch (ZaruException e) {
                throw new ZaruException("Invalid data on line %d of the save file: %s"
                        .formatted(lineIndex + 1, e.getMessage()));
            }
        }

        return tasks;
    }

    /**
     * Saves all tasks to the hard disk.
     *
     * @param tasks Task list to save.
     * @throws ZaruException If the save file cannot be written.
     */
    public void save(List<Task> tasks) throws ZaruException {
        assert tasks != null : "Storage should receive an initialized task list.";

        StringBuilder contents = new StringBuilder();

        for (Task task : tasks) {
            assert task != null : "Task lists should not contain null entries.";
            contents.append(taskToFileString(task)).append(System.lineSeparator());
        }

        Path absolutePath = filePath.toAbsolutePath();
        Path parentDirectory = absolutePath.getParent();
        Path temporaryFile = null;

        try {
            if (parentDirectory == null || absolutePath.getFileName() == null) {
                throw new IOException("Save-file path has no parent directory or file name.");
            }

            Files.createDirectories(parentDirectory);
            temporaryFile = Files.createTempFile(parentDirectory, "zaru-save-", ".tmp");
            Files.writeString(temporaryFile, contents.toString());
            replaceSaveFile(temporaryFile, absolutePath);
        } catch (IOException | SecurityException e) {
            throw new ZaruException("Unable to write the save file: %s".formatted(filePath));
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Replaces the save file atomically where the file system supports it.
     *
     * @param temporaryFile Complete temporary save file.
     * @param destination Final save-file path.
     * @throws IOException If neither an atomic nor a regular replacement succeeds.
     */
    private void replaceSaveFile(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Deletes a leftover temporary save file without masking the original save result. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException e) {
            // A later save can safely ignore an orphaned temporary file.
        }
    }

    /**
     * Converts a task object into one line of save-file text.
     *
     * @param task Task to convert.
     * @return Save-file representation of the task.
     * @throws ZaruException If the task type is not supported by the save format.
     */
    private String taskToFileString(Task task) throws ZaruException {
        validateTitle(task.getDescription());
        String completionMarker = task.isCompleted() ? COMPLETED_MARKER : INCOMPLETE_MARKER;

        return switch (task) {
            case ToDo toDo -> "%s | %s | %s".formatted(
                    TODO_TYPE, completionMarker, toDo.getDescription());
            case Deadline deadline -> "%s | %s | %s | %s".formatted(
                    DEADLINE_TYPE,
                    completionMarker,
                    deadline.getDescription(),
                    DateTimeParser.formatForStorage(deadline.getDueDate()));
            case Event event -> "%s | %s | %s | %s | %s".formatted(
                    EVENT_TYPE,
                    completionMarker,
                    event.getDescription(),
                    DateTimeParser.formatForStorage(event.getFrom()),
                    DateTimeParser.formatForStorage(event.getTo()));
            default -> throw new ZaruException("Unknown task type!");
        };
    }

    /**
     * Converts one line of save-file text into a task object.
     *
     * @param line One line from the save file.
     * @return Task represented by the line.
     * @throws ZaruException If the line does not match the expected save format.
     */
    private Task convertLineToTask(String line) throws ZaruException {
        String[] parts = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (parts.length < TODO_PART_COUNT) {
            throw new ZaruException("Invalid task data in save file!");
        }

        String taskType = parts[0];
        boolean isCompleted = parseCompleted(parts[1]);
        String title = parts[2];
        validateTitle(title);

        return switch (taskType) {
            case TODO_TYPE -> {
                validatePartCount(parts, TODO_PART_COUNT);
                yield new ToDo(title, isCompleted);
            }
            case DEADLINE_TYPE -> {
                validatePartCount(parts, DEADLINE_PART_COUNT);
                yield new Deadline(title, isCompleted, parts[3]);
            }
            case EVENT_TYPE -> {
                validatePartCount(parts, EVENT_PART_COUNT);
                yield new Event(title, isCompleted, parts[3], parts[4]);
            }
            default -> throw new ZaruException("Unknown task type!");
        };
    }

    /**
     * Converts a saved done marker into a boolean value.
     *
     * @param text Saved done marker, either {@code 1} or {@code 0}.
     * @return True if the task was done.
     * @throws ZaruException If the marker is not valid.
     */
    private boolean parseCompleted(String text) throws ZaruException {
        return switch (text) {
            case COMPLETED_MARKER -> true;
            case INCOMPLETE_MARKER -> false;
            default -> throw new ZaruException("Invalid task status in save file!");
        };
    }

    /**
     * Checks that a saved task has the expected number of fields.
     *
     * @param parts Fields parsed from one save-file line.
     * @param expected Expected number of fields.
     * @throws ZaruException If the field count is wrong.
     */
    private void validatePartCount(String[] parts, int expected) throws ZaruException {
        if (parts.length != expected) {
            throw new ZaruException("Invalid task data in save file!");
        }
    }

    /**
     * Checks whether a task title is safe for the line-based save format.
     *
     * @param title Task title to validate.
     * @throws ZaruException If the title is empty or contains a reserved separator.
     */
    private void validateTitle(String title) throws ZaruException {
        if (title == null || title.isBlank()) {
            throw new ZaruException("Task description cannot be empty.");
        }
        if (title.contains("|")) {
            throw new ZaruException("Task description contains the reserved | character.");
        }
    }
}
