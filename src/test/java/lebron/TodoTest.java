package lebron;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_notDone_correctFormat() {
        Todo todo = new Todo("read book");

        assertEquals(
                "[T][ ] read book",
                todo.toString()
        );
    }

    @Test
    public void toString_done_correctFormat() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals(
                "[T][X] read book",
                todo.toString()
        );
    }
}
