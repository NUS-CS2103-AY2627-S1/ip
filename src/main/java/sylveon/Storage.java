package sylveon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Handles saving and loading tasks from a file. */
public class Storage {
    private static final String TODO_CODE = "T";
    private static final String DEADLINE_CODE = "D";
    private static final String EVENT_CODE = "E";
    private static final String DONE_CODE = "1";
    private static final String NOT_DONE_CODE = "0";
    private static final String STORAGE_SEPARATOR = " | ";

    private final Path filePath;

    /** Creates a storage object for the specified file. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Saves the supplied tasks to the storage file. */
    public void save(ArrayList<Task> tasks) {
        assert tasks != null : "Tasks to save must not be null";
        List<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            String line;

            if (task instanceof Todo) {
                line = TODO_CODE + STORAGE_SEPARATOR + (task.isDone() ? DONE_CODE : NOT_DONE_CODE)
                        + STORAGE_SEPARATOR + task.getDescription();
            } else if (task instanceof Deadline deadline) {
                line = DEADLINE_CODE + STORAGE_SEPARATOR + (task.isDone() ? DONE_CODE : NOT_DONE_CODE)
                        + STORAGE_SEPARATOR + task.getDescription()
                        + STORAGE_SEPARATOR + deadline.getBy();
            } else if (task instanceof Event event) {
                line = EVENT_CODE + STORAGE_SEPARATOR + (task.isDone() ? DONE_CODE : NOT_DONE_CODE)
                        + STORAGE_SEPARATOR + task.getDescription()
                        + STORAGE_SEPARATOR + event.getFrom()
                        + STORAGE_SEPARATOR + event.getTo();
            } else {
                continue;
            }

            lines.add(line);
        }

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Error: Could not save tasks.");
        }
    }

    /** Loads tasks from the storage file. */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                try {
                    Task task = parseTask(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (DateTimeParseException e) {
                    // Ignore malformed entries so one bad line does not prevent loading valid tasks.
                }
            }

        } catch (IOException e) {
            System.out.println("Error: Could not load tasks.");
        }

        return tasks;
    }

    /**
     * Parses one stored task without treating separators inside its description as field boundaries.
     *
     * @param line the line read from the storage file
     * @return the parsed task, or {@code null} when the line is malformed
     */
    private Task parseTask(String line) {
        int typeSeparator = line.indexOf(STORAGE_SEPARATOR);
        if (typeSeparator == -1) {
            return null;
        }

        int statusStart = typeSeparator + STORAGE_SEPARATOR.length();
        int statusSeparator = line.indexOf(STORAGE_SEPARATOR, statusStart);
        if (statusSeparator == -1) {
            return null;
        }

        String taskType = line.substring(0, typeSeparator);
        String completionStatus = line.substring(statusStart, statusSeparator);
        String taskData = line.substring(statusSeparator + STORAGE_SEPARATOR.length());
        if (!completionStatus.equals(DONE_CODE) && !completionStatus.equals(NOT_DONE_CODE)) {
            return null;
        }

        Task task;
        if (taskType.equals(TODO_CODE)) {
            task = new Todo(taskData);
        } else if (taskType.equals(DEADLINE_CODE)) {
            int dateSeparator = taskData.lastIndexOf(STORAGE_SEPARATOR);
            if (dateSeparator == -1) {
                return null;
            }
            String description = taskData.substring(0, dateSeparator);
            String dateText = taskData.substring(dateSeparator + STORAGE_SEPARATOR.length()).trim();
            task = new Deadline(description, LocalDate.parse(dateText));
        } else if (taskType.equals(EVENT_CODE)) {
            int toSeparator = taskData.lastIndexOf(STORAGE_SEPARATOR);
            if (toSeparator == -1) {
                return null;
            }
            int fromSeparator = taskData.lastIndexOf(STORAGE_SEPARATOR, toSeparator - 1);
            if (fromSeparator == -1) {
                return null;
            }
            String description = taskData.substring(0, fromSeparator);
            String fromText = taskData.substring(fromSeparator + STORAGE_SEPARATOR.length(), toSeparator).trim();
            String toText = taskData.substring(toSeparator + STORAGE_SEPARATOR.length()).trim();
            LocalDate from = LocalDate.parse(fromText);
            LocalDate to = LocalDate.parse(toText);
            if (to.isBefore(from)) {
                return null;
            }
            task = new Event(description, from, to);
        } else {
            return null;
        }

        if (completionStatus.equals(DONE_CODE)) {
            task.markAsDone();
        }
        return task;
    }
}
