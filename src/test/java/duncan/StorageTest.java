package duncan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duncan.task.Deadline;
import duncan.task.Event;
import duncan.task.Task;
import duncan.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_noSaveFileYet_emptyListReturned() {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_mixOfTaskTypesAndDoneStatuses_roundTripsCorrectly() throws DuncanException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());

        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        deadline.markAsDone();
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));

        ArrayList<Task> original = new ArrayList<>();
        original.add(todo);
        original.add(deadline);
        original.add(event);
        storage.save(original);

        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals(todo.toString(), loaded.get(0).toString());
        assertEquals(deadline.toString(), loaded.get(1).toString());
        assertEquals(event.toString(), loaded.get(2).toString());
    }

    @Test
    public void saveThenLoad_rescheduledAndSnoozedTasks_newDatesLoaded() throws DuncanException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        deadline.reschedule(LocalDate.of(2019, 12, 9));
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));
        event.snooze(7);

        ArrayList<Task> original = new ArrayList<>();
        original.add(deadline);
        original.add(event);
        storage.save(original);

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[D][ ] return book (by: Dec 9 2019)", loaded.get(0).toString());
        assertEquals("[E][ ] project fair (from: Dec 8 2019 to: Dec 9 2019)", loaded.get(1).toString());
    }

    @Test
    public void save_parentFolderDoesNotExistYet_folderCreatedAndFileSaved() throws DuncanException {
        Path savePath = tempDir.resolve("nested/dir/tasks.txt");
        Storage storage = new Storage(savePath.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertTrue(Files.exists(savePath));
    }

    @Test
    public void load_noSaveFileYet_noWarningsNoBackupAndSavingWorks() throws DuncanException {
        Path savePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(savePath.toString());

        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getLoadWarnings().isEmpty());
        assertFalse(Files.exists(tempDir.resolve("tasks.txt.bak")));

        storage.save(listOf(new Todo("read book")));
        assertTrue(Files.exists(savePath));
    }

    @Test
    public void load_badLines_skippedAndReportedByNumberWhileGoodLinesLoad() throws IOException {
        Path savePath = tempDir.resolve("tasks.txt");
        Files.writeString(savePath, String.join("\n",
                "T\t0\tgood todo",
                "garbage", // 2: too few fields
                "T\t1", // 3: no description
                "D\t0\tx", // 4: no date
                "D\t0\tx\t2019-02-30", // 5: date that does not exist
                "X\t0\tmystery", // 6: unknown type
                "T\tyes\tx", // 7: done flag not 0 or 1
                "T\t0\ta\textra", // 8: extra field
                "E\t0\tx\t2019-12-05\t2019-12-01", // 9: event ends before it starts
                "T\t0\t   ", // 10: blank description
                "   ", // 11: only spaces
                "D\t1\tgood deadline\t2019-12-02"));
        Storage storage = new Storage(savePath.toString());

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] good todo", loaded.get(0).toString());
        assertEquals("[D][X] good deadline (by: Dec 2 2019)", loaded.get(1).toString());
        assertEquals(List.of("WARNING: skipped bad lines in " + savePath + ": 2, 3, 4, 5, 6, 7, 8, 9, 10, 11",
                "The original file was backed up to " + savePath + ".bak"),
                storage.getLoadWarnings());
    }

    @Test
    public void load_badLine_originalCopiedOverOldBackup() throws IOException {
        Path savePath = tempDir.resolve("tasks.txt");
        Path backupPath = tempDir.resolve("tasks.txt.bak");
        String original = "T\t0\tread book\ngarbage\n";
        Files.writeString(savePath, original);
        Files.writeString(backupPath, "an older backup");

        new Storage(savePath.toString()).load();

        assertEquals(original, Files.readString(backupPath));
    }

    @Test
    public void load_notValidUtf8_warningShownAndEmptyListReturned() throws IOException {
        Path savePath = tempDir.resolve("tasks.txt");
        byte[] latin1Cafe = {'T', '\t', '0', '\t', 'c', 'a', 'f', (byte) 0xE9};
        Files.write(savePath, latin1Cafe);
        Storage storage = new Storage(savePath.toString());

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("WARNING: couldn't read " + savePath
                + " because it isn't valid UTF-8 text; starting with an empty task list.",
                "The original file was backed up to " + savePath + ".bak"),
                storage.getLoadWarnings());
    }

    @Test
    public void load_unreadableFile_savingTurnedOffAndFileLeftAlone() throws IOException {
        Path savePath = tempDir.resolve("tasks.txt");
        Files.writeString(savePath, "T\t0\tprecious\n");
        boolean isMadeUnreadable = savePath.toFile().setReadable(false) && !Files.isReadable(savePath);
        assumeTrue(isMadeUnreadable, "this OS or user can still read the file, so it cannot be made unreadable");
        Storage storage = new Storage(savePath.toString());

        try {
            assertTrue(storage.load().isEmpty());
            List<String> warnings = storage.getLoadWarnings();
            assertEquals(2, warnings.size());
            assertTrue(warnings.get(0).startsWith("WARNING: couldn't read " + savePath));
            assertTrue(warnings.get(1).endsWith("so saving is turned off for this session."));

            DuncanException e = assertThrows(DuncanException.class, () ->
                    storage.save(listOf(new Todo("new task"))));
            assertEquals("saving is turned off for this session", e.getMessage());
        } finally {
            savePath.toFile().setReadable(true);
        }
        assertEquals("T\t0\tprecious\n", Files.readString(savePath));
    }

    @Test
    public void save_pathIsNowAFolder_exceptionSaysFileCouldNotBeWritten() throws IOException {
        Path savePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(savePath.toString());
        storage.load();
        Files.createDirectory(savePath);

        DuncanException e = assertThrows(DuncanException.class, () ->
                storage.save(listOf(new Todo("read book"))));

        assertTrue(e.getMessage().startsWith("couldn't write to " + savePath + " ("));
    }

    private static ArrayList<Task> listOf(Task task) {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task);
        return tasks;
    }
}
