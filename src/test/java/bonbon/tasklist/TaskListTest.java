package bonbon.tasklist;

import bonbon.exception.BonBonException;
import bonbon.exception.BonBonOutOfBoundsException;
import bonbon.exception.BonBonTaskListFullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList(10);
    }

    // Task Addition Tests

    @Test
    public void addToDo_validInput_returnsCorrectIndexAndIncrementsSize() throws BonBonTaskListFullException {
        int index1 = taskList.addToDo("read book");
        int index2 = taskList.addToDo("buy groceries");

        assertEquals(0, index1);
        assertEquals(1, index2);
        assertEquals(2, taskList.getCurrSize());
    }

    @Test
    public void addDeadline_validInput_addsDeadlineTask() throws BonBonTaskListFullException {
        LocalDateTime deadline = LocalDateTime.of(2026, 9, 20, 23, 59);
        taskList.addDeadline("submit report", deadline);

        assertEquals(1, taskList.getCurrSize());
        assertEquals("submit report", taskList.getTask(0).getName());
    }

    @Test
    public void addEvent_validInput_addsEventTask() throws BonBonException {
        LocalDateTime start = LocalDateTime.of(2026, 9, 20, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 20, 12, 0);
        taskList.addEvent("orientation", start, end, "location");

        assertEquals(1, taskList.getCurrSize());
        assertEquals("orientation", taskList.getTask(0).getName());
    }

    @Test
    public void addToDo_exceedCapacity_throwsTaskListFullException() throws BonBonTaskListFullException {
        TaskList smallList = new TaskList(1);
        smallList.addToDo("task 1");

        assertThrows(BonBonTaskListFullException.class, () -> smallList.addToDo("task 2"));
        assertEquals(1, smallList.getCurrSize());
    }

    // Mark & Unmark Tests

    @Test
    public void mark_validIndex_marksTaskAsDone() throws BonBonTaskListFullException, BonBonOutOfBoundsException {
        taskList.addToDo("read book");
        taskList.mark(0);

        assertTrue(taskList.getTask(0).isDone());
    }

    @Test
    public void unmark_markedTask_unmarksTask() throws BonBonOutOfBoundsException, BonBonTaskListFullException {
        taskList.addToDo("read book");
        taskList.mark(0);
        taskList.unmark(0);

        assertFalse(taskList.getTask(0).isDone());
    }

    // Task Removal Tests

    @Test
    public void removeTask_validIndex_removesTaskAndDecrementsSize() throws BonBonTaskListFullException, BonBonOutOfBoundsException {
        taskList.addToDo("read book");
        taskList.addToDo("buy groceries");

        int status = taskList.removeTask(0);

        assertEquals(1, status);
        assertEquals(1, taskList.getCurrSize());
        assertEquals("buy groceries", taskList.getTask(0).getName());
    }

    @Test
    public void removeTask_outOfBoundsIndex_throwsOutOfBoundsException() throws BonBonTaskListFullException {
        taskList.addToDo("read book");

        assertThrows(BonBonOutOfBoundsException.class, () -> taskList.removeTask(5));
        assertThrows(BonBonOutOfBoundsException.class, () -> taskList.removeTask(-1));
        assertEquals(1, taskList.getCurrSize());
    }

    // Search / Find Tests

    @Test
    public void find_matchingKeyword_returnsMatchingTasksString() throws BonBonTaskListFullException {
        taskList.addToDo("read book");
        taskList.addToDo("buy groceries");

        String result = taskList.find("book");

        assertTrue(result.contains("read book"));
        assertFalse(result.contains("buy groceries"));
    }
}