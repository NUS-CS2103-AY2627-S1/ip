package della.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import della.task.Deadline;
import della.task.Event;
import della.task.Task;
import della.task.Todo;
import della.util.DateParser;

/**
 * Stores tasks in and retrieves tasks from a file.
 */
public class Storage {
    /** Storage identifier used for todo tasks. */
    private static final String TODO_TYPE = "T";
    /** Storage identifier used for deadline tasks. */
    private static final String DEADLINE_TYPE = "D";
    /** Storage identifier used for event tasks. */
    private static final String EVENT_TYPE = "E";
    /** Storage value used to indicate that a task is completed. */
    private static final String COMPLETED_STATUS = "1";
    /** Date-time format used when serialising deadline and event data. */
    private static final String STORAGE_DATE_TIME_FORMAT = "MMM dd yyyy h:mma";

    /** Path of the file used to persist tasks. */
    private final String filePath;

    /**
     * Creates storage that uses the specified file path.
     *
     * @param filePath the path of the file used to store tasks.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Adds a task to the end of the storage file.
     *
     * @param task the task to store.
     * @throws IOException if the storage file cannot be written.
     */
    public void storeTask(Task task) throws IOException {
        try (FileWriter fileWriter = new FileWriter(this.filePath, true)) {
            fileWriter.write(task.formatForStorage());
            fileWriter.write("\n");
        }
    }

    /**
     * Returns whether the storage file exists and contains task data.
     *
     * @return {@code true} if the storage file contains stored tasks; {@code false} otherwise.
     */
    public boolean hasData() {
        File file = new File(this.filePath);
        return file.exists() && file.length() > 0;
    }

    /**
     * Returns tasks reconstructed from the storage file.
     *
     * @return the tasks read from the storage file in their stored order.
     * @throws FileNotFoundException if the storage file does not exist.
     * @throws java.time.format.DateTimeParseException if a stored deadline or event date has an invalid format.
     */
    public ArrayList<Task> loadTasks() throws FileNotFoundException {
        File storageFile = new File(this.filePath);
        ArrayList<Task> tasks = new ArrayList<>();

        try (Scanner scanner = new Scanner(storageFile)) {
            while (scanner.hasNext()) {
                tasks.add(parseTask(scanner.nextLine()));
            }
        }

        return tasks;
    }

    /**
     * Returns a task reconstructed from one line of storage data.
     *
     * @param taskLine the storage line representing a task.
     * @return the reconstructed task.
     */
    private Task parseTask(String taskLine) {
        String[] taskParts = taskLine.split("\\|");
        boolean isDone = taskParts[1].equals(COMPLETED_STATUS);

        if (taskParts[0].equals(TODO_TYPE)) {
            return new Todo(taskParts[2], isDone);
        } else if (taskParts[0].equals(DEADLINE_TYPE)) {
            return parseDeadline(taskParts, isDone);
        } else if (taskParts[0].equals(EVENT_TYPE)) {
            return parseEvent(taskParts, isDone);
        } else {
            throw new IllegalArgumentException(
                    "Invalid task type in storage: " + taskParts[0]);
        }
    }

    /**
     * Returns a deadline reconstructed from parsed storage fields.
     *
     * @param taskParts the fields extracted from a stored deadline.
     * @param isDone whether the deadline has been completed.
     * @return the reconstructed deadline.
     */
    private Deadline parseDeadline(String[] taskParts, boolean isDone) {
        return new Deadline(
                taskParts[2],
                isDone,
                DateParser.parseDateTime(taskParts[3], STORAGE_DATE_TIME_FORMAT));
    }

    /**
     * Returns an event reconstructed from parsed storage fields.
     *
     * @param taskParts the fields extracted from a stored event.
     * @param isDone whether the event has been completed.
     * @return the reconstructed event.
     */
    private Event parseEvent(String[] taskParts, boolean isDone) {
        return new Event(
                taskParts[2],
                isDone,
                DateParser.parseDateTime(taskParts[3], STORAGE_DATE_TIME_FORMAT),
                DateParser.parseDateTime(taskParts[4], STORAGE_DATE_TIME_FORMAT));
    }

    /**
     * Updates the stored task at the specified one-based task number.
     *
     * @param taskNum one-based position of the task to update.
     * @param task updated task to store at the specified position.
     * @throws IOException if the storage file cannot be read or written.
     */
    public void updateTaskStatus(int taskNum, Task task) throws IOException {
        Path filePath = Path.of(this.filePath);
        List<String> taskLines = Files.readAllLines(filePath);
        int lineIndex = taskNum - 1;
        assert lineIndex >= 0 && lineIndex < taskLines.size() : "Task number must identify an existing storage entry";
        taskLines.set(lineIndex, task.formatForStorage());
        Files.write(filePath, taskLines);
    }

    /**
     * Deletes the stored task at the specified one-based task number.
     *
     * @param taskNum one-based position of the task to delete.
     * @throws IOException if the storage file cannot be read or written.
     */
    public void deleteTask(int taskNum) throws IOException {
        Path filePath = Path.of(this.filePath);
        List<String> taskLines = Files.readAllLines(filePath);
        int lineIndex = taskNum - 1;
        assert lineIndex >= 0 && lineIndex < taskLines.size() : "Task number must identify an existing storage entry";
        taskLines.remove(lineIndex);
        Files.write(filePath, taskLines);
    }
}
