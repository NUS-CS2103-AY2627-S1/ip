package tianyi.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import tianyi.task.Task;

/**
 * Loads tasks from a data file and saves tasks back to it.
 */
public class Storage {
    private final File file;
    private final DataParser parser;

    /**
     * Creates a storage manager for the specified data file.
     *
     * @param filePath Path of the data file.
     */
    public Storage(String filePath) {
        file = new File(filePath);
        parser = new DataParser();
    }

    /**
     * Loads all tasks from the data file.
     *
     * @return Tasks stored in the file, or an empty list if the file does not exist.
     * @throws StorageException If the file cannot be read or contains invalid data.
     */
    public List<Task> load()
            throws StorageException {
        if (!file.exists()) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                tasks.add(parser.parse(scanner.nextLine()));
            }
        } catch (IOException e) {
            throw new StorageException("Unable to load tasks from " + file.getPath() + ".");
        }

        return tasks;
    }

    /**
     * Replaces the data file contents with the supplied tasks.
     *
     * @param tasks Tasks to persist.
     * @throws StorageException If the data directory or file cannot be written.
     */
    public void save(List<Task> tasks)
            throws StorageException {
        File dataFolder = file.getParentFile();
        boolean shouldCreateDataFolder = dataFolder != null && !dataFolder.exists();

        if (shouldCreateDataFolder && !dataFolder.mkdirs()) {
            throw new StorageException("Unable to create the data folder.");
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks) {
                writer.write(task.getData() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new StorageException("Unable to save tasks to " + file.getPath() + ".");
        }
    }
}
