package amigobot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for Todo's toString formatting.
 */
public class TodoTest {

    @Test
    public void toString_notDone_formatsWithTPrefix() {
        Todo todo = new Todo("buy groceries");
        assertEquals("[T][ ] buy groceries", todo.toString());
    }

    @Test
    public void toString_done_formatsWithXMark() {
        Todo todo = new Todo("buy groceries");
        todo.markAsDone();
        assertEquals("[T][X] buy groceries", todo.toString());
    }

    @Test
    public void getDescription_returnsDescription() {
        Todo todo = new Todo("read book");
        assertEquals("read book", todo.getDescription());
    }
}
