package victoria.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void todo_newTask_formatsAsIncompleteAndTracksCompletion() {
        Todo task = new Todo("read a book");

        assertEquals("read a book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("[T][ ] read a book", task.toString());

        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read a book", task.toString());

        task.markNotDone();
        assertFalse(task.isDone());
    }
}
