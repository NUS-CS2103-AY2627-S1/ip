package samantha.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import samantha.exception.TaskValidationException;

class TodoTest {
    @Test
    void constructor_blankDescription_taskValidationExceptionThrown() {
        assertThrows(TaskValidationException.class, () -> new Todo("   "));
    }

    @Test
    void constructor_descriptionWithSeparator_taskValidationExceptionThrown() {
        TaskValidationException exception = assertThrows(TaskValidationException.class, () ->
                new Todo("a | b"));
        assertEquals("The description of a todo cannot contain |.", exception.getMessage());
    }

    @Test
    void todo_newAndMarked_statusAndRepresentationsUpdated() throws TaskValidationException {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        assertEquals("[T]", todo.getType());
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileString());
        assertFalse(todo.isOnDate(LocalDate.of(2024, 1, 1)));

        todo.markDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toFileString());

        todo.markNotDone();
        assertFalse(todo.isDone());
    }
}
