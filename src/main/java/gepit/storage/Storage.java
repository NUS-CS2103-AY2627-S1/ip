package gepit.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import gepit.GepitException;
import gepit.TaskList;
import gepit.task.Deadline;
import gepit.task.Event;
import gepit.task.Task;
import gepit.task.Todo;


/**
 * Handles loading tasks from and saving tasks to disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager for the specified file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves the specified task list to disk.
     *
     * @throws GepitException If the tasks cannot be saved.
     */
    public void save(TaskList tasks) throws GepitException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();

            for (Task task : tasks.getTasks()) {
                lines.add(task.toDataString());
            }

            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new GepitException("I couldn't save your tasks.");
        }
    }

    /**
     * Loads tasks from disk.
     *
     * @return Tasks loaded from the data file.
     * @throws GepitException If the tasks cannot be loaded.
     */
    public List<Task> load() throws GepitException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            List<Task> loadedTasks = new ArrayList<>();

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                loadedTasks.add(parseSavedTask(line));
            }

            return loadedTasks;
        } catch (IOException e) {
            throw new GepitException("I couldn't load your saved tasks.");
        }
    }

    private Task parseSavedTask(String line) throws GepitException {
        String[] parts = line.split(" \\| ");

        if (parts.length < 3) {
            throw new GepitException("Invalid task data: " + line);
        }

        String type = parts[0];
        String doneValue = parts[1];
        String description = parts[2];

        if (!doneValue.equals("0") && !doneValue.equals("1")) {
            throw new GepitException(
                    "Invalid task status in saved data: " + line);
        }

        Task task;

        try {
            switch (type) {
                case "T":
                    if (parts.length != 3) {
                        throw new GepitException(
                                "Invalid todo data: " + line);
                    }
                    task = new Todo(description);
                    break;

                case "D":
                    if (parts.length != 4) {
                        throw new GepitException(
                                "Invalid deadline data: " + line);
                    }
                    task = new Deadline(
                            description,
                            LocalDate.parse(parts[3]));
                    break;

                case "E":
                    if (parts.length != 5) {
                        throw new GepitException(
                                "Invalid event data: " + line);
                    }
                    LocalDate start = LocalDate.parse(parts[3]);
                    LocalDate end = LocalDate.parse(parts[4]);
                    if (!end.isAfter(start)) {
                        throw new GepitException(
                                "Invalid event dates in saved data: " + line);
                    }
                    task = new Event(description, start, end);
                    break;

                default:
                    throw new GepitException(
                            "Unknown task type in saved data: " + type);
            }
        } catch (DateTimeParseException e) {
            throw new GepitException(
                    "Invalid date in saved task: " + line);
        }

        assert task != null : "Parsed task must be initialised for a valid task type";

        if (doneValue.equals("1")) {
            task.markDone();
        }

        return task;
    }
}
