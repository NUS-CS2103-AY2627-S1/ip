package genie.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import genie.model.Deadline;
import genie.model.Event;
import genie.model.Task;
import genie.model.ToDo;
import genie.util.DateFormats;

/** Deals with loading tasks from and saving tasks to a file. */
public class Storage {
    private final String filePath;

    /**
     * Initializes a Storage handler with the specified file path.
     *
     * @param filePath Path of the file where tasks are saved.
     */
    public Storage(String filePath) {
        assert filePath != null : "Storage file path must not be null";
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the local file system.
     *
     * @return List of tasks loaded from the file.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(" \\| ");
                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task task = null;
                if (type.equals("T")) {
                    task = new ToDo(description);
                } else if (type.equals("D")) {
                    LocalDateTime by = LocalDateTime.parse(parts[3], DateFormats.INPUT_FORMAT);
                    task = new Deadline(description, by);
                } else if (type.equals("E")) {
                    LocalDateTime from = LocalDateTime.parse(parts[3], DateFormats.INPUT_FORMAT);
                    LocalDateTime to = LocalDateTime.parse(parts[4], DateFormats.INPUT_FORMAT);
                    task = new Event(description, from, to);
                }

                if (task != null) {
                    if (isDone) {
                        task.markAsDone();
                    }
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException | DateTimeParseException | IndexOutOfBoundsException e) {
            System.out.println("     Error loading file: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Saves the current list of tasks to the local file system.
     *
     * @param tasks List of tasks to be saved.
     */
    public void save(ArrayList<Task> tasks) {
        assert tasks != null : "Task list must not be null";
        try {
            File dir = new File("./data");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                for (Task task : tasks) {
                    assert task != null : "Task list must not contain null tasks";
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("     Error saving tasks: " + e.getMessage());
        }
    }
}
