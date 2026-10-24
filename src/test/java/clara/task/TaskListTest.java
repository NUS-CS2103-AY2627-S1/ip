package clara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clara.exception.ClaraException;

// NOTE: AI-assisted test suite implementation. See CITATIONS.md [C-010].

/**
 * Unit tests for {@link TaskList}.
 */
public class TaskListTest {
    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
    }

    @Test
    public void addTask_validTask_taskAddedAndSizeIncreases() throws ClaraException {
        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());

        Todo todo = new Todo("borrow book");
        taskList.addTask(todo);

        assertEquals(1, taskList.size());
        assertFalse(taskList.isEmpty());
        assertEquals(todo, taskList.getTask(1));
    }

    @Test
    // NOTE: AI-assisted assertion test. See CITATIONS.md [C-015].
    public void addTask_nullTask_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> taskList.addTask(null));
    }

    @Test
    public void getTask_validIndex_returnsCorrectTask() throws ClaraException {
        Todo firstTask = new Todo("task 1");
        Todo secondTask = new Todo("task 2");
        taskList.addTask(firstTask);
        taskList.addTask(secondTask);

        assertEquals(firstTask, taskList.getTask(1));
        assertEquals(secondTask, taskList.getTask(2));
    }

    @Test
    public void getTask_zeroOrNegativeIndex_exceptionThrown() {
        taskList.addTask(new Todo("task 1"));

        ClaraException exZero = assertThrows(ClaraException.class, () -> {
            taskList.getTask(0);
        });
        assertEquals("There is no task 0.", exZero.getMessage());

        ClaraException exNegative = assertThrows(ClaraException.class, () -> {
            taskList.getTask(-1);
        });
        assertEquals("There is no task -1.", exNegative.getMessage());
    }

    @Test
    public void getTask_indexOutOfBounds_exceptionThrown() {
        taskList.addTask(new Todo("task 1"));

        ClaraException ex = assertThrows(ClaraException.class, () -> {
            taskList.getTask(2);
        });
        assertEquals("There is no task 2.", ex.getMessage());
    }

    @Test
    public void markTask_unmarkedTask_taskMarkedSuccessfully() throws ClaraException {
        Todo todo = new Todo("read chapter");
        taskList.addTask(todo);

        assertFalse(todo.isDone());
        Task marked = taskList.markTask(1);
        assertTrue(marked.isDone());
        assertTrue(todo.isDone());
    }

    @Test
    public void markTask_alreadyMarkedTask_exceptionThrown() throws ClaraException {
        Todo todo = new Todo("read chapter");
        taskList.addTask(todo);
        taskList.markTask(1);

        ClaraException ex = assertThrows(ClaraException.class, () -> {
            taskList.markTask(1);
        });
        assertTrue(ex.getMessage().contains("is already marked"));
    }

    @Test
    public void markTask_invalidIndex_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            taskList.markTask(1);
        });
        assertEquals("There is no task 1.", ex.getMessage());
    }

    @Test
    public void unmarkTask_markedTask_taskUnmarkedSuccessfully() throws ClaraException {
        Todo todo = new Todo("read chapter");
        taskList.addTask(todo);
        taskList.markTask(1);
        assertTrue(todo.isDone());

        Task unmarked = taskList.unmarkTask(1);
        assertFalse(unmarked.isDone());
        assertFalse(todo.isDone());
    }

    @Test
    public void unmarkTask_alreadyUnmarkedTask_exceptionThrown() {
        Todo todo = new Todo("read chapter");
        taskList.addTask(todo);

        ClaraException ex = assertThrows(ClaraException.class, () -> {
            taskList.unmarkTask(1);
        });
        assertTrue(ex.getMessage().contains("is already unmarked"));
    }

    @Test
    public void unmarkTask_invalidIndex_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            taskList.unmarkTask(1);
        });
        assertEquals("There is no task 1.", ex.getMessage());
    }

    @Test
    public void deleteTask_validIndex_taskRemovedAndReturned() throws ClaraException {
        Todo firstTask = new Todo("task 1");
        Todo secondTask = new Todo("task 2");
        taskList.addTask(firstTask);
        taskList.addTask(secondTask);

        Task removed = taskList.deleteTask(1);
        assertEquals(firstTask, removed);
        assertEquals(1, taskList.size());
        assertEquals(secondTask, taskList.getTask(1));
    }

    @Test
    public void deleteTask_invalidIndex_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            taskList.deleteTask(1);
        });
        assertEquals("There is no task 1.", ex.getMessage());
    }

    @Test
    public void findMatchingIndices_matchingKeyword_returnsMatchingIndices() {
        taskList.addTask(new Todo("read book"));
        taskList.addTask(new Todo("return book"));
        taskList.addTask(new Todo("write essay"));

        List<Integer> matches = taskList.findMatchingIndices("book");
        assertEquals(List.of(0, 1), matches);
    }

    @Test
    public void findMatchingIndices_noMatch_returnsEmptyList() {
        taskList.addTask(new Todo("read book"));
        taskList.addTask(new Todo("write essay"));

        List<Integer> matches = taskList.findMatchingIndices("science");
        assertTrue(matches.isEmpty());
    }

    @Test
    public void findMatchingIndices_specialCharacters_matchesLiteral() {
        taskList.addTask(new Todo("math (revision)"));
        taskList.addTask(new Todo("science revision"));

        List<Integer> matches = taskList.findMatchingIndices("(revision)");
        assertEquals(List.of(0), matches);
    }
}
