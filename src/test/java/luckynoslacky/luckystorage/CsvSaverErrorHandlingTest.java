package luckynoslacky.luckystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import luckynoslacky.luckyexception.LuckyNoStorageException;
import luckynoslacky.luckytask.TaskList;
import luckynoslacky.luckytask.TaskMaster;
import luckynoslacky.luckytask.TodoTask;

/** Tests CSV validation, failure handling, and rollback behavior. */
class CsvSaverErrorHandlingTest extends CsvSaverTestSupport {
    /** Verifies load-side security failures retain their original cause. */
    @Test
    void load_securityFailures_throwStorageException() throws Exception {
        Path dataFile = temporaryDirectory.resolve("secured.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n",
                StandardCharsets.UTF_8);
        for (FileOperation operation : List.of(
                FileOperation.NOT_EXISTS,
                FileOperation.IS_REGULAR_FILE,
                FileOperation.NEW_BUFFERED_READER)) {
            CsvSaver saver = new CsvSaver(
                    dataFile,
                    new FaultInjectingFileOperations(Set.of(operation)));

            LuckyNoStorageException exception = assertThrows(
                    LuckyNoStorageException.class, saver::load);

            assertEquals("Unable to load tasks.", exception.getMessage());
            assertInstanceOf(SecurityException.class, exception.getCause());
        }
    }

    /** Verifies save-side security failures consistently become storage errors. */
    @Test
    void save_securityFailures_throwStorageException() {
        for (FileOperation operation : List.of(
                FileOperation.CREATE_DIRECTORIES,
                FileOperation.CREATE_TEMPORARY_FILE,
                FileOperation.NEW_BUFFERED_WRITER,
                FileOperation.MOVE_ATOMICALLY,
                FileOperation.DELETE_IF_EXISTS)) {
            CsvSaver saver = new CsvSaver(
                    temporaryDirectory.resolve(operation.name() + ".csv"),
                    new FaultInjectingFileOperations(Set.of(operation)));

            LuckyNoStorageException exception = assertThrows(
                    LuckyNoStorageException.class, () -> saver.save(createTaskList()));

            assertEquals("Unable to save tasks.", exception.getMessage());
            assertInstanceOf(SecurityException.class, exception.getCause());
        }
    }

    /** Verifies fallback replacement security failures become storage errors. */
    @Test
    void save_nonAtomicMoveSecurityFailure_throwsStorageException() {
        CsvSaver saver = new CsvSaver(
                temporaryDirectory.resolve("fallback-move.csv"),
                new FaultInjectingFileOperations(
                        Set.of(FileOperation.MOVE_REPLACING), true));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () -> saver.save(createTaskList()));

