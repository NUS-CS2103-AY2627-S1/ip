package sioet.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_incompleteTodo_showsIncompleteStatus() {
        Todo todo = new Todo("read book");

        assertEquals(
                "[T][ ] read book",
                todo.toString()
        );
    }

    @Test
    public void toString_completedTodo_showsCompletedStatus() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals(
                "[T][X] read book",
                todo.toString()
        );
    }
}
