package meow;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Handles loading tasks from and saving tasks to a data file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage object using the default task data file.
     */
    public Storage() {
        this.filePath = Paths.get("data", "meow.txt");
    }

    /**
     * Creates a storage object using the specified file path.
     *
     * @param filePath the path of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves all tasks in the task list to the data file.
     *
     * @param tasks the task list to save
     * @throws IOException if an error occurs while writing to the file
     */
    public void saveTasks(TaskList tasks) throws IOException {
        Path parent = filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(tasks.getTask(i).toFileString());
                writer.write(System.lineSeparator());
            }
        }
    }

    /**
     * Loads tasks from the data file.
     *
     * @return the task list loaded from the file, or an empty task list if the file does not exist
     * @throws IOException if an error occurs while reading the file
     */
    public TaskList loadTasks() throws IOException {
        TaskList taskList = new TaskList();
        if (!Files.exists(filePath)) {
            return taskList;
        }
        try (Scanner scanner = new Scanner(filePath.toFile())) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|", -1);

                Task task = parseTask(parts);
                taskList.add(task);
            }
        }
        return taskList;
    }

    /**
     * Creates a task from its stored file components.
     *
     * @param parts the components of a stored task
     * @return the reconstructed task
     * @throws IOException if the stored task data is invalid
     */
    private static Task parseTask(String[] parts) throws IOException {
        validateBasicTaskData(parts);

        String type = parts[0].trim();
        String status = parts[1].trim();
        String description = parts[2].trim();

        Task task = createTask(type, description, parts);
        restoreTaskStatus(task, status);

        return task;
    }

    /**
     * Validates the common fields shared by all stored tasks.
     *
     * @param parts the stored task components
     * @throws IOException if the common task data is invalid
     */
    private static void validateBasicTaskData(String[] parts) throws IOException {
        if (parts.length < 3) {
            throw new IOException("Invalid task data found in storage.");
        }

        String status = parts[1].trim();
        String description = parts[2].trim();

        validateStatus(status);

        if (description.isBlank()) {
            throw new IOException("Task description cannot be empty.");
        }
    }

    /**
     * Creates the appropriate task type from stored task components.
     *
     * @param type the stored task type
     * @param description the task description
     * @param parts the stored task components
     * @return the reconstructed task
     * @throws IOException if the stored task data is invalid
     */
    private static Task createTask(String type, String description, String[] parts)
            throws IOException {

        switch (type) {
            case Todo.STORAGE_TYPE -> {
                validateFieldCount(parts, 3);
                return new Todo(description);
            }
            case Deadline.STORAGE_TYPE -> {
                validateFieldCount(parts, 4);

                LocalDate dueDate = parseStoredDate(parts[3]);
                return new Deadline(description, dueDate);
            }
            case Event.STORAGE_TYPE -> {
                validateFieldCount(parts, 5);

                LocalDate fromDate = parseStoredDate(parts[3]);
                LocalDate toDate = parseStoredDate(parts[4]);

                validateEventDates(fromDate, toDate);

                return new Event(description, fromDate, toDate);
            }
            default -> throw new IOException("Unknown task type found in storage.");
        }
    }

    /**
     * Parses a date stored in the task data file.
     *
     * @param value the stored date value
     * @return the parsed date
     * @throws IOException if the stored date is invalid
     */
    private static LocalDate parseStoredDate(String value) throws IOException {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IOException("Invalid date found in storage.", e);
        }
    }

    /**
     * Validates the chronological order of an event's dates.
     *
     * @param fromDate the event start date
     * @param toDate the event end date
     * @throws IOException if the event ends before it starts
     */
    private static void validateEventDates(LocalDate fromDate, LocalDate toDate)
            throws IOException {

        if (toDate.isBefore(fromDate)) {
            throw new IOException(
                    "Event end date cannot be before its start date.");
        }
    }

    /**
     * Restores the saved completion status of a task.
     *
     * @param task the reconstructed task
     * @param status the stored completion status
     */
    private static void restoreTaskStatus(Task task, String status) {
        if (status.equals("1")) {
            task.markAsDone();
        }
    }

    /**
     * Checks that a stored task has the expected number of fields.
     *
     * @param parts the stored task components
     * @param expectedCount the expected number of components
     * @throws IOException if the field count is incorrect
     */
    private static void validateFieldCount(String[] parts, int expectedCount)
            throws IOException {
        if (parts.length != expectedCount) {
            throw new IOException(
                    "Unexpected number of fields in stored task data.");
        }
    }

    /**
     * Checks that a stored task status is valid.
     *
     * @param status the stored completion status
     * @throws IOException if the status is not 0 or 1
     */
    private static void validateStatus(String status) throws IOException {
        if (!status.equals("0") && !status.equals("1")) {
            throw new IOException("Invalid task status found in storage.");
        }
    }
}