        assertEquals("Unable to save tasks.", exception.getMessage());
        assertInstanceOf(SecurityException.class, exception.getCause());
    }

    /** Verifies a primary save failure retains a cleanup failure as suppressed. */
    @Test
    void save_primaryAndCleanupFailures_preservesPrimaryFailure() {
        CsvSaver saver = new CsvSaver(
                temporaryDirectory.resolve("multiple-failures.csv"),
                new FaultInjectingFileOperations(Set.of(
                        FileOperation.MOVE_ATOMICALLY,
                        FileOperation.DELETE_IF_EXISTS)));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () -> saver.save(createTaskList()));

        assertEquals("MOVE_ATOMICALLY", exception.getCause().getMessage());
        assertEquals(1, exception.getSuppressed().length);
        assertInstanceOf(
                SecurityException.class,
                exception.getSuppressed()[0].getCause());
    }

    /** Verifies a cleanup-only failure causes task mutations to roll back. */
    @Test
    void addTask_cleanupFailure_rollsBackTaskList() {
        CsvSaver saver = new CsvSaver(
                temporaryDirectory.resolve("cleanup-failure.csv"),
                new FaultInjectingFileOperations(Set.of(FileOperation.DELETE_IF_EXISTS)));
        TaskMaster taskMaster = new TaskMaster(100, saver);

        assertThrows(LuckyNoStorageException.class, () ->
                taskMaster.addTask(new TodoTask("read book")));

        assertEquals(0, taskMaster.getTaskCount());
    }

    /** Verifies saving creates a missing parent directory. */
    @Test
    void save_missingParentDirectory_createsDirectoryAndFile() {
        Path dataFile = temporaryDirectory
                .resolve("nested")
                .resolve("luckyNoSlacky.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);

        taskMaster.addTask(new TodoTask("read book"));

        assertTrue(Files.isRegularFile(dataFile));
    }

    /** Verifies unknown task types are rejected during loading. */
    @Test
    void load_unknownTaskType_throwsExactRowMessage() throws Exception {
        Path dataFile = temporaryDirectory.resolve("invalid.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n"
                        + "X,0,unknown task,,\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals(
                "Invalid task record at row 2: unknown task type",
                exception.getMessage());
    }

    /** Verifies invalid completion flags are rejected during loading. */
    @Test
    void load_invalidCompletionStatus_throwsStorageException() throws Exception {
        Path dataFile = temporaryDirectory.resolve("invalid-status.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n"
                        + "T,2,read book,,\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals(
                "Invalid task record at row 2: invalid completion status",
                exception.getMessage());
    }

    /** Verifies invalid CSV headers are rejected during loading. */
    @Test
    void load_invalidHeader_throwsStorageException() throws Exception {
        Path dataFile = temporaryDirectory.resolve("invalid-header.csv");
        Files.writeString(dataFile,
                "type,status,description,start,end\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals("Invalid task data header.", exception.getMessage());
    }

    /** Verifies malformed task records are rejected during loading. */
    @Test
    void load_malformedTaskRecord_throwsExactRowMessage() throws Exception {
        Path dataFile = temporaryDirectory.resolve("malformed-record.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n"
                        + "D,0,return book,,2030-13-01 10:00\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals("Invalid task data at row 2", exception.getMessage());
    }

    /** Verifies records with missing fields are rejected during loading. */
    @Test
    void load_recordWithMissingFields_throwsStorageException() throws Exception {
        Path dataFile = temporaryDirectory.resolve("missing-fields.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n"
                        + "T,0,read book,\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals(
                "Invalid task record at row 2: incorrect number of fields",
                exception.getMessage());
    }

    /** Verifies records with extra fields are rejected during loading. */
    @Test
    void load_recordWithExtraFields_throwsExactStorageException() throws Exception {
        Path dataFile = temporaryDirectory.resolve("extra-fields.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n"
                        + "T,0,read book,,,unexpected\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals(
                "Invalid task record at row 2: incorrect number of fields",
                exception.getMessage());
    }

    /** Verifies blank descriptions are rejected during loading. */
    @Test
    void load_blankDescription_throwsExactStorageException() throws Exception {
        Path dataFile = temporaryDirectory.resolve("blank-description.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n"
                        + "T,0,   ,,\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals("Invalid task data at row 2", exception.getMessage());
    }

    /** Verifies syntactically malformed CSV is converted to a storage error. */
    @Test
    void load_malformedCsvSyntax_throwsExactStorageException() throws Exception {
        Path dataFile = temporaryDirectory.resolve("malformed-syntax.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n"
                        + "T,0,\"unterminated description,,\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals("Unable to load tasks.", exception.getMessage());
    }

    /** Verifies that a malformed later record prevents partial loading. */
    @Test
    void load_validThenMalformedRecord_throwsExactLaterRowMessage() throws Exception {
        Path dataFile = temporaryDirectory.resolve("malformed-later-record.csv");
        Files.writeString(dataFile,
                "Task type,isCompleted,Description,startTime,endTime\n"
                        + "T,0,valid task,,\n"
                        + "D,0,invalid task,,2030-13-01 10:00\n",
                StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals("Invalid task data at row 3", exception.getMessage());
    }

    /** Verifies saving a null task list is rejected. */
    @Test
    void save_nullTaskList_throwsIllegalArgumentException() {
        CsvSaver saver = new CsvSaver(temporaryDirectory.resolve("tasks.csv"));

        assertThrows(IllegalArgumentException.class, () -> saver.save(null));
    }

    /** Verifies that a saver rejects a missing data-file path. */
    @Test
    void construct_nullDataFile_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CsvSaver(null));
    }

    /** Verifies file paths that point to directories are rejected. */
    @Test
    void loadOrSave_directoryPath_throwsStorageException() throws Exception {
        Path dataPath = temporaryDirectory.resolve("directory");
        Files.createDirectory(dataPath);
        CsvSaver saver = new CsvSaver(dataPath);

        LuckyNoStorageException loadException = assertThrows(
                LuckyNoStorageException.class, saver::load);
        assertEquals(
                "The task data path is not a regular file.",
                loadException.getMessage());
        LuckyNoStorageException saveException = assertThrows(
                LuckyNoStorageException.class, () ->
                saver.save(new TaskList()));
        assertEquals("Unable to save tasks.", saveException.getMessage());
    }
}
