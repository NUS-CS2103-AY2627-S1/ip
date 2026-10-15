package dukey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_newTodo_returnsTodoTypeAndUndoneStatus() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileString_newTodo_returnsStorageFormatWithUndoneStatus() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book |", todo.toFileString());
    }

    @Test
    public void toFileString_doneTodo_returnsStorageFormatWithDoneStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone(LocalDateTime.of(2026, 9, 14, 14, 30));

        assertEquals("T | 1 | read book | 2026-09-14 1430", todo.toFileString());
    }
}
