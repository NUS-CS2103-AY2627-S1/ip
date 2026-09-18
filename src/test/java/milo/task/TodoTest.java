package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TodoTest {
    @Test
    void toString_newTodo_prependsTTag() {
        Todo todo = new Todo("borrow book");
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    void toString_doneTodo_showsXInBracket() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    void toFileFormat_newTodo_matchesExpectedFormat() {
        Todo todo = new Todo("borrow book");
        assertEquals("T | 0 | borrow book", todo.toFileFormat());
    }

    @Test
    void toFileFormat_doneTodo_flagIsOne() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("T | 1 | borrow book", todo.toFileFormat());
    }
}
