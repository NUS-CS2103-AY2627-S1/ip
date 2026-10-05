package friday.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import friday.FridayException;
import friday.Parser;
import friday.task.Task;
import friday.task.TaskList;

/**
 * Loads tasks from and saves tasks to the hard disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage object that uses the given file path.
     *
     * @param filePath path of the save file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the save file.
     *
     * @return tasks loaded from the save file
     * @throws FridayException if the save file cannot be read
     */
    public ArrayList<Task> load() throws FridayException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            if (Files.notExists(filePath)) {
                return tasks;
            }

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    try {
                        tasks.add(Parser.parseSavedTask(line));
                    } catch (FridayException e) {
                        throw new FridayException("Saved task on line " + (i + 1) + " is invalid: " + e.getMessage());
                    }
                }
            }
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new FridayException("Sorry, I could not read the saved task file at " + filePath + ".");
        }
    }

    /**
     * Saves all tasks to the save file.
     *
     * @param tasks tasks to save
     * @throws FridayException if the tasks cannot be saved
     */
    public void save(TaskList tasks) throws FridayException {
        Path temporaryFile = null;
        try {
            Path parentDirectory = filePath.toAbsolutePath().getParent();
            Files.createDirectories(parentDirectory);
            List<String> lines = new ArrayList<>();
            for (Task task : tasks.asList()) {
                lines.add(task.toFileString());
            }
            temporaryFile = Files.createTempFile(parentDirectory, "friday-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            throw new FridayException("Sorry, I could not save your tasks to the hard disk.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException e) {
                    // A leftover temporary file must not hide the original save result.
                }
            }
        }
    }
}
