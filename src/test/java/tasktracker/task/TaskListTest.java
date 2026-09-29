package tasktracker.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasktracker.exception.TaskTrackerException;
import tasktracker.ui.Message;

public class TaskListTest {

    private TaskList taskList;
    private ToDo sampleTodo;
    private Fixed sampleFixedTask;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList(new ArrayList<>());
        sampleTodo = new ToDo("read documentation");
        sampleFixedTask = new Fixed("gym workout", "1 hour");
    }

    // =========================================================================
    // Add & Capacity Tests
    // =========================================================================

    @Test
    public void add_validTasks_increasesSizeAndContainsTasks() throws TaskTrackerException {
        taskList.add(sampleTodo);
        assertEquals(1, taskList.getTasks().size());
        assertEquals(sampleTodo, taskList.getTasks().get(0));

        taskList.add(sampleFixedTask);
        assertEquals(2, taskList.getTasks().size());
        assertEquals(sampleFixedTask, taskList.getTasks().get(1));
    }

    @Test
    public void add_exceedsCapacity_throwsException() {
        for (int i = 0; i < 100; i++) {
            try {
                taskList.add(new ToDo("Task " + i));
            } catch (TaskTrackerException e) {
                // Should not throw during setup
            }
        }

        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            taskList.add(new ToDo("Task 101"));
        });
        assertEquals(Message.ERR_TASK_LIST_FULL, ex.getMessage());
    }

    // =========================================================================
    // Delete & Boundary Tests
    // =========================================================================

    @Test
    public void deleteTask_validOneBasedIndex_removesTaskAndDecrementsSize() throws TaskTrackerException {
        taskList.add(sampleTodo);
        taskList.add(sampleFixedTask);

        String resultMessage = taskList.deleteTask(1);
        assertTrue(resultMessage.contains(Message.MSG_TASK_REMOVED));
        assertEquals(1, taskList.getTasks().size());
        assertEquals(sampleFixedTask, taskList.getTasks().get(0));
    }

    @Test
    public void deleteTask_emptyList_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            taskList.deleteTask(1);
        });
        assertEquals(Message.getErrOutOfBounds(0), ex.getMessage());
    }

    @Test
    public void deleteTask_invalidIndex_throwsException() throws TaskTrackerException {
        taskList.add(sampleTodo);

        // Negative index
        assertThrows(TaskTrackerException.class, () -> {
            taskList.deleteTask(-1);
        });

        // Zero index (1-based system)
        assertThrows(TaskTrackerException.class, () -> {
            taskList.deleteTask(0);
        });

        // Out of bounds index
        assertThrows(TaskTrackerException.class, () -> {
            taskList.deleteTask(2);
        });
    }

    // =========================================================================
    // Undo Restoration & State Tests
    // =========================================================================

    @Test
    public void undoDelete_noHistory_throwsException() {
        assertFalse(taskList.hasDeletedTaskToUndo());
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            taskList.undoDelete();
        });
        assertEquals(Message.ERR_NO_UNDO_TASK, ex.getMessage());
    }

    @Test
    public void undoDelete_deleteMiddleTask_restoresToExactIndex() throws TaskTrackerException {
        ToDo taskC = new ToDo("sleep 8 hours");
        taskList.add(sampleTodo); // index 0
        taskList.add(sampleFixedTask); // index 1
        taskList.add(taskC); // index 2

        taskList.deleteTask(2); // deletes sampleFixedTask
        assertEquals(2, taskList.getTasks().size());
        assertEquals(sampleTodo, taskList.getTasks().get(0));
        assertEquals(taskC, taskList.getTasks().get(1));

        String undoMsg = taskList.undoDelete();
        assertTrue(undoMsg.contains(Message.MSG_TASK_RESTORED));
        assertEquals(3, taskList.getTasks().size());
        assertEquals(sampleFixedTask, taskList.getTasks().get(1)); // verified restored to index 1
    }

    @Test
    public void undoDelete_deleteFirstTask_restoresToFront() throws TaskTrackerException {
        taskList.add(sampleTodo);
        taskList.add(sampleFixedTask);

        taskList.deleteTask(1); // deletes sampleTodo
        assertEquals(sampleFixedTask, taskList.getTasks().get(0));

        taskList.undoDelete();
        assertEquals(2, taskList.getTasks().size());
        assertEquals(sampleTodo, taskList.getTasks().get(0));
    }

    @Test
    public void undoDelete_deleteLastTask_restoresToEnd() throws TaskTrackerException {
        taskList.add(sampleTodo);
        taskList.add(sampleFixedTask);

        taskList.deleteTask(2); // deletes sampleFixedTask
        assertEquals(1, taskList.getTasks().size());

        taskList.undoDelete();
        assertEquals(2, taskList.getTasks().size());
        assertEquals(sampleFixedTask, taskList.getTasks().get(1));
    }

    @Test
    public void undoDelete_consecutiveCalls_throwsExceptionOnSecondCall() throws TaskTrackerException {
        taskList.add(sampleTodo);
        taskList.deleteTask(1);

        taskList.undoDelete(); // first call succeeds
        assertFalse(taskList.hasDeletedTaskToUndo());

        // second consecutive call must fail
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            taskList.undoDelete();
        });
        assertEquals(Message.ERR_NO_UNDO_TASK, ex.getMessage());
    }

    @Test
    public void clearUndoHistory_pendingDelete_invalidatesUndo() throws TaskTrackerException {
        taskList.add(sampleTodo);
        taskList.deleteTask(1);
        assertTrue(taskList.hasDeletedTaskToUndo());

        taskList.clearUndoHistory();
        assertFalse(taskList.hasDeletedTaskToUndo());

        assertThrows(TaskTrackerException.class, () -> {
            taskList.undoDelete();
        });
    }

    // =========================================================================
    // Status (Mark / Unmark) Tests
    // =========================================================================

    @Test
    public void setTaskStatus_markDone_updatesStatus() throws TaskTrackerException {
        taskList.add(sampleTodo);

        String result = taskList.setTaskStatus(1, true);
        assertTrue(result.contains(Message.MSG_TASK_MARKED));
        assertTrue(taskList.getTasks().get(0).toString().contains("[X]"));
    }

    @Test
    public void setTaskStatus_unmarkDone_updatesStatus() throws TaskTrackerException {
        taskList.add(sampleTodo);
        taskList.setTaskStatus(1, true);

        String result = taskList.setTaskStatus(1, false);
        assertTrue(result.contains(Message.MSG_TASK_UNMARKED));
        assertTrue(taskList.getTasks().get(0).toString().contains("[ ]"));
    }

    @Test
    public void setTaskStatus_outOfBounds_throwsException() throws TaskTrackerException {
        taskList.add(sampleTodo);

        assertThrows(TaskTrackerException.class, () -> {
            taskList.setTaskStatus(0, true);
        });
        assertThrows(TaskTrackerException.class, () -> {
            taskList.setTaskStatus(2, true);
        });
    }

    // =========================================================================
    // Search & Formatting Tests
    // =========================================================================

    @Test
    public void findTasks_matchingKeyword_returnsFormattedResults() throws TaskTrackerException {
        taskList.add(new ToDo("read book chapter 1"));
        taskList.add(new ToDo("clean bedroom"));
        taskList.add(new ToDo("return library book"));

        String result = taskList.findTasks("book");
        assertTrue(result.contains("read book chapter 1"));
        assertTrue(result.contains("return library book"));
        assertFalse(result.contains("clean bedroom"));
    }

    @Test
    public void findTasks_noMatch_returnsNoMatchingMessage() throws TaskTrackerException {
        taskList.add(sampleTodo);
        String result = taskList.findTasks("nonexistent");
        assertTrue(result.contains(Message.ERR_NO_MATCHING_TASKS));
    }

    @Test
    public void getFormattedList_emptyList_returnsEmptyMessage() {
        assertEquals(Message.ERR_TASK_LIST_EMPTY, taskList.getFormattedList());
    }

    @Test
    public void getFormattedList_populatedList_returnsNumberedItems() throws TaskTrackerException {
        taskList.add(sampleTodo);
        taskList.add(sampleFixedTask);

        String formatted = taskList.getFormattedList();
        assertTrue(formatted.startsWith("1. "));
        assertTrue(formatted.contains("2. "));
    }
}
