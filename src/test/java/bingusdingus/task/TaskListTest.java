package bingusdingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests task-list mutation, persistence, and tolerant loading of malformed records. */
class TaskListTest {
    private static final Path STORAGE_PATH = Path.of(".", "data", "bingusdingus.txt");
    private byte[] originalStorage;
    private boolean wasStoragePresent;

    @BeforeEach
    void isolateStorage() throws IOException {
        wasStoragePresent = Files.exists(STORAGE_PATH);
        originalStorage = wasStoragePresent ? Files.readAllBytes(STORAGE_PATH) : null;
        Files.deleteIfExists(STORAGE_PATH);
    }

    @AfterEach
    void restoreStorage() throws IOException {
        if (wasStoragePresent) {
            Files.createDirectories(STORAGE_PATH.getParent());
            Files.write(STORAGE_PATH, originalStorage);
        } else {
            Files.deleteIfExists(STORAGE_PATH);
        }
    }

    @Test
    void taskList_addMarkRemoveAndSaveUpdatesTheList() throws IOException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));
        taskList.add(new Deadline("return book", "2019-06-06"));

        assertEquals(2, taskList.size());
        taskList.markAsDone(0);
        assertTrue(taskList.get(0).isDone());
        assertEquals("buy milk", taskList.remove(0).getDescription());
        assertEquals(1, taskList.size());
        assertEquals(List.of("D | 0 | return book | 2019-06-06T00:00"),
                Files.readAllLines(STORAGE_PATH, StandardCharsets.UTF_8));
    }

    @Test
    void taskList_insertAndSetDoneUpdateTheListAndSave() throws IOException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first task"));
        taskList.insert(0, new Todo("inserted task"));
        taskList.setDone(0, true);

        assertEquals("inserted task", taskList.get(0).getDescription());
        assertTrue(taskList.get(0).isDone());
        assertEquals(List.of("T | 1 | inserted task", "T | 0 | first task"),
                Files.readAllLines(STORAGE_PATH, StandardCharsets.UTF_8));
    }

    @Test
    void taskList_rejectsNullTasks() {
        TaskList taskList = new TaskList();
        assertThrows(IllegalArgumentException.class, () -> taskList.add(null));
        assertEquals(0, taskList.size());
    }

    @Test
    void taskList_rejectsDuplicateTasksRegardlessOfCompletionState() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));
        taskList.markAsDone(0);

        assertThrows(IllegalArgumentException.class, () -> taskList.add(new Todo("buy milk")));
        assertEquals(1, taskList.size());
    }

    @Test
    void taskList_findReturnsCaseInsensitiveDescriptionMatches() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));
        taskList.add(new Todo("read book"));
        taskList.add(new Deadline("return BOOK", "2019-06-06"));

        assertEquals(List.of(1, 2), taskList.findIndexes("book"));
    }

    @Test
    void taskList_loadsValidRecordsAndIgnoresMalformedRecords() throws IOException {
        Files.createDirectories(STORAGE_PATH.getParent());
        Files.write(STORAGE_PATH, List.of(
                "T | 1 | valid todo",
                "malformed record",
                "D | 0 | valid deadline | 2019-10-15T00:00",
                "E | 1 | valid event | 2019-10-15T09:00 | 2019-10-15T10:00",
                "X | 0 | unknown type",
                "T | 2 | invalid status"), StandardCharsets.UTF_8);

        TaskList taskList = new TaskList();

        assertEquals(3, taskList.size());
        assertTrue(taskList.get(0).isDone());
        assertEquals("[D][ ] valid deadline (by: Oct 15 2019)", taskList.get(1).toString());
        assertTrue(taskList.get(2).isDone());
        assertFalse(taskList.get(1).isDone());
    }
}
