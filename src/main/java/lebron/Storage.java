package lebron;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles saving tasks to and loading tasks from a data file.
 */
public class Storage {

    private final Path filePath;

    /**
     * Creates a storage object using the specified folder and file name.
     *
     * @param folderName name of the folder containing the data file
     * @param fileName name of the file used to store tasks
     */
    public Storage(String folderName, String fileName) {
        this.filePath = Path.of(folderName, fileName);
    }

    /**
     * Loads tasks from the data file.
     * Creates the required folder and file if they do not already exist.
     * Malformed lines are skipped instead of crashing the application.
     *
     * @return task list loaded from the data file
     * @throws IOException if an error occurs while reading or creating the file
     */
    public TaskList loadTasks() throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }

        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
            return new TaskList();
        }

        List<String> lines = Files.readAllLines(filePath);
        TaskList tasks = new TaskList();

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            Task task = parseStoredTask(line);

            if (task != null) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Parses one line from the data file.
     *
     * @param line line to parse
     * @return parsed task, or null if the line is malformed
     */
    private Task parseStoredTask(String line) {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3) {
            return null;
        }

        String taskType = parts[0];
        String doneValue = parts[1];

        if (!doneValue.equals("0") && !doneValue.equals("1")) {
            return null;
        }

        Task task;

        switch (taskType) {
            case "T":
                task = parseTodo(parts);
                break;

            case "D":
                task = parseDeadline(parts);
                break;

            case "E":
                task = parseEvent(parts);
                break;

            default:
                return null;
        }

        if (task == null) {
            return null;
        }

        if (doneValue.equals("1")) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Parses a stored todo.
     *
     * @param parts stored task fields
     * @return parsed todo, or null if malformed
     */
    private Task parseTodo(String[] parts) {
        if (parts.length != 3 || parts[2].isBlank()) {
            return null;
        }

        return new Todo(parts[2]);
    }

    /**
     * Parses a stored deadline.
     *
     * @param parts stored task fields
     * @return parsed deadline, or null if malformed
     */
    private Task parseDeadline(String[] parts) {
        if (parts.length != 4
                || parts[2].isBlank()
                || parts[3].isBlank()) {
            return null;
        }

        try {
            return new Deadline(
                    parts[2],
                    parts[3]
            );
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a stored event.
     *
     * @param parts stored task fields
     * @return parsed event, or null if malformed
     */
    private Task parseEvent(String[] parts) {
        if (parts.length != 5
                || parts[2].isBlank()
                || parts[3].isBlank()
                || parts[4].isBlank()) {
            return null;
        }

        return new Event(
                parts[2],
                parts[3],
                parts[4]
        );
    }

    /**
     * Saves all tasks in the given task list to the data file.
     *
     * @param tasks task list to save
     * @throws IOException if an error occurs while writing to the file
     */
    public void saveTasks(TaskList tasks) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }

        StringBuilder data = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String done = task.isDone ? "1" : "0";

            if (task instanceof Todo) {
                data.append("T | ")
                        .append(done)
                        .append(" | ")
                        .append(task.description);

            } else if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;

                data.append("D | ")
                        .append(done)
                        .append(" | ")
                        .append(deadline.description)
                        .append(" | ")
                        .append(deadline.by);

            } else if (task instanceof Event) {
                Event event = (Event) task;

                data.append("E | ")
                        .append(done)
                        .append(" | ")
                        .append(event.description)
                        .append(" | ")
                        .append(event.from)
                        .append(" | ")
                        .append(event.to);
            }

            data.append(System.lineSeparator());
        }

        Files.writeString(
                filePath,
                data.toString()
        );
    }
}
