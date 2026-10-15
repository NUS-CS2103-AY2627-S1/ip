package sevensix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Loads tasks and replaces their data file atomically after a successful write.
 *
 * <p>An unreadable or damaged file blocks saving for this session, protecting records
 * that could not be loaded. Record encoding and validation belong to {@link TaskCodec}.
 */
public class TaskStorage {
    /** Data file, or null when its configured path is invalid. */
    private final Path dataFile;
    /** Converts individual records without handling filesystem access. */
    private final TaskCodec codec = new TaskCodec();
    /** User-visible reason saving is disabled, or an empty string for healthy storage. */
    private String loadWarning = "";

    /** Creates storage for a resolved path.
     *
     * @param dataFile the relative or absolute data-file path.
     */
    public TaskStorage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /** Creates storage without allowing an invalid configured path to crash startup.
     *
     * @param configuredPath the data-file path supplied by configuration.
     */
    public TaskStorage(String configuredPath) {
        dataFile = parsePath(configuredPath);
    }

    /** Resolves a configured path, recording a warning when it is invalid.
     *
     * @param configuredPath the path supplied by configuration.
     * @return the resolved path, or null if it cannot be used.
     */
    private Path parsePath(String configuredPath) {
        try {
            return Path.of(configuredPath);
        } catch (InvalidPathException | SecurityException exception) {
            loadWarning = Ui.LOAD_FAILURE;
            return null;
        }
    }

    /** Loads valid records and marks unreadable or corrupt storage as read-only.
     *
     * @return all valid tasks, or an empty list for a missing or unreadable file.
     */
    public List<Task> load() {
        if (dataFile == null) {
            loadWarning = Ui.LOAD_FAILURE;
            return new ArrayList<>();
        }
        try {
            createParentDirectory();
            if (Files.notExists(dataFile, LinkOption.NOFOLLOW_LINKS)) {
                return new ArrayList<>();
            }
            rejectSymbolicLink();
            return readTasks();
        } catch (IOException | SecurityException exception) {
            loadWarning = Ui.LOAD_FAILURE;
            return new ArrayList<>();
        }
    }

    /** Reports why saving has been disabled for the current session.
     *
     * @return the warning text, or an empty string when loading succeeded.
     */
    public String getLoadWarning() {
        return loadWarning;
    }

    /** Saves to a temporary sibling before atomically replacing the destination.
     *
     * @param tasks the complete task list to persist.
     * @return true if replacement succeeds; false leaves the original file untouched.
     */
    public boolean save(Iterable<Task> tasks) {
        if (!loadWarning.isEmpty() || dataFile == null) {
            return false;
        }
        try {
            createParentDirectory();
            rejectSymbolicLink();
            if (Files.exists(dataFile) && !Files.isWritable(dataFile)) {
                return false;
            }
            writeTasks(formatTasks(tasks));
            return true;
        } catch (IOException | SecurityException exception) {
            return false;
        }
    }

    /** Rejects links so replacement cannot unexpectedly target a different file.
     *
     * @throws IOException if the configured path is a symbolic link.
     */
    private void rejectSymbolicLink() throws IOException {
        if (Files.isSymbolicLink(dataFile)) {
            throw new IOException("Symbolic task-file links are not supported");
        }
    }

    /** Reads valid, unique records while remembering any damaged records.
     *
     * @return the successfully decoded tasks.
     * @throws IOException if the data file cannot be read completely.
     */
    private List<Task> readTasks() throws IOException {
        List<Task> tasks = new ArrayList<>();
        for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            if (!line.isBlank()) {
                addRecord(tasks, line);
            }
        }
        return tasks;
    }

    /** Adds one valid record or blocks future writes if it is invalid or duplicated.
     *
     * @param tasks the valid records loaded so far.
     * @param line the record to decode.
     */
    private void addRecord(List<Task> tasks, String line) {
        Task task = codec.parseTask(line);
        if (task == null || tasks.stream().anyMatch(existing -> TaskValidation.hasSameDetails(existing, task))) {
            loadWarning = Ui.CORRUPT_STORAGE;
            return;
        }
        tasks.add(task);
    }

    /** Converts tasks into the lines written to the temporary file.
     *
     * @param tasks the tasks being saved.
     * @return serialized task records.
     */
    private List<String> formatTasks(Iterable<Task> tasks) {
        return StreamSupport.stream(tasks.spliterator(), false).map(codec::formatTask).toList();
    }

    /** Writes a complete new file before replacing the old file in one filesystem operation.
     *
     * @param lines the serialized task records.
     * @throws IOException if writing or atomic replacement fails.
     */
    private void writeTasks(List<String> lines) throws IOException {
        Path target = dataFile.toAbsolutePath();
        Path temporaryFile = Files.createTempFile(target.getParent(), ".sevensix-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            discardTemporaryFile(temporaryFile);
        }
    }

    /** Removes an abandoned temporary file without masking the write or move result.
     *
     * @param temporaryFile the temporary sibling created for this save.
     */
    private void discardTemporaryFile(Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException exception) {
            // A leftover temporary file is preferable to reporting a committed save as failed.
        }
    }

    /** Creates the folder containing the data file when needed.
     *
     * @throws IOException if its parent folder cannot be created.
     */
    private void createParentDirectory() throws IOException {
        Path parent = dataFile.toAbsolutePath().getParent();
        Files.createDirectories(parent);
    }
}
