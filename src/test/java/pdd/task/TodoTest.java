package pdd.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void toString_notDone_showsEmptyStatusBracket() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_markedDone_showsXStatusBracket() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toFileFormat_notDone_matchesSaveFileFormat() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_markedDone_usesOneStatusFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void occursOn_anyDate_alwaysFalse() {
        Todo todo = new Todo("read book");
        assertEquals(false, todo.occursOn(java.time.LocalDate.of(2019, 12, 1)));
    }
}
