package happy.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import happy.exception.HappyException;
import happy.task.Deadline;
import happy.task.Event;
import happy.task.Priority;
import happy.task.Task;
import happy.task.TaskList;
import happy.task.Todo;

/**
 * Handles loading tasks from file and saving tasks to file on disk.
 */
public class Storage {
    private static final String DELIMITER_REGEX = "\\s*\\|\\s*";

    private final String filePath;

    /**
     * Constructs a Storage manager for the specified file path.
     *
     * @param filePath Relative or absolute path to the data storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks from disk. If file or parent directory does not exist,
     * returns an empty task list. Skips corrupted lines safely.
     *
     * @return List of tasks loaded from file.
     * @throws HappyException If reading file encounters severe errors.
     */
    public ArrayList<Task> load() throws HappyException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                Task task = parseTaskFromLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            throw new HappyException("Warning: Failed to load task data from " + filePath);
        }
        return tasks;
    }

    private Task parseTaskFromLine(String line) {
        if (line.isEmpty()) {
            return null;
        }
        String[] parts = line.split(DELIMITER_REGEX);
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        int currIndex = 2;

        Priority priority = Priority.NONE;
        if (parts[currIndex].equals("HIGH") || parts[currIndex].equals("MEDIUM") || parts[currIndex].equals("LOW")) {
            priority = Priority.parse(parts[currIndex]);
            currIndex++;
        }

        if (currIndex >= parts.length) {
            return null;
        }
        String description = parts[currIndex];

        Task task = createBareTask(type, description, parts, currIndex);
        if (task != null) {
            if (isDone) {
                task.markAsDone();
            }
            task.setPriority(priority);
        }
        return task;
    }

    private Task createBareTask(String type, String description, String[] parts, int currIndex) {
        if (type.equals("T")) {
            return new Todo(description);
        } else if (type.equals("D") && parts.length > currIndex + 1) {
            return new Deadline(description, parts[currIndex + 1]);
        } else if (type.equals("E") && parts.length > currIndex + 2) {
            return new Event(description, parts[currIndex + 1], parts[currIndex + 2]);
        }
        return null;
    }

    /**
     * Saves the current list of tasks to file on disk.
     * Automatically creates parent directories if they do not exist.
     *
     * @param taskList TaskList containing tasks to persist.
     * @throws HappyException If writing to file fails.
     */
    public void save(TaskList taskList) throws HappyException {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : taskList.getTasks()) {
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new HappyException("Error: Failed to save tasks to file: " + e.getMessage());
        }
    }
}
