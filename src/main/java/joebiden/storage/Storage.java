package joebiden.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import joebiden.exception.JoeBidenException;
import joebiden.task.Deadline;
import joebiden.task.Event;
import joebiden.task.Task;
import joebiden.task.Todo;


/**
 * Manages storing and reading data
 */
public class Storage {

    private static final Path FILE_PATH =
            Paths.get("src", "main", "data", "joebiden.txt");

    /**
     * Saves the given list of tasks to the data file.
     *
     * @param list List of tasks to save.
     * @throws JoeBidenException If the tasks cannot be saved.
     */
    public static void saveList(ArrayList<Task> list)
            throws JoeBidenException {
        assert list != null : "Task list to save should not be null";
        try {
            Files.createDirectories(FILE_PATH.getParent());

            ArrayList<String> lines = new ArrayList<>();

            for (Task task : list) {
                String done = task.isDone() ? "1" : "0";

                if (task instanceof Todo) {
                    lines.add("T | " + done + " | "
                            + task.getName());

                } else if (task instanceof Deadline deadline) {
                    lines.add("D | " + done + " | "
                            + task.getName() + " | "
                            + deadline.getBy());

                } else if (task instanceof Event event) {
                    lines.add("E | " + done + " | "
                            + task.getName() + " | "
                            + event.getFrom() + " | "
                            + event.getTo());
                }
            }

            Files.write(FILE_PATH, lines);

        } catch (IOException e) {
            throw new JoeBidenException(
                    "Failed to save tasks."
            );
        }
    }
    /**
     * Loads previously saved tasks from the data file.
     *
     * @return List of tasks loaded from storage.
     * @throws JoeBidenException If the tasks cannot be loaded.
     */
    public static ArrayList<Task> loadList()
            throws JoeBidenException {

        try {
            ArrayList<Task> tasks = new ArrayList<>();

            if (!Files.exists(FILE_PATH)) {
                Files.createDirectories(FILE_PATH.getParent());
                return tasks;
            }

            for (String line : Files.readAllLines(FILE_PATH)) {

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\s*\\|\\s*");

                if (parts.length < 3) {
                    continue;
                }

                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task task;

                switch (type) {
                    case "T":
                        task = new Todo(description);
                        break;

                    case "D":
                        if (parts.length < 4) {
                            continue;
                        }

                        LocalDateTime by =
                                LocalDateTime.parse(parts[3]);

                        task = new Deadline(description, by);
                        break;

                    case "E":
                        if (parts.length < 5) {
                            continue;
                        }

                        LocalDateTime from =
                                LocalDateTime.parse(parts[3]);

                        LocalDateTime to =
                                LocalDateTime.parse(parts[4]);

                        task = new Event(description, from, to);
                        break;

                    default:
                        continue;
                }

                if (isDone) {
                    task.markDone();
                }

                tasks.add(task);
            }

            return tasks;

        } catch (IOException | DateTimeParseException e) {
            throw new JoeBidenException(
                    "Failed to load tasks."
            );
        }
    }
}
