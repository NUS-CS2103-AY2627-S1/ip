package pixel.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import pixel.task.Task;
import pixel.task.TaskList;

/**
 * Loads tasks from and saves tasks to the application's data file.
 */
public class Storage {
    private static final int MAX_REPLACE_ATTEMPTS = 3;
    private static final long REPLACE_RETRY_DELAY_MILLISECONDS = 25;

    private final Path filePath;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Relative or absolute path to the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes the current task list to the data file.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> taskData = IntStream.range(0, tasks.size())
                .mapToObj(index -> tasks.get(index).toDataString())
                .toList();

        Path temporaryFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        Files.write(temporaryFile, taskData, StandardCharsets.UTF_8);
        replaceDataFile(temporaryFile);
    }

    private void replaceDataFile(Path temporaryFile) throws IOException {
        IOException lastException = null;
        for (int attempt = 1; attempt <= MAX_REPLACE_ATTEMPTS; attempt++) {
            try {
                moveTemporaryFile(temporaryFile);
                return;
            } catch (IOException exception) {
                lastException = exception;
                if (attempt < MAX_REPLACE_ATTEMPTS) {
                    waitBeforeRetry();
                }
            }
        }
        throw lastException;
    }

    private void moveTemporaryFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void waitBeforeRetry() throws IOException {
        try {
            Thread.sleep(REPLACE_RETRY_DELAY_MILLISECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while retrying the task save.", exception);
        }
    }

    /**
     * Loads saved tasks, or returns an empty list when no data file exists yet.
     * Invalid records are skipped and described in {@code warnings}.
     *
     * @param warnings Messages describing records that could not be loaded.
     * @return Valid tasks restored from the data file.
     */
    public ArrayList<Task> load(List<String> warnings) {
        ArrayList<Task> tasks = new ArrayList<>();
        List<String> savedLines;
        try {
            savedLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (NoSuchFileException exception) {
            return tasks;
        } catch (IOException | SecurityException exception) {
            warnings.add("Oops! I couldn't read the saved tasks. Starting with an empty list.");
            return tasks;
        }

        for (int i = 0; i < savedLines.size(); i++) {
            String taskData = savedLines.get(i);
            if (taskData.isBlank()) {
                continue;
            }
            try {
                Task task = Task.fromDataString(taskData);
                boolean isDuplicate = tasks.stream()
                        .anyMatch(existingTask -> existingTask.hasSameDetails(task));
                if (isDuplicate) {
                    warnings.add("Oops! I skipped duplicate saved task on line " + (i + 1) + ".");
                } else {
                    tasks.add(task);
                }
            } catch (IllegalArgumentException exception) {
                warnings.add("Oops! I skipped invalid saved task on line " + (i + 1)
                        + ": " + exception.getMessage());
            }
        }
        return tasks;
    }
}
