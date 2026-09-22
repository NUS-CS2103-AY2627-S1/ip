package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the behavior of {@link Todo} tasks. */
class TodoTest {

    @Test
    void constructor_validDescription_todoStartsUndone() {
        Todo todo = new Todo("milk");

        assertEquals("[T][ ] milk", todo.toString());
    }

    @Test
    void constructor_nullDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
    }

    @Test
    void constructor_emptyDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(""));
    }

    @Test
    void constructor_whitespaceDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Todo("   "));
    }

    @Test
    void markAsDone_updatesStatusInDisplay() {
        Todo todo = new Todo("milk");

        todo.markAsDone();

        assertEquals("[T][X] milk", todo.toString());
    }

    @Test
    void markAsUndone_doneTodo_updatesStatusInDisplay() {
        Todo todo = new Todo("milk");
        todo.markAsDone();

        todo.markAsUndone();

        assertEquals("[T][ ] milk", todo.toString());
    }

    @Test
    void toFileString_undoneTodo_returnsStorageFormat() {
        Todo todo = new Todo("milk");

        assertEquals("[T],0,milk", todo.toFileString());
    }

    @Test
    void toFileString_doneTodo_returnsStorageFormat() {
        Todo todo = new Todo("milk");
        todo.markAsDone();

        assertEquals("[T],1,milk", todo.toFileString());
    }

    @Test
    void toFileString_descriptionWithSpecialCharacters_escapesCharacters() {
        Todo todo = new Todo("buy milk, eggs\\bread\nnext line\rcarriage return");

        assertEquals("[T],0,buy milk\\, eggs\\\\bread\\nnext line\\rcarriage return",
                todo.toFileString());
    }
}
