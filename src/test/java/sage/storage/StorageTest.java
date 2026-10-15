package sage.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sage.exception.SageException;
import sage.task.Deadline;
import sage.task.Event;
import sage.task.Task;
import sage.task.TaskType;
import sage.task.Todo;

class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void save_typeWithoutRequiredFields_detectsInternalInconsistency() {
        Storage storage = new Storage(tempDir.resolve("inconsistent.txt").toString());
        Task task = new Task("missing deadline fields", TaskType.DEADLINE);

        assertThrows(AssertionError.class, () -> storage.save(List.of(task)));
    }

    @Test
    void saveAndLoad_roundTripsTasks() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", "2025-02-14");
        Event event = new Event("team sync", "2025-02-14 09:00", "2025-02-14 10:30");

        storage.save(List.of(todo, deadline, event));
        List<String> lines = Files.readAllLines(file);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("T | 1 | read book"));
        assertTrue(lines.get(1).contains("D | 0 | submit report"));
        assertTrue(lines.get(2).contains("E | 0 | team sync"));

        List<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0) instanceof Todo);
        assertTrue(loaded.get(1) instanceof Deadline);
        assertTrue(loaded.get(2) instanceof Event);
        assertEquals("X", loaded.get(0).getStatusIcon());
    }

    @Test
    void load_withCorruptedTaskFile_reportsErrorAndPreventsOverwrite() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.write(file, List.of("bad | data"));

        Storage storage = new Storage(file.toString());
        assertThrows(SageException.class, storage::load);
        assertThrows(SageException.class, () -> storage.save(List.of(new Todo("replacement"))));
        assertEquals(List.of("bad | data"), Files.readAllLines(file));
    }

    @Test
    void load_invalidRecordAfterValidRecord_rejectsEntireFile() throws Exception {
        Path file = tempDir.resolve("partially-corrupt.txt");
        Files.write(file, List.of("T | 1 | read", "E | 0 | missing times"));

        assertThrows(SageException.class, () -> new Storage(file.toString()).load());
    }

    @Test
    void saveAndLoad_freeTextTimesAndBlankLines_preservesTasks() throws Exception {
        Path file = tempDir.resolve("free-text.txt");
        Storage storage = new Storage(file.toString());
        storage.save(List.of(new Deadline("submit", "Sunday"), new Event("meet", "Monday", "Tuesday")));
        List<String> records = Files.readAllLines(file);
        Files.write(file, List.of("", records.get(0), "  ", records.get(1)));

        List<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("[D][ ] submit (by: Sunday)", loaded.get(0).toString());
        assertEquals("[E][ ] meet (from: Monday to: Tuesday)", loaded.get(1).toString());
    }

    @Test
    void load_missingFile_hasNoFilesystemSideEffects() throws Exception {
        Path file = tempDir.resolve("new-folder/tasks.txt");
        assertTrue(new Storage(file.toString()).load().isEmpty());
        assertTrue(Files.notExists(file.getParent()));
    }

    @Test
    void load_legacyDuplicates_preservesRecordsButOversizedFileIsRejected() throws Exception {
        Path file = tempDir.resolve("invalid-list.txt");
        Files.write(file, List.of("T | 0 | read", "T | 1 | READ"));
        assertEquals(2, new Storage(file.toString()).load().size());
        List<String> tooMany = IntStream.rangeClosed(0, 100)
                .mapToObj(index -> "T | 0 | task " + index).toList();
        Files.write(file, tooMany);
        assertThrows(SageException.class, () -> new Storage(file.toString()).load());
        assertEquals(tooMany, Files.readAllLines(file));
    }

    @Test
    void save_existingDestinationDirectory_failsAndCleansTemporaryFile() throws Exception {
        Path directory = tempDir.resolve("tasks.txt");
        Files.createDirectory(directory);
        Files.writeString(directory.resolve("keep.txt"), "keep");
        Storage storage = new Storage(directory.toString());
        assertThrows(SageException.class, () -> storage.save(List.of(new Todo("read"))));
        assertEquals("keep", Files.readString(directory.resolve("keep.txt")));
        try (var files = Files.list(tempDir)) {
            assertEquals(List.of(directory), files.toList());
        }
    }

    @Test
    void load_invalidOrInaccessiblePath_reportsCheckedError() {
        for (String invalid : List.of("", "\u0000", tempDir.toString())) {
            Storage storage = new Storage(invalid);
            assertThrows(SageException.class, storage::load);
            assertThrows(SageException.class, () -> storage.save(List.of(new Todo("read"))));
        }
        assertThrows(SageException.class, new Storage(null)::load);
    }

    @Test
    void load_afterRepair_enablesSavingAgain() throws Exception {
        Path file = tempDir.resolve("repair.txt");
        Files.writeString(file, "broken");
        Storage storage = new Storage(file.toString());
        assertThrows(SageException.class, storage::load);
        Files.writeString(file, "T | 0 | repaired");
        assertEquals("repaired", storage.load().get(0).getDescription());
        storage.save(List.of(new Todo("new item")));
        assertEquals("new item", storage.load().get(0).getDescription());
    }

    @Test
    void save_serializationFailure_preservesExistingFileAndLeavesNoTemporaryFiles() throws Exception {
        Path file = tempDir.resolve("protected.txt");
        Storage storage = new Storage(file.toString());
        storage.save(List.of(new Todo("original task")));
        byte[] original = Files.readAllBytes(file);

        assertThrows(AssertionError.class,
                () -> storage.save(List.of(new Task("missing event fields", TaskType.EVENT))));

        assertArrayEquals(original, Files.readAllBytes(file));
        assertEquals("original task", storage.load().get(0).getDescription());
        try (var files = Files.list(tempDir)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    @Test
    void load_invalidUtf8_reportsErrorAndPreservesOriginalBytes() throws Exception {
        Path file = tempDir.resolve("invalid-encoding.txt");
        byte[] invalidBytes = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, invalidBytes);
        Storage storage = new Storage(file.toString());

        assertThrows(SageException.class, storage::load);
        assertThrows(SageException.class, () -> storage.save(List.of(new Todo("replacement"))));
        assertArrayEquals(invalidBytes, Files.readAllBytes(file));
    }

    @Test
    void save_lastItemRemoved_persistsAnEmptyListWithoutTemporaryFiles() throws Exception {
        Path file = tempDir.resolve("emptied.txt");
        Storage storage = new Storage(file.toString());
        storage.save(List.of(new Todo("finish")));
        storage.save(List.of());

        assertTrue(storage.load().isEmpty());
        assertEquals("", Files.readString(file));
        try (var files = Files.list(tempDir)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    @Test
    void save_explicitDateTimeObjects_roundTripsWithoutLosingTime() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 9, 18, 14, 30, 45);
        List<Task> tasks = List.of(new Deadline("submit", start), new Event("meeting", start, start.plusHours(1)));
        Storage storage = new Storage(tempDir.resolve("explicit-times.txt").toString());
        storage.save(tasks);

        List<Task> reloaded = storage.load();
        assertEquals(tasks.stream().map(Object::toString).toList(), reloaded.stream().map(Object::toString).toList());
        assertEquals(start, ((Deadline) reloaded.get(0)).getBy());
        assertEquals(start.plusHours(1), ((Event) reloaded.get(1)).getTo());
    }
}
