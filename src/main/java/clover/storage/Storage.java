package clover.storage;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import clover.task.Deadline;
import clover.task.Event;
import clover.task.Task;
import clover.task.ToDo;
import clover.tutoree.Tutoree;

/**
 * Saves Clover tasks to, and loads them from, a file on the hard disk.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "clover.txt");
    private static final Path TUTOREE_FILE_PATH = Path.of("data", "tutorees.txt");
    private static final String TODO_TASK_TYPE = "T";
    private static final String DEADLINE_TASK_TYPE = "D";
    private static final String EVENT_TASK_TYPE = "E";
    private static final String TUTOREE_TYPE = "S";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";
    private static final Pattern FEE_PATTERN = Pattern.compile("\\d+(?:\\.\\d{1,2})?"
            + "(?:/(?:hour|session|lesson|month))?");
    private static final int TASK_TYPE_FIELD = 0;
    private static final int TASK_STATUS_FIELD = 1;
    private static final int TASK_DESCRIPTION_FIELD = 2;
    private static final int DEADLINE_DUE_DATE_FIELD = 3;
    private static final int EVENT_START_DATE_FIELD = 3;
    private static final int EVENT_END_DATE_FIELD = 4;
    private static final int TUTOREE_NAME_FIELD = 1;
    private static final int TUTOREE_ADDRESS_FIELD = 2;
    private static final int TUTOREE_FEE_FIELD = 3;
    private final Path taskFilePath;
    private final Path tutoreeFilePath;
    private FileChannel lockChannel;
    private FileLock applicationLock;
    private boolean writingAllowed = true;

    /**
     * Creates storage that uses Clover's default data-file location.
     */
    public Storage() {
        this(FILE_PATH, TUTOREE_FILE_PATH);
    }

    /**
     * Creates storage that uses the supplied data-file location.
     */
    public Storage(Path filePath) {
        this(filePath, filePath.resolveSibling("tutorees.txt"));
    }

    /**
     * Creates storage that uses the supplied task and tutoree data-file locations.
     */
    public Storage(Path taskFilePath, Path tutoreeFilePath) {
        this.taskFilePath = taskFilePath;
        this.tutoreeFilePath = tutoreeFilePath;
    }

    /** Acquires an exclusive application lock, returning false when another Clover instance owns it. */
    public boolean acquireApplicationLock() throws IOException {
        try {
            Path lockPath = taskFilePath.resolveSibling(taskFilePath.getFileName() + ".lock");
            Files.createDirectories(lockPath.getParent());
            lockChannel = FileChannel.open(lockPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            try {
                applicationLock = lockChannel.tryLock();
            } catch (OverlappingFileLockException exception) {
                applicationLock = null;
            }
            writingAllowed = applicationLock != null;
            if (!writingAllowed) {
                lockChannel.close();
                lockChannel = null;
            }
            return writingAllowed;
        } catch (IOException | SecurityException exception) {
            writingAllowed = false;
            throw exception;
        }
    }

    /** Releases the application lock when Clover closes. */
    public void releaseApplicationLock() {
        try {
            if (applicationLock != null) {
                applicationLock.release();
            }
            if (lockChannel != null) {
                lockChannel.close();
            }
        } catch (IOException ignored) {
            // Closing should not prevent the application from exiting.
        }
    }

    /** Prevents writes when Clover cannot safely lock its data files. */
    public void disableWriting() {
        writingAllowed = false;
    }

    /** Copies the task data file to a recoverable backup after a failed load. */
    public void backupTaskData() throws IOException {
        backup(taskFilePath);
    }

    /** Copies the tutoree data file to a recoverable backup after a failed load. */
    public void backupTutoreeData() throws IOException {
        backup(tutoreeFilePath);
    }

    /**
     * Writes the current task list to the data file.
     */
    public void save(List<Task> tasks) throws IOException {
        assert tasks != null : "Storage saves a task collection supplied by TaskList.";
        ensureWritingAllowed();
        Files.createDirectories(taskFilePath.getParent());
        if (Files.isDirectory(taskFilePath)) {
            throw new IOException("The task data path is a directory.");
        }

        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            assert task != null : "TaskList must not pass null tasks to storage.";
            taskLines.add(toFileLine(task));
        }

        Path temporaryFile = Files.createTempFile(taskFilePath.getParent(), "clover-", ".tmp");
        try {
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            moveIntoPlace(temporaryFile, taskFilePath);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads saved tasks, or returns an empty list when Clover is run for the first time.
     */
    public List<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(taskFilePath)) {
            return tasks;
        }
        if (!Files.isRegularFile(taskFilePath)) {
            throw new IOException("The task data path is not a regular file.");
        }

        List<String> lines = Files.readAllLines(taskFilePath, StandardCharsets.UTF_8);
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            if (!line.isBlank()) {
                Task task = fromFileLine(line, lineNumber + 1);
                if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                    throw invalidData(lineNumber + 1, "duplicate task");
                }
                tasks.add(task);
            }
        }
        return tasks;
    }

    /** Writes the current tutoree list to the tutoree data file. */
    public void saveTutorees(List<Tutoree> tutorees) throws IOException {
        assert tutorees != null : "Storage saves a tutoree collection supplied by TutoreeList.";
        ensureWritingAllowed();
        Files.createDirectories(tutoreeFilePath.getParent());
        if (Files.isDirectory(tutoreeFilePath)) {
            throw new IOException("The tutoree data path is a directory.");
        }

        List<String> tutoreeLines = new ArrayList<>();
        for (Tutoree tutoree : tutorees) {
            assert tutoree != null : "TutoreeList must not pass null tutorees to storage.";
            tutoreeLines.add(toTutoreeFileLine(tutoree));
        }

        Path temporaryFile = Files.createTempFile(tutoreeFilePath.getParent(), "tutorees-", ".tmp");
        try {
            Files.write(temporaryFile, tutoreeLines, StandardCharsets.UTF_8);
            moveIntoPlace(temporaryFile, tutoreeFilePath);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Loads saved tutorees, or returns an empty list when no tutoree data file exists. */
    public List<Tutoree> loadTutorees() throws IOException {
        ArrayList<Tutoree> tutorees = new ArrayList<>();
        if (Files.notExists(tutoreeFilePath)) {
            return tutorees;
        }
        if (!Files.isRegularFile(tutoreeFilePath)) {
            throw new IOException("The tutoree data path is not a regular file.");
        }

        List<String> lines = Files.readAllLines(tutoreeFilePath, StandardCharsets.UTF_8);
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            if (!line.isBlank()) {
                Tutoree tutoree = tutoreeFromFileLine(line, lineNumber + 1);
                if (tutorees.stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(tutoree.getName()))) {
                    throw invalidTutoreeData(lineNumber + 1, "duplicate tutoree name");
                }
                tutorees.add(tutoree);
            }
        }
        return tutorees;
    }

    /** Converts one task to a stable, pipe-separated file line. */
    private String toFileLine(Task task) throws IOException {
        String status = task.isDone() ? COMPLETE_STATUS : INCOMPLETE_STATUS;
        String tutoreeSuffix = task.getTutoreeName() == null ? "" : " | " + escape(task.getTutoreeName());
        if (task instanceof Deadline deadline) {
            return DEADLINE_TASK_TYPE + " | " + status + " | " + escape(deadline.getDescription())
                    + " | " + escape(deadline.getEndBy().toString()) + tutoreeSuffix;
        }
        if (task instanceof Event event) {
            return EVENT_TASK_TYPE + " | " + status + " | " + escape(event.getDescription())
                    + " | " + escape(event.getStart().toString())
                    + " | " + escape(event.getEnd().toString()) + tutoreeSuffix;
        }
        if (task instanceof ToDo) {
            return TODO_TASK_TYPE + " | " + status + " | " + escape(task.getDescription()) + tutoreeSuffix;
        }
        throw new IOException("Unsupported task type.");
    }

    /** Converts one tutoree to a stable, pipe-separated file line. */
    private String toTutoreeFileLine(Tutoree tutoree) {
        return TUTOREE_TYPE + " | " + escape(tutoree.getName()) + " | " + escape(tutoree.getAddress())
                + " | " + escape(tutoree.getFee());
    }

    /** Parses an ISO date stored in the data file. */
    private LocalDate parseDate(String text, int lineNumber) throws IOException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException exception) {
            throw invalidData(lineNumber, "invalid date");
        }
    }

    /** Recreates one task from a pipe-separated file line. */
    private Task fromFileLine(String line, int lineNumber) throws IOException {
        List<String> parts = splitFields(line, lineNumber);
        assert !parts.isEmpty() : "Splitting a task-data line always produces its first field.";

        Task task = switch (parts.get(TASK_TYPE_FIELD)) {
            case TODO_TASK_TYPE -> {
                requirePartCount(parts, 3, 4, lineNumber);
                yield new ToDo(parts.get(TASK_DESCRIPTION_FIELD), getOptionalTutoreeName(parts, 3, lineNumber));
            }
            case DEADLINE_TASK_TYPE -> {
                requirePartCount(parts, 4, 5, lineNumber);
                yield new Deadline(parts.get(TASK_DESCRIPTION_FIELD),
                        parseDate(parts.get(DEADLINE_DUE_DATE_FIELD), lineNumber),
                        getOptionalTutoreeName(parts, 4, lineNumber));
            }
            case EVENT_TASK_TYPE -> {
                requirePartCount(parts, 5, 6, lineNumber);
                yield new Event(parts.get(TASK_DESCRIPTION_FIELD),
                        parseDate(parts.get(EVENT_START_DATE_FIELD), lineNumber),
                        parseDate(parts.get(EVENT_END_DATE_FIELD), lineNumber),
                        getOptionalTutoreeName(parts, 5, lineNumber));
            }
            default -> throw invalidData(lineNumber, "unknown task type");
        };

        if (COMPLETE_STATUS.equals(parts.get(TASK_STATUS_FIELD))) {
            task.markAsDone();
        } else if (!INCOMPLETE_STATUS.equals(parts.get(TASK_STATUS_FIELD))) {
            throw invalidData(lineNumber, "invalid task status");
        }
        if (task.getDescription().isBlank()) {
            throw invalidData(lineNumber, "blank task description");
        }
        if (task instanceof Event event && !event.getEnd().isAfter(event.getStart())) {
            throw invalidData(lineNumber, "event end date is not after start date");
        }
        return task;
    }

    /** Recreates one tutoree from a pipe-separated file line. */
    private Tutoree tutoreeFromFileLine(String line, int lineNumber) throws IOException {
        List<String> parts = splitFields(line, lineNumber);
        if (parts.size() != 4) {
            throw invalidTutoreeData(lineNumber, "wrong number of fields");
        }
        if (!TUTOREE_TYPE.equals(parts.get(0))) {
            throw invalidTutoreeData(lineNumber, "unknown record type");
        }
        if (parts.get(TUTOREE_NAME_FIELD).isBlank() || parts.get(TUTOREE_ADDRESS_FIELD).isBlank()
                || parts.get(TUTOREE_FEE_FIELD).isBlank()) {
            throw invalidTutoreeData(lineNumber, "blank required field");
        }
        if (!containsLetter(parts.get(TUTOREE_NAME_FIELD))) {
            throw invalidTutoreeData(lineNumber, "tutoree name contains no letters");
        }
        if (!isPositiveFee(parts.get(TUTOREE_FEE_FIELD))) {
            throw invalidTutoreeData(lineNumber, "fee is not a positive number");
        }
        return new Tutoree(parts.get(TUTOREE_NAME_FIELD), parts.get(TUTOREE_ADDRESS_FIELD),
                parts.get(TUTOREE_FEE_FIELD));
    }

    /** Returns an optional tutoree field from a task record. */
    private String getOptionalTutoreeName(List<String> parts, int tutoreeField, int lineNumber) throws IOException {
        if (parts.size() == tutoreeField) {
            return null;
        }
        String tutoreeName = parts.get(tutoreeField);
        if (tutoreeName.isBlank()) {
            throw invalidData(lineNumber, "blank tutoree name");
        }
        return tutoreeName;
    }

    /** Returns whether text is a positive decimal fee amount. */
    private boolean isPositiveFee(String fee) {
        try {
            return FEE_PATTERN.matcher(fee).matches() && new BigDecimal(fee.split("/", 2)[0]).signum() > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /** Returns whether text contains at least one Unicode letter. */
    private boolean containsLetter(String text) {
        return text.codePoints().anyMatch(Character::isLetter);
    }

    /** Splits a line at unescaped pipe characters and removes delimiter spacing. */
    private List<String> splitFields(String line, int lineNumber) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;
        for (char character : line.toCharArray()) {
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw invalidData(lineNumber, "invalid escape sequence");
                }
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            throw invalidData(lineNumber, "unfinished escape sequence");
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /** Escapes characters that have a special meaning in the file format. */
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\|");
    }

    /** Refuses writes when another running Clover instance owns the data lock. */
    private void ensureWritingAllowed() throws IOException {
        if (!writingAllowed) {
            throw new IOException("Another Clover instance is already using the data files.");
        }
    }

    /** Copies one existing data file to its adjacent {@code .bak} recovery file. */
    private void backup(Path source) throws IOException {
        if (Files.isRegularFile(source)) {
            Files.copy(source, source.resolveSibling(source.getFileName() + ".bak"),
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Replaces the old data file only after the temporary file is fully written. */
    private void moveIntoPlace(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Verifies that a task line includes all fields required by its task type. */
    private void requirePartCount(List<String> parts, int minimumCount, int maximumCount, int lineNumber)
            throws IOException {
        if (parts.size() < minimumCount || parts.size() > maximumCount) {
            throw invalidData(lineNumber, "wrong number of fields");
        }
    }

    /** Creates a clear error for a corrupt line in the saved task data. */
    private IOException invalidData(int lineNumber, String reason) {
        return new IOException("Invalid task data on line " + lineNumber + ": " + reason + ".");
    }

    /** Creates a clear error for a corrupt tutoree-data line. */
    private IOException invalidTutoreeData(int lineNumber, String reason) {
        return new IOException("Invalid tutoree data on line " + lineNumber + ": " + reason + ".");
    }
}
