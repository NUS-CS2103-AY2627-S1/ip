package plana.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ToDoTest {

    @Test
    void toString_incompleteTodo_includesTodoLabelAndDescription() {
        ToDo todo = new ToDo("buy groceries");

        assertEquals("[T][ ] buy groceries", todo.toString());
    }

    @Test
    void toString_completedTodo_includesDoneStatus() {
        ToDo todo = new ToDo("buy groceries");
        todo.markAsDone();

        assertEquals("[T][X] buy groceries", todo.toString());
    }
}
