package orion.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import orion.exception.OrionException;
import orion.task.Deadline;
import orion.task.Event;
import orion.task.Task;
import orion.task.Todo;

/**
 * Handles the loading and saving of task data to a file.
 */
public class Storage {
    private String filePath;

    /**
     * Initializes a Storage object with the specified file path.
     *
     * @param filePath The path of the file to save and load tasks from.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.trim().isEmpty() : "Storage file path should not be null or empty";
        this.filePath = filePath;
    }

    /**
     * Loads the tasks from the save file.
     *
     * @return A list of tasks parsed from the save file.
     * @throws OrionException If there is an error reading the file or parsing the data.
     */
    public List<Task> load() throws OrionException {
        List<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" \\| ");
                String taskType = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                switch (taskType) {
                    case "T":
                        tasks.add(new Todo(description, isDone));
                        break;

                    case "D":
                        LocalDateTime deadline = LocalDateTime.parse(parts[3], Task.SAVE_FORMAT);
                        tasks.add(new Deadline(description, deadline, isDone));
                        break;

                    case "E":
                        LocalDateTime start = LocalDateTime.parse(parts[3], Task.SAVE_FORMAT);
                        LocalDateTime end = LocalDateTime.parse(parts[4], Task.SAVE_FORMAT);
                        tasks.add(new Event(description, start, end, isDone));
                        break;

                    default:
                        System.out.println("Unknown task type found in save file. Skipping line: " + line);
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new OrionException("Could not find the save file!");
        } catch (IndexOutOfBoundsException | NumberFormatException | DateTimeParseException e) {
            System.out.println("Corrupted data found in save file. Skipping line...");
        }
        return tasks;
    }

    /**
     * Saves the current list of tasks to the save file.
     *
     * @param tasks The list of tasks to be saved.
     * @throws OrionException If there is an error writing the tasks to the file.
     */
    public void save(List<Task> tasks) throws OrionException {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (FileWriter fw = new FileWriter(filePath)) {
                for (Task task : tasks) {
                    fw.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new OrionException("There was an error saving tasks to the save file.");
        }
    }
}
