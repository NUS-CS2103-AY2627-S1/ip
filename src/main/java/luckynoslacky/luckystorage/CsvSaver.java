package luckynoslacky.luckystorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import luckynoslacky.luckyexception.LuckyNoStorageException;
import luckynoslacky.luckytask.DeadlineTask;
import luckynoslacky.luckytask.EventTask;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskList;
import luckynoslacky.luckytask.TaskTimes;
import luckynoslacky.luckytask.TodoTask;

/**
 * Saves the current task list as a CSV file.
 */
public class CsvSaver {
    private static final List<String> CSV_HEADER = List.of(
            "Task type",
            "isCompleted",
            "Description",
            "startTime",
            "endTime");
    private static final int EXPECTED_FIELD_COUNT = 5;

    private static final Path DEFAULT_DATA_FILE =
            Paths.get("data", "luckyNoSlacky.csv");

    private final Path dataFile;
    private final FileOperations fileOperations;

    /**
     * Creates a saver that writes to the application's default data file.
     */
    public CsvSaver() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates a saver that writes to a specified file.
     *
     * @param dataFile destination CSV file
     * @throws IllegalArgumentException if {@code dataFile} is null
     */
    public CsvSaver(Path dataFile) {
        this(dataFile, new NioFileOperations());
    }

    /**
     * Creates a saver with explicit filesystem operations.
     *
     * @param dataFile destination CSV file
     * @param fileOperations filesystem operations used to access the data file
     * @throws IllegalArgumentException if either argument is null
     */
    CsvSaver(Path dataFile, FileOperations fileOperations) {
        if (dataFile == null) {
            throw new IllegalArgumentException("Data file cannot be null.");
        }
        if (fileOperations == null) {
            throw new IllegalArgumentException("File operations cannot be null.");
        }
        this.dataFile = dataFile;
        this.fileOperations = fileOperations;
    }

    /**
     * Rewrites the CSV file with the current task list.
     *
     * @param taskList task list to save
     */
    public void save(TaskList taskList) {
        if (taskList == null) {
            throw new IllegalArgumentException("Task list cannot be null.");
        }

        Path temporaryFile = null;
        LuckyNoStorageException saveFailure = null;
        try {
            createParentDirectory();
            temporaryFile = createTemporaryFile();
            writeCsvFile(taskList, temporaryFile);
            replaceDataFile(temporaryFile);
        } catch (IOException | IllegalArgumentException | SecurityException exception) {
            saveFailure = new LuckyNoStorageException(
                    "Unable to save tasks.", exception);
        } finally {
            saveFailure = deleteTemporaryFile(temporaryFile, saveFailure);
        }

        if (saveFailure != null) {
            throw saveFailure;
        }
    }

    /**
     * Creates the parent directory for the data file when necessary.
     *
     * @throws IOException if the directory cannot be created
     */
    private void createParentDirectory() throws IOException {
        Path parent = dataFile.getParent();

        if (parent != null) {
            fileOperations.createDirectories(parent);
        }
    }

    /**
     * Creates a temporary file in the data file's directory.
     *
     * @return path to the temporary file
     * @throws IOException if the temporary file cannot be created
     */
    private Path createTemporaryFile() throws IOException {
        Path parent = dataFile.getParent();

        return fileOperations.createTemporaryFile(parent);
    }

    /**
     * Writes the CSV header and task records to a temporary file.
     *
     * @param taskList task list to serialize
     * @param outputFile temporary output file
     * @throws IOException if writing the file fails
     */
    private void writeCsvFile(TaskList taskList, Path outputFile)
            throws IOException {
        try (BufferedWriter writer = fileOperations.newBufferedWriter(outputFile);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord(CSV_HEADER);

            for (Task task : taskList.getTasks()) {
                List<String> record = TaskCsvCodec.toRecord(task);
                assert record.size() == EXPECTED_FIELD_COUNT
                        : "Unexpected CSV field count: " + record.size();
                printer.printRecord(record);
            }
        }
    }

