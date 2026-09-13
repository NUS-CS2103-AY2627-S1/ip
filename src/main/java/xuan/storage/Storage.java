package xuan.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import xuan.task.Deadline;
import xuan.task.Event;
import xuan.task.Task;
import xuan.task.Todo;

/**
 * Handles saving tasks to and loading tasks from a data file.
 */
public class Storage {

    private String filePath;

    /**
     * Creates a Storage object that uses the specified file path.
     *
     * @param filePath the path of the file used to store task data
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the tasks to xuan.txt so that they can be loaded again.
     * Each call overwrites the previous content.
     *
     * @param tasks the list of tasks to save
     * @throws IOException if an error occurs while writing the file
     */
    public void saveTasks(ArrayList<Task> tasks) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        FileWriter writer = new FileWriter(filePath);

        for (Task task : tasks) {
            if (task instanceof Todo) {
                writer.write("T | " + (task.isDone() ? "1" : "0")
                        + " | " + task.getDescription() + "\n");

            } else if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;

                writer.write("D | " + (task.isDone() ? "1" : "0")
                        + " | " + task.getDescription()
                        + " | " + deadline.getBy() + "\n");

            } else if (task instanceof Event) {
                Event event = (Event) task;

                writer.write("E | " + (task.isDone() ? "1" : "0")
                        + " | " + task.getDescription()
                        + " | " + event.getFrom()
                        + " | " + event.getTo() + "\n");
            }
        }
        writer.close();
    }

    /**
     * Reads the saved tasks from xuan.txt.
     *
     * @return the loaded list of tasks
     * @throws FileNotFoundException if the file cannot be opened
     */
    public ArrayList<Task> loadTasks() throws FileNotFoundException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return tasks;
        }

        Scanner fileScanner = new Scanner(file);

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] parts = line.split(" \\| ");

            String type = parts[0];
            boolean isDone = parts[1].equals("1");
            String description = parts[2];

            Task task;
            if (type.equals("T")) {
                task = new Todo(description);

            } else if (type.equals("D")) {
                LocalDate by = LocalDate.parse(parts[3]);
                task = new Deadline(description, by);
            } else {
                String from = parts[3];
                String to = parts[4];
                task = new Event(description, from, to);
            }

            if (isDone) {
                task.markAsDone();
            }
            tasks.add(task);
        }
        fileScanner.close();
        return tasks;
    }
}
