package bags.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bags.exception.BagsException;

/**
 * Tests the {@link Todo} class.
 */
class TodoTest {

    @Test
    void createTask_validCommand_createsTaskWithDescription() throws BagsException {
        Todo todo = Todo.createTask("todo Read chapter one");

        assertEquals("Read chapter one", todo.getDescription());
        assertEquals(TaskType.TODO, todo.getType());
        assertFalse(todo.isDone());
    }

    @Test
    void createTask_missingDescription_throwsException() {
        BagsException exception = assertThrows(
                BagsException.class, () -> Todo.createTask("todo")
        );

        assertTrue(exception.getMessage().contains("description"));
    }

    @Test
    void formattedTask_markedDone_returnsDisplayAndStorageFormats() {
        Todo todo = new Todo("Read book");
        todo.markDone();

        assertEquals("[T][X] Read book", todo.toString());
        assertEquals("T | [X] | Read book", todo.parseEvent());
    }
}
