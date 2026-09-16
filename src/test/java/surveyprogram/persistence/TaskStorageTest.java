package surveyprogram.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import surveyprogram.object.AfterTask;
import surveyprogram.object.Deadline;
import surveyprogram.object.Event;
import surveyprogram.object.TaskList;
import surveyprogram.object.Todo;

/** Tests persistence parsing independently from the application's real save file. */
class TaskStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_escapedDescriptions_preservesAllTypesAndRelationships() throws IOException {
        Path file = temporaryDirectory.resolve("escaped.txt");
        String description = "pipes || and \\| and %7C %25 and \\n";
        TaskList original = new TaskList(10);
        Todo parent = new Todo(description);
        original.restore(parent);
        original.restore(new Deadline(description, LocalDateTime.of(2026, 1, 1, 0, 0)));
        original.restore(new Event(description, LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 2, 0, 0)));
        original.restore(new AfterTask(description, parent));
        original.restore(new AfterTask(description, LocalDateTime.of(2026, 1, 1, 0, 0)));

        TaskStorage.save(original, file);
        TaskList restored = new TaskList(10);
        TaskStorage.load(restored, file);

        assertEquals(5, restored.getList().size());
        restored.getList().forEach(item -> assertEquals(description, item.getName()));
        AfterTask child = (AfterTask) restored.getList().get(3);
        assertEquals(restored.getList().getFirst(), child.getParent());
        assertTrue(Files.readString(file).contains("%7C"));
    }

    @Test
    void load_invalidValuesAndRelationships_skipsInvalidRecords() throws IOException {
        Path file = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(file, String.join("\n",
                "T | blank | n |   ",
                "T | | n | missing ID",
                "T | marker | maybe | invalid marker",
                "T | extra | n | description | extra",
                "E | equal | n | equal | 2026-01-01T00:00:00 | 2026-01-01T00:00:00",
                "E | reverse | n | reverse | 2026-01-02T00:00:00 | 2026-01-01T00:00:00",
                "D | date | n | invalid | 2026-02-30T00:00:00",
                "T | duplicate | n | first",
                "A | duplicate | n | second | TASK | parent",
                "A | ambiguous | n | child | TASK | duplicate",
                "A | inconsistent | y | completed child | TASK | parent",
                "A | future | y | completed early | TIME | 9999-01-01T00:00:00",
                "A | missing | n | orphan | TASK | nonexistent",
                "A | self | n | self | TASK | self",
                "A | cycle1 | n | cycle | TASK | cycle2",
                "A | cycle2 | n | cycle | TASK | cycle1",
                "A | validChild | n | valid child | TASK | parent",
                "T | parent | n | valid parent",
                "T | n | legacy %7C \\path"));
        TaskList restored = new TaskList(30);

        TaskStorage.load(restored, file);

        assertEquals(3, restored.getList().size());
        assertEquals("valid child", restored.getList().get(0).getName());
        assertEquals("valid parent", restored.getList().get(1).getName());
        assertEquals("legacy %7C \\path", restored.getList().get(2).getName());
    }

    @Test
    void save_serializationFails_preservesOriginalAndRemovesTemporaryFile() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "original contents");
        TaskList list = new TaskList(10);
        list.restore(new Todo("first record"));
        list.restore(new Deadline("invalid date", null));

        assertThrows(NullPointerException.class, () -> TaskStorage.save(list, file));

        assertEquals("original contents", Files.readString(file));
        try (var paths = Files.list(temporaryDirectory)) {
            assertEquals(1, paths.count());
        }
    }

    @Test
    void save_replacementFails_reportsFailureAndPreservesDestination() throws IOException {
        Path destination = Files.createDirectory(temporaryDirectory.resolve("tasks.txt"));
        Path existingFile = destination.resolve("keep.txt");
        Files.writeString(existingFile, "keep");

        assertThrows(IOException.class, () -> TaskStorage.save(new TaskList(10), destination));

        assertEquals("keep", Files.readString(existingFile));
        try (var paths = Files.list(temporaryDirectory)) {
            assertEquals(1, paths.count());
        }
    }

    @Test
    void save_existingFile_replacesItWithCompleteSnapshot() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "old contents that must disappear");
        TaskList original = new TaskList(10);
        original.restore(new Todo("replacement"));

        TaskStorage.save(original, file);

        TaskList restored = new TaskList(10);
        TaskStorage.load(restored, file);
        assertEquals(1, restored.getList().size());
        assertEquals("replacement", restored.getList().getFirst().getName());
    }

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
    void saveAndLoad_allIndependentTypes_preservesDetailsAndCompletion() throws IOException {
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
