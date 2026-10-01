package victoria.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;

import victoria.model.Deadline;
import victoria.model.Event;
import victoria.model.Task;
import victoria.model.TaskList;
import victoria.model.Todo;

/** Reads and writes the task list in a small, human-independent persistence format. */
public final class TaskFile {
    private static final Path FILE = Paths.get("data", "victoria.txt");

    /** Prevents construction because this class exposes only static operations. */
    private TaskFile() { }

    /** Describes the outcome of loading the task file. */
    public enum LoadStatus { LOADED, NO_FILE, EMPTY, NO_VALID_RECORDS, ERROR }

    /** Contains both the load outcome and the number of valid tasks restored. */
    public record LoadResult(LoadStatus status, int loadedTasks) { }

    /** Loads valid records from disk; a missing or damaged file leaves the list usable. */
    public static LoadResult loadInto(TaskList tasks) {
        if (tasks == null) {
            return new LoadResult(LoadStatus.ERROR, 0);
        }
        if (!Files.exists(FILE)) {
            return new LoadResult(LoadStatus.NO_FILE, 0);
        }
        if (!Files.isRegularFile(FILE)) {
            return new LoadResult(LoadStatus.ERROR, 0);
        }
        int loadedTasks = 0;
        try {
            List<String> lines = Files.readAllLines(FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (loadLine(tasks, line)) {
                    loadedTasks++;
                }
            }
        } catch (IOException | SecurityException exception) {
            // Persistence must never prevent the chatbot from starting.
            return new LoadResult(LoadStatus.ERROR, loadedTasks);
        }
        if (linesAreEmpty(FILE)) {
            return new LoadResult(LoadStatus.EMPTY, 0);
        }
        return new LoadResult(loadedTasks > 0 ? LoadStatus.LOADED : LoadStatus.NO_VALID_RECORDS,
                loadedTasks);
    }

    /** Returns whether the persistence file contains no bytes. */
    private static boolean linesAreEmpty(Path file) {
        try {
            return Files.size(file) == 0;
        } catch (IOException | SecurityException exception) {
            return false;
        }
    }

    /** Saves the complete current list, creating the data directory when necessary. */
    public static void save(TaskList tasks) {
        if (tasks == null) {
            return;
        }
        try {
            Files.createDirectories(FILE.getParent());
            StringBuilder contents = new StringBuilder();
            for (int i = 1; i <= tasks.size(); i++) {
                Task task = tasks.getTask(i);
                assert task != null : "Every task number in the list must refer to a task";
                contents.append(encode(task)).append(System.lineSeparator());
            }
            Path temporaryFile = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temporaryFile, contents.toString(), StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, FILE, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException exception) {
                Files.move(temporaryFile, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            // Persistence failures do not terminate the chatbot.
        }
    }

    /** Parses one record and appends it when the record is valid. */
    private static boolean loadLine(TaskList tasks, String line) {
        if (line == null || line.isBlank()) {
            return false;
        }
        try {
            String[] fields = line.split("\\|", -1);
            if (fields.length < 3 || (!fields[1].equals("0") && !fields[1].equals("1"))) {
                return false;
            }
            String description = decode(fields[2]);
            Task task;
            if (fields[0].equals("T") && fields.length == 3) {
                task = new Todo(description);
            } else if (fields[0].equals("D") && fields.length == 4) {
                task = new Deadline(description, decode(fields[3]));
            } else if (fields[0].equals("E") && fields.length == 5) {
                task = new Event(description, decode(fields[3]), decode(fields[4]));
            } else {
                return false;
            }
            int originalSize = tasks.size();
            boolean wasAdded = tasks.add(task);
            if (wasAdded && fields[1].equals("1")) {
                task.markDone();
            }
            assert tasks.size() == originalSize + (wasAdded ? 1 : 0)
                    : "Loading one record must add exactly one task or none";
            return wasAdded;
        } catch (IllegalArgumentException exception) {
            // Skip malformed Base64 or otherwise corrupted records.
            return false;
        }
    }

    /** Encodes a task and its type-specific fields as one persistence record. */
    private static String encode(Task task) {
        String type;
        String[] extra;
        if (task instanceof Deadline deadline) {
            type = "D";
            extra = new String[] { deadline.getBy() };
        } else if (task instanceof Event event) {
            type = "E";
            extra = new String[] { event.getFrom(), event.getTo() };
        } else {
            type = "T";
            extra = new String[0];
        }
        StringBuilder result = new StringBuilder(type).append('|').append(task.isDone() ? '1' : '0')
                .append('|').append(encode(task.getDescription()));
        for (String value : extra) {
            result.append('|').append(encode(value));
        }
        return result.toString();
    }

    /** Encodes text as Base64 so delimiters cannot corrupt a stored record. */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes one Base64 persistence field back into user text. */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

}
