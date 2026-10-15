package botzilla.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import botzilla.BotzillaException;
import botzilla.task.DeadlineTask;
import botzilla.task.EventTask;
import botzilla.task.Task;
import botzilla.task.ToDoTask;

/**
 * Handles reading tasks from, and writing tasks to, a save file on disk.
 */
public class Storage {
    private String filePath;
    private int skippedLineCount;

    /**
     * Constructs a Storage bound to the given file path.
     *
     * @param filePath path to the save file
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the save file. Returns an empty list if the file
     * doesn't exist yet (e.g. first run). Lines that can't be parsed are
     * skipped rather than causing a failure; the number skipped is
     * recorded and can be retrieved via {@link #getSkippedLineCount()} so
     * the caller can let the user know some saved data was lost.
     *
     * @return the list of tasks loaded from disk
     * @throws BotzillaException if the file exists but can't be read (e.g.
     *                           the OS denies read access to it)
     */
    public ArrayList<Task> load() throws BotzillaException {
        ArrayList<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;
        File file = new File(filePath);

        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseLine(line);
                if (task != null) {
                    tasks.add(task);
                } else {
                    skippedLineCount++;
                }
            }
        } catch (IOException e) {
            throw new BotzillaException("Could not read saved tasks: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Returns the number of lines skipped by the most recent {@link #load()}
     * call because they couldn't be parsed (e.g. corrupted or manually
     * edited save file content), so the caller can warn the user that some
     * saved data was lost.
     *
     * @return the number of corrupted lines skipped during the last load
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Parses a single line from the save file into a Task, based on its
     * pipe-delimited fields (type, done flag, name, type-specific fields,
     * and an optional trailing comma-separated tags field). Returns null
     * if the line is malformed or has an unrecognized type code.
     *
     * @param line a single line from the save file
     * @return the parsed task, or null if the line couldn't be parsed
     */
    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String typeCode = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String name = parts[2].trim();

        Task task;
        String tagsField;
        try {
            switch (typeCode) {
                case "T":
                    task = new ToDoTask(name);
                    tagsField = parts.length >= 4 ? parts[3] : "";
                    break;
                case "D":
                    if (parts.length < 4) {
                        return null;
                    }
                    task = new DeadlineTask(name, parts[3].trim());
                    tagsField = parts.length >= 5 ? parts[4] : "";
                    break;
                case "E":
                    if (parts.length < 5) {
                        return null;
                    }
                    task = new EventTask(name, parts[3].trim(), parts[4].trim());
                    tagsField = parts.length >= 6 ? parts[5] : "";
                    break;
                default:
                    return null;
            }
        } catch (BotzillaException e) {
            return null;
        }

        if (isDone) {
            task.mark();
        }
        for (String tag : tagsField.split(",")) {
            if (!tag.isBlank()) {
                task.addTag(tag.trim());
            }
        }

        return task;
    }

    /**
     * Saves the given list of tasks to the save file, overwriting any
     * previous contents. Creates the parent directory if it doesn't
     * already exist.
     *
     * @param tasks the current list of tasks to persist
     * @throws BotzillaException if the file can't be written (e.g. the
     *                           OS denies write access, or the parent
     *                           directory couldn't be created)
     */
    public void save(ArrayList<Task> tasks) throws BotzillaException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new BotzillaException("could not create the save folder \"" + parentDir + "\"");
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new BotzillaException(e.getMessage());
        }
    }
}
