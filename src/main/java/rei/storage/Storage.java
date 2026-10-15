package rei.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import rei.task.Deadlines;
import rei.task.Events;
import rei.task.Task;

/** Saves and loads Rei tasks using a file relative to the project directory. */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private final Path filePath;

    /**
     * Creates storage that uses the supplied data file.
     *
     * @param filePath Location of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Load all valid tasks and ignore corrupted records.
     * Missing files are treated as an empty task list.
     */
    public LoadResult load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        int skippedLines = 0;
        if (!Files.exists(filePath)) {
            return new LoadResult(tasks, skippedLines);
        }

        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            try {
                tasks.add(deserialise(line));
            } catch (IllegalArgumentException | DateTimeException exception) {
                skippedLines++;
            }
        }
        return new LoadResult(tasks, skippedLines);
    }

    /** Writes the complete task list, creating its parent folder when needed. */
    public void save(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = tasks.stream().map(this::serialise).toList();
        Path temporaryFile = Files.createTempFile(filePath.toAbsolutePath().getParent(), "rei-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Converts one task into a delimiter-separated record with safely encoded text. */
    private String serialise(Task task) {
        List<String> fields = new ArrayList<>();
        fields.add(task.getTaskType());
        fields.add(task.isDone() ? "1" : "0");
        fields.add(encode(task.getDescription()));
        if (task instanceof Deadlines deadline) {
            fields.add(encode(deadline.getBy().toString()));
        } else if (task instanceof Events event) {
            fields.add(encode(event.getStart().toString()));
            fields.add(encode(event.getEnd().toString()));
        }
        if (!task.getTags().isEmpty()) {
            fields.add(encode(String.join(",", task.getTags())));
        }
        return String.join(FIELD_SEPARATOR, fields);
    }

    /** Reconstructs one task and rejects records that do not match the expected format. */
    private Task deserialise(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task record");
        }

        Task task = switch (fields[0]) {
            case "T" -> {
                requireLength(fields, 3, 4);
                yield new Task(decodeRequired(fields[2]));
            }
            case "D" -> {
                requireLength(fields, 4, 5);
                yield new Deadlines(decodeRequired(fields[2]),
                        LocalDateTime.parse(decodeRequired(fields[3])));
            }
            case "E" -> {
                requireLength(fields, 5, 6);
                yield new Events(decodeRequired(fields[2]),
                        LocalDateTime.parse(decodeRequired(fields[3])),
                        LocalDateTime.parse(decodeRequired(fields[4])));
            }
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (task instanceof Events event && !event.getEnd().isAfter(event.getStart())) {
            throw new IllegalArgumentException("Event end must be after its start");
        }
        int legacyLength = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (fields.length == legacyLength + 1) {
            loadTags(task, decode(fields[legacyLength]));
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Ensures a record has exactly the number of fields required by its task type. */
    private void requireLength(String[] fields, int legacyLength, int taggedLength) {
        if (fields.length != legacyLength && fields.length != taggedLength) {
            throw new IllegalArgumentException("Wrong number of fields");
        }
    }

    /** Restores comma-separated tags and rejects invalid or duplicate entries. */
    private void loadTags(Task task, String tagText) {
        if (tagText.isEmpty()) {
            return;
        }
        for (String tag : tagText.split(",", -1)) {
            if (!task.addTag(tag)) {
                throw new IllegalArgumentException("Duplicate task tag");
            }
        }
    }

    /** Encodes text so task descriptions cannot conflict with storage delimiters. */
    private String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes text that was previously encoded for safe storage. */
    private String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }

    /** Decodes a required text field and rejects empty or whitespace-only values. */
    private String decodeRequired(String text) {
        String decoded = decode(text);
        if (decoded.isBlank()) {
            throw new IllegalArgumentException("Required task field is empty");
        }
        return decoded;
    }

    /**
     * Contains loaded tasks and the number of malformed records that were skipped.
     *
     * @param tasks Valid tasks reconstructed from storage.
     * @param skippedLines Number of malformed records ignored while loading.
     */
    public record LoadResult(List<Task> tasks, int skippedLines) {
    }
}
