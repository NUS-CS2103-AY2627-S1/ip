package lily.storage;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lily.exception.LilyException;
import lily.parser.DateTimeParser;
import lily.task.Deadline;
import lily.task.Event;
import lily.task.Task;
import lily.task.ToDo;

/**
 * Saves and loads the task list from a data file on disk.
 *
 * <p>
 * Each instance is bound to one file path, supplied to the constructor (e.g.
 * {@code new Storage("data/lily.txt")}), so a caller could in principle point
 * Lily at a
 * different save file without changing this class.
 *
 * <p>
 * Loading is designed to never crash the application. Invalid UTF-8 files are
 * backed up, access failures leave the original untouched, and individual
 * malformed records are skipped rather than aborting the whole load. Saving is
 * done atomically so a crash or power loss mid-write cannot leave behind a
 * half-written data file.
 */
public class Storage {
    /** Literal delimiter used between fields in a saved record. */
    private static final String DELIMITER = " | ";

    private final Path dataFile;
    private final Path dataDir;
    private final Clock clock;

    /**
     * Creates a Storage bound to the given file path.
     *
     * @param filePath path (relative or absolute) to the save file, e.g.
     *                 {@code "data/lily.txt"}
     */
    public Storage(String filePath) {
        this(filePath, Clock.systemDefaultZone());
    }

    /** Creates storage with a supplied clock so backup naming can be tested reliably. */
    Storage(String filePath, Clock clock) {
        this.dataFile = Path.of(filePath);
        Path parent = dataFile.getParent();
        this.dataDir = (parent != null) ? parent : Path.of(".");
        this.clock = clock;
    }

    /**
     * Escapes a field so it can safely be embedded in a
     * {@value #DELIMITER}-separated
     * record even if it contains a backslash or pipe character.
     *
     * <p>
     * This stays a static utility (rather than an instance method) because escaping
     * is
     * a pure text transformation that has nothing to do with any particular file;
     * {@link
     * Task} subclasses call it directly while building their own save records.
     */
    public static String escapeField(String field) {
        if (field == null) {
            return "";
        }
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Replaces the data file with one parseable record for every task.
     *
     * <p>
     * The write is atomic: tasks are first written to a temporary file in the same
     * directory, then moved into place, so a crash partway through a write cannot
     * corrupt or truncate the existing save file.
     *
     * @param tasks the tasks to save; must not be {@code null}, but may be empty
     * @throws IOException if the data directory or file cannot be created or
     *                     written
     */
    public void save(List<Task> tasks) throws IOException {
        if (tasks == null) {
            throw new IOException("Cannot save a null task list.");
        }

        try {
            createDataDirectory();
        } catch (FileAlreadyExistsException e) {
            throw new IOException("Cannot save tasks: '" + dataDir
                    + "' exists but is not a directory. Please remove or rename it.", e);
        } catch (AccessDeniedException e) {
            throw new IOException("Cannot create or access data directory '" + dataDir
                    + "': permission denied.", e);
        } catch (IOException e) {
            throw new IOException("Cannot create data directory '" + dataDir + "': "
                    + safeReason(e) + ".", e);
        }

        if (Files.isDirectory(dataFile)) {
            throw new IOException("Cannot save tasks: '" + dataFile
                    + "' is a directory, not a file.");
        }

        List<String> taskRecords = serializeTasks(tasks);
        writeTaskRecordsAtomically(taskRecords);
    }

    /** Converts each non-null task to the record stored in the data file. */
    private List<String> serializeTasks(List<Task> tasks) {
        List<String> taskRecords = new ArrayList<>();
        for (Task task : tasks) {
            if (task == null) {
                // Defensive: skip any null slot rather than let a NullPointerException
                // during toFileString() take down the whole save operation.
                continue;
            }
            taskRecords.add(task.toFileString());
        }
        return taskRecords;
    }

    /** Writes all records to a temporary file before replacing the data file. */
    private void writeTaskRecordsAtomically(List<String> taskRecords) throws IOException {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(dataDir, "lily", ".tmp");
            Files.write(tempFile, taskRecords, StandardCharsets.UTF_8);
            moveIntoPlace(tempFile);
        } catch (AccessDeniedException e) {
            cleanupQuietly(tempFile);
            throw new IOException("Permission denied while saving tasks to '" + dataFile + "'.", e);
        } catch (IOException e) {
            cleanupQuietly(tempFile);
            throw new IOException("Unable to save tasks to '" + dataFile + "': " + safeReason(e), e);
        }
    }

