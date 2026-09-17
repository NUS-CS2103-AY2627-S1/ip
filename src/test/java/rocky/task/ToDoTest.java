package rocky.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/** Tests for how a {@link ToDo} is created and displayed. */
public class ToDoTest {

    @Test
    public void constructor_description_storedAndNotDone() {
        ToDo todo = new ToDo("read book");

        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    public void toString_notDone_typeIconAndEmptyCheckbox() {
        assertEquals("[T][ ] read book", new ToDo("read book").toString());
    }

    @Test
    public void toString_done_typeIconAndTickedCheckbox() {
        ToDo todo = new ToDo("read book");
        todo.mark();

        assertEquals("[T][x] read book", todo.toString());
    }
}
