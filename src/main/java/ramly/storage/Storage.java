package ramly.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import ramly.exception.StorageException;
import ramly.exception.TaskValidationException;
import ramly.model.Deadline;
import ramly.model.Event;
import ramly.model.Task;
import ramly.model.TaskList;
import ramly.model.Todo;

/** Loads tasks from and atomically saves tasks to a UTF-8 file. */
public class Storage {
    private static final int MAX_STORAGE_LINE_LENGTH = 1_000;

    private final Path filePath;
    private final ArrayList<String> warnings = new ArrayList<>();

    /** Creates storage backed by the specified file path. */
    public Storage(String filePath) {
        assert filePath != null : "Storage file path must not be null";
        assert !filePath.isBlank() : "Storage file path must not be blank";
        try {
            this.filePath = Path.of(filePath).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            throw new StorageException("The configured task data path is invalid.", e);
        }
    }

    /** Loads valid tasks and backs up the file before excluding malformed records. */
    public ArrayList<Task> load() {
        warnings.clear();
        ensureFileExists();
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Integer> invalidLines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String record = line.strip();
                if (record.isEmpty()) {
                    continue;
                }
                if (line.length() > MAX_STORAGE_LINE_LENGTH) {
                    invalidLines.add(lineNumber);
                    continue;
                }
                try {
                    Task task = parseTask(record);
                    if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameIdentity(task))) {
                        invalidLines.add(lineNumber);
                    } else {
                        tasks.add(task);
                    }
                } catch (IllegalArgumentException | TaskValidationException e) {
                    invalidLines.add(lineNumber);
                }
            }
        } catch (IOException | SecurityException e) {
            throw new StorageException("I couldn't read the task data file. Check that it is accessible.", e);
        }

        if (!invalidLines.isEmpty()) {
            Path backupPath = backUpCorruptFile();
            warnings.add("I skipped invalid stored data on line(s) " + joinLineNumbers(invalidLines)
                    + ". A backup is at " + backupPath + ".");
        }
        return tasks;
    }

    /** Returns warnings produced by the most recent load. */
    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    /** Converts one serialized storage record into a validated task. */
    private Task parseTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task record");
        }

        Task task = createTask(parts);
        if (parts[1].equals("1")) {
            task.mark();
        }
        return task;
    }

    /** Creates the task subtype identified by a serialized record. */
    private Task createTask(String[] parts) {
        switch (parts[0]) {
        case "T":
            requireFieldCount(parts, 3);
            return new Todo(parts[2]);
        case "D":
            requireFieldCount(parts, 4);
            return Deadline.fromStorage(parts[2], parts[3]);
        case "E":
            requireFieldCount(parts, 5);
            return Event.fromStorage(parts[2], parts[3], parts[4]);
        default:
            throw new IllegalArgumentException("Unknown task type");
        }
    }

    /** Requires a stored record to contain exactly the expected number of fields. */
    private void requireFieldCount(String[] parts, int expectedCount) {
        if (parts.length != expectedCount) {
            throw new IllegalArgumentException("Incorrect task field count");
        }
    }

    /** Saves the current task list without exposing a partially written destination. */
    public void save(TaskList tasks) {
        assert tasks != null : "Saved task list must not be null";
        ensureFileExists();
        Path parentDirectory = filePath.getParent();
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile(parentDirectory, filePath.getFileName().toString(), ".tmp");
            ArrayList<String> records = new ArrayList<>();
            for (Task task : tasks) {
                records.add(task.toFileString());
            }
            Files.write(temporaryFile, records, StandardCharsets.UTF_8);
            replaceAtomically(temporaryFile);
        } catch (IOException | SecurityException e) {
            deleteTemporaryFile(temporaryFile);
            throw new StorageException(
                    "I couldn't save your tasks because the data file is not writable. No changes were applied.", e);
        }
    }

    /** Creates the storage parent and file, rejecting unusable paths. */
    private void ensureFileExists() {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            if (Files.exists(filePath) && !Files.isRegularFile(filePath)) {
                throw new StorageException("The task data path is not a regular file: " + filePath);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
            if (!Files.isReadable(filePath)) {
                throw new StorageException("I can't read the task data file. Check its permissions.");
            }
        } catch (IOException | SecurityException e) {
            throw new StorageException("I couldn't create or access the task data file.", e);
        }
    }

    /** Preserves the original file before malformed records can be excluded by a later save. */
    private Path backUpCorruptFile() {
        Path backupPath = null;
        try {
            backupPath = Files.createTempFile(
                    filePath.getParent(), filePath.getFileName() + ".corrupt-", ".bak");
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            return backupPath;
        } catch (IOException | SecurityException e) {
            deleteTemporaryFile(backupPath);
            throw new StorageException("I found invalid stored data but couldn't create a safe backup.", e);
        }
    }

    /** Moves a complete temporary file into place, with a portable fallback. */
    private void replaceAtomically(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, filePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Best-effort cleanup for a temporary file left by a failed save. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original storage file remains intact even when temporary cleanup fails.
        }
    }

    /** Formats invalid source line numbers for one concise warning. */
    private String joinLineNumbers(List<Integer> lineNumbers) {
        return lineNumbers.stream()
                .map(String::valueOf)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
    }
}
