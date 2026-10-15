package ev.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ev.EvException;
import ev.task.Deadline;
import ev.task.Event;
import ev.task.Task;
import ev.task.Todo;

public class StorageTest {

    @TempDir
    Path workFolder;

    private Path dataFile() {
        return workFolder.resolve("data").resolve("ev.txt");
    }

    private void writeDataFile(String... lines) throws IOException {
        Files.createDirectories(dataFile().getParent());
        Files.write(dataFile(), List.of(lines));
    }

    private ArrayList<Task> threeTasks() throws EvException {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Event("camp",
                LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0)));
        return tasks;
    }

    @Test
    public void load_missingFile_emptyListAndNoError() throws EvException {
        Storage storage = new Storage(dataFile());
        assertTrue(storage.load().isEmpty());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void save_missingFolder_folderCreated() throws EvException {
        assertFalse(Files.exists(dataFile().getParent()));
        new Storage(dataFile()).save(threeTasks());
        assertTrue(Files.exists(dataFile()));
    }

    @Test
    public void saveThenLoad_allTaskTypes_tasksUnchanged() throws EvException {
        Storage storage = new Storage(dataFile());
        storage.save(threeTasks());

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)", loaded.get(1).toString());
        assertEquals("[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)",
                loaded.get(2).toString());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void save_calledTwice_fileHoldsOnlyLatestList() throws EvException {
        Storage storage = new Storage(dataFile());
        storage.save(threeTasks());
        storage.save(new ArrayList<>(List.of(new Todo("only this one"))));

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("[T][ ] only this one", loaded.get(0).toString());
    }

    @Test
    public void save_emptyList_emptyFileThatLoadsBack() throws EvException {
        Storage storage = new Storage(dataFile());
        storage.save(new ArrayList<>());
        assertTrue(Files.exists(dataFile()));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_blankLines_ignoredWithoutCountingAsCorrupted() throws IOException, EvException {
        writeDataFile("T | 0 | read book", "", "   ", "T | 1 | water plants");
        Storage storage = new Storage(dataFile());
        assertEquals(2, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_corruptedLines_skippedAndCountedButRestKept() throws IOException, EvException {
        writeDataFile(
                "T | 1 | read book",
                "X | 0 | unknown type",
                "D | 2 | status is not a flag | 2019-12-02T18:00",
                "T | 0 |",
                "D | 0 | date is not readable | June 6th",
                "E | 0 | too few fields | 2019-12-01T09:00",
                "E | 0 | camp | 2019-12-01T09:00 | 2019-12-03T17:00");

        Storage storage = new Storage(dataFile());
        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)",
                loaded.get(1).toString());
        assertEquals(5, storage.getSkippedLineCount());
    }

    @Test
    public void load_extraSpacesAroundSeparators_stillReadable() throws IOException, EvException {
        writeDataFile("T|1|read book", "D  |  0  |  return book  |  2019-12-02T18:00");
        Storage storage = new Storage(dataFile());
        ArrayList<Task> loaded = storage.load();
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)", loaded.get(1).toString());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_calledTwice_skippedCountNotAccumulated() throws IOException, EvException {
        writeDataFile("T | 1 | read book", "X | 0 | unknown type");
        Storage storage = new Storage(dataFile());
        storage.load();
        storage.load();
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_folderInsteadOfFile_exceptionThrown() throws IOException {
        Files.createDirectories(dataFile());
        Storage storage = new Storage(dataFile());
        assertThrows(EvException.class, storage::load);
    }
}
