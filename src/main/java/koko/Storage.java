package koko;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading koko.Koko's tasks from the hard disk.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Paths.get("data", "koko.txt");

    /** The format used to store dates and times in the save file. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final Path filePath;

    /**
     * Creates storage that uses the application's default save-file location.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage using the given save-file location.
     * This constructor supports isolated automated tests.
     *
     * @param filePath the location of the save file
     */
    Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks the tasks to save
     * @throws KokoException if the file cannot be written
     */
    public void save(List<Task> tasks) throws KokoException {
        try {
            Files.createDirectories(filePath.getParent());

            List<String> lines = new ArrayList<>();

            for (Task task : tasks) {
                lines.add(toFileFormat(task));
            }

            Files.write(filePath, lines);
        } catch (IOException exception) {
            throw new KokoException("I couldn't save your tasks.");
        }
    }

    /**
     * Loads tasks from the data file.
     * If the file does not exist yet, an empty task list is returned.
     *
     * @return the tasks loaded from the file
     * @throws KokoException if the file cannot be read or is corrupted
     */
    public List<Task> load() throws KokoException {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    tasks.add(fromFileFormat(line));
                }
            }

            return tasks;
        } catch (IOException exception) {
            throw new KokoException("I couldn't load your saved tasks.");
        }
    }

    /**
     * Converts a task into the format used in the save file.
     *
     * @param task the task to convert
     * @return the saved representation of the task
     */
    private String toFileFormat(Task task) {
        assert task != null : "Only real tasks can be saved.";
        String status = task.getStatusIcon().equals("X") ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + status + " | " + task.getDescription();
        }

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;

            return "D | " + status + " | "
                    + task.getDescription() + " | "
                    + deadline.getBy().format(DATE_TIME_FORMAT);
        }

        assert task instanceof Event : "Saved tasks must be to-dos, deadlines, or events.";
        Event event = (Event) task;

        return "E | " + status + " | "
                + task.getDescription() + " | "
                + event.getFrom().format(DATE_TIME_FORMAT) + " | "
                + event.getTo().format(DATE_TIME_FORMAT);
    }

    /**
     * Converts one saved line back into a koko.Task.
     *
     * @param line one line from the data file
     * @return the reconstructed task
     * @throws KokoException if the line does not have the expected format
     */
    private Task fromFileFormat(String line) throws KokoException {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3) {
            throw new KokoException("The saved task file is corrupted.");
        }

        String type = parts[0];
        String status = parts[1];

        if (!status.equals("0") && !status.equals("1")) {
            throw new KokoException("The saved task file is corrupted.");
        }

        try {
            Task task;

            switch (type) {
                case "T":
                    if (parts.length != 3) {
                        throw new KokoException("The saved task file is corrupted.");
                    }

                    task = new Todo(parts[2]);
                    break;

                case "D":
                    if (parts.length != 4) {
                        throw new KokoException("The saved task file is corrupted.");
                    }

                    LocalDateTime by = LocalDateTime.parse(parts[3], DATE_TIME_FORMAT);
                    task = new Deadline(parts[2], by);
                    break;

                case "E":
                    if (parts.length != 5) {
                        throw new KokoException("The saved task file is corrupted.");
                    }

                    LocalDateTime from = LocalDateTime.parse(parts[3], DATE_TIME_FORMAT);
                    LocalDateTime to = LocalDateTime.parse(parts[4], DATE_TIME_FORMAT);
                    task = new Event(parts[2], from, to);
                    break;

                default:
                    throw new KokoException("The saved task file is corrupted.");
            }

            if (status.equals("1")) {
                task.markAsDone();
            }

            return task;
        } catch (DateTimeParseException exception) {
            throw new KokoException("The saved task file is corrupted.");
        }
    }
}
