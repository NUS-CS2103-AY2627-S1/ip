package nori;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import nori.task.Deadline;
import nori.task.Event;
import nori.task.Task;
import nori.task.Todo;

/**
 * Loads and saves Nori tasks in a local text file.
 */
public class Storage {
    private static final String INVALID_DATA_MESSAGE = "I couldn't read a task in your save file.";

    private final Path filePath;

    /**
     * Creates storage backed by the given file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the data file.
     *
     * @return Saved tasks, or an empty task list if the file does not exist.
     * @throws NoriException If the file cannot be read or contains invalid task data.
     */
    public TaskList load() throws NoriException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return new TaskList(tasks);
        }

        try {
            for (String line : Files.readAllLines(filePath)) {
                tasks.add(parseTask(line));
            }
            return new TaskList(tasks);
        } catch (IOException e) {
            throw new NoriException("I couldn't load your saved tasks.");
        }
    }

    /**
     * Writes all tasks to the data file, creating its parent directory if needed.
     *
     * @param tasks Tasks to save.
     * @throws NoriException If the data file cannot be written.
     */
    public void save(TaskList tasks) throws NoriException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            lines.add(tasks.get(i).toStorageString());
        }

        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new NoriException("I couldn't save your tasks.");
        }
    }

    private static Task parseTask(String line) throws NoriException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new NoriException(INVALID_DATA_MESSAGE);
        }

        Task task = switch (fields[0]) {
            case "T" -> {
                requireFieldCount(fields, 3);
                requireNonBlankFields(fields, 2);
                yield new Todo(fields[2]);
            }
            case "D" -> {
                requireFieldCount(fields, 4);
                requireNonBlankFields(fields, 2, 3);
                yield new Deadline(fields[2], fields[3]);
            }
            case "E" -> {
                requireFieldCount(fields, 5);
                requireNonBlankFields(fields, 2, 3, 4);
                yield new Event(fields[2], fields[3], fields[4]);
            }
            default -> throw new NoriException(INVALID_DATA_MESSAGE);
        };

        if ("1".equals(fields[1])) {
            task.mark();
        } else if (!"0".equals(fields[1])) {
            throw new NoriException(INVALID_DATA_MESSAGE);
        }
        return task;
    }

    private static void requireFieldCount(String[] fields, int expectedCount) throws NoriException {
        if (fields.length != expectedCount) {
            throw new NoriException(INVALID_DATA_MESSAGE);
        }
    }

    private static void requireNonBlankFields(String[] fields, int... indexes) throws NoriException {
        for (int index : indexes) {
            if (fields[index].isBlank()) {
                throw new NoriException(INVALID_DATA_MESSAGE);
            }
        }
    }
}
