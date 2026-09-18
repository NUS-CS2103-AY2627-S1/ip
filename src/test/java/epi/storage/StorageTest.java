package epi.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import epi.exception.EpiException;
import epi.task.Deadline;
import epi.task.Event;
import epi.task.TaskList;
import epi.task.Todo;

/**
 * Protects the existing task-file format using independent temporary directories.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void prepareFile_missingParents_createsEmptyFile() throws IOException, EpiException {
        Path path = temporaryDirectory.resolve("nested/task data/epi.txt");
        Storage storage = new Storage(path.toString());

        storage.prepareFile();

        assertTrue(Files.isRegularFile(path));
        assertEquals("", Files.readString(path));
        assertEquals(0, storage.load().size());
    }

    @Test
    void prepareFile_existingFile_preservesContents() throws IOException, EpiException {
        Path path = temporaryDirectory.resolve("epi.txt");
        String saved = "T | 1 | read book\n";
        Files.writeString(path, saved);

        new Storage(path.toString()).prepareFile();

        assertEquals(saved, Files.readString(path));
    }

    @Test
    void load_existingRecords_restoresTypesStatusDatesAndOrder() throws IOException, EpiException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 1 | read book\n"
                + "D | 0 | return book | 2019-12-02 1800\n"
                + "E | 1 | meeting | 2019-12-02 1400 | 2019-12-02 1600\n");

        TaskList loaded = new Storage(path.toString()).load();

        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)", loaded.get(1).toString());
        assertEquals("[E][X] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                loaded.get(2).toString());
    }

    @Test
    void saveAndLoad_mixedTasks_roundTripsAllFields() throws IOException, EpiException {
        Path path = temporaryDirectory.resolve("tasks with spaces.txt");
        Storage storage = new Storage(path.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read café notes"));
        tasks.add(new Deadline("return book", "2019-12-02 1800"));
        tasks.add(new Event("meeting", "2019-12-02 1400", "2019-12-02 1600"));
        tasks.get(1).markAsDone();
        storage.prepareFile();

        storage.save(tasks);
        TaskList loaded = new Storage(path.toString()).load();

        assertEquals(List.of("T | 0 | read café notes", "D | 1 | return book | 2019-12-02 1800",
                "E | 0 | meeting | 2019-12-02 1400 | 2019-12-02 1600"), Files.readAllLines(path));
        assertEquals(tasks.size(), loaded.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).getClass(), loaded.get(i).getClass());
            assertEquals(tasks.get(i).toString(), loaded.get(i).toString());
            assertEquals(tasks.get(i).toFileFormat(), loaded.get(i).toFileFormat());
        }
    }

    @Test
    void save_shorterList_replacesOldRecords() throws IOException, EpiException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | first\nT | 0 | second\nT | 0 | third\n");
        Storage storage = new Storage(path.toString());
        TaskList tasks = storage.load();
        tasks.delete(1);
        tasks.delete(1);

        storage.save(tasks);

        assertEquals(List.of("T | 0 | first"), Files.readAllLines(path));
        assertEquals(1, new Storage(path.toString()).load().size());
    }

    @Test
    void save_emptyList_removesPreviousRecords() throws IOException, EpiException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | read book\n");
        Storage storage = new Storage(path.toString());

        storage.save(new TaskList());

        assertEquals("", Files.readString(path));
        assertEquals(0, storage.load().size());
    }

    @Test
    void save_separateFiles_doesNotAlterOtherData() throws IOException, EpiException {
        Path first = temporaryDirectory.resolve("first.txt");
        Path second = temporaryDirectory.resolve("second.txt");
        Files.writeString(first, "T | 0 | first\n");
        Files.writeString(second, "T | 1 | second\n");

        new Storage(first.toString()).save(new TaskList());

        assertEquals("T | 1 | second\n", Files.readString(second));
        assertEquals("[T][X] second", new Storage(second.toString()).load().get(0).toString());
    }

    @Test
    void load_missingFile_createsEmptyFileWithoutWarnings() {
        Path path = temporaryDirectory.resolve("new/epi.txt");
        Storage storage = new Storage(path.toString());
        assertEquals(0, storage.load().size());
        assertTrue(Files.isRegularFile(path));
        assertEquals(List.of(), storage.getWarnings());
    }

    @Test
    void load_badRecords_recoversValidRowsAndBlocksOverwrite() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        for (String bad : List.of("broken", "T | 0", "T | 0 | ", "T | 2 | book", "Z | 0 | book",
                "T | 0 | book | extra", "T | 0 | a|b", "D | 0 | book | 2020-02-30 1200",
                "E | 0 | meeting | 2020-01-01 1200 | 2020-01-01 1200",
                "E | 0 | meeting | 2020-01-01 1300 | 2020-01-01 1200")) {
            String contents = "T | 1 | first\n" + bad + "\nT | 0 | last\n";
            Files.writeString(path, contents);
            Storage storage = new Storage(path.toString());
            TaskList recovered = storage.load();
            assertEquals(2, recovered.size(), bad);
            assertEquals("[T][X] first", recovered.get(0).toString());
            assertEquals("[T][ ] last", recovered.get(1).toString());
            assertEquals(2, storage.getWarnings().size());
            assertTrue(storage.getWarnings().getFirst().contains("line 2"));
            assertThrows(EpiException.class, () -> storage.save(recovered));
            assertEquals(contents, Files.readString(path), bad);
        }
    }

    @Test
    void load_blankLinesAndDuplicates_preservesValidLegacyTasks() throws IOException, EpiException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "\nT | 0 | book\r\n \t \r\nT | 0 | book\r\n");
        Storage storage = new Storage(path.toString());
        TaskList tasks = storage.load();
        assertEquals(2, tasks.size());
        assertEquals(List.of(), storage.getWarnings());
        storage.save(tasks);
        assertEquals(2, new Storage(path.toString()).load().size());
    }

    @Test
    void load_invalidUtf8_reportsWarningAndPreservesBytes() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        byte[] invalid = {(byte) 0xc3, (byte) 0x28};
        Files.write(path, invalid);
        Storage storage = new Storage(path.toString());
        assertEquals(0, storage.load().size());
        assertEquals(2, storage.getWarnings().size());
        assertThrows(EpiException.class, () -> storage.save(new TaskList()));
        assertArrayEquals(invalid, Files.readAllBytes(path));
    }

    @Test
    void load_unusablePaths_reportsErrorsWithoutChangingOtherFiles() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "keep this file");
        for (String path : List.of(temporaryDirectory.toString(), temporaryDirectory.getRoot().toString(),
                parentFile.resolve("epi.txt").toString(),
                temporaryDirectory.resolve("bad").toString() + "\0")) {
            Storage storage = new Storage(path);
            assertEquals(0, storage.load().size());
            assertEquals(2, storage.getWarnings().size());
            assertThrows(EpiException.class, () -> storage.save(new TaskList()));
        }
        assertEquals("keep this file", Files.readString(parentFile));
    }

    @Test
    void load_readAccessDenied_reportsWarningWithoutOverwriting() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | book\n");
        Storage storage = new Storage(path.toString()) {
            @Override
            byte[] readBytes(Path target) throws IOException {
                throw new AccessDeniedException(target.toString());
            }
        };
        assertEquals(0, storage.load().size());
        assertEquals(2, storage.getWarnings().size());
        assertThrows(EpiException.class, () -> storage.save(new TaskList()));
        assertEquals("T | 0 | book\n", Files.readString(path));
    }

    @Test
    void save_replacementDenied_preservesOriginalAndRemovesTemporaryFile() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | book\n");
        Storage storage = new Storage(path.toString()) {
            @Override
            void replaceFile(Path source, Path target) throws IOException {
                throw new AccessDeniedException(target.toString());
            }
        };
        storage.load();
        EpiException error = assertThrows(EpiException.class, () -> storage.save(new TaskList()));
        assertTrue(error.getMessage().contains("No task changes were kept"));
        assertEquals("T | 0 | book\n", Files.readString(path));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(path), files.toList());
        }
    }

    @Test
    void save_atomicReplacementUnsupported_doesNotFallBackToUnsafeWrite() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | book\n");
        Storage storage = new Storage(path.toString()) {
            @Override
            void replaceFile(Path source, Path target) throws IOException {
                throw new AtomicMoveNotSupportedException(source.toString(), target.toString(), "test");
            }
        };
        assertTrue(assertThrows(EpiException.class, () -> storage.save(new TaskList()))
                .getMessage().contains("atomic file moves"));
        assertEquals("T | 0 | book\n", Files.readString(path));
    }

    @Test
    void save_externalChange_blocksStaleOverwriteUntilReload() throws IOException, EpiException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | original\n");
        Storage storage = new Storage(path.toString());
        TaskList stale = storage.load();
        Files.writeString(path, "T | 0 | external edit\n");
        assertTrue(assertThrows(EpiException.class, () -> storage.save(stale))
                .getMessage().contains("changed outside Epi"));
        assertEquals("T | 0 | external edit\n", Files.readString(path));
        assertThrows(EpiException.class, () -> storage.save(stale));
        TaskList fresh = storage.load();
        fresh.add(new Todo("new"));
        storage.save(fresh);
        assertEquals(2, storage.load().size());
    }

    @Test
    void save_deletedOrReplacedFile_failsWithoutRecreatingIt() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "T | 0 | book\n");
        Storage storage = new Storage(path.toString());
        storage.load();
        Files.delete(path);
        assertThrows(EpiException.class, () -> storage.save(new TaskList()));
        assertTrue(Files.notExists(path));
        Files.createDirectory(path);
        assertThrows(EpiException.class, () -> storage.save(new TaskList()));
        assertTrue(Files.isDirectory(path));
    }

    @Test
    void getWarnings_returnedList_cannotDisableProtection() throws IOException {
        Path path = temporaryDirectory.resolve("epi.txt");
        Files.writeString(path, "broken\n");
        Storage storage = new Storage(path.toString());
        storage.load();
        assertThrows(UnsupportedOperationException.class, () -> storage.getWarnings().clear());
        assertThrows(EpiException.class, () -> storage.save(new TaskList()));
    }
}
