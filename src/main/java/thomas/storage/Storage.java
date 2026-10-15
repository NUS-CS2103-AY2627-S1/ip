package thomas.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import thomas.ThomasException;
import thomas.task.Deadline;
import thomas.task.Event;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.Todo;

/**
 * Handles loading tasks from and saving tasks to a hard disk file using OS-independent paths.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage instance with an OS-independent relative path.
     *
     * @param relativePath Folder path segments and file name.
     */
    public Storage(String... relativePath) {
        this.filePath = Paths.get("", relativePath);
    }

    /**
     * Loads tasks from the disk file.
     *
     * @return Restored task objects.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = filePath.toFile();

        if (!file.exists()) {
            try {
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                System.out.println(" !!! Could not create data file, starting empty list");
            }
            return tasks;
        }
        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    Task task = parseLineToTask(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (Exception e) {
                    System.out.println(" !!! Corrupted data on line " + lineNumber + " skipped.");
                }
            }
        } catch (IOException e) {
            System.out.println(" Error reading storage file: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Parses a single file line into the corresponding task.
     *
     * @param line A single line from the storage file.
     * @return The parsed task.
     * @throws ThomasException If the line is invalid or corrupt.
     */
    private Task parseLineToTask(String line) throws ThomasException {
        int firstSeparatorIndex = line.indexOf('|');
        if (firstSeparatorIndex == -1) {
            throw new ThomasException("Invalid format: missing task status");
        }

        int secondSeparatorIndex = line.indexOf('|', firstSeparatorIndex + 1);
        if (secondSeparatorIndex == -1) {
            throw new ThomasException("Invalid format: missing task description");
        }

        String taskType = line.substring(0, firstSeparatorIndex).trim();
        String status = line.substring(firstSeparatorIndex + 1, secondSeparatorIndex).trim();
        if (!status.equals("Y") && !status.equals("N")) {
            throw new ThomasException("Task status must be Y or N.");
        }
        boolean isDone = status.equals("Y");
        Task task = switch (taskType) {
            case "T" -> parseTodo(line, secondSeparatorIndex);
            case "D" -> parseDeadline(line, secondSeparatorIndex);
            case "E" -> parseEvent(line, secondSeparatorIndex);
            default -> throw new ThomasException("Unknown task type: " + taskType);
        };

        assert task != null : "A recognized task type must produce a task";
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private Task parseTodo(String line, int descriptionSeparatorIndex) throws ThomasException {
        String description = line.substring(descriptionSeparatorIndex + 1).trim();
        if (description.isEmpty() || description.contains("|")) {
            throw new ThomasException("Todo description cannot be empty.");
        }
        return new Todo(description);
    }

    private Task parseDeadline(String line, int descriptionSeparatorIndex) throws ThomasException {
        int dateSeparatorIndex = line.indexOf('|', descriptionSeparatorIndex + 1);
        if (dateSeparatorIndex == -1) {
            throw new ThomasException("Deadline date/time is missing.");
        }

        String description = line.substring(descriptionSeparatorIndex + 1, dateSeparatorIndex).trim();
        String dueDateTime = line.substring(dateSeparatorIndex + 1).trim();
        if (description.isEmpty() || dueDateTime.isEmpty() || dueDateTime.contains("|")) {
            throw new ThomasException("Deadline description or date/time cannot be empty.");
        }
        return new Deadline(description, dueDateTime);
    }

    private Task parseEvent(String line, int descriptionSeparatorIndex) throws ThomasException {
        int startSeparatorIndex = line.indexOf('|', descriptionSeparatorIndex + 1);
        if (startSeparatorIndex == -1) {
            throw new ThomasException("Event start date/time is missing.");
        }

        int endSeparatorIndex = line.indexOf('|', startSeparatorIndex + 1);
        if (endSeparatorIndex == -1) {
            throw new ThomasException("Event end date/time is missing.");
        }

        String description = line.substring(descriptionSeparatorIndex + 1, startSeparatorIndex).trim();
        String startDateTime = line.substring(startSeparatorIndex + 1, endSeparatorIndex).trim();
        String endDateTime = line.substring(endSeparatorIndex + 1).trim();
        if (description.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty()
                || endDateTime.contains("|")) {
            throw new ThomasException("Event description and date/times cannot be empty.");
        }
        return new Event(description, startDateTime, endDateTime);
    }

    /**
     * Saves the current list of tasks to the disk file.
     *
     * @param tasks Tasks to save.
     */
    public void save(ArrayList<Task> tasks) {
        saveTasks(tasks);
    }

    /**
     * Saves a task list without exposing its internal collection to the caller.
     *
     * @param taskList Task list to save.
     */
    public void save(TaskList taskList) {
        saveTasks(taskList.getTasks());
    }

    private void saveTasks(ArrayList<Task> tasks) {
        File file = filePath.toFile();
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter fileWriter = new FileWriter(file)) {
                for (Task task : tasks) {
                    fileWriter.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (Exception e) {
            System.out.println(" !!! Cannot save to disk: " + e.getMessage());
        }
    }
}

