package odysseus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves the voyage log at one portable file path.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage for the supplied relative file path.
     *
     * @param filePath location of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks or an empty list when the file has not been created.
     *
     * @return loaded task list.
     * @throws OdysseusException if the saved data cannot be read.
     */
    public TaskList load() throws OdysseusException {
        TaskList tasks = new TaskList();
        if (Files.notExists(filePath)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(filePath)) {
                tasks.addTask(readTask(line));
            }
            return tasks;
        } catch (IOException exception) {
            throw new OdysseusException("I could not load the ship's log.");
        }
    }

    /**
     * Saves every task in the voyage log.
     *
     * @param tasks tasks to persist.
     * @throws OdysseusException if the data cannot be written.
     */
    public void save(TaskList tasks) throws OdysseusException {
        List<String> lines = new ArrayList<>();
        try {
            for (int number = 1; number <= tasks.getTaskCount(); number++) {
                lines.add(writeTask(tasks.getTask(number)));
            }
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines);
        } catch (IOException exception) {
            throw new OdysseusException("I could not save the ship's log.");
        }
    }

    /**
     * Converts one saved line into its corresponding task.
     */
    private Task readTask(String line) throws OdysseusException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new OdysseusException("Invalid saved task");
        }
        assert parts.length >= 3 : "A validated saved task has type, status, and description fields";
        Task task = switch (parts[0]) {
            case "T" -> new Todo(parts[2]);
            case "D" -> deadlineFrom(parts);
            case "E" -> eventFrom(parts);
            default -> throw new OdysseusException("Invalid saved task type");
        };
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Builds a deadline from validated saved-field values.
     */
    private Deadline deadlineFrom(String[] parts) throws OdysseusException {
        if (parts.length != 4) {
            throw new OdysseusException("Invalid saved deadline");
        }
        try {
            return new Deadline(parts[2], LocalDate.parse(parts[3]));
        } catch (DateTimeParseException exception) {
            throw new OdysseusException("Invalid saved deadline");
        }
    }

    /**
     * Builds an event from validated saved-field values.
     */
    private Event eventFrom(String[] parts) throws OdysseusException {
        if (parts.length != 5) {
            throw new OdysseusException("Invalid saved event");
        }
        return new Event(parts[2], parts[3], parts[4]);
    }

    /**
     * Converts one task into the portable saved-line format.
     */
    private String writeTask(Task task) {
        String done = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + done + " | " + task.getDescription() + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            return "E | " + done + " | " + task.getDescription() + " | " + event.getFrom()
                    + " | " + event.getTo();
        }
        return "T | " + done + " | " + task.getDescription();
    }
}
