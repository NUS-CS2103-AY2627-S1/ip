package echo.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import echo.exception.CorruptFormatException;
import echo.exception.StorageException;
import echo.task.Task;
import echo.task.TaskList;

/**
 * Saves the task list to a file on disk.
 * Each task is stored on its own line in a pipe-delimited
 * save format (see {@link Task#toSaveFormat()}), e.g.
 * {@code D | 1 | return book | Sunday}.
 */
public class Storage {
    private final Path saveFile;

    /**
     * Instantiates the {@link Storage} class with default
     * save file path.
     */
    public Storage() {
        this.saveFile = Path.of("data", "echo.txt");
    }

    /**
     * Instantiates the {@link Storage} class with custom
     * save file path.
     * @param fileSavePath {@link Path} of save file
     */
    public Storage(Path fileSavePath) {
        this.saveFile = Objects.requireNonNull(fileSavePath, "The save-file path must not be null.");
    }

    /**
     * Overwrites the save file with one line per task, creating the
     * {@code data} directory first if it does not exist yet.
     *
     * @param tasks list of tasks to write to the save file
     * @throws StorageException when writing to the save file fails
     */
    public void save(List<Task> tasks) throws StorageException {
        Objects.requireNonNull(tasks, "The saved task collection must not be null.");
        List<String> lines = serialize(tasks);
        Path absoluteSaveFile = saveFile.toAbsolutePath();
        Path parentDirectory = absoluteSaveFile.getParent();
        try {
            Files.createDirectories(parentDirectory);
            Files.write(absoluteSaveFile, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new StorageException("I could not save your missions to " + saveFile + ".");
        }
    }

    /**
     * Reads the save file line by line and lets each task class reconstruct
     * itself from its own line (see {@link Task#fromSaveFormat(String)}).
     * A missing save file is treated as an empty task list so that the
     * chatbot starts cleanly on first use.
     *
     * @return tasks restored from the save file
     * @throws StorageException when reading fails or a line is malformed
     */
    public List<Task> read() throws StorageException {
        if (Files.notExists(saveFile)) {
            return new ArrayList<>();
        }

        TaskList loadedTasks = new TaskList();
        try (BufferedReader reader = Files.newBufferedReader(saveFile, StandardCharsets.UTF_8)) {
            String rawLine;
            int lineNumber = 0;
            while ((rawLine = reader.readLine()) != null) {
                lineNumber++;
                String line = rawLine.trim();
                if (line.isEmpty()) {
                    continue;
                }
                Task task = parseRecord(line, lineNumber);
                if (loadedTasks.containsTaskWithSameDetails(task)) {
                    throw new CorruptFormatException(lineNumber, line,
                            "it duplicates an earlier mission");
                }
                loadedTasks.add(task);
            }
            return new ArrayList<>(loadedTasks.getAll());
        } catch (IOException | SecurityException exception) {
            throw new StorageException("I could not read your missions at " + saveFile + ".");
        }
    }

    private List<String> serialize(List<Task> tasks) throws StorageException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new StorageException("I could not save a missing mission record.");
            }
            lines.add(task.toSaveFormat());
        }
        return lines;
    }

    private Task parseRecord(String line, int lineNumber) throws CorruptFormatException {
        try {
            return Task.fromSaveFormat(line);
        } catch (StorageException exception) {
            throw new CorruptFormatException(lineNumber, line, "its fields are invalid");
        }
    }

}
