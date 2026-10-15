package bubba;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Protects event persistence, including compatibility with previously saved events.
 */
public class StorageTest {
    @TempDir
    Path directory;

    @Test
    public void save_event_roundTripsTimesAndCompletion() throws Exception {
        LocalDateTime from = LocalDateTime.of(2026, 12, 3, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 12, 3, 16, 30);
        Event event = new Event("meeting", from, to);
        event.done();
        Path file = directory.resolve("data/tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.save(new ArrayList<>(List.of(event)));
        assertEquals("E | 1 | meeting | 03/12/2026 1400 | 03/12/2026 1630",
                Files.readString(file).strip());

        Storage reloaded = new Storage(file.toString());
        List<Task> tasks = reloaded.load();
        assertEquals(1, tasks.size());
        Event actual = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("meeting", actual.description);
        assertEquals(from, actual.from);
        assertEquals(to, actual.to);
        assertTrue(actual.isDone);
        assertTrue(reloaded.getLoadWarnings().isEmpty());
    }

    @Test
    public void load_legacyIsoEvent_preservesTaskAndCanResave() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "E | 1 | meeting | 2026-12-03T14:00 | 2026-12-03T16:30\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals(LocalDateTime.of(2026, 12, 3, 14, 0), event.from);
        assertEquals(LocalDateTime.of(2026, 12, 3, 16, 30), event.to);
        assertTrue(event.isDone);
        assertTrue(storage.getLoadWarnings().isEmpty());
        storage.save(tasks);
        assertEquals("E | 1 | meeting | 03/12/2026 1400 | 03/12/2026 1630",
                Files.readString(file).strip());
    }

    @Test
    public void load_malformedRecords_skipsRecordsWithWarnings() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | valid task\n"
                + "D | 0 | missing date\n"
                + "E | 0 | bad date | tomorrow | later\n"
                + "X | 0 | unknown type\n");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] valid task", tasks.get(0).toString());
        assertEquals(3, storage.getLoadWarnings().size());
    }
}
