package niulai.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import niulai.model.Deadline;
import niulai.model.DuplicateTaskException;
import niulai.model.Event;
import niulai.model.Task;
import niulai.model.TaskList;
import niulai.model.TaskStatus;
import niulai.model.Todo;

/**
 * Saves tasks atomically and recovers valid records from the application's data file.
 */
public class Storage {
    /** Maximum attempts for a move temporarily denied by Windows. */
    private static final int MAX_MOVE_ATTEMPTS = 5;

    /** Initial delay before retrying a denied move. */
    private static final long INITIAL_RETRY_DELAY_MILLISECONDS = 25L;

    /** The file used to store tasks. */
    private final Path filePath;

    /** Performs file moves and provides a controllable boundary for retry tests. */
    private final FileMover fileMover;

    /** Writes recovery backups and provides a controllable boundary for failure tests. */
    private final BackupWriter backupWriter;

    /** Whether a successful load or save established the expected file state. */
    private boolean hasSnapshot;

    /** Whether the file existed when the current snapshot was established. */
    private boolean snapshotFileExists;

    /** SHA-256 digest of the file represented by the current snapshot. */
    private byte[] snapshotDigest;

    /** Original bytes retained until a recovered file is safely backed up. */
    private byte[] recoveryBytes;

    /** Whether an unrecoverable load failure disabled writes for this storage instance. */
    private boolean areWritesBlocked;

    /**
     * Creates a storage component backed by the specified file.
     *
     * @param filePath the path of the task data file
     */
    public Storage(String filePath) {
        this(filePath, Files::move, Storage::writeNewFile);
    }

    /** Creates storage using the supplied file-move operation. */
    Storage(String filePath, FileMover fileMover) {
        this(filePath, fileMover, Storage::writeNewFile);
    }

    /** Creates storage using supplied move and backup operations. */
    Storage(String filePath, FileMover fileMover, BackupWriter backupWriter) {
        this.filePath = Path.of(Objects.requireNonNull(filePath, "filePath"));
        this.fileMover = Objects.requireNonNull(fileMover, "fileMover");
        this.backupWriter = Objects.requireNonNull(backupWriter, "backupWriter");
    }

    /**
     * Writes the complete task list to disk without overwriting unexpected external changes.
     *
     * @param tasks the current task list
     * @throws IOException if the data directory or file cannot be written safely
     */
    public void save(TaskList tasks) throws IOException {
        Objects.requireNonNull(tasks, "tasks");
        Path temporaryFile = null;
        StorageException failure = null;

        try {
            verifySaveIsSafe();
            createRecoveryBackupIfNeeded();
            byte[] serializedTasks = serialize(tasks);
            Path dataDirectory = getDataDirectory();
            Files.createDirectories(dataDirectory);
            temporaryFile = Files.createTempFile(dataDirectory, "niulai-", ".tmp");
            Files.write(
                    temporaryFile,
                    serializedTasks,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            replaceDataFile(temporaryFile);
            updateSnapshot(true, serializedTasks);
            recoveryBytes = null;
        } catch (StorageException e) {
            failure = e;
        } catch (IOException | SecurityException e) {
            failure = new StorageException(StorageException.SAVE_FAILED_MESSAGE, e);
        } finally {
            failure = deleteTemporaryFile(temporaryFile, failure);
        }

        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Reads the data file, retaining valid unique records and reporting skipped lines.
     *
     * @return the recovered tasks and immutable line issues
     * @throws StorageException if the complete file cannot be read and decoded safely
     */
    public LoadResult load() throws StorageException {
        TaskList tasks = new TaskList();
        ArrayList<LoadIssue> issues = new ArrayList<>();

        try {
            if (Files.notExists(filePath)) {
                areWritesBlocked = false;
                recoveryBytes = null;
                updateSnapshot(false, new byte[0]);
                return new LoadResult(tasks, issues);
            }
            if (!Files.exists(filePath)) {
                throw new AccessDeniedException(filePath.toString());
            }
            if (!Files.isRegularFile(filePath)) {
                throw new IOException("The configured task data path is not a regular file.");
            }

            byte[] originalBytes = Files.readAllBytes(filePath);
            String decodedData = decodeUtf8(originalBytes);
            String[] lines = decodedData.split("\\R", -1);
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                int lineNumber = i + 1;
                if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                if (line.isBlank()) {
                    continue;
                }

                try {
                    tasks.add(parseTask(line, lineNumber));
                } catch (IOException | IllegalArgumentException e) {
                    issues.add(new LoadIssue(lineNumber, describeIssue(e)));
                }
            }

            areWritesBlocked = false;
            recoveryBytes = issues.isEmpty() ? null : originalBytes.clone();
            updateSnapshot(true, originalBytes);
            return new LoadResult(tasks, issues);
        } catch (IOException | SecurityException e) {
            areWritesBlocked = true;
            hasSnapshot = false;
            recoveryBytes = null;
            throw new StorageException(StorageException.READ_BLOCKED_MESSAGE, e);
        }
    }

    /** Returns the serialized UTF-8 representation of a task list. */
    private static byte[] serialize(TaskList tasks) {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            Objects.requireNonNull(task, "tasks cannot contain null");
            lines.add(task.toStorageString());
        }

        if (lines.isEmpty()) {
            return new byte[0];
        }
        String serialized = String.join(System.lineSeparator(), lines)
                + System.lineSeparator();
        return serialized.getBytes(StandardCharsets.UTF_8);
    }

