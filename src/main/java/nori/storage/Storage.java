package nori.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import nori.NoriException;
import nori.task.Deadline;
import nori.task.Event;
import nori.task.Task;
import nori.task.Todo;

/**
 * Stores Nori's tasks on the local disk.
 */
public class Storage {
    /** System property that redirects storage into an isolated directory for tests. */
    private static final String STORAGE_DIRECTORY_PROPERTY = "nori.storage.dir";
    /** Reported whenever a stored line cannot be turned back into a task. */
    private static final String UNREADABLE_STORAGE_ERROR =
            "SAD NOOT! I couldn't read your saved tasks off the ice.";
    /**
     * Tells a user how to get past a storage file Nori can neither read nor recover.
     *
     * Nori refuses to save over such a file so that the tasks in it are not lost,
     * which leaves every change failing until the user deals with the file.
     * Moving the file aside keeps those tasks for a later repair.
     */
    private static final String UNREADABLE_FILE_REMEDY =
            "Repair that file or move it out of the data folder, then open me again.";
    /** Marks a stored field as Base64-encoded, distinguishing it from the older plain-text format. */
    private static final String ENCODED_FIELD_PREFIX = "b64:";
    /** Separates the fields of one stored task line. */
    private static final String TASK_SEPARATOR = " | ";
    /** Type code that begins a stored to-do line. */
    private static final String TYPE_TODO = "T";
    /** Type code that begins a stored deadline line. */
    private static final String TYPE_DEADLINE = "D";
    /** Type code that begins a stored event line. */
    private static final String TYPE_EVENT = "E";
    /** Stored completion status of a finished task. */
    private static final String STATUS_DONE = "1";
    /** Stored completion status of an unfinished task. */
    private static final String STATUS_NOT_DONE = "0";
    /** Position of the type code in a stored line. */
    private static final int INDEX_TYPE = 0;
    /** Position of the completion status in a stored line. */
    private static final int INDEX_STATUS = 1;
    /** Position of the description in a stored line. */
    private static final int INDEX_DESCRIPTION = 2;
    /** Position of a deadline's due date in a stored line. */
    private static final int INDEX_DEADLINE_DATE = 3;
    /** Position of an event's start details in a stored line. */
    private static final int INDEX_EVENT_FROM = 3;
    /** Position of an event's end details in a stored line. */
    private static final int INDEX_EVENT_TO = 4;
    /** Number of fields in a stored to-do line, which is also the fewest any line has. */
    private static final int FIELD_COUNT_TODO = 3;
    /** Number of fields in a stored deadline line. */
    private static final int FIELD_COUNT_DEADLINE = 4;
    /** Number of fields in a stored event line. */
    private static final int FIELD_COUNT_EVENT = 5;
    /** Prefix of the temporary file a save is written to before replacing the storage file. */
    private static final String TEMP_FILE_PREFIX = "nori-";
    /** Suffix of that temporary save file. */
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    /** Project root, so storage does not depend on the launch directory. */
    private final Path projectRoot;
    /** Directory holding every storage file for this launch. */
    private final Path storageDirectory;
    /** Main storage file that holds the current task list. */
    private final Path filePath;
    /** Copy of the last storage file that was read successfully. */
    private final Path backupFilePath;
    /** Where an unreadable storage file is kept after a backup recovery. */
    private final Path corruptFilePath;
    /** Storage file location used before data moved to the project root. */
    private final Path legacyFilePath;
    /** Backup file location matching {@link #legacyFilePath}. */
    private final Path legacyBackupFilePath;
    /** False once storage is known to be unreadable, so a save cannot overwrite unrecovered tasks. */
    private boolean canWriteToStorage = true;
    /** Message about migration or recovery to show after startup, if any. */
    private String loadingNotice;

    /**
     * Creates storage using the configured or project-root data directory.
     */
    public Storage() {
        projectRoot = findProjectRoot();
        storageDirectory = findStorageDirectory();
        filePath = storageDirectory.resolve("nori.txt");
        backupFilePath = storageDirectory.resolve("nori.txt.bak");
        corruptFilePath = storageDirectory.resolve("nori.txt.corrupt");
        legacyFilePath = projectRoot.resolve("src").resolve("main").resolve("java").resolve("data")
                .resolve("nori.txt");
        legacyBackupFilePath = projectRoot.resolve("src").resolve("main").resolve("java").resolve("data")
                .resolve("nori.txt.bak");
    }

