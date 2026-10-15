package gongrilla.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gongrilla.task.Task;
import gongrilla.task.Todo;

/** Exercises corrupt journals and filesystem boundaries without using real application data. */
class StorageBoundaryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void append_existingFileWithoutFinalNewline_keepsRecordsSeparate() throws IOException {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(path, "T | 0 | original");
        Storage storage = new Storage(path);
        storage.appendAdd(new Todo("new"));
        assertEquals(List.of("original", "new"), storage.load().stream().map(Task::getName).toList());
    }

    @Test
    void append_fileLocked_reportsIoErrorAndPreservesFile() throws IOException {
        Path path = temporaryDirectory.resolve("locked.txt");
        Files.writeString(path, "T | 0 | original\n");
        String before = Files.readString(path);
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE);
                FileLock lock = channel.lock()) {
            assertTrue(lock.isValid());
            assertThrows(IOException.class, () -> new Storage(path).appendAdd(new Todo("new")));
        }
        assertEquals(before, Files.readString(path));
    }

    @Test
    void constructor_nullPath_rejectsInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new Storage(null));
    }

    @Test
    void append_invalidArguments_doesNotCreateFile() {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(path);
        assertThrows(IllegalArgumentException.class, () -> storage.appendAdd(null));
        assertThrows(IllegalArgumentException.class, () -> storage.appendDelete(-1));
        assertThrows(IllegalArgumentException.class, () -> storage.appendMark(-1));
        assertThrows(IllegalArgumentException.class, () -> storage.appendUnmark(-1));
        assertFalse(Files.exists(path));
    }

    @Test
    void load_directoryInsteadOfFile_reportsIoError() {
        IOException error = assertThrows(IOException.class, () -> new Storage(temporaryDirectory).load());
        assertTrue(error.getMessage().contains("not a regular file"));
    }

    @Test
    void append_parentIsFile_reportsIoErrorWithoutChangingParent() throws IOException {
        Path parent = temporaryDirectory.resolve("blocked");
        Files.writeString(parent, "keep me");
        Storage storage = new Storage(parent.resolve("tasks.txt"));
        assertThrows(IOException.class, () -> storage.appendAdd(new Todo("read")));
        assertEquals("keep me", Files.readString(parent));
    }

    @Test
    void load_blankLinesAndMixedRecords_preservesLegacyTextAndIndexOrder() throws IOException {
        Path path = temporaryDirectory.resolve("mixed.txt");
        Files.write(path, List.of("", "  ", "T | 0 | keep + %20", "A | T2 | 0 | buy+bananas",
                "M | 1", "X | 0", "U | 0", "T2 | 1 | %E4%BD%A0%E5%A5%BD"));
        List<Task> tasks = new Storage(path).load();
        assertEquals(2, tasks.size());
        assertEquals("buy bananas", tasks.get(0).getName());
        assertFalse(tasks.get(0).isDone());
        assertEquals("你好", tasks.get(1).getName());
        assertTrue(tasks.get(1).isDone());
        Files.writeString(path, "T | 0 | keep + %20");
        assertEquals("keep + %20", new Storage(path).load().getFirst().getName());
    }

    @Test
    void load_corruptRecords_reportsOriginalLineAndPreservesFile() throws IOException {
        List<String> records = List.of(
                "Q | 0", "A", "A | Q | 0 | task", "T2 | 2 | task",
                "T2 | 0 | task | extra", "D2 | 0 | task", "E2 | 0 | task | 2026-01-01",
                "T2 | 0 | ", "T2 | 0 | %ZZ", "D2 | 0 | task | invalid",
                "E2 | 0 | task | 2026-01-01 | invalid",
                "M | not-a-number", "M | 999999999999999999", "U | -1", "X | 1",
                "M | 0 | extra", "X");
        Path path = temporaryDirectory.resolve("corrupt.txt");
        for (String record : records) {
            String data = "T2 | 0 | valid\n\n" + record + "\n";
            Files.writeString(path, data);
            IOException error = assertThrows(IOException.class, () -> new Storage(path).load(), record);
            assertTrue(error.getMessage().contains("line 3"), record);
            assertNotNull(error.getCause(), record);
            assertEquals(data, Files.readString(path), record);
        }
    }

    @Test
    void load_operationOnEmptyJournal_rejectsIndex() throws IOException {
        Path path = temporaryDirectory.resolve("empty.txt");
        for (String operation : List.of("M", "U", "X")) {
            Files.writeString(path, operation + " | 0");
            assertThrows(IOException.class, () -> new Storage(path).load(), operation);
        }
    }

    @Test
    void append_nestedDirectoryAndUnicode_roundTripsWithoutRewriting() throws IOException {
        Path path = temporaryDirectory.resolve("数据/nested/tasks.txt");
        Storage storage = new Storage(path);
        storage.appendAdd(new Todo("买香蕉 🐴 + | %\nnext"));
        String firstRecord = Files.readString(path);
        storage.appendAdd(new Todo("second"));
        assertTrue(Files.readString(path).startsWith(firstRecord));
        assertEquals("买香蕉 🐴 + | %\nnext", storage.load().getFirst().getName());
        assertEquals(2, Files.readAllLines(path).size());
    }

    @Test
    void append_carriageReturnInBrokenSerializer_rejectsBeforeWriting() {
        Path path = temporaryDirectory.resolve("broken.txt");
        Task brokenTask = new Todo("broken") {
            @Override
            public String toDataString() {
                return "T2 | 0 | first\rsecond";
            }
        };
        assertThrows(AssertionError.class, () -> new Storage(path).appendAdd(brokenTask));
        assertFalse(Files.exists(path));
    }
}
