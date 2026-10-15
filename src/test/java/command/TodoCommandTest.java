package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import exception.CbtException;
import task.TaskList;

/** Tests creation of todo tasks from commands. */
public class TodoCommandTest {
    /** Verifies that a valid description adds a todo and reports a state change. */
    @Test
    public void execute_validDescription_taskAddedAndChangedResultReturned() throws CbtException {
        TaskList tasks = new TaskList();

        CommandResult result = new TodoCommand("read book").execute(tasks);

        assertEquals(1, tasks.getSize());
        assertEquals("[T][ ] read book", tasks.getTask(0).toString());
        assertTrue(result.isTaskListChanged());
        assertFalse(result.isExit());
        assertTrue(result.message().contains("Your flight plan now has 1 task."));
    }

    /** Verifies that a blank todo description is rejected without changing the list. */
    @Test
    public void execute_blankDescription_exceptionThrownAndListUnchanged() {
        TaskList tasks = new TaskList();

        assertThrows(CbtException.class, () -> new TodoCommand("   ").execute(tasks));
        assertEquals(0, tasks.getSize());
    }

    /** Verifies that unsafe, excessively long, and duplicate descriptions are rejected. */
    @Test
    public void execute_invalidOrDuplicateDescription_exceptionThrownAndListUnchanged() throws CbtException {
        TaskList tasks = new TaskList();
        new TodoCommand("read book").execute(tasks);

        assertThrows(CbtException.class, () -> new TodoCommand("read | book").execute(tasks));
        assertThrows(CbtException.class, () -> new TodoCommand("x".repeat(301)).execute(tasks));
        assertThrows(CbtException.class, () -> new TodoCommand("READ BOOK").execute(tasks));
        assertEquals(1, tasks.getSize());
    }
}
