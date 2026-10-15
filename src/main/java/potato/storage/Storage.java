package potato.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import potato.exception.PotatoException;
import potato.task.Deadline;
import potato.task.Event;
import potato.task.Task;
import potato.task.Todo;

/**
 * Handles reading tasks from and writing tasks to hard disk file storage.
 */
public class Storage {
    private final String filePath;

    /**
     * Constructs a {@code Storage} object targeting the specified file path.
     *
     * @param filePath Path to the data storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves all tasks from the provided list into hard disk file storage.
     *
     * @param tasks List of tasks to write to the file.
     * @throws PotatoException If the task list cannot be saved.
     */
    public void save(ArrayList<Task> tasks) throws PotatoException {
        try {
            Path path = Paths.get(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (FileWriter writer = new FileWriter(filePath)) {
                for (Task task : tasks) {
                    writer.write(taskToFileFormat(task) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new PotatoException("Could not save your tasks: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from the file system into an {@code ArrayList}.
     *
     * @return List of loaded tasks.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                Task task = parseTaskFromFile(line);
                if (task != null) {
                    tasks.add(task);
                } else {
                    System.out.println("Skipping corrupted save entry: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Formats a {@code Task} object into a line string representation for storage.
     *
     * @param task Task to convert.
     * @return Formatted file storage line.
     */
    private String taskToFileFormat(Task task) {
        String isDoneFlag = task.getStatusIcon().equals("X") ? "1" : "0";
        if (task instanceof Todo) {
            return "T | " + isDoneFlag + " | " + task.getDescription();
        } else if (task instanceof Deadline) {
            return "D | " + isDoneFlag + " | " + task.getDescription() + " | " + ((Deadline) task).getBy();
        } else if (task instanceof Event) {
            return "E | " + isDoneFlag + " | " + task.getDescription() + " | " + ((Event) task).getFrom() + " | " + ((Event) task).getTo();
        }
        return "";
    }

    /**
     * Parses a single line string from the storage file into a corresponding {@code Task}.
     *
     * @param line Line text read from storage file.
     * @return Constructed {@code Task} object, or {@code null} if formatted improperly.
     */
    private Task parseTaskFromFile(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String desc = parts[2];

        Task task = null;
        if (type.equals("T")) {
            task = new Todo(desc);
        } else if (type.equals("D") && parts.length >= 4) {
            task = new Deadline(desc, parts[3]);
        } else if (type.equals("E") && parts.length >= 5) {
            task = new Event(desc, parts[3], parts[4]);
        }

        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }
}
