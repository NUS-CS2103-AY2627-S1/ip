package anders.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import anders.AndersException;
import anders.collection.TaskList;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;

/** Tests persistence of tasks, including encoding and legacy records. */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_encodedRecords_reconstructsTasksAndStatus() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "2|T|1|cmVhZCB8IGJvb2s=",
                "2|D|0|cmV0dXJuIGJvb2s=|MjAxOS0xMi0wMg==",
                "2|E|0|cHJvamVjdCBtZWV0aW5n|MjAyNS0wMS0wMSAxNDowMA==|MjAyNS0wMS0wMSAxNjowMA=="));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals("read | book", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());
        assertEquals("2019-12-02", ((Deadline) tasks.get(1)).getByText());
        assertEquals("2025-01-01 1400", ((Event) tasks.get(2)).getFromText());
        assertEquals("2025-01-01 1600", ((Event) tasks.get(2)).getToText());
    }

    @Test
    public void load_taggedRecords_reconstructsReusableTags() throws Exception {
        Path file = temporaryDirectory.resolve("tagged-tasks.txt");
        Files.write(file, List.of(
                "3|T|0|cmVhZCBib29r|I2Z1bg==",
                "3|T|1|cmV2aXNlIG5vdGVz|I2Z1bg==",
                "3|D|0|cmV0dXJuIGJvb2s=|MjAxOS0xMi0wMg==|I2FkbWlu"));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals(List.of("#fun"), tasks.get(0).getTags());
        assertEquals(List.of("#fun"), tasks.get(1).getTags());
        assertEquals(List.of("#admin"), tasks.get(2).getTags());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void load_legacyRecords_reconstructsTaskTypesAndStatus() throws Exception {
        Path file = temporaryDirectory.resolve("legacy-tasks.txt");
        Files.write(file, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2/12/2019",
                "E | 0 | project meeting | 2025-01-01 1400 | 2025-01-01 1600"));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(3, tasks.size());
        assertTrue(tasks.get(0).isDone());
        assertEquals("2/12/2019", ((Deadline) tasks.get(1)).getByText());
        assertEquals("2025-01-01 1400", ((Event) tasks.get(2)).getFromText());
    }

    @Test
    public void load_malformedRecords_ignoresInvalidEntries() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "2|T|0|dmFsaWQ=",
                "not a valid record",
                "2|X|0|YmFkIHR5cGU=",
                "2|T|9|aW52YWxpZCBzdGF0dXM="));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("valid", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void save_tasks_writesEncodedVersionedRecords() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        TaskList tasks = new TaskList();
        Task todo = new Todo("read | book");
        todo.markAsDone();
        todo.addTags(List.of("#fun"));
        tasks.add(todo);
        tasks.add(new Deadline("return book", "2019-12-02"));
        tasks.add(new Event("meeting", "2025-01-01 14:00", "2025-01-01 16:00"));

        new Storage(file.toString()).save(tasks);

        List<Task> loaded = new Storage(file.toString()).load();
        assertEquals(3, loaded.size());
        assertEquals("read | book", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
        assertEquals(List.of("#fun"), loaded.get(0).getTags());
        assertEquals("2019-12-02", ((Deadline) loaded.get(1)).getByText());
        assertEquals("2025-01-01 1400", ((Event) loaded.get(2)).getFromText());
        assertTrue(Files.readAllLines(file).get(0).startsWith("3|T|1|"));
    }

    @Test
    public void load_invalidSavedDates_retainsValidTasksAndWarns() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "T | 0 | first task",
                "D | 0 | bad date | tomorrow",
                "D | 0 | impossible date | 31/2/2026",
                "E | 0 | reversed event | 2026-10-02 1600 | 2026-10-02 1400",
                "E | 0 | bad end | 2026-10-02 | tomorrow",
                "T | 0 | last task"));
        Storage storage = new Storage(file.toString());

        List<Task> tasks = storage.load();

        assertEquals(List.of("first task", "last task"), tasks.stream().map(Task::getDescription).toList());
        assertTrue(storage.getLoadWarning().contains("Skipped 4 invalid saved task record(s)"));
    }

    @Test
    public void load_unreadableFile_warnsAndProtectsExistingData() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        byte[] original = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, original);
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getLoadWarning().contains("saving is disabled"));
        assertThrows(AndersException.class, () -> storage.save(new TaskList()));
        assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    public void save_missingParent_createsFileAfterEmptyStartup() throws Exception {
        Path file = temporaryDirectory.resolve("new-data/tasks.txt");
        Storage storage = new Storage(file.toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getLoadWarning().isEmpty());

        storage.save(new TaskList(List.of(new Todo("new task"))));

        assertTrue(Files.isRegularFile(file));
        assertEquals("new task", new Storage(file.toString()).load().getFirst().getDescription());
    }

    @Test
    public void save_blockedParent_reportsUnsavedChangesAndCanRetry() throws Exception {
        Path parent = temporaryDirectory.resolve("data");
        Files.writeString(parent, "blocking file");
        Storage storage = new Storage(parent.resolve("tasks.txt").toString());
        TaskList tasks = new TaskList(List.of(new Todo("keep in memory")));

        AndersException exception = assertThrows(AndersException.class, () -> storage.save(tasks));

        assertTrue(exception.getMessage().contains("Changes are only available in this session"));
        assertEquals("blocking file", Files.readString(parent));
        Files.delete(parent);
        storage.save(tasks);
        assertEquals("keep in memory", storage.load().getFirst().getDescription());
    }

    @Test
    public void save_isoDeadlineTimes_preservesTimesTagsAndStatusAfterReload() throws Exception {
        Path file = temporaryDirectory.resolve("deadlines.txt");
        List<String> values = List.of("2026-10-02 1800", "2026-10-02 18:00",
                "2026-10-02 0000", "2026-10-02 00:00");
        TaskList tasks = new TaskList();
        for (String value : values) {
            Deadline deadline = new Deadline("submit report", value);
            deadline.addTags(List.of("#school"));
            deadline.markAsDone();
            tasks.add(deadline);
        }

        new Storage(file.toString()).save(tasks);

        Storage reopenedStorage = new Storage(file.toString());
        List<Task> loaded = reopenedStorage.load();
        assertEquals(values.size(), loaded.size());
        assertTrue(reopenedStorage.getLoadWarning().isEmpty());
        for (int i = 0; i < loaded.size(); i++) {
            Deadline deadline = assertInstanceOf(Deadline.class, loaded.get(i));
            int hour = i < 2 ? 18 : 0;
            assertEquals(LocalDateTime.of(2026, 10, 2, hour, 0), deadline.getBy());
            assertEquals(values.get(i), deadline.getByText());
            assertEquals(tasks.get(i).toString(), deadline.toString());
            assertEquals(List.of("#school"), deadline.getTags());
            assertTrue(deadline.isDone());
        }
    }

}