    /** Strictly decodes bytes so corrupt UTF-8 cannot be silently replaced. */
    private static String decodeUtf8(byte[] bytes) throws CharacterCodingException {
        return StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(bytes))
                .toString();
    }

    /** Ensures writes are enabled and the file still matches the session snapshot. */
    private void verifySaveIsSafe() throws StorageException {
        if (areWritesBlocked) {
            throw new StorageException(StorageException.READ_BLOCKED_MESSAGE);
        }
        if (!hasSnapshot) {
            return;
        }

        try {
            boolean fileExists = Files.exists(filePath);
            if (!fileExists && !Files.notExists(filePath)) {
                throw new AccessDeniedException(filePath.toString());
            }
            if (fileExists != snapshotFileExists) {
                throw externalChange();
            }
            if (fileExists) {
                if (!Files.isRegularFile(filePath)) {
                    throw externalChange();
                }
                byte[] currentDigest = digest(Files.readAllBytes(filePath));
                if (!MessageDigest.isEqual(snapshotDigest, currentDigest)) {
                    throw externalChange();
                }
            }
        } catch (StorageException e) {
            throw e;
        } catch (IOException | SecurityException e) {
            throw new StorageException(StorageException.EXTERNAL_CHANGE_MESSAGE, e);
        }
    }

    /** Creates a non-destructive sibling backup when load skipped any records. */
    private void createRecoveryBackupIfNeeded() throws StorageException {
        if (recoveryBytes == null) {
            return;
        }

        try {
            String backupName = filePath.getFileName() + ".bak";
            int suffix = 0;
            while (true) {
                String candidateName = suffix == 0 ? backupName : backupName + "." + suffix;
                Path candidate = filePath.resolveSibling(candidateName);
                try {
                    backupWriter.write(candidate, recoveryBytes.clone());
                    recoveryBytes = null;
                    return;
                } catch (FileAlreadyExistsException e) {
                    suffix++;
                }
            }
        } catch (IOException | SecurityException e) {
            throw new StorageException(StorageException.BACKUP_FAILED_MESSAGE, e);
        }
    }

    /** Writes bytes to a new file without replacing an existing path. */
    private static void writeNewFile(Path path, byte[] bytes) throws IOException {
        Files.write(path, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    /** Returns the data directory, using the working directory for a bare file name. */
    private Path getDataDirectory() {
        Path dataDirectory = filePath.getParent();
        return dataDirectory == null ? Path.of(".") : dataDirectory;
    }

    /** Replaces the data file, falling back when an atomic move is unsupported. */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            moveWithRetry(
                    temporaryFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException e) {
            moveWithRetry(temporaryFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Retries a move when Windows temporarily denies replacement access. */
    private void moveWithRetry(Path temporaryFile, CopyOption... options) throws IOException {
        for (int attempt = 1; attempt <= MAX_MOVE_ATTEMPTS; attempt++) {
            try {
                fileMover.move(temporaryFile, filePath, options);
                return;
            } catch (AccessDeniedException e) {
                if (attempt == MAX_MOVE_ATTEMPTS) {
                    throw e;
                }
                waitBeforeMoveRetry(attempt);
            }
        }
    }

    /** Waits briefly before another file replacement attempt. */
    private static void waitBeforeMoveRetry(int attempt) throws IOException {
        long delay = INITIAL_RETRY_DELAY_MILLISECONDS << (attempt - 1);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while retrying task-file replacement.", e);
        }
    }

    /** Deletes a temporary file while retaining any earlier failure as the primary cause. */
    private static StorageException deleteTemporaryFile(
            Path temporaryFile, StorageException earlierFailure) {
        if (temporaryFile == null) {
            return earlierFailure;
        }

        try {
            Files.deleteIfExists(temporaryFile);
            return earlierFailure;
        } catch (IOException | SecurityException e) {
            if (earlierFailure != null) {
                earlierFailure.addSuppressed(e);
                return earlierFailure;
            }
            return new StorageException(StorageException.SAVE_FAILED_MESSAGE, e);
        }
    }

    /** Updates the external-change snapshot after a complete successful read or write. */
    private void updateSnapshot(boolean fileExists, byte[] bytes) {
        hasSnapshot = true;
        snapshotFileExists = fileExists;
        snapshotDigest = fileExists ? digest(bytes) : null;
    }

    /** Computes the stable digest used for file-change comparisons. */
    private static byte[] digest(byte[] bytes) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("Every Java implementation must provide SHA-256.", e);
        }
    }

    /** Creates the typed error used when the task file no longer matches its snapshot. */
    private static StorageException externalChange() {
        return new StorageException(StorageException.EXTERNAL_CHANGE_MESSAGE);
    }

    /** Converts a skipped line's parsing failure into a concise recovery reason. */
    private static String describeIssue(Exception exception) {
        if (exception instanceof DuplicateTaskException) {
            return "Duplicate task.";
        }
        return exception.getMessage() == null
                ? "Invalid task data."
                : exception.getMessage();
    }

    /**
     * Converts one saved line into a task object.
     *
     * @param line the saved task line
     * @param lineNumber the line's number in the data file
     * @return the reconstructed task
     * @throws IOException if the line does not follow the save format
     */
    private static Task parseTask(String line, int lineNumber) throws IOException {
        ArrayList<String> fields = splitFields(line, lineNumber);
        if (fields.size() < 3) {
            throw invalidLine(lineNumber);
        }

        String description = requireValue(fields.get(2), lineNumber);
        TaskStatus status = parseStatus(fields.get(1), lineNumber);
        Task.TaskType type;
        try {
            type = Task.TaskType.fromIcon(fields.get(0));
        } catch (IllegalArgumentException e) {
            throw invalidLine(lineNumber, e);
        }

        Task task = switch (type) {
            case TODO -> {
                requireFieldCount(fields, 3, lineNumber);
                yield new Todo(description);
            }
            case DEADLINE -> {
                requireFieldCount(fields, 4, lineNumber);
                yield new Deadline(description, requireValue(fields.get(3), lineNumber));
            }
            case EVENT -> {
                requireFieldCount(fields, 5, lineNumber);
                yield new Event(
                        description,
                        requireValue(fields.get(3), lineNumber),
                        requireValue(fields.get(4), lineNumber)
                );
            }
            default -> throw invalidLine(lineNumber);
        };

        if (status == TaskStatus.COMPLETED) {
            task.markAsDone();
        }
        return task;
    }

    /** Parses a persisted task status. */
    private static TaskStatus parseStatus(String value, int lineNumber) throws IOException {
        try {
            return TaskStatus.fromStorageValue(Integer.parseInt(value));
        } catch (IllegalArgumentException e) {
            throw invalidLine(lineNumber, e);
        }
    }

    /** Splits a saved line while honoring escaped pipes and backslashes. */
    private static ArrayList<String> splitFields(String line, int lineNumber) throws IOException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (escaped) {
                if (current == '\\' || current == '|') {
                    field.append(current);
                } else {
                    field.append('\\').append(current);
                }
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '|') {
                fields.add(field.toString().strip());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }

        if (escaped) {
            throw invalidLine(lineNumber);
        }
        fields.add(field.toString().strip());
        return fields;
    }

    /** Ensures that a saved field contains meaningful text. */
    private static String requireValue(String value, int lineNumber) throws IOException {
        if (value.isBlank()) {
            throw invalidLine(lineNumber);
        }
        return value;
    }

    /** Checks that a saved line has the number of fields required by its task type. */
    private static void requireFieldCount(
            ArrayList<String> fields, int expectedCount, int lineNumber) throws IOException {
        if (fields.size() != expectedCount) {
            throw invalidLine(lineNumber);
        }
    }

    /** Creates a consistent error for malformed storage data. */
    private static IOException invalidLine(int lineNumber) {
        return new IOException("Invalid task data on line " + lineNumber + ".");
    }

    /** Creates a consistent error for malformed storage data with a cause. */
    private static IOException invalidLine(int lineNumber, Exception cause) {
        return new IOException("Invalid task data on line " + lineNumber + ".", cause);
    }

    /** Describes one saved line that was skipped during recovery. */
    public record LoadIssue(int lineNumber, String reason) {
        /** Validates one immutable recovery issue. */
        public LoadIssue {
            if (lineNumber < 1) {
                throw new IllegalArgumentException("A storage line number must be positive.");
            }
            reason = Objects.requireNonNull(reason, "reason");
        }
    }

    /** Contains recovered tasks and an immutable list of skipped-line issues. */
    public record LoadResult(TaskList tasks, List<LoadIssue> issues) {
        /** Makes the issues list immutable while retaining the recovered task list. */
        public LoadResult {
            tasks = Objects.requireNonNull(tasks, "tasks");
            issues = List.copyOf(Objects.requireNonNull(issues, "issues"));
        }
    }

    /** Moves a temporary file into its final storage location. */
    @FunctionalInterface
    interface FileMover {
        void move(Path source, Path target, CopyOption... options) throws IOException;
    }

    /** Writes original bytes to a new recovery-backup path. */
    @FunctionalInterface
    interface BackupWriter {
        void write(Path target, byte[] bytes) throws IOException;
    }
}
