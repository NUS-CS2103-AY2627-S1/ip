package nimbus.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import nimbus.task.Deadline;
import nimbus.task.Event;
import nimbus.task.Task;
import nimbus.task.Todo;

/** Loads and saves tasks in a local data file. */
public class Storage {
    private final Path filePath;

    /** Creates storage backed by the given relative file path. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /** Loads all valid task records and reports records that could not be recovered. */
    public LoadResult load() throws IOException {
        createFileIfMissing();
        ArrayList<Task> tasks = new ArrayList<>();
        int skippedRecordCount = 0;
        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            try {
                tasks.add(decodeTask(line));
            } catch (IllegalArgumentException ignored) {
                // Skip a corrupted record while preserving the remaining valid tasks.
                skippedRecordCount++;
            }
        }
        return new LoadResult(tasks, skippedRecordCount);
    }

    /** Replaces the data file contents with the supplied tasks. */
    public void save(List<Task> tasks) throws IOException {
        createFileIfMissing();
        List<String> lines = tasks.stream().map(this::encodeTask).toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    private void createFileIfMissing() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    private String encodeTask(Task task) {
        String typeCode;
        ArrayList<String> fields = new ArrayList<>();
        fields.add(task.getDescription());
        if (task instanceof Deadline deadline) {
            typeCode = "D";
            fields.add(deadline.getStorageDate());
        } else if (task instanceof Event event) {
            typeCode = "E";
            fields.add(event.getFrom());
            fields.add(event.getTo());
        } else {
            typeCode = "T";
        }
        String encodedFields = fields.stream().map(Storage::encode).reduce((a, b) -> a + "|" + b).orElse("");
        return typeCode + "|" + (task.isDone() ? "1" : "0") + "|" + encodedFields;
    }

    private Task decodeTask(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Incomplete task record");
        }
        Task task = switch (parts[0]) {
            case "T" -> new Todo(decode(requiredPart(parts, 2)));
            case "D" -> new Deadline(decode(requiredPart(parts, 2)), decode(requiredPart(parts, 3)));
            case "E" -> new Event(decode(requiredPart(parts, 2)), decode(requiredPart(parts, 3)),
                    decode(requiredPart(parts, 4)));
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (parts[1].equals("1")) {
            task.markAsDone();
        } else if (!parts[1].equals("0")) {
            throw new IllegalArgumentException("Invalid task status");
        }
        return task;
    }

    private static String requiredPart(String[] parts, int index) {
        if (index >= parts.length) {
            throw new IllegalArgumentException("Incomplete task record");
        }
        return parts[index];
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /** Contains recovered tasks and the number of damaged records skipped while loading. */
    public record LoadResult(List<Task> tasks, int skippedRecordCount) {
        /** Creates an immutable snapshot of a storage load. */
        public LoadResult {
            tasks = List.copyOf(tasks);
        }
    }
}
