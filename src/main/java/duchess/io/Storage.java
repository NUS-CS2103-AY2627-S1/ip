package duchess.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import duchess.io.exceptions.StorageException;
import duchess.tasks.Task;
import duchess.tasks.collections.TaskList;
import duchess.tasks.exceptions.TaskException;
import duchess.tasks.exceptions.UnrecognizedCommandException;
import duchess.tasks.factories.TaskFactory;

/**
 * Handles loading tasks from and saving tasks to a text file.
 *
 * <p>Each task occupies one line in the storage file, using one of these formats:</p>
 * <pre>
 * &lt;isDone&gt; | &lt;description&gt; | T |
 * &lt;isDone&gt; | &lt;description&gt; | D | /by &lt;by&gt;
 * &lt;isDone&gt; | &lt;description&gt; | E | /from &lt;from&gt; /to &lt;to&gt;
 * &lt;isDone&gt; | &lt;description&gt; | R | /by &lt;by&gt; /repeat &lt;days&gt;
 * </pre>
 *
 * <p>{@code isDone} is {@code 0} for an incomplete task and {@code 1} for a completed task.</p>
 */
public class Storage {
    /* Class-level constants */
    private static final Map<String, String> STORED_CHAR_TO_TYPE = Map.of(
            "T", "todo",
            "D", "deadline",
            "E", "event",
            "R", "recurring"
    );

    /** Uses "\\|" to represent "|", as this string is for regex */
    private static final String SPLIT_CHARACTER = "\\|";

    private static final String COMPLETED_STATUS = "1";
    private static final String INCOMPLETE_STATUS = "0";

    private static final int EXPECTED_COMPONENT_COUNT = 4;
    private static final int STATUS_COMPONENT_INDEX = 0;
    private static final int DESCRIPTION_COMPONENT_INDEX = 1;
    private static final int TYPE_COMPONENT_INDEX = 2;
    private static final int ADDITIONAL_INFORMATION_COMPONENT_INDEX = 3;

    /* Instance-level variables */
    private final Path filePath;

    /**
     * Creates a storage handler that saves data in {@code filePath}.
     * If the file or directory doesn't exist, create it.
     *
     * @param filePath The location of the file to save to.
     * @throws IOException If the file doesn't exist and couldn't
     *                      be created.
     */
    public Storage(Path filePath) throws IOException {
        assert (filePath != null) : "Cannot create storage without a file";
        this.filePath = filePath;

        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    /**
     * Splits the stored task into individual components.
     * @param line The line representing a stored task.
     * @return An array representing the individual components
     * @throws StorageException If the line cannot be interpreted
     *                          as a task.
     */
    private static TaskComponents parseStoredComponents(String line) throws StorageException {
        assert (line != null) : "Stored task should not be null";

        // Set limit to -1 so that all instances of "|" are split.
        String[] items = line.split(SPLIT_CHARACTER, -1);
        for (int i = 0; i < items.length; i++) {
            assert (items[i] != null) : "Component of a task cannot be null";
            items[i] = items[i].trim();
        }

        /* Check line validity */
        boolean hasExpectedComponentCount = (items.length == EXPECTED_COMPONENT_COUNT);
        boolean hasRecognisedTaskType = hasExpectedComponentCount // short-circuiting
                && STORED_CHAR_TO_TYPE.containsKey(items[TYPE_COMPONENT_INDEX]);
        if (!hasExpectedComponentCount || !hasRecognisedTaskType) {
            throw new StorageException("Split failed. \n" + line);
        }

        return new TaskComponents(
                items[STATUS_COMPONENT_INDEX],
                items[DESCRIPTION_COMPONENT_INDEX],
                items[TYPE_COMPONENT_INDEX],
                items[ADDITIONAL_INFORMATION_COMPONENT_INDEX]);
    }

    /**
     * Creates a task from its components.
     * @param components The string array representing the split
     *                   components of the storage line.
     * @return A {@code Task}, as described by the components.
     * @throws TaskException If the components cannot be recognized as a {@code Task}.
     */
    private static Task createTaskFromComponents(TaskComponents components)
            throws TaskException {
        return TaskFactory.createFromCommand(String.format(
                "%s %s %s",
                STORED_CHAR_TO_TYPE.get(components.type()),
                components.description(),
                components.additionalInformation()
        ));
    }

    /**
     * Marks the {@code task} as complete if the {@code taskStatus} says so.
     */
    private static void markIfComplete(Task task, String taskStatus) throws StorageException {
        if (isTaskComplete(taskStatus)) {
            task.mark();
        }
    }

    /**
     * Determines if the {@code taskStatus} describes a completed task.
     * @throws StorageException If the {@code taskStatus} could not be recognized.
     */
    private static boolean isTaskComplete(String taskStatus) throws StorageException {
        return switch (taskStatus) {
            case INCOMPLETE_STATUS -> false;
            case COMPLETED_STATUS -> true;
            default -> throw new StorageException(
                    "IsDone has incorrect format: " + taskStatus);
        };
    }

    /**
     * Loads previously saved tasks.
     *
     * @return The saved tasks, excluding invalid stored records.
     */
    public ArrayList<Task> load() throws IOException {
        return deserializeLines(readStorageLines());
    }

    /** Reads all records currently stored in the data file. */
    private List<String> readStorageLines() throws IOException {
        return Files.readAllLines(this.filePath);
    }

    /** Reconstructs all valid tasks from the supplied storage records. */
    private ArrayList<Task> deserializeLines(List<String> lines) {
        // Add all tasks in storage to an arraylist.
        ArrayList<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            try {
                tasks.add(Storage.deserialize(line));
            } catch (TaskException t) {
                // do nothing, since we are expected to
                // skip unrecognised lines.
            }
        }

        return tasks;
    }

    /**
     * Reconstructs one task from a line in the data file.
     *
     * @param line One stored task record.
     * @return A new task with the same data as recorded.
     */
    private static Task deserialize(String line) throws TaskException {
        try {
            TaskComponents components = parseStoredComponents(line);
            Task task = createTaskFromComponents(components);
            markIfComplete(task, components.status());
            return task;
        } catch (StorageException e) {
            throw UnrecognizedCommandException.declare(line);
        }
    }


    /** Holds the named fields of one stored task record. */
    private record TaskComponents(
            String status,
            String description,
            String type,
            String additionalInformation) {
    }

    /**
     * Saves the supplied tasks, replacing the previous contents of the data file.
     *
     * @param tasks The current task list.
     */
    public void save(TaskList tasks) throws IOException {
        assert (tasks != null) : "Task list to save must not be null";
        Files.write(this.filePath, tasks.getStorageFormat());
    }
}
