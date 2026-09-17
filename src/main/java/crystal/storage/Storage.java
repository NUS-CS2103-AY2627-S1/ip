package crystal.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import crystal.exception.CrystalException;
import crystal.task.Deadline;
import crystal.task.Event;
import crystal.task.Task;
import crystal.task.Todo;
import crystal.task.WithinPeriod;

/**
 * Loads and saves Crystal's tasks using a file on the hard disk.
 */
public final class Storage {
    /** Location of Crystal's task data. */
    private final Path dataFilePath;

    /**
     * Creates storage backed by the supplied file path.
     *
     * @param dataFilePath relative path to Crystal's task data.
     */
    public Storage(Path dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    /**
     * Loads the task list from disk, or returns an empty list when no data file exists yet.
     *
     * @return tasks reconstructed from the data file.
     * @throws CrystalException if the task data cannot be read or is invalid.
     */
    public ArrayList<Task> loadTasks() throws CrystalException {
        try {
            if (Files.notExists(dataFilePath)) {
                return new ArrayList<>();
            }

            ArrayList<Task> tasks = new ArrayList<>();
            for (String taskLine : Files.readAllLines(dataFilePath, StandardCharsets.UTF_8)) {
                tasks.add(parseTask(taskLine));
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new CrystalException("I couldn't load your tasks from the hard disk.");
        }
    }

    /**
     * Rewrites the data file with the current task list, creating its directory if needed.
     *
     * @param tasks tasks to save.
     * @throws CrystalException if the task data cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws CrystalException {
        try {
            Path parentDirectory = dataFilePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            List<String> taskLines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            Files.write(dataFilePath, taskLines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new CrystalException("I couldn't save your tasks to the hard disk.");
        }
    }

    /**
     * Reconstructs one task from its pipe-separated storage representation.
     *
     * @param taskLine one line from the data file.
     * @return reconstructed task.
     * @throws CrystalException if the line does not use Crystal's storage format.
     */
    private static Task parseTask(String taskLine) throws CrystalException {
        String[] fields = taskLine.split(" \\| ", -1);
        if (fields.length < 3 || Arrays.stream(fields).anyMatch(String::isBlank)) {
            throw invalidDataException();
        }

        Task task = switch (fields[0]) {
            case "T" -> fields.length == 3 ? new Todo(fields[2]) : null;
            case "D" -> fields.length == 4 ? new Deadline(fields[2], fields[3]) : null;
            case "E" -> fields.length == 5 ? new Event(fields[2], fields[3], fields[4]) : null;
            case "W" -> fields.length == 5
                    ? new WithinPeriod(fields[2], fields[3], fields[4]) : null;
            default -> null;
        };
        if (task == null || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw invalidDataException();
        }
        assert task != null : "Validated task data must produce a task";

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates the consistent user-facing exception used for malformed saved data.
     *
     * @return invalid-data exception.
     */
    private static CrystalException invalidDataException() {
        return new CrystalException("Your saved task data is invalid.");
    }
}
