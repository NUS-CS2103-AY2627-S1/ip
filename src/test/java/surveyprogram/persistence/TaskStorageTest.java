package surveyprogram.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import surveyprogram.object.Deadline;
import surveyprogram.object.Event;
import surveyprogram.object.TaskList;
import surveyprogram.object.Todo;

/** Tests persistence parsing independently from the application's real save file. */
class TaskStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_leavesListEmpty() {
        TaskList list = new TaskList(10);

        TaskStorage.load(list, temporaryDirectory.resolve("missing.txt"));

        assertEquals(0, list.getList().size());
    }

    @Test
    void load_malformedAndUnknownRecords_skipsThemAndLoadsValidRecords() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "missing separators",
                "Q | n | unknown",
                "D | n | missing date",
                "D | n | invalid date | someday",
                "T | y | restored todo",
                "D | n | restored deadline | 2026-08-24T17:30:00"));
        TaskList list = new TaskList(10);

        TaskStorage.load(list, file);

        assertEquals(2, list.getList().size());
        assertInstanceOf(Todo.class, list.getList().get(0));
        assertInstanceOf(Deadline.class, list.getList().get(1));
        assertEquals("restored todo", list.getList().get(0).getName());
        assertFalse(list.getList().get(1).isDone());
    }

    @Test
    void save_emptyList_createsEmptyFileAndParentDirectories() throws IOException {
        Path file = temporaryDirectory.resolve("deep/data/tasks.txt");

        TaskStorage.save(new TaskList(10), file);

        assertEquals("", Files.readString(file));
    }

    @Test
    void saveAndLoad_allIndependentTypes_preservesDetailsAndCompletion() {
        Path file = temporaryDirectory.resolve("all-types.txt");
        TaskList original = new TaskList(10);
        original.restore(new Todo("todo"));
        original.restore(new Deadline("deadline", LocalDateTime.of(2026, 9, 10, 17, 30)));
        original.restore(new Event("event",
                LocalDateTime.of(2026, 9, 11, 9, 0),
                LocalDateTime.of(2026, 9, 11, 10, 0)));
        original.getList().get(1).setDone(true);

        TaskStorage.save(original, file);
        TaskList restored = new TaskList(10);
        TaskStorage.load(restored, file);

        assertEquals(3, restored.getList().size());
        assertInstanceOf(Todo.class, restored.getList().get(0));
        assertInstanceOf(Deadline.class, restored.getList().get(1));
        assertInstanceOf(Event.class, restored.getList().get(2));
        assertTrue(restored.getList().get(1).isDone());
        assertTrue(restored.getList().get(2).toString().contains("Sep 11 2026, 9:00 AM"));
    }

    @Test
    void load_legacyRecords_restoresSupportedTypesAndCompletion() throws IOException {
        Path file = temporaryDirectory.resolve("legacy.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "T | y | old todo",
                "D | n | old deadline | 2026-09-10T17:30:00",
                "E | n | old event | 2026-09-11T09:00:00 | 2026-09-11T10:00:00"));
        TaskList restored = new TaskList(10);

        TaskStorage.load(restored, file);

        assertEquals(3, restored.getList().size());
        assertTrue(restored.getList().get(0).isDone());
        assertInstanceOf(Deadline.class, restored.getList().get(1));
        assertInstanceOf(Event.class, restored.getList().get(2));
    }
}
