package minerva.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests todo display and storage formatting. */
public class ToDoTest {

    @Test
    public void toString_unmarkedTask_success() {
        ToDo todo = new ToDo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFormat_unmarkedTask_success() {
        ToDo todo = new ToDo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }
}
