package kibo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import kibo.exception.StorageException;
import kibo.support.StorageTestSupport;
import kibo.task.Deadline;
import kibo.task.Event;
import kibo.task.Task;
import kibo.task.TaskList;
import kibo.task.Todo;

/**
 * Tests persistence, compatibility, validation, and file-system failures using disposable data.
 */
public class StorageTest extends StorageTestSupport {
    @Test
    void load_missingFolderOrFile_returnsEmptyList() throws Exception {
        assertTrue(Storage.load().isEmpty());
        assertFalse(Files.exists(DATA_DIRECTORY));
        Files.createDirectory(DATA_DIRECTORY);
        assertTrue(Storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesDetailsOrderAndStatus() throws Exception {
        Todo todo = new Todo("阅读 book|notes");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 2, 29));
        Event datedEvent = new Event("meeting", "2024-02-29 14:00", "16:00");
        datedEvent.markAsDone();
        Event legacyEvent = new Event("legacy", "Mon 2pm", "4pm");
        TaskList original = new TaskList(List.of(todo, deadline, datedEvent, legacyEvent));

        Storage.save(original);
        List<Task> loaded = Storage.load();

        assertEquals(4, loaded.size());
        for (int index = 0; index < original.size(); index++) {
            assertEquals(original.get(index).getClass(), loaded.get(index).getClass());
            assertEquals(original.get(index).toString(), loaded.get(index).toString());
            assertEquals(original.get(index).isDone(), loaded.get(index).isDone());
        }
        assertTrue(loaded.get(2).isScheduledOn(LocalDate.of(2024, 2, 29)));
        assertFalse(loaded.get(3).isScheduledOn(LocalDate.of(2024, 2, 29)));
        assertFalse(Files.exists(TEMPORARY_PATH));
    }

    @Test
    void save_existingFileThenEmptyList_replacesInsteadOfAppending() throws Exception {
        writeFixture("T | 0 | old task\n");
        Storage.save(new TaskList(List.of(new Todo("new task"))));
        assertEquals(List.of("T | 0 | new task"), Files.readAllLines(SAVE_PATH));
        Storage.save(new TaskList());
        assertEquals("", Files.readString(SAVE_PATH));
        assertTrue(Storage.load().isEmpty());
    }

    @Test
    void load_blankLinesAndLegacyInvalidDate_preservesLegacyEvent() throws Exception {
        writeFixture("\n  \nT | 0 | read book\nE | 1 | old event | 2019-02-29 2pm | 4pm\n");
        List<Task> tasks = Storage.load();
        assertEquals(2, tasks.size());
        Event event = assertInstanceOf(Event.class, tasks.get(1));
        assertEquals("2019-02-29 2pm", event.getFrom());
        assertTrue(event.isDone());
        assertFalse(event.isScheduledOn(LocalDate.of(2019, 2, 28)));
    }

    @Test
    void load_malformedRecords_reportsPhysicalLineAndPreservesFile() throws Exception {
        String[] records = {
            "T | 0", "T | 2 | task", "T | 0 | ", "T | 0 | task | extra",
            "D | 0 | task", "D | 0 | task | ", "D | 0 | task | 2023-02-29",
            "E | 0 | task | start", "E | 0 | task |  | end", "E | 0 | task | start | ",
            "E | 0 | task | start | end | extra", "Q | 0 | task"
        };
        for (String record : records) {
            String fixture = "T | 1 | valid\n\n" + record + "\n";
            writeFixture(fixture);
            StorageException exception = assertThrows(StorageException.class, Storage::load, record);
            assertEquals("The saved task on line 3 has an invalid format.", exception.getMessage());
            assertEquals(fixture, Files.readString(SAVE_PATH));
        }
    }

    @Test
    void save_delimiterInAnyTaskField_rejectsWithoutReplacingFile() throws Exception {
        LocalDate date = LocalDate.of(2024, 2, 29);
        List<Task> invalidTasks = List.of(new Todo("a | b"), new Deadline("a | b", date),
                new Event("a | b", "start", "end"), new Event("task", "a | b", "end"),
                new Event("task", "start", "a | b"));
        writeFixture("T | 0 | keep me\n");
        for (Task invalid : invalidTasks) {
            assertThrows(StorageException.class, () -> Storage.save(new TaskList(List.of(invalid))));
            assertEquals("T | 0 | keep me\n", Files.readString(SAVE_PATH));
        }
    }

    @Test
    void load_savePathIsDirectory_reportsReadError() throws Exception {
        Files.createDirectories(SAVE_PATH);
        StorageException exception = assertThrows(StorageException.class, Storage::load);
        assertTrue(exception.getMessage().startsWith("I could not read your saved tasks from "));
    }

    @Test
    void save_dataPathIsFile_reportsWriteError() throws Exception {
        Files.writeString(DATA_DIRECTORY, "blocked");
        assertThrows(StorageException.class, () -> Storage.save(new TaskList()));
        assertEquals("blocked", Files.readString(DATA_DIRECTORY));
    }

    @Test
    void save_temporaryPathIsDirectory_preservesExistingFile() throws Exception {
        writeFixture("T | 0 | keep me\n");
        blockSaving();
        StorageException exception = assertThrows(StorageException.class, () -> Storage.save(new TaskList()));
        assertTrue(exception.getMessage().startsWith("I could not save your tasks to "));
        assertEquals("T | 0 | keep me\n", Files.readString(SAVE_PATH));
    }
}
