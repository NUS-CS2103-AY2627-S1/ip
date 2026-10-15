package catgpt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Loads and saves CatGPT tasks using a file relative to the project directory.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final int FIELD_COUNT = 4;

    private final Path filePath;

    /**
     * Creates storage backed by the specified file path.
     *
     * @param filePath Path to the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @return Tasks reconstructed from the data file.
     * @throws CatGPTException If the file cannot be read or contains invalid data.
     */
    public List<Task> load() throws CatGPTException {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            if (lines.size() > TaskList.MAX_TASKS) {
                throw new CatGPTException("The saved task list contains too many tasks.");
            }

            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                tasks.add(loadTask(lines.get(i), i));
            }
            return tasks;
        } catch (IOException | SecurityException error) {
            throw new CatGPTException("I couldn't load tasks from " + filePath + ".");
        }
    }

    /**
     * Saves all current tasks, creating the data directory and file when necessary.
     *
     * @param tasks Tasks to save.
     * @throws CatGPTException If the task data cannot be written.
     */
    public void save(TaskList tasks) throws CatGPTException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.getType().name()
                    + FIELD_SEPARATOR + (task.isDone() ? "1" : "0")
                    + FIELD_SEPARATOR + encode(task.getDescription())
                    + FIELD_SEPARATOR + encode(task.getStorageDetails()));
        }

        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException error) {
            throw new CatGPTException("I couldn't save tasks to " + filePath + ".");
        }
    }

    /**
     * Parses one saved task.
     *
     * @param line Serialized task data.
     * @param index Zero-based source line number.
     * @return The reconstructed task.
     * @throws CatGPTException If the serialized task is invalid.
     */
    private Task loadTask(String line, int index) throws CatGPTException {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (fields.length != FIELD_COUNT) {
            throw corruptedDataException(index);
        }

        try {
            TaskType taskType = TaskType.valueOf(fields[0]);
            if (!fields[1].equals("0") && !fields[1].equals("1")) {
                throw corruptedDataException(index);
            }
            boolean isDone = fields[1].equals("1");
            String description = decode(fields[2]);
            String details = decode(fields[3]);
            return switch (taskType) {
                case TODO -> new Todo(description, isDone);
                case DEADLINE -> new Deadline(description, LocalDate.parse(details), isDone);
                case EVENT -> new Event(description, details, isDone);
            };
        } catch (IllegalArgumentException | DateTimeException error) {
            throw corruptedDataException(index);
        }
    }

    /**
     * Encodes task text so separators inside user input cannot corrupt the data format.
     *
     * @param value Text to encode.
     * @return Base64-encoded text.
     */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes text previously written by {@link #encode(String)}.
     *
     * @param value Base64-encoded text.
     * @return Decoded text.
     */
    private String decode(String value) {
        byte[] decodedBytes = Base64.getDecoder().decode(value);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Creates a user-friendly error identifying a corrupted data-file line.
     *
     * @param index Zero-based line index.
     * @return The corruption error.
     */
    private CatGPTException corruptedDataException(int index) {
        return new CatGPTException("The saved task data is corrupted at line " + (index + 1) + ".");
    }
}
