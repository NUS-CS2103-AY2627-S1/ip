package task;

import michael.MichaelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoTest {

    @Test
    @DisplayName("Should successfully create a Todo task with valid description")
    void constructor_validDescription_success() throws MichaelException {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        assertFalse(todo.getStatusIcon().equals("X")); // Assuming Task tracks completion via isDone()
    }

    @Test
    @DisplayName("Should format string representation correctly when marked as done")
    void toString_markedAsDone_formattedCorrectly() throws MichaelException {
        Todo todo = new Todo("read book");
        todo.markAsDone(); // Assuming Task has a markAsDone() method

        assertEquals("[T][X]", todo.toString().substring(0, 6));
        assertTrue(todo.getStatusIcon().equals("X"));
    }

    @Test
    @DisplayName("Should throw MichaelException when description is empty")
    void constructor_emptyDescription_throwsMichaelException() {
        assertThrows(MichaelException.class, () -> new Todo(""));
    }

    @Test
    @DisplayName("Should throw MichaelException when description is only whitespace")
    void constructor_whitespaceDescription_throwsMichaelException() {
        assertThrows(MichaelException.class, () -> new Todo("   "));
    }

    @Test
    @DisplayName("Should preserve the task description")
    void getDescription_validTodo_returnsDescription() throws MichaelException {
        Todo todo = new Todo("read book");

        assertEquals("read book", todo.getDescription());
    }

    @Test
    @DisplayName("Should throw MichaelException when description is null")
    void constructor_nullDescription_throwsMichaelException() {
        assertThrows(MichaelException.class, () -> new Todo(null));
    }
}
