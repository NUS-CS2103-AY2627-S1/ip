package mochi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mochi.task.Task;

/**
 * Handles loading tasks from and saving tasks to a file on disk.
 */
public class Storage {
    private final File dataFile;

    /**
     * Creates a storage backed by the given file path.
     */
    public Storage(String filePath) {
        this.dataFile = Paths.get(filePath).toFile();
    }

    /**
     * Loads tasks from the data file, creating the directory and file if needed.
     */
    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();

        File dir = dataFile.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                return tasks;
            }
        }

        try (Scanner scanner = new Scanner(dataFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    Task task = Task.fromFileString(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (MochiException e) {
                    // Skip corrupted lines silently.
                }
            }
        } catch (FileNotFoundException e) {
            // File does not exist yet; return an empty list.
        }
        return tasks;
    }

    /**
     * Saves the given list of tasks to the data file.
     */
    public void saveTasks(List<Task> tasks) {
        File dir = dataFile.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(dataFile)) {
            for (Task task : tasks) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Warning: could not save tasks to file.");
        }
    }
}
