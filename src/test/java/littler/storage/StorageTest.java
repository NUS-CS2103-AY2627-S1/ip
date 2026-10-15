package littler.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import littler.datetime.StringDateTimeConverter;
import littler.exception.LittleRException;
import littler.task.Deadline;
import littler.task.Event;
import littler.task.Task;
import littler.task.Todo;
import littler.task.attribute.PriorityLevel;

public class StorageTest {

    @TempDir
    Path tempDir;

    // ---- load: missing file ----

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws LittleRException {
        Storage storage = new Storage(tempDir.resolve("nonexistent.txt").toString());
        ArrayList<Task> loaded = storage.load();
        assertTrue(loaded.isEmpty());
    }

    // ---- save + load: round trip ----

    @Test
    public void saveThenLoad_todoTask_roundTripsCorrectly() throws LittleRException {
        Storage storage = new Storage(tempDir.resolve("data.txt").toString());
        ArrayList<Task> original = new ArrayList<>();
        original.add(new Todo("read book"));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(original.get(0).toString(), loaded.get(0).toString());
    }

    @Test
    public void saveThenLoad_deadlineTask_roundTripsCorrectly() throws LittleRException {
        Storage storage = new Storage(tempDir.resolve("data.txt").toString());
        ArrayList<Task> original = new ArrayList<>();
        original.add(new Deadline("return book", StringDateTimeConverter.parse("2-12-2019 1800")));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(original.get(0).toString(), loaded.get(0).toString());
    }

    @Test
    public void saveThenLoad_eventTask_roundTripsCorrectly() throws LittleRException {
        Storage storage = new Storage(tempDir.resolve("data.txt").toString());
        ArrayList<Task> original = new ArrayList<>();
        original.add(new Event("meeting",
            StringDateTimeConverter.parse("6-8-2026 1400"),
            StringDateTimeConverter.parse("6-8-2026 1600")));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(original.get(0).toString(), loaded.get(0).toString());
    }

    @Test
    public void saveThenLoad_markedTask_preservesMarkedState() throws LittleRException {
        Storage storage = new Storage(tempDir.resolve("data.txt").toString());
        Todo task = new Todo("read book");
        task.mark();
        ArrayList<Task> original = new ArrayList<>();
        original.add(task);

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.get(0).toString().contains("[X]"));
    }

    @Test
    public void saveThenLoad_multipleTasksMixedTypes_preservesOrderAndContent() throws LittleRException {
        Storage storage = new Storage(tempDir.resolve("data.txt").toString());
        ArrayList<Task> original = new ArrayList<>();
        original.add(new Todo("first"));
        original.add(new Deadline("second", StringDateTimeConverter.parse("2-12-2019")));
        original.add(new Event("third",
            StringDateTimeConverter.parse("6-8-2026"),
            StringDateTimeConverter.parse("7-8-2026")));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), loaded.get(i).toString());
        }
    }

    @Test
    public void saveThenLoad_taskWithPriority_preservesPriority() throws LittleRException {
        // Priority is a Category C extension — verify it survives a storage round-trip.
        Storage storage = new Storage(tempDir.resolve("data.txt").toString());
        Todo task = new Todo("important task");
        task.setPriority(PriorityLevel.HIGH);
        ArrayList<Task> original = new ArrayList<>();
        original.add(task);

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(PriorityLevel.HIGH, loaded.get(0).getPriority());
    }

    @Test
    public void saveThenLoad_taskWithTags_preservesTags() throws LittleRException {
        // Tags are a Category C extension — verify they survive a storage round-trip.
        Storage storage = new Storage(tempDir.resolve("data.txt").toString());
        Todo task = new Todo("tagged task");
        task.addTag("work");
        task.addTag("urgent");
        ArrayList<Task> original = new ArrayList<>();
        original.add(task);

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.get(0).getTags().contains("work"));
        assertTrue(loaded.get(0).getTags().contains("urgent"));
    }

    // ---- load: corrupted lines ----

    @Test
    public void load_corruptedLineAmongValidLines_skipsOnlyCorruptedLine()
            throws LittleRException, java.io.IOException {
        Path file = tempDir.resolve("data.txt");
        Storage storage = new Storage(file.toString());

        // Save one valid task first, to get a real, correctly-formatted line
        ArrayList<Task> original = new ArrayList<>();
        original.add(new Todo("valid task"));
        storage.save(original);

        // Append a deliberately malformed line
        List<String> lines = new ArrayList<>(Files.readAllLines(file));
        lines.add("NOT | A | VALID | LINE | AT ALL");
        Files.write(file, lines);

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("valid task", loaded.get(0).toString().replaceAll("^\\[T\\]\\[.\\] ", ""));
    }
}
