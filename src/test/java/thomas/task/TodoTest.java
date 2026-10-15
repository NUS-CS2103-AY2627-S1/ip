package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TodoTest {
    @Test
    void toFileFormat_andToString_includeTodoType() {
        Todo todo = new Todo("read book");

        assertEquals("T | N | read book", todo.toFileFormat());
        assertEquals("[T][ ] read book", todo.toString());
    }
}
