package gatsby.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gatsby.model.Deadline;
import gatsby.model.Event;
import gatsby.model.Task;
import gatsby.model.Todo;
import gatsby.testutil.TestSupport;

/** Tests persistence round trips and recovery from malformed save files. */
class StorageTest {
    private Path dataBackup;

    @BeforeEach
    void isolateDataDirectory() throws Exception {
        dataBackup = TestSupport.isolateDataDirectory();
    }

    @AfterEach
    void restoreDataDirectory() throws Exception {
        TestSupport.restoreDataDirectory(dataBackup);
    }

    /** Verifies that a first run with no save file starts with no tasks. */
    @Test
    void load_missingSaveFile_returnsEmptyList() {
        assertTrue(Storage.load().isEmpty());
    }

    /** Verifies that all task types and done states survive a save/load round trip. */
    @Test
    void saveThenLoad_allTaskTypes_preservesTasksAndStatuses() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));
        deadline.markDone();

        Storage.save(List.of(todo, deadline, event));
        ArrayList<Task> loaded = Storage.load();

        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("read book", loaded.get(0).getTaskName());
        assertFalse(loaded.get(0).isDone());
        assertEquals("return book", loaded.get(1).getTaskName());
        assertTrue(loaded.get(1).isDone());
        assertEquals("project meeting", loaded.get(2).getTaskName());
    }

    /** Verifies that saving an empty list creates an empty save file. */
    @Test
    void save_emptyList_createsEmptySaveFile() throws Exception {
        Storage.save(List.of());

        Path saveFile = Path.of("data", "gatsby.txt");
        assertTrue(Files.exists(saveFile));
        assertEquals("", Files.readString(saveFile));
    }

    /** Verifies that valid lines load while blank and malformed lines are skipped. */
    @Test
    void load_mixedValidAndMalformedLines_keepsValidTasksAndReportsSkippedLines() throws Exception {
        TestSupport.writeSaveFile("T | 0 | read book\n"
                + "\n"
                + "X | broken task\n"
                + "D | 1 | return book | not-a-date\n"
                + "E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00\n");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(output));

        ArrayList<Task> loaded;
        try {
            loaded = Storage.load();
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getTaskName());
        assertEquals("project meeting", loaded.get(1).getTaskName());
        assertTrue(output.toString().contains("skipped a line"));
        assertTrue(output.toString().contains("2 skipped line(s)"));
    }

    /** Verifies that an invalid event range in a hand-edited save file is skipped. */
    @Test
    void load_eventWithNonIncreasingTimes_skipsCorruptedLine() throws Exception {
        TestSupport.writeSaveFile("E | 0 | impossible meeting | 2019-12-02T16:00 | 2019-12-02T16:00\n"
                + "T | 0 | read book\n");

        ArrayList<Task> loaded = Storage.load();

        assertEquals(1, loaded.size());
        assertEquals("read book", loaded.get(0).getTaskName());
    }

    /** Verifies that duplicate task details in a save file do not become duplicate tasks. */
    @Test
    void load_duplicateTaskDetails_keepsFirstTaskOnly() throws Exception {
        TestSupport.writeSaveFile("T | 0 | read book\n"
                + "T | 1 | read book\n"
                + "D | 0 | read book | 2019-12-02T18:00\n");

        ArrayList<Task> loaded = Storage.load();

        assertEquals(2, loaded.size());
        assertFalse(loaded.get(0).isDone());
        assertInstanceOf(Deadline.class, loaded.get(1));
    }

    /** Verifies that a data path that is a file is reported without overwriting it. */
    @Test
    void save_dataPathIsFile_reportsErrorAndPreservesExistingFile() throws Exception {
        Path dataPath = Path.of("data");
        Files.writeString(dataPath, "do not overwrite");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(output));

        try {
            Storage.save(List.of(new Todo("read book")));
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("do not overwrite", Files.readString(dataPath));
        assertTrue(output.toString().contains("already exists as a file"));
    }
}
