package tt.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tt.task.Deadline;
import tt.task.Event;
import tt.task.Task;
import tt.task.Todo;

/**
 * Reads and writes tt.TT tasks in a simple, human-readable text format.
 */
public class TaskStorage {
    private final Path filePath;

    /**
     * Creates storage using the application's relative data file.
     */
    public TaskStorage() {
        this(Paths.get("data", "tt.TT.txt"));
    }

    /**
     * Creates storage at the supplied path, primarily useful for tests.
     *
     * @param filePath location of the task data file.
     */
    public TaskStorage(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath);
    }

    /**
     * Loads all tasks, returning an empty list when the save file is absent.
     *
     * @return tasks loaded from the save file.
     * @throws StorageException if the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                try {
                    tasks.add(parse(lines.get(i)));
                } catch (IllegalArgumentException | DateTimeParseException ignored) {
                    throw new StorageException("The save file contains invalid data on line " + (i + 1)
                            + ". Fix or remove that line, then restart tt.TT.");
                }
            }
        } catch (IOException e) {
            throw new StorageException("I couldn't read the save file at " + filePath
                    + ". Check that the file is readable, then restart tt.TT.", e);
        }
        return tasks;
    }

    /**
     * Saves the complete task list, creating its parent directory when necessary.
     *
     * @param tasks complete task list to save.
     * @throws StorageException if the tasks cannot be written.
     */
    public void save(List<Task> tasks) {
        Objects.requireNonNull(tasks);
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(serialize(task));
        }

        try {
            Path absoluteFilePath = filePath.toAbsolutePath();
            Path parent = absoluteFilePath.getParent();
            Files.createDirectories(parent);
            String temporaryFilePrefix = absoluteFilePath.getFileName() + "---";
            Path temporaryFile = Files.createTempFile(parent, temporaryFilePrefix, ".tmp");
            try {
                Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
                moveIntoPlace(temporaryFile, absoluteFilePath);
            } finally {
                Files.deleteIfExists(temporaryFile);
            }
        } catch (IOException e) {
            throw new StorageException("I couldn't save tasks to " + filePath
                    + ". Check that the location is writable, then restart tt.TT.", e);
        }
    }

    private void moveIntoPlace(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Saves the supplied tasks, providing a concise API for a small number of tasks.
     *
     * @param tasks tasks to save.
     */
    public void save(Task... tasks) {
        save(List.of(tasks));
    }

    private Task parse(String line) {
        String[] fields = line.split("\\|", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        if (fields.length < 3 || !isValidCompletionStatus(fields[1])) {
            throw new IllegalArgumentException();
        }
        Task task = switch (fields[0]) {
            case "T" -> fields.length == 3 ? new Todo(fields[2]) : null;
            case "D" -> fields.length == 4 ? new Deadline(fields[2], LocalDate.parse(fields[3])) : null;
            case "E" -> fields.length == 5 ? new Event(fields[2], fields[3], fields[4]) : null;
            default -> null;
        };
        if (task == null || task.getTask().isEmpty() || hasBlankEventTime(task)) {
            throw new IllegalArgumentException();
        }
        if (fields[1].equals("1")) {
            task.mark();
        }
        return task;
    }

    private boolean hasBlankEventTime(Task task) {
        return task instanceof Event event && (event.getFrom().isEmpty() || event.getTo().isEmpty());
    }

    private boolean isValidCompletionStatus(String status) {
        return status.equals("0") || status.equals("1");
    }

    private String serialize(Task task) {
        String line = getTaskTypeCode(task) + " | "
                + (task.isDone() ? "1" : "0")
                + " | " + task.getTask();

        if (task instanceof Deadline deadline) {
            line += " | " + deadline.getBy();
        } else if (task instanceof Event event) {
            line += " | " + event.getFrom()
                    + " | " + event.getTo();
        }
        return line;
    }

    private String getTaskTypeCode(Task task) {
        if (task instanceof Todo) {
            return "T";
        } else if (task instanceof Deadline) {
            return "D";
        } else if (task instanceof Event) {
            return "E";
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }
}
