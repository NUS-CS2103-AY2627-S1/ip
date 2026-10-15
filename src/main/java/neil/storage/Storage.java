package neil.storage;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import neil.exception.NeilException;
import neil.task.Task;

/**
 * Handles loading tasks from and saving tasks to the hard disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath path to the task storage file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves all tasks to the configured file.
     *
     * @param tasks tasks to save.
     * @throws NeilException if the file cannot be written.
     */
    public void save(List<Task> tasks) throws NeilException {
        List<String> lines = tasks.stream()
                .map(Task::encode)
                .toList();
        Path temporaryFile = null;

        try {
            Path absoluteFilePath = filePath.toAbsolutePath();
            Path parentDirectory = absoluteFilePath.getParent();

            if (parentDirectory == null) {
                throw new IOException("Storage file has no parent directory");
            }

            Files.createDirectories(parentDirectory);
            temporaryFile = Files.createTempFile(parentDirectory, ".neil-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);

            try (FileChannel channel = FileChannel.open(temporaryFile, StandardOpenOption.WRITE)) {
                channel.force(true);
            }

            replaceFile(temporaryFile, absoluteFilePath);
            temporaryFile = null;
        } catch (IOException e) {
            throw new NeilException("Unable to save tasks to " + filePath);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Replaces the storage file atomically when supported by its file system.
     *
     * @param temporaryFile complete temporary storage file.
     * @param targetFile storage file to replace.
     * @throws IOException if the replacement fails.
     */
    void replaceFile(Path temporaryFile, Path targetFile) throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException e) {
            // Preserve the original save failure rather than replacing it with a cleanup failure.
        }
    }

    /**
     * Loads tasks from the configured file.
     * Returns an empty list if the file does not exist yet.
     *
     * @return tasks loaded from the file.
     * @throws NeilException if the file cannot be read or decoded.
     */
    public List<Task> load() throws NeilException {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(
                    filePath,
                    StandardCharsets.UTF_8);

            for (String line : lines) {
                if (!line.isBlank()) {
                    tasks.add(Task.decode(line));
                }
            }

            return tasks;
        } catch (IOException e) {
            throw new NeilException("Unable to load tasks from " + filePath);
        }
    }
}
