package com.gihun456.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;
import com.gihun456.model.Deadline;
import com.gihun456.model.Event;
import com.gihun456.model.Task;
import com.gihun456.model.Todo;
import com.gihun456.util.DateTimeParser;

/**
 * Saves the current task list to the application's data file.
 */
public class Storage {
    private static final Path DEFAULT_TASK_FILE = Path.of("data", "Gihun456.txt");
    private final Path taskFile;

    public Storage() {
        this(DEFAULT_TASK_FILE);
    }

    public Storage(Path filePath) {
        this.taskFile = filePath;
    }

    public Storage(String filePath) {
        this(Path.of(filePath));
    }

    /**
     * Writes all tasks to disk, replacing the previous snapshot.
     *
     * @param tasks The current task list.
     * @throws GihunException If the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws GihunException {
        assert tasks != null : "Storage cannot save a null task list";
        try {
            Path parentDirectory = taskFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            StringBuilder contents = new StringBuilder();
            for (Task task : tasks) {
                contents.append(formatTask(task)).append(System.lineSeparator());
            }
            Files.writeString(
                    taskFile,
                    contents.toString(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GihunException(ErrorMessages.CANNOT_ACCESS_FILE, e);
        }
    }

    /**
     * Loads tasks from the previous snapshot.
     *
     * @return List of tasks, empty list if previous snapshot does not exist.
     * @throws GihunException If the task could not be loaded.
     */
    public List<Task> load() throws GihunException {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(taskFile)) {
            return tasks;
        }

        try (BufferedReader reader = Files.newBufferedReader(taskFile, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 3) {
                    continue;
                }

                String type = parts[0].trim();
                boolean isDone = parts[1].trim().equals("X");
                String description = parts[2].trim();

                Task task;

                try {
                    switch (type) {
                        case "T":
                            task = new Todo(description);
                            break;

                        case "D":
                            if (parts.length < 4) {
                                continue;
                            }
                            String deadline = parts[3].trim();
                            task = new Deadline(description, DateTimeParser.parseStoredDateTime(deadline));
                            break;

                        case "E":
                            if (parts.length < 5) {
                                continue;
                            }
                            String eventStart = parts[3].trim();
                            String eventEnd = parts[4].trim();

                            task = new Event(
                                    description,
                                    DateTimeParser.parseStoredDateTime(eventStart),
                                    DateTimeParser.parseStoredDateTime(eventEnd));
                            break;

                        default:
                            throw new GihunException(ErrorMessages.CANNOT_PARSE_TASK);
                    }
                } catch (GihunException | DateTimeParseException e) {
                    continue;
                }

                if (isDone) {
                    task.markAsDone();
                }

                tasks.add(task);
            }

            return tasks;
        } catch (IOException e) {
            throw new GihunException(ErrorMessages.CANNOT_ACCESS_FILE, e);
        }
    }

    /**
     * Formats tasks for saving to disk.
     *
     * @param task Task object to be formatted.
     * @return String representation of formatted task, for writing to the file.
     */
    private String formatTask(Task task) {
        assert task != null : "Storage cannot format a null task";
        String type;
        String details = "";
        switch (task) {
            case Deadline deadline -> {
                type = "D";
                details = " | " + DateTimeParser.formatForStorage(deadline.getDueDate());
            }
            case Event event -> {
                type = "E";
                details = " | " + DateTimeParser.formatForStorage(event.getStartDate())
                        + " | " + DateTimeParser.formatForStorage(event.getEndDate());
            }
            default -> {
                type = "T";
            }
        }

        return type + " | " + task.getStatusIcon() + " | " + task.getTaskName() + details;
    }

}
