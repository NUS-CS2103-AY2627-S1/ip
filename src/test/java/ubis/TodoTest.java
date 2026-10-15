package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests validation and normalization of todo descriptions.
 */
public class TodoTest {
    @Test
    public void initialise_blankName_returnsNull() {
        assertNull(new Todo().initialise("   \t"));
    }

    @Test
    public void initialise_nameWithOuterWhitespace_returnsTrimmedTodo() {
        Task todo = new Todo().initialise("  read a book  ");
        assertEquals("{T}{0}{read a book}", todo.stringify());
    }

    @Test
    public void initialise_nullOrEmptyName_returnsHelpfulError() {
        for (String input : new String[] {null, "", "\t"}) {
            Todo todo = new Todo();
            assertNull(todo.initialise(input));
            assertEquals("Please provide a name for the todo.\nExample: todo read a book",
                    todo.getInitialisationError());
        }
    }

    @Test
    public void mark_repeatedStateChanges_updatesDisplayAndStorage() {
        Task todo = new Todo().initialise("read  a book");
        assertNull(todo.getInitialisationError());
        assertEquals("read  a book", todo.getName());
        assertEquals("[T][ ] read  a book", todo.toString());
        todo.mark();
        todo.mark();
        assertEquals("[T][X] read  a book", todo.toString());
        assertEquals("{T}{1}{read  a book}", todo.stringify());
        todo.unmark();
        todo.unmark();
        assertEquals("[T][ ] read  a book", todo.toString());
        assertEquals("{T}{0}{read  a book}", todo.stringify());
    }
}
