package bubu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ToDoTest {

    @Test
    void getDescription_newTask_returnsDescription() {
        ToDo todo = new ToDo("read book");

        assertEquals("read book", todo.getDescription());
        assertTrue(todo.getReminderTime().isEmpty());
    }

    @Test
    void markAsDone_uncompletedTask_marksAsDone() {
        ToDo todo = new ToDo("read book");
        assertFalse(todo.isDone());

        todo.markAsDone();
        assertTrue(todo.isDone());

        todo.markAsUndone();
        assertFalse(todo.isDone());
    }

    @Test
    void toString_validTask_returnsFormattedString() {
        ToDo todo = new ToDo("read book");
        assertEquals("[T][ ] read book", todo.toString());

        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }
}
