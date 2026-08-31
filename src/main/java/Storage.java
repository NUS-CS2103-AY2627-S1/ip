import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves tasks to, and later loads them from, a plain-text file on the hard disk.
 *
 * <p>Each task occupies one line in the form {@code T | 1 | read book}, where the
 * first field is the task type code and the second field is {@code 1} when the
 * task is complete. The path is relative to the project root and is built with
 * {@link Paths#get(String, String...)} so it works on any operating system.
 */
public class Storage {
    /** Default save location, relative to the project root: {@code ./data/bibi.txt}. */
    private static final Path DEFAULT_FILE_PATH = Paths.get("data", "bibi.txt");

    private final Path filePath;

    /**
     * Creates storage that uses the default {@code ./data/bibi.txt} save file.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage that uses the supplied save file, which is useful for testing.
     *
     * @param filePath location of the save file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes every task to the save file, replacing any previous contents.
     *
     * <p>The containing folder is created first when it does not exist yet, so a
     * fresh copy of the project can save tasks without any manual setup.
     *
     * @param tasks the tasks to store
     * @throws IOException if the folder or file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        Path parentFolder = filePath.getParent();
        if (parentFolder != null) {
            Files.createDirectories(parentFolder);
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks.getTasks()) {
            lines.add(task.toFileFormat());
        }
        Files.write(filePath, lines);
    }

    /**
     * Reads the saved tasks back into a task list.
     *
     * <p>A missing save file is normal rather than an error: someone running a
     * fresh copy of the project simply starts with an empty list.
     *
     * @return the stored tasks, or an empty list when nothing has been saved yet
     * @throws IOException if the file exists but cannot be read
     * @throws BibiException if a saved line is not in the expected format
     */
    public TaskList load() throws IOException, BibiException {
        TaskList tasks = new TaskList();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath)) {
            tasks.add(parseTask(line));
        }
        return tasks;
    }

    /**
     * Rebuilds one task from its save-file line.
     *
     * @param line a line written by {@link Task#toFileFormat()}
     * @return the task described by the line
     * @throws BibiException if the line does not describe a valid task
     */
    private Task parseTask(String line) throws BibiException {
        // Split on the separator and drop the spaces padding it, so the fields
        // come back exactly as the user typed them.
        String[] fields = line.trim().split("\\s*\\" + Task.FIELD_SEPARATOR + "\\s*");
        String typeCode = fields[0];
        boolean isComplete = fields[1].equals("1");
        String description = fields[2];

        Task task = switch (typeCode) {
        case "T" -> new Todo(description);
        case "D" -> new Deadline(description, fields[3]);
        case "E" -> new Event(description, fields[3], fields[4]);
        default -> throw new BibiException("Unknown task type '" + typeCode + "'.");
        };

        if (isComplete) {
            task.markComplete();
        }
        return task;
    }
}
