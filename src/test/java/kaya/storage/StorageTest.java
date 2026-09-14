package kaya.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kaya.task.Deadline;
import kaya.task.Event;
import kaya.task.Task;
import kaya.task.Todo;

/** Tests saving and loading tasks without touching Kaya's real data file. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoadTasks_multipleTaskTypes_restoresAllFields() throws IOException {
        Path file = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 1));
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 2),
                LocalDate.of(2026, 9, 3));
        deadline.markAsDone();

        storage.saveTasks(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertFalse(loadedTasks.get(0).isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), loadedDeadline.getBy());
        assertTrue(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals(LocalDate.of(2026, 9, 2), loadedEvent.getFrom());
        assertEquals(LocalDate.of(2026, 9, 3), loadedEvent.getTo());
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void loadTasks_corruptedRecord_skipsRecordAndLoadsValidTasks() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.saveTasks(List.of(new Todo("valid task")));
        Files.writeString(file, "corrupted record" + System.lineSeparator(),
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("valid task", loadedTasks.get(0).getDescription());
        assertEquals(1, storage.getSkippedRecordCount());
        String original = Files.readString(file);
        assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("replacement"))));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void saveTasks_emptyList_createsEmptyDataFile() throws IOException {
        Path file = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(file);

        storage.saveTasks(List.of());

        assertTrue(Files.exists(file));
        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void loadTasks_invalidStoredValues_skipsAndProtectsOriginalData() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.saveTasks(List.of(new Todo("valid task"), new Todo("   "),
                new Event("reversed event", LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 20))));
        Files.writeString(file, "T | 0 | _w==\n", StandardOpenOption.APPEND);
        String original = Files.readString(file);

        List<Task> loaded = storage.loadTasks();

        assertEquals(1, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
        assertEquals(3, storage.getSkippedRecordCount());
        assertThrows(IOException.class, () -> storage.saveTasks(loaded));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void saveTasks_repeatedSave_replacesFileWithoutLeavingTemporaryFiles() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.saveTasks(List.of(new Todo("before")));
        storage.saveTasks(List.of(new Todo("after")));

        assertEquals("after", storage.loadTasks().get(0).getDescription());
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    @Test
    public void saveTasks_readOnlyFile_preservesItsContents() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.saveTasks(List.of(new Todo("original")));
        assumeTrue(Files.getFileStore(file).supportsFileAttributeView("posix"));
        Set<PosixFilePermission> originalPermissions = Files.getPosixFilePermissions(file);
        String original = Files.readString(file);
        try {
            Files.setPosixFilePermissions(file, Set.of(PosixFilePermission.OWNER_READ));
            assumeFalse(Files.isWritable(file), "This account can override file permissions.");
            assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("replacement"))));
            assertEquals(original, Files.readString(file));
        } finally {
            Files.setPosixFilePermissions(file, originalPermissions);
        }
    }

    @Test
    public void loadTasks_symbolicLink_preservesLinkAndTarget() throws IOException {
        assumeTrue(Files.getFileStore(temporaryDirectory).supportsFileAttributeView("posix"));
        Path target = temporaryDirectory.resolve("original.txt");
        Files.writeString(target, "original data");
        Path link = temporaryDirectory.resolve("tasks.txt");
        Files.createSymbolicLink(link, target);
        Storage storage = new Storage(link);

        assertThrows(IOException.class, storage::loadTasks);
        assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("replacement"))));
        assertTrue(Files.isSymbolicLink(link));
        assertEquals("original data", Files.readString(target));
    }

    @Test
    public void saveAndLoadTasks_unicodeSeparatorsAndNewlines_preservesDescriptionExactly() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String description = "读书 | café /by notes =\nsecond line\twith a tab";
        Todo task = new Todo(description);
        task.markAsDone();
        Storage storage = new Storage(file);

        storage.saveTasks(List.of(task));
        List<Task> loaded = storage.loadTasks();

        assertEquals(1, loaded.size());
        assertEquals(description, loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
        assertEquals(0, storage.getSkippedRecordCount());
        assertEquals(1, Files.readAllLines(file).size());
    }

    @Test
    public void loadTasks_malformedRecordShapesAndDates_keepsValidNeighbouringRecords() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String valid = "T | 0 | dmFsaWQ=\n";
        String[] malformed = {
            "T | 2 | dGFzaw==", "X | 0 | dGFzaw==", "T | 0 | %%%",
            "T | 0 | dGFzaw== | extra", "D | 0 | dGFzaw==",
            "E | 0 | dGFzaw== | MjAyNi0wOS0yMA==",
            "D | 0 | dGFzaw== | MjAyNi0wMi0zMA==", "T | 0", ""
        };
        for (String record : malformed) {
            Files.writeString(file, valid + record + "\n" + valid);
            Storage storage = new Storage(file);
            List<Task> loaded = storage.loadTasks();

            assertEquals(2, loaded.size(), record);
            assertEquals(List.of("valid", "valid"), loaded.stream().map(Task::getDescription).toList(), record);
            assertEquals(1, storage.getSkippedRecordCount(), record);
        }
    }

    @Test
    public void loadTasks_invalidFileEncoding_blocksWritesAndPreservesOriginalBytes() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        byte[] invalidUtf8 = {(byte) 0xc3, 0x28};
        Files.write(file, invalidUtf8);
        Storage storage = new Storage(file);

        assertThrows(IOException.class, storage::loadTasks);
        assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("replacement"))));
        assertArrayEquals(invalidUtf8, Files.readAllBytes(file));
    }

    @Test
    public void loadTasks_repairedFile_resetsWarningCountAndAllowsSavingAgain() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "broken record\n");
        Storage storage = new Storage(file);
        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(1, storage.getSkippedRecordCount());

        Files.writeString(file, "T | 0 | cmVjb3ZlcmVk\n");
        assertEquals("recovered", storage.loadTasks().get(0).getDescription());
        assertEquals(0, storage.getSkippedRecordCount());
        storage.saveTasks(List.of(new Todo("saved after repair")));
        assertEquals("saved after repair", new Storage(file).loadTasks().get(0).getDescription());
    }
}
