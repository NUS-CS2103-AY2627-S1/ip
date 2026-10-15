package cooper.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import cooper.exception.CooperException;
import cooper.task.Deadline;
import cooper.task.Event;
import cooper.task.Task;
import cooper.task.ToDo;

/** Loads complete task collections and saves them using atomic file replacement. */
public class Storage {
    public static final String UNAVAILABLE = "I couldn't load your tasks. Fix the task file and restart me. "
            + "Your saved tasks have not been changed.";
    public static final String SAVE_FAILED = "I couldn't save your tasks. Your change has not been applied.";
    private final Path path;

    /** Creates a storage manager for the given task file. */
    public Storage(String filePath) {
        path = Path.of(filePath).toAbsolutePath();
    }

    /** Loads every task, rejecting unreadable or malformed data without publishing a partial list. */
    public List<Task> loadTasks() {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
            List<Task> result = new ArrayList<>();
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                result.add(decodeTask(line));
            }
            return result;
        } catch (IOException | SecurityException | CooperException e) {
            throw new CooperException(UNAVAILABLE);
        }
    }

    /** Writes a complete candidate before replacing the original; failed writes preserve the original. */
    public void saveTasks(List<Task> tasks) {
        Path temporary = null;
        try {
            Files.createDirectories(path.getParent());
            temporary = Files.createTempFile(path.getParent(), "tasks-", ".tmp");
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                String description = task.getDescription();
                String line = task.toDataString();
                // Legacy records remain unchanged. V2 encodes descriptions that contain pipe delimiters.
                if (description.contains("|")) {
                    int start = line.indexOf(" | ", line.indexOf(" | ") + 3) + 3;
                    String encoded = Base64.getEncoder().encodeToString(description.getBytes(StandardCharsets.UTF_8));
                    line = "V2 | " + line.substring(0, start) + encoded + line.substring(start + description.length());
                }
                decodeTask(line);
                lines.add(line);
            }
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            replaceFile(temporary, path);
        } catch (IOException | SecurityException | CooperException e) {
            throw new CooperException(SAVE_FAILED);
        } finally {
            cleanup(temporary);
        }
    }

    /** Atomic replacement boundary, overridable for deterministic failure tests. */
    protected void replaceFile(Path temporary, Path destination) throws IOException {
        Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /** Best-effort cleanup never masks the outcome of the actual save. */
    private void cleanup(Path temporary) {
        if (temporary != null) {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException | SecurityException ignored) {
                // Temporary files are never loaded as task data.
            }
        }
    }

    /** Decodes legacy or V2 records, validating field counts, status, descriptions, and dates. */
    public Task decodeTask(String line) {
        boolean encoded = line.startsWith("V2 | ");
        String[] fields = (encoded ? line.substring(5) : line).split("\\|", -1);
        try {
            if (fields.length < 3 || (!fields[1].trim().equals("0") && !fields[1].trim().equals("1"))) {
                throw new CooperException(UNAVAILABLE);
            }
            String description = fields[2].strip();
            if (encoded) {
                byte[] bytes = Base64.getDecoder().decode(description);
                description = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
            }
            if (description.isBlank() || description.matches("(?s).*\\R.*")) {
                throw new CooperException(UNAVAILABLE);
            }
            boolean done = fields[1].trim().equals("1");
            String type = fields[0].trim();
            if (type.equals("T") && fields.length == 3) {
                return new ToDo(description, done);
            }
            if (type.equals("D") && fields.length == 4) {
                return new Deadline(description, done, LocalDateTime.parse(fields[3].trim()));
            }
            if (type.equals("E") && fields.length == 5) {
                LocalDateTime start = LocalDateTime.parse(fields[3].trim());
                LocalDateTime end = LocalDateTime.parse(fields[4].trim());
                if (!end.isAfter(start)) {
                    throw new CooperException(UNAVAILABLE);
                }
                return new Event(description, done, start, end);
            }
            throw new CooperException(UNAVAILABLE);
        } catch (DateTimeParseException | IllegalArgumentException | IOException e) {
            throw new CooperException(UNAVAILABLE);
        }
    }
}
