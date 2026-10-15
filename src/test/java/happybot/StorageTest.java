package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import happybot.task.Deadline;
import happybot.task.Event;
import happybot.task.Task;
import happybot.task.ToDo;

public class StorageTest {
    // JUnit creates this directory before each test and deletes it afterwards, so no test
    // touches the real data file or is affected by what an earlier test wrote.
    @TempDir
    private Path temporaryDirectory;

    private Path dataFile() {
        return temporaryDirectory.resolve("HappyBot.txt");
    }

    // Writes the specified lines straight into the data file, standing in for a file saved
    // by an earlier run or edited by hand.
    private void writeDataFile(String... lines) throws IOException {
        Files.write(dataFile(), List.of(lines), StandardCharsets.UTF_8);
    }

    // ==================== saveTasks then loadTasks ====================

    @Test
    public void saveThenLoad_oneOfEachTaskType_tasksRestored() throws IOException {
        Storage storage = new Storage(dataFile());
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event(LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0), "orientation");
        storage.saveTasks(List.of(new ToDo("read book"), deadline, event));

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(ToDo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTasks.get(0).getDescription());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), loadedDeadline.getDueDateTime());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(LocalDateTime.of(2019, 12, 1, 9, 0), loadedEvent.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 17, 0), loadedEvent.getEndTime());
    }

    @Test
    public void saveThenLoad_completedTask_stillCompleted() throws IOException {
        Storage storage = new Storage(dataFile());
        Task doneTask = new ToDo("read book");
        doneTask.markAsDone(LocalDate.of(2026, 9, 16));
        storage.saveTasks(List.of(doneTask, new ToDo("return book")));

        List<Task> loadedTasks = storage.loadTasks();

        assertTrue(loadedTasks.get(0).isDone());
        assertEquals(LocalDate.of(2026, 9, 16), loadedTasks.get(0).getCompletionDate());
        assertFalse(loadedTasks.get(1).isDone());
        assertNull(loadedTasks.get(1).getCompletionDate());
    }

    @Test
    public void saveThenLoad_completedDatedTasks_completionDatesRestored() throws IOException {
        Storage storage = new Storage(dataFile());
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 9, 18, 18, 0));
        Event event = new Event(LocalDateTime.of(2026, 9, 19, 9, 0),
                LocalDateTime.of(2026, 9, 19, 10, 0), "meeting");
        deadline.markAsDone(LocalDate.of(2026, 9, 16));
        event.markAsDone(LocalDate.of(2026, 9, 17));

        storage.saveTasks(List.of(deadline, event));

        List<Task> loadedTasks = storage.loadTasks();
        assertEquals(LocalDate.of(2026, 9, 16), loadedTasks.get(0).getCompletionDate());
        assertEquals(LocalDate.of(2026, 9, 17), loadedTasks.get(1).getCompletionDate());
    }

    @Test
    public void saveTasks_calledTwice_previousContentsReplaced() throws IOException {
        Storage storage = new Storage(dataFile());
        storage.saveTasks(List.of(new ToDo("read book"), new ToDo("return book")));

        storage.saveTasks(List.of(new ToDo("pay fees")));

        List<Task> loadedTasks = storage.loadTasks();
        assertEquals(1, loadedTasks.size());
        assertEquals("pay fees", loadedTasks.get(0).getDescription());
    }

    @Test
    public void saveTasks_missingParentDirectory_directoryCreated() throws IOException {
        Path nestedFile = temporaryDirectory.resolve("data").resolve("HappyBot.txt");
        Storage storage = new Storage(nestedFile);

        storage.saveTasks(List.of(new ToDo("read book")));

        assertTrue(Files.exists(nestedFile));
    }

    @Test
    public void saveTasks_existingTemporaryFile_fileLeftUntouched() throws IOException {
        Path unrelatedTemporaryFile = temporaryDirectory.resolve("HappyBot.txt.tmp");
        Files.writeString(unrelatedTemporaryFile, "unrelated contents", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile());

        storage.saveTasks(List.of(new ToDo("read book")));

        assertEquals("unrelated contents",
                Files.readString(unrelatedTemporaryFile, StandardCharsets.UTF_8));
    }

    @Test
    public void saveTasks_noTasks_fileIsEmpty() throws IOException {
        Storage storage = new Storage(dataFile());

        storage.saveTasks(List.of());

        assertEquals(List.of(), Files.readAllLines(dataFile(), StandardCharsets.UTF_8));
    }

    @Test
    public void saveTasks_unsupportedTaskType_exceptionThrown() {
        // Only the three task types HappyBot creates have a storage format.
        Storage storage = new Storage(dataFile());
        List<Task> tasks = List.of(new Task("a plain task"));

        assertThrows(IllegalArgumentException.class, () -> storage.saveTasks(tasks));
    }

    @Test
    public void dataFileLock_secondStorageCannotAcquireUntilFirstReleases() throws IOException {
        Storage firstStorage = new Storage(dataFile());
        Storage secondStorage = new Storage(dataFile());

        assertTrue(firstStorage.tryAcquireDataFileLock());
        try {
            assertFalse(secondStorage.tryAcquireDataFileLock());

            firstStorage.releaseDataFileLock();
            assertTrue(secondStorage.tryAcquireDataFileLock());
        } finally {
            firstStorage.releaseDataFileLock();
            secondStorage.releaseDataFileLock();
        }
    }

    @Test
    public void dataFileLock_alreadyOwned_reacquisitionAndReleaseAreSafe() throws IOException {
        Storage storage = new Storage(dataFile());

        assertTrue(storage.tryAcquireDataFileLock());
        assertTrue(storage.tryAcquireDataFileLock());
        storage.releaseDataFileLock();
        storage.releaseDataFileLock();

        Storage nextStorage = new Storage(dataFile());
        assertTrue(nextStorage.tryAcquireDataFileLock());
        nextStorage.releaseDataFileLock();
    }

    @Test
    public void getFilePath_constructorPathSamePathReturned() {
        Path expectedFilePath = dataFile();

        assertEquals(expectedFilePath, new Storage(expectedFilePath).getFilePath());
    }

    // ==================== loadTasks on files written elsewhere ====================

    @Test
    public void loadTasks_fileDoesNotExist_noTasksAndNoError() throws IOException {
        Storage storage = new Storage(dataFile());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_pathIsDirectory_exceptionThrown() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("HappyBot.txt");
        Files.createDirectory(directoryPath);
        Storage storage = new Storage(directoryPath);

        assertThrows(IOException.class, storage::loadTasks);
    }

    @Test
    public void loadTasks_blankLines_linesIgnoredAndNotCounted() throws IOException {
        writeDataFile("T | 0 | read book", "", "   ", "T | 1 | return book");
        Storage storage = new Storage(dataFile());

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_everySupportedStoredType_typesAndDetailsRestored() throws IOException {
        writeDataFile("T | 0 | read book", "D | 1 | return book | 2019-12-02T18:00",
                "E | 0 | orientation | 2019-12-01T09:00 | 2019-12-03T17:00");
        Storage storage = new Storage(dataFile());

        List<Task> loadedTasks = storage.loadTasks();

        Task todo = assertInstanceOf(ToDo.class, loadedTasks.get(0));
        assertEquals("read book", todo.getDescription());
        Deadline deadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
        assertTrue(deadline.isDone());

        Event event = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("orientation", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 1, 9, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 17, 0), event.getEndTime());
    }

    @Test
    public void loadTasks_legacyCompletedTask_completionDateUnknown() throws IOException {
        writeDataFile("T | 1 | read book");
        Storage storage = new Storage(dataFile());

        List<Task> loadedTasks = storage.loadTasks();

        assertTrue(loadedTasks.get(0).isDone());
        assertNull(loadedTasks.get(0).getCompletionDate());
    }

    @Test
    public void saveTasks_legacyCompletedTask_extendedFormatUsesPlaceholder() throws IOException {
        Task legacyCompletedTask = new ToDo("read book");
        legacyCompletedTask.markAsDone();
        Storage storage = new Storage(dataFile());

        storage.saveTasks(List.of(legacyCompletedTask));

        assertEquals(List.of("T | 1 | read book | -"),
                Files.readAllLines(dataFile(), StandardCharsets.UTF_8));
    }

    @Test
    public void loadTasks_unknownTaskType_lineSkippedAndCounted() throws IOException {
        writeDataFile("T | 0 | read book", "X | 0 | mystery task");
        Storage storage = new Storage(dataFile());

        List<Task> loadedTasks = storage.loadTasks();

        // The readable tasks are kept rather than the whole file being abandoned.
        assertEquals(1, loadedTasks.size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_duplicateTask_lineSkippedAndCounted() throws IOException {
        writeDataFile("T | 0 | read book", "T | 1 | READ BOOK", "T | 0 | return book");
        Storage storage = new Storage(dataFile());

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertEquals("return book", loadedTasks.get(1).getDescription());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_wrongFieldCount_lineSkippedAndCounted() throws IOException {
        // A deadline needs four fields, so one carrying a todo's three cannot be read.
        writeDataFile("D | 0 | return book", "T | 0 | read book");
        Storage storage = new Storage(dataFile());

        assertEquals(1, storage.loadTasks().size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_tooFewFields_lineSkippedAndCounted() throws IOException {
        writeDataFile("T | 0");
        Storage storage = new Storage(dataFile());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_blankField_lineSkippedAndCounted() throws IOException {
        // The description is empty, which no saved task should have.
        writeDataFile("T | 0 |  ");
        Storage storage = new Storage(dataFile());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_unknownCompletionStatus_lineSkippedAndCounted() throws IOException {
        writeDataFile("T | yes | read book");
        Storage storage = new Storage(dataFile());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_unreadableDate_lineSkippedAndCounted() throws IOException {
        writeDataFile("D | 0 | return book | 2 Dec 2019");
        Storage storage = new Storage(dataFile());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_invalidCompletionDate_lineSkippedAndCounted() throws IOException {
        writeDataFile("T | 1 | read book | not-a-date");
        Storage storage = new Storage(dataFile());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_blankCompletionDate_lineSkippedAndCounted() throws IOException {
        writeDataFile("T | 1 | read book | ");
        Storage storage = new Storage(dataFile());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_uncompletedTaskWithCompletionDate_lineSkippedAndCounted() throws IOException {
        writeDataFile("T | 0 | read book | 2026-09-16");
        Storage storage = new Storage(dataFile());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_eventEndingAtOrBeforeStart_linesSkippedAndCounted() throws IOException {
        writeDataFile("E | 0 | meeting | 2019-12-01T09:00 | 2019-12-01T09:00",
                "E | 0 | review | 2019-12-02T09:00 | 2019-12-02T08:00",
                "E | 0 | lecture | 2019-12-03T09:00 | 2019-12-03T10:00");
        Storage storage = new Storage(dataFile());

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("lecture", loadedTasks.get(0).getDescription());
        assertEquals(2, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_severalUnreadableLines_allCounted() throws IOException {
        writeDataFile("X | 0 | mystery", "T | 0", "T | 0 | read book", "D | 0 | return book | soon");
        Storage storage = new Storage(dataFile());

        assertEquals(1, storage.loadTasks().size());
        assertEquals(3, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_calledAgain_skippedCountReset() throws IOException {
        writeDataFile("X | 0 | mystery");
        Storage storage = new Storage(dataFile());
        storage.loadTasks();

        writeDataFile("T | 0 | read book");
        storage.loadTasks();

        // The count describes the most recent load, not every load so far.
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void loadTasks_readableLinesAfterBadLine_stillLoaded() throws IOException {
        writeDataFile("X | 0 | mystery", "T | 0 | read book", "T | 1 | return book");
        Storage storage = new Storage(dataFile());

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(1).isDone());
    }

    @Test
    public void loadTasks_loadedList_canBeModified() throws IOException {
        // HappyBot hands the loaded list to a TaskList that keeps adding to it.
        writeDataFile("T | 0 | read book");
        Storage storage = new Storage(dataFile());

        List<Task> loadedTasks = storage.loadTasks();
        loadedTasks.add(new ToDo("return book"));

        assertEquals(2, loadedTasks.size());
    }
}
