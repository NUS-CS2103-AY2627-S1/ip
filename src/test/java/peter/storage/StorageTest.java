package peter.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import peter.exception.PeterException;
import peter.task.Deadline;
import peter.task.Event;
import peter.task.Task;
import peter.task.TaskList;
import peter.task.Todo;

public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyResult() throws PeterException {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());

        Storage.LoadResult result = storage.load();

        assertEquals(0, result.tasks().size());
        assertEquals(0, result.skippedEntries());
    }

    @Test
    public void load_mixedValidAndDamagedEntries_recoversValidEntries() throws IOException, PeterException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | read book\n"
                + "D | maybe | damaged | 2026-09-10 1800\n"
                + "T | 0 | READ BOOK\n"
                + "not a task\n");
        Storage storage = new Storage(dataFile.toString());

        Storage.LoadResult result = storage.load();

        assertEquals(1, result.tasks().size());
        assertEquals(3, result.skippedEntries());
    }

    @Test
    public void save_validTasks_replacesDataFile() throws IOException, PeterException {
        Path dataFile = tempDir.resolve("nested/tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertEquals("T | 0 | read book" + System.lineSeparator(), Files.readString(dataFile));
    }

    @Test
    public void save_destinationIsDirectory_exceptionThrown() {
        Storage storage = new Storage(tempDir.toString());

        assertThrows(PeterException.class, () -> {
            storage.save(new TaskList());
        });
    }

    @Test
    public void load_blankLinesValidTypesAndCompletedStatus_restoresAllTasks()
            throws IOException, PeterException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT | 1 | read book\n"
                + "D | 0 | submit report | 2026-09-10 1800\n"
                + "E | 1 | meeting | 0900 | 1000\n\n");
        Storage storage = new Storage(dataFile.toString());

        Storage.LoadResult result = storage.load();

        assertEquals(3, result.tasks().size());
        assertEquals("[T][X] read book", result.tasks().get(0).toString());
        assertEquals("[D][ ] submit report (by: Sep 10 2026, 6:00 PM)", result.tasks().get(1).toString());
        assertEquals("[E][X] meeting (from: 0900 to: 1000)", result.tasks().get(2).toString());
    }

    @Test
    public void load_everyMalformedRecordShape_skipsAllEntries() throws IOException, PeterException {
        Path dataFile = tempDir.resolve("damaged.txt");
        Files.writeString(dataFile, "T | 0 |\n"
                + "T | 2 | invalid status\n"
                + "T | 0 | extra | field\n"
                + "D | 0 | missing date\n"
                + "D | 0 | invalid date | 2026-02-30 1200\n"
                + "E | 0 | missing end | 0900\n"
                + "E | 0 | backwards | 1000 | 0900\n"
                + "X | 0 | unknown type\n");
        Storage storage = new Storage(dataFile.toString());

        Storage.LoadResult result = storage.load();

        assertEquals(0, result.tasks().size());
        assertEquals(8, result.skippedEntries());
    }

    @Test
    public void saveAndReload_allTaskTypesAndStatuses_roundTrips() throws PeterException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(
                todo,
                new Deadline("submit report", "2026-09-10 1800"),
                new Event("meeting", "0900", "1000")));

        storage.save(tasks);
        Storage.LoadResult result = storage.load();

        assertEquals(tasks.getTasks().stream().map(Task::toString).toList(),
                result.tasks().stream().map(Task::toString).toList());
        assertEquals(0, result.skippedEntries());
    }

    @Test
    public void save_existingFile_replacesOldContents() throws IOException, PeterException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, "old content");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList(List.of(new Todo("new content")));

        storage.save(tasks);

        assertEquals("T | 0 | new content" + System.lineSeparator(), Files.readString(dataFile));
    }

    @Test
    public void load_nullBlankOrInvalidPath_exceptionThrown() {
        assertThrows(PeterException.class, () -> new Storage(null).load());
        assertThrows(PeterException.class, () -> new Storage("   ").load());
        assertThrows(PeterException.class, () -> new Storage("bad\0path").load());
    }

    @Test
    public void load_directoryInsteadOfFile_exceptionThrown() {
        assertThrows(PeterException.class, () -> new Storage(tempDir.toString()).load());
    }

    @Test
    public void saveAndLoad_unicodeDescription_preservesUtf8Text() throws PeterException {
        Path dataFile = tempDir.resolve("国际化.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList(List.of(new Todo("完成报告 🧭")));

        storage.save(tasks);
        Storage.LoadResult result = storage.load();

        assertEquals("完成报告 🧭", result.tasks().get(0).getDescription());
    }

    @Test
    public void load_malformedUtf8_exceptionThrown() throws IOException {
        Path dataFile = tempDir.resolve("invalid-utf8.txt");
        Files.write(dataFile, new byte[] {(byte) 0xC3, (byte) 0x28});

        assertThrows(PeterException.class, () -> new Storage(dataFile.toString()).load());
    }
}
