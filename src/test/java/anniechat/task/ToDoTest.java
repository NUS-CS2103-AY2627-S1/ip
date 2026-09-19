package anniechat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the behaviour of {@link ToDo}. */
public class ToDoTest {
    @Test
    public void constructor_validDescription_storesTaskDetails() {
        ToDo todo = new ToDo("todo read book");

        assertEquals("read book", todo.getTaskDesc());
        assertEquals("T", todo.getTaskIcon());
        assertEquals("[ ]", todo.statusIcon());
    }

    @Test
    public void constructor_taggedDescription_storesTag() {
        ToDo todo = new ToDo("todo read book #school");

        assertEquals("#school", todo.getTag());
        assertTrue(todo.hasTag("#school"));
        assertTrue(todo.hasTag("school"));
    }

    @Test
    public void constructor_multipleTags_rejectsInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new ToDo("todo read book #school #urgent"));
    }
}
