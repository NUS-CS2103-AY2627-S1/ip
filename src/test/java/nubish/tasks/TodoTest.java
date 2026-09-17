package nubish.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests todo task status and saving behavior.
 */
public class TodoTest {
    /**
     * Verifies that a new todo task starts as not done.
     */
    @Test
    public void getStatusIcon_newTodo_returnsUndoneIcon() {
        Task task = new Todo("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that a loaded done todo task shows the done icon.
     */
    @Test
    public void getStatusIcon_savedDoneTodo_returnsDoneIcon() {
        Task task = new Todo(true, "read book");

        assertEquals("X", task.getStatusIcon());
    }

    /**
     * Verifies that a loaded undone todo task shows the undone icon.
     */
    @Test
    public void getStatusIcon_savedUndoneTodo_returnsUndoneIcon() {
        Task task = new Todo(false, "read book");

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that marking a todo task changes its status icon to done.
     */
    @Test
    public void getStatusIcon_markedTodo_returnsDoneIcon() {
        Task task = new Todo("read book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    /**
     * Verifies that unmarking a todo task changes its status icon to undone.
     */
    @Test
    public void getStatusIcon_unmarkedTodo_returnsUndoneIcon() {
        Task task = new Todo(true, "read book");

        task.unmarkAsDone();

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that an undone todo task is saved in the expected file format.
     */
    @Test
    public void saveString_unmarkedTodo_returnsCorrectFormat() {
        Task task = new Todo("read book");

        assertEquals("T | 0 | read book", task.saveString());
    }

    /**
     * Verifies that a done todo task is saved in the expected file format.
     */
    @Test
    public void saveString_markedTodo_returnsCorrectFormat() {
        Task task = new Todo(true, "read book");

        assertEquals("T | 1 | read book", task.saveString());
    }

    /**
     * Verifies that a todo task is displayed in the expected user-facing format.
     */
    @Test
    public void toString_validTodo_returnsFormattedTodo() {
        Task task = new Todo("read book");

        assertEquals("[T][ ] read book", task.toString());
    }
}