    /**
     * Replaces the data file with the completed temporary file.
     *
     * @param temporaryFile completed temporary file
     * @throws IOException if the replacement fails
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            fileOperations.moveAtomically(temporaryFile, dataFile);
        } catch (AtomicMoveNotSupportedException exception) {
            fileOperations.moveReplacing(temporaryFile, dataFile);
        }
    }

    /**
     * Deletes a temporary file after a save attempt and combines cleanup
     * failures with any earlier save failure.
     *
     * @param temporaryFile temporary file to delete, or null if none was made
     * @param saveFailure primary save failure, or null when saving succeeded
     * @return the primary failure, cleanup failure, or null when neither failed
     */
    private LuckyNoStorageException deleteTemporaryFile(
            Path temporaryFile, LuckyNoStorageException saveFailure) {
        if (temporaryFile == null) {
            return saveFailure;
        }

        try {
            fileOperations.deleteIfExists(temporaryFile);
            return saveFailure;
        } catch (IOException | SecurityException exception) {
            LuckyNoStorageException cleanupFailure = new LuckyNoStorageException(
                    "Unable to save tasks.", exception);
            if (saveFailure != null) {
                saveFailure.addSuppressed(cleanupFailure);
                return saveFailure;
            }
            return cleanupFailure;
        }
    }

    /**
     * Loads all tasks from the CSV file.
     *
     * @return tasks stored in the file, or an empty list if the file is absent
     */
    public List<Task> load() {
        try {
            if (fileOperations.notExists(dataFile)) {
                return List.of();
            }

            if (!fileOperations.isRegularFile(dataFile)) {
                throw new LuckyNoStorageException(
                        "The task data path is not a regular file.");
            }

            return readCsvFile();
        } catch (IOException | SecurityException exception) {
            throw new LuckyNoStorageException(
                    "Unable to load tasks.", exception);
        } catch (LuckyNoStorageException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new LuckyNoStorageException(
                    "Unable to load tasks.", exception);
        }
    }

    /**
     * Reads CSV records after the data file has been verified as readable.
     *
     * @return tasks represented by the CSV file
     * @throws IOException if the data file cannot be read
     */
    private List<Task> readCsvFile() throws IOException {
        try (BufferedReader reader = fileOperations.newBufferedReader(dataFile);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            List<CSVRecord> records = parser.getRecords();

            if (records.isEmpty()) {
                return List.of();
            }

            validateHeader(records.get(0));

            return IntStream.range(1, records.size())
                    .mapToObj(index -> createTaskFromCsvStorageRecord(records.get(index)))
                    .toList();
        }
    }

    /**
     * Validates that a CSV header has the expected columns.
     *
     * @param header first CSV record in the data file
     * @throws LuckyNoStorageException if the header is not recognized
     */
    private void validateHeader(CSVRecord header) {
        if (!header.toList().equals(CSV_HEADER)) {
            throw new LuckyNoStorageException("Invalid task data header.");
        }
    }

