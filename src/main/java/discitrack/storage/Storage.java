package discitrack.storage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import discitrack.task.Deadline;
import discitrack.task.Event;
import discitrack.task.Task;
import discitrack.task.Todo;

/**
 * Handles loading tasks from disk and saving tasks back to disk.
 */
public class Storage {
    private final String filePath;
    private boolean isLoadBlocked;

    /**
     * Creates a storage manager for the given file path.
     *
     * @param filePath path to the task data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given tasks to the configured data file.
     *
     * @param tasks the tasks to save.
     * @throws IOException if the file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        if (isLoadBlocked) {
            throw new IOException("Saved data could not be loaded; saving is disabled.");
        }
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            String line = taskToFileLine(task);
            if (!task.getTags().isEmpty()) {
                line += " | tags=" + String.join(",", task.getTags());
            }
            lines.add(line);
        }
        Path target = Path.of(filePath).toAbsolutePath();
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(target.getParent(), ".discitrack-", ".tmp");
        try {
            Files.write(temporary, lines, Charset.defaultCharset());
            replaceFile(temporary, target);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * Replaces the data file without exposing a partly written file.
     * No non-atomic fallback is used when the file system cannot support it.
     *
     * @param temporary the fully written temporary file beside the target.
     * @param target the data file to replace.
     * @throws IOException if the atomic replacement fails.
     */
    protected void replaceFile(Path temporary, Path target) throws IOException {
        Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Loads tasks from the configured data file.
     *
     * @return the tasks stored in the data file, or an empty list if the file does not exist.
     * @throws IOException if the file cannot be read or contains malformed data.
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        Path dataFile = Path.of(filePath);
        if (Files.notExists(dataFile)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(dataFile, Charset.defaultCharset());
            for (int i = 0; i < lines.size(); i++) {
                tasks.add(parseTaskFromFileLine(lines.get(i), i + 1));
            }
        } catch (IOException e) {
            isLoadBlocked = true;
            throw e;
        }
        return tasks;
    }

    private Task parseTaskFromFileLine(String line, int lineNumber) throws IOException {
        String[] parts = line.split(" \\| ", -1);
        Task task;
        int fieldCount;
        try {
            task = createTask(parts[0], parts);
            fieldCount = task instanceof Todo ? 3 : task instanceof Deadline ? 4 : 5;
            if (parts[1].equals("1")) {
                task.markAsDone();
            } else if (!parts[1].equals("0")) {
                throw new IllegalArgumentException("Invalid status");
            }
        } catch (DateTimeParseException e) {
            throw new IOException("invalid date '" + e.getParsedString() + "' on line " + lineNumber
                    + "; use a real date in yyyy-MM-dd format", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("invalid task data on line " + lineNumber + ": " + e.getMessage(), e);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("invalid task data on line " + lineNumber, e);
        }
        if (parts.length > fieldCount) {
            try {
                if (parts.length != fieldCount + 1 || !parts[fieldCount].startsWith("tags=")) {
                    throw new IllegalArgumentException("Unexpected tag field");
                }
                task.replaceTags(Arrays.asList(parts[fieldCount].substring(5).split(",", -1)));
            } catch (IllegalArgumentException e) {
                throw new IOException("invalid tag data on line " + lineNumber, e);
            }
        }
        return task;
    }

    private Task createTask(String taskType, String[] parts) {
        if (taskType.equals("T")) {
            return new Todo(parts[2]);
        } else if (taskType.equals("D")) {
            return new Deadline(parts[2], LocalDate.parse(parts[3]));
        } else if (taskType.equals("E")) {
            return new Event(parts[2], LocalDate.parse(parts[3]),
                    LocalDate.parse(parts[4]));
        } else {
            throw new IllegalArgumentException(
                    "Unsupported task type in data file: " + taskType);
        }
    }

    /**
     * Converts a task into the text format used in the data file.
     *
     * @param task the task to convert.
     * @return the file line representing the task.
     */
    private String taskToFileLine(Task task) {
        String status = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + status + " | " + task.getActivity();
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + status + " | " + deadline.getActivity() + " | " + deadline.getTime();
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + status + " | " + event.getActivity()
                    + " | " + event.getFrom() + " | " + event.getTo();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported task type: " + task.getClass().getSimpleName());
        }
    }
}
