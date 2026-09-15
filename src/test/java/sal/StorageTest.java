package sal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Storage} save/load round-trips and handling of missing or corrupt data files.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    private Storage createStorage(String fileName) {
        return new Storage(tempDir.resolve(fileName).toString());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws IOException {
        Storage storage = createStorage("missing.txt");
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_todoDeadlineAndEvent_roundTripsAllFields() throws Exception {
        Storage storage = createStorage("sal.txt");
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2019-10-15");
        Event event = Parser.parseEvent("event meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);
        TaskList loaded = new TaskList(storage.load());

        assertEquals(3, loaded.size());

        Task loadedTodo = loaded.get(0);
        assertInstanceOf(Todo.class, loadedTodo);
        assertEquals("read book", loadedTodo.description);
        assertTrue(loadedTodo.isDone);

        Task loadedDeadline = loaded.get(1);
        assertInstanceOf(Deadline.class, loadedDeadline);
        Deadline asDeadline = (Deadline) loadedDeadline;
        assertEquals("return book", asDeadline.description);
        assertFalse(asDeadline.isDone);
        assertFalse(asDeadline.by.hasTime());
        assertEquals("2019-10-15", DateTimeParser.formatForStorage(asDeadline.by));

        Task loadedEvent = loaded.get(2);
        assertInstanceOf(Event.class, loadedEvent);
        Event asEvent = (Event) loadedEvent;
        assertEquals("meeting", asEvent.description);
        assertTrue(asEvent.from.hasTime());
        assertEquals("2019-12-02 1400", DateTimeParser.formatForStorage(asEvent.from));
        assertEquals("2019-12-02 1600", DateTimeParser.formatForStorage(asEvent.to));
    }

    @Test
    public void saveThenLoad_tasksWithTags_roundTripsTags() throws Exception {
        Storage storage = createStorage("tagged.txt");
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.addTag("fun");
        todo.addTag("cs2103");
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2019-10-15");
        deadline.addTag("urgent");
        tasks.add(todo);
        tasks.add(deadline);

        storage.save(tasks);
        TaskList loaded = new TaskList(storage.load());

        assertEquals(2, loaded.get(0).getTags().size());
        assertEquals("fun", loaded.get(0).getTags().get(0));
        assertEquals("cs2103", loaded.get(0).getTags().get(1));
        assertEquals("urgent", loaded.get(1).getTags().get(0));
    }

    @Test
    public void save_nestedPath_createsMissingParentDirectories() throws Exception {
        Storage storage = new Storage(tempDir.resolve("nested/data/sal.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        storage.save(tasks);
        assertTrue(Files.exists(tempDir.resolve("nested/data/sal.txt")));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_todoWithoutTagsField_loadsEmptyTags() throws Exception {
        Path file = tempDir.resolve("legacy.txt");
        Files.write(file, List.of("T | 0 | read book"));
        Storage storage = new Storage(file.toString());

        Task loaded = new TaskList(storage.load()).get(0);
        assertEquals("read book", loaded.description);
        assertTrue(loaded.getTags().isEmpty());
    }

    @Test
    public void load_emptyLines_areSkipped() throws Exception {
        Path file = tempDir.resolve("sal.txt");
        Files.write(file, List.of("", "T | 0 | read book", "   "));
        Storage storage = new Storage(file.toString());

        TaskList loaded = new TaskList(storage.load());
        assertEquals(1, loaded.size());
        assertEquals("read book", loaded.get(0).description);
    }

    @Test
    public void load_invalidLine_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("sal.txt");
        Files.write(file, List.of("not a valid task line"));
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void load_unknownTaskType_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("sal.txt");
        Files.write(file, List.of("X | 0 | mystery task"));
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void load_todoWithExtraFields_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("sal.txt");
        Files.write(file, List.of("T | 0 | read book | extra | more"));
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void load_deadlineWithInvalidDate_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("sal.txt");
        Files.write(file, List.of("D | 0 | return book | not-a-date"));
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void load_eventWithInvalidDate_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("sal.txt");
        Files.write(file, List.of("E | 0 | meeting | 2019-10-15 | not-a-date"));
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void load_deadlineWithoutTagsField_loadsEmptyTags() throws Exception {
        Path file = tempDir.resolve("legacy-deadline.txt");
        Files.write(file, List.of("D | 0 | return book | 2019-10-15"));
        Storage storage = new Storage(file.toString());

        Task loaded = new TaskList(storage.load()).get(0);
        assertInstanceOf(Deadline.class, loaded);
        assertEquals("return book", loaded.description);
        assertTrue(loaded.getTags().isEmpty());
        assertEquals("2019-10-15", DateTimeParser.formatForStorage(((Deadline) loaded).by));
    }

    @Test
    public void load_eventWithoutTagsField_loadsEmptyTags() throws Exception {
        Path file = tempDir.resolve("legacy-event.txt");
        Files.write(file, List.of("E | 1 | meeting | 2019-10-15 | 2019-10-16"));
        Storage storage = new Storage(file.toString());

        Task loaded = new TaskList(storage.load()).get(0);
        assertInstanceOf(Event.class, loaded);
        assertEquals("meeting", loaded.description);
        assertTrue(loaded.isDone);
        assertTrue(loaded.getTags().isEmpty());
    }

    @Test
    public void load_deadlineMissingDate_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("sal.txt");
        Files.write(file, List.of("D | 0 | return book"));
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void load_eventMissingEndDate_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("sal.txt");
        Files.write(file, List.of("E | 0 | meeting | 2019-10-15"));
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::load);
    }
}
