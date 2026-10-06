package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import duke.exception.DukeException;

/**
 * Unit tests for the {@link TaskList} class.
 */
public class TaskListTest {
    private TaskList taskList;
    private Task todo;
    private Task deadline;

    @BeforeEach
    public void setUp() throws DukeException {
        taskList = new TaskList();
        todo = new ToDo("read book");
        deadline = new Deadline("submit assignment", "2026-09-01 2359");
    }

    @Test
    public void addTask_singleTask_sizeIncrements() {
        assertEquals(0, taskList.size());
        taskList.addTask(todo);
        assertEquals(1, taskList.size());
    }

    @Test
    public void contains_existingAndNonExistingTasks_returnsExpected() {
        taskList.addTask(todo);
        org.junit.jupiter.api.Assertions.assertTrue(taskList.contains(new ToDo("read book")));
        org.junit.jupiter.api.Assertions.assertFalse(taskList.contains(deadline));
    }

    @Test
    public void getTask_validIndex_returnsCorrectTask() throws DukeException {
        taskList.addTask(todo);
        taskList.addTask(deadline);
        assertEquals(todo, taskList.getTask(0));
        assertEquals(deadline, taskList.getTask(1));
    }

    @Test
    public void getTask_negativeIndex_exceptionThrown() {
        taskList.addTask(todo);
        DukeException exception = assertThrows(DukeException.class, () -> taskList.getTask(-1));
        assertEquals("OOPS!!! The task number is invalid.", exception.getMessage());
    }

    @Test
    public void getTask_indexOutOfBounds_exceptionThrown() {
        taskList.addTask(todo);
        DukeException exception = assertThrows(DukeException.class, () -> taskList.getTask(1));
        assertEquals("OOPS!!! The task number is invalid.", exception.getMessage());
    }

    @Test
    public void deleteTask_validIndex_removesAndReturnsTask() throws DukeException {
        taskList.addTask(todo);
        taskList.addTask(deadline);
        Task removed = taskList.deleteTask(0);
        assertEquals(todo, removed);
        assertEquals(1, taskList.size());
        assertEquals(deadline, taskList.getTask(0));
    }

    @Test
    public void deleteTask_invalidIndex_exceptionThrown() {
        taskList.addTask(todo);
        DukeException exception = assertThrows(DukeException.class, () -> taskList.deleteTask(5));
        assertEquals("OOPS!!! The task number is invalid.", exception.getMessage());
    }

    @Test
    public void constructor_withExistingList_initializesProperly() {
        ArrayList<Task> initialList = new ArrayList<>();
        initialList.add(todo);
        TaskList list = new TaskList(initialList);
        assertEquals(1, list.size());
    }

    @Test
    public void findTasks_matchingKeyword_returnsMatchingTasks() throws DukeException {
        taskList.addTask(todo); // "read book"
        taskList.addTask(deadline); // "submit assignment"
        taskList.addTask(new ToDo("return book"));

        TaskList found = taskList.findTasks("book");
        assertEquals(2, found.size());
        assertEquals("read book", found.getTask(0).toString());
        assertEquals("return book", found.getTask(1).toString());
    }

    @Test
    public void findTasks_noMatch_returnsEmptyList() {
        taskList.addTask(todo);
        TaskList found = taskList.findTasks("nonexistent");
        assertEquals(0, found.size());
    }

    @Test
    public void constructor_varargsEmpty_initializesEmptyList() {
        TaskList emptyList = new TaskList();
        assertEquals(0, emptyList.size());
    }

    @Test
    public void constructor_varargsMultipleTasks_initializesProperly() throws DukeException {
        TaskList list = new TaskList(todo, deadline);
        assertEquals(2, list.size());
        assertEquals(todo, list.getTask(0));
        assertEquals(deadline, list.getTask(1));
    }

    @Test
    public void addTasks_varargsMultipleTasks_addsAllTasks() throws DukeException {
        taskList.addTasks(todo, deadline);
        assertEquals(2, taskList.size());
        assertEquals(todo, taskList.getTask(0));
        assertEquals(deadline, taskList.getTask(1));
    }

    @Test
    public void addTask_nullTask_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> taskList.addTask(null));
    }

    @Test
    public void undo_emptyHistory_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> taskList.undo());
        assertEquals("OOPS!!! There are no previous commands to undo.", exception.getMessage());
    }

    @Test
    public void undo_afterSnapshot_revertsToPreviousState() throws DukeException {
        taskList.addTask(todo);
        taskList.saveSnapshot();

        taskList.addTask(deadline);
        assertEquals(2, taskList.size());

        taskList.undo();
        assertEquals(1, taskList.size());
        assertEquals(todo.toString(), taskList.getTask(0).toString());
    }

    @Test
    public void canUndo_withAndWithoutHistory_returnsCorrectBoolean() {
        assertFalse(taskList.canUndo());
        taskList.saveSnapshot();
        assertTrue(taskList.canUndo());
    }

    @Test
    public void copy_taskList_createsIndependentDeepCopy() throws DukeException {
        taskList.addTask(todo);
        TaskList cloned = taskList.copy();

        assertEquals(1, cloned.size());
        cloned.getTask(0).markAsDone();

        assertTrue(cloned.getTask(0).isDone());
        assertFalse(taskList.getTask(0).isDone());
    }

    @Test
    public void copy_eventAndDeadline_createsIndependentCopies() throws DukeException {
        Event event = new Event("project meeting", "2026-09-01 1400", "2026-09-01 1600");
        Event eventCopy = event.copy();
        assertEquals(event.toString(), eventCopy.toString());
        eventCopy.markAsDone();
        assertTrue(eventCopy.isDone());
        assertFalse(event.isDone());

        Deadline dl = new Deadline("submit quiz", "2026-09-02 2359");
        Deadline dlCopy = dl.copy();
        assertEquals(dl.toString(), dlCopy.toString());
        dlCopy.markAsDone();
        assertTrue(dlCopy.isDone());
        assertFalse(dl.isDone());
    }

    @Test
    public void getTasks_returnsUnderlyingList() {
        taskList.addTask(todo);
        assertEquals(1, taskList.getTasks().size());
        assertEquals(todo, taskList.getTasks().get(0));
    }

    @Test
    public void constructor_nullArguments_throwsAssertionError() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> new TaskList((Task[]) null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () ->
                new TaskList((java.util.ArrayList<Task>) null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> taskList.contains(null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> taskList.findTasks(null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> taskList.addTasks((Task[]) null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> taskList.addTasks(new Task[]{null}));
    }
}
