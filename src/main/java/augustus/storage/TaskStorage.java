package augustus.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import augustus.exception.AugustusException;
import augustus.task.Deadline;
import augustus.task.Event;
import augustus.task.Task;
import augustus.task.ToDo;

/**
 * Handles the saving and loading of tasks from a data file
 */
public class TaskStorage {
    private final String filePath;

    /**
     * Creates a TaskStorage that uses the specified file path
     *
     * @param filePath Path of the file used to store tasks.
     */
    public TaskStorage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Create the data file and its parent directories if they do not exist
     *
     * @throws AugustusException if the data file cannot be created or accessed
     */
    public void createFile() throws AugustusException {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new AugustusException("Unable to access/create the data file");
        }
    }

    /**
     * Saves all the tasks to the data file
     *
     * @param tasks Tasks to be saved
     * @throws AugustusException If the tasks cannot be saved to the file
     */
    public void saveTasks(ArrayList<Task> tasks) throws AugustusException {
        try {
            FileWriter writer = new FileWriter(filePath);
            for (Task task : tasks) {
                writer.write(task.toFileString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new AugustusException("Unable to save tasks");
        }
    }

    /**
     * Loads the tasks from the data file
     *
     * @return A list containing the tasks loaded from the file
     * @throws AugustusException If the tasks cannot be loaded or the stored data is invalid
     */
    public ArrayList<Task> loadTasks() throws AugustusException {
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.isBlank()) {
                    continue;
                }

                tasks.add(parseTask(line));
            }

            scanner.close();
        } catch (IOException e) {
            throw new AugustusException("Unable to load tasks");
        }

        return tasks;
    }

    /**
     * Converts a stored task record into a task.
     *
     * @param line Stored task record.
     * @return Task represented by the record.
     * @throws AugustusException If the task type is invalid.
     */
    private Task parseTask(String line) throws AugustusException {
        try {
            String[] segments = line.split(" \\| ");

            String taskType = segments[0];
            boolean isMarked = segments[1].equals("1");
            String description = segments[2];

            Task task;
            String tag = "";

            if (taskType.equals("T")) {
                task = new ToDo(description);

                if (segments.length > 3) {
                    tag = segments[3];
                }
            } else if (taskType.equals("D")) {
                LocalDate by = LocalDate.parse(segments[3]);
                task = new Deadline(description, by);

                if (segments.length > 4) {
                    tag = segments[4];
                }
            } else if (taskType.equals("E")) {
                String from = segments[3];
                String to = segments[4];
                task = new Event(description, from, to);

                if (segments.length > 5) {
                    tag = segments[5];
                }
            } else {
                throw new AugustusException("Invalid task type in data file");
            }

            if (isMarked) {
                task.markDone();
            }

            if (!tag.isEmpty()) {
                task.setTag(tag);
            }

            return task;

        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            throw new AugustusException("Invalid task data in data file");
        }
    }

}
