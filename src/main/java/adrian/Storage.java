package adrian;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves tasks to disk and reconstructs them when the application starts.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "adrian.txt");

    private final Path filePath;

    /**
     * Creates a storage service for Adrian's task data.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a storage service that uses the specified task data file.
     *
     * @param filePath location of the task data file.
     */
    Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes all tasks to Adrian's data file, replacing its existing contents.
     *
     * @param tasks tasks to save.
     * @throws IOException if the data directory or file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Files.createDirectories(filePath.getParent());

        List<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            lines.add(task.toDataString());
        }

        Files.write(filePath, lines);
    }

    /**
     * Loads all tasks from Adrian's data file.
     *
     * @return tasks reconstructed from storage, or an empty list if no data file exists.
     * @throws IOException if the data file cannot be read or contains malformed task data.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath);

        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(parseTask(lines.get(i)));
            } catch (DateTimeException | IllegalArgumentException | IndexOutOfBoundsException e) {
                throw new IOException("Invalid task data on line " + (i + 1) + ".", e);
            }
        }

        return tasks;
    }

    /**
     * Reconstructs a task from one line of stored task data.
     *
     * @param line serialized task data.
     * @return task represented by the stored data.
     * @throws IllegalArgumentException if the serialized task data is malformed or unsupported.
     */
    private static Task parseTask(String line) {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3) {
            throw new IllegalArgumentException("Task data has too few fields");
        }

        String taskTypeSymbol = parts[0];
        String completionStatus = parts[1];
        String description = parts[2];

        if (!completionStatus.equals("0") && !completionStatus.equals("1")) {
            throw new IllegalArgumentException("Task completion status must be 0 or 1");
        }

        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank");
        }

        boolean isDone = completionStatus.equals("1");
        Task task;

        if (taskTypeSymbol.equals("T")) {
            requirePartCount(parts, 3, taskTypeSymbol);
            task = new Todo(description);
        } else if (taskTypeSymbol.equals("D")) {
            requirePartCount(parts, 4, taskTypeSymbol);
            LocalDateTime dueDateTime = LocalDateTime.parse(parts[3]);
            task = new Deadline(description, dueDateTime);
        } else if (taskTypeSymbol.equals("E")) {
            requirePartCount(parts, 5, taskTypeSymbol);
            LocalDateTime startDateTime = LocalDateTime.parse(parts[3]);
            LocalDateTime endDateTime = LocalDateTime.parse(parts[4]);
            task = new Event(description, startDateTime, endDateTime);
        } else if (taskTypeSymbol.equals("F")) {
            requirePartCount(parts, 4, taskTypeSymbol);
            int durationInMinutes = Integer.parseInt(parts[3]);
            task = new FixedDurationTask(description, durationInMinutes);
        } else {
            throw new IllegalArgumentException("Unsupported task type: " + taskTypeSymbol);
        }

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Ensures serialized task data contains exactly the fields required by its type.
     *
     * @param parts serialized task fields.
     * @param expectedCount required number of fields.
     * @param taskTypeSymbol symbol identifying the task type.
     * @throws IllegalArgumentException if the number of fields does not match the task type.
     */
    private static void requirePartCount(String[] parts, int expectedCount, String taskTypeSymbol) {
        if (parts.length != expectedCount) {
            throw new IllegalArgumentException(
                    "Task type " + taskTypeSymbol + " requires " + expectedCount + " fields");
        }
    }
}
