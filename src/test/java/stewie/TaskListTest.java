package stewie;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import stewie.model.TaskList;
import stewie.storage.Storage;
import stewie.storage.StorageException;

/**
 * Verifies task-list mutations, searching, persistence, and preservation after rejected changes.
 */
public class TaskListTest {
    @TempDir
    private Path directory;
    private Path file;
    private TaskList tasks;

    /**
     * Creates isolated storage for each test so no test touches the user's agenda.
     */
    @BeforeEach
    public void setUp() {
        file = directory.resolve("stewie.txt");
        tasks = new TaskList(new Storage(file));
    }

    /**
     * Verifies creation order and an independent snapshot of the list.
     */
    @Test
    public void addTasks_preservesOrderAndReturnsIndependentSnapshot() {
        assertArrayEquals(new String[0], tasks.produceTaskList());
        addEveryType();
        String[] expected = {"[T] [ ] read book", "[D] [ ] report (by: 02 Jan 2026)",
            "[E] [ ] trip (from: 01 Jan 2026 to: 03 Jan 2026)"};
        assertArrayEquals(expected, tasks.produceTaskList());
        String[] snapshot = tasks.produceTaskList();
        snapshot[0] = "changed by caller";
        assertArrayEquals(expected, tasks.produceTaskList());
        assertPersisted(3);
    }

    /**
     * Verifies every task type retains its metadata through repeated status transitions.
     */
    @Test
    public void changeStatus_preservesEveryTaskType() {
        addEveryType();
        String[] original = tasks.produceTaskList();
        for (int index = 0; index < original.length; index++) {
            tasks.markAsDone(index);
            assertEquals(original[index].replace("[ ]", "[X]"), tasks.produceTaskList()[index]);
            assertPersisted(4 + index * 3);
            tasks.markAsDone(index);
            assertEquals(original[index].replace("[ ]", "[X]"), tasks.produceTaskList()[index]);
            tasks.markAsUndone(index);
            assertEquals(original[index], tasks.produceTaskList()[index]);
            assertPersisted(6 + index * 3);
        }
    }

    /**
     * Verifies deleting first, last, and final tasks persists the remaining order.
     */
    @Test
    public void deleteTask_handlesListBoundaries() {
        addEveryType();
        tasks.deleteTask(0);
        assertArrayEquals(new String[] {"[D] [ ] report (by: 02 Jan 2026)",
            "[E] [ ] trip (from: 01 Jan 2026 to: 03 Jan 2026)"}, tasks.produceTaskList());
        assertPersisted(4);
        tasks.deleteTask(1);
        assertArrayEquals(new String[] {"[D] [ ] report (by: 02 Jan 2026)"}, tasks.produceTaskList());
        tasks.deleteTask(0);
        assertArrayEquals(new String[0], tasks.produceTaskList());
        assertPersisted(6);
    }

    /**
     * Verifies description-only updates retain dates and completed status for all task types.
     */
    @Test
    public void updateDescription_preservesMetadataAndCompletion() {
        addEveryType();
        for (int index = 0; index < 3; index++) {
            tasks.markAsDone(index);
            tasks.updateTask(index, "revised " + index);
        }
        assertArrayEquals(new String[] {"[T] [X] revised 0", "[D] [X] revised 1 (by: 02 Jan 2026)",
            "[E] [X] revised 2 (from: 01 Jan 2026 to: 03 Jan 2026)"}, tasks.produceTaskList());
        assertPersisted(9);
    }

    /**
     * Verifies independent date updates and all-field updates preserve omitted values.
     */
    @Test
    public void updateDates_preservesOmittedFields() {
        addEveryType();
        tasks.updateTask(1, null, "2026-02-01", null, null);
        assertEquals("[D] [ ] report (by: 01 Feb 2026)", tasks.produceTaskList()[1]);
        tasks.updateTask(1, "final report", "2026-02-02", null, null);
        assertEquals("[D] [ ] final report (by: 02 Feb 2026)", tasks.produceTaskList()[1]);
        tasks.updateTask(2, null, null, "2026-01-02", null);
        assertEquals("[E] [ ] trip (from: 02 Jan 2026 to: 03 Jan 2026)", tasks.produceTaskList()[2]);
        tasks.updateTask(2, null, null, null, "2026-01-04");
        assertEquals("[E] [ ] trip (from: 02 Jan 2026 to: 04 Jan 2026)", tasks.produceTaskList()[2]);
        tasks.updateTask(2, "holiday", null, "2026-02-01", "2026-02-03");
        assertEquals("[E] [ ] holiday (from: 01 Feb 2026 to: 03 Feb 2026)", tasks.produceTaskList()[2]);
        tasks.updateTask(0, null, null, null, null);
        assertEquals("[T] [ ] read book", tasks.produceTaskList()[0]);
        assertPersisted(9);
    }

    /**
     * Verifies incompatible fields and invalid values cannot partially update any task.
     */
    @Test
    public void updateTask_rejectsIncompatibleAndInvalidFields() throws IOException {
        addEveryType();
        assertRejected(list -> list.updateTask(0, "new", "2026-01-01", null, null));
        assertRejected(list -> list.updateTask(0, "new", null, "2026-01-01", null));
        assertRejected(list -> list.updateTask(0, "new", null, null, "2026-01-01"));
        assertRejected(list -> list.updateTask(1, "new", null, "2026-01-01", null));
        assertRejected(list -> list.updateTask(1, "new", null, null, "2026-01-01"));
        assertRejected(list -> list.updateTask(2, "new", "2026-01-01", null, null));
        assertRejected(list -> list.updateTask(2, "new", null, "2026-01-03", null));
        assertRejected(list -> list.updateTask(2, "new", null, null, "2025-12-31"));
        assertRejected(list -> list.updateTask(1, "new", "2026-02-30", null, null));
        for (int index = 0; index < 3; index++) {
            int taskIndex = index;
            assertRejected(list -> list.updateTask(taskIndex, ""));
            assertRejected(list -> list.updateTask(taskIndex, "unsafe|description"));
        }
    }

