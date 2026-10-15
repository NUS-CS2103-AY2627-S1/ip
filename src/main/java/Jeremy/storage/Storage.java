package jeremy.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jeremy.exception.JeremyException;
import jeremy.task.Deadline;
import jeremy.task.Event;
import jeremy.task.Task;
import jeremy.task.Todo;

/** Deals with loading tasks from the data file and saving tasks to it. */
public class Storage {
    private final Path dataFile;

    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "Storage path must be provided";
        try {
            this.dataFile = Paths.get(filePath);
        } catch (InvalidPathException | NullPointerException e) {
            throw new IllegalArgumentException("The storage path is invalid.", e);
        }
    }

    /**
     * Loads tasks from the data file. Returns an empty list if the
     * file doesn't exist yet. Individual corrupted lines are skipped
     * with a warning; a total read failure throws JeremyException.
     */
    public List<Task> load() throws JeremyException {
        List<Task> loadedTasks = new ArrayList<>();
        try {
            if (!Files.exists(dataFile)) {
                return loadedTasks;
            }

            try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    loadedTasks.add(parseSavedTask(line));
                } catch (JeremyException e) {
                    System.out.println(" Warning: I skipped a corrupted saved task.");
                }
            }
            }
        } catch (IOException | SecurityException e) {
            throw new JeremyException("I couldn't load your saved tasks.");
        }

        return loadedTasks;
    }

    /** Saves the given tasks to the data file, overwriting whatever was there before. */
    public void save(List<Task> tasks) {
        assert tasks != null : "Tasks to save must not be null";
        try {
            if (dataFile.getParent() != null) {
                Files.createDirectories(dataFile.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(dataFile, StandardCharsets.UTF_8)) {
                for (Task task : tasks) {
                    writer.write(encode(task));
                    writer.newLine();
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println(" Warning: I couldn't save your tasks.");
        }
    }

    private String encode(Task task) {
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D|" + (task.isDone() ? "1" : "0") + "|" + task.getDescription() + "|" + deadline.getBy();
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return "E|" + (task.isDone() ? "1" : "0") + "|" + task.getDescription()
                    + "|" + event.getFrom() + "|" + event.getTo();
        } else {
            return "T|" + (task.isDone() ? "1" : "0") + "|" + task.getDescription();
        }
    }

    private Task parseSavedTask(String line) throws JeremyException {
        assert line != null : "Saved task line must not be null";
        String[] parts = line.split("\\|", -1);

        if (parts.length < 3) {
            throw new JeremyException("Invalid saved task.");
        }

        String type = parts[0];
        String status = parts[1];
        String description = parts[2];

        if (!status.equals("0") && !status.equals("1")) {
            throw new JeremyException("Invalid task status.");
        }
        if (description.isEmpty()) {
            throw new JeremyException("Empty task description.");
        }

        Task task;
        switch (type) {
        case "T":
            if (parts.length != 3) {
                throw new JeremyException("Invalid todo format.");
            }
            task = new Todo(description);
            break;
        case "D":
            if (parts.length != 4 || parts[3].isEmpty()) {
                throw new JeremyException("Invalid deadline format.");
            }
            task = new Deadline(description, parts[3]);
            break;
        case "E":
            if (parts.length != 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
                throw new JeremyException("Invalid event format.");
            }
            task = new Event(description, parts[3], parts[4]);
            break;
        default:
            throw new JeremyException("Unknown saved task type.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
