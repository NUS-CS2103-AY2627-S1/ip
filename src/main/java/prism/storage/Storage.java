package prism.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import prism.PrismException;
import prism.task.Deadline;
import prism.task.Event;
import prism.task.Task;
import prism.task.Todo;

/** Loads tasks from and saves tasks to Prism's data file. */
public class Storage {
    private final String filePath;

    /** Creates storage backed by the specified file path. */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /** Loads valid task entries from disk, skipping malformed entries. */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        File file = prepareFile();

        if (file == null || !file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            int lineNumber = 0;
            while (fileScanner.hasNextLine()) {
                lineNumber++;
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                Task task = parseLine(line);
                if (task == null) {
                    System.out.println("!!! Skipping corrupted entry on line "
                            + lineNumber + " of the data file.");
                    continue;
                }
                tasks.add(task);
            }
        } catch (FileNotFoundException e) {
            System.out.println("!!! Could not find the data file. Starting with an empty list.");
        }

        return tasks;
    }

    /** Converts one serialized data-file line into a task, or {@code null} if invalid. */
    private Task parseLine(String line) {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            return null;
        }

        String typeSymbol = parts[0].trim();
        String doneFlag = parts[1].trim();
        String description = parts[2].trim();

        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            return null;
        }

        Task task = createTaskFromParts(typeSymbol, description, parts);
        if (task == null) {
            return null;
        }

        assert task.getDescription().equals(description) : "Loaded task must retain its description";

        if (doneFlag.equals("1")) {
            task.markAsDone();
        }

        return task;
    }

    /** Creates a task from its serialized type and fields. */
    private Task createTaskFromParts(String typeSymbol, String description, String[] parts) {
        try {
            switch (typeSymbol) {
                case "T":
                    return new Todo(description);

                case "D":
                    if (parts.length < 4) {
                        return null;
                    }
                    return new Deadline(description, parts[3].trim());

                case "E":
                    if (parts.length < 5) {
                        return null;
                    }
                    return new Event(description, parts[3].trim(), parts[4].trim());

                default:
                    return null;
            }
        } catch (PrismException e) {
            return null;
        }
    }

    /** Writes all supplied tasks to disk in the Prism data format. */
    public void save(List<Task> tasks) {
        File file = new File(this.filePath);
        File parentDir = file.getParentFile();

        try {
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("!!! Could not save tasks to disk: " + e.getMessage());
        }
    }

    /** Creates the data file and its parent directory when necessary. */
    private File prepareFile() {
        File file = new File(this.filePath);
        File parentDir = file.getParentFile();

        try {
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            System.out.println("!!! Could not set up the data file. Starting with an empty list.");
            return null;
        }
    }
}
