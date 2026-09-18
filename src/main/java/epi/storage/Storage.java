package epi.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import epi.exception.EpiException;
import epi.task.Deadline;
import epi.task.Event;
import epi.task.Task;
import epi.task.TaskList;
import epi.task.Todo;

/** Reads legacy task records and saves changes without overwriting damaged or externally changed data. */
public class Storage {
    private static final String PROTECTED_FILE = "Meow! Task changes are disabled to protect your file. "
            + "Repair it and restart Epi.";
    private static final String SAVE_ERROR = "Meow! I couldn't save your tasks. No task changes were kept. "
            + "Check the file and its permissions, then try again.";

    private final String filePath;
    private final List<String> warnings = new ArrayList<>();
    private byte[] loadedBytes;
    private boolean loaded;
    private boolean writeBlocked;

    /**
     * Creates storage backed by the specified file path without accessing the file yet.
     *
     * @param filePath Non-null task-file path; relative paths are resolved against the working directory.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves a complete snapshot through a sibling temporary file and an atomic replacement.
     * No direct truncating write or non-atomic fallback is used.
     *
     * @param tasks Proposed complete task list.
     * @throws EpiException If the file is protected, changed externally, cannot be safely replaced,
     *     or an outgoing record has an invalid type, field count, or completion status.
     * @throws java.time.format.DateTimeParseException If an outgoing record contains an invalid date/time.
     * @throws IllegalArgumentException If an outgoing record has an invalid description or event range.
     */
    public void save(TaskList tasks) throws EpiException {
        if (!loaded) {
            load();
        }
        if (writeBlocked) {
            throw new EpiException(PROTECTED_FILE);
        }
        Path temporary = null;
        try {
            Path target = resolvePath();
            requireRegularFile(target);
            if (!Files.isWritable(target)) {
                throw new IOException("The task file is read-only");
            }
            if (!Arrays.equals(loadedBytes, readBytes(target))) {
                writeBlocked = true;
                throw new EpiException("Meow! The task file changed outside Epi. No task changes were kept. "
                        + "Restart Epi to load that file before editing tasks.");
            }
            StringBuilder records = new StringBuilder();
            for (Task task : tasks) {
                String record = task.toFileFormat();
                // Validate the outgoing representation before touching the existing file.
                parseRecord(record);
                records.append(record).append(System.lineSeparator());
            }
            byte[] bytes = records.toString().getBytes(StandardCharsets.UTF_8);
            temporary = Files.createTempFile(target.getParent(), ".epi-", ".tmp");
            Files.write(temporary, bytes);
            replaceFile(temporary, target);
            loadedBytes = bytes;
        } catch (AtomicMoveNotSupportedException e) {
            throw new EpiException("Meow! This folder cannot safely replace my task file. "
                    + "No task changes were kept. Use a local folder that supports atomic file moves.", e);
        } catch (IOException | SecurityException e) {
            throw new EpiException(SAVE_ERROR, e);
        } finally {
            removeTemporaryFile(temporary);
        }
    }

