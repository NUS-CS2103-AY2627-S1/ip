package dude.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dude.storage.json.TaskJsonCodec;
import dude.task.Task;
import dude.task.TaskList;

/**
 * Loads and saves tasks as one escaped JSON object per line.
 */
public class Storage {
    /** Relative path of the current task file. */
    private static final Path DATA_FILE = Path.of("data", "dude.jsonl");
    /** Relative path of the legacy task file supported during migration. */
    private static final Path LEGACY_DATA_FILE = Path.of("data", "dude.txt");
    private final Path dataFile;
    private final Path legacyDataFile;
    private final TaskJsonCodec codec;

    /**
     * Creates a storage handler for the default task data file.
     */
    public Storage() {
        this(DATA_FILE, LEGACY_DATA_FILE);
    }

    /**
     * Creates a storage handler using explicit data paths.
     *
     * @param dataFile       JSONL task file.
     * @param legacyDataFile Legacy task file used for migration.
     */
    public Storage(Path dataFile, Path legacyDataFile) {
        this.dataFile = Objects.requireNonNull(dataFile, "dataFile");
        this.legacyDataFile = Objects.requireNonNull(legacyDataFile, "legacyDataFile");
        codec = new TaskJsonCodec();
    }

    private static ArrayList<Task> readTasks(Path file, TaskJsonCodec codec) throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                .filter(line -> !line.isBlank())
                .map(codec::parseSafely)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Replaces the data file with the completed temporary file.
     *
     * @param temporaryFile Completed temporary file.
     * @param dataFile      Destination task file.
     * @throws IOException If the replacement cannot be completed.
     */
    private static void replaceDataFile(Path temporaryFile, Path dataFile) throws IOException {
        try {
            Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads every valid task and represents malformed records as corrupted tasks.
     *
     * @return Tasks reconstructed from the data file.
     * @throws IOException If the data directory or file cannot be accessed.
     */
    public TaskList loadTasks() throws IOException {
        Files.createDirectories(dataFile.getParent());

        if (Files.exists(dataFile)) {
            return new TaskList(readTasks(dataFile, codec));
        }

        if (Files.exists(legacyDataFile)) {
            ArrayList<Task> tasks = readTasks(legacyDataFile, codec);
            saveTasks(new TaskList(tasks));
            return new TaskList(tasks);
        }

        Files.createFile(dataFile);
        return new TaskList();
    }

    /**
     * Saves the supplied tasks, replacing the data file only after writing completes.
     *
     * @param tasks Tasks to persist.
     * @throws IOException If the data directory or file cannot be accessed.
     */
    public void saveTasks(TaskList tasks) throws IOException {
        Objects.requireNonNull(tasks, "tasks");
        Files.createDirectories(dataFile.getParent());
        Path temporaryFile = Files.createTempFile(dataFile.getParent(), "dude", ".tmp");

        try {
            List<String> lines = tasks.asList().stream()
                    .map(codec::serialize)
                    .toList();
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile, dataFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }
}
