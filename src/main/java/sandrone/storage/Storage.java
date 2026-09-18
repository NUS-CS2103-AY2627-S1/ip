package sandrone.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

import sandrone.task.Task;
import sandrone.ui.Ui;

/**
 * Reads task records from, and writes task records to, the save file.
 */
public class Storage {
    private final Path filePath;
    private final Ui ui;

    /**
     * Creates storage that uses the given save-file path.
     *
     * @param filePath location of the task save file
     * @param ui user interface used to report file errors
     */
    public Storage(Path filePath, Ui ui) {
        this.filePath = filePath;
        this.ui = ui;
    }

    /**
     * Loads every non-interpreted task record from the save file.
     * A missing file represents a first run and therefore returns an empty list.
     *
     * @return the saved task records, or an empty list when they cannot be loaded
     */
    public List<String> load() {
        try {
            if (!Files.exists(filePath)) {
                return List.of();
            }
            if (!Files.isRegularFile(filePath)) {
                ui.showMessage("Warning: Could not load tasks: Save path is not a file");
                return List.of();
            }
            return Files.readAllLines(filePath);
        } catch (IOException | InvalidPathException | SecurityException e) {
            ui.showMessage("Warning: Could not load tasks: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Saves every task, creating the parent directory when necessary.
     *
     * @param tasks tasks to write to the save file
     */
    public void save(List<Task> tasks) {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat());
                    writer.newLine();
                }
            }
        } catch (IOException | InvalidPathException | SecurityException e) {
            ui.showMessage("Warning: Could not save tasks: " + e.getMessage());
        }
    }
}
