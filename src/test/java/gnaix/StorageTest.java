package gnaix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gnaix.task.Deadline;
import gnaix.task.Event;
import gnaix.task.Task;
import gnaix.task.Todo;

class StorageTest {

    @Test
    void load_missingFile_emptyTaskListReturned(@TempDir Path tempDir)
            throws Exception {
        Storage storage = new Storage(tempDir.resolve("tasks.txt"));

        ArrayList<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void load_emptyFile_emptyTaskListReturned(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(filePath, "", StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void load_blankLines_emptyTaskListReturned(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                System.lineSeparator()
                        + "   "
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void saveAndLoad_todoTask_taskPreserved(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("buy milk"));

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("buy milk", loadedTasks.get(0).getDescription());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertFalse(loadedTasks.get(0).isCompleted());
    }

    @Test
    void saveAndLoad_deadlineTask_datePreserved(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Deadline(
                "submit assignment",
                LocalDate.of(2026, 9, 1)));

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());

        Deadline deadline = assertInstanceOf(
                Deadline.class,
                loadedTasks.get(0));

        assertEquals("submit assignment", deadline.getDescription());
        assertEquals(
                LocalDate.of(2026, 9, 1),
                deadline.getDoBy());
    }

    @Test
    void saveAndLoad_eventTask_timingsPreserved(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 1, 14, 0),
                LocalDateTime.of(2026, 9, 1, 16, 0)));

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());

        Event event = assertInstanceOf(
                Event.class,
                loadedTasks.get(0));

        assertEquals("project meeting", event.getDescription());
        assertEquals(
                LocalDateTime.of(2026, 9, 1, 14, 0),
                event.getFrom());
        assertEquals(
                LocalDateTime.of(2026, 9, 1, 16, 0),
                event.getTo());
    }

    @Test
    void saveAndLoad_completedTask_completionStatusPreserved(
            @TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        Todo todo = new Todo("buy milk");
        todo.markAsComplete();

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(todo);

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0).isCompleted());
    }

    @Test
    void saveAndLoad_taggedTodo_tagsPreserved(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        Todo todo = new Todo("study CS2103T");
        todo.addTag("school");
        todo.addTag("urgent");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(todo);

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals(
                Set.of("school", "urgent"),
                loadedTasks.get(0).getTags());
    }

    @Test
    void saveAndLoad_taggedDeadline_tagsPreserved(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        Deadline deadline = new Deadline(
                "submit quiz",
                LocalDate.of(2026, 9, 10));
        deadline.addTag("school");
        deadline.addTag("urgent");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(deadline);

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        Deadline loadedDeadline = assertInstanceOf(
                Deadline.class,
                loadedTasks.get(0));

        assertEquals(
                Set.of("school", "urgent"),
                loadedDeadline.getTags());
        assertEquals(
                LocalDate.of(2026, 9, 10),
                loadedDeadline.getDoBy());
    }

    @Test
    void saveAndLoad_taggedEvent_tagsPreserved(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        Event event = new Event(
                "team meeting",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0));
        event.addTag("school");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(event);

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        Event loadedEvent = assertInstanceOf(
                Event.class,
                loadedTasks.get(0));

        assertEquals(Set.of("school"), loadedEvent.getTags());
        assertEquals(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                loadedEvent.getFrom());
        assertEquals(
                LocalDateTime.of(2026, 9, 15, 11, 0),
                loadedEvent.getTo());
    }

    @Test
    void load_oldUntaggedRecords_tasksPreserved(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");

        Files.writeString(
                filePath,
                "T | 0 | buy milk" + System.lineSeparator()
                        + "D | 1 | submit assignment | 2026-09-10"
                        + System.lineSeparator()
                        + "E | 0 | meeting | 2026-09-15T10:00 "
                        + "| 2026-09-15T11:00"
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);

        Storage storage = new Storage(filePath);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());

        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("buy milk", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).getTags().isEmpty());

        Deadline deadline = assertInstanceOf(
                Deadline.class,
                loadedTasks.get(1));
        assertEquals("submit assignment", deadline.getDescription());
        assertTrue(deadline.isCompleted());
        assertTrue(deadline.getTags().isEmpty());

        Event event = assertInstanceOf(
                Event.class,
                loadedTasks.get(2));
        assertEquals("meeting", event.getDescription());
        assertTrue(event.getTags().isEmpty());
    }

    @Test
    void save_taggedTask_tagFieldWritten(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        Todo todo = new Todo("study CS2103T");
        todo.addTag("school");
        todo.addTag("urgent");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(todo);

        storage.save(tasks);

        String stored = Files.readString(
                filePath,
                StandardCharsets.UTF_8);

        assertTrue(stored.contains(
                "T | 0 | study CS2103T | school,urgent"));
    }

    @Test
    void saveAndLoad_mixedTaskTypes_orderStateAndTagsPreserved(
            @TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        Todo todo = new Todo("buy milk");
        Deadline deadline = new Deadline(
                "submit quiz",
                LocalDate.of(2026, 9, 10));
        deadline.markAsComplete();
        Event event = new Event(
                "team meeting",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0));
        event.addTag("school");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertFalse(loadedTasks.get(0).isCompleted());

        Deadline loadedDeadline = assertInstanceOf(
                Deadline.class,
                loadedTasks.get(1));
        assertTrue(loadedDeadline.isCompleted());
        assertEquals(LocalDate.of(2026, 9, 10), loadedDeadline.getDoBy());

        Event loadedEvent = assertInstanceOf(
                Event.class,
                loadedTasks.get(2));
        assertEquals(Set.of("school"), loadedEvent.getTags());
        assertEquals(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                loadedEvent.getFrom());
    }

    @Test
    void load_unknownTaskType_recordIgnored(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "X | 0 | mystery task" + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_invalidCompletionStatus_recordIgnored(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "T | maybe | buy milk" + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_tooFewFields_recordIgnored(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "T | 0" + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_extraTodoField_recordIgnored(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "T | 0 | buy milk | school | extra"
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_missingDeadlineField_recordIgnored(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "D | 0 | submit quiz" + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_invalidDeadlineDate_recordIgnored(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "D | 0 | submit quiz | not-a-date" + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_missingEventField_recordIgnored(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "E | 0 | workshop | 2026-09-10T09:00"
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_invalidEventTime_recordIgnored(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "E | 0 | workshop | not-a-time | 2026-09-10T11:00"
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_corruptedRecord_validRecordsStillLoaded(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "T | 0 | first valid task" + System.lineSeparator()
                        + "X | 0 | corrupted task" + System.lineSeparator()
                        + "T | 1 | second valid task" + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("first valid task", loadedTasks.get(0).getDescription());
        assertFalse(loadedTasks.get(0).isCompleted());
        assertEquals("second valid task", loadedTasks.get(1).getDescription());
        assertTrue(loadedTasks.get(1).isCompleted());
    }

    @Test
    void load_duplicateStoredTags_duplicatesCollapsed(@TempDir Path tempDir)
            throws Exception {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "T | 0 | study CS2103T | school,School,urgent"
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals(
                Set.of("school", "urgent"),
                loadedTasks.get(0).getTags());
    }
}
