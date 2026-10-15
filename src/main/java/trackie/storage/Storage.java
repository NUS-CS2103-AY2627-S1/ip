package trackie.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import trackie.exception.TrackieException;
import trackie.task.Deadline;
import trackie.task.Event;
import trackie.task.Task;
import trackie.task.Todo;
import trackie.task.WithinPeriod;

/**
 * Saves Trackie's tasks to a file on the local disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that uses the given OS-independent path.
     *
     * @param filePath path of the task data file
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks tasks to save
     * @throws TrackieException if the folder or file cannot be written
     */
    public void saveTasks(List<Task> tasks) throws TrackieException {
        try {
            Path parentDirectory = this.filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            List<String> taskLines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            Files.write(this.filePath, taskLines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new TrackieException("Oops! I couldn't save your tasks.");
        }
    }

    /**
     * Loads tasks from the data file, or returns an empty list on first launch.
     *
     * @return tasks reconstructed from the data file
     * @throws TrackieException if the file cannot be read or contains an unknown task type
     */
    public ArrayList<Task> loadTasks() throws TrackieException {
        if (Files.notExists(this.filePath)) {
            return new ArrayList<>();
        }

        try {
            ArrayList<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(this.filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                tasks.add(parseTask(lines.get(i), i + 1));
            }
            return tasks;
        } catch (IOException exception) {
            throw new TrackieException("Oops! I couldn't load your saved tasks.");
        }
    }

    /**
     * Reconstructs one task from a line in the data file.
     *
     * @param line serialized task
     * @param lineNumber one-based line number used in error messages
     * @return reconstructed task
     * @throws TrackieException if the serialized task is malformed
     */
    private Task parseTask(String line, int lineNumber) throws TrackieException {
        String[] fields = line.split(" \\| ", -1);
        int expectedFieldCount = getExpectedFieldCount(fields[0], lineNumber);
        validateFields(fields, expectedFieldCount, lineNumber);
        Task task = createTask(fields, lineNumber);
        restoreTaskStatus(task, fields[1]);
        return task;
    }

    private int getExpectedFieldCount(String taskType, int lineNumber) throws TrackieException {
        return switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            case "W" -> 5;
            default -> throw corruptedDataException(lineNumber);
        };
    }

    private void validateFields(String[] fields, int expectedFieldCount, int lineNumber)
            throws TrackieException {
        if (fields.length != expectedFieldCount
                || (!fields[1].equals("0") && !fields[1].equals("1"))) {
            throw corruptedDataException(lineNumber);
        }
        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw corruptedDataException(lineNumber);
            }
        }
    }

    private Task createTask(String[] fields, int lineNumber) throws TrackieException {
        try {
            return switch (fields[0]) {
                case "T" -> new Todo(fields[2]);
                case "D" -> new Deadline(fields[2], LocalDate.parse(fields[3]));
                case "E" -> new Event(fields[2], LocalDate.parse(fields[3]), LocalDate.parse(fields[4]));
                case "W" -> new WithinPeriod(fields[2], LocalDate.parse(fields[3]),
                        LocalDate.parse(fields[4]));
                default -> throw corruptedDataException(lineNumber);
            };
        } catch (DateTimeParseException exception) {
            throw corruptedDataException(lineNumber);
        }
    }

    private void restoreTaskStatus(Task task, String status) {
        if (status.equals("1")) {
            task.markAsDone();
        }
    }

    /**
     * Creates a consistent exception for malformed saved data.
     *
     * @param lineNumber one-based location of the malformed data
     * @return exception describing the corrupted line
     */
    private TrackieException corruptedDataException(int lineNumber) {
        return new TrackieException("Oops! Saved task data is corrupted at line " + lineNumber + ".");
    }
}
