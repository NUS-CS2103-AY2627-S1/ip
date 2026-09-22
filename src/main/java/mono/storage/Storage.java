package mono.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import mono.task.Deadline;
import mono.task.Event;
import mono.task.Task;
import mono.task.ToDo;

/**
 * Handles creation of Mono's task file and appending task records to it.
 */
public class Storage {
    private static final String STORAGE_FILE_PATH = "./data/tasks.txt";

    /**
     * Creates the task file if it does not already exist.
     */
    public Storage() {
        try {
            initStorage();
        } catch (IOException e) {
            System.out.println("Unable to create task file: " + e.getMessage());
        }
    }

    /**
     * Saves the complete current task list to the task file.
     *
     * @param tasks tasks to write to storage
     */
    public void saveTasks(Task[] tasks) {
        try {
            initStorage();
            try (FileWriter writer = new FileWriter(STORAGE_FILE_PATH)) {
                for (Task task : tasks) {
                    writer.write(task.toFileString());
                    writer.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to save tasks: " + e.getMessage());
        }
    }

    /**
     * Reads and parses all task records from the task file.
     *
     * @return tasks represented by valid storage lines
     */
    public Task[] readStorage() {
        ArrayList<Task> storedTasks = new ArrayList<>();
        try {
            initStorage();
            for (String line : Files.readAllLines(Path.of(STORAGE_FILE_PATH))) {
                if (line.isBlank()) {
                    continue;
                }

                String[] fields = line.split("\\s*\\|\\s*", -1);
                Task task = parseTask(fields);
                if (task != null) {
                    storedTasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to read task file: " + e.getMessage());
        }
        return storedTasks.toArray(new Task[0]);
    }

    /**
     * Converts one persisted line into a task object.
     *
     * @param fields fields split from one persisted line
     * @return parsed task, or {@code null} for an invalid line
     */
    private Task parseTask(String[] fields) {
        try {
            Task task = switch (fields[0]) {
                case "T" -> fields.length == 3
                    ? new ToDo(fields[2])
                    : null;
                case "D" -> fields.length == 4
                    ? new Deadline(fields[2], fields[3])
                    : null;
                case "E" -> fields.length == 5
                    ? new Event(fields[2], fields[3], fields[4])
                    : null;
                default -> null;
            };

            if (task != null && (fields[1].equals("0") || fields[1].equals("1"))) {
                if (fields[1].equals("1")) {
                    task.markDone();
                }
                return task;
            }
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            // Ignore malformed storage lines and continue loading valid tasks.
        }
        return null;
    }

    /**
     * Creates Mono's data directory and task file when they do not exist.
     *
     * @throws IOException if the directory or file cannot be created
     */
    private void initStorage() throws IOException {
        File file = new File(STORAGE_FILE_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()
                && !parent.mkdirs() && !parent.isDirectory()) {
            throw new IOException("Unable to create directory " + parent);
        }

        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Unable to create file " + file);
        }
    }
}
