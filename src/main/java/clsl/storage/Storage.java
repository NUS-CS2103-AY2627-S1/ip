package clsl.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import clsl.ClslException;
import clsl.task.Deadline;
import clsl.task.Event;
import clsl.task.Task;
import clsl.task.ToDo;

/**
 * Handles saving task data to the hard disk.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " \\| ";
    private static final String TODO_MARKER = "T";
    private static final String DEADLINE_MARKER = "D";
    private static final String EVENT_MARKER = "E";
    private static final String COMPLETED_STATUS = "1";

    private final Path filePath;

    /**
     * Creates storage that saves tasks to the given file path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks Tasks currently in the task list.
     * @throws IOException If the file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(filePath.getParent());

        StringBuilder contents = new StringBuilder();
        for (Task task : tasks) {
            contents.append(task.getSaveString()).append(System.lineSeparator());
        }

        Files.writeString(filePath, contents.toString());
    }

    /**
     * Loads tasks from the data file.
     *
     * @return Tasks recreated from the data file.
     * @throws IOException If the data file cannot be read.
     * @throws ClslException If the data file contains an unknown task type.
     */
    public List<Task> load() throws IOException, ClslException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        List<String> lines = Files.readAllLines(filePath);
        List<Task> tasks = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(FIELD_SEPARATOR);
            Task task;

            if (parts[0].equals(TODO_MARKER)) {
                task = new ToDo(parts[2]);
            } else if (parts[0].equals(DEADLINE_MARKER)) {
                task = new Deadline(parts[2], parts[3]);
            } else if (parts[0].equals(EVENT_MARKER)) {
                task = new Event(parts[2], parts[3], parts[4]);
            } else {
                throw new ClslException("Unknown task type in file input");
            }

            if (parts[1].equals(COMPLETED_STATUS)) {
                task.markAsDone();
            }
            tasks.add(task);
        }
        return tasks;
    }
}
