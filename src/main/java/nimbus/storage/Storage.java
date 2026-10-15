package nimbus.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nimbus.exception.NimbusException;
import nimbus.task.Task;

/**
 * Represents the storage of the Nimbus application.
 * It is responsible for reading and writing tasks to a file.
 */
public class Storage {

    public final String filePath;

    /**
     * Constructs a new Storage object that can read files from the specified
     * filePath.
     *
     * @param filePath the path to read the file from.
     */
    public Storage(String filePath) {
        assert filePath != null : "File path cannot be null";
        this.filePath = filePath;
    }

    /**
     * Reads from the file and constructs a List of tasks.
     *
     * @return The list of tasks read from the file.
     * @throws NimbusException If file is corrupted.
     */
    public List<Task> readFromFile() throws NimbusException {
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // Create parent directories if they don't exist
        if (!file.exists()) {
            return new ArrayList<>();
        }
        ArrayList<Task> taskList = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                Task task = Task.fromFileString(line);
                taskList.add(task);
            }
        } catch (FileNotFoundException e) {
            throw new NimbusException("File is corrupted or cannot be read, recreating file.");
        }
        return taskList;
    }

    /**
     * Writes the new taskList into the file
     *
     * @param taskList List of tasks to write.
     * @throws NimbusException If there is any error during the writing process.s
     */
    public void writeToFile(List<Task> taskList) throws NimbusException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : taskList) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new NimbusException("Error writing to file: " + e.getMessage());
        }
    }
}
