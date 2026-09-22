package miaow.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import miaow.task.Deadline;
import miaow.task.Event;
import miaow.task.Task;


/**
 * Storage
 */
public class Storage {
    private String filePath;

    public Storage(String filepath) {
        this.filePath = filepath;
    }

    /**
     * Creates a directory to store list if it doesn't exist。
     *
     * @throws IOException if input is invalid.
     */
    public void createDirectoryIfNeeded() throws IOException {
        Path path = Paths.get("./data");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
    /**
     * Saves an arraylist of tasks
     * If the position is unset, NaN is returned.
     *
     * @param tasks The arraylist of tasks.
     */
    public void saveTasks(ArrayList<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException(
                    "Tasks cannot be null."
            );
        }

        try {
            Path target = Paths.get(filePath);
            Path parent = target.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                String content = tasks.stream()
                        .map(Task::toFileFormat)
                        .collect(Collectors.joining(
                                System.lineSeparator()));

                writer.write(content);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save tasks to " + filePath,
                    e
            );
        }
    }

    /**
     * Shows the list of tasks stored in the file
     * @return Array of tasks in the file
     */
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        // Check if file exists
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                Task task = parseTaskFromLine(line);

                if (task != null) {
                    tasks.add(task);
                }
            }

            System.out.println("Loaded " + tasks.size() + " tasks from file.");
        } catch (IOException e) {
            System.out.println("Error loading tasks: File not found.");
        }

        return tasks;
    }

    private static Task parseTaskFromLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        try {
            switch (type) {
                case "T":
                    // Todo: T | 0/1 | description
                    Task task = new Task(description);
                    if (isDone) {
                        task.mark();
                    }
                    return task;

                case "D":
                    // Deadline: D | 0/1 | description | by
                    if (parts.length < 4) {
                        return null;
                    }
                    Deadline deadline = new Deadline(description);
                    deadline.by(parts[3].trim());
                    if (isDone) {
                        deadline.mark();
                    }
                    return deadline;

                case "E":
                    // Event: E | 0/1 | description | from | to
                    if (parts.length < 5) {
                        return null;
                    }
                    Event event = new Event(description);
                    event.from(parts[3].trim());
                    event.to(parts[4].trim());
                    if (isDone) {
                        event.mark();
                    }
                    return event;

                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns a string of the task as stored in the file.
     *
     * @param task The task to be formatted
     */
    public String formatTaskForFile(Task task) {
        return task.toFileFormat();
    }

    /**
     * gets file path
     * @return The string representation of getting to the data file
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Checks for existence of the file
     * @return Boolean value
     */
    public boolean fileExists() {
        return new File(filePath).exists();
    }
}
