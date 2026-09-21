package friday.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import friday.task.Deadline;
import friday.task.Event;
import friday.task.Task;
import friday.task.ToDo;

/**
 * Reads and writes UTF-8 task records, keeping file handling separate from the console UI.
 * Each line contains a task type, completion status, description, and any date fields.
 */
public class Storage {
    private final Path file;

    /**
     * Creates storage for the supplied save-file path.
     */
    public Storage(Path file) {
        this.file = file;
    }

    /**
     * Loads all tasks, or returns an empty list on the first run when no file exists.
     * Rejects the entire file on a malformed record so a partial list cannot replace saved data.
     *
     * @throws IOException if the file cannot be read or contains an invalid record.
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    tasks.add(parseTask(line));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid task record on line " + lineNumber + ".", e);
                }
            }
        } catch (NoSuchFileException e) {
            return tasks;
        }
        return tasks;
    }

    /**
     * Writes a complete snapshot to a temporary file before replacing the previous save.
     * Creates missing directories and falls back to a regular move if atomic moves are unsupported.
     *
     * @throws IOException if the snapshot cannot be written or moved into place.
     */
    public void save(List<Task> tasks) throws IOException {
        Path directory = createSaveDirectory();
        Path temporaryFile = Files.createTempFile(directory, "friday-", ".tmp");
        try {
            writeTasks(temporaryFile, tasks);
            replaceSaveFile(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Creates and returns the folder that will contain the task save file. */
    private Path createSaveDirectory() throws IOException {
        Path directory = file.getParent();
        if (directory == null) {
            directory = Path.of(".");
        }
        Files.createDirectories(directory);
        return directory;
    }

    /** Writes each task as one independently encoded record in the temporary snapshot. */
    private void writeTasks(Path temporaryFile, List<Task> tasks) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                String record = formatTask(task);
                assert !record.contains("\n") && !record.contains("\r")
                        : "Each encoded task must occupy exactly one storage record.";
                writer.write(record);
                writer.newLine();
            }
        }
    }

    /** Replaces the destination atomically when supported, otherwise using a regular replacement move. */
    private void replaceSaveFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Encodes task-specific fields without depending on the task's display format.
     */
    private String formatTask(Task task) {
        String fields = "|" + (task.isDone() ? "1" : "0") + "|" + escape(task.getDescription());
        if (task instanceof Deadline deadline) {
            return "D" + fields + "|" + deadline.getBy();
        } else if (task instanceof Event event) {
            return "E" + fields + "|" + event.getFrom() + "|" + event.getTo();
        } else if (task instanceof ToDo) {
            return "T" + fields;
        }
        throw new IllegalArgumentException("Unsupported task type.");
    }

    /**
     * Validates the type, status, field count, and required text before constructing a task.
     */
    private Task parseTask(String line) {
        List<String> fields = splitFields(line);
        if (fields.size() < 3 || !(fields.get(1).equals("0") || fields.get(1).equals("1"))) {
            throw new IllegalArgumentException("Invalid status or missing fields.");
        }
        for (String field : fields) {
            if (field.isBlank()) {
                throw new IllegalArgumentException("Empty task field.");
            }
        }
        Task task;
        String type = fields.getFirst();
        String description = fields.get(2);
        if (type.equals("T") && fields.size() == 3) {
            task = new ToDo(description);
        } else if (type.equals("D") && fields.size() == 4) {
            task = new Deadline(description, fields.get(3));
        } else if (type.equals("E") && fields.size() == 5) {
            task = new Event(description, fields.get(3), fields.get(4));
        } else {
            throw new IllegalArgumentException("Invalid type or field count.");
        }
        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        assert task.isDone() == fields.get(1).equals("1")
                : "A decoded task's completion status must match its stored status field.";
        return task;
    }

    /**
     * Escapes delimiters and line breaks so arbitrary text stays within one record.
     */
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\|")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    /**
     * Splits on unescaped pipes and rejects incomplete or unknown escape sequences.
     */
    private List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;
        for (char character : line.toCharArray()) {
            if (isEscaped) {
                switch (character) {
                    case '\\', '|' -> field.append(character);
                    case 'n' -> field.append('\n');
                    case 'r' -> field.append('\r');
                    default -> throw new IllegalArgumentException("Unknown escape sequence.");
                }
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            throw new IllegalArgumentException("Incomplete escape sequence.");
        }
        fields.add(field.toString());
        return fields;
    }
}
