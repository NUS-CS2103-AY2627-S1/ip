package bonbon.storage;

import bonbon.parser.Parser;
import bonbon.tasklist.Task;
import bonbon.tasklist.TaskList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage writes and loads task data to and from a file.
 */
public class Storage {

    /**
     * Saves all current tasks in the task list into the specified file.
     * Overwrites existing content so deleted or modified tasks are accurately reflected.
     *
     * @param filePath the file path to write into.
     * @param tasks the task list containing tasks to save.
     */
    public static void writeFile(Path filePath, TaskList tasks) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            List<String> lines = new ArrayList<>();
            int i = 0;
            while (true) {
                try {
                    Task task = tasks.getTask(i);
                    if (task == null) {
                        break;
                    }
                    lines.add(task.toFileFormat());
                    i++;
                } catch (Exception e) {
                    break;
                }
            }

            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Loads saved tasks from the specified file path into the task list.
     *
     * @param filePath the file path to read from.
     * @param tasks the task list to populate.
     */
    public static void loadFile(Path filePath, TaskList tasks) {
        if (!Files.exists(filePath)) {
            System.out.println("No saved data found! Starting fresh.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    Task task = Parser.parseTaskFromFile(line);
                    if (task != null) {
                        tasks.addTask(task);
                    }
                } catch (Exception e) {
                    // Catches errors per line so one corrupt line doesn't abort the entire load
                    System.out.println("Skipping corrupted entry at line " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}