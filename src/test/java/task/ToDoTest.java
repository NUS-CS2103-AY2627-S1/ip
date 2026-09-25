package task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ToDo} class.
 */
class ToDoTest {
    @Test
    void toDo_newToDo_returnsCorrectData() {
        ToDo task = new ToDo("read book");

        assertEquals("read book", task.getName());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals("[T][ ] read book", task.renderTask());
        assertEquals("read book,P,T", task.serialize());
    }

    @Test
    void toDo_doneStatus_returnsCorrectData() {
        ToDo task = new ToDo("read book", TaskStatus.DONE);

        assertEquals("read book", task.getName());
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("[T][X] read book", task.renderTask());
        assertEquals("read book,D,T", task.serialize());
    }

    @Test
    void toDo_markAndUnmark_returnsCorrectStatus() {
        ToDo task = new ToDo("read book");

        task.mark();
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("[T][X] read book", task.renderTask());

        task.unmark();
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals("[T][ ] read book", task.renderTask());
    }
}
