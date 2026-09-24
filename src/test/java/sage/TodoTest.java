package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import sage.model.Todo;
class TodoTest {

    @Test
    void toString_newTodo_correctTodoFormatReturned() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toString_completedTodo_completedFormatReturned() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }
}
