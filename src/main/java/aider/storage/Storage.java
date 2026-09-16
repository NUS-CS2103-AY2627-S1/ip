package aider.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

import aider.AiderException;
import aider.model.Deadline;
import aider.model.Event;
import aider.model.Task;
import aider.model.Todo;
import aider.util.DateTimeParser;

/**
 * Handles loading tasks from and saving tasks to the hard disk.
 */
public class Storage {
    /** The relative path of the file used to persist tasks. */
    private final String filePath;

    /**
     * Creates a storage handler that uses the given file path.
     *
     * @param filePath the relative path of the data file
     * @throws IllegalArgumentException if the file path is null or blank
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("A data file path must be provided.");
        }
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file. If the file or its parent directory does not
     * exist, an empty list is returned. Malformed lines are skipped (with a warning)
     * so that one corrupt entry never discards the tasks that could still be read.
     *
     * @return the list of tasks loaded from disk
     * @throws AiderException if the file cannot be read at all
     */
    public ArrayList<Task> load() throws AiderException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }
        if (!file.isFile()) {
            throw new AiderException("Data path exists but is not a file: " + filePath);
        }
        if (!file.canRead()) {
            throw new AiderException("Cannot read the data file: " + filePath);
        }

        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Strip a UTF-8 byte-order mark if the file was written by a Windows editor.
                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                // Handle extremely long lines that could cause memory issues
                if (line.length() > 10000) {
                    System.err.println("Warning: skipping extremely long task line " + lineNumber
                            + " in " + filePath + ": line too long");
                    continue;
                }
                try {
                    Task task = parseLine(line);
                    if (tasks.stream().anyMatch(existing -> existing.toFileString()
                            .equals(task.toFileString()))) {
                        System.err.println("Warning: skipped duplicate task at line " + lineNumber
                                + " in " + filePath);
                        continue;
                    }
                    tasks.add(task);
                } catch (AiderException exception) {
                    System.err.println("Warning: skipped malformed task at line " + lineNumber
                            + " in " + filePath + ": " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            throw new AiderException("Could not read the data file: " + exception.getMessage());
        }
        return tasks;
    }

    /**
     * Saves the given tasks to the data file, creating the parent directory if needed.
     * Tasks are written to a temporary file first and then moved into place, so a
     * failure never leaves the previous data file damaged.
     *
     * @param tasks the tasks to persist to disk
     * @throws AiderException if the file cannot be written
     */
    public void save(ArrayList<Task> tasks) throws AiderException {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }

        File file = new File(filePath);
        File parent = file.getParentFile();
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new AiderException("Data path exists but is not a file: " + filePath);
            }
            if (!file.canWrite()) {
                throw new AiderException("Cannot write to the data file: " + filePath);
            }
        }
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new AiderException("Could not create the data directory: " + parent.getPath());
        }
        if (parent != null && !parent.isDirectory()) {
            throw new AiderException("Data parent path is not a directory: " + parent.getPath());
        }
        if (parent != null && !parent.canWrite()) {
            throw new AiderException("Cannot write to the data directory: " + parent.getPath());
        }

        File temp;
        try {
            temp = File.createTempFile("aider-save", ".tmp",
                    parent != null ? parent : new File(System.getProperty("user.dir")));
        } catch (IOException exception) {
            throw new AiderException("Could not create a temporary file for saving: "
                    + exception.getMessage());
        }

        try (FileWriter writer = new FileWriter(temp)) {
            for (Task task : tasks) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        } catch (IOException exception) {
            temp.delete();
            throw new AiderException("Could not save the data file: " + exception.getMessage());
        }

        try {
            Files.move(temp.toPath(), file.toPath(),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            temp.delete();
            throw new AiderException("Could not finalize the data file save: "
                    + exception.getMessage());
        }
    }

    /**
     * Parses a single line from the data file into a Task.
     *
     * <p>The description is reconstructed by joining every field between the done
     * flag and the type-specific trailing fields, so descriptions that themselves
     * contain the " | " separator round-trip correctly.
     *
     * @param line the line to parse
     * @return the parsed task
     * @throws AiderException if the line does not match a known task format
     */
    private Task parseLine(String line) throws AiderException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            throw new AiderException("Could not parse saved task: " + line);
        }

        String type = parts[0].trim();
        String status = parts[1].trim();
        if (!status.equals("0") && !status.equals("1")) {
            throw new AiderException("Invalid completion flag in save file: " + status);
        }
        boolean done = status.equals("1");

        Task task;
        switch (type) {
            case "T":
                task = new Todo(requireDescription(join(parts, 2, parts.length), line));
                break;
            case "D":
                if (parts.length < 4) {
                    throw new AiderException("Could not parse saved deadline: " + line);
                }
                task = new Deadline(requireDescription(join(parts, 2, parts.length - 1), line),
                        DateTimeParser.parse(parts[parts.length - 1]), parts[parts.length - 1]);
                break;
            case "E":
                if (parts.length < 5) {
                    throw new AiderException("Could not parse saved event: " + line);
                }
                task = new Event(requireDescription(join(parts, 2, parts.length - 2), line),
                        DateTimeParser.parse(parts[parts.length - 2]),
                        DateTimeParser.parse(parts[parts.length - 1]),
                        parts[parts.length - 2], parts[parts.length - 1]);
                break;
            default:
                throw new AiderException("Unknown task type in save file: " + type);
        }

        if (done) {
            task.markAsDone();
        }
        return task;
    }

    /** Ensures a saved task has text that can be shown to the user. */
    private static String requireDescription(String description, String line) throws AiderException {
        if (description.trim().isEmpty()) {
            throw new AiderException("Saved task has no description: " + line);
        }
        return description;
    }

    /**
     * Joins a range of fields from a split line back together with the " | " separator.
     *
     * @param parts the fields produced by splitting a line on " | "
     * @param from the index of the first field to include (inclusive)
     * @param to the index of the first field to exclude (exclusive)
     * @return the joined field text
     */
    private static String join(String[] parts, int from, int to) {
        StringBuilder builder = new StringBuilder();
        for (int i = from; i < to; i++) {
            if (i > from) {
                builder.append(" | ");
            }
            builder.append(parts[i]);
        }
        return builder.toString();
    }
}
