package bambolino;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import bambolino.storage.Storage;
import bambolino.task.Deadline;
import bambolino.task.Event;
import bambolino.task.Task;
import bambolino.task.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Exercises real file persistence in temporary directories, never the user's data. */
class StorageTest {
    @TempDir
    Path directory;

    @Test
    void saveLoad_allTaskTypes_roundTripSpecialCharactersAndStatus() throws IOException {
        Storage storage = new Storage(directory.resolve("nested/tasks.txt"));
        List<Task> tasks = List.of(new Todo("中文 | café\nnext line"),
                new Deadline("return book", LocalDate.of(2024, 2, 29)),
                new Event("meeting", "Mon | 2pm", "4pm\nend"));
        tasks.forEach(Task::markAsDone);
        storage.save(tasks);
        List<Task> loaded = storage.load();
        assertEquals(tasks.stream().map(Task::toString).toList(), loaded.stream().map(Task::toString).toList());
        assertEquals(tasks.stream().map(Task::toStorageString).toList(),
                loaded.stream().map(Task::toStorageString).toList());
        storage.save(List.of());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void load_missingFile_returnsEmptyList() throws IOException {
        assertTrue(new Storage(directory.resolve("missing.txt")).load().isEmpty());
    }

    @Test
    void load_corruptRecords_keepsValidNeighbors() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.write(file, List.of("T|0|Ym9vaw==", "bad", "T|2|Ym9vaw==", "T|0|%%%",
                "X|0|Ym9vaw==", "T|0|Ym9vaw==|extra", "D|0|Ym9vaw==|MjAyMy0wMi0yOQ==",
                "E|0|YQ==|Yg==|Yw==|extra", "E|0|YQ==|Yg==|Yw=="));
        assertEquals(List.of("[T][ ] book", "[E][ ] a (from: b to: c)"),
                new Storage(file).load().stream().map(Task::toString).toList());
    }

    @Test
    void save_afterReadFailure_preservesOriginalBytesEvenAfterRepair() throws IOException {
        Path file = directory.resolve("tasks.txt");
        byte[] invalidUtf8 = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, invalidUtf8);
        Storage storage = new Storage(file);
        assertThrows(IOException.class, storage::load);
        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("replacement"))));
        assertArrayEquals(invalidUtf8, Files.readAllBytes(file));
        String repaired = "T|0|Ym9vaw==\n";
        Files.writeString(file, repaired);
        storage.load();
        assertThrows(IOException.class, () -> storage.save(List.of()));
        assertEquals(repaired, Files.readString(file));
        Storage restarted = new Storage(file);
        assertEquals("book", restarted.load().getFirst().getDescription());
        restarted.save(List.of(new Todo("recovered")));
        assertEquals("recovered", restarted.load().getFirst().getDescription());
    }

    @Test
    void save_afterCorruptRecord_preservesWholeFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        String original = "bad\nT|0|Ym9vaw==\n";
        Files.writeString(file, original);
        Storage storage = new Storage(file);
        assertEquals(1, storage.load().size());
        assertThrows(IOException.class, () -> storage.save(List.of()));
        assertEquals(original, Files.readString(file));
    }

    @Test
    void save_afterMissingFile_createsDataNormally() throws IOException {
        Storage storage = new Storage(directory.resolve("new/tasks.txt"));
        assertTrue(storage.load().isEmpty());
        storage.save(List.of(new Todo("first task")));
        assertEquals("first task", storage.load().getFirst().getDescription());
    }

    @Test
    void storage_invalidFileLocations_reportIoErrors() throws IOException {
        assertThrows(IOException.class, () -> new Storage(directory).load());
        assertThrows(IOException.class, () -> new Storage(directory).save(List.of()));
        Path parentFile = directory.resolve("file");
        Files.writeString(parentFile, "occupied");
        assertThrows(IOException.class, () -> new Storage(parentFile.resolve("tasks.txt")).save(List.of()));
    }
}
