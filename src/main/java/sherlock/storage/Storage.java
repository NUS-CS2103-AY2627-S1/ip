package sherlock.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import sherlock.exception.SherlockException;
import sherlock.task.Deadline;
import sherlock.task.Event;
import sherlock.task.Task;
import sherlock.task.TaskList;
import sherlock.task.Todo;

/**
 * Saves tasks to, and restores tasks from, Sherlock's local data file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the given relative data-file path.
     *
     * @param filePath location of Sherlock's saved tasks
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, creating the data folder and empty file on first use.
     *
     * @return the restored task list
     */
    public TaskList load() throws IOException, SherlockException {
        TaskList tasks = new TaskList(100);
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
            return tasks;
        }
        for (String line : Files.readAllLines(filePath)) {
            if (!line.isBlank()) {
                tasks.add(parseTask(line));
            }
        }
        return tasks;
    }

    /**
     * Replaces the saved data with the current list of tasks.
     *
     * @param tasks task list to persist
     * @throws IOException if the file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            lines.add(tasks.get(index).toFileString());
        }
        Files.write(filePath, lines);
    }

    /**
     * Converts one saved data-file line into a task.
     *
     * @param line task record from the data file
     * @return reconstructed task
     * @throws SherlockException if the record is invalid
     */
    private Task parseTask(String line) throws SherlockException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new SherlockException("a saved task has an invalid format.");
        }
        Task task;
        switch (fields[0]) {
            case "T":
                task = new Todo(fields[2]);
                break;
            case "D":
                if (fields.length != 4) {
                    throw new SherlockException("a saved deadline has an invalid format.");
                }
                task = new Deadline(fields[2], fields[3]);
                break;
            case "E":
                if (fields.length != 5) {
                    throw new SherlockException("a saved event has an invalid format.");
                }
                task = new Event(fields[2], fields[3], fields[4]);
                break;
            default:
                throw new SherlockException("a saved task has an unknown type.");
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new SherlockException("a saved task has an invalid completion state.");
        }
        return task;
    }
}
