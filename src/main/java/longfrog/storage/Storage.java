package longfrog.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import longfrog.exception.LongfrogException;
import longfrog.task.Deadline;
import longfrog.task.Event;
import longfrog.task.Task;
import longfrog.task.Todo;
import longfrog.util.FormatUtils;

/** Persists tasks to and restores tasks from the application's save file. */
public class Storage {
    private final String filePath;

    /**
     * Creates storage backed by the supplied file path.
     *
     * @param filePath the path of the save file
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the file. Creates directory and file if they do not exist.
     *
     * @return the tasks loaded from storage
     * @throws LongfrogException if the save file cannot be read or created
     */
    public List<Task> load() throws LongfrogException {
        List<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        try {
            // Create parent directories if missing (e.g. data/)
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            // If file doesn't exist, create an empty one and return empty list
            if (!file.exists()) {
                file.createNewFile();
                return tasks;
            }

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    Task task = parseLine(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new LongfrogException("Unable to load save file.");
        }

        return tasks;
    }

    /**
     * Parses a single line from the file into a Task object.
     *
     * @param line The raw line from the save file.
     * @return The reconstructed Task object, or null if the line is corrupted/invalid.
     */
    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String name = parts[2];

        try {
            Task task = null;
            switch (type) {
                case "T":
                    if (parts.length == 3) {
                        task = new Todo(name);
                    }
                    break;
                case "D":
                    if (parts.length == 4) {
                        LocalDateTime by = LocalDateTime.parse(parts[3], FormatUtils.INPUT_SAVE_FORMAT);
                        task = new Deadline(name, by);
                    }
                    break;
                case "E":
                    if (parts.length == 5) {
                        LocalDateTime from = LocalDateTime.parse(parts[3], FormatUtils.INPUT_SAVE_FORMAT);
                        LocalDateTime to = LocalDateTime.parse(parts[4], FormatUtils.INPUT_SAVE_FORMAT);
                        if (from.isBefore(to)) {
                            task = new Event(name, from, to);
                        }
                    }
                    break;
                default:
                    return null;
            }

            if (task != null && isDone) {
                task.markAsDone();
            }

            return task;

        } catch (DateTimeParseException e) {
            // Skip corrupted entries so valid saved tasks can still be recovered.
            return null;
        }
    }

    /**
     * Overwrites the file with the current list of tasks.
     *
     * @param tasks the tasks to save
     * @return whether the tasks were saved successfully
     */
    public boolean save(List<Task> tasks) {
        assert tasks != null : "Tasks to save must be supplied by the task list";

        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    assert task != null : "Task list must not contain null entries";
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
