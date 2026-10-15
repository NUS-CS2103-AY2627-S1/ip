package kiaw.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import kiaw.task.Deadline;
import kiaw.task.Event;
import kiaw.task.Task;
import kiaw.task.Todo;

/**
 * Handles loading tasks from and saving tasks to persistent storage.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager for the specified folder and file.
     *
     * @param folderName folder containing the data file
     * @param fileName   name of the data file
     */
    public Storage(String folderName, String fileName) {
        this.filePath = Path.of(folderName, fileName);
    }

    /**
     * Saves all tasks to the configured data file.
     * <p>
     * Creates the parent directory when it does not already exist.
     *
     * @param tasks tasks to save
     * @throws IOException if the tasks cannot be written to the file
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            lines.add(taskToString(task));
        }

        Files.write(filePath, lines);
    }

    /**
     * Loads tasks from the configured data file.
     * <p>
     * If the directory or file does not exist, it is created and an empty
     * task list is returned.
     *
     * @return tasks loaded from storage
     * @throws IOException if the data file cannot be read or created
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath);

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            Task task = stringToTask(line);

            if (task != null) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Converts a task into its persistent text representation.
     *
     * @param task task to convert
     * @return text representation suitable for saving
     */
    private String taskToString(Task task) {
        String status = task.getStatusIcon().equals("X") ? "1" : "0";

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;

            return "D | "
                    + status
                    + " | "
                    + deadline.getDescription()
                    + " | "
                    + deadline.getBy();
        }

        if (task instanceof Event) {
            Event event = (Event) task;

            return "E | "
                    + status
                    + " | "
                    + event.getDescription()
                    + " | "
                    + event.getFrom()
                    + " | "
                    + event.getTo();
        }

        return "T | "
                + status
                + " | "
                + task.getDescription();
    }

    /**
     * Converts a stored text representation back into a task.
     * <p>
     * Invalid or corrupted records are ignored so that other valid tasks
     * can still be loaded.
     *
     * @param line line read from the data file
     * @return reconstructed task, or null if the line is invalid
     */
    private Task stringToTask(String line) {
        String[] parts = line.split(" \\| ");

        if (!hasValidCommonFields(parts)) {
            return null;
        }

        try {
            Task task = createTask(parts);
            restoreCompletionStatus(task, parts[1]);
            return task;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Checks whether the common fields of a stored task are valid.
     *
     * @param parts fields of the stored task
     * @return true if the common fields are valid
     */
    private boolean hasValidCommonFields(String[] parts) {
        if (parts.length < 3) {
            return false;
        }

        String status = parts[1];
        String description = parts[2];

        boolean hasValidStatus =
                status.equals("0") || status.equals("1");

        return hasValidStatus && !description.isBlank();
    }

    /**
     * Creates a task from its stored fields.
     *
     * @param parts fields of the stored task
     * @return reconstructed task, or null if the fields are invalid
     */
    private Task createTask(String[] parts) {
        switch (parts[0]) {
            case "T":
                return createTodo(parts);
            case "D":
                return createDeadline(parts);
            case "E":
                return createEvent(parts);
            default:
                return null;
        }
    }

    /**
     * Creates a todo from its stored fields.
     *
     * @param parts fields of the stored task
     * @return reconstructed todo, or null if the fields are invalid
     */
    private Task createTodo(String[] parts) {
        if (parts.length != 3) {
            return null;
        }

        return new Todo(parts[2]);
    }

    /**
     * Creates a deadline from its stored fields.
     *
     * @param parts fields of the stored task
     * @return reconstructed deadline, or null if the fields are invalid
     */
    private Task createDeadline(String[] parts) {
        if (parts.length != 4) {
            return null;
        }

        return new Deadline(
                parts[2],
                LocalDate.parse(parts[3])
        );
    }

    /**
     * Creates an event from its stored fields.
     *
     * @param parts fields of the stored task
     * @return reconstructed event, or null if the fields are invalid
     */
    private Task createEvent(String[] parts) {
        if (parts.length != 5) {
            return null;
        }

        LocalDate from = LocalDate.parse(parts[3]);
        LocalDate to = LocalDate.parse(parts[4]);

        if (to.isBefore(from)) {
            return null;
        }

        return new Event(
                parts[2],
                from,
                to
        );
    }

    /**
     * Restores the completion status of a reconstructed task.
     *
     * @param task reconstructed task
     * @param status stored completion status
     */
    private void restoreCompletionStatus(Task task, String status) {
        if (task != null && status.equals("1")) {
            task.markAsDone();
        }
    }
}
