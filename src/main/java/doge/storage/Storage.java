package doge.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import doge.exception.DogeException;
import doge.model.Deadline;
import doge.model.Event;
import doge.model.Task;
import doge.model.TaskList;
import doge.model.Todo;

/** Reads and writes tasks using the application's local data file. */
public class Storage {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "tasks.txt");
    private final Path dataFile;

    /** Creates storage backed by the application's default data file. */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /** Creates storage backed by the supplied data file. */
    Storage(Path dataFile) {
        this.dataFile = Objects.requireNonNull(dataFile);
    }

    /** Saves all tasks to disk, replacing the existing data file. */
    public void save(TaskList taskList) throws DogeException {
        try {
            if (dataFile.getParent() != null) {
                Files.createDirectories(dataFile.getParent());
            }
            List<String> lines = new ArrayList<>();

            for (Task task : taskList.getTasks()) {
                lines.add(task.toStorageString());
            }

            Files.write(dataFile, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException | SecurityException e) {
            throw new DogeException("Could not save tasks.");
        }
    }

    /** Loads tasks from disk, returning an empty list when no data file exists. */
    public TaskList load() throws DogeException {
        TaskList taskList = new TaskList();

        try {
            if (!Files.exists(dataFile)) {
                return taskList;
            }
            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (!line.isBlank()) {
                    taskList.add(convertLineToTask(line));
                }
            }

            return taskList;
        } catch (IOException | SecurityException e) {
            throw new DogeException("Could not load tasks.");
        }
    }

    /** Converts one persisted task line into a task object with its saved status. */
    private Task convertLineToTask(String line) throws DogeException {
        assert line != null && !line.isBlank() : "Only non-blank lines can be converted to tasks";
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw new DogeException("Invalid task data: " + line);
        }

        String type = parts[0];
        String status = parts[1];
        if ((!status.equals("0") && !status.equals("1")) || parts[2].isBlank()) {
            throw new DogeException("Invalid task data: " + line);
        }

        boolean isDone = status.equals("1");
        Task task;

        switch (type) {
            case "T" -> {
                if (parts.length != 3 && parts.length != 4) {
                    throw new DogeException("Invalid todo data: " + line);
                }
                task = new Todo(parts[2]);
            }
            case "D" -> {
                if (parts.length != 4 && parts.length != 5) {
                    throw new DogeException("Invalid deadline data: " + line);
                }
                try {
                    LocalDateTime deadline = LocalDateTime.parse(
                            parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    task = new Deadline(parts[2], deadline);
                } catch (DateTimeParseException e) {
                    throw new DogeException("Invalid deadline data: " + line);
                }
            }
            case "E" -> {
                if (parts.length != 5 && parts.length != 6) {
                    throw new DogeException("Invalid event data: " + line);
                }
                try {
                    LocalDateTime start = LocalDateTime.parse(
                            parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    LocalDateTime end = LocalDateTime.parse(
                            parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    task = new Event(parts[2], start, end);
                } catch (DateTimeParseException e) {
                    throw new DogeException("Invalid event data: " + line);
                }
            }
            default -> throw new DogeException("Unknown task type: " + type);
        }

        assert task != null : "A valid persisted task line must produce a task";
        if (isDone) {
            task.markDone();
        }
        int priorityIndex = type.equals("T") ? 3 : type.equals("D") ? 4 : 5;
        if (parts.length > priorityIndex) {
            try {
                int priority = Integer.parseInt(parts[priorityIndex]);
                if (priority < 0) {
                    throw new NumberFormatException();
                }
                task.setPriority(priority);
            } catch (NumberFormatException e) {
                throw new DogeException("Invalid task priority data: " + line);
            }
        }
        return task;
    }
}
