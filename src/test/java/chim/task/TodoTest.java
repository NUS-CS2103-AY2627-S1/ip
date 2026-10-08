package chim.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_notDone_correctDisplayFormat() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_markedDone_correctDisplayFormat() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toFileFormat_notDone_correctFormat() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | none | read book", todo.toFileFormat());
    }

    @Test
    public void getDetailsKey_sameDescription_equalKeys() {
        Todo t1 = new Todo("read book");
        Todo t2 = new Todo("read book");
        assertEquals(t1.getDetailsKey(), t2.getDetailsKey());
    }
}
