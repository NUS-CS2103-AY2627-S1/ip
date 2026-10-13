package arrodes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests todo-specific display formatting. */
class TodoTest {

    /** Displays the todo type and incomplete status. */
    @Test
    void toString_incompleteTodo_hasCorrectPrefix() {
        Todo todo = new Todo("buy groceries");
        assertEquals("[T][ ] buy groceries", todo.toString());
    }

    /** Displays the todo type and completed status. */
    @Test
    void toString_completedTodo_hasCorrectPrefix() {
        Todo todo = new Todo("buy groceries");
        todo.markAsDone();
        assertEquals("[T][X] buy groceries", todo.toString());
    }
}