    /**
     * Loads valid records in file order and reports damaged records without changing the file.
     * Any load error blocks saving for this instance until a successful reload or restart.
     * Creates a missing file and its parent directories when possible.
     *
     * @return Valid tasks recovered from the backing file, or an empty list if it cannot be read.
     * @see #getWarnings()
     */
    public TaskList load() {
        TaskList tasks = new TaskList();
        warnings.clear();
        loaded = true;
        writeBlocked = false;
        try {
            prepareFile();
            loadedBytes = readBytes(resolvePath());
            String contents = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(loadedBytes)).toString();
            List<String> records = contents.lines().toList();
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseRecord(records.get(i)));
                } catch (EpiException | DateTimeException | IllegalArgumentException e) {
                    warnings.add("Meow! Task data on line " + (i + 1) + " is invalid. Please repair that record.");
                    writeBlocked = true;
                }
            }
        } catch (EpiException | IOException | SecurityException e) {
            warnings.add("Meow! I couldn't load my task file. Check its path, UTF-8 contents, and permissions.");
            writeBlocked = true;
        }
        if (writeBlocked) {
            warnings.add(PROTECTED_FILE);
        }
        return tasks;
    }

    /**
     * Returns warnings from the most recent load for either the chat window or console.
     *
     * @return An immutable snapshot of warning messages, empty if no load warnings were recorded.
     */
    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    /**
     * Creates missing parent directories and an empty file without overwriting an existing file.
     *
     * @throws EpiException If the path is invalid, inaccessible, a directory, or a symbolic link.
     */
    public void prepareFile() throws EpiException {
        try {
            Path target = resolvePath();
            if (target.getParent() == null) {
                throw new IOException("A filesystem root cannot be used as a task file");
            }
            Files.createDirectories(target.getParent());
            if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                try {
                    Files.createFile(target);
                } catch (FileAlreadyExistsException e) {
                    // Another process may have created it; validate the resulting path below.
                }
            }
            requireRegularFile(target);
        } catch (IOException | SecurityException e) {
            throw new EpiException("Meow! I couldn't prepare my task file. Check its path and permissions.", e);
        }
    }

    /**
     * Validates every legacy field before constructing a task, including its dates and event range.
     *
     * @param record Non-null task record with fields separated by {@code " | "}.
     * @return The corresponding task with its stored completion state restored.
     * @throws EpiException If the task type, field count, or completion status is invalid.
     * @throws java.time.format.DateTimeParseException If a stored date/time has an invalid format or value.
     * @throws IllegalArgumentException If the description or event range is invalid.
     */
    private Task parseRecord(String record) throws EpiException {
        String[] parts = record.split(" \\| ", -1);
        int expectedFields = switch (parts[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> 0;
        };
        if (expectedFields == 0 || parts.length != expectedFields
                || !(parts[1].equals("0") || parts[1].equals("1"))) {
            throw new EpiException("Invalid task record");
        }
        Task task = switch (parts[0]) {
            case "T" -> new Todo(parts[2]);
            case "D" -> new Deadline(parts[2], parts[3]);
            case "E" -> new Event(parts[2], parts[3], parts[4]);
            default -> throw new EpiException("Unknown task type");
        };
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Resolves invalid platform-specific paths as user-facing errors instead of startup crashes.
     *
     * @return The configured file path normalized to an absolute path.
     * @throws EpiException If the configured path is invalid or access to resolve it is denied.
     */
    private Path resolvePath() throws EpiException {
        try {
            return Path.of(filePath).toAbsolutePath().normalize();
        } catch (InvalidPathException | SecurityException e) {
            throw new EpiException("Meow! My task-file path is invalid or inaccessible.", e);
        }
    }

    /**
     * Refuses to replace a directory or symbolic link as though it were an ordinary task file.
     *
     * @param target Non-null path to check without following symbolic links.
     * @throws IOException If the path cannot be confirmed to identify an existing regular file.
     */
    private void requireRegularFile(Path target) throws IOException {
        if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Expected a regular task file");
        }
    }

    /**
     * Reads the exact file bytes; package access allows deterministic I/O-failure tests.
     *
     * @param target Non-null path of the file to read.
     * @return All bytes in the file, without decoding or changing line endings.
     * @throws IOException If the file cannot be read.
     */
    byte[] readBytes(Path target) throws IOException {
        return Files.readAllBytes(target);
    }

    /**
     * Performs the only replacement operation; package access allows simulated write-failure tests.
     *
     * @param source Non-null path of the completed temporary file.
     * @param target Non-null task-file path to replace atomically.
     * @throws AtomicMoveNotSupportedException If the filesystem cannot perform the move atomically.
     * @throws IOException If the replacement fails for another I/O reason.
     */
    void replaceFile(Path source, Path target) throws IOException {
        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Removes only the scratch file created by this save, without masking its original failure.
     * Cleanup failures are ignored; the original task file is never removed here.
     *
     * @param temporary Scratch-file path, or {@code null} if no temporary file was created.
     */
    private void removeTemporaryFile(Path temporary) {
        if (temporary == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporary);
        } catch (IOException | SecurityException e) {
            // Retain the uniquely named scratch file if cleanup is denied; never remove the task file.
        }
    }
}
