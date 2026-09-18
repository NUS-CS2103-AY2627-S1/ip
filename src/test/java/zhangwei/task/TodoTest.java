package zhangwei.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Todo}. A todo adds only its "[T]" marker to what {@link Task}
 * already prints, so these tests check that marker and that the shared
 * behaviour still comes through.
 */
public class TodoTest {

    @Test
    public void constructor_newTodo_notDone() {
        assertFalse(new Todo("borrow book").isDone());
    }

    @Test
    public void getDescription_todoCreatedWithDescription_sameDescriptionReturned() {
        assertEquals("borrow book", new Todo("borrow book").getDescription());
    }

    @Test
    public void toString_notDoneTodo_typeIconAndEmptyBoxShown() {
        assertEquals("[T][ ] borrow book", new Todo("borrow book").toString());
    }

    @Test
    public void toString_doneTodo_typeIconAndCrossedBoxShown() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("[T][X] borrow book", todo.toString());
    }
}
