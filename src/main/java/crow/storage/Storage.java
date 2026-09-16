package crow.storage;

import crow.exception.CrowException;
import crow.task.Deadline;
import crow.task.Event;
import crow.task.Task;
import crow.task.Todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to a local text file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager for the given file path.
     *
     * @param filePath Relative path of the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file.
     * Returns an empty list when the file does not exist yet.
     *
     * @return Tasks loaded from the data file.
     * @throws CrowException If the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws CrowException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return tasks;
        }

        try {
            for (String taskDataLine : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!taskDataLine.isBlank()) {
                    Task task = parseTask(taskDataLine);
                    boolean isDuplicate = tasks.stream()
                            .anyMatch(existingTask -> existingTask.hasSameDetailsAs(task));
                    if (isDuplicate) {
                        throw new CrowException("Error: Duplicate task in data file.");
                    }
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new CrowException("Error: Unable to read saved tasks.");
        }
    }

    /**
     * Saves all tasks, creating the data directory and file when necessary.
     *
     * @param tasks Tasks to save.
     * @throws CrowException If the tasks cannot be saved.
     */
    public void save(List<Task> tasks) throws CrowException {
        for (Task task : tasks) {
            validateTaskForStorage(task);
        }
        List<String> taskDataLines = tasks.stream()
                .map(this::formatTask)
                .toList();

        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, taskDataLines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            throw new CrowException("Error: Unable to save tasks.");
        }
    }

    /**
     * Converts one task into its file representation.
     */
    private String formatTask(Task task) {
        assert task instanceof Todo || task instanceof Deadline || task instanceof Event
                : "Storage supports only Todo, Deadline, and Event tasks";
        String completionStatus = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + completionStatus + " | " + task.getDescription()
                    + " | " + deadline.getDeadlineDateTime();
        }
        if (task instanceof Event event) {
            return "E | " + completionStatus + " | " + task.getDescription()
                    + " | " + event.getStartDateTime() + " | " + event.getEndDateTime();
        }
        return "T | " + completionStatus + " | " + task.getDescription();
    }

    /**
     * Converts one line from the data file into a task.
     *
     * @throws CrowException If the line has an unsupported format.
     */
    private Task parseTask(String taskDataLine) throws CrowException {
        String[] taskFields = taskDataLine.split(" \\| ", -1);
        try {
            validateFieldCount(taskFields);
            if (taskFields.length < 3 || taskFields[2].isBlank() || taskFields[2].contains("|")) {
                throw createInvalidTaskDataException();
            }

            Task task = switch (taskFields[0]) {
                case "T" -> new Todo(taskFields[2]);
                case "D" -> new Deadline(taskFields[2], LocalDateTime.parse(taskFields[3]));
                case "E" -> new Event(taskFields[2], LocalDateTime.parse(taskFields[3]),
                        LocalDateTime.parse(taskFields[4]));
                default -> throw new CrowException("Error: Invalid task type in data file.");
            };
            if (taskFields[1].equals("1")) {
                task.markAsDone();
            } else if (!taskFields[1].equals("0")) {
                throw new CrowException("Error: Invalid task status in data file.");
            }
            validateTaskForStorage(task);
            return task;
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            throw createInvalidTaskDataException();
        }
    }

    /**
     * Validates the number of fields used by each stored task type.
     */
    private void validateFieldCount(String[] taskFields) throws CrowException {
        int expectedFieldCount = switch (taskFields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new CrowException("Error: Invalid task type in data file.");
        };
        if (taskFields.length != expectedFieldCount) {
            throw createInvalidTaskDataException();
        }
    }

    /**
     * Ensures a task can be represented safely in the storage format.
     */
    private void validateTaskForStorage(Task task) throws CrowException {
        boolean isSupportedType = task instanceof Todo
                || task instanceof Deadline
                || task instanceof Event;
        if (!isSupportedType || task.getDescription().isBlank() || task.getDescription().contains("|")) {
            throw new CrowException("Error: Unable to save invalid task data.");
        }
        if (task instanceof Event event && !event.getStartDateTime().isBefore(event.getEndDateTime())) {
            throw new CrowException("Error: Event start must be before its end in data file.");
        }
    }

    private CrowException createInvalidTaskDataException() {
        return new CrowException("Error: Invalid task data in data file.");
    }
}
