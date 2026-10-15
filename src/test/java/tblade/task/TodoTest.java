package tblade.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests Todo, and through it the shared behaviour defined in the abstract Task class
 * (marking done/undone, the status icon, the toString format, and equals/hashCode).
 */
public class TodoTest {
    @Test
    public void constructor_newTodo_isNotDone() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
        assertEquals("read book", todo.getDescription());
        assertEquals(TaskType.TODO, todo.getType());
    }

    @Test
    public void markAsDone_updatesStatusIconAndIsDone() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void unmarkAsDone_afterMarking_revertsToNotDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.unmarkAsDone();

        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void toString_notDone_hasEmptyTimeDetailsAndSpaceIcon() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_done_showsXIcon() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void equals_sameDescription_returnsTrue() {
        assertEquals(new Todo("read book"), new Todo("read book"));
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        assertNotEquals(new Todo("read book"), new Todo("return book"));
    }

    @Test
    public void equals_differentTaskType_returnsFalse() {
        assertNotEquals(new Todo("read book"), new Deadline("read book", LocalDate.of(2026, 3, 15)));
    }

    @Test
    public void equals_ignoresDoneStatus() {
        Todo done = new Todo("read book");
        done.markAsDone();

        assertEquals(new Todo("read book"), done);
    }

    @Test
    public void hashCode_equalTasks_haveSameHashCode() {
        assertEquals(new Todo("read book").hashCode(), new Todo("read book").hashCode());
    }
}
