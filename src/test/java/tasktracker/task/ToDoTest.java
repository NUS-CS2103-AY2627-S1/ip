package tasktracker.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {

    @Test
    public void toString_validToDo_correctFormat() {
        ToDo todo = new ToDo("read lecture slides");
        assertEquals("[T][ ] read lecture slides", todo.toString());

        todo.markAsDone();
        assertEquals("[T][X] read lecture slides", todo.toString());
    }

    @Test
    public void toFileFormat_unmarkedAndMarked_correctDelimiters() {
        ToDo todo = new ToDo("buy milk");
        assertEquals("T | 0 | buy milk", todo.toFileFormat());

        todo.markAsDone();
        assertEquals("T | 1 | buy milk", todo.toFileFormat());
    }
}