    private void createDataDirectory() throws IOException {
        if (Files.exists(dataDir) && !Files.isDirectory(dataDir)) {
            throw new FileAlreadyExistsException(dataDir.toString());
        }
        Files.createDirectories(dataDir);
    }

    private void moveIntoPlace(Path tempFile) throws IOException {
        try {
            Files.move(tempFile, dataFile,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException e) {
            // Some filesystems (e.g. certain network drives) don't support atomic moves.
            // Fall back to a plain (non-atomic) replace rather than failing the save.
            Files.move(tempFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void cleanupQuietly(Path tempFile) {
        if (tempFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException ignored) {
            // Best-effort cleanup only; nothing more we can do here.
        }
    }

    /**
     * Loads saved tasks, or returns an empty list when Lily has not saved any tasks
     * yet.
     *
     * <p>
     * This method is deliberately tolerant of a damaged save file so a corrupted or
     * partially-written file never prevents the chatbot from starting up:
     * <ul>
     * <li>a missing file, or a missing data directory, simply yields an empty
     * list;</li>
     * <li>individual malformed lines are skipped (with a returned warning) so the
     * rest of a mostly-valid file still loads;</li>
     * <li>invalid UTF-8 is renamed aside as a timestamped backup;</li>
     * <li>permission and other I/O failures preserve the original file.</li>
     * </ul>
     *
     * @return the tasks reconstructed from the data file (possibly empty)
     */
    public List<Task> load() {
        return loadWithReport().tasks();
    }

    /**
     * Loads tasks together with warnings suitable for display in either user
     * interface.
     */
    public LoadResult loadWithReport() {
        List<String> warnings = new ArrayList<>();
        ReadResult readResult = readTaskRecords(warnings);
        ParseResult parseResult = parseTaskRecords(readResult.taskRecords(), warnings);
        LoadStatus status = parseResult.skippedCount() > 0
                ? LoadStatus.MALFORMED_RECORDS_SKIPPED : readResult.status();
        return new LoadResult(parseResult.tasks(), warnings, status);
    }

    /**
     * Reads saved records while classifying missing paths and read failures.
     */
    private ReadResult readTaskRecords(List<String> warnings) {
        if (Files.notExists(dataDir)) {
            return new ReadResult(List.of(), LoadStatus.DATA_DIRECTORY_MISSING);
        }

        if (Files.notExists(dataFile)) {
            return new ReadResult(List.of(), LoadStatus.SAVE_FILE_MISSING);
        }

        if (Files.isDirectory(dataFile)) {
            warnings.add("The save path '" + dataFile
                    + "' is a directory, not a file. Lily started with an empty task list.");
            return new ReadResult(List.of(), LoadStatus.SAVE_PATH_NOT_FILE);
        }

        try {
            return new ReadResult(Files.readAllLines(dataFile, StandardCharsets.UTF_8), LoadStatus.LOADED);
        } catch (MalformedInputException e) {
            LoadStatus status = backupCorruptedFile("it is not valid UTF-8 text", warnings)
                    ? LoadStatus.INVALID_UTF8_BACKED_UP : LoadStatus.INVALID_UTF8_BACKUP_FAILED;
            return new ReadResult(List.of(), status);
        } catch (AccessDeniedException | SecurityException e) {
            warnings.add("Permission was denied while reading '" + dataFile
                    + "'. The original file was left untouched, and Lily started with an empty task list.");
            return new ReadResult(List.of(), LoadStatus.PERMISSION_DENIED);
        } catch (IOException e) {
            warnings.add("Could not read '" + dataFile + "' (" + safeReason(e)
                    + "). The original file was left untouched, and Lily started with an empty task list.");
            return new ReadResult(List.of(), LoadStatus.READ_FAILED);
        }
    }

    /** Parses valid records and reports individual records that cannot be recovered. */
    private ParseResult parseTaskRecords(List<String> taskRecords, List<String> warnings) {
        List<Task> tasks = new ArrayList<>();
        int lineNumber = 0;
        int skippedCount = 0;
        for (String taskRecord : taskRecords) {
            lineNumber++;
            if (taskRecord == null || taskRecord.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(taskRecord));
            } catch (LilyException e) {
                warnings.add("Skipped malformed entry on line " + lineNumber
                        + " of '" + dataFile + "': " + e.getMessage() + ".");
                skippedCount++;
            }
        }

        if (skippedCount > 0) {
            warnings.add(skippedCount + " malformed task record(s) were ignored; "
                    + "the remaining tasks loaded normally.");
        }
        return new ParseResult(tasks, skippedCount);
    }

    /**
     * Moves a file proven to contain invalid UTF-8 aside so a fresh save file can
     * take its place instead of the chatbot refusing to start.
     */
    private boolean backupCorruptedFile(String reason, List<String> warnings) {
        String timestamp = LocalDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        try {
            Path backup = moveToUnusedBackup(timestamp);
            warnings.add("Could not read '" + dataFile + "' because " + reason
                    + ". The corrupted file was backed up to '" + backup
                    + "', and Lily started with an empty task list.");
            return true;
        } catch (IOException moveFailed) {
            warnings.add("Could not read '" + dataFile + "' because " + reason
                    + ", and the corrupted file could not be backed up (" + safeReason(moveFailed)
                    + "). Lily started with an empty task list; the original file was left untouched.");
            return false;
        }
    }

    /** Moves a corrupt file without replacing a backup created in the same second. */
    private Path moveToUnusedBackup(String timestamp) throws IOException {
        int suffix = 0;
        while (true) {
            String suffixText = suffix == 0 ? "" : "-" + suffix;
            Path backup = dataDir.resolve(dataFile.getFileName()
                    + ".corrupted-" + timestamp + suffixText);
            try {
                return Files.move(dataFile, backup);
            } catch (FileAlreadyExistsException e) {
                suffix++;
            }
        }
    }

    /**
     * Reconstructs one task from a record created by {@link Task#toFileString()}.
     *
     * @throws LilyException if the record is missing fields, has an unknown task
     *                       type,
     *                       an invalid done-flag, or blank required fields
     */
    private static Task parseTask(String taskRecord) throws LilyException {
        String[] fields = parseEscapedFields(taskRecord);
        if (fields.length < 3) {
            throw new LilyException("expected at least 3 fields separated by \" | \", found "
                    + fields.length);
        }

        validateDoneFlag(fields[1]);
        Task task = createTask(fields);
        restoreDoneState(task, fields[1]);
        return task;
    }

    /** Parses fields while rejecting unknown, dangling, and unescaped delimiters. */
    private static String[] parseEscapedFields(String taskRecord) throws LilyException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        int index = 0;
        while (index < taskRecord.length()) {
            if (taskRecord.startsWith(DELIMITER, index)) {
                fields.add(field.toString().trim());
                field.setLength(0);
                index += DELIMITER.length();
                continue;
            }

            char character = taskRecord.charAt(index);
            if (character == '\\') {
                if (index + 1 >= taskRecord.length()) {
                    throw new LilyException("field ends with an incomplete escape");
                }
                char escapedCharacter = taskRecord.charAt(index + 1);
                if (escapedCharacter != '\\' && escapedCharacter != '|') {
                    throw new LilyException("unsupported escape '\\" + escapedCharacter + "'");
                }
                field.append(escapedCharacter);
                index += 2;
                continue;
            }
            if (character == '|') {
                throw new LilyException("unescaped '|' inside a field");
            }
            field.append(character);
            index++;
        }
        fields.add(field.toString().trim());
        return fields.toArray(String[]::new);
    }

    private static String safeReason(Exception exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? exception.getClass().getSimpleName()
                : exception.getMessage();
    }

    /** Describes how loading completed, including normal first-run cases. */
    public enum LoadStatus {
        LOADED,
        DATA_DIRECTORY_MISSING,
        SAVE_FILE_MISSING,
        SAVE_PATH_NOT_FILE,
        PERMISSION_DENIED,
        INVALID_UTF8_BACKED_UP,
        INVALID_UTF8_BACKUP_FAILED,
        READ_FAILED,
        MALFORMED_RECORDS_SKIPPED
    }

    /** Immutable result containing tasks, startup warnings, and the load outcome. */
    public record LoadResult(List<Task> tasks, List<String> warnings, LoadStatus status) {
        public LoadResult {
            tasks = List.copyOf(tasks);
            warnings = List.copyOf(warnings);
        }
    }

    private record ReadResult(List<String> taskRecords, LoadStatus status) {
    }

    private record ParseResult(List<Task> tasks, int skippedCount) {
    }

    /** Validates the common completion-state field used by every record type. */
    private static void validateDoneFlag(String doneFlag) throws LilyException {
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new LilyException("done-flag must be '0' or '1', found '" + doneFlag + "'");
        }
    }

    /** Creates the correct task subtype after its fields have been normalized. */
    private static Task createTask(String[] fields) throws LilyException {
        switch (fields[0]) {
            case "T":
                return createTodo(fields);
            case "D":
                return createDeadline(fields);
            case "E":
                return createEvent(fields);
            default:
                throw new LilyException("unknown task type '" + fields[0] + "' (expected T, D, or E)");
        }
    }

    private static Task createTodo(String[] fields) throws LilyException {
        requireFieldCount(fields, 3, "todo");
        requireNonBlank(fields, 2, "description");
        return new ToDo(fields[2]);
    }

    private static Task createDeadline(String[] fields) throws LilyException {
        requireFieldCount(fields, 4, 5, "deadline");
        requireNonBlank(fields, 2, "description");
        requireNonBlank(fields, 3, "'by' date");
        LocalDateTime by = DateTimeParser.parseStorageFormat(fields[3]);
        boolean hasExplicitTime = fields.length == 5
                ? parseExplicitTimeFlag(fields[4]) : !by.toLocalTime().equals(java.time.LocalTime.MIDNIGHT);
        return new Deadline(fields[2], by, hasExplicitTime);
    }

    private static Task createEvent(String[] fields) throws LilyException {
        requireFieldCount(fields, 5, 7, "event");
        requireNonBlank(fields, 2, "description");
        requireNonBlank(fields, 3, "'from' time");
        requireNonBlank(fields, 4, "'to' time");
        LocalDateTime from = DateTimeParser.parseStorageFormat(fields[3]);
        LocalDateTime to = DateTimeParser.parseStorageFormat(fields[4]);
        if (to.isBefore(from)) {
            throw new LilyException("event 'to' time cannot be before its 'from' time");
        }
        boolean hasExplicitFromTime = fields.length == 7
                ? parseExplicitTimeFlag(fields[5]) : !from.toLocalTime().equals(java.time.LocalTime.MIDNIGHT);
        boolean hasExplicitToTime = fields.length == 7
                ? parseExplicitTimeFlag(fields[6]) : !to.toLocalTime().equals(java.time.LocalTime.MIDNIGHT);
        return new Event(fields[2], from, hasExplicitFromTime, to, hasExplicitToTime);
    }

    /** Restores a task's persisted completion state after it has been constructed. */
    private static void restoreDoneState(Task task, String doneFlag) {
        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
    }

    private static boolean parseExplicitTimeFlag(String field) throws LilyException {
        if (!field.equals("true") && !field.equals("false")) {
            throw new LilyException("explicit-time flag must be 'true' or 'false'");
        }
        return Boolean.parseBoolean(field);
    }

    private static void requireFieldCount(String[] fields, int legacyExpected, int expected, String taskTypeName)
            throws LilyException {
        if (fields.length != legacyExpected && fields.length != expected) {
            throw new LilyException(taskTypeName + " record needs " + legacyExpected + " or " + expected
                    + " fields, found " + fields.length);
        }
    }

    /** Requires an exact record width for task types whose format has not changed. */
    private static void requireFieldCount(String[] fields, int expected, String taskTypeName)
            throws LilyException {
        if (fields.length != expected) {
            throw new LilyException(taskTypeName + " record needs exactly " + expected
                    + " fields, found " + fields.length);
        }
    }

    private static void requireNonBlank(String[] fields, int index, String fieldName)
            throws LilyException {
        if (fields[index].isBlank()) {
            throw new LilyException(fieldName + " cannot be blank");
        }
    }
}
