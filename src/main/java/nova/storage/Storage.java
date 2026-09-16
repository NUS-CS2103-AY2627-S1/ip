package nova.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import nova.tasks.Deadline;
import nova.tasks.Event;
import nova.tasks.Task;
import nova.tasks.ToDo;

/**
 * Handles saving and loading tasks from a file.
 */
public class Storage {

    private final String filepath;

    /**
     * Creates a Storage object using the given file path.
     *
     * @param filepath path to the storage file
     */
    public Storage(String filepath) {
        this.filepath = filepath;
    }

    /**
     * Saves all tasks to the storage file.
     *
     * @param tasks tasks to save
     * @throws IOException if the file cannot be written
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        File file = new File(filepath);
        File parentDirectory = file.getParentFile();

        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        FileWriter fileWriter = new FileWriter(filepath);

        for (Task task : tasks) {
            fileWriter.write(task.toFileString());
            fileWriter.write(System.lineSeparator());
        }

        fileWriter.close();
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return list of loaded tasks
     * @throws IOException if the file cannot be read
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filepath);

        if (!file.exists()) {
            return tasks;
        }

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\|");

            String type = parts[0].trim();
            boolean isDone = parts[1].trim().equals("X");

            Task task;

            if (type.equals("T")) {
                String description = parts[2].trim();
                task = new ToDo(description);

            } else if (type.equals("D")) {
                String description = parts[2].trim();
                String byString = parts[3].trim();

                LocalDateTime by =
                        LocalDateTime.parse(byString);

                task = new Deadline(description, by);

            } else {
                String description = parts[2].trim();
                String fromString = parts[3].trim();
                String toString = parts[4].trim();

                LocalDateTime from =
                        LocalDateTime.parse(fromString);

                LocalDateTime to =
                        LocalDateTime.parse(toString);

                task = new Event(description, from, to);
            }

            if (isDone) {
                task.markDone();
            }

            tasks.add(task);
        }

        scanner.close();

        return tasks;
    }
}
