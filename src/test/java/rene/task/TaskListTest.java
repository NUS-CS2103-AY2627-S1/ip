package rene.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rene.exception.ReneException;

class TaskListTest {
    private TaskList tasks;

    @BeforeEach
    void setUp() {
        tasks = new TaskList();
        tasks.add(new Todo("first task"));
        tasks.add(new Todo("second task"));
    }

    @Test
    void mark_incompleteTask_marksRequestedTask() throws ReneException {
        Task markedTask = tasks.mark(2);

        assertEquals("second task", markedTask.getDescription());
        assertTrue(markedTask.isDone());
        assertFalse(tasks.getTasks().get(0).isDone());
    }

    @Test
    void mark_completedTask_throwsExceptionWithoutChangingOtherTask() throws ReneException {
        tasks.mark(1);

        ReneException exception = assertThrows(ReneException.class, () -> tasks.mark(1));

        assertEquals("This action item has already been completed — no action required.", exception.getMessage());
        assertFalse(tasks.getTasks().get(1).isDone());
    }

    @Test
    void unmark_completedTask_marksRequestedTaskIncomplete() throws ReneException {
        tasks.mark(1);

        Task unmarkedTask = tasks.unmark(1);

        assertFalse(unmarkedTask.isDone());
    }

    @Test
    void remove_firstTask_renumbersRemainingTask() throws ReneException {
        Task removedTask = tasks.remove(1);

        assertEquals("first task", removedTask.getDescription());
        assertEquals(1, tasks.size());
        assertEquals("second task", tasks.getTasks().get(0).getDescription());
    }

    @Test
    void remove_invalidPositions_throwsExceptionWithoutChangingList() {
        assertThrows(ReneException.class, () -> tasks.remove(0));
        assertThrows(ReneException.class, () -> tasks.remove(-1));
        assertThrows(ReneException.class, () -> tasks.remove(3));

        assertEquals(2, tasks.size());
    }

    @Test
    void getTasks_returnedSnapshotCannotModifyTaskList() {
        assertThrows(UnsupportedOperationException.class, () -> tasks.getTasks().clear());
        assertEquals(2, tasks.size());
    }

    @Test
    void copy_changesDoNotAffectOriginalTaskList() throws ReneException {
        TaskList copy = tasks.copy();

        copy.mark(1);
        copy.remove(2);

        assertFalse(tasks.getTasks().get(0).isDone());
        assertEquals(2, tasks.size());
        assertTrue(copy.getTasks().get(0).isDone());
        assertEquals(1, copy.size());
    }

    @Test
    void find_mixedCaseKeyword_returnsDescriptionMatchesInOriginalOrder() {
        tasks.add(new Todo("read BOOK"));
        tasks.add(new Deadline("return Book", LocalDate.of(2026, 8, 31)));

        List<Task> matches = tasks.find("bOoK");

        assertEquals(2, matches.size());
        assertEquals("read BOOK", matches.get(0).getDescription());
        assertEquals("return Book", matches.get(1).getDescription());
    }

    @Test
    void find_keywordOnlyInTaskMetadata_doesNotMatch() {
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 8, 31)));

        List<Task> matches = tasks.find("2026");

        assertTrue(matches.isEmpty());
    }
}
