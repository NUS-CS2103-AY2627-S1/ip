package yuki.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yuki.exception.YukiException;
import yuki.task.Deadline;
import yuki.task.Event;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.task.ToDo;
import yuki.time.DateTimeParser;

/**
 * Saves and loads Yuki's task list from a text file.
 *
 * <p>Each line uses {@code |} to separate fields. For example:
 * {@code T | 0 | read book}.</p>
 */
public class Storage {
    /** The path is relative to the project root. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "userdata.txt");
    /** File used by this storage instance. */
    private final Path dataFile;
    /** Whether saving is blocked to avoid overwriting data that could not be loaded. */
    private boolean isSaveBlocked;

    /** Creates storage that uses Yuki's default data file. */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates storage backed by the specified file.
     *
     * <p>This package-private constructor lets storage tests use an isolated temporary file.</p>
     *
     * @param dataFile File used to load and save tasks.
     */
    Storage(Path dataFile) {
        this.dataFile = Objects.requireNonNull(dataFile, "dataFile must not be null");
        if (dataFile.getFileName() == null) {
            throw new IllegalArgumentException("dataFile must identify a file");
        }
    }

    /**
     * Loads all tasks from the data file.
     *
     * @return The saved tasks, or an empty list if the file does not exist.
     * @throws YukiException If the file cannot be read or is invalid.
     */
    public ArrayList<Task> loadTasks() {
        List<String> lines;

        try {
            lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            isSaveBlocked = false;
            return new ArrayList<>();
        } catch (IOException | SecurityException e) {
            isSaveBlocked = true;
            throw new YukiException("I couldn't read the saved tasks. Check that the data file "
                    + "is readable and is not a folder.", e);
        }

        try {
            ArrayList<Task> tasks = new ArrayList<>();
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseTask(line, lineIndex + 1);
                if (tasks.stream().anyMatch(savedTask -> savedTask.hasSameDetails(task))) {
                    throw new YukiException(
                            "The saved task on line " + (lineIndex + 1) + " duplicates an earlier task.");
                }
                tasks.add(task);
            }
            isSaveBlocked = false;
            return tasks;
        } catch (YukiException e) {
            isSaveBlocked = true;
            throw e;
        }
    }

    /** Saves the current task list, replacing the old file contents. */
    public void saveTasks(List<Task> tasks) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        new TaskList(tasks); // Validate the complete snapshot before replacing the saved file.
        if (isSaveBlocked) {
            throw new YukiException("I won't overwrite a saved-task file that could not be loaded. "
                    + "Please repair or move the data file, then restart Yuki.");
        }
        Path temporaryFile = null;
        try {
            Path absoluteDataFile = dataFile.toAbsolutePath();
            Path parentDirectory = absoluteDataFile.getParent();
            assert parentDirectory != null : "An absolute data-file path must have a parent";
            Files.createDirectories(parentDirectory);
            List<String> lines = tasks.stream()
                    .map(this::formatTask)
                    .toList();
            temporaryFile = Files.createTempFile(
                    parentDirectory, absoluteDataFile.getFileName().toString(), ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile, absoluteDataFile);
            temporaryFile = null;
        } catch (IOException | SecurityException e) {
            throw new YukiException("I couldn't save the tasks. Check that the data folder is writable "
                    + "and the data file is not open in another program.", e);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Converts one task into a line in the data file. */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";

        if (task instanceof ToDo) {
            return String.join(" | ", "T", status, encode(task.getDescription()));
        }
        if (task instanceof Deadline deadline) {
            return String.join(" | ", "D", status,
                    encode(task.getDescription()), encode(DateTimeParser.formatStored(deadline.getBy())));
        }
        if (task instanceof Event event) {
            return String.join(" | ", "E", status,
                    encode(task.getDescription()), encode(DateTimeParser.formatStored(event.getFrom())),
                    encode(DateTimeParser.formatStored(event.getTo())));
        }
        throw new IllegalArgumentException("Unknown task type");
    }

    /** Converts one saved line back into a task object. */
    private Task parseTask(String line, int lineNumber) {
        String[] parts = line.trim().split("\\s*\\|\\s*", -1);

        try {
            String type = parts[0];
            boolean isDone = switch (parts[1]) {
                case "0" -> false;
                case "1" -> true;
                default -> throw new IllegalArgumentException("Invalid task status");
            };

            Task task = switch (type) {
                case "T" -> requireFields(parts, 3, new ToDo(decode(parts[2])));
                case "D" -> requireFields(parts, 4,
                        new Deadline(decode(parts[2]), DateTimeParser.parseStored(decode(parts[3]))));
                case "E" -> requireFields(parts, 5,
                        new Event(decode(parts[2]), DateTimeParser.parseStored(decode(parts[3])),
                                DateTimeParser.parseStored(decode(parts[4]))));
                default -> throw new IllegalArgumentException("Unknown task type");
            };

            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | DateTimeParseException e) {
            throw new YukiException("The saved task on line " + lineNumber + " is invalid.", e);
        }
    }

    /** Checks that a parsed line has the expected number of fields. */
    private Task requireFields(String[] parts, int expected, Task task) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("Wrong number of fields");
        }
        return task;
    }

    /** Escapes characters that would otherwise be mistaken for field separators. */
    private String encode(String value) {
        return value.replace("%", "%25").replace("|", "%7C");
    }

    /** Restores escaped characters after a task is read from the data file. */
    private String decode(String value) {
        return value.replace("%7C", "|").replace("%25", "%");
    }

    /** Atomically replaces the data file when the file system supports it. */
    private void replaceDataFile(Path temporaryFile, Path absoluteDataFile) throws IOException {
        try {
            Files.move(temporaryFile, absoluteDataFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, absoluteDataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Removes a leftover temporary file without hiding the original save error. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The failed save operation already reports the actionable problem to the user.
        }
    }
}
