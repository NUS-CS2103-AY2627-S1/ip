package auto.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.storage.Storage.LoadResult;
import auto.task.Deadline;
import auto.task.Event;
import auto.task.Task;
import auto.task.ToDo;

/** Tests durable task round trips, malformed-data recovery, and storage failures. */
class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    void load_missingFile_returnsEmptyTasksAndWarnings() throws Exception {
        Storage storage = storageAt("missing/tasks.txt");

        LoadResult result = storage.load();

        assertTrue(result.tasks().isEmpty());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesTextDatesAndCompletionStatus() throws Exception {
        Task todo = new ToDo("read | book");
        todo.mark();
        Task deadline = new Deadline("提交报告", LocalDate.of(2026, 8, 22));
        Task event = new Event("conference", LocalDate.of(2026, 8, 21),
                LocalDate.of(2026, 8, 23));
        event.mark();
        Storage storage = storageAt("nested/tasks.txt");

        storage.save(List.of(todo, deadline, event));
        LoadResult result = storage.load();

        assertTrue(Files.isRegularFile(tempDirectory.resolve("nested/tasks.txt")));
        assertTrue(result.warnings().isEmpty());
        assertEquals(3, result.tasks().size());
        assertEquals("[T][X] read | book", result.tasks().get(0).toString());
        assertEquals("[D][ ] 提交报告 (by: Aug 22 2026)", result.tasks().get(1).toString());
        assertEquals("[E][X] conference (from: Aug 21 2026 to: Aug 23 2026)",
                result.tasks().get(2).toString());
    }

    @Test
    void save_existingFile_replacesOldContentsWithoutTemporaryFiles() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "obsolete data", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile.toString());
        Task replacement = new ToDo("read book");

        storage.save(List.of(replacement));

        assertEquals(List.of(replacement.toDataString()),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        try (var files = Files.list(tempDirectory)) {
            assertEquals(List.of(dataFile), files.toList());
        }
    }

    @Test
    void save_emptyList_replacesExistingDataWithEmptyFile() throws Exception {
        Storage storage = storageAt("tasks.txt");
        storage.save(List.of(new ToDo("read book")));

        storage.save(List.of());
        LoadResult result = storage.load();

        assertEquals(0, Files.size(tempDirectory.resolve("tasks.txt")));
        assertTrue(result.tasks().isEmpty());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void load_blankLines_ignoresThemWithoutWarnings() throws Exception {
        Path dataFile = writeData("tasks.txt", "\n   \nT | 0 | cmVhZCBib29r\n\t\n");
        Storage storage = new Storage(dataFile.toString());

        LoadResult result = storage.load();

        assertEquals(1, result.tasks().size());
        assertEquals("[T][ ] read book", result.tasks().getFirst().toString());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void load_mixedValidAndMalformedRecords_recoversValidTasksAndReportsLineNumbers()
            throws Exception {
        String data = String.join("\n",
                "T | 1 | cmVhZCBib29r",
                "not a task",
                "",
                "T | 2 | cmVhZCBib29r",
                "T | 0",
                "T | 0 | !!!",
                "T | 0 | IA==",
                "D | 0 | cmV0dXJuIGJvb2s= | bm90LWEtZGF0ZQ==",
                "E | 0 | Y29uZmVyZW5jZQ== | QXVnIDIxIDIwMjY=",
                "Z | 0 | dW5rbm93bg==",
                "D | 0 | c3VibWl0IHJlcG9ydA== | QXVnIDIyIDIwMjY=");
        Path dataFile = writeData("tasks.txt", data);

        LoadResult result = new Storage(dataFile.toString()).load();

        assertEquals(2, result.tasks().size());
        assertEquals("[T][X] read book", result.tasks().get(0).toString());
        assertEquals("[D][ ] submit report (by: Aug 22 2026)",
                result.tasks().get(1).toString());
        assertEquals(List.of(
                "line 2 is invalid and was skipped",
                "line 4 is invalid and was skipped",
                "line 5 is invalid and was skipped",
                "line 6 is invalid and was skipped",
                "line 7 is invalid and was skipped",
                "line 8 is invalid and was skipped",
                "line 9 is invalid and was skipped",
                "line 10 is invalid and was skipped"), result.warnings());
    }

    @Test
    void load_zeroAndOneStatuses_restoresEachTaskTypeCorrectly() throws Exception {
        String data = String.join("\n",
                "T | 0 | cmVhZCBib29r",
                "D | 1 | c3VibWl0IHJlcG9ydA== | QXVnIDIyIDIwMjY=",
                "E | 0 | Y29uZmVyZW5jZQ== | QXVnIDIxIDIwMjY= | QXVnIDIzIDIwMjY=");
        Path dataFile = writeData("tasks.txt", data);

        List<Task> tasks = new Storage(dataFile.toString()).load().tasks();

        assertFalse(tasks.get(0).isCompleted());
        assertTrue(tasks.get(1).isCompleted());
        assertFalse(tasks.get(2).isCompleted());
    }

    @Test
    void load_dataPathIsDirectory_throwsIOException() {
        Storage storage = new Storage(tempDirectory.toString());

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("the data path is not a regular file", exception.getMessage());
    }

    @Test
    void save_dataPathIsDirectory_throwsIOExceptionWithoutCreatingTemporaryFile()
            throws Exception {
        Storage storage = new Storage(tempDirectory.toString());

        IOException exception = assertThrows(IOException.class,
                () -> storage.save(List.of(new ToDo("read book"))));

        assertEquals("the data path is not a regular file", exception.getMessage());
        try (var files = Files.list(tempDirectory)) {
            assertTrue(files.findAny().isEmpty());
        }
    }

    @Test
    void save_parentPathIsRegularFile_throwsIOExceptionAndPreservesParentFile()
            throws Exception {
        Path parentFile = tempDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "keep me", StandardCharsets.UTF_8);
        Storage storage = new Storage(parentFile.resolve("tasks.txt").toString());

        assertThrows(IOException.class,
                () -> storage.save(List.of(new ToDo("read book"))));

        assertEquals("keep me", Files.readString(parentFile, StandardCharsets.UTF_8));
    }

    @Test
    void load_invalidUtf8_throwsIOException() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.write(dataFile, new byte[] {(byte) 0xC3, (byte) 0x28});
        Storage storage = new Storage(dataFile.toString());

        assertThrows(IOException.class, storage::load);
    }

    private Storage storageAt(String relativePath) {
        return new Storage(tempDirectory.resolve(relativePath).toString());
    }

    private Path writeData(String relativePath, String contents) throws IOException {
        Path dataFile = tempDirectory.resolve(relativePath);
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, contents, StandardCharsets.UTF_8);
        return dataFile;
    }
}
