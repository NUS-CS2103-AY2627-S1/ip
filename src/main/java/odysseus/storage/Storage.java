package odysseus.storage;

import odysseus.OdysseusException;
import odysseus.task.Task;
import odysseus.task.TaskFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all storage tasks: saving tasks to file and
 * loading of tasks from file.
 */
public class Storage {
    private final Path path;

    /**
     * Creates a Storage backed by the given file path.
     *
     * @param filePath string of filePath for storage
     */
    public Storage(String filePath) {
        this.path = Path.of(filePath);
    }

    /**
     * Returns list of tasks for file path.
     *
     * @return list of tasks extracted from file provided
     * @throws OdysseusException if there is an IOException while reading the file
     */
    public List<Task> load() throws OdysseusException {
        if (Files.exists(path)) {
            try {
                List<String> lines = Files.readAllLines(path);
                List<Task> tasks = new ArrayList<>();
                for (String line : lines) {
                    Task newTask = TaskFactory.fromSaveFormat(line);
                    tasks.add(newTask);
                }
                return tasks;
            } catch (IOException e) {
                throw new OdysseusException("Hey! An error occurred while loading file: " +
                        e.getMessage());
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Saves a list of tasks into file path.
     *
     * @param tasks list of task to save into file
     * @throws OdysseusException if there is an IOException while writing to file
     */
    public void save(List<Task> tasks) throws OdysseusException {
        List<String> lines = tasks.stream()
                .map(Task::toSaveFormat)
                .toList();
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, lines);
        } catch (IOException e) {
            throw new OdysseusException("Error occurred while saving tasks: " +
                    e.getMessage());
        }
    }
}
