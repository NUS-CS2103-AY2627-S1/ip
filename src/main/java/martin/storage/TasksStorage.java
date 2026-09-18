package martin.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import martin.exception.IllegalCommandException;
import martin.exception.StorageException;
import martin.task.Task;
import martin.task.TaskList;

/**
 * Handles the loading and saving of tasks to and from the local storage file.
 */
public class TasksStorage {
    private final Path filePath;

    /**
     * Constructs a {@code TasksStorage} object with the specified file path.
     *
     * @param filePath The relative or absolute path to the data file.
     */
    public TasksStorage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the data file. If the file or parent directory does not
     * exist,
     * it creates them and returns an empty list. Uses streams.
     *
     * @return The list of loaded tasks.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(this.filePath)) {
                if (this.filePath.getParent() != null) {
                    Files.createDirectories(this.filePath.getParent());
                }
                Files.createFile(this.filePath);
                return tasks;
            }

            List<String> lines = Files.readAllLines(this.filePath);
            tasks = lines.stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> {
                        try {
                            return Task.fromDataFormat(line);
                        } catch (IllegalCommandException | IllegalArgumentException exception) {
                            System.out.println("Skipping corrupted or outdated task line: " + line);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException exception) {
            throw new StorageException("Unable to load tasks from " + this.filePath + ".", exception);
        }
        if (!tasks.isEmpty()) {
            System.out.println("Loaded " + tasks.size() + " task(s) from the data file!");
        }
        return tasks;
    }

    /**
     * Saves the given task list to the data file.
     *
     * @param taskList The {@link TaskList} to save.
     */
    public void save(TaskList taskList) {
        this.save(taskList.getAllTasks());
    }

    /**
     * Saves the given list of tasks to the data file.
     *
     * @param tasks The list of tasks to save. Uses streams.
     */
    public void save(List<Task> tasks) {
        try {
            if (this.filePath.getParent() != null && !Files.exists(this.filePath.getParent())) {
                Files.createDirectories(this.filePath.getParent());
            }

            List<String> lines = tasks.stream()
                    .map(Task::toDataFormat)
                    .toList();
            Files.write(this.filePath, lines);
            assert Files.exists(this.filePath)
                    && lines.size() == tasks.size()
                    : "Saved file is missing or the number of lines does not match the task count.";
        } catch (IOException exception) {
            throw new StorageException("Unable to save tasks to " + this.filePath + ".", exception);
        }
    }
}
