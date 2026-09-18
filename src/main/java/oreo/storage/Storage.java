package oreo.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import oreo.model.Deadline;
import oreo.model.Event;
import oreo.model.Task;
import oreo.model.TaskList;
import oreo.model.Todo;

/** Loads and saves tasks without exposing file-format details to the command loop. */
public class Storage {
    private final Path taskFile;

    /** Creates storage backed by the supplied file. */
    public Storage(Path taskFile) {
        assert taskFile != null : "Storage requires a path for its task file.";
        this.taskFile = taskFile;
    }

    /** Saves the task list atomically where the file system supports it. */
    public void save(TaskList tasks) {
        Path parent = taskFile.getParent();
        Path temporaryFile = null;
        try {
            if (Files.exists(parent) && !Files.isDirectory(parent)) {
                throw new IOException("The data path is not a directory.");
            }
            Files.createDirectories(parent);
            temporaryFile = Files.createTempFile(parent, "oreo", ".tmp");
            List<String> lines = tasks.stream().map(this::formatTask).toList();
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(temporaryFile, taskFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException | UnsupportedOperationException e) {
                Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            System.err.println("Warning: unable to save tasks: " + e.getMessage());
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // The temporary file can be cleaned up later.
                }
            }
        }
    }

    /** Loads valid saved tasks and ignores malformed records. */
    public List<Task> load() {
        if (!Files.isRegularFile(taskFile)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(taskFile).stream()
                    .map(this::parseTask)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException | SecurityException e) {
            System.err.println("Warning: unable to load tasks: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private String formatTask(Task task) {
        assert task != null : "Only real tasks can be serialized.";
        StringBuilder line = new StringBuilder(task.getTaskType().getMarker())
                .append('|').append(task.getStatusIcon().equals("X") ? "1" : "0")
                .append('|').append(encode(task.getDescription()));
        if (task instanceof Deadline deadline) {
            line.append('|').append(encode(deadline.getBy()));
        } else if (task instanceof Event event) {
            line.append('|').append(encode(event.getFrom())).append('|').append(encode(event.getTo()));
        }
        line.append('|').append(encode(task.getMerchandise()));
        return line.toString();
    }

    private Task parseTask(String line) {
        String[] fields = line.split("\\|", -1);
        try {
            if (fields.length < 3 || !fields[1].matches("[01]")) {
                return parseLegacyTask(line);
            }
            String description = decode(fields[2]);
            Task task;
            if (fields[0].equals("T") && (fields.length == 3 || fields.length == 4)) {
                task = new Todo(description);
            } else if (fields[0].equals("D") && (fields.length == 4 || fields.length == 5)) {
                task = new Deadline(description, decode(fields[3]));
            } else if (fields[0].equals("E") && (fields.length == 5 || fields.length == 6)) {
                task = new Event(description, decode(fields[3]), decode(fields[4]));
            } else {
                return null;
            }
            if (fields[1].equals("1")) {
                task.markAsDone();
            }
            if ((task instanceof Todo && fields.length == 4)
                    || (task instanceof Deadline && fields.length == 5)
                    || (task instanceof Event && fields.length == 6)) {
                String merchandise = decode(fields[fields.length - 1]);
                if (!merchandise.isEmpty()) {
                    task.setMerchandise(merchandise);
                }
            }
            return task;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Task parseLegacyTask(String line) {
        if (line.length() < 7 || line.charAt(0) != '[' || line.charAt(2) != ']'
                || line.charAt(3) != '[') {
            return null;
        }
        boolean done = line.startsWith("[T][X]") || line.startsWith("[D][X]") || line.startsWith("[E][X]");
        String content = line.substring(6).trim();
        try {
            Task task;
            if (line.startsWith("[T]")) {
                task = new Todo(content);
            } else if (line.startsWith("[D]")) {
                int marker = content.lastIndexOf(" (by: ");
                if (marker < 1 || !content.endsWith(")")) {
                    return null;
                }
                task = new Deadline(content.substring(0, marker), content.substring(marker + 6, content.length() - 1));
            } else if (line.startsWith("[E]")) {
                int marker = content.lastIndexOf(" (from: ");
                int separator = content.lastIndexOf(" to: ");
                if (marker < 1 || separator < marker || !content.endsWith(")")) {
                    return null;
                }
                task = new Event(content.substring(0, marker), content.substring(marker + 8, separator),
                        content.substring(separator + 5, content.length() - 1));
            } else {
                return null;
            }
            if (done) {
                task.markAsDone();
            }
            return task;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
