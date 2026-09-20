package nexus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Stores tasks in a text file between Nexus sessions. */
public class Storage {
    /** Separator between fields in the persisted text format. */
    private static final String SEPARATOR = " | ";
    /** File used to preserve task data between sessions. */
    private final Path dataFile;

    /** Creates storage that uses the supplied data file. */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /** Loads valid saved tasks and counts records that could not be restored. */
    public LoadResult load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return new LoadResult(tasks, 0);
        }

        int skippedRecords = 0;
        for (String line : Files.readAllLines(dataFile)) {
            try {
                Task task = parseTask(line);
                if (task == null) {
                    skippedRecords++;
                } else {
                    tasks.add(task);
                }
            } catch (RuntimeException exception) {
                skippedRecords++;
            }
        }
        return new LoadResult(tasks, skippedRecords);
    }

    /** Saves every task to disk. */
    public void save(List<Task> tasks) throws IOException {
        Path parent = dataFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }
        Files.write(dataFile, lines);
    }

    private Task parseTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))
                || parts[2].isBlank()) {
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
                task = new Deadline(parts[2], parts[3]);
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
        }
        return task;
    }

    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D" + SEPARATOR + status + SEPARATOR + task.getDescription()
                    + SEPARATOR + deadline.getBy();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E" + SEPARATOR + status + SEPARATOR + task.getDescription()
                    + SEPARATOR + event.getFrom() + SEPARATOR + event.getTo();
        }
        return "T" + SEPARATOR + status + SEPARATOR + task.getDescription();
    }
}
