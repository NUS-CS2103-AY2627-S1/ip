package michael;

import task.Deadline;
import task.Task;
import task.Todo;
import task.Event;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Saves and loads the chatbot's tasks from a file on disk.
 */
public class Storage {
    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Writes every task in the current list to the storage file atomically.
     *
     * @param taskList the tasks container to save
     * @throws IOException if the storage directory or file cannot be written
     */
    public void save(TaskList taskList) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Path tempDir = parent != null ? parent : Path.of(".");
        Path temporaryFile = Files.createTempFile(tempDir, "michael-", ".tmp");

        try {
            Files.write(temporaryFile, taskList.getTasks().stream().map(this::formatTask).toList(),
                    StandardCharsets.UTF_8);
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads previously saved tasks, or returns an empty list when no data file exists yet.
     *
     * @return the tasks read from the storage file
     * @throws IOException if the storage file cannot be read
     * @throws MichaelException if a saved task cannot be recreated
     */
    public List<Task> load() throws IOException, MichaelException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(createTask(lines.get(i)));
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                throw new MichaelException("Unable to load task on line " + (i + 1) + ".");
            }
        }
        return tasks;
    }

    private Task createTask(String line) throws MichaelException {
        if (line.startsWith("[")) {
            return createLegacyTask(line);
        }

        String[] fields = line.split("\\|", -1);
        if (fields.length < 3 || fields[0].length() != 1
                || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new MichaelException("Saved task has an invalid format.");
        }

        String taskType = fields[0];
        boolean isDone = fields[1].equals("1");
        Task task;

        if (taskType.equals("T")) {
            requireFieldCount(fields, 3);
            task = new Todo(decode(fields[2]));
        } else if (taskType.equals("D")) {
            requireFieldCount(fields, 4);
            task = new Deadline(decode(fields[2]), LocalDateTime.parse(decode(fields[3])));
        } else if (taskType.equals("E")) {
            requireFieldCount(fields, 5);
            task = new Event(decode(fields[2]), decode(fields[3]), decode(fields[4]));
        } else {
            throw new MichaelException("Saved task has an unknown type.");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private Task createLegacyTask(String line) throws MichaelException {
        if (line.length() < 7 || line.charAt(0) != '[' || line.charAt(2) != ']'
                || line.charAt(3) != '[' || line.charAt(5) != ']' || line.charAt(6) != ' '
                || !(line.charAt(4) == ' ' || line.charAt(4) == 'X')) {
            throw new MichaelException("Saved task has an invalid format.");
        }

        String taskType = line.substring(1, 2);
        String details = line.substring(7);
        Task task;
        if (taskType.equals("T")) {
            task = new Todo(details);
        } else if (taskType.equals("D")) {
            int byIndex = details.lastIndexOf(" (by: ");
            if (byIndex < 0 || !details.endsWith(")")) {
                throw new MichaelException("Saved task has an invalid format.");
            }
            task = new Deadline(
                    details.substring(0, byIndex),
                    LocalDateTime.parse(
                            details.substring(byIndex + 6, details.length() - 1),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm")
                    )
            );
        } else if (taskType.equals("E")) {
            int fromIndex = details.lastIndexOf(" (from: ");
            int toIndex = details.lastIndexOf(" to: ");
            if (fromIndex < 0 || toIndex <= fromIndex + 8 || !details.endsWith(")")) {
                throw new MichaelException("Saved task has an invalid format.");
            }
            task = new Event(details.substring(0, fromIndex),
                    details.substring(fromIndex + 8, toIndex),
                    details.substring(toIndex + 5, details.length() - 1));
        } else {
            throw new MichaelException("Saved task has an unknown type.");
        }

        if (line.charAt(4) == 'X') {
            task.markAsDone();
        }
        return task;
    }

    private String formatTask(Task task) {
        String status = task.getStatusIcon().equals("X") ? "1" : "0";
        if (task instanceof Todo) {
            return "T|" + status + "|" + encode(task.getDescription());
        } else if (task instanceof Deadline deadline) {
            return "D|" + status + "|" + encode(task.getDescription())
                    + "|" + encode(deadline.getBy().toString());
        } else if (task instanceof Event event) {
            return "E|" + status + "|" + encode(task.getDescription()) + "|" + encode(event.getFrom())
                    + "|" + encode(event.getTo());
        }
        throw new IllegalArgumentException("Unsupported task type.");
    }

    private void requireFieldCount(String[] fields, int expectedCount) throws MichaelException {
        if (fields.length != expectedCount) {
            throw new MichaelException("Saved task has an invalid format.");
        }
    }

    private String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String text) {

        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }
}
