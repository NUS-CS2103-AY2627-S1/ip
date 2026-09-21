package nova;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving tasks.
 */
public class Storage {

    private final Path filePath;
    private boolean hasCorruptedEntries;

    /**
     * Creates storage using the specified file.
     *
     * @param filePath Task storage file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return Loaded task list.
     * @throws IOException If the storage file cannot be accessed.
     */
    public TaskList load() throws IOException {
        hasCorruptedEntries = false;
        createParentDirectory();

        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
            return new TaskList();
        }

        List<Task> tasks = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(Task.fromStorageString(line));
            } catch (RuntimeException ignored) {
                hasCorruptedEntries = true;
            }
        }

        return new TaskList(tasks);
    }

    /**
     * Returns whether corrupted entries were skipped during the most recent load.
     *
     * @return True if at least one corrupted entry was skipped.
     */
    public boolean hasCorruptedEntries() {
        return hasCorruptedEntries;
    }

    /**
     * Saves tasks to the storage file.
     *
     * @param taskList Tasks to save.
     * @throws IOException If the storage file cannot be written.
     */
    public void save(TaskList taskList) throws IOException {
        createParentDirectory();

        List<String> lines = new ArrayList<>();
        for (Task task : taskList.getAll()) {
            lines.add(task.toStorageString());
        }

        Files.write(
                filePath,
                lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void createParentDirectory() throws IOException {
        Path parent = filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
