package shan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

import shan.exception.DataFileException;
import shan.task.Deadline;
import shan.task.Event;
import shan.task.Task;
import shan.task.ToDo;

class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void load_missingDataFile_returnsEmptyResult() throws DataFileException {
        Storage storage = new Storage(this.tempDirectory.resolve("data/shan.txt"));

        Storage.LoadResult result = storage.load();

        assertTrue(result.tasks().isEmpty());
        assertEquals(0, result.skippedTasks());
    }

    @Test
    void save_taskList_createsParentDirectoryAndSerializesTasks()
            throws DataFileException, IOException {
        Path dataFile = this.tempDirectory.resolve("nested/data/shan.txt");
        Storage storage = new Storage(dataFile);
        ToDo toDo = new ToDo("read book");
        toDo.markDone();
        Deadline deadline = new Deadline(
                "return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0),
                LocalDateTime.of(2019, 12, 3, 16, 0));

        storage.save(List.of(toDo, deadline, event));

        assertTrue(Files.isDirectory(dataFile.getParent()));
        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-12-02 18:00",
                "E | 0 | project meeting | 2019-12-03 14:00 | 2019-12-03 16:00"),
                Files.readAllLines(dataFile));
    }

    @Test
    void load_validEntries_restoresTaskTypesAndCompletionState()
            throws IOException, DataFileException {
        Path dataFile = this.tempDirectory.resolve("shan.txt");
        Files.write(dataFile, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-12-02 18:00",
                "E | 0 | project meeting | 2019-12-03 14:00 | 2019-12-03 16:00"));
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.load();

        assertEquals(3, result.tasks().size());
        assertEquals(0, result.skippedTasks());
        assertInstanceOf(ToDo.class, result.tasks().get(0));
        assertInstanceOf(Deadline.class, result.tasks().get(1));
        assertInstanceOf(Event.class, result.tasks().get(2));
        assertTrue(result.tasks().get(0).isDone());
        assertFalse(result.tasks().get(1).isDone());
        assertFalse(result.tasks().get(2).isDone());
    }

    @Test
    void load_malformedEntries_loadsValidTasksAndCountsSkippedEntries()
            throws IOException, DataFileException {
        Path dataFile = this.tempDirectory.resolve("shan.txt");
        Files.write(dataFile, List.of(
                "",
                "T | 0 | valid task",
                "D | 0 | missing deadline",
                "X | 0 | unknown type",
                "T | 2 | invalid status",
                "E | 0 | backwards | 2019-12-03 16:00 | 2019-12-03 14:00"));
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.load();

        assertEquals(1, result.tasks().size());
        assertEquals("[T][ ] valid task", result.tasks().get(0).toString());
        assertEquals(4, result.skippedTasks());
    }

    @Test
    void load_dataPathIsDirectory_exceptionThrown() throws IOException {
        Path dataPath = this.tempDirectory.resolve("shan.txt");
        Files.createDirectory(dataPath);
        Storage storage = new Storage(dataPath);

        assertThrows(DataFileException.class, storage::load);
    }

    @Test
    void save_dataPathIsDirectory_exceptionThrown() throws IOException {
        Path dataPath = this.tempDirectory.resolve("shan.txt");
        Files.createDirectory(dataPath);
        Storage storage = new Storage(dataPath);
        List<Task> tasks = List.of(new ToDo("read book"));

        assertThrows(DataFileException.class, () -> storage.save(tasks));
    }

    @Test
    void save_emptyTaskList_createsEmptyDataFile() throws DataFileException, IOException {
        Path dataFile = this.tempDirectory.resolve("shan.txt");
        Storage storage = new Storage(dataFile);

        storage.save(List.of());

        assertTrue(Files.isRegularFile(dataFile));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    void getFilePath_pathContainsBackslashes_returnsForwardSlashes() {
        Storage storage = new Storage(Path.of("data\\nested\\shan.txt"));

        assertEquals("data/nested/shan.txt", storage.getFilePath());
    }

    @Test
    void loadResult_sourceListChanged_resultRemainsImmutable() {
        Task task = new ToDo("read book");
        ArrayList<Task> sourceTasks = new ArrayList<>(List.of(task));
        Storage.LoadResult result = new Storage.LoadResult(sourceTasks, 2);

        sourceTasks.clear();

        assertEquals(1, result.tasks().size());
        assertEquals(2, result.skippedTasks());
        assertThrows(
                UnsupportedOperationException.class, () -> result.tasks().add(new ToDo("write book")));
    }

}
