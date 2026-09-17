package orbit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Orbit tasks in a local text file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the given relative file path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads all tasks from disk, returning an empty list if the file is absent.
     *
     * @return Loaded tasks.
     * @throws IOException If the data file cannot be read.
     * @throws OrbitException If saved task data is invalid.
     */
    public List<Task> load() throws IOException, OrbitException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }
        List<Task> tasks = new ArrayList<>();
        for (String line : Files.readAllLines(filePath)) {
            tasks.add(parseTask(line));
        }
        return tasks;
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks Tasks to persist.
     * @throws IOException If the tasks cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temporary = Files.createTempFile(filePath.toAbsolutePath().getParent(), "orbit-", ".tmp");
        try {
            Files.write(temporary, tasks.stream().map(Task::toDataString).toList());
            Files.move(temporary, filePath, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /** Validates saved records and translates invalid dates into a recoverable load error. */
    private Task parseTask(String line) throws OrbitException {
        try {
            return parseRecord(line);
        } catch (DateTimeParseException e) {
            throw new OrbitException("Invalid date in saved task: " + line);
        }
    }

    private Task parseRecord(String line) throws OrbitException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new OrbitException("Invalid saved task: " + line);
        }
        int expectedFields = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> 0;
        };
        if (fields.length != expectedFields || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new OrbitException("Invalid saved task: " + line);
        }
        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new OrbitException("Empty field in saved task: " + line);
            }
        }
        Task task;
        switch (fields[0]) {
            case "T":
                task = new Todo(fields[2]);
                break;
            case "D":
                task = new Deadline(fields[2], fields[3]);
                break;
            case "E":
                task = new Event(fields[2], fields[3], fields[4]);
                break;
            default:
                throw new OrbitException("Unknown saved task type: " + fields[0]);
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
