package bobo.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bobo.exception.BoboException;
import bobo.task.Deadline;
import bobo.task.Event;
import bobo.task.Task;
import bobo.task.TaskList;
import bobo.task.Todo;

/**
 * Handles loading tasks from file and saving tasks into file.
 */
public class Storage {

    private final String filePath;

    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.trim().isEmpty() : "File path for storage cannot be null or empty";
        this.filePath = filePath;
    }

    /**
     * Checks if storage file exists; creates file and parent directories if missing.
     */
    public void checkOrCreateFile() {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Could not create storage file: " + e.getMessage());
            }
        }
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return List of parsed tasks.
     * @throws BoboException If a critical read error occurs.
     */
    public List<Task> load() throws BoboException {
        File file = new File(filePath);
        if (!file.exists()) {
            checkOrCreateFile();
            return new ArrayList<>();
        }

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            List<Task> loadedList = lines
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::parseTaskFromLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            assert loadedList != null : "Loaded task list must not be null";
            return loadedList;
        } catch (IOException e) {
            throw new BoboException("Read error while loading file: " + e.getMessage());
        }
    }

    private Task parseTaskFromLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] pieces = line.split("\\|");
        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = pieces[i].trim();
        }

        String type = pieces[0];
        boolean isDone = pieces.length > 1 && pieces[1].equals("1");

        Task task = null;
        switch (type) {
        case "T":
            if (pieces.length > 2) {
                task = new Todo(pieces[2]);
            }
            break;
        case "D":
            if (pieces.length > 3) {
                task = new Deadline(pieces[2], pieces[3]);
            }
            break;
        case "E":
            if (pieces.length > 4) {
                task = new Event(pieces[2], pieces[3], pieces[4]);
            }
            break;
        case "Task":
            if (pieces.length > 2) {
                task = new Task(pieces[2]);
            }
            break;
        default:
            break;
        }

        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Saves tasks from TaskList to the storage file.
     *
     * @param taskList TaskList containing tasks to save.
     * @throws BoboException If a write error occurs.
     */
    public void save(TaskList taskList) throws BoboException {
        assert taskList != null : "TaskList passed to save cannot be null";
        assert filePath != null && !filePath.trim().isEmpty() : "File path for storage cannot be null or empty";
        checkOrCreateFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : taskList.getTasks()) {
                writer.write(task.toTextLine());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new BoboException("Write error while saving file: " + e.getMessage());
        }
    }
}
