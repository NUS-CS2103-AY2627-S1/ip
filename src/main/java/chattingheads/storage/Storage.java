package chattingheads.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import chattingheads.exception.InvalidInputException;
import chattingheads.exception.StorageException;
import chattingheads.task.Task;
import chattingheads.task.TaskList;

/**
 * Handles loading tasks from and saving tasks to a file.
 */
public class Storage {

    private final Path filePath;

    /**
     * Creates a storage object that reads from and writes to the specified file.
     *
     * @param file Path of the file used to store tasks.
     */
    public Storage(String file) {
        filePath = Path.of(file);
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return Tasks loaded from the file.
     * @throws StorageException If an existing storage file cannot be read.
     */
    public List<Task> load() throws StorageException {
        List<Task> tasks = new ArrayList<>();
        List<String> lines;

        try {
            lines = Files.readAllLines(filePath);
        } catch (NoSuchFileException e) {
            return tasks;
        } catch (IOException e) {
            throw new StorageException("Unable to load tasks.");
        }

        for (String line : lines) {
            try {
                tasks.add(Task.fromCsv(line));
            } catch (InvalidInputException e) {
                continue;
            }
        }

        return tasks;
    }

    /**
     * Saves all tasks in the task list to the storage file.
     *
     * @param taskList Task list to save.
     * @throws StorageException When the file is unable to be written to.
     */
    public void save(TaskList taskList) throws StorageException {
        try {
            Files.write(
                    filePath,
                    taskList.getTasks().stream()
                            .map(Task::toCsv)
                            .toList());
        } catch (IOException e) {
            throw new StorageException("Unable to save tasks.");
        }
    }
}
