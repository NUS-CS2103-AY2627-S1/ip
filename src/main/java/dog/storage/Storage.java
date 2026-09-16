package dog.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import dog.model.Task;

/**
 * Handles loading and saving tasks to a file storage.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a Storage instance with the specified file path.
     *
     * @param filePath the path to the storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads a task list from the storage file.
     *
     * @return an ArrayList of loaded tasks, or an empty list if the file doesn't exist.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = getStorageFile();

        if (!file.exists()) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                Task task = parseTask(scanner.nextLine());

                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading tasks: File not found");
        }

        return tasks;
    }

    /**
     * Saves a task list to the storage file.
     *
     * @param tasks the list of tasks to save.
     */
    public void save(ArrayList<Task> tasks) {
        File file = getStorageFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : tasks) {
                writer.write(task.toSaveFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Gets the storage file and ensures its parent directory exists.
     *
     * @return the storage file.
     */
    private File getStorageFile() {
        File file = new File(filePath);
        File directory = file.getParentFile();

        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }

        return file;
    }

    /**
     * Parses a task from a line of the storage file.
     *
     * @param line the line to parse.
     * @return the parsed Task, or null if the line format is invalid.
     */
    private Task parseTask(String line) {
        return Task.fromSaveFormat(line);
    }
}
