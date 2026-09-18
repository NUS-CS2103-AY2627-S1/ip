package meowmeow.storage;

import meowmeow.MeowMeowException;
import meowmeow.task.Deadline;
import meowmeow.task.Event;
import meowmeow.task.Task;
import meowmeow.task.TaskDateTime;
import meowmeow.task.Todo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles reading tasks from and writing tasks to a file on disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Constructs a storage handler for the file at the given path.
     *
     * @param filePath Path to the save file, relative to the project root.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Returns the tasks stored in the save file.
     * Returns an empty list if the file does not exist. Lines that cannot be
     * parsed are skipped, and the number skipped is reported to the user.
     *
     * @return Tasks loaded from disk.
     * @throws MeowMeowException If the file exists but cannot be read.
     */
    public ArrayList<Task> load() throws MeowMeowException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            int corruptedCount = 0;

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = parseLine(line);
                if (task == null) {
                    corruptedCount++;
                } else {
                    tasks.add(task);
                }
            }

            if (corruptedCount > 0) {
                System.out.println("     Skipped " + corruptedCount
                        + " unreadable line(s) in the save file.");
            }
        } catch (IOException e) {
            throw new MeowMeowException("I couldn't read your save file: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Returns the task encoded by the given line, or null if the line is malformed.
     *
     * @param line Single line from the save file.
     * @return Decoded task, or null if the line cannot be parsed.
     */
    private Task parseLine(String line) {
        String[] parts = line.split("\\s*\\|\\s*");
        if (!hasValidCommonFields(parts)) {
            return null;
        }

        String description = parts[2];
        try {
            Task task = switch (parts[0]) {
                case "T" -> new Todo(description);
                case "D" -> readDeadline(description, parts);
                case "E" -> readEvent(description, parts);
                default -> throw new MeowMeowException("Unknown task type: " + parts[0]);
            };
            if (parts[1].equals("1")) {
                task.markAsDone();
            }
            return task;
        } catch (MeowMeowException e) {
            return null;
        }
    }

    /**
     * Returns whether the record has the three fields every task type shares:
     * a type, a done flag of 0 or 1, and a non-empty description.
     *
     * @param parts Fields split from one line of the save file.
     * @return True if the shared fields are present and well formed.
     */
    private boolean hasValidCommonFields(String[] parts) {
        return parts.length >= 3
                && !parts[2].isEmpty()
                && (parts[1].equals("0") || parts[1].equals("1"));
    }

    /**
     * Returns the deadline encoded by the given record.
     *
     * @param description Description already read from the record.
     * @param parts Fields split from one line of the save file.
     * @return Decoded deadline.
     * @throws MeowMeowException If the due date is missing or unreadable.
     */
    private Deadline readDeadline(String description, String[] parts) throws MeowMeowException {
        if (parts.length < 4 || parts[3].isEmpty()) {
            throw new MeowMeowException("Deadline record has no due date.");
        }
        return new Deadline(description, TaskDateTime.fromStorage(parts[3]));
    }

    /**
     * Returns the event encoded by the given record.
     *
     * @param description Description already read from the record.
     * @param parts Fields split from one line of the save file.
     * @return Decoded event.
     * @throws MeowMeowException If either time is missing or unreadable, or the
     *         start is not before the end.
     */
    private Event readEvent(String description, String[] parts) throws MeowMeowException {
        if (parts.length < 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
            throw new MeowMeowException("Event record is missing a start or end time.");
        }
        return new Event(description,
                TaskDateTime.fromStorage(parts[3]),
                TaskDateTime.fromStorage(parts[4]));
    }

    /**
     * Writes the given tasks to the save file, overwriting any existing content.
     * Creates the parent folder if it does not yet exist.
     *
     * @param tasks Tasks to write to disk.
     * @throws MeowMeowException If the file cannot be written.
     */
    public void save(ArrayList<Task> tasks) throws MeowMeowException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            String data = tasks.stream()
                    .map(task -> task.toFileFormat() + System.lineSeparator())
                    .collect(Collectors.joining());

            Files.writeString(filePath, data);
        } catch (IOException e) {
            throw new MeowMeowException("I couldn't save your tasks: " + e.getMessage());
        }
    }
}