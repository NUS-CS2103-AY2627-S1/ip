package alfred.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alfred.task.Deadline;
import alfred.task.Event;
import alfred.task.Task;
import alfred.task.TaskDateTime;
import alfred.task.ToDo;

/**
 * Tests {@link Storage#save(List)} and {@link Storage#load()} including missing files
 * and corrupted lines.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDir.resolve("missing.txt"));
        List<Task> loaded = storage.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void load_directoryPath_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDir);
        List<Task> loaded = storage.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void saveThenLoad_roundTripsTodoDeadlineAndEvent() throws IOException {
        Path file = tempDir.resolve("data/alfred.txt");
        Storage storage = new Storage(file);
        List<Task> saved = new ArrayList<>();
        saved.add(new ToDo("read book"));
        saved.add(new Deadline("return book", TaskDateTime.parseUserInput("2019-10-15")));
        Event event = new Event("camp", TaskDateTime.parseUserInput("2019-10-14"),
                TaskDateTime.parseUserInput("2019-10-16"));
        event.markAsDone();
        saved.add(event);

        storage.save(saved);
        assertTrue(Files.isRegularFile(file));

        List<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).getDisplayText());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).getDisplayText());
        assertEquals("[E][X] camp (from: Oct 14 2019 to: Oct 16 2019)", loaded.get(2).getDisplayText());
        assertEquals("E | 1 | camp | 2019-10-14 | 2019-10-16 | UA", loaded.get(2).toSaveFormat());
    }

    @Test
    public void load_skipsCorruptedAndBlankLines() throws IOException {
        Path file = tempDir.resolve("alfred.txt");
        Files.writeString(file, """
                T | 0 | keep this

                not-a-task
                T | 2 | bad status
                D | 0 | broken | Sunday
                E | 0 | camp | 2019-10-14 | 2019-10-16
                """, StandardCharsets.UTF_8);

        List<Task> loaded = new Storage(file).load();
        assertEquals(2, loaded.size());
        assertEquals("[T][ ] keep this", loaded.get(0).getDisplayText());
        assertEquals("[E][ ] camp (from: Oct 14 2019 to: Oct 16 2019)", loaded.get(1).getDisplayText());
    }

    @Test
    public void load_mixedArchiveFlags_keepsArchivedAndLive() throws IOException {
        Path file = tempDir.resolve("alfred.txt");
        Files.writeString(file, """
                T | 0 | live task | UA
                T | 0 | archived task | A
                """, StandardCharsets.UTF_8);

        List<Task> loaded = new Storage(file).load();
        assertEquals(2, loaded.size());
        assertFalse(loaded.get(0).isArchived());
        assertTrue(loaded.get(1).isArchived());
        assertEquals("T | 0 | archived task | A", loaded.get(1).toSaveFormat());
    }
}
