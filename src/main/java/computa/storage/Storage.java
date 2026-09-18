package computa.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import computa.task.Deadline;
import computa.task.Event;
import computa.task.Task;
import computa.task.Todo;
import computa.util.DateTimeParser;

/**
 * Stores Computa tasks in a text file.
 */
public class Storage {
    /** Path to the task data file. */
    private final String filePath;

    /**
     * Creates a storage object for the specified file.
     *
     * @param filePath path to the task data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /** Creates the parent directory and data file when they do not exist. */
    public void initialiseDataFile() {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException | SecurityException exception) {
            System.out.println("An error occurred while creating the data file.");
        }
    }

    /**
     * Writes the current task list to the data file, replacing its previous contents.
     *
     * @param tasks tasks to write.
     */
    public void saveTasks(ArrayList<Task> tasks) {
        assert tasks != null : "Task list must be provided";
        initialiseDataFile();
        try (BufferedWriter writer = Files.newBufferedWriter(new File(filePath).toPath(), StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                writer.write(task.toFileFormat());
                writer.newLine();
            }
        } catch (IOException | InvalidPathException | SecurityException exception) {
            System.out.println("Something went wrong while saving tasks: " + exception.getMessage());
        }
    }

    /**
     * Loads valid task records from the data file.
     * Missing files, unreadable files, blank lines, and malformed records are treated as empty or
     * skipped so that startup can continue safely.
     *
     * @return tasks reconstructed from valid records.
     */
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        try {
            File file = new File(filePath);
            if (!file.isFile()) {
                return loadedTasks;
            }
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            loadedTasks.addAll(lines.stream()
                    .map(this::parseTask)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        } catch (IOException | InvalidPathException | SecurityException exception) {
            // Return the valid records collected so far when the file cannot be read completely.
        }
        return loadedTasks;
    }

    /**
     * Parses one serialized task record and rejects malformed content.
     *
     * @param line serialized task record.
     * @return the reconstructed task, or {@code null} when the record is invalid.
     */
    private Task parseTask(String line) {
        if (line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3 || !(parts[1].equals("0") || parts[1].equals("1"))) {
            return null;
        }

        String description = parts[2].trim();
        if (description.isEmpty()) {
            return null;
        }

        Task task;
        switch (parts[0].trim()) {
            case "T":
                if (parts.length != 3) {
                    return null;
                }
                task = new Todo(description);
                break;
            case "D":
                if (parts.length != 4 || parts[3].trim().isEmpty()) {
                    return null;
                }
                if (DateTimeParser.looksLikeDate(parts[3]) && DateTimeParser.parse(parts[3]) == null) {
                    return null;
                }
                task = new Deadline(description, parts[3].trim());
                break;
            case "E":
                if (parts.length != 5 || parts[3].trim().isEmpty() || parts[4].trim().isEmpty()) {
                    return null;
                }
                if ((DateTimeParser.looksLikeDate(parts[3]) && DateTimeParser.parse(parts[3]) == null)
                        || (DateTimeParser.looksLikeDate(parts[4]) && DateTimeParser.parse(parts[4]) == null)) {
                    return null;
                }
                LocalDateTime parsedFrom = DateTimeParser.parse(parts[3]);
                LocalDateTime parsedTo = DateTimeParser.parse(parts[4]);
                if (parsedFrom != null && parsedTo != null && parsedFrom.isAfter(parsedTo)) {
                    return null;
                }
                task = new Event(description, parts[3].trim(), parts[4].trim());
                break;
            default:
                return null;
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
