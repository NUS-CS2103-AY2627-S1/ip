package spoon.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import spoon.exception.FileCorruptedException;
import spoon.exception.SpoonException;
import spoon.task.Deadline;
import spoon.task.Event;
import spoon.task.Task;
import spoon.task.ToDo;

/**
 * Handles the loading and saving of the list of tasks to an external file.
 */
public class Storage {
    private final String filePath;

    // Constructor
    public Storage(String filePath) {
        // File path should never be null or blank, else no such place to load from and write to
        assert filePath != null && !filePath.isBlank() : "Storage filePath is null or blank";
        this.filePath = filePath;
    }

    // Methods
    /**
     * Loads tasks from file.
     *
     * @return list of tasks; returns an empty list if file doesn't exist.
     * @throws SpoonException if an exception is detected (refer to the different exceptions).
     * @throws FileNotFoundException if file is not found.
     */
    public ArrayList<Task> load() throws SpoonException, FileNotFoundException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        int lineCounter = 0;

        // If file does not exist, initialize an empty list
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                // Get next line
                String line = scanner.nextLine().trim();
                lineCounter++;

                // Check for empty line
                if (line.isEmpty()) {
                    throw new FileCorruptedException(lineCounter);
                }

                Task task = parseTask(line, lineCounter);

                // Check for duplicate tasks
                if (tasks.contains(task)) {
                    throw new FileCorruptedException(lineCounter);
                }

                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Converts a line read from file to the corresponding task.
     *
     * @param line line read from file.
     * @param lineCounter number of lines read so far.
     * @return task read from line.
     * @throws SpoonException if an exception is detected (refer to the different exceptions).
     */
    private Task parseTask(String line, int lineCounter) throws SpoonException {
        String[] inputArray = line.split("\\s*\\|\\s*");

        // Check for missing or wrong completed status and name
        if (inputArray.length < 3 || inputArray[2].isBlank()
                || !(inputArray[1].equals("0") || inputArray[1].equals("1"))) {
            throw new FileCorruptedException(lineCounter);
        }

        String taskType = inputArray[0];
        boolean isCompleted = inputArray[1].equals("1");
        String name = inputArray[2];

        Task task;
        switch (taskType) {
            case "T":
                task = new ToDo(name);
                break;
            case "D":
                if (inputArray.length < 4 || inputArray[3].isBlank()) {
                    throw new FileCorruptedException(lineCounter);
                }
                task = new Deadline(name, inputArray[3]);
                break;
            case "E":
                if (inputArray.length < 5 || inputArray[3].isBlank()
                        || inputArray[4].isBlank()) {
                    throw new FileCorruptedException(lineCounter);
                }
                task = new Event(name, inputArray[3], inputArray[4]);
                break;
            default:
                throw new FileCorruptedException(lineCounter);
        }

        // Task should be created before updating its completion status and saving it to storage
        assert task != null : "Task object is not successfully initialized"
                + "before status update and storage write";

        if (isCompleted) {
            task.complete();
        }

        return task;
    }

    /**
     * Saves the current list of tasks to file.
     *
     * @param tasks current list of tasks.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        // Task list should never be null
        assert tasks != null : "A null list of tasks is being written to storage";

        File file = new File(filePath);
        File parentDir = file.getParentFile();

        // Automatically create ./data/ folder if missing
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter fw = new FileWriter(file)) {
            String task = tasks.stream()
                    .map(Task::format)
                    .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
            // Task should never be null
            assert task != null : "A null task is being written to storage";
            fw.write(task);
        }
    }
}
