package penguin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list operations, searches, and undo history.
 */
class TaskListTest {
    @Test
    void undo_markedTask_restoresPreviousStatus() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.markTask(0);

        tasks.undo();

        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    void undo_withoutHistory_throwsException() {
        TaskList tasks = new TaskList();

        assertThrows(IllegalStateException.class, tasks::undo);
    }

    @Test
    void addLoadedTask_doesNotCreateUndoHistory() {
        TaskList tasks = new TaskList();
        tasks.addLoadedTask(new Todo("saved task"));

        assertThrows(IllegalStateException.class, tasks::undo);
        assertEquals(1, tasks.size());
    }

    @Test
    void undo_addedTask_restoresEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        tasks.undo();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void undo_deletedTask_restoresTaskAndPosition() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("buy milk"));
        tasks.deleteTask(0);

        tasks.undo();

        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
        assertEquals("[T][ ] buy milk", tasks.getTasks().get(1).toString());
    }

    @Test
    void undo_unmarkedTask_restoresCompletedStatus() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.markTask(0);
        tasks.unmarkTask(0);

        tasks.undo();

        assertEquals("[T][X] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    void markTask_alreadyMarked_rejectsWithoutAddingUndoHistory() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.markTask(0);

        assertThrows(IllegalStateException.class, () -> tasks.markTask(0));
        tasks.undo();

        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    void unmarkTask_alreadyUnmarked_rejectsWithoutAddingUndoHistory() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.markTask(0);
        tasks.unmarkTask(0);

        assertThrows(IllegalStateException.class, () -> tasks.unmarkTask(0));
        tasks.undo();

        assertEquals("[T][X] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    void undo_sixthChange_keepsOnlyFiveUndoStates() {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= 6; i++) {
            tasks.addTask(new Todo("task " + i));
        }

        for (int i = 0; i < 5; i++) {
            tasks.undo();
        }

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] task 1", tasks.getTasks().get(0).toString());
        assertThrows(IllegalStateException.class, tasks::undo);
    }

    @Test
    void addTask_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.addTask(null));
    }

    @Test
    void addLoadedTask_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.addLoadedTask(null));
    }

    @Test
    void restoreSnapshot_nullSnapshot_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.restoreSnapshot(null));
    }

    @Test
    void copy_completedTask_preservesStateForEveryTaskType() {
        Task[] tasks = {
            new Todo("read book"),
            new Deadline("submit report", LocalDateTime.of(2099, 12, 31, 18, 0)),
            new Event("meeting", LocalDateTime.of(2099, 12, 31, 14, 0),
                    LocalDateTime.of(2099, 12, 31, 16, 0))
        };

        for (Task task : tasks) {
            task.markDone();
            Task copy = task.copy();

            assertNotSame(task, copy);
            assertEquals(task.toString(), copy.toString());
            copy.markUndone();
            assertEquals("X", task.getStatus());
        }
    }

    @Test
    void restoreSnapshot_afterChanges_restoresTasksAndUndoHistory() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        TaskListSnapshot snapshot = tasks.createSnapshot();
        tasks.markTask(0);
        tasks.addTask(new Todo("buy milk"));

        tasks.restoreSnapshot(snapshot);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
        tasks.undo();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void addAndDelete_validTask_updatesTaskList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.deleteTask(0).toString());
        assertEquals(0, tasks.size());
    }

    @Test
    void markTask_validIndex_updatesCompletionStatus() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        tasks.markTask(0);

        assertEquals("[T][X] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    void findTasksOnDate_matchingDate_returnsOnlyDatedTasks() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Deadline("submit report",
                LocalDateTime.of(2099, 12, 31, 18, 0)));

        assertEquals(1, tasks.findTasksOnDate(LocalDate.of(2099, 12, 31)).size());
    }

    @Test
    void findTasksOnDate_nonFirstMatch_preservesOriginalTaskNumber() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Deadline("submit report",
                LocalDateTime.of(2099, 12, 31, 18, 0)));

        TaskSearchResult result = tasks.findTasksOnDate(
                LocalDate.of(2099, 12, 31)).get(0);

        assertEquals(2, result.getTaskNumber());
        assertEquals("[D][ ] submit report (by: 31 Dec 2099, 6:00PM)",
                result.getTask().toString());
    }

    @Test
    void findMatchingTasks_nonFirstMatch_preservesOriginalTaskNumber() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("unrelated"));
        tasks.addTask(new Todo("target report"));

        TaskSearchResult result = tasks.findMatchingTasks("report").get(0);

        assertEquals(2, result.getTaskNumber());
        assertEquals("[T][ ] target report", result.getTask().toString());
    }

    @Test
    void deleteTask_invalidIndex_throwsIndexException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class,
                () -> tasks.deleteTask(0));
    }

    @Test
    void emptyTaskList_noTasks_reportsEmptyAndNoStorageLines() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        assertEquals(0, tasks.getTasks().size());
        assertEquals(0, tasks.toStorageLines().size());
    }

    @Test
    void unmarkTask_completedTask_restoresIncompleteStatus() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.markTask(0);

        tasks.unmarkTask(0);

        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    void findTasksOnDate_spanningEvent_returnsEventOnDate() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Event("conference",
                LocalDateTime.of(2099, 12, 30, 9, 0),
                LocalDateTime.of(2099, 12, 31, 17, 0)));

        assertEquals(1, tasks.findTasksOnDate(LocalDate.of(2099, 12, 31)).size());
    }

    @Test
    void findTasksOnDate_eventStartingOnDate_includesEvent() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Event("conference",
                LocalDateTime.of(2099, 12, 31, 9, 0),
                LocalDateTime.of(2099, 12, 31, 17, 0)));

        assertEquals(1, tasks.findTasksOnDate(LocalDate.of(2099, 12, 31)).size());
    }

    @Test
    void findTasksOnDate_eventOutsideDate_excludesEvent() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Event("conference",
                LocalDateTime.of(2099, 12, 30, 9, 0),
                LocalDateTime.of(2099, 12, 30, 17, 0)));

        assertEquals(0, tasks.findTasksOnDate(LocalDate.of(2099, 12, 31)).size());
    }

    @Test
    void deleteTask_validIndex_preservesRemainingOrder() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first task"));
        tasks.addTask(new Todo("second task"));
        tasks.addTask(new Todo("third task"));

        tasks.deleteTask(1);

        assertEquals("[T][ ] first task", tasks.getTasks().get(0).toString());
        assertEquals("[T][ ] third task", tasks.getTasks().get(1).toString());
    }
}
