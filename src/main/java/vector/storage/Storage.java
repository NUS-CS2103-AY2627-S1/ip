package vector.storage;

import java.util.ArrayList;

import vector.task.Deadline;
import vector.task.Event;
import vector.task.Task;
import vector.task.Todo;

/**
 * Handles loading tasks from and saving tasks to the data file.
 */
public class Storage {
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String STATUS_DONE = "1";
    private static final String DELIMITER = " \\| ";

    private String filePath;

    /**
     * Constructs a Storage object.
     *
     * @param filePath The path to the data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file.
     *
     * @return An ArrayList of tasks loaded from the file.
     */
    public ArrayList<Task> load() throws vector.VectorException {
        ArrayList<Task> tasks = new ArrayList<>();
        java.io.File file = new java.io.File(this.filePath);
        if (!file.exists()) {
            return tasks;
        }
        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    Task task = parseLineToTask(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (Exception e) {
                    throw new vector.VectorException("Corrupted data found in save file: " + line);
                }
            }
        } catch (java.io.FileNotFoundException e) {
            throw new vector.VectorException("Data file not found.");
        }
        return tasks;
    }

    private Task parseLineToTask(String line) throws Exception {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 3) {
            throw new Exception("Incomplete data line");
        }
        String type = parts[0];
        String isDoneString = parts[1];
        String description = parts[2];
        boolean isDone = isDoneString.equals(STATUS_DONE);

        Task task = null;
        if (type.equals(TYPE_TODO)) {
            task = new Todo(description);
        } else if (type.equals(TYPE_DEADLINE) && parts.length >= 4) {
            task = new Deadline(description, parts[3]);
        } else if (type.equals(TYPE_EVENT) && parts.length >= 5) {
            task = new Event(description, parts[3], parts[4]);
        }

        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Saves the current tasks to the data file.
     *
     * @param tasks The ArrayList of tasks to save.
     */
    public void save(ArrayList<Task> tasks) throws vector.VectorException {
        assert tasks != null : "Task list to save should not be null";
        try {
            java.io.File file = new java.io.File(this.filePath);
            java.io.File dir = file.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            java.io.FileWriter fileWriter = new java.io.FileWriter(file);
            for (Task task : tasks) {
                fileWriter.write(task.toFileFormat() + "\n");
            }
            fileWriter.close();
        } catch (java.io.IOException e) {
            throw new vector.VectorException("Something went wrong saving tasks: " + e.getMessage());
        }
    }
}

