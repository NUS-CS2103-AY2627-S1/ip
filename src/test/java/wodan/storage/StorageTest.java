package wodan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wodan.WodanException;
import wodan.task.Deadline;
import wodan.task.Event;
import wodan.task.Task;
import wodan.task.TaskDateTime;
import wodan.task.Todo;

/**
 * Unit tests for {@link Storage} load and save behavior.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_emptyListWithoutWarning() throws WodanException {
        Path file = tempDir.resolve("missing.txt");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void saveThenLoad_roundTrip_preservesTodoAndDeadline() throws Exception {
        Path file = tempDir.resolve("nested").resolve("wodan.txt");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> original = new ArrayList<>();
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", TaskDateTime.parse("2019-10-15")));
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("[T][X] borrow book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("general", loaded.get(0).getCategory());
        assertEquals("general", loaded.get(1).getCategory());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void load_oldTodoWithoutTag_defaultsToGeneral() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file, "T | 0 | borrow book\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("general", loaded.get(0).getCategory());
        assertTrue(loaded.get(0).isGeneral());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void load_todoWithTag_keepsCategory() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file, "T | 0 | borrow book | school\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("school", loaded.get(0).getCategory());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void load_deadlineAndEventWithOptionalTag_oldAndNewFormats() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file,
                "D | 0 | return book | 2019-10-15\n"
                        + "D | 0 | homework | 2019-10-15 | school\n"
                        + "E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00\n"
                        + "E | 0 | camp | 2019-12-02T14:00 | 2019-12-02T16:00 | work\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(4, loaded.size());
        assertEquals("general", loaded.get(0).getCategory());
        assertEquals("school", loaded.get(1).getCategory());
        assertEquals("general", loaded.get(2).getCategory());
        assertEquals("work", loaded.get(3).getCategory());
        assertInstanceOf(Deadline.class, loaded.get(0));
        assertInstanceOf(Event.class, loaded.get(2));
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void load_corruptLines_skipsThemAndWarns() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file,
                "T | 1 | keep me\nthis is garbage\nX | 0 | unknown\nT | 1 | also keep\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("keep me", loaded.get(0).getDescription());
        assertEquals("also keep", loaded.get(1).getDescription());
        assertEquals("The ravens skipped 2 corrupted quests in the save file.",
                storage.getLoadWarning());
    }

    @Test
    public void load_unreadableFile_exceptionThrown() throws Exception {
        Assumptions.assumeTrue(
                tempDir.getFileSystem().supportedFileAttributeViews().contains("posix"));
        Path file = tempDir.resolve("locked.txt");
        Files.writeString(file, "T | 0 | keep\n", StandardCharsets.UTF_8);
        Files.setPosixFilePermissions(file, Set.of());
        try {
            Storage storage = new Storage(file.toString());
            WodanException ex = assertThrows(WodanException.class, storage::load);
            assertEquals(
                    "The ravens were denied access to the save file. "
                            + "They could not recall the quests.",
                    ex.getMessage());
        } finally {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
        }
    }

    @Test
    public void load_pathIsDirectory_exceptionThrown() throws Exception {
        Path dir = tempDir.resolve("not-a-file");
        Files.createDirectory(dir);
        Storage storage = new Storage(dir.toString());
        WodanException ex = assertThrows(WodanException.class, storage::load);
        assertEquals("The save path is not a file. The ravens could not recall the quests.",
                ex.getMessage());
    }

    @Test
    public void load_emptyFile_emptyListWithoutWarning() throws Exception {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "", StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        assertTrue(storage.load().isEmpty());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void load_bomAndBlankLines_readsTodo() throws Exception {
        Path file = tempDir.resolve("bom.txt");
        Files.writeString(file, "\uFEFFT | 0 | borrow book | general\n\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("borrow book", loaded.get(0).getDescription());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void load_oneCorruptLine_singularWarning() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file, "T | 0 | keep | general\ngarbage\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("The ravens skipped 1 corrupted quest in the save file.",
                storage.getLoadWarning());
        storage.load();
        assertEquals("The ravens skipped 1 corrupted quest in the save file.",
                storage.getLoadWarning());
    }

    @Test
    public void load_invalidDeadlineEventAndTodoShapes_skipped() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file,
                "D | 0 | return book | not-a-date\n"
                        + "D | 0 | missing-date |\n"
                        + "E | 0 | backwards | 2019-12-02T16:00 | 2019-12-02T14:00\n"
                        + "E | 0 | same-time | 2019-12-02T14:00 | 2019-12-02T14:00\n"
                        + "T | 0 |\n"
                        + "T | 0 | extra | general | too-many\n"
                        + "T | 0 | keep | general\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("keep", loaded.get(0).getDescription());
        assertEquals("The ravens skipped 6 corrupted quests in the save file.",
                storage.getLoadWarning());
    }

    @Test
    public void saveThenLoad_eventRoundTrip_preservesTimes() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> original = new ArrayList<>();
        original.add(new Event("meeting",
                TaskDateTime.parse("2019-12-02 1400"),
                TaskDateTime.parse("2019-12-02 1600")));
        storage.save(original);
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals(
                "[E][ ] meeting (from: Dec 02 2019, 2:00pm to: Dec 02 2019, 4:00pm)",
                loaded.get(0).toString());
    }

    @Test
    public void save_pathIsDirectory_exceptionThrown() throws Exception {
        Path dir = tempDir.resolve("folder");
        Files.createDirectory(dir);
        Storage storage = new Storage(dir.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("borrow book"));
        WodanException ex = assertThrows(WodanException.class, () -> storage.save(tasks));
        assertEquals("The save path is not a file. The ravens could not record the quests.",
                ex.getMessage());
    }

    @Test
    public void save_unwritableFile_exceptionThrown() throws Exception {
        Assumptions.assumeTrue(
                tempDir.getFileSystem().supportedFileAttributeViews().contains("posix"));
        Path file = tempDir.resolve("locked-save.txt");
        Files.writeString(file, "T | 0 | keep | general\n", StandardCharsets.UTF_8);
        Files.setPosixFilePermissions(file, Set.of());
        try {
            Storage storage = new Storage(file.toString());
            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(new Todo("borrow book"));
            WodanException ex = assertThrows(WodanException.class, () -> storage.save(tasks));
            assertEquals(
                    "The ravens were denied access to the save file. "
                            + "They could not record the quests.",
                    ex.getMessage());
        } finally {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
        }
    }

    @Test
    public void load_invalidDoneFlagAndTooFewFields_skipped() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file,
                "T | 2 | book | general\n"
                        + "T | 0\n"
                        + "T | 0 | keep | general\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("keep", loaded.get(0).getDescription());
        assertEquals("The ravens skipped 2 corrupted quests in the save file.",
                storage.getLoadWarning());
    }

    @Test
    public void load_emptyCategoryAndMarkedEvent_defaultsAndDone() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file,
                "T | 0 | book |\n"
                        + "E | 1 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00 | general\n"
                        + "E | 0 | camp | 2019-12-02 | 2019-12-02\n"
                        + "D | 0 | homework | 2019-10-15 | school | extra\n"
                        + "E | 0 | broken | no-date | 2019-12-02T16:00\n"
                        + "E | 0 | missing-end | 2019-12-02T14:00 |\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("general", loaded.get(0).getCategory());
        assertTrue(loaded.get(1).isDone());
        assertEquals("[E][X] meeting (from: Dec 02 2019, 2:00pm to: Dec 02 2019, 4:00pm)",
                loaded.get(1).toString());
        assertEquals("[E][ ] camp (from: Dec 02 2019 to: Dec 02 2019)", loaded.get(2).toString());
        assertEquals("The ravens skipped 3 corrupted quests in the save file.",
                storage.getLoadWarning());
    }

    @Test
    public void save_parentPathIsFile_exceptionThrown() throws Exception {
        Path parent = tempDir.resolve("not-a-folder");
        Files.writeString(parent, "not a directory", StandardCharsets.UTF_8);
        Storage storage = new Storage(parent.resolve("wodan.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("borrow book"));
        WodanException ex = assertThrows(WodanException.class, () -> storage.save(tasks));
        assertEquals("The ravens could not record the quests.", ex.getMessage());
    }
}
