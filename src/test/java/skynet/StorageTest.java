package skynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests storage persistence and malformed save-file handling.
 */
public class StorageTest {

    @TempDir
    Path temporaryDirectory;

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data/saveFile.txt"));
    }

    @Test
    void load_missingFile_returnsEmptyList() throws IOException {
        assertTrue(createStorage().load().isEmpty());
    }

    @Test
    void saveAndLoad_preservesAllTaskTypesAndStatuses() throws IOException {
        Todo todo = new Todo("buy milk");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 12, 1, 18, 0));
        Event event = new Event("meeting",
                LocalDateTime.of(2026, 12, 2, 10, 0),
                LocalDateTime.of(2026, 12, 2, 11, 0));

        Storage storage = createStorage();
        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0).isDone());
        assertEquals("buy milk", loaded.get(0).getDescription());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loaded.get(1));
        assertEquals(deadline.getBy(), loadedDeadline.getBy());

        Event loadedEvent = assertInstanceOf(Event.class, loaded.get(2));
        assertEquals(event.getFrom(), loadedEvent.getFrom());
        assertEquals(event.getTo(), loadedEvent.getTo());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    void save_nullList_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> createStorage().save(null));
    }

    @Test
    void save_nullTaskOrUnsupportedTaskType_isRejected() {
        Storage storage = createStorage();
        Task unsupportedTask = new Task("unsupported") {
            @Override
            public Task copy() {
                return this;
            }

            @Override
            public String toString() {
                return "unsupported";
            }
        };

        assertThrows(IllegalArgumentException.class, () ->
                storage.save(java.util.Arrays.asList((Task) null)));
        assertThrows(IllegalArgumentException.class, () ->
                storage.save(List.of(unsupportedTask)));
    }

    @Test
    void load_malformedRows_throwFileErrorWithLineNumber() throws IOException {
        Path saveFile = temporaryDirectory.resolve("saveFile.txt");
        Storage storage = new Storage(saveFile);
        Files.write(saveFile, List.of("D | 0 | missing date"));

        IOException exception = assertThrows(IOException.class, storage::load);

        assertTrue(exception.getMessage().contains("line 1"));
    }

    @Test
    void load_invalidStatusTypeAndFieldCount_areRejected() throws IOException {
        String[] invalidRows = {
            "T | 2 | task",
            "X | 0 | task",
            "T | 0",
            "E | 0 | meeting | 2026-12-02T10:00",
            "E | 0 | meeting | 2026-12-02T10:00 | 2026-12-02T09:00"
        };

        for (String invalidRow : invalidRows) {
            Path saveFile = temporaryDirectory.resolve("save-" + invalidRows.length + ".txt");
            Files.write(saveFile, List.of(invalidRow));
            Storage storage = new Storage(saveFile);

            assertThrows(IOException.class, storage::load, invalidRow);
        }
    }

    @Test
    void load_blankRow_isRejected() throws IOException {
        Path saveFile = temporaryDirectory.resolve("blank.txt");
        Files.write(saveFile, List.of(""));

        assertThrows(IOException.class, () -> new Storage(saveFile).load());
    }
}
