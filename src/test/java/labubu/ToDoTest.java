package labubu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class ToDoTest {
    @Test
    public void testMarker() {
        ToDo todo = new ToDo("task");
        assertEquals("T", todo.getMarker());
    }

    @Test
    public void testSaveFormat() {
        ToDo todo = new ToDo("task");
        todo.setStatus(Task.Status.COMPLETED);
        assertEquals("T|COMPLETED|task", todo.toSaveFormat());
    }
}
