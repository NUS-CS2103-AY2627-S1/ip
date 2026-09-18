package kopi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Loads tasks from disk and saves tasks to disk. */
public class Storage {
    private final Path filePath;

    /** Creates storage backed by the given data file. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /** Loads tasks, or returns an empty list if the data file does not exist. */
    public List<Task> load() throws KopiException {
        List<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                Task task = parseTask(line);
                if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                    throw new KopiException("The data file contains duplicate tasks.");
                }
                tasks.add(task);
            }
            return tasks;
        } catch (IOException e) {
            throw new KopiException("I couldn't load tasks from " + filePath + ".");
        }
    }

    /** Saves all tasks, creating the data directory when necessary. */
    public void save(List<Task> tasks) throws KopiException {
        Path temporaryFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = tasks.stream().map(Task::toDataString).toList();
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceFile(temporaryFile);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupError) {
                e.addSuppressed(cleanupError);
            }
            throw new KopiException("I couldn't save tasks to " + filePath + ".");
        }
    }

    /** Converts one line from the data file back into a task. */
    private Task parseTask(String line) throws KopiException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new KopiException("The data file contains an invalid task.");
        }

        Task task;
        try {
            switch (fields[0]) {
                case "T":
                    requireFieldCount(fields, 3, "todo");
                    requireDescription(fields[2], "todo");
                    task = new Todo(fields[2]);
                    break;
                case "D":
                    requireFieldCount(fields, 4, "deadline");
                    requireDescription(fields[2], "deadline");
                    task = new Deadline(fields[2], LocalDate.parse(fields[3]));
                    break;
                case "E":
                    requireFieldCount(fields, 5, "event");
                    requireDescription(fields[2], "event");
                    LocalDate startDate = LocalDate.parse(fields[3]);
                    LocalDate endDate = LocalDate.parse(fields[4]);
                    if (!startDate.isBefore(endDate)) {
                        throw new KopiException("The data file contains an invalid event range.");
                    }
                    task = new Event(fields[2], startDate, endDate);
                    break;
                default:
                    throw new KopiException("The data file contains an unknown task type.");
            }
        } catch (DateTimeParseException e) {
            throw new KopiException("The data file contains an invalid date.");
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new KopiException("The data file contains an invalid task status.");
        }
        return task;
    }

    private void requireFieldCount(String[] fields, int expected, String taskType) throws KopiException {
        if (fields.length != expected) {
            throw new KopiException("The data file contains an invalid " + taskType + ".");
        }
    }

    private void requireDescription(String description, String taskType) throws KopiException {
        if (description.isBlank()) {
            throw new KopiException("The data file contains an invalid " + taskType + ".");
        }
    }

    private void replaceFile(Path temporaryFile) throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    filePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
