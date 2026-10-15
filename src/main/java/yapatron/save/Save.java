package yapatron.save;

import yapatron.YapException;
import yapatron.task.Task;
import yapatron.task.Todo;
import yapatron.task.Deadline;
import yapatron.task.Event;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Handles loading tasks from a file and storing tasks into the file
 */
public class Save {
    private final String path;

    /**
     * Creates a Save object with specified filepath
     *
     * @param path Path pf the file used to store data
     */
    public Save(String path) {
        this.path = path;
    }

    /**
     * Loads saved tasks from the file
     *
     * @return List of tasks loaded from file
     * @throws YapException if there is a problem loading the file
     */
    public List<Task> getTasks() throws YapException {
        // get tasks from the filepath
        List<Task> tasks = new ArrayList<>();
        File file = new File(this.path);

        if (!file.exists()) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                Task task = parseTask(line);

                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            throw new YapException(
                    "Oh noo :( I don't think I have permission to access that file! Bummer.");
        } catch (YapException e) {
            throw e;
        } catch (Exception e) {
            throw new YapException(
                    "Oh noo :( I can't read that #Kena");
        }

        return tasks;
    }

    /**
     * Saves the list of tasks into the file
     *
     * @param tasks List of tasks to save into the file
     * @throws YapException if error occurs while writing into the file
     */
    public void saveTasks(List<Task> tasks) throws YapException {
        // saves list of tasks to the file
        try {
            File file = new File(this.path);

            if (file.getParentFile() != null
                    && !file.getParentFile().exists()
                    && !file.getParentFile().mkdirs()) {
                throw new IOException("Erm! I couldn't make a new directory D: #Bums");
                    }

            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat()
                            + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new YapException(
                    "Oh noo :( I don't think I have permission to save that file! #PleaseGiveMe");
        }
    }

    private Task parseTask(String line) throws YapException {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String taskType = parts[0];
        boolean isDone = parts[1].equals("X");
        String desc = parts[2];
        Task task = null;

        switch (taskType) {
            case "[TASK]" :
                task = new Todo(desc);
                break;
            case "[DEADLINE]" :
                if (parts.length < 4) {
                    return null;
                }
                task = new Deadline(desc, parts[3]);
                break;
            case "[EVENT]" :
                if (parts.length < 5) {
                    return null;
                }
                task = new Event(desc, parts[3], parts[4]);
                break;
            default:
                return null;
        }

        assert task != null : "A recognised task type must be used";

        if (isDone) {
            task.markAsDone();
            assert task.isDone() : "A stored completed task must be restored as completed";
        }

        return task;

    }
}










