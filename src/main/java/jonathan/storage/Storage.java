package jonathan.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import jonathan.task.Task;

/** Saves tasks to and loads tasks from a local text file. */
public class Storage {
    private final Path filePath;

    /**
     * Constructs a {@code Storage} handler with the specified file path.
     *
     * @param filePath The relative or absolute path where the data file will be stored.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves the current list of tasks to the local text file.
     * Creates the necessary directories if they do not exist.
     *
     * @param tasks     The array of current tasks to be saved.
     * @param itemCount The total number of active tasks in the array.
     * @throws IOException If an error occurs while creating directories or writing to the file.
     */
    public void save(Task[] tasks, int itemCount) throws IOException {
        Files.createDirectories(filePath.getParent());

        StringBuilder fileContents = new StringBuilder();
        for (int i = 0; i < itemCount; i++) {
            fileContents.append(tasks[i].toFileString()).append(System.lineSeparator());
        }
        Files.writeString(filePath, fileContents.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Reads saved tasks from the local file and returns them as a list of raw strings.
     * If the file does not exist yet, it returns an empty list.
     *
     * @return A {@code List<String>} containing the raw text lines from the save file.
     * @throws IOException If an error occurs while attempting to read the file.
     */
    public List<String> load() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>(); // Return empty list if no save file exists yet
        }
        return Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }
}
