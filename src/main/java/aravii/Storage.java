package aravii;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Reads legacy TSV records and replaces save files atomically after validation.
 */
public class Storage {
    private final Path dataFile;

    /**
     * Creates storage at a path relative to the application's working directory.
     *
     * @param dataFile The save-file path.
     */
    public Storage(Path dataFile) {
        if (dataFile == null) {
            throw new IllegalArgumentException("Save path must not be null.");
        }
        this.dataFile = dataFile.toAbsolutePath();
    }

    /**
     * Loads all tasks, returning an empty list only when the file is missing.
     *
     * @return The complete task list.
     * @throws IOException If any record is corrupt or the file cannot be read.
     */
    public TaskList load() throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile);
        } catch (NoSuchFileException exception) {
            return new TaskList();
        }
        TaskList tasks = new TaskList();
        for (int index = 0; index < lines.size(); index++) {
            try {
                tasks.add(decode(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid saved task on line " + (index + 1) + ".", exception);
            }
        }
        return tasks;
    }

    /**
     * Writes a complete temporary file before atomically replacing the old save.
     * Filesystems without atomic replacement report an error instead of risking data.
     *
     * @param tasks The tasks to save in their current order.
     * @throws IOException If the new save cannot be written or replaced safely.
     */
    public void save(TaskList tasks) throws IOException {
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks must not be null.");
        }
        List<String> lines = tasks.getTasks().stream().map(this::encode).toList();
        Files.createDirectories(dataFile.getParent());
        Path temporaryFile = Files.createTempFile(dataFile.getParent(), "aravii-", ".tmp");
        try {
            Files.write(temporaryFile, lines);
            Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private String encode(Task task) {
        return task.getType().name() + "\t" + task.isCompleted() + "\t"
                + task.getDescription() + "\t" + task.formatStorageDetails();
    }

    private Task decode(String line) {
        String[] fields = line.split("\\t", -1);
        if (fields.length != 4 || !(fields[1].equals("true") || fields[1].equals("false"))) {
            throw new IllegalArgumentException("Invalid saved task fields.");
        }
        Task task = switch (TaskType.valueOf(fields[0])) {
            case TODO -> {
                if (!fields[3].isEmpty()) {
                    throw new IllegalArgumentException("A todo cannot have date details.");
                }
                yield new Todo(fields[2]);
        }
        case DEADLINE -> new Deadline(fields[2], Parser.parseDate(unwrap(fields[3], "(by: ")));
        case EVENT -> decodeEvent(fields[2], fields[3]);
        };
        if (fields[1].equals("true")) {
            task.mark();
        }
        return task;
    }

    private Event decodeEvent(String description, String details) {
        String[] times = unwrap(details, "(from: ").split(" to: ", -1);
        if (times.length != 2) {
            throw new IllegalArgumentException("Invalid saved event times.");
        }
        return new Event(description, Parser.parseDateTime(times[0]), Parser.parseDateTime(times[1]));
    }

    private String unwrap(String value, String prefix) {
        if (!value.startsWith(prefix) || !value.endsWith(")")) {
            throw new IllegalArgumentException("Invalid saved date details.");
        }
        return value.substring(prefix.length(), value.length() - 1);
    }
}
