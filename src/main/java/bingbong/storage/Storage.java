package bingbong.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import bingbong.task.Deadline;
import bingbong.task.Event;
import bingbong.task.Task;
import bingbong.task.TaskList;
import bingbong.task.Todo;

/**
 * Handles reading from and writing to the hard disk save file. It keeps tasks safe even after closing the program.
 */
public class Storage {

    private final File file;

    /**
     * Constructs a Storage manager instance targeting a specific file path configuration.
     *
     * @param filePath The relative path location string where data records are read and written.
     */
    public Storage(String filePath) {
        this.file = new File(filePath);
    }

    /**
     * serialises the in-memory task records and saves them onto disk storage. Creates any missing destination parent
     * folder subdirectories automatically.
     *
     * @param tasks The active TaskList tracking data records to write to file.
     */
    public void save(TaskList tasks) {
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(tasks.get(i).formatToFile() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving file!");
        }
    }

    /**
     * Reads saved text record logs from the hard disk and unpacks them into objects. Skips over corrupted rows or
     * unparseable task strings.
     *
     * @return A collection list array of unpacked material tasks initialized from disk logs.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!file.exists()) {
            return tasks;
        }
        try (Scanner s = new Scanner(file)) {
            while (s.hasNextLine()) {
                String[] parts = s.nextLine().split(" \\| ");
                Task task = null;
                if (parts[0].equals("T")) {
                    task = new Todo(parts[2]);
                } else if (parts[0].equals("D")) {
                    task = new Deadline(parts[2], parts[3]);
                } else if (parts[0].equals("E")) {
                    task = new Event(parts[2], parts[3], parts[4]);
                }

                if (task != null) {
                    if (parts[1].equals("1")) {
                        task.mark();
                    }
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading file!");
        }
        return tasks;
    }
}
