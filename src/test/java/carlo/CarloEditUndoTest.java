package carlo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import carlo.exception.CarloException;
import carlo.task.Deadline;
import carlo.task.Task;

/**
 * Tests for {@link Carlo#editTask(int, String)} and {@link Carlo#undo()}.
 *
 * <p>JUnit creates a fresh temporary folder for each test (the
 * {@code @TempDir} field), so these tests never touch the real save file.
 */
class CarloEditUndoTest {
    @TempDir
    Path tempDir;

    private Carlo carlo;

    @BeforeEach
    void setUp() {
        carlo = new Carlo(tempDir.resolve("carlo.txt").toString());
    }

    @Test
    void canUndo_freshStart_isFalse() {
        assertFalse(carlo.canUndo());
        assertThrows(CarloException.class, carlo::undo);
    }

    @Test
    void undo_afterAdd_removesTask() throws CarloException {
        carlo.addTaskFromCommand("todo read book");

        assertEquals("adding \"read book\"", carlo.undo());
        assertTrue(carlo.getTasks().isEmpty());
    }

    @Test
    void undo_afterDelete_restoresTaskInSamePlace() throws CarloException {
        carlo.addTaskFromCommand("todo first");
        carlo.addTaskFromCommand("deadline second /by 2026-10-15 1800");
        carlo.addTaskFromCommand("todo third");
        carlo.deleteTask(1);

        carlo.undo();

        assertEquals(3, carlo.getTasks().size());
        Task restored = carlo.getTasks().get(1);
        assertEquals("D | 0 | second | 2026-10-15 1800", restored.toFileFormat());
    }

    @Test
    void undo_afterMark_unmarksTask() throws CarloException {
        carlo.addTaskFromCommand("todo read");
        carlo.markTask(0);

        carlo.undo();

        assertFalse(carlo.getTasks().get(0).isDone());
    }

    @Test
    void undo_severalTimes_stepsBackInOrder() throws CarloException {
        carlo.addTaskFromCommand("todo a");
        carlo.addTaskFromCommand("todo b");

        assertEquals("adding \"b\"", carlo.undo());
        assertEquals("adding \"a\"", carlo.undo());
        assertFalse(carlo.canUndo());
    }

    @Test
    void undo_failedCommand_leavesNoSnapshot() {
        assertThrows(CarloException.class, () -> carlo.addTaskFromCommand("deadline /by 2026-10-15"));
        assertFalse(carlo.canUndo());
    }

    @Test
    void editTask_changesTypeAndKeepsPositionAndDoneState() throws CarloException {
        carlo.addTaskFromCommand("todo essay");
        carlo.addTaskFromCommand("todo other");
        carlo.markTask(0);

        Task edited = carlo.editTask(0, "deadline essay draft /by 2026-10-20");

        assertTrue(edited instanceof Deadline);
        assertTrue(edited.isDone());
        assertEquals(edited, carlo.getTasks().get(0));
        assertEquals("essay draft", carlo.getTasks().get(0).getDescription());
    }

    @Test
    void editTask_canBeUndone() throws CarloException {
        carlo.addTaskFromCommand("todo essay");
        carlo.editTask(0, "todo essay final");

        assertEquals("editing \"essay\"", carlo.undo());
        assertEquals("essay", carlo.getTasks().get(0).getDescription());
    }

    @Test
    void editTask_invalidIndex_throws() {
        assertThrows(CarloException.class, () -> carlo.editTask(0, "todo something"));
    }

    @Test
    void undo_isSavedToDisk() throws CarloException {
        carlo.addTaskFromCommand("todo keep me");
        carlo.deleteTask(0);
        carlo.undo();

        Carlo reloaded = new Carlo(tempDir.resolve("carlo.txt").toString());
        assertEquals("keep me", reloaded.getTasks().get(0).getDescription());
    }
}
