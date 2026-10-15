package clarry.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

import clarry.ClarryException;
import clarry.task.Deadline;
import clarry.task.Event;
import clarry.task.Task;
import clarry.task.Todo;

/** Tests real save files in temporary directories, including malformed records. */
class StorageTest {
    @TempDir
    private Path directory;

    @Test
    void load_missingAndEmptyFiles_returnsEmptyWithoutCreatingData() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        assertTrue(storage.load().getTasks().isEmpty());
        assertFalse(Files.exists(file));
        Files.writeString(file, "");
        assertEquals(0, storage.load().getCorruptedLineCount());
        assertTrue(storage.load().getTasks().isEmpty());
    }

    @Test
    void save_allTaskTypes_roundTripsStatusOrderAndUnicode() throws IOException, ClarryException {
        Path file = directory.resolve("nested/tasks.txt");
        Todo todo = new Todo("café 鱼");
        Deadline deadline = new Deadline("book", "2024-02-29");
        Event event = new Event("trip", "2024-02-29 23:00", "2024-03-01 01:00");
        todo.markAsDone();
        event.markAsDone();
        List<Task> tasks = List.of(todo, deadline, event);
        Storage storage = new Storage(file);
        storage.save(tasks);
        var result = storage.load();
        assertEquals(0, result.getCorruptedLineCount());
        assertEquals(tasks.stream().map(Task::toFileFormat).toList(),
                result.getTasks().stream().map(Task::toFileFormat).toList());
        assertTrue(Files.readString(file).contains("café 鱼"));
        storage.save(List.of());
        assertEquals("", Files.readString(file));
    }

    @Test
    void load_invalidRecordShapes_skipsEachWithoutLosingFollowingTasks() throws IOException {
        List<String> invalid = List.of("", "garbage", "T | 2 | book", "T | 0 | ",
                "T | 0 | a|b", "T | 0 | a\tb", "T | 0 | book | extra",
                "D | 0 | book", "D | 0 | book | ", "D | 0 | book | 2023-02-29",
                "E | 0 | trip", "E | 0 | trip |  | 2024-01-01 10:00",
                "E | 0 | trip | 2024-01-01 10:00 | ",
                "E | 0 | trip | 2024-01-01 10:00 | 2024-01-01 10:00",
                "X | 0 | book");
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | first\n" + String.join("\n", invalid) + "\nT | 1 | last\n");
        var result = new Storage(file).load();
        assertEquals(invalid.size(), result.getCorruptedLineCount());
        assertEquals(List.of("T | 0 | first", "T | 1 | last"),
                result.getTasks().stream().map(Task::toFileFormat).toList());
    }

    @Test
    void load_duplicateRecords_keepsFirstStatusAndCountsDuplicate() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | book\nT | 1 | book\nT | 0 | Book\n");
        var result = new Storage(file).load();
        assertEquals(1, result.getCorruptedLineCount());
        assertEquals(2, result.getTasks().size());
        assertFalse(result.getTasks().getFirst().isDone());
    }

    @Test
    void load_lfAndCrLfFiles_reconstructsIdenticalTasks() throws IOException {
        Path file = directory.resolve("tasks.txt");
        for (String ending : List.of("\n", "\r\n")) {
            Files.writeString(file, "T | 0 | first" + ending + "T | 1 | last" + ending);
            var result = new Storage(file).load();
            assertEquals(0, result.getCorruptedLineCount());
            assertEquals(2, result.getTasks().size());
            assertTrue(result.getTasks().getLast().isDone());
        }
    }

    @Test
    void load_invalidUtf8_reportsIoFailure() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.write(file, new byte[] {(byte) 0xc3, (byte) 0x28});
        assertThrows(IOException.class, () -> new Storage(file).load());
    }

    @Test
    void save_parentIsFile_failsWithoutChangingParent() throws IOException {
        Path parent = directory.resolve("parent");
        Files.writeString(parent, "original");
        assertThrows(IOException.class, () -> new Storage(parent.resolve("tasks.txt")).save(List.of(new Todo("book"))));
        assertEquals("original", Files.readString(parent));
    }

    @Test
    @ResourceLock("defaultLocale")
    void save_chineseLocale_keepsPortableDateFormat() throws IOException, ClarryException {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
            Path file = directory.resolve("tasks.txt");
            new Storage(file).save(List.of(new Deadline("还书", "2024-02-29")));
            assertEquals("D | 0 | 还书 | 2024-02-29", Files.readAllLines(file).getFirst());
            assertEquals("还书", new Storage(file).load().getTasks().getFirst().getDescription());
        } finally {
            Locale.setDefault(original);
        }
    }
}
