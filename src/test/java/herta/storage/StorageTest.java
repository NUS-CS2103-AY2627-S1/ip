package herta.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.exception.HertaException;
import herta.task.TaskList;

/**
 * Tests storage loading, path policy, and archive-file error wording.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingDataFile_returnsEmptyTaskList() throws HertaException {
        Path dataFile = temporaryDirectory.resolve("missing.txt");

        TaskList loadedTasks = new Storage(dataFile.toString()).load();

        assertEquals(0, loadedTasks.size());
    }

    @Test
    void storagePaths_nullAndMalformedInput_rejectConfiguration() {
        IllegalArgumentException storageException = assertThrows(IllegalArgumentException.class, () ->
                new Storage(null));
        IllegalArgumentException archiveException = assertThrows(IllegalArgumentException.class, () ->
                Storage.resolveArchivePath("\u0000"));

        assertEquals("Configured data path is invalid.", storageException.getMessage());
        assertEquals("Configured data path is invalid.", archiveException.getMessage());
    }

    @Test
    void resolveArchivePath_relativeAndNestedFiles_useSiblingArchive() {
        Path relativeArchive = Storage.resolveArchivePath("tasks.txt");
        Path nestedArchive = Storage.resolveArchivePath("data" + java.io.File.separator + "tasks.txt");

        assertEquals(Path.of(".").resolve("archive.txt"), relativeArchive);
        assertEquals(Path.of("data").resolve("archive.txt"), nestedArchive);
    }

    @Test
    void load_directoryPath_throwsHelpfulException() throws Exception {
        Path dataDirectory = temporaryDirectory.resolve("data-directory");
        Files.createDirectory(dataDirectory);

        HertaException exception = assertThrows(HertaException.class, () ->
                new Storage(dataDirectory.toString()).load());

        assertEquals("Failed to load tasks: data path is not a regular file.",
                exception.getMessage());
    }

    @Test
    void load_malformedRecord_reportsLineNumber() throws Exception {
        Path dataFile = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(dataFile, "T | 0 | valid task\nX | 0 | unknown task\n");

        HertaException exception = assertThrows(HertaException.class, () ->
                new Storage(dataFile.toString()).load());

        assertEquals("Failed to load tasks at line 2: Invalid saved task: unknown task type 'X'.",
                exception.getMessage());
    }

    @Test
    void load_duplicateRecords_preservesEachRecord() throws Exception {
        Path dataFile = temporaryDirectory.resolve("duplicates.txt");
        Files.writeString(dataFile, "T | 0 | read book\nT | 1 |  read   book \n");

        TaskList tasks = new Storage(dataFile.toString()).load();

        assertEquals(2, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("read   book", tasks.get(1).getDescription());
        assertTrue(tasks.get(1).isCompleted());
    }

    @Test
    void load_blankRecord_reportsLineNumberInsteadOfSkippingIt() throws Exception {
        Path dataFile = temporaryDirectory.resolve("blank-record.txt");
        Files.writeString(dataFile, "T | 0 | first\n\nT | 0 | second\n");

        HertaException exception = assertThrows(HertaException.class, () ->
                new Storage(dataFile.toString()).load());

        assertEquals("Failed to load tasks at line 2: blank records are not supported.",
                exception.getMessage());
    }

    @Test
    void load_controlCharacterInTaskDescription_reportsInvalidField() throws Exception {
        Path dataFile = temporaryDirectory.resolve("control-character.txt");
        Files.writeString(dataFile, "T | 0 | bad\u0000text\n");

        HertaException exception = assertThrows(HertaException.class, () ->
                new Storage(dataFile.toString()).load());

        assertEquals("Failed to load tasks at line 1: Task descriptions cannot contain `|`, "
                + "line breaks, or control characters.", exception.getMessage());
    }

    @Test
    void loadArchived_missingAndEmptyFiles_returnEmptyArchive() throws Exception {
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        Storage storage = new Storage(archiveFile.toString());

        assertEquals(0, storage.loadArchived().size());
        storage.save(new TaskList());
        assertTrue(Files.exists(archiveFile));
        assertEquals(0, storage.loadArchived().size());
    }

    @Test
    void loadArchived_preservesIncompleteRecordsAndUsesArchiveErrors() throws Exception {
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        Files.writeString(archiveFile, "T | 0 | incomplete\n");

        TaskList archivedTasks = new Storage(archiveFile.toString()).loadArchived();

        assertFalse(archivedTasks.get(0).isCompleted());
        Files.writeString(archiveFile, "T | 2 | invalid\n");
        Storage storage = new Storage(archiveFile.toString());
        HertaException exception = assertThrows(HertaException.class, storage::loadArchived);
        assertTrue(exception.getMessage().startsWith("Failed to load archived tasks at line "));
    }

    @Test
    void loadArchived_invalidEncoding_reportsArchiveStartupFailure() throws Exception {
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        Files.write(archiveFile, new byte[] {(byte) 0xc3, (byte) 0x28});

        Storage storage = new Storage(archiveFile.toString());
        HertaException exception = assertThrows(HertaException.class, storage::loadArchived);

        assertTrue(exception.getMessage().startsWith("Failed to load archived tasks: "));
    }
}
