package bro.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import bro.exception.BroException;
import bro.task.Deadlines;
import bro.task.Events;
import bro.task.TaskList;
import bro.task.ToDos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies that Storage persists and restores typed task data. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Saving and loading preserves task order, completion, descriptions, and deadlines. */
    @Test
    void saveAndLoad_preservesTaskData() throws BroException {
        Path filePath = temporaryDirectory.resolve("data/tasks.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("read book"));
        tasks.add(new Deadlines(LocalDate.of(2019, 10, 15), "submit report"));
        tasks.getTask(0).setDone(true);

        storage.save(tasks);
        TaskList restoredTasks = storage.load();

        assertTrue(Files.exists(filePath));
        assertEquals(2, restoredTasks.size());
        assertEquals("[T] [X] read book", restoredTasks.getTask(0).toString());
        assertEquals("[D] [ ] submit report(by: Oct 15 2019)", restoredTasks.getTask(1).toString());
    }

    /** Saving an empty list creates an empty file rather than leaving stale task data behind. */
    @Test
    void save_emptyTaskList_createsEmptyFile() throws Exception {
        Path filePath = temporaryDirectory.resolve("empty/tasks.txt");

        new Storage(filePath.toString()).save(new TaskList());

        assertTrue(Files.exists(filePath));
        assertEquals("", Files.readString(filePath));
    }

    /** Loading a path that has not been created returns an empty task list. */
    @Test
    void load_missingFile_returnsEmptyTaskList() throws BroException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        TaskList tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    /** Saving and loading preserves event fields and a deadline time. */
    @Test
    void saveAndLoad_timedDeadlineAndEvent_preservesTypedData() throws BroException {
        Path filePath = temporaryDirectory.resolve("typed-tasks.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Deadlines(LocalDateTime.of(2019, 12, 2, 18, 0), "return book"));
        tasks.add(new Events("2pm", "4pm", "project meeting"));

        storage.save(tasks);
        TaskList restoredTasks = storage.load();

        assertEquals("[D] [ ] return book(by: Dec 2 2019 6:00PM)",
                restoredTasks.getTask(0).toString());
        assertEquals("[E] [ ] project meeting(from: 2pm to: 4pm)",
                restoredTasks.getTask(1).toString());
    }

    /** Every supported task subtype is written with its expected serialized fields and flags. */
    @Test
    void save_supportedTaskTypes_writesStableFileFormat() throws Exception {
        Path filePath = temporaryDirectory.resolve("serialized.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("read book"));
        tasks.add(new Deadlines(LocalDate.of(2019, 10, 15), "submit report"));
        tasks.add(new Events("2pm", "4pm", "project meeting"));
        tasks.getTask(0).setDone(true);

        storage.save(tasks);

        assertEquals(List.of(
                        "T | 1 | read book",
                        "D | 0 | submit report | 2019-10-15T00:00 | 0",
                        "E | 0 | project meeting | 2pm | 4pm"),
                Files.readAllLines(filePath));
    }

    /** Blank lines in the storage file are ignored while completion flags are restored. */
    @Test
    void load_blankLinesAndCompletionFlags_restoresOnlySavedTasks() throws Exception {
        Path filePath = temporaryDirectory.resolve("with-blanks.txt");
        Files.writeString(filePath, "\nT | 0 | first task\n  \nT | 1 | finished task\n");

        TaskList tasks = new Storage(filePath.toString()).load();

        assertEquals(2, tasks.size());
        assertFalse(tasks.getTask(0).isDone());
        assertTrue(tasks.getTask(1).isDone());
    }

    /** A malformed saved line is rejected instead of being silently dropped. */
    @Test
    void load_malformedTaskLine_throwsBroException() throws Exception {
        Path filePath = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(filePath, "X | 0 | unknown task");

        Storage storage = new Storage(filePath.toString());

        assertThrows(BroException.class, storage::load);
    }

    /** Invalid flags, types, field counts, dates, and deadline metadata are rejected uniformly. */
    @Test
    void load_invalidTaskFormats_throwsConsistentBroException() throws Exception {
        Path filePath = temporaryDirectory.resolve("invalid.txt");
        Storage storage = new Storage(filePath.toString());
        List<String> invalidLines = List.of(
                "T | 2 | invalid completion flag",
                "T | 0",
                "X | 0 | unsupported type",
                "E | 0 | event | 2pm",
                "D | 0 | deadline | 2019-10-15T00:00 | 2",
                "D | 0 | deadline | not-a-date | 0",
                "T | 0 | task | extra field");

        for (String invalidLine : invalidLines) {
            Files.writeString(filePath, invalidLine);

            BroException exception = assertThrows(BroException.class, storage::load);

            assertEquals("A saved task has an invalid format.", exception.getMessage());
        }
    }

    /** File-system failures are translated into the user-facing loading message. */
    @Test
    void load_directoryPath_throwsHelpfulIoMessage() {
        Storage storage = new Storage(temporaryDirectory.toString());

        BroException exception = assertThrows(BroException.class, storage::load);

        assertEquals("I couldn't load the saved task list.", exception.getMessage());
    }

    /** File-system failures while saving are translated into the user-facing saving message. */
    @Test
    void save_directoryPath_throwsHelpfulIoMessage() {
        Storage storage = new Storage(temporaryDirectory.toString());

        BroException exception = assertThrows(BroException.class,
                () -> storage.save(new TaskList()));

        assertEquals("I couldn't save the task list.", exception.getMessage());
    }
}
