package potato.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for testing the behavior of the {@link Todo} class.
 */
public class TodoTest {

    /**
     * Tests that todo tasks show the correct status before and after being marked done.
     */
    @Test
    public void testStringConversion() {
        Todo todo = new Todo("prep vegetables");
        assertEquals("[T][ ] prep vegetables", todo.toString());

        todo.markAsDone();
        assertEquals("[T][X] prep vegetables", todo.toString());
    }

    /**
     * Tests that todo tasks are converted into the correct save file format.
     */
    @Test
    public void testFileFormat() {
        Todo todo = new Todo("prep vegetables");
        assertEquals("T | 0 | prep vegetables", todo.toFileFormat());

        todo.markAsDone();
        assertEquals("T | 1 | prep vegetables", todo.toFileFormat());
    }
}
