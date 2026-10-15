package larry.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import larry.task.Deadline;
import larry.task.Event;
import larry.task.Task;
import larry.task.TaskList;
import larry.task.Todo;

/**
 * Tests persistence of Larry's task data.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadTasks_missingFile_emptyListReturned() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/larry.txt"));

        TaskList loadedTasks = storage.loadTasks();

        assertEquals(0, loadedTasks.size());
        assertEquals(List.of(), storage.getLoadWarnings());
    }

    @Test
    void saveAndLoadTasks_allTaskTypesAndEscapedText_roundTripPreserved() throws IOException {
        Path dataFile = temporaryDirectory.resolve("larry.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read | book \\ notes");
        Deadline deadline = new Deadline("return | book",
                LocalDateTime.of(2026, 6, 6, 18, 0, 30));
        Event event = new Event("meeting \\ team",
                LocalDateTime.of(2026, 8, 6, 14, 0, 15),
                LocalDateTime.of(2026, 8, 6, 16, 0, 45));
        todo.markAsDone();
        event.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.saveTasks(tasks);
        TaskList loadedTasks = storage.loadTasks();
        List<String> storedLines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);

        assertEquals(List.of(
                "T | 1 | read \\| book \\\\ notes",
                "D | 0 | return \\| book | 2026-06-06T18:00:30",
                "E | 1 | meeting \\\\ team | 2026-08-06T14:00:15 | 2026-08-06T16:00:45"
        ), storedLines);
        assertEquals(3, loadedTasks.size());
        assertLoadedTodo(loadedTasks.get(0));
        assertLoadedDeadline(loadedTasks.get(1));
        assertLoadedEvent(loadedTasks.get(2));
        assertEquals(List.of(), storage.getLoadWarnings());
    }

    @Test
    void loadTasks_mixedValidAndInvalidRecords_validTasksAndWarningsReturned() throws IOException {
        Path dataFile = temporaryDirectory.resolve("larry.txt");
        Files.writeString(dataFile, """
                ﻿T | 0 | valid todo

                X | 0 | unknown task
                T | 2 | invalid status
                D | 0 | missing date
                T | 0 | \s
                T | 1 | valid done todo
                """, StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        TaskList loadedTasks = storage.loadTasks();
        List<String> warnings = storage.getLoadWarnings();

        assertEquals(2, loadedTasks.size());
        assertEquals("valid todo", loadedTasks.get(0).getDescription());
        assertFalse(loadedTasks.get(0).isDone());
        assertEquals("valid done todo", loadedTasks.get(1).getDescription());
        assertTrue(loadedTasks.get(1).isDone());
        assertEquals(List.of(
                "Rejected line 3: unknown task type 'X'",
                "Rejected line 4: status must be 0 or 1",
                "Rejected line 5: "
                        + "wrong number of fields for task type D",
                "Rejected line 6: task details cannot be blank"
        ), warnings);
        assertThrows(UnsupportedOperationException.class, () ->
                warnings.add("unexpected warning"));
    }

    @Test
    void loadTasks_eventsWithInvalidOrdering_recordsSkippedWithLineWarnings() throws IOException {
        Path dataFile = temporaryDirectory.resolve("larry.txt");
        Files.writeString(dataFile, """
                E | 0 | reversed | 2026-08-06T16:00:00 | 2026-08-06T14:00:00
                T | 0 | valid task
                E | 1 | equal | 2026-08-06T14:00:00 | 2026-08-06T14:00:00
                """, StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        TaskList loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("valid task", loadedTasks.get(0).getDescription());
        assertEquals(List.of(
                "Rejected line 1: event end must be after its start",
                "Rejected line 3: event end must be after its start"
        ), storage.getLoadWarnings());
    }

    @Test
    void loadTasks_legacyEventDates_recordsRemainCompatible() throws IOException {
        Path dataFile = temporaryDirectory.resolve("larry.txt");
        Files.writeString(dataFile, """
                E | 0 | legacy dates | Monday morning | Monday afternoon
                E | 1 | legacy start | Friday evening | 2026-08-06T16:00:00
                E | 0 | legacy end | 2026-08-06T14:00:00 | after lunch
                """, StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        TaskList loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(1).isDone());
        assertEquals(List.of(), storage.getLoadWarnings());
    }

    @Test
    void loadTasks_invalidUtf8_saveBlockedUntilSuccessfulReload() throws IOException {
        Path dataFile = temporaryDirectory.resolve("larry.txt");
        byte[] invalidUtf8 = {'T', ' ', '|', ' ', '0', ' ', '|', ' ', (byte) 0xC3, 0x28};
        Files.write(dataFile, invalidUtf8);
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("safe task"));

        assertThrows(IOException.class, storage::loadTasks);
        assertThrows(IOException.class, () -> storage.saveTasks(tasks));

        Files.writeString(dataFile, "T | 0 | recovered task\n", StandardCharsets.UTF_8);
        TaskList recoveredTasks = storage.loadTasks();
        storage.saveTasks(recoveredTasks);

        assertEquals(1, recoveredTasks.size());
        assertEquals("recovered task", recoveredTasks.get(0).getDescription());
    }

    @Test
    void saveTasks_missingParentDirectories_directoriesAndFileCreated() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested/data/larry.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        storage.saveTasks(tasks);

        assertTrue(Files.isDirectory(dataFile.getParent()));
        assertTrue(Files.isRegularFile(dataFile));
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    private static void assertLoadedTodo(Task task) {
        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("read | book \\ notes", todo.getDescription());
        assertTrue(todo.isDone());
    }

    private static void assertLoadedDeadline(Task task) {
        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return | book", deadline.getDescription());
        assertFalse(deadline.isDone());
        assertEquals(Optional.of(LocalDateTime.of(2026, 6, 6, 18, 0, 30)),
                deadline.getDueDateTime().getValue());
    }

    private static void assertLoadedEvent(Task task) {
        Event event = assertInstanceOf(Event.class, task);
        assertEquals("meeting \\ team", event.getDescription());
        assertTrue(event.isDone());
        assertEquals(Optional.of(LocalDateTime.of(2026, 8, 6, 14, 0, 15)),
                event.getStartDateTime().getValue());
        assertEquals(Optional.of(LocalDateTime.of(2026, 8, 6, 16, 0, 45)),
                event.getEndDateTime().getValue());
    }
}
