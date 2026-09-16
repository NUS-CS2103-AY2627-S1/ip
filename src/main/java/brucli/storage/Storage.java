package brucli.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import brucli.task.DateTimes;
import brucli.task.Deadline;
import brucli.task.Event;
import brucli.task.Task;
import brucli.task.Todo;
import brucli.ui.Messages;

/**
 * Loads and saves BruCLI tasks using a text file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage component that uses the supplied file path.
     *
     * @param filePath Location of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves all tasks, replacing the previous contents of the data file.
     */
    public void save(List<Task> tasks) throws IOException {
        StringBuilder output = new StringBuilder();

        for (Task task : tasks) {
            output.append(task.serialize()).append("\n");
        }

        Files.writeString(filePath, output.toString());
    }

    /**
     * Loads tasks from the data file, or returns an empty list if it does not exist.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> loadedTasks = new ArrayList<>();

        if (Files.notExists(filePath)) {
            return loadedTasks;
        }

        try {
            for (String line : Files.readAllLines(filePath)) {
                loadedTasks.add(parseTask(line, loadedTasks.size()));
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new IOException(Messages.malformedTaskFile(), e);
        }

        return loadedTasks;
    }

    /**
     * Reconstructs one task from its saved representation.
     */
    private Task parseTask(String line, int id) {
        String[] parts = line.split(" \\| ");

        String type = parts[0];
        boolean done = parts[1].equals("1");
        String description = parts[2];
        Task task;

        switch (type) {
            case "T":
                task = new Todo(id, description);
                break;
            case "D":
                task = new Deadline(
                        id,
                        description,
                        DateTimes.parseStored(parts[3])
                );
                break;
            case "E":
                task = new Event(
                        id,
                        description,
                        DateTimes.parseStored(parts[3]),
                        DateTimes.parseStored(parts[4])
                );
                break;
            default:
                throw new IllegalArgumentException(Messages.unknownTaskType(type));
        }

        if (done) {
            task.markDone();
        }

        return task;
    }
}
