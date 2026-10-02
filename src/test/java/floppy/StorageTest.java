package floppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws FloppyException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveThenLoad_multipleTaskTypes_restoresAllFields() throws FloppyException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 1));
        Event event = new Event("camp", LocalDate.of(2026, 9, 2), LocalDate.of(2026, 9, 3));
        PeriodTask periodTask = new PeriodTask(
                "collect certificate", LocalDate.of(2026, 9, 4), LocalDate.of(2026, 9, 8));

        storage.save(List.of(todo, deadline, event, periodTask));
        List<Task> loadedTasks = storage.load();

        assertEquals(4, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals(LocalDate.of(2026, 9, 1),
                assertInstanceOf(Deadline.class, loadedTasks.get(1)).getDueDate());
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(LocalDate.of(2026, 9, 2), loadedEvent.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 3), loadedEvent.getEndDate());
        PeriodTask loadedPeriodTask = assertInstanceOf(PeriodTask.class, loadedTasks.get(3));
        assertEquals(LocalDate.of(2026, 9, 4), loadedPeriodTask.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 8), loadedPeriodTask.getEndDate());
    }

    @Test
    void load_corruptedRecord_throwsException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "D | maybe | incomplete");
        Storage storage = new Storage(dataFile.toString());

        assertThrows(FloppyException.class, storage::load);
    }

    @Test
    void load_unknownTaskType_throwsException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "Z | 0 | unknown");
        Storage storage = new Storage(dataFile.toString());

        assertThrows(FloppyException.class, storage::load);
    }

    @Test
    void load_invalidDate_throwsException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "D | 0 | submit report | 2026-02-30");
        Storage storage = new Storage(dataFile.toString());

        assertThrows(FloppyException.class, storage::load);
    }
}
