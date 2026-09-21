package es;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves the task list to disk and reloads it when Es starts.
 * The file path is relative and built with {@link Path#of(String, String...)},
 * so it works across operating systems.
 */
public class Storage {
    private static final Path DATA_FILE = Path.of("data", "es.txt");

    /**
     * Loads saved tasks from {@code data/es.txt}.
     * A missing folder or file is treated as an empty list so a first run still works.
     *
     * @return the tasks stored on disk, or an empty list if none have been saved yet
     * @throws EsException if the file cannot be read or a line is not in the expected format
     */
    public ArrayList<Task> load() throws EsException {
        if (!Files.exists(DATA_FILE)) {
            return new ArrayList<>();
        }
        if (!Files.isRegularFile(DATA_FILE)) {
            throw new EsException("The saved task list is corrupted.");
        }

        try {
            List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
            ArrayList<Task> tasks = new ArrayList<>();
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                tasks.add(parseTask(line));
            }
            return tasks;
        } catch (IOException e) {
            throw new EsException("Unable to read the saved task list.");
        }
    }

    /**
     * Writes the current task list to {@code data/es.txt}, creating the {@code data}
     * folder when it does not exist yet.
     *
     * @param tasks the tasks to save
     * @throws EsException if the folder or file cannot be written
     */
    public void save(ArrayList<Task> tasks) throws EsException {
        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toStorageLine());
            }
            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EsException("Unable to save the task list.");
        }
    }

    /**
     * Converts one saved line into a task.
     * Expected formats:
     * {@code T | 1 | read book},
     * {@code D | 0 | return book | June 6th},
     * {@code E | 0 | project meeting | Aug 6th 2pm | 4pm}.
     *
     * @param line a non-blank line from the data file
     * @return the reconstructed task
     * @throws EsException if the line does not match the expected format
     */
    private Task parseTask(String line) throws EsException {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw new EsException("The saved task list is corrupted.");
        }

        TaskType type = TaskType.fromIcon(parts[0]);
        boolean isDone = parseDoneFlag(parts[1]);
        String description = parts[2].trim();
        if (description.isEmpty()) {
            throw new EsException("The saved task list is corrupted.");
        }

        ArrayList<String> legacyTags = new ArrayList<>();
        if (parts.length == 3) {
            String[] words = description.split("\\s+");
            StringBuilder cleanDescription = new StringBuilder();
            for (String word : words) {
                if (word.startsWith("#")) {
                    legacyTags.add(word);
                } else {
                    if (cleanDescription.length() > 0) {
                        cleanDescription.append(' ');
                    }
                    cleanDescription.append(word);
                }
            }
            description = cleanDescription.toString();
        }
        Task task;
        switch (type) {
        case TODO:
            if (parts.length != 3 && parts.length != 4) {
                throw new EsException("The saved task list is corrupted.");
            }
            task = new Todo(description);
            break;
        case DEADLINE:
            if ((parts.length != 4 && parts.length != 5) || parts[3].trim().isEmpty()) {
                throw new EsException("The saved task list is corrupted.");
            }
            task = new Deadline(description, parts[3].trim());
            break;
        case EVENT:
            if ((parts.length != 5 && parts.length != 6) || parts[3].trim().isEmpty() || parts[4].trim().isEmpty()) {
                throw new EsException("The saved task list is corrupted.");
            }
            task = new Event(description, parts[3].trim(), parts[4].trim());
            break;
        default:
            throw new EsException("The saved task list is corrupted.");
        }

        if (isDone) {
            task.markAsDone();
        }
        int tagIndex = type == TaskType.TODO ? 3 : type == TaskType.DEADLINE ? 4 : 5;
        if (parts.length > tagIndex && !parts[tagIndex].trim().isEmpty()) {
            for (String tag : parts[tagIndex].trim().split(",")) {
                task.addTag(tag.trim());
            }
        }
        for (String tag : legacyTags) {
            task.addTag(tag);
        }
        return task;
    }

    /**
     * Reads the {@code 0} or {@code 1} completion flag from a saved line.
     *
     * @param flag the saved completion field
     * @return {@code true} when the flag is {@code 1}
     * @throws EsException if the flag is not {@code 0} or {@code 1}
     */
    private boolean parseDoneFlag(String flag) throws EsException {
        if (flag.equals("1")) {
            return true;
        }
        if (flag.equals("0")) {
            return false;
        }
        throw new EsException("The saved task list is corrupted.");
    }
}
