package kdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles saving the task list to the hard drive.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates storage using the given task-file path.
     *
     * @param filePath path of the file used to store tasks
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Replaces the task file with the current contents of the task list.
     *
     * @param tasks tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("could not create data directory");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : tasks) {
                writer.write(task.toFileFormat());
                writer.newLine();
            }
        }
    }

    /**
     * Loads tasks from the task file. A missing file represents an empty list.
     *
     * @return tasks reconstructed from the file
     * @throws IOException if the file cannot be read or contains invalid data
     */
    public TaskList load() throws IOException {
        TaskList tasks = new TaskList();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    tasks.add(parseTask(line));
                }
            }
        }
        return tasks;
    }

    /**
     * Reconstructs one task from its pipe-separated file representation.
     *
     * @param line one line from the task file
     * @return the reconstructed task
     * @throws IOException if the line has an unsupported or invalid format
     */
    private Task parseTask(String line) throws IOException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3 || parts[2].trim().isEmpty()) {
            throw new IOException("invalid task format: " + line);
        }

        String type = parts[0];
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new IOException("invalid task status: " + line);
        }
        boolean isDone = parts[1].equals("1");
        Task task;

        try {
            switch (type) {
                case "T":
                    if (parts.length != 3) {
                        throw new IOException("invalid todo format: " + line);
                    }
                    task = new Todo(parts[2]);
                    break;
                case "D":
                    if (parts.length != 4) {
                        throw new IOException("invalid deadline format: " + line);
                    }
                    LocalDateTime deadline = LocalDateTime.parse(
                            parts[3], DateTimeFormatter.ofPattern("d/M/uuuu HHmm"));
                    task = new Deadline(parts[2], deadline);
                    break;
                case "E":
                    if (parts.length != 5 || parts[3].trim().isEmpty()
                            || parts[4].trim().isEmpty()) {
                        throw new IOException("invalid event format: " + line);
                    }
                    task = new Event(parts[2], parts[3], parts[4]);
                    break;
                default:
                    throw new IOException("unknown task type: " + type);
            }
        } catch (DateTimeParseException e) {
            throw new IOException("invalid deadline date: " + line, e);
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
