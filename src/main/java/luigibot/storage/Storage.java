package luigibot.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import luigibot.task.Deadline;
import luigibot.task.Event;
import luigibot.task.Task;
import luigibot.task.TaskList;
import luigibot.task.Todo;
import luigibot.ui.Ui;

/**
 * Loads tasks from and saves tasks to the hard disk.
 */
public class Storage {
    private final Path savePath;

    /**
     * Creates a storage manager that uses the given file path.
     *
     * @param filePath path of the task data file.
     */
    public Storage(String filePath) {
        this.savePath = Path.of(filePath);
    }

    /**
     * Writes the current task list to the hard disk.
     *
     * @param tasks tasks to save.
     * @param ui user interface used to report save failures.
     */
    public void save(TaskList tasks, Ui ui) {
        try {
            Files.createDirectories(this.savePath.getParent());
            List<String> taskData = new ArrayList<>();
            for (Task task : tasks.getTasks()) {
                taskData.add(task.toFileString());
            }
            Files.write(this.savePath, taskData);
        } catch (IOException exception) {
            ui.showError("Mamma mia! Luigi couldn't-a save your tasks.");
        }
    }

    /**
     * Loads tasks from the save file, or returns an empty list when it does not exist.
     *
     * @param ui user interface used to report invalid data and read failures.
     * @return tasks reconstructed from the save file.
     */
    public List<Task> load(Ui ui) {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(this.savePath)) {
            return tasks;
        }

        try {
            for (String taskData : Files.readAllLines(this.savePath)) {
                try {
                    tasks.add(parseTask(taskData));
                } catch (IllegalArgumentException | DateTimeParseException exception) {
                    ui.showError("Mamma mia! Luigi skipped-a an invalid saved task.");
                }
            }
        } catch (IOException exception) {
            ui.showError("Mamma mia! Luigi couldn't-a read the task file.");
        }
        return tasks;
    }

    /**
     * Reconstructs one task from its save-file representation.
     *
     * @param taskData save-file representation of one task.
     * @return reconstructed task.
     */
    private Task parseTask(String taskData) {
        String[] fields = taskData.split(" \\| ", -1);
        if (fields.length < 2 || (!fields[1].equals("0") && !fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task status");
        }

        int expectedFieldCount = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException("Invalid task field count");
        }
        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new IllegalArgumentException("Empty task field");
            }
        }

        Task task = switch (fields[0]) {
            case "T" -> new Todo(fields[2]);
            case "D" -> new Deadline(fields[2], fields[3]);
            case "E" -> new Event(fields[2], fields[3], fields[4]);
            default -> throw new IllegalArgumentException("Unknown task type");
        };

        if (fields[1].equals("1")) {
            task.mark();
        }
        return task;
    }
}
