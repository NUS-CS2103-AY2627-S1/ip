package sioet.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sioet.task.Deadline;
import sioet.task.Event;
import sioet.task.RecurringTask;
import sioet.task.Task;
import sioet.task.TaskList;
import sioet.task.Todo;

/**
 * Handles saving tasks to the hard disk.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "sioet.txt");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks the tasks to save
     */
    public void save(TaskList tasks) {
        try {
            Files.createDirectories(FILE_PATH.getParent());

            List<String> lines = new ArrayList<>();

            for (Task task : tasks) {
                if (task instanceof Todo) {
                    lines.add("T | " + (task.isDone() ? "1" : "0")
                            + " | " + task.getDescription());
                } else if (task instanceof Deadline deadline) {
                    lines.add("D | " + (task.isDone() ? "1" : "0")
                            + " | " + task.getDescription()
                            + " | " + deadline.getBy().format(DATE_TIME_FORMATTER));
                } else if (task instanceof Event event) {
                    lines.add("E | " + (task.isDone() ? "1" : "0")
                            + " | " + task.getDescription()
                            + " | " + event.getFrom().format(DATE_TIME_FORMATTER)
                            + " | " + event.getTo().format(DATE_TIME_FORMATTER));
                } else if (task instanceof RecurringTask recurringTask) {
                    lines.add("R | " + (task.isDone() ? "1" : "0")
                            + " | " + task.getDescription()
                            + " | " + recurringTask.getRecurrence());
                }
            }

            Files.write(FILE_PATH, lines);
        } catch (IOException exception) {
            throw new RuntimeException("Could not save tasks.", exception);
        }
    }

    /**
     * Loads tasks from the data file.
     *
     * @return the tasks stored in the data file
     */
    public TaskList load() {
        TaskList tasks = new TaskList();

        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(FILE_PATH)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(" \\| ", -1);

                try {
                    String type = parts[0];
                    boolean isDone = parts[1].equals("1");
                    String description = parts[2];

                    Task task;

                    if (type.equals("T") && parts.length == 3) {
                        task = new Todo(description);
                    } else if (type.equals("D") && parts.length == 4) {
                        task = new Deadline(
                                description,
                                LocalDateTime.parse(parts[3], DATE_TIME_FORMATTER)
                        );
                    } else if (type.equals("E") && parts.length == 5) {
                        task = new Event(
                                description,
                                LocalDateTime.parse(parts[3], DATE_TIME_FORMATTER),
                                LocalDateTime.parse(parts[4], DATE_TIME_FORMATTER)
                        );
                    } else if (type.equals("R") && parts.length == 4) {
                        task = new RecurringTask(
                                description,
                                parts[3]
                        );
                    } else {
                        continue;
                    }

                    if (isDone) {
                        task.markAsDone();
                    }

                    tasks.add(task);
                } catch (RuntimeException exception) {
                    // Skip corrupted task entries.
                }
            }
        } catch (IOException exception) {
            System.out.println("Warning: Could not load saved tasks.");
        }

        return tasks;
    }

}
