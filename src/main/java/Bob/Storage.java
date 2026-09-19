package bob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Loads and saves tasks from the application's data file. */
public class Storage {
    private final Path file;

    /**
     * Creates storage backed by the supplied file path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        file = Path.of(filePath);
    }

    /**
     * Loads valid tasks from the data file.
     *
     * @return the tasks found in the data file, or an empty list if it does
     *         not exist.
     * @throws BobException if the file cannot be read.
     */
    public List<Task> load() throws BobException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(file)) {
            return tasks;
        }
        if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new BobException("I couldn't read your task file.");
        }

        try {
            int lineNumber = 0;
            for (String line : Files.readAllLines(file)) {
                lineNumber++;
                Task task = parseTask(line);
                if (task == null) {
                    throw new BobException("Your task file has invalid data on line " + lineNumber + ".");
                }
                if (tasks.stream().anyMatch(existing -> existing.toStorageString().equals(task.toStorageString()))) {
                    throw new BobException("Your task file contains duplicate tasks.");
                }
                tasks.add(task);
            }
        } catch (NoSuchFileException e) {
            // The file may have been removed after the existence check.
            return tasks;
        } catch (IOException e) {
            throw new BobException("I couldn't load your tasks.");
        }

        return tasks;
    }

    /**
     * Converts one saved line into a task.
     *
     * @param line a line from the task data file.
     * @return the parsed task, or null when the line is malformed.
     */
    private static Task parseTask(String line) {
        assert line != null : "The storage parser expects a file line";
        String[] parts = line.split("\\s*\\|\\s*", 5);
        if (parts.length < 3
                || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            return null;
        }

        String type = parts[0];
        String description = parts[2].trim();
        if (description.isEmpty()) {
            return null;
        }

        Task task = switch (type) {
            case Task.TODO_TYPE -> new Task(description);
            case Task.DEADLINE_TYPE -> parseDeadline(parts, description);
            case Task.EVENT_TYPE -> parseEvent(parts, description);
            default -> null;
        };
        return restoreCompletionState(task, parts[1]);
    }

    private static Task parseDeadline(String[] parts, String description) {
        if (parts.length < 4 || parts[3].trim().isEmpty()) {
            return null;
        }

        try {
            return new Deadline(description, LocalDate.parse(parts[3].trim()));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static Task parseEvent(String[] parts, String description) {
        if (parts.length < 5 || parts[3].trim().isEmpty()
                || parts[4].trim().isEmpty()) {
            return null;
        }

        try {
            return new Event(
                    description,
                    LocalDate.parse(parts[3].trim()),
                    LocalDate.parse(parts[4].trim()));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static Task restoreCompletionState(Task task, String completionFlag) {
        if (task != null && completionFlag.equals(Task.COMPLETE_FLAG)) {
            task.markDone();
        }
        return task;
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks tasks to save.
     * @throws BobException if the directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws BobException {
        if (tasks == null) {
            throw new BobException("There are no tasks to save.");
        }
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new BobException("The task list contains invalid data.");
            }
            lines.add(task.toStorageString());
        }

        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            Files.write(file, lines);
        } catch (IOException e) {
            throw new BobException("I couldn't save your tasks.");
        }
    }
}
