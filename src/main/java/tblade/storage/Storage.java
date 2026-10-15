package tblade.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import tblade.exception.TBladeException;
import tblade.task.Deadline;
import tblade.task.Event;
import tblade.task.Task;
import tblade.task.Todo;

/**
 * Saves the task list to, and loads it from, a file relative to the project directory.
 */
public class Storage {
    private final Path dataFile;

    /**
     * Creates a Storage that reads from and writes to the given file path.
     *
     * @param filePath path of the file used to persist tasks
     */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath);
    }

    /**
     * Loads stored tasks, returning an empty list when the application is run for the first time.
     * Invalid stored lines are ignored so that valid tasks remain available.
     *
     * @return the tasks read from storage
     * @throws TBladeException if the data file cannot be read
     */
    public List<Task> load() throws TBladeException {
        if (Files.notExists(dataFile)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(dataFile).stream()
                    .map(this::parseTask)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException exception) {
            throw new TBladeException("I could not read your saved tasks: " + exception.getMessage());
        }
    }

    /**
     * Writes all tasks to storage, creating the data directory when needed.
     *
     * @param tasks tasks to save
     * @throws TBladeException if the data file cannot be written
     */
    public void save(List<Task> tasks) throws TBladeException {
        List<String> lines = tasks.stream()
                .map(this::formatTask)
                .collect(Collectors.toList());
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(dataFile, lines);
        } catch (IOException exception) {
            throw new TBladeException("I could not save your tasks: " + exception.getMessage());
        }
    }

    /**
     * Recreates a task from one stored line, or returns {@code null} for a corrupted line.
     */
    private Task parseTask(String line) {
        try {
            String[] parts = line.split(" \\| ", -1);
            if (parts.length < 3) {
                return null;
            }
            Task task;
            switch (parts[0]) {
                case "T":
                    if (parts.length != 3) {
                        return null;
                    }
                    task = new Todo(parts[2]);
                    break;
                case "D":
                    if (parts.length != 4) {
                        return null;
                    }
                    task = new Deadline(parts[2], LocalDate.parse(parts[3]));
                    break;
                case "E":
                    if (parts.length != 5) {
                        return null;
                    }
                    task = new Event(parts[2], parts[3], parts[4]);
                    break;
                default:
                    return null;
            }
            if (parts[1].equals("1")) {
                task.markAsDone();
            } else if (!parts[1].equals("0")) {
                return null;
            }
            return task;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    /**
     * Converts a task into its pipe-delimited storage representation.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        switch (task.getType()) {
            case TODO:
                return "T | " + status + " | " + task.getDescription();
            case DEADLINE:
                assert task instanceof Deadline
                        : "a task reporting TaskType.DEADLINE must be a Deadline: only Deadline's constructor "
                        + "passes that type to Task's constructor";
                Deadline deadline = (Deadline) task;
                return "D | " + status + " | " + task.getDescription() + " | " + deadline.getBy();
            case EVENT:
                assert task instanceof Event
                        : "a task reporting TaskType.EVENT must be an Event: only Event's constructor "
                        + "passes that type to Task's constructor";
                Event event = (Event) task;
                return "E | " + status + " | " + task.getDescription() + " | "
                        + event.getFrom() + " | " + event.getTo();
            default:
                throw new IllegalStateException("Unsupported task type");
        }
    }
}
