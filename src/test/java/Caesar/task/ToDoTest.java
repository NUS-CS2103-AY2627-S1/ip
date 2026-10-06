package caesar.task;

import caesar.exception.CaesarException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ToDoTest {

    @Test
    public void testToDoStringRepresentation() {
        ToDo todo = new ToDo("Read a book");

        assertEquals("[T][ ] Read a book", todo.toString());
    }

    @Test
    public void testToDoStringRepresentationWithDone() throws CaesarException {
        ToDo todo = new ToDo("Read a book");

        todo.markAsDone();

        assertEquals("[T][X] Read a book", todo.toString());

        CaesarException exception =
                assertThrows(CaesarException.class, todo::markAsDone);

        assertEquals("You have marked this task!", exception.getMessage());
    }
}
