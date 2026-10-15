package luckynoslacky.luckystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.io.TempDir;

import luckynoslacky.luckytask.TaskList;
import luckynoslacky.luckytask.TaskMaster;
import luckynoslacky.luckytask.TodoTask;

/**
 * Shared fixtures and filesystem doubles for CSV persistence tests.
 */
abstract class CsvSaverTestSupport {
    protected static final List<String> CSV_HEADER = List.of(
            "Task type",
            "isCompleted",
            "Description",
            "startTime",
            "endTime");
    protected static final LocalDateTime DEADLINE =
            LocalDateTime.of(2026, 12, 6, 23, 59);
    protected static final LocalDateTime EVENT_START =
            LocalDateTime.of(2026, 8, 6, 14, 0);
    protected static final LocalDateTime EVENT_END =
            LocalDateTime.of(2026, 8, 6, 16, 0);

    @TempDir
    protected Path temporaryDirectory;

    /** Creates a task list containing one task for save-failure tests. */
    protected TaskList createTaskList() {
        TaskList taskList = new TaskList();
        taskList.addTask(new TodoTask("read book"));
        return taskList;
    }

    /** Reads all records from a saved CSV file. */
    protected List<CSVRecord> readCsvRecords(Path dataFile) throws Exception {
        try (Reader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            return parser.getRecords();
        }
    }

    /** Reads CSV records as value lists for semantic comparisons. */
    protected List<List<String>> readCsvValues(Path dataFile) throws Exception {
        return readCsvRecords(dataFile).stream()
                .map(CSVRecord::toList)
                .toList();
    }

    /** Verifies the first saved task record after the CSV header. */
    protected void assertCsvRecord(
            Path dataFile,
            List<String> expectedRecord)
            throws Exception {
        List<CSVRecord> records = readCsvRecords(dataFile);
        assertEquals(expectedRecord, records.get(1).toList());
    }

    /** Writes a CSV header followed by the supplied records. */
    protected void writeCsvRecords(
            Path dataFile,
            List<List<String>> records)
            throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(
                dataFile, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord(CSV_HEADER);
            for (List<String> record : records) {
                printer.printRecord(record);
            }
        }
    }

    /** Loads a task master from the supplied CSV file. */
    protected TaskMaster loadTaskMaster(Path dataFile) {
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.loadTasksFromCsvStorageRecord(saver.load());
        return taskMaster;
    }

    /** Identifies a filesystem operation that can raise a security failure. */
    protected enum FileOperation {
        NOT_EXISTS,
        IS_REGULAR_FILE,
        NEW_BUFFERED_READER,
        CREATE_DIRECTORIES,
        CREATE_TEMPORARY_FILE,
        NEW_BUFFERED_WRITER,
        MOVE_ATOMICALLY,
        MOVE_REPLACING,
        DELETE_IF_EXISTS
    }

    /** Provides real filesystem behavior with selected deterministic failures. */
    protected static final class FaultInjectingFileOperations
            implements CsvSaver.FileOperations {
        private final Set<FileOperation> failingOperations;
        private final boolean doesNotSupportAtomicMove;

        protected FaultInjectingFileOperations(Set<FileOperation> failingOperations) {
            this(failingOperations, false);
        }

        protected FaultInjectingFileOperations(
                Set<FileOperation> failingOperations,
                boolean doesNotSupportAtomicMove) {
            this.failingOperations = EnumSet.copyOf(failingOperations);
            this.doesNotSupportAtomicMove = doesNotSupportAtomicMove;
        }

        @Override
        public boolean notExists(Path path) {
            failWhenConfigured(FileOperation.NOT_EXISTS);
            return Files.notExists(path);
        }

        @Override
        public boolean isRegularFile(Path path) {
            failWhenConfigured(FileOperation.IS_REGULAR_FILE);
            return Files.isRegularFile(path);
        }

        @Override
        public BufferedReader newBufferedReader(Path path) throws IOException {
            failWhenConfigured(FileOperation.NEW_BUFFERED_READER);
            return Files.newBufferedReader(path, StandardCharsets.UTF_8);
        }

        @Override
        public void createDirectories(Path directory) throws IOException {
            failWhenConfigured(FileOperation.CREATE_DIRECTORIES);
            Files.createDirectories(directory);
        }

        @Override
        public Path createTemporaryFile(Path directory) throws IOException {
            failWhenConfigured(FileOperation.CREATE_TEMPORARY_FILE);
            return directory == null
                    ? Files.createTempFile("luckyNoSlacky-", ".tmp")
                    : Files.createTempFile(directory, "luckyNoSlacky-", ".tmp");
        }

        @Override
        public BufferedWriter newBufferedWriter(Path path) throws IOException {
            failWhenConfigured(FileOperation.NEW_BUFFERED_WRITER);
            return Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        }

        @Override
        public void moveAtomically(Path source, Path destination) throws IOException {
            failWhenConfigured(FileOperation.MOVE_ATOMICALLY);
            if (doesNotSupportAtomicMove) {
                throw new AtomicMoveNotSupportedException(
                        source.toString(), destination.toString(), "Test fallback");
            }
            Files.move(
                    source,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        }

        @Override
        public void moveReplacing(Path source, Path destination) throws IOException {
            failWhenConfigured(FileOperation.MOVE_REPLACING);
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        @Override
        public boolean deleteIfExists(Path path) throws IOException {
            failWhenConfigured(FileOperation.DELETE_IF_EXISTS);
            return Files.deleteIfExists(path);
        }

        /** Throws a security exception when an operation was configured to fail. */
        private void failWhenConfigured(FileOperation operation) {
            if (failingOperations.contains(operation)) {
                throw new SecurityException(operation.name());
            }
        }
    }
}
