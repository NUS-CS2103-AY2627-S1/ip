package shrek.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shrek.task.Deadline;
import shrek.task.Event;
import shrek.task.Task;
import shrek.task.Todo;

/**
 * Handles loading tasks from and saving tasks to the hard disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a new Storage pointing at the given relative file path.
     *
     * @param relativePath the relative path to the data file.
     */
    public Storage(String relativePath) {
        assert relativePath != null && !relativePath.isBlank()
                : "Storage requires a non-blank file path.";
        this.filePath = Paths.get(relativePath);
    }

    /**
     * Loads tasks from the data file. If the file or its parent folder
     * doesn't exist, returns an empty list instead of failing.
     *
     * @return the list of tasks loaded from disk.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = filePath.toFile();

        if (!file.exists()) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                Task task = parseLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: could not read the data file. Starting with an empty list.");
        }

        return tasks;
    }

    /**
     * Parses a single saved line into a Task. Returns null if the line
     * is corrupted or malformed, so it can be skipped rather than crash.
     *
     * @param line the raw line read from the data file.
     * @return the parsed task, or null if the line couldn't be parsed.
     */
    private Task parseLine(String line) {
        try {
            assert line != null : "A saved task line must not be null.";
            String[] parts = line.split(" \\| ", -1);
            // Save files are external input, so malformed records are rejected normally.
            if (parts.length < 3) {
                return null;
            }
            String type = parts[0].trim();
            String doneFlag = parts[1].trim();
            if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
                return null;
            }
            // The guard above establishes the only status values understood by the format.
            assert doneFlag.equals("0") || doneFlag.equals("1")
                    : "A saved task must use 0 or 1 for its status.";
            boolean isDone = doneFlag.equals("1");
            String description = parts[2].trim();
            if (description.isEmpty()) {
                return null;
            }

            Task task;
            switch (type) {
                case "T":
                    ArrayList<String> todoTags = parseStoredTags(parts, 3);
                    if (todoTags == null) {
                        return null;
                    }
                    task = new Todo(description, todoTags);
                    break;
                case "D":
                    if (parts.length < 4) {
                        return null;
                    }
                    assert parts.length >= 4 : "A deadline record must contain a due date.";
                    ArrayList<String> deadlineTags = parseStoredTags(parts, 4);
                    if (deadlineTags == null) {
                        return null;
                    }
                    LocalDate by = LocalDate.parse(parts[3].trim());
                    task = new Deadline(description, by, deadlineTags);
                    break;
                case "E":
                    if (parts.length < 5) {
                        return null;
                    }
                    assert parts.length >= 5 : "An event record must contain two times.";
                    String from = parts[3].trim();
                    String to = parts[4].trim();
                    if (from.isEmpty() || to.isEmpty()) {
                        return null;
                    }
                    ArrayList<String> eventTags = parseStoredTags(parts, 5);
                    if (eventTags == null) {
                        return null;
                    }
                    task = new Event(description, from, to, eventTags);
                    break;
                default:
                    return null;
            }

            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException | IllegalArgumentException e) {
            // Corrupted or malformed line — skip it rather than crash.
            return null;
        }
    }

    /**
     * Parses the optional comma-separated tag field of a saved record.
     *
     * @param parts the fields in the saved record.
     * @param firstOptionalField the index after the task's required fields.
     * @return the parsed tags, an empty list for an old-format record, or null for an invalid shape.
     */
    private ArrayList<String> parseStoredTags(String[] parts, int firstOptionalField) {
        if (parts.length == firstOptionalField) {
            return new ArrayList<>();
        }
        if (parts.length != firstOptionalField + 1) {
            return null;
        }
        String tagField = parts[firstOptionalField].trim();
        if (tagField.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(tagField.split(",", -1)));
    }

    /**
     * Saves the given list of tasks to the data file, creating the
     * parent folder first if it doesn't exist.
     *
     * @param tasks the tasks to save.
     */
    public void save(ArrayList<Task> tasks) {
        assert tasks != null : "Storage cannot save a null task list.";
        try {
            File parentDir = filePath.toFile().getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                for (Task task : tasks) {
                    assert task != null : "Storage cannot save a null task.";
                    writer.write(task.toSaveFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: could not save tasks to disk.");
        }
    }
}
