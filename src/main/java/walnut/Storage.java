package walnut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to Walnut's data file.
 */
public class Storage {
    private Path path = Paths.get("./data/Walnut.txt");
    private Path dir = Paths.get("./data/");

    private Path getPath() {
        return path;
    }

    private Path getDir() {
        return dir;
    }

    /**
     * Returns all stored task records.
     *
     * <p>Creates the data directory and data file if they do not exist.
     *
     * @return List of stored task records.
     * @throws IOException If the data directory or file cannot be accessed.
     */
    public List<String> load() throws IOException {
        if (Files.notExists(getDir())) {
            Files.createDirectories(getDir());
        }
        if (Files.notExists(getPath())) {
            Files.createFile(getPath());
        }
        List<String> data;
        data = Files.readAllLines(getPath());
        return data;
    }

    /**
     * Saves all tasks to Walnut's data file.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data directory or file cannot be accessed.
     */
    public void save(TaskList tasks) throws IOException {
        assert tasks != null : "tasks should not be null";

        if (Files.notExists(getDir())) {
            Files.createDirectories(getDir());
        }
        if (Files.notExists(getPath())) {
            Files.createFile(getPath());
        }

        List<String> data = new ArrayList<>();

        for (Task task : tasks) {
            String line;
            assert task instanceof ToDo
                    || task instanceof Deadline
                    || task instanceof Event
                    : "Unexpected task type";
            if (task instanceof ToDo) {
                line = "T | " + (task.isDone() ? 1 : 0) + " | " + task.getDescription();
            } else if (task instanceof Deadline) {
                line = "D | " + (task.isDone() ? 1 : 0) + " | " + task.getDescription()
                       + " | " + ((Deadline) task).getDeadline();
            } else if (task instanceof Event) {
                line = "E | " + (task.isDone() ? 1 : 0) + " | " + task.getDescription()
                       + " | " + ((Event) task).getEventStartTime() + "-" + ((Event) task).getEventEndTime();
            } else {
                throw new IllegalStateException("Unsupported Task subtype: " + task.getClass().getName());
            }
            data.add(line);
        }
        Files.write(getPath(), data);
    }
}
