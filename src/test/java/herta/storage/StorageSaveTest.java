package herta.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.exception.HertaException;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.TaskList;
import herta.task.Todo;

/**
 * Tests single-file saves, validation, external edits, and size limits.
 */
class StorageSaveTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveThenLoad_roundTripPreservesTasksAndStatuses() throws Exception {
        Path dataFile = temporaryDirectory.resolve("herta.txt");
        Todo todo = new Todo("buy milk");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("team meeting", LocalDateTime.of(2019, 12, 3, 10, 0),
                LocalDateTime.of(2019, 12, 3, 11, 0));
        TaskList originalTasks = new TaskList(List.of(todo, deadline, event));

        new Storage(dataFile.toString()).save(originalTasks);

        assertEquals(List.of(todo.toStorageString(), deadline.toStorageString(),
                event.toStorageString()), Files.readAllLines(dataFile));

        TaskList loadedTasks = new Storage(dataFile.toString()).load();
        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertTrue(loadedTodo.isCompleted());
        assertEquals(deadline.getBy(), loadedDeadline.getBy());
        assertEquals(event.getFrom(), loadedEvent.getFrom());
        assertEquals(event.getTo(), loadedEvent.getTo());
    }

    @Test
    void save_repeatedlyReplacesExistingFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("herta.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.save(new TaskList(List.of(new Todo("first task"))));
        storage.save(new TaskList(List.of(new Todo("second task"))));

        assertEquals(List.of("T | 0 | second task"), Files.readAllLines(dataFile));
    }

    @Test
    void save_afterExternalEdit_abortsWithoutOverwritingTheEditedFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("external-edit.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.save(new TaskList(List.of(new Todo("original"))));
        storage.load();
        Files.writeString(dataFile, "T | 0 | edited outside Herta\n");

        HertaException exception = assertThrows(HertaException.class, () ->
                storage.save(new TaskList(List.of(new Todo("new")))));

        assertTrue(exception.getMessage().contains("changed outside Herta"));
        assertEquals("T | 0 | edited outside Herta\n", Files.readString(dataFile));
    }

    @Test
    void save_afterSaveExternalEdit_abortsWithoutOverwritingEditedFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("external-edit-after-save.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.save(new TaskList(List.of(new Todo("original"))));
        Files.writeString(dataFile, "T | 0 | edited outside Herta\n");

        HertaException exception = assertThrows(HertaException.class, () ->
                storage.save(new TaskList(List.of(new Todo("new")))));

        assertTrue(exception.getMessage().contains("changed outside Herta"));
        assertEquals("T | 0 | edited outside Herta\n", Files.readString(dataFile));
    }

    @Test
    void save_taskWithStorageDelimiter_rejectsInvalidRecord() {
        Path dataFile = temporaryDirectory.resolve("invalid.txt");
        TaskList tasks = new TaskList(List.of(new MalformedStorageTask("valid task")));

        HertaException exception = assertThrows(HertaException.class, () ->
                new Storage(dataFile.toString()).save(tasks));

        assertEquals("Failed to save tasks: Invalid saved task: type T requires 3 fields.",
                exception.getMessage());
        assertTrue(Files.notExists(dataFile));
    }

    @Test
    void save_nullOrNullContainingTaskList_rejectsInvalidInput() {
        Storage storage = new Storage(temporaryDirectory.resolve("invalid.txt").toString());

        HertaException nullListException = assertThrows(HertaException.class, () ->
                storage.save(null));
        NullPointerException nullTaskException = assertThrows(NullPointerException.class, () ->
                storage.save(new TaskList(Collections.singletonList(null))));

        assertEquals("Failed to save tasks: task list is null.", nullListException.getMessage());
        assertEquals("A task list cannot contain a null task.", nullTaskException.getMessage());
    }

    @Test
    void save_lockHeldBySameJvm_preservesFileAndReportsUnattempted() throws Exception {
        Path dataFile = temporaryDirectory.resolve("locked.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.save(new TaskList(List.of(new Todo("original"))));
        byte[] originalBytes = Files.readAllBytes(dataFile);

        try (StorageFileLock storageLock = StorageFileLock.acquire(dataFile)) {
            HertaException exception = assertThrows(HertaException.class, () ->
                    storage.save(new TaskList(List.of(new Todo("replacement")))));

            assertEquals("Failed to save tasks: data file is locked.", exception.getMessage());
            assertEquals(PersistenceState.NOT_ATTEMPTED, storage.getLastPersistenceState());
            assertArrayEquals(originalBytes, Files.readAllBytes(dataFile));
        }

        storage.save(new TaskList(List.of(new Todo("replacement"))));
        assertEquals(PersistenceState.COMMITTED, storage.getLastPersistenceState());
    }

    @Test
    void saveAndLoad_exactMaximumSerializedSize_acceptsBoundaryAndRejectsOverLimit()
            throws Exception {
        Path dataFile = temporaryDirectory.resolve("size-limit.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList maximumTasks = StorageTestSupport.createTasksWithSerializedSize(
                StorageFileManager.MAX_STORAGE_FILE_BYTES);

        storage.save(maximumTasks);

        assertEquals(PersistenceState.COMMITTED, storage.getLastPersistenceState());
        assertEquals(StorageFileManager.MAX_STORAGE_FILE_BYTES, Files.size(dataFile));
        assertEquals(maximumTasks.size(), storage.load().size());
        byte[] maximumBytes = Files.readAllBytes(dataFile);
        HertaException exception = assertThrows(HertaException.class, () ->
                storage.save(StorageTestSupport.createTasksWithSerializedSize(
                        StorageFileManager.MAX_STORAGE_FILE_BYTES + 1)));

        assertEquals("Failed to save tasks: data file is too large.", exception.getMessage());
        assertArrayEquals(maximumBytes, Files.readAllBytes(dataFile));
        StorageTestSupport.assertNoTransactionFiles(temporaryDirectory);
    }

    /** Supplies an invalid serialized record without bypassing task construction validation. */
    private static final class MalformedStorageTask extends Todo {
        MalformedStorageTask(String description) {
            super(description);
        }

        @Override
        public String toStorageString() {
            return "T | 0 | contains | separator";
        }
    }
}
