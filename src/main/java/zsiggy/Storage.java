package zsiggy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import zsiggy.task.Deadline;
import zsiggy.task.Event;
import zsiggy.task.Task;
import zsiggy.task.Todo;

/**
 * Reads validated task records and replaces saved data only after a complete write.
 */
public class Storage {
    private final Path dataFile;

    public Storage(String filePath) {
        dataFile = Path.of(filePath).toAbsolutePath();
    }

    /**
     * Creates the actual save file's parent directories when necessary.
     */
    public void createDataFile() throws IOException {
        Files.createDirectories(dataFile.getParent());
        if (!Files.exists(dataFile)) {
            Files.createFile(dataFile);
        }
    }

    /**
     * Loads the whole file or reports an invalid record without modifying the file.
     * A missing file represents a new, empty task list.
     */
    public TaskList load() throws IOException {
        TaskList tasks = new TaskList();
        if (Files.notExists(dataFile)) {
            return tasks;
        }
        List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }
            try {
                tasks.add(fromRecord(lines.get(i)));
            } catch (IllegalArgumentException | DateTimeException e) {
                throw new IOException("Invalid saved task on line " + (i + 1) + ".", e);
            }
        }
        return tasks;
    }

    /**
     * Parses the existing pipe-delimited format with strict field validation.
     */
    private Task fromRecord(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))
                || fields[2].isBlank() || fields[2].contains("|")) {
            throw new IllegalArgumentException("Invalid task fields");
        }
        Task task;
        if (fields[0].equals("T") && fields.length == 3) {
            task = new Todo(fields[2]);
        } else if (fields[0].equals("D") && fields.length == 4) {
            task = new Deadline(fields[2], fields[3]);
        } else if (fields[0].equals("E") && fields.length == 5) {
            if (LocalDate.parse(fields[4]).isBefore(LocalDate.parse(fields[3]))) {
                throw new IllegalArgumentException("Event ends before it starts");
            }
            task = new Event(fields[2], fields[3], fields[4]);
        } else {
            throw new IllegalArgumentException("Invalid task type or field count");
        }
        if (fields[1].equals("1")) {
            task.mark();
        }
        return task;
    }

    /**
     * Converts a task to the original on-disk format.
     */
    public static String toRecord(Task task) {
        String statusAndDescription = " | " + (task.isDone() ? "1" : "0") + " | " + task.getDescription();
        if (task instanceof Deadline deadline) {
            return "D" + statusAndDescription + " | " + deadline.getDeadline();
        } else if (task instanceof Event event) {
            return "E" + statusAndDescription + " | " + event.getFromDate() + " | " + event.getToDate();
        }
        return "T" + statusAndDescription;
    }

    /**
     * Writes UTF-8 to a temporary sibling before replacing the original save file.
     */
    public void save(TaskList tasks) throws IOException {
        Files.createDirectories(dataFile.getParent());
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < tasks.getTaskCount(); i++) {
            String record = toRecord(tasks.get(i));
            if (record.contains("\n") || record.contains("\r")) {
                throw new IOException("Task descriptions must stay on one line.");
            }
            try {
                fromRecord(record);
            } catch (IllegalArgumentException | DateTimeException e) {
                throw new IOException("Task cannot be saved safely.", e);
            }
            content.append(record).append(System.lineSeparator());
        }
        Path temporary = Files.createTempFile(dataFile.getParent(), "tasks-", ".tmp");
        try {
            Files.writeString(temporary, content, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, dataFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
