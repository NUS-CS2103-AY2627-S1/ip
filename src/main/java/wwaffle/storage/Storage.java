package wwaffle.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import wwaffle.task.Deadline;
import wwaffle.task.Event;
import wwaffle.task.Task;
import wwaffle.task.Todo;

/**
 * Loads and saves WWaffle tasks in a local text file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Path of the data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the data file.
     *
     * @return Tasks reconstructed from the saved data.
     * @throws IOException If the data file cannot be read or is malformed.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath)) {
            if (!line.isBlank()) {
                tasks.add(parseTask(line));
            }
        }
        return tasks;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }
        // Write completely before replacing the original file to avoid partially saved lists.
        Path target = filePath.toAbsolutePath();
        Path temporary = Files.createTempFile(target.getParent(), "wwaffle-", ".tmp");
        try {
            Files.write(temporary, lines);
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private Task parseTask(String line) throws IOException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            throw new IOException("Invalid task data: " + line);
        }

        if (!(parts[1].equals("0") || parts[1].equals("1")) || parts[2].isBlank()) {
            throw new IOException("Invalid task status or empty description.");
        }
        Task task;
        try {
            task = switch (parts[0]) {
                case "T" -> {
                    if (parts.length != 3) {
                        throw new IOException("Invalid todo fields.");
                    }
                    yield new Todo(parts[2]);
                }
                case "D" -> {
                    if (parts.length != 4) {
                        throw new IOException("Invalid deadline data: " + line);
                    }
                    yield new Deadline(parts[2], parts[3]);
                }
                case "E" -> {
                    if (parts.length != 5 || parts[3].isBlank() || parts[4].isBlank()) {
                        throw new IOException("Invalid event data: " + line);
                    }
                    yield new Event(parts[2], parts[3], parts[4]);
                }
                default -> throw new IOException("Unknown task type: " + parts[0]);
            };
        } catch (DateTimeParseException e) {
            throw new IOException("Invalid deadline date in saved data: " + line, e);
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private String formatTask(Task task) throws IOException {
        String status = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + status + " | " + task.getDescription();
        } else if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + task.getDescription()
                    + " | " + deadline.getDueDate();
        } else if (task instanceof Event event) {
            return "E | " + status + " | " + task.getDescription()
                    + " | " + event.getStart() + " | " + event.getEnd();
        }
        throw new IOException("Unsupported task type");
    }
}