    /**
     * Verifies duplicate details are rejected regardless of case, whitespace, or completion.
     */
    @Test
    public void addAndUpdate_rejectDuplicatesButAllowOwnDetails() throws IOException {
        addEveryType();
        tasks.markAsDone(0);
        assertRejected(list -> list.addToDo("READ  BOOK"));
        assertRejected(list -> list.addDeadline("REPORT", "2/1/2026"));
        assertRejected(list -> list.addEvent("TRIP", "1/1/2026", "3/1/2026"));
        tasks.addToDo("other");
        assertRejected(list -> list.updateTask(3, "read book"));
        tasks.addDeadline("other", "2026-01-01");
        assertRejected(list -> list.updateTask(4, "report", "2026-01-02", null, null));
        tasks.addEvent("other", "2026-01-02", "2026-01-04");
        assertRejected(list -> list.updateTask(5, "trip", null, "2026-01-01", "2026-01-03"));
        tasks.updateTask(0, "read book");
        assertEquals("[T] [X] read book", tasks.produceTaskList()[0]);
        assertPersisted(8);
    }

    /**
     * Verifies invalid indices do not save or change state for any numbered operation.
     */
    @Test
    public void numberedActions_rejectOutOfRangeIndices() throws IOException {
        addEveryType();
        String[] before = tasks.produceTaskList();
        byte[] saved = Files.readAllBytes(file);
        for (int index : new int[] {-1, 3, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsDone(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsUndone(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.deleteTask(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.updateTask(index, "new"));
            assertArrayEquals(before, tasks.produceTaskList());
            assertArrayEquals(saved, Files.readAllBytes(file));
            assertEquals(3, tasks.getRevision());
        }
    }

    /**
     * Verifies failed saves preserve metadata, completion, revision, and externally changed bytes.
     */
    @Test
    public void failedSave_preservesEveryTaskType() throws IOException {
        addEveryType();
        for (int index = 0; index < 3; index++) {
            tasks.markAsDone(index);
        }
        String[] before = tasks.produceTaskList();
        Files.writeString(file, "T | 0 | external");
        for (int index = 0; index < 3; index++) {
            int taskIndex = index;
            assertThrows(StorageException.class, () -> tasks.markAsUndone(taskIndex));
            assertThrows(StorageException.class, () -> tasks.markAsDone(taskIndex));
            assertThrows(StorageException.class, () -> tasks.updateTask(taskIndex, "changed"));
            assertThrows(StorageException.class, () -> tasks.deleteTask(taskIndex));
            assertArrayEquals(before, tasks.produceTaskList());
            assertEquals(6, tasks.getRevision());
            assertEquals("T | 0 | external", Files.readString(file));
        }
    }

    /**
     * Verifies search uses any keyword, includes metadata, preserves order, and avoids duplicate matches.
     */
    @Test
    public void findTasks_matchesAnyKeywordOnce() {
        assertArrayEquals(new String[0], tasks.findTasks("anything"));
        addEveryType();
        String[] all = tasks.produceTaskList();
        assertArrayEquals(new String[] {all[0], all[2]}, tasks.findTasks("book", "trip", "read", "book"));
        assertArrayEquals(new String[] {all[1], all[2]}, tasks.findTasks("Jan"));
        assertArrayEquals(new String[] {all[1]}, tasks.findTasks("[D]"));
        assertArrayEquals(new String[0], tasks.findTasks("absent"));
        assertArrayEquals(new String[0], tasks.findTasks());
        assertEquals(3, tasks.getRevision());
    }

    /**
     * Verifies shared model operations leave all console presentation to the caller.
     */
    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    public void mutations_doNotWriteConsoleOutput() {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            addEveryType();
            tasks.markAsDone(0);
            tasks.markAsUndone(0);
            tasks.updateTask(0, "revised book");
            tasks.deleteTask(0);
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsDone(99));
        } finally {
            System.setOut(original);
        }
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Seeds the three supported task types with distinct details.
     */
    private void addEveryType() {
        tasks.addToDo("read book");
        tasks.addDeadline("report", "2026-01-02");
        tasks.addEvent("trip", "2026-01-01", "2026-01-03");
    }

    /**
     * Checks successful mutations survive restart with the expected revision.
     */
    private void assertPersisted(long revision) {
        assertEquals(revision, tasks.getRevision());
        TaskList restored = new TaskList(new Storage(file));
        assertArrayEquals(tasks.produceTaskList(), restored.produceTaskList());
        assertEquals("", restored.getLoadWarning());
        assertEquals(0, restored.getRevision());
    }

    /**
     * Checks invalid mutations leave both persisted bytes and live state unchanged.
     */
    private void assertRejected(Consumer<TaskList> action) throws IOException {
        String[] before = tasks.produceTaskList();
        byte[] saved = Files.readAllBytes(file);
        long revision = tasks.getRevision();
        assertThrows(IllegalArgumentException.class, () -> action.accept(tasks));
        assertArrayEquals(before, tasks.produceTaskList());
        assertArrayEquals(saved, Files.readAllBytes(file));
        assertEquals(revision, tasks.getRevision());
    }
}
