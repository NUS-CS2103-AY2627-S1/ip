package chris;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Loads and saves tasks using an operating-system-independent path. */
public class Storage {
    private final Path filePath;

    /** Creates storage backed by the given relative file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Loads all tasks, returning an empty list when the data file is absent. */
    public ArrayList<Task> load() throws ChrisException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (int index = 0; index < lines.size(); index++) {
                try {
                    tasks.add(parseTask(lines.get(index)));
                } catch (RuntimeException exception) {
                    throw new ChrisException("Invalid saved task on line " + (index + 1)
                            + " of " + filePath + ".", exception);
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new ChrisException("I could not read the saved tasks: " + exception.getMessage());
        }
    }

    /** Saves all tasks, creating the data directory when necessary. */
    public void save(List<Task> tasks) throws ChrisException {
        Path temporaryFile = null;
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path directory = filePath.toAbsolutePath().getParent();
            temporaryFile = Files.createTempFile(directory, "chris-", ".tmp");
            Files.write(temporaryFile, tasks.stream().map(Task::toDataString).toList());
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new ChrisException("I could not save the tasks: " + exception.getMessage());
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // A failed cleanup does not replace the more useful save result.
                }
            }
        }
    }

    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        int expectedFields = switch (fields[0]) {
        case "T" -> 3;
        case "D" -> 4;
        case "E" -> 5;
        default -> throw new IllegalArgumentException("unknown task type");
        };
        if (fields.length != expectedFields || fields[2].isBlank()
                || (!"0".equals(fields[1]) && !"1".equals(fields[1]))) {
            throw new IllegalArgumentException("invalid task record");
        }
        Task task = switch (fields[0]) {
        case "T" -> new Todo(fields[2]);
        case "D" -> new Deadline(fields[2], fields[3]);
        case "E" -> new Event(fields[2], fields[3], fields[4]);
        default -> throw new IllegalArgumentException("unknown task type");
        };
        if ("1".equals(fields[1])) {
            task.markAsDone();
        }
        return task;
    }
}
