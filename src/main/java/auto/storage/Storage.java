package auto.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import auto.task.Deadline;
import auto.task.Event;
import auto.task.Task;
import auto.task.ToDo;
import auto.util.DateUtil;

/** Loads and saves the task list on the hard disk. */
public class Storage {
    private final String filePath;

    /** Creates a storage manager that reads and writes the specified data file. */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /** Contains recovered tasks and warnings for malformed lines. */
    public record LoadResult(List<Task> tasks, List<String> warnings) {
    }

    /**
     * Atomically replaces the data file with durable task records.
     *
     * @param tasks Current tasks to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path dataFile = getDataFile().toAbsolutePath();
        if (Files.exists(dataFile) && !Files.isRegularFile(dataFile)) {
            throw new IOException("the data path is not a regular file");
        }
        Path parent = dataFile.getParent();
        Files.createDirectories(parent);

        Path temporaryFile = Files.createTempFile(parent, dataFile.getFileName().toString(), ".tmp");
        try {
            Files.write(temporaryFile,
                    tasks.stream().map(Task::toDataString).toList(), StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile, dataFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Replaces the data file, falling back when an atomic replacement fails. */
    private static void replaceDataFile(Path temporaryFile, Path dataFile) throws IOException {
        try {
            Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Some Windows file systems report atomic moves as supported but
            // cannot atomically replace an existing target.
            if (!Files.exists(temporaryFile)) {
                throw e;
            }
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads valid tasks and reports malformed lines without losing good data.
     *
     * @return Recovered tasks and warnings for skipped lines.
     * @throws IOException If the data file cannot be read.
     */
    public LoadResult load() throws IOException {
        Path dataFile = getDataFile();
        if (!Files.exists(dataFile)) {
            return new LoadResult(new ArrayList<>(), new ArrayList<>());
        }
        if (!Files.isRegularFile(dataFile)) {
            throw new IOException("the data path is not a regular file");
        }

        List<Task> tasks = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseDataTask(line));
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                warnings.add(String.format("line %d is invalid and was skipped", i + 1));
            }
        }
        return new LoadResult(tasks, warnings);
    }

    private Path getDataFile() {
        return Path.of(filePath);
    }

    /** Parses and validates one durable task record. */
    private static Task parseDataTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("invalid task record");
        }

        Task task;
        switch (fields[0]) {
            case "T" -> {
                requireFieldCount(fields, 3);
                task = new ToDo(decodeRequired(fields[2]));
            }
            case "D" -> {
                requireFieldCount(fields, 4);
                task = new Deadline(decodeRequired(fields[2]),
                        DateUtil.parseStored(decodeRequired(fields[3])));
            }
            case "E" -> {
                requireFieldCount(fields, 5);
                task = new Event(decodeRequired(fields[2]),
                        DateUtil.parseStored(decodeRequired(fields[3])),
                        DateUtil.parseStored(decodeRequired(fields[4])));
            }
            default -> throw new IllegalArgumentException("unknown task type");
        }
        restoreStatus(task, fields[1]);
        return task;
    }

    private static void requireFieldCount(String[] fields, int expected) {
        if (fields.length != expected) {
            throw new IllegalArgumentException("wrong number of fields");
        }
    }

    /**
     * Decodes a required Base64 text field and rejects blank decoded values.
     *
     * @param encoded Base64-encoded field.
     * @return Decoded nonblank text.
     * @throws IllegalArgumentException if the field is not valid Base64 or
     *                                  decodes to blank text.
     */
    private static String decodeRequired(String encoded) {
        return requireText(new String(
                Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8));
    }

    private static String requireText(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("a required field is empty");
        }
        return value;
    }

    private static void restoreStatus(Task task, String status) {
        // Record validation must reject malformed flags before this helper interprets them.
        assert "0".equals(status) || "1".equals(status) : "Stored status must already be validated";
        if (status.equals("1")) {
            task.mark();
        }
    }
}
