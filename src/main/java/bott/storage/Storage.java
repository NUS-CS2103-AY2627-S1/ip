package bott.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import bott.BottException;
import bott.task.Deadline;
import bott.task.Event;
import bott.task.FixedDurationTask;
import bott.task.Task;
import bott.task.Todo;

/**
 * Handles loading tasks from, and saving tasks to, a save file on disk.
 */
public class Storage {

    private final String filePath;

    /**
     * Creates a new Storage backed by the given file.
     *
     * @param filePath Path of the file tasks are loaded from and saved to.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads previously saved tasks from the save file.
     *
     * @return Tasks read from the save file, or an empty list if the file
     *         does not exist yet.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            while (fileScanner.hasNextLine()) {
                Task task = parseSavedTask(fileScanner.nextLine());
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException exception) {
            // No save file yet - start with an empty task list.
        }
        return tasks;
    }

    /**
     * Overwrites the save file with the given tasks.
     *
     * @param tasks Tasks to save.
     * @throws BottException If the save file cannot be written.
     */
    public void save(List<Task> tasks) throws BottException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks) {
                writer.write(task.toFileFormat() + System.lineSeparator());
            }
        } catch (IOException exception) {
            throw new BottException("Could not save tasks: " + exception.getMessage());
        }
    }

    /**
     * Parses one line of the save file into a task. Expected formats:
     * "T | 1 | desc", "D | 1 | desc | by", "E | 1 | desc | from | to", and
     * "F | 1 | desc | minutes", where the second field is "1" if the task is
     * done, or "0" otherwise.
     *
     * @param line Line read from the save file.
     * @return Task described by {@code line}, or {@code null} if the line
     *         is corrupted and should be skipped.
     */
    private static Task parseSavedTask(String line) {
        String[] fields = line.split(" \\| ");
        try {
            Task task;
            switch (fields[0]) {
                case "T":
                    task = new Todo(fields[2]);
                    break;
                case "D":
                    task = new Deadline(fields[2], LocalDate.parse(fields[3]));
                    break;
                case "E":
                    task = new Event(fields[2], LocalDate.parse(fields[3]), LocalDate.parse(fields[4]));
                    break;
                case "F":
                    task = new FixedDurationTask(fields[2], Integer.parseInt(fields[3]));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown task type: " + fields[0]);
            }
            if (fields[1].equals("1")) {
                task.markAsDone();
            }
            return task;
        } catch (RuntimeException exception) {
            System.out.println("Skipping corrupted save file line: " + line);
            return null;
        }
    }
}
