package jimbo;

import jimbo.task.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the saving and loading of data.
 */
public class Storage {
    private String file_path;

    /**
     * Initialises Storage.
     *
     * @param file_path The path where data is saved.
     */
    public Storage(String file_path) {
        this.file_path = file_path;
    }

    private void createFileIfNotExists() throws JimboException {
        File f = new File(file_path);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
        } catch (IOException e) {
            throw new JimboException("failed to create file");
        }
        assert f.exists() : "File should be created if no exception thrown";
    }

    /**
     * Saves tasks to file.
     *
     * @param tasks List of tasks currently being tracked.
     * @throws JimboException If there is any error writing to file.
     */
    public void save(List<? extends Task> tasks) throws JimboException {
        createFileIfNotExists();
        StringBuilder serialisedData = new StringBuilder();
        for (Task task : tasks) {
            serialisedData.append(task.serialise());
            serialisedData.append('\n');
        }
        assert new File(file_path).exists() : "File should exist before writing to it";
        try {
            FileWriter fw = new FileWriter(file_path);
            fw.write(serialisedData.toString());
            fw.close();
        } catch (IOException e) {
            throw new JimboException("error while saving :(");
        }
    }

    /**
     * Loads tasks from file.
     *
     * @return The list of tasks loaded.
     * @throws JimboException If there is any error reading from file.
     */
    public List<Task> load() throws JimboException {
        createFileIfNotExists();
        File f = new File(file_path);

        List<Task> tasks = new ArrayList<>();

        try (Scanner s = new Scanner(f)) {
            while (s.hasNext()) {
                tasks.add(Task.deserialise(s.nextLine()));
            }
        } catch (FileNotFoundException e) {
            throw new JimboException("preexisting save file not found");
        }

        return tasks;
    }
}
