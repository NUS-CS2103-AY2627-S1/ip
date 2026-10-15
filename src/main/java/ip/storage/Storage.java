
package ip.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ip.collection.TaskList;
import ip.exception.TabbyException;
import ip.model.Deadline;
import ip.model.Event;
import ip.model.Task;
import ip.model.Todo;
import ip.parser.ParsedDateTime;
import ip.parser.Parser;

/** Reads and writes tasks using the application's text-file format. */
public class Storage {

    private static final String FILE_DELIMITER = " \\| ";
    private final Path filePath;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePathStr) {
        assert filePathStr != null && !filePathStr.isBlank() : "Storage requires a file path";
        this.filePath = Paths.get(filePathStr);
    }

    /** Loads valid tasks from disk, returning an empty list when the file is absent. */
    public List<Task> load() throws TabbyException {
        List<Task> loadedTasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                Task task = parseTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new TabbyException("Could not load saved tasks.");
        }
        return loadedTasks;
    }

    /** Saves all tasks to disk, creating the parent directory when necessary. */
    public void save(TaskList tasks) throws TabbyException {
        assert tasks != null : "Storage can only save a task list";
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                lines.add(tasks.get(i).toFileFormat());
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new TabbyException("Unable to save tasks to file.");
        }
    }

    /** Converts one storage line into a task, or returns null for malformed data. */
    private Task parseTask(String line) {
        assert line != null : "Storage lines must not be null";
        String[] parts = line.split(FILE_DELIMITER);
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task = null;
        try {
            switch (type) {
                case "T" ->
                    task = new Todo(description);
                case "D" -> {
                    if (parts.length >= 4) {
                        ParsedDateTime by = Parser.parseDateTime(parts[3]);
                        task = new Deadline(description, by);
                    }
                }
                case "E" -> {
                    if (parts.length >= 5) {
                        ParsedDateTime from = Parser.parseDateTime(parts[3]);
                        ParsedDateTime to = Parser.parseDateTime(parts[4]);
                        task = new Event(description, from, to);
                    }
                }
                default -> {
                }
            }
        } catch (TabbyException e) {
            return null;
        }

        if (task != null && isDone) {
            task.markAsDone();
        }
        assert task == null || task.getDescription() != null : "Loaded tasks must have descriptions";
        return task;
    }
}
