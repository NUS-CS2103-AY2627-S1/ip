package botzilla.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import botzilla.BotzillaException;

/**
 * Tests TaskList's duplicate-detection, used to reject re-adding a task
 * that's effectively identical to one already in the list.
 */
public class TaskListTest {

    @Test
    void hasDuplicate_sameTypeAndName_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDoTask("read book"));

        assertTrue(tasks.hasDuplicate(new ToDoTask("read book")));
    }

    @Test
    void hasDuplicate_nameDiffersOnlyInCase_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDoTask("read book"));

        assertTrue(tasks.hasDuplicate(new ToDoTask("Read Book")));
    }

    @Test
    void hasDuplicate_differentName_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDoTask("read book"));

        assertFalse(tasks.hasDuplicate(new ToDoTask("write book")));
    }

    @Test
    void hasDuplicate_sameNameDifferentType_returnsFalse() throws BotzillaException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDoTask("read book"));

        assertFalse(tasks.hasDuplicate(new DeadlineTask("read book", "2/12/2019")));
    }

    @Test
    void hasDuplicate_sameNameAndDeadline_returnsTrue() throws BotzillaException {
        TaskList tasks = new TaskList();
        tasks.add(new DeadlineTask("return book", "2/12/2019 1800"));

        assertTrue(tasks.hasDuplicate(new DeadlineTask("return book", "2/12/2019 1800")));
    }

    @Test
    void hasDuplicate_sameNameDifferentDeadline_returnsFalse() throws BotzillaException {
        TaskList tasks = new TaskList();
        tasks.add(new DeadlineTask("return book", "2/12/2019 1800"));

        assertFalse(tasks.hasDuplicate(new DeadlineTask("return book", "3/12/2019 1800")));
    }

    @Test
    void hasDuplicate_sameNameAndBothEventTimes_returnsTrue() throws BotzillaException {
        TaskList tasks = new TaskList();
        tasks.add(new EventTask("party", "1/1/2026 1800", "1/1/2026 2200"));

        assertTrue(tasks.hasDuplicate(new EventTask("party", "1/1/2026 1800", "1/1/2026 2200")));
    }

    @Test
    void hasDuplicate_doneStatusIgnored_stillReturnsTrue() {
        TaskList tasks = new TaskList();
        Task existing = new ToDoTask("read book");
        existing.mark();
        tasks.add(existing);

        assertTrue(tasks.hasDuplicate(new ToDoTask("read book")));
    }
}
