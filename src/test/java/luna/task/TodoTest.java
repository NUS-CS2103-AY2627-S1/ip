package luna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import luna.exception.LunaException;

public class TodoTest {

    @Test
    void markDone_taskMarkedAsDone_success() throws LunaException {
        Todo todo = new Todo("read book");
        todo.markDone();
        assertEquals(true, todo.isDone());
    }

    @Test
    void toString_correctFormat_success() throws LunaException {
        Todo todo = new Todo("read book");

        // not done
        assertEquals("[T][ ] read book", todo.toString());

        // done
        todo.markDone();
        assertEquals("[T][X] read book", todo.toString());
    }
}
