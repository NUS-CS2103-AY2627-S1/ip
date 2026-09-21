package margit.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import margit.task.DeadlineTask;
import margit.task.EventTask;
import margit.task.Task;
import margit.task.TaskDateTime;
import margit.task.TaskList;
import margit.task.TodoTask;

/** Loads tasks from and saves tasks to the application's data file. */
public class Storage {
    private final String filePath;

    /** Creates storage that reads from and writes to {@code filePath}. */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /** Saves the tasks in their current order. */
    public void save(TaskList tasks) {
        File saveFile = new File(filePath);
        File parentDir = saveFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(saveFile)) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(tasks.get(i).toSaveFormat() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("     Warning: could not save tasks to disk (" + e.getMessage() + ")");
        }
    }

    /** Reads tasks from the configured file into {@code tasks}. */
    public void load(TaskList tasks) {
        File saveFile = new File(filePath);
        if (!saveFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            while ((line = reader.readLine()) != null && !tasks.isFull()) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    tasks.add(parseSavedTask(line));
                } catch (RuntimeException e) {
                    System.out.println("     Warning: skipping corrupted line in save file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: could not load tasks from disk (" + e.getMessage() + ")");
        }
    }

    /** Recreates one task from the existing save-file format. */
    private Task parseSavedTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        switch (type) {
            case "T":
                task = new TodoTask(description);
                break;
            case "D":
                task = new DeadlineTask(description, TaskDateTime.fromSaveFormat(parts[3]));
                break;
            case "E":
                task = new EventTask(description, TaskDateTime.fromSaveFormat(parts[3]),
                        TaskDateTime.fromSaveFormat(parts[4]));
                break;
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }

        if (isDone) {
            task.mark();
        }
        return task;
    }
}
