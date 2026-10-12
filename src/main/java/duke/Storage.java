package duke;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Loads tasks from, and saves tasks to, a text file on disk.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a Storage that reads from and writes to the given file.
     *
     * @param filePath location of the save file, e.g. "data/ducke.txt"
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads the save file and rebuilds the list of tasks.
     * Returns an empty list if the file does not exist yet (e.g. first run).
     *
     * @return the tasks read from the file
     * @throws IOException if the file exists but cannot be read
     * @throws DuckeException if a saved line has an invalid format
     */
    public ArrayList<Task> load() throws IOException, DuckeException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return tasks;
        }

        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.isBlank()) {
                tasks.add(parseTask(line));
            }
        }
        sc.close();
        return tasks;
    }

    /**
     * Converts one saved line back into the matching Task subclass.
     * Expected formats:
     *   T | done | description
     *   D | done | description | by
     *   E | done | description | from | to
     */
    private Task parseTask(String line) throws DuckeException {
        String[] parts = line.split(" \\| ");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String name = parts[2];

        Task task;
        switch (type) {
        case "T" -> task = new Todo(name);
        case "D" -> task = new Deadline(name, parts[3]);
        case "E" -> task = new Event(name, parts[3], parts[4]);
        default -> throw new IllegalArgumentException("Unknown task type in save file: " + type);
        }
        assert task != null : "parseTask must assign a task for a valid type";

        if (isDone) {
            task.markDone();
        }
        return task;
    }

    /**
     * Writes the whole task list to the save file, overwriting previous content.
     * Creates the parent folder (e.g. "data/") if it does not exist.
     *
     * @param tasks the tasks to save
     * @throws IOException if the file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        File file = new File(filePath);

        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        FileWriter fw = new FileWriter(file);
        for (int i = 0; i < tasks.size(); i++) {
            fw.write(tasks.get(i).toSaveFormat() + "\n");
        }
        fw.close();
    }
}
