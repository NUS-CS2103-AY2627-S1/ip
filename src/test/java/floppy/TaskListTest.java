package floppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskListTest {
    private TaskList taskList;

    @BeforeEach
    void setUp() {
        taskList = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return BOOK", LocalDate.of(2026, 9, 1)),
                new Todo("join sports club")));
    }

    @Test
    void find_partialKeyword_returnsMatchingTasks() {
        List<Task> matches = taskList.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return BOOK", matches.get(1).getDescription());
    }

    @Test
    void find_mixedCaseKeyword_matchesIgnoringCase() {
        List<Task> matches = taskList.find("SpOrTs");

        assertEquals(1, matches.size());
        assertEquals("join sports club", matches.get(0).getDescription());
    }

    @Test
    void find_absentKeyword_returnsEmptyList() {
        assertTrue(taskList.find("homework").isEmpty());
    }

    @Test
    void addMarkUnmarkDelete_validIndexes_updatesList() throws FloppyException {
        taskList.add(new Todo("buy milk"));
        assertEquals(4, taskList.size());

        Task markedTask = taskList.mark(3);
        assertTrue(markedTask.isDone());

        Task unmarkedTask = taskList.unmark(3);
        assertFalse(unmarkedTask.isDone());

        Task deletedTask = taskList.delete(3);
        assertEquals("buy milk", deletedTask.getDescription());
        assertEquals(3, taskList.size());
    }

    @Test
    void indexedOperations_invalidIndexes_throwException() {
        assertThrows(FloppyException.class, () -> taskList.mark(-1));
        assertThrows(FloppyException.class, () -> taskList.unmark(3));
        assertThrows(FloppyException.class, () -> taskList.delete(10));
    }

    @Test
    void getTasks_returnedSnapshot_cannotModifyTaskList() {
        List<Task> snapshot = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, () ->
                snapshot.add(new Todo("unexpected task")));
        assertEquals(3, taskList.size());
    }
}
