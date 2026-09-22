package yappa.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import yappa.exception.YappaException;
import yappa.task.Deadline;
import yappa.task.Event;
import yappa.task.Task;
import yappa.task.TaskList;
import yappa.task.TaskType;
import yappa.task.Todo;
import yappa.util.DateUtil;

/**
 * Loads and saves Yappa tasks in a line-based text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private final Path filePath;

    /**
     * Creates a storage instance bound to the given file path.
     *
     * @param filePath Absolute or relative path to the storage file.
     *                 Must not be {@code null}.
     */
    public Storage(Path filePath) {
        this.filePath = Objects.requireNonNull(
                filePath, "Storage file path cannot be null");
    }

    /**
     * Loads all valid task records from the storage file.
     *
     * <p>
     * If the file does not exist, an empty task list is returned. Invalid records
     * are reported and skipped so that remaining tasks can still be loaded.
     * </p>
     *
     * @return Loaded tasks and warnings produced for invalid records.
     * @throws YappaException If an existing storage file cannot be opened.
     */
    public LoadResult loadTasks() throws YappaException {
        List<Task> tasks = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        File file = filePath.toFile();

        if (!file.exists()) {
            return new LoadResult(new TaskList(tasks), warnings);
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String taskString = scanner.nextLine().trim();

                if (taskString.isEmpty()) {
                    continue;
                }

                try {
                    tasks.add(parseTask(taskString));
                } catch (YappaException e) {
                    warnings.add(
                            "Task cannot be loaded: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            throw new YappaException(
                    "Storage file could not be opened: " + e.getMessage(), e);
        }

        return new LoadResult(new TaskList(tasks), warnings);
    }

    /**
     * Converts one storage record into its corresponding task subtype.
     *
     * @param taskString Storage record to parse.
     * @return Parsed task.
     * @throws YappaException If the storage record is malformed or contains
     *                        invalid task data.
     */
    private static Task parseTask(String taskString)
            throws YappaException {

        String[] taskParts = taskString.split(FIELD_SEPARATOR_REGEX, -1);

        validateTaskParts(taskParts);

        boolean isDone = parseCompletionStatus(taskParts[1]);
        String description = taskParts[2];

        switch (TaskType.fromStorageCode(taskParts[0])) {
            case TODO:
                return new Todo(description, isDone);

            case DEADLINE:
                LocalDateTime by = DateUtil.parseStorageDateTime(taskParts[3]);
                return new Deadline(description, isDone, by);

            case EVENT:
                LocalDateTime from = DateUtil.parseStorageDateTime(taskParts[3]);
                LocalDateTime to = DateUtil.parseStorageDateTime(taskParts[4]);
                return new Event(description, isDone, from, to);
            default:
                throw new YappaException("Unknown task type: " + taskParts[0]);
        }

    }

    /**
     * Validates the number of fields in a storage record before field access.
     *
     * @param taskParts Fields extracted from a storage record.
     * @throws YappaException If the record has an invalid structure.
     */
    private static void validateTaskParts(String[] taskParts) throws YappaException {
        if (taskParts.length < 3) {
            throw new YappaException("Storage record has too few fields.");
        }

        int expectedFieldCount = switch (taskParts[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new YappaException("Unknown task type: " + taskParts[0]);
        };

        if (taskParts.length != expectedFieldCount) {
            throw new YappaException("Storage record has an invalid number of fields.");
        }
    }

    /**
     * Converts the persisted completion marker into a boolean value.
     *
     * @param completionStatus Persisted completion marker.
     * @return True when the task is complete.
     * @throws YappaException If the marker is neither {@code 0} nor {@code 1}.
     */
    private static boolean parseCompletionStatus(String completionStatus) throws YappaException {
        if (!completionStatus.equals("0") && !completionStatus.equals("1")) {
            throw new YappaException("Storage record has an invalid completion status.");
        }
        return completionStatus.equals("1");
    }

    /**
     * Saves all tasks in the task list to the storage file.
     *
     * @param tasks Tasks to save in iteration order.
     * @throws YappaException If the storage directory or file cannot be written.
     */
    public void saveTasks(TaskList tasks) throws YappaException {
        File file = filePath.toFile();

        File parent = file.getParentFile();
        if (parent != null
                && !parent.exists()
                && !parent.mkdirs()) {
            throw new YappaException(
                    "Failed to create storage directory.");
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            for (Task task : tasks) {
                fileWriter.write(task.toFileString()
                        + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new YappaException("Failed to save tasks to file: " + e.getMessage(), e);
        }
    }

}
