package auto.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests todo-specific rendering while retaining the base task behavior. */
class ToDoTest {
    @Test
    void toString_incompleteAndCompletedTodo_displaysTypeAndStatus() {
        ToDo task = new ToDo("read book");
        assertEquals("[T][ ] read book", task.toString());

        task.mark();
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    void toDataString_todo_usesTodoRecordTypeAndStatus() {
        ToDo task = new ToDo("read book");
        task.mark();

        assertEquals("T | 1 | cmVhZCBib29r", task.toDataString());
    }
}
