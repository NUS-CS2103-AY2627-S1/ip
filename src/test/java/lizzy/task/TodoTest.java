package lizzy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests todo construction and display formatting.
 */
public class TodoTest {
    @Test
    void toString_incompleteAndCompleteTodo_typeStatusAndDescriptionIncluded() {
        Todo incompleteTodo = new Todo("read chapter");
        Todo completeTodo = new Todo("write tests", true);

        assertEquals("[T][ ] read chapter", incompleteTodo.toString());
        assertEquals("[T][X] write tests", completeTodo.toString());
        assertTrue(completeTodo.isDone());
    }
}
