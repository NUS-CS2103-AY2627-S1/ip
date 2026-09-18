package student.project.bob.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import student.project.bob.model.Deadline;
import student.project.bob.model.Event;
import student.project.bob.model.Task;
import student.project.bob.model.Todo;

/**
 * Saves Bob's tasks to the local data file.
 */
public class Storage {
    private static final Path DATA_FILE = Path.of("data", "Bob.txt");

    /**
     * Loads tasks from the local data file.
     *
     * @return tasks represented by the data file
     * @throws IOException if the data file cannot be read
     */
    public static List<Task> loadTasks() throws StorageException {
        try {
            List<String> taskLines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
            return taskLines.stream().map(Storage::parseTask).collect(Collectors.toList());
        } catch (NoSuchFileException e) {
            return new ArrayList<>();
        } catch (IOException | RuntimeException e) {
            throw new StorageException("Could not load tasks from " + DATA_FILE + ".", e);
        }
    }

    /**
     * Saves the current tasks, replacing the previous contents of the data file.
     *
     * @param tasks tasks to save
     * @throws IOException if the data directory or file cannot be written
     */
    public static void saveTasks(List<Task> tasks) throws StorageException {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            List<String> taskLines = tasks.stream().map(Task::toString).collect(Collectors.toList());
            Files.write(DATA_FILE, taskLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("Could not save tasks to " + DATA_FILE + ".", e);
        }
    }

    /**
     * Reconstructs a task from the format produced by {@link #saveTasks(List)}.
     *
     * @param taskLine serialized task line
     * @return reconstructed task
     */
    private static Task parseTask(String taskLine) {
        char taskType = taskLine.charAt(1);
        boolean isDone = taskLine.charAt(4) == 'X';
        String taskContent = taskLine.substring(6).trim();

        Task task =
                switch (taskType) {
                    case 'T' -> new Todo(taskContent);
                    case 'D' -> parseDeadline(taskContent);
                    case 'E' -> parseEvent(taskContent);
                    default -> throw new IllegalArgumentException("Unknown task type: " + taskType);
                };

        if (isDone) {
            task.setDone();
        }
        return task;
    }

    /**
     * Reconstructs a deadline from its serialized task content.
     *
     * @param taskContent serialized deadline content
     * @return reconstructed deadline
     */
    private static Deadline parseDeadline(String taskContent) {
        int byIndex = taskContent.lastIndexOf(" (by: ");
        String description = taskContent.substring(0, byIndex);
        String by = taskContent.substring(byIndex + " (by: ".length(), taskContent.length() - 1);
        return new Deadline(description, by);
    }

    /**
     * Reconstructs an event from its serialized task content.
     *
     * @param taskContent serialized event content
     * @return reconstructed event
     */
    private static Event parseEvent(String taskContent) {
        int fromIndex = taskContent.indexOf(" (from: ");
        int toIndex = taskContent.lastIndexOf(" to: ");
        String description = taskContent.substring(0, fromIndex);
        String from = taskContent.substring(fromIndex + " (from: ".length(), toIndex);
        String to = taskContent.substring(toIndex + " to: ".length(), taskContent.length() - 1);
        return new Event(description, from, to);
    }
}