    /**
     * Converts one CSV record into the corresponding task subtype.
     *
     * @param record CSV task record
     * @return task represented by the record
     * @throws LuckyNoStorageException if the record is malformed
     */
    private Task createTaskFromCsvStorageRecord(CSVRecord record) {
        if (record.size() != EXPECTED_FIELD_COUNT) {
            throw invalidRecord(record, "incorrect number of fields");
        }

        String taskType = record.get(0);
        String description = record.get(2);

        boolean isCompleted = parseCompletionStatus(record);

        Task task;
        try {
            TaskTimes times = createTaskTimes(record);
            task = switch (taskType) {
                case "T" -> new TodoTask(description);
                case "D" -> new DeadlineTask(description, times);
                case "E" -> new EventTask(description, times);
                default -> throw invalidRecord(record, "unknown task type");
            };
        } catch (DateTimeParseException | IllegalArgumentException exception) {
            throw new LuckyNoStorageException(
                    "Invalid task data at row "
                            + record.getRecordNumber(),
                    exception);
        }

        if (isCompleted) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Converts the time columns of a CSV record into task timing information.
     *
     * @param record CSV task record containing the stored task type and times
     * @return timing information represented by the record
     */
    private TaskTimes createTaskTimes(CSVRecord record) {
        String taskType = record.get(0);
        String startTimeText = record.get(3);
        String endTimeText = record.get(4);

        return switch (taskType) {
            case "T" -> TaskTimes.none();
            case "D" -> TaskTimes.makeDeadlineTimes(
                    DateTimeStorageCodec.parse(endTimeText));
            case "E" -> TaskTimes.makeEventTimes(
                    DateTimeStorageCodec.parse(startTimeText),
                    DateTimeStorageCodec.parse(endTimeText));
            default -> throw invalidRecord(record, "unknown task type");
        };
    }

    /**
     * Parses the completion flag stored in a CSV record.
     *
     * @param record CSV task record
     * @return whether the task is completed
     * @throws LuckyNoStorageException if the flag is neither 0 nor 1
     */
    private boolean parseCompletionStatus(CSVRecord record) {
        return switch (record.get(1)) {
            case "0" -> false;
            case "1" -> true;
            default -> throw invalidRecord(record, "invalid completion status");
        };
    }

    /**
     * Creates a storage exception describing an invalid CSV record.
     *
     * @param record malformed CSV record
     * @param reason explanation of the validation failure
     * @return exception describing the invalid record
     */
    private LuckyNoStorageException invalidRecord(
            CSVRecord record,
            String reason) {
        return new LuckyNoStorageException(
                "Invalid task record at row "
                        + record.getRecordNumber()
                        + ": " + reason);
    }

    /** Defines the filesystem operations required by CSV storage. */
    interface FileOperations {
        /** Checks whether a path is known not to exist. */
        boolean notExists(Path path);

        /** Checks whether a path is a regular file. */
        boolean isRegularFile(Path path);

        /** Opens a UTF-8 reader for a file. */
        BufferedReader newBufferedReader(Path path) throws IOException;

        /** Creates a directory and any missing parent directories. */
        void createDirectories(Path directory) throws IOException;

        /** Creates a temporary file, optionally in a supplied directory. */
        Path createTemporaryFile(Path directory) throws IOException;

        /** Opens a UTF-8 writer for a file. */
        BufferedWriter newBufferedWriter(Path path) throws IOException;

        /** Replaces a file using an atomic move when the filesystem supports it. */
        void moveAtomically(Path source, Path destination) throws IOException;

        /** Replaces a file using a non-atomic move. */
        void moveReplacing(Path source, Path destination) throws IOException;

        /** Deletes a file if it exists. */
        boolean deleteIfExists(Path path) throws IOException;
    }

    /** Implements storage filesystem operations using the platform NIO API. */
    private static final class NioFileOperations implements FileOperations {
        @Override
        public boolean notExists(Path path) {
            return Files.notExists(path);
        }

        @Override
        public boolean isRegularFile(Path path) {
            return Files.isRegularFile(path);
        }

        @Override
        public BufferedReader newBufferedReader(Path path) throws IOException {
            return Files.newBufferedReader(path, StandardCharsets.UTF_8);
        }

        @Override
        public void createDirectories(Path directory) throws IOException {
            Files.createDirectories(directory);
        }

        @Override
        public Path createTemporaryFile(Path directory) throws IOException {
            return directory == null
                    ? Files.createTempFile("luckyNoSlacky-", ".tmp")
                    : Files.createTempFile(directory, "luckyNoSlacky-", ".tmp");
        }

        @Override
        public BufferedWriter newBufferedWriter(Path path) throws IOException {
            return Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        }

        @Override
        public void moveAtomically(Path source, Path destination) throws IOException {
            Files.move(
                    source,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        }

        @Override
        public void moveReplacing(Path source, Path destination) throws IOException {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        @Override
        public boolean deleteIfExists(Path path) throws IOException {
            return Files.deleteIfExists(path);
        }
    }
}
