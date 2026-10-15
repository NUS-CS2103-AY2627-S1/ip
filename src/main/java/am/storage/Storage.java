package am.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import am.task.Task;
import am.task.TaskList;

/**
 * Reads and writes the task list in the application's data file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the supplied file path.
     *
     * @param filePath path to the task data file
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from disk, ignoring blank lines.
     *
     * @return the loaded tasks, or an empty list when the file does not exist
     * @throws IOException if the file cannot be read
     * @throws CorruptedDataException if a non-blank line is malformed
     */
    public TaskList load() throws IOException, CorruptedDataException {
        TaskList tasks = new TaskList();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        int lineNumber = 0;
        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            lineNumber++;
            if (!line.isBlank()) {
                try {
                    tasks.addTask(Task.fromSerialized(line));
                } catch (CorruptedDataException error) {
                    throw new CorruptedDataException("Corrupted task data on line " + lineNumber, error);
                }
            }
        }

        return tasks;
    }

    /**
     * Saves all tasks, replacing any existing contents of the data file.
     *
     * @param tasks tasks to persist
     * @throws IOException if the file or its parent directory cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < tasks.getLength(); i++) {
            content.append(tasks.getTask(i).toSerialized()).append(System.lineSeparator());
        }

        Files.writeString(filePath,
                content.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