    /**
     * Saves every task to the storage file, replacing its previous contents.
     *
     * @param tasks the tasks to save.
     * @throws NoriException if the tasks cannot be written to disk.
     */
    public void saveTasks(List<Task> tasks) throws NoriException {
        if (!canWriteToStorage) {
            throw new NoriException("SAD NOOT! I won't save over data/nori.txt while I can't read"
                    + " the tasks in it. " + UNREADABLE_FILE_REMEDY);
        }
        if (tasks == null) {
            throw new NoriException("SAD NOOT! I couldn't save a task list that isn't there.");
        }

        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(formatTask(task));
        }
        assert taskLines.size() == tasks.size()
                : "One line per task is what makes a reload restore the same number of tasks.";

        Path temporaryFile = null;
        try {
            Path storageDirectory = filePath.getParent();
            Files.createDirectories(storageDirectory);
            temporaryFile = Files.createTempFile(storageDirectory, TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            replaceStorageFile(temporaryFile);
            updateBackupFile();
        } catch (IOException | SecurityException exception) {
            throw new NoriException("SAD NOOT! I couldn't write your tasks onto the ice.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Loads every stored task, or returns an empty list when no storage file exists.
     *
     * @return the tasks restored from disk.
     * @throws NoriException if the storage file cannot be read or contains an invalid task.
     */
    public List<Task> loadTasks() throws NoriException {
        loadingNotice = null;
        migrateLegacyStorageIfNeeded();
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            return readTasks(filePath);
        } catch (IOException | SecurityException exception) {
            return recoverFromBackup();
        } catch (NoriException exception) {
            return recoverFromBackup();
        }
    }

    /**
     * Returns a startup notice generated while loading storage, if any.
     *
     * @return the startup notice, or {@code null} when loading completed normally.
     */
    public String getLoadingNotice() {
        return loadingNotice;
    }

    /**
     * Locates the project root so storage remains independent of the directory used to launch Nori.
     *
     * @return the project root, or the current directory when no project source marker is found.
     */
    private static Path findProjectRoot() {
        Path currentDirectory = Path.of("").toAbsolutePath().normalize();
        for (Path candidate = currentDirectory; candidate != null; candidate = candidate.getParent()) {
            try {
                Path sourceFile = candidate.resolve("src").resolve("main").resolve("java")
                        .resolve("nori").resolve("Nori.java");
                if (Files.isRegularFile(sourceFile)) {
                    return candidate;
                }
            } catch (SecurityException exception) {
                break;
            }
        }
        return currentDirectory;
    }

    /**
     * Returns the configured storage directory or the normal project-root data directory.
     *
     * The {@code nori.storage.dir} property exists for isolated automated test launches.
     * Normal application launches do not set it and therefore retain project-root storage.
     *
     * @return the directory that contains Nori's storage files.
     */
    private Path findStorageDirectory() {
        String configuredDirectory = System.getProperty(STORAGE_DIRECTORY_PROPERTY);
        if (configuredDirectory != null && !configuredDirectory.trim().isEmpty()) {
            return Path.of(configuredDirectory).toAbsolutePath().normalize();
        }
        return projectRoot.resolve("data");
    }

    /**
     * Copies legacy data created from {@code src/main/java} to the canonical project-root location.
     *
     * @throws NoriException if existing legacy data cannot be copied safely.
     */
    private void migrateLegacyStorageIfNeeded() throws NoriException {
        if (hasConfiguredStorageDirectory() || Files.exists(filePath) || Files.notExists(legacyFilePath)) {
            return;
        }

        try {
            Files.createDirectories(filePath.getParent());
            Files.copy(legacyFilePath, filePath);
            if (Files.isRegularFile(legacyBackupFilePath)) {
                Files.copy(legacyBackupFilePath, backupFilePath);
            }
            loadingNotice = "Noot! I've imported saved tasks from src/main/java/data to data/nori.txt.";
        } catch (IOException | SecurityException exception) {
            throw new NoriException("SAD NOOT! I couldn't carry your saved tasks over"
                    + " from src/main/java/data.");
        }
    }

    /**
     * Returns whether this launch has explicitly selected an isolated storage directory.
     *
     * @return {@code true} when the test-storage property is set to nonblank text.
     */
    private static boolean hasConfiguredStorageDirectory() {
        String configuredDirectory = System.getProperty(STORAGE_DIRECTORY_PROPERTY);
        return configuredDirectory != null && !configuredDirectory.trim().isEmpty();
    }

    /**
     * Formats one task as a line in the storage file.
     *
     * @param task the task to format.
     * @return the formatted task line.
     * @throws NoriException if the task type is unsupported.
     */
    private static String formatTask(Task task) throws NoriException {
        if (task == null) {
            throw new NoriException("SAD NOOT! I couldn't save a task that isn't there.");
        }

        String status = task.isDone() ? STATUS_DONE : STATUS_NOT_DONE;
        if (task instanceof Todo) {
            return TYPE_TODO + TASK_SEPARATOR + status + TASK_SEPARATOR
                    + encodeField(task.getDescription());
        }
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return TYPE_DEADLINE + TASK_SEPARATOR + status + TASK_SEPARATOR
                    + encodeField(task.getDescription())
                    + TASK_SEPARATOR + encodeField(deadline.getStorageDate());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return TYPE_EVENT + TASK_SEPARATOR + status + TASK_SEPARATOR
                    + encodeField(task.getDescription())
                    + TASK_SEPARATOR + encodeField(event.getFrom())
                    + TASK_SEPARATOR + encodeField(event.getTo());
        }
        throw new NoriException("SAD NOOT! I don't know how to save that kind of task.");
    }

    /**
     * Rebuilds one task from a line in the storage file.
     *
     * @param taskLine the stored task line.
     * @return the restored task.
     * @throws NoriException if the stored task line has an invalid format.
     */
    private static Task parseTask(String taskLine) throws NoriException {
        String[] taskFields = taskLine.split(Pattern.quote(TASK_SEPARATOR), -1);
        if (taskFields.length < FIELD_COUNT_TODO || !isValidStatus(taskFields[INDEX_STATUS])) {
            throw new NoriException(UNREADABLE_STORAGE_ERROR);
        }

        Task task = createTask(taskFields);
        if (taskFields[INDEX_STATUS].equals(STATUS_DONE)) {
            task.markAsDone();
        }
        if (!hasTaskDetails(task)) {
            throw new NoriException(UNREADABLE_STORAGE_ERROR);
        }
        return task;
    }

    /**
     * Creates the task that the fields of one stored line describe.
     *
     * @param taskFields the fields of a stored task line.
     * @return the restored task.
     * @throws NoriException if no task type matches the fields.
     */
    private static Task createTask(String[] taskFields) throws NoriException {
        String taskType = taskFields[INDEX_TYPE];
        if (taskType.equals(TYPE_TODO) && taskFields.length == FIELD_COUNT_TODO) {
            return new Todo(decodeField(taskFields[INDEX_DESCRIPTION]));
        }
        if (taskType.equals(TYPE_DEADLINE) && taskFields.length == FIELD_COUNT_DEADLINE) {
            return Deadline.fromStorage(decodeField(taskFields[INDEX_DESCRIPTION]),
                    decodeField(taskFields[INDEX_DEADLINE_DATE]));
        }
        if (taskType.equals(TYPE_EVENT) && taskFields.length == FIELD_COUNT_EVENT) {
            return new Event(decodeField(taskFields[INDEX_DESCRIPTION]),
                    decodeField(taskFields[INDEX_EVENT_FROM]),
                    decodeField(taskFields[INDEX_EVENT_TO]));
        }
        throw new NoriException(UNREADABLE_STORAGE_ERROR);
    }

    /**
     * Returns whether text represents a stored task completion status.
     *
     * @param status the stored completion status.
     * @return {@code true} when the status is 0 or 1.
     */
    private static boolean isValidStatus(String status) {
        return status.equals(STATUS_NOT_DONE) || status.equals(STATUS_DONE);
    }

    /**
     * Reads and parses every task from a storage file.
     *
     * @param filePath the file to read.
     * @return the parsed tasks.
     * @throws IOException if the file cannot be read.
     * @throws NoriException if a stored task is invalid.
     */
    private static List<Task> readTasks(Path filePath) throws IOException, NoriException {
        List<Task> tasks = new ArrayList<>();
        for (String taskLine : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            tasks.add(parseTask(taskLine));
        }
        return tasks;
    }

    /**
     * Restores tasks from the backup file after the main storage file cannot be read.
     *
     * @return the restored tasks.
     * @throws NoriException if no valid backup can be used.
     */
    private List<Task> recoverFromBackup() throws NoriException {
        if (Files.notExists(backupFilePath)) {
            canWriteToStorage = false;
            throw new NoriException("SAD NOOT! Your saved tasks in data/nori.txt are corrupted"
                    + " and I have no backup to waddle back to. " + UNREADABLE_FILE_REMEDY);
        }

        try {
            List<Task> backupTasks = readTasks(backupFilePath);
            Files.copy(filePath, corruptFilePath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(backupFilePath, filePath, StandardCopyOption.REPLACE_EXISTING);
            loadingNotice = "Noot! Your saved tasks were corrupted. I've restored the backup"
                    + " and kept the damaged file as data/nori.txt.corrupt.";
            return backupTasks;
        } catch (IOException | SecurityException | NoriException exception) {
            canWriteToStorage = false;
            throw new NoriException("SAD NOOT! Your saved tasks in data/nori.txt are corrupted"
                    + " and the backup wouldn't come back either. " + UNREADABLE_FILE_REMEDY);
        }
    }

    /**
     * Returns whether a restored task contains every detail required by its type.
     *
     * @param task the restored task.
     * @return {@code true} if every required task field is nonempty.
     */
    private static boolean hasTaskDetails(Task task) {
        if (task.getDescription().isEmpty()) {
            return false;
        }
        if (task instanceof Deadline) {
            return true;
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return !event.getFrom().isEmpty() && !event.getTo().isEmpty();
        }
        return true;
    }

    /**
     * Encodes one task field so delimiters in user-entered text do not corrupt storage.
     *
     * @param field the task field to encode.
     * @return the encoded task field.
     * @throws NoriException if the field is missing.
     */
    private static String encodeField(String field) throws NoriException {
        if (field == null) {
            throw new NoriException("SAD NOOT! I couldn't save a task with missing details.");
        }
        return ENCODED_FIELD_PREFIX + Base64.getEncoder().encodeToString(field.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a task field, retaining compatibility with the original plain-text format.
     *
     * @param storedField the stored task field.
     * @return the decoded task field.
     * @throws NoriException if the encoded field is invalid.
     */
    private static String decodeField(String storedField) throws NoriException {
        if (!storedField.startsWith(ENCODED_FIELD_PREFIX)) {
            return storedField;
        }

        try {
            byte[] encodedBytes = Base64.getDecoder().decode(storedField.substring(ENCODED_FIELD_PREFIX.length()));
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(encodedBytes))
                    .toString();
        } catch (IllegalArgumentException | CharacterCodingException exception) {
            throw new NoriException(UNREADABLE_STORAGE_ERROR);
        }
    }

    /**
     * Replaces the storage file with a completed temporary file.
     *
     * @param temporaryFile the complete replacement file.
     * @throws IOException if the replacement cannot be completed.
     */
    private void replaceStorageFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Copies the latest valid storage file as the recovery backup.
     */
    private void updateBackupFile() {
        try {
            Files.copy(filePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException exception) {
            // A successful main save is still usable when creating its optional recovery backup fails.
        }
    }

    /**
     * Removes a leftover temporary file without masking the primary storage error.
     *
     * @param temporaryFile the temporary file to remove, if one was created.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException exception) {
            // The completed save or its original error is more useful than temporary-file cleanup details.
        }
    }
}
