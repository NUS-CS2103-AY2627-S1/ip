package bambolino.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import bambolino.task.Deadline;
import bambolino.task.Event;
import bambolino.task.Task;
import bambolino.task.Todo;

/**
 * Saves and restores Bambolino tasks using a file relative to the project directory.
 */
public class Storage {
    /** The location used for task data. */
    private final Path filePath;

    /** Prevents overwriting data that could not be fully loaded during this session. */
    private boolean isSaveBlocked;

    /** Creates storage in the application data directory. */
    public Storage() {
        this(Path.of("data", "bambolino.txt"));
    }

    /**
     * Creates storage at a supplied location, allowing isolated tests and alternate data directories.
     *
     * @param filePath The file in which tasks are stored.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads every valid task in the data file.
     *
     * @return The loaded tasks, or an empty list when no data file exists.
     * @throws IOException If the data file cannot be read.
     */
    public List<Task> load() throws IOException {
        return load(System.out::println);
    }

    /**
     * Loads valid tasks and sends corrupt-record warnings to the active interface.
     *
     * @param warningOutput The destination for loading warnings.
     * @return The loaded tasks, or an empty list when no data file exists.
     * @throws IOException If the data file cannot be read.
     */
    public List<Task> load(Consumer<String> warningOutput) throws IOException {
        List<Task> tasks = new ArrayList<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (NoSuchFileException error) {
            return tasks;
        } catch (IOException error) {
            isSaveBlocked = true;
            throw error;
        }

        for (String line : lines) {
            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException | DateTimeException error) {
                isSaveBlocked = true;
                warningOutput.accept("Warning: Ignored a corrupted task in " + filePath + ".");
            }
        }
        if (isSaveBlocked) {
            warningOutput.accept("Warning: Saving is disabled to protect your existing data. "
                    + "Back up and repair the data file, then restart Bambolino.");
        }
        return tasks;
    }

    /**
     * Rejects changes when loading failed, protecting the original data until restart.
     *
     * @throws IOException If saving is disabled after a loading problem.
     */
    public void checkWritable() throws IOException {
        if (isSaveBlocked) {
            throw new IOException("Saving is disabled to protect your existing data. "
                    + "Back up and repair the data file, then restart Bambolino.");
        }
    }

    /**
     * Writes the current task list, creating its data directory if needed.
     *
     * @param tasks The tasks to save.
     * @throws IOException If the task data cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        checkWritable();
        Path parent = filePath.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toStorageString());
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /** Converts one stored line into a task. */
    private Task parseTask(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 3 || fields.length > 5) {
            throw new IllegalArgumentException("Incorrect number of fields");
        }
        boolean isDone = switch (fields[1]) {
        case "0" -> false;
        case "1" -> true;
        default -> throw new IllegalArgumentException("Invalid completion status");
        };
        Task task;
        if (fields[0].equals("T") && fields.length == 3) {
            task = new Todo(decode(fields[2]));
        } else if (fields[0].equals("D") && fields.length == 4) {
            task = new Deadline(decode(fields[2]), LocalDate.parse(decode(fields[3])));
        } else if (fields[0].equals("E") && fields.length == 5) {
            task = new Event(decode(fields[2]), decode(fields[3]), decode(fields[4]));
        } else {
            throw new IllegalArgumentException("Invalid task type");
        }
        assert task != null : "a valid storage record must create a task";
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /** Decodes one Base64-encoded text field. */
    private String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }
}
