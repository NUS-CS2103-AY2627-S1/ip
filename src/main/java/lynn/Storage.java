package lynn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/** Handles persistent storage of Lynn's task list. */
final class Storage {
    private static final Path DEFAULT_STORAGE_PATH = Path.of("data", "lynn.txt");

    private final Path storagePath;

    Storage() {
        this(DEFAULT_STORAGE_PATH);
    }

    /** Creates storage backed by the supplied path, which is useful for isolated tests. */
    Storage(Path storagePath) {
        this.storagePath = storagePath;
    }

    /** Loads saved tasks, creating the storage directory and file if needed. */
    TaskList load() throws LynnException {
        try {
            createStorageIfMissing();
            List<String> taskLines = Files.readAllLines(storagePath, StandardCharsets.UTF_8);
            Task[] tasks = new Task[TaskList.MAX_TASKS];
            int taskCount = 0;

            for (String taskLine : taskLines) {
                if (taskLine.isBlank()) {
                    continue;
                }
                if (taskCount == tasks.length) {
                    throw new LynnException("Your saved task list is too large to load.");
                }
                tasks[taskCount] = parseTask(taskLine);
                taskCount++;
            }
            return new TaskList(tasks);
        } catch (IOException e) {
            throw new LynnException("I couldn't load your saved tasks.");
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new LynnException("Your saved task data is invalid.");
        }
    }

    /** Saves all current tasks to a relative, OS-independent file path. */
    void save(TaskList tasks) throws LynnException {
        Path temporaryPath = null;
        try {
            createStorageIfMissing();
            List<String> taskLines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                taskLines.add(formatTask(tasks.get(i)));
            }
            Path temporaryDirectory = storagePath.getParent() == null
                    ? Path.of(".") : storagePath.getParent();
            temporaryPath = Files.createTempFile(temporaryDirectory, "lynn-", ".tmp");
            Files.write(temporaryPath, taskLines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryPath, storagePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryPath, storagePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new LynnException("I couldn't save your tasks.");
        } finally {
            if (temporaryPath != null) {
                try {
                    Files.deleteIfExists(temporaryPath);
                } catch (IOException ignored) {
                    // The saved file is already valid; a leftover temporary file is harmless.
                }
            }
        }
    }

    /** Creates the storage directory and file when Lynn runs for the first time. */
    private void createStorageIfMissing() throws IOException {
        Path parent = storagePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (Files.notExists(storagePath)) {
            Files.createFile(storagePath);
        }
    }

    /** Converts one task into a pipe-separated storage record. */
    private static String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        String description = encode(task.getDescription());
        return switch (task.getTaskType()) {
        case TODO -> "T|" + status + "|" + description;
        case DEADLINE -> "D|" + status + "|" + description + "|"
                + encode(((Deadline) task).getBy().toString());
        case EVENT -> "E|" + status + "|" + description + "|"
                + encode(((Event) task).getFrom()) + "|" + encode(((Event) task).getTo());
        };
    }

    /** Converts one saved storage record into the corresponding task object. */
    private static Task parseTask(String taskLine) {
        String[] fields = taskLine.split("\\|", -1);
        if (fields.length < 2) {
            throw new IllegalArgumentException("Missing task status");
        }
        Task task = switch (fields[0]) {
        case "T" -> createTodo(fields);
        case "D" -> createDeadline(fields);
        case "E" -> createEvent(fields);
        default -> throw new IllegalArgumentException("Unknown task type");
        };
        if ("1".equals(fields[1])) {
            task.markAsDone();
        } else if (!"0".equals(fields[1])) {
            throw new IllegalArgumentException("Unknown task status");
        }
        return task;
    }

    private static Todo createTodo(String[] fields) {
        requireFieldCount(fields, 3);
        return new Todo(decode(fields[2]));
    }

    private static Deadline createDeadline(String[] fields) {
        requireFieldCount(fields, 4);
        return new Deadline(decode(fields[2]), LocalDate.parse(decode(fields[3])));
    }

    private static Event createEvent(String[] fields) {
        requireFieldCount(fields, 5);
        return new Event(decode(fields[2]), decode(fields[3]), decode(fields[4]));
    }

    private static void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Incorrect number of fields");
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
