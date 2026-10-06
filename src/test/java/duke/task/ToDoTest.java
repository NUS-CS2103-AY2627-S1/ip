package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ToDo} class.
 */
public class ToDoTest {

    @Test
    public void getTaskIcon_returnsT() {
        ToDo todo = new ToDo("read book");
        assertEquals("T", todo.getTaskIcon());
    }

    @Test
    public void toFileFormat_unmarkedToDo_formattedCorrectly() {
        ToDo todo = new ToDo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_markedToDo_formattedCorrectly() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void toString_returnsDescription() {
        ToDo todo = new ToDo("read book");
        assertEquals("read book", todo.toString());
    }

    @Test
    public void equals_andHashCode_operateCorrectly() {
        ToDo t1 = new ToDo("read book");
        ToDo t2 = new ToDo("read book");
        ToDo t3 = new ToDo("other book");

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        org.junit.jupiter.api.Assertions.assertNotEquals(t1, t3);
        org.junit.jupiter.api.Assertions.assertNotEquals(t1, null);
    }
}
