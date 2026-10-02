package florkofcows.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import florkofcows.task.Deadline;
import florkofcows.task.Event;
import florkofcows.task.Task;
import florkofcows.task.Todo;

/**
 * Handles saving tasks to disk and loading them back on startup.
 * Tasks are persisted as one pipe-delimited line per task under ./data/.
 */
public class Storage {
    private final Path filePath;

    public Storage() {
        this(Path.of(System.getProperty("user.dir"), "data", "florkofcows.txt"));
    }

    public Storage(String filePath) {
        this(Path.of(filePath));
    }

    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given tasks to disk, overwriting any previous contents.
     * Creates the data folder if it does not already exist.
     *
     * @param tasks the tasks to save.
     * @throws IOException if the file or folder cannot be written to.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Task task : tasks) {
                writer.write(task.toSaveFormat());
                writer.newLine();
            }
        }
    }

    /**
     * Loads tasks from disk. If the data file or folder does not exist yet
     * (e.g. on a first run), returns an empty list instead of failing.
     * Individual corrupted lines are skipped with a warning rather than
     * aborting the entire load.
     *
     * @return the list of tasks loaded from disk, or an empty list if none exist yet.
     * @throws IOException if the file exists but cannot be read for a reason
     *                      other than it being missing.
     */
    public ArrayList<Task> load() throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (NoSuchFileException e) {
            return new ArrayList<>();
        }

        ArrayList<Task> tasks = new ArrayList<>();
        int skippedCount = 0;
        for (String line : lines) {
            try {
                tasks.add(parseLine(line));
            } catch (IllegalArgumentException e) {
                skippedCount++;
            }
        }
        if (skippedCount > 0) {
            System.out.println("FLORKALERT! Careful ah, got " + skippedCount
                    + " corrupted line(s) in save file.");
        }
        return tasks;
    }

    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Not enough fields: " + line);
        }
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        String tagsField = "";
        Task task;
        switch (type) {
            case "T":
                if (parts.length >= 4) {
                    tagsField = parts[3];
                }
                task = new Todo(description);
                break;
            case "D":
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Deadline missing '/by' field: " + line);
                }
                String by = parts[3];
                if (parts.length >= 5) {
                    tagsField = parts[4];
                }
                task = new Deadline(description, by);
                break;
            case "E":
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Event missing '/from' or '/to' field: " + line);
                }
                String from = parts[3];
                String to = parts[4];
                if (parts.length >= 6) {
                    tagsField = parts[5];
                }
                task = new Event(description, from, to);
                break;
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }

        if (isDone) {
            task.markAsDone();
        }
        task.setTagsFromSaveFormat(tagsField);
        return task;
    }
}
