package ada;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles file‑system persistence for tasks.
 * Responsible for loading saved tasks from disk and writing task list back to storage file.
 * Automatically creates the data directory if it does not exist.
 */
public class Storage {
    /**
     * Relative file path used to store serialized task data.
     */
    private static final String FILE_PATH = "./data/Ada.txt";

    /**
     * Constructs a Storage instance. Creates the data folder if it is absent.
     */
    public Storage() {
        File dataFolder = new File("./data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /**
     * Reads task records from local file and reconstructs list of {@code Task} objects.
     * If the file is missing or cannot be read, returns an empty ArrayList.
     *
     * @return list of tasks loaded from disk
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return tasks;
        }
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Task task = parseLineToTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            sc.close();
        } catch (IOException e) {
            System.out.println("File cannot be loaded. Starting with empty task list.");
        }
        return tasks;
    }

    /**
     * Serializes and writes the given task list into the local storage file.
     * Prints warning message when IO error occurs during saving.
     *
     * @param tasks collection of tasks to be persisted
     */
    public void save(ArrayList<Task> tasks) {
        assert tasks != null;

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            for (Task t : tasks) {
                assert t != null;
                fw.write(t.toFileFormat() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Warning: fail to save tasks to file.");
        }
    }

    /**
     * Parses one line from storage file and reconstructs corresponding Task object.
     * Supports Todo, Deadline and Event task types. Returns null for unrecognised format.
     *
     * @param line single raw text‑line read from storage file
     * @return reconstructed Task instance, or null if format is unknown
     */
    Task parseLineToTask(String line) {
        // use '|' to split the String
        String[] parts = line.split(" \\| ");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        if (type.equals("T")) {
            Todo todo = new Todo(description);
            if (isDone) {
                todo.markDone();
            }
            return todo;
        } else if (type.equals("D")) {
            String byStr = parts[3];
            LocalDateTime by = LocalDateTime.parse(byStr);
            Deadline d = new Deadline(description, by);
            if (isDone) {
                d.markDone();
            }
            return d;
        } else if (type.equals("E")) {
            String startStr = parts[3].trim();
            String endStr = parts[4].trim();
            LocalDateTime start = LocalDateTime.parse(startStr);
            LocalDateTime end = LocalDateTime.parse(endStr);
            Event e = new Event(description, start, end);
            if (isDone) {
                e.markDone();
            }
            return e;
        }
        return null;
    }
}
