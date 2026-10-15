package minerva.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import minerva.task.Deadline;
import minerva.task.Event;
import minerva.task.Task;
import minerva.task.ToDo;

/** Reads and writes tasks using the pipe-separated storage format. */
public class Storage {
    /** Path of the saved task file. */
    private final String filePath;

    /**
     * Creates storage at the specified path.
     *
     * @param filePath path of the saved task file
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads supported tasks from the saved file.
     *
     * @return tasks read from the file
     * @throws FileNotFoundException if the file cannot be opened
     */
    public ArrayList<Task> load() throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        ArrayList<Task> loadedTasks = new ArrayList<>();
        try (Scanner fileScan = new Scanner(file)) {
            while (fileScan.hasNextLine()) {
                String line = fileScan.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(" \\| ");
                if (parts.length < 2) {
                    continue;
                }

                try {
                    String type = parts[0];
                    boolean isDone = parts[1].equals("1");
                    Task task = null;

                    switch (type) {
                        case "T":
                            if (parts.length >= 3) {
                                task = new ToDo(parts[2]);
                            }
                            break;
                        case "D":
                            if (parts.length >= 4) {
                                task = new Deadline(parts[2], parts[3]);
                            }
                            break;
                        case "E":
                            if (parts.length >= 5) {
                                task = new Event(parts[2], parts[3], parts[4]);
                            }
                            break;
                        default:
                            task = new Task(parts[1]);
                            isDone = parts[0].equals("1");
                            break;
                    }

                    if (task != null) {
                        if (isDone) {
                            task.markDone();
                        }
                        loadedTasks.add(task);
                    }
                } catch (Exception e) {
                    System.out.println("I couldn’t read one saved task, so I skipped it and continued.");
                }
            }
        }
        return loadedTasks;
    }

    /**
     * Saves tasks and reports whether the write succeeded.
     *
     * @param tasks tasks to save
     * @return true if all tasks were written and the file was closed successfully
     */
    public boolean save(ArrayList<Task> tasks) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }
}
