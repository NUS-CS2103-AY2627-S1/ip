package blud.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the formatting of todo tasks. */
public class ToDoTest {
    /** Verifies that a todo task has the expected display format. */
    @Test
    public void testFormat() {
        ToDo newDeadline = new ToDo(new String[] {"Todo task1"});
        assertEquals("[T][ ] task1", newDeadline.toString());
    }
}
