package anby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void toString_undoneTodo_returnsTodoDisplayString() {
        Todo todo = new Todo("eat burger");

        assertEquals("[T][ ] eat burger", todo.toString());
    }

    @Test
    public void toString_doneTodo_returnsDoneTodoDisplayString() {
        Todo todo = new Todo("eat burger");

        todo.markAsDone();

        assertEquals("[T][X] eat burger", todo.toString());
    }

    @Test
    public void toFileString_undoneTodo_returnsTodoStorageString() {
        Todo todo = new Todo("eat burger");

        assertEquals("T | 0 | eat burger", todo.toFileString());
    }

    @Test
    public void toFileString_doneTodo_returnsDoneTodoStorageString() {
        Todo todo = new Todo("eat burger");

        todo.markAsDone();

        assertEquals("T | 1 | eat burger", todo.toFileString());
    }
}
