package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import parser.Parser;
import task.Task;
import task.TaskList;

/**
 * Handles loading tasks from a file and saving tasks back to the file.
 */
public class Storage {
    private final File taskFile;

    /**
     * Constructs a Storage object with the given file path.
     *
     * @param filePath the path to the task data file
     */
    public Storage(String filePath) {
        assert filePath != null : "Storage requires a task file path";
        this.taskFile = new File(filePath);
        
        try {
            // 1. Get the parent directory structure
            File parentDir = taskFile.getParentFile();
            
            // 2. Create parent directories if they don't exist
            if (parentDir != null && !parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    System.out.println("Directory path created successfully.");
                } else {
                    System.out.println("Failed to create directory path.");
                }
            }

            // 3. Create the actual file if it doesn't exist
            if (taskFile.createNewFile()) {
                System.out.println("File created successfully: " + taskFile.getName());
            } else {
                System.out.println("File already exists.");
            }

        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads tasks from the task file into a TaskList.
     *
     * @return a TaskList containing tasks loaded from the file
     * @throws IOException when the task file cannot be read
     */
    public TaskList load() throws IOException {
        TaskList tasks = new TaskList();
        try {
            Scanner scanner = new Scanner(taskFile);
            while (scanner.hasNextLine()) {
                String taskSignature = scanner.nextLine();
                Task task = Parser.parseTaskSignature(taskSignature);
                if (task != null) {
                    tasks.add(task);
                }
            }
            scanner.close();
            return tasks;
        } catch (FileNotFoundException e) {
            return tasks;
        }
    }

    /**
     * Saves the given TaskList to the task file, overwriting any existing content.
     *
     * @param tasks the TaskList to be saved
     * @throws IOException when the file cannot be written to
     */
    public void save(TaskList tasks) throws IOException {
        assert tasks != null : "Storage cannot save a null task list";
        FileWriter writer = new FileWriter(taskFile);
        writer.write(tasks.toSignature());
        writer.close();
    }
}
