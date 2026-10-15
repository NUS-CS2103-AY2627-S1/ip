package bucket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void getTypeIcon_always_isT() {
        assertEquals("T", new Todo("read book").getTypeIcon());
    }

    @Test
    public void toString_notDone_showsEmptyBox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_done_showsCross() {
        Todo todo = new Todo("read book");
        todo.setDone(true);
        assertEquals("[T][X] read book", todo.toString());
    }

    // Unmarking has to put the box back, not leave the cross behind
    @Test
    public void toString_markedThenUnmarked_showsEmptyBoxAgain() {
        Todo todo = new Todo("read book");
        todo.setDone(true);
        todo.setDone(false);
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toSaveString_notDone_usesZero() {
        assertEquals("T | 0 | read book", new Todo("read book").toSaveString());
    }

    @Test
    public void toSaveString_done_usesOne() {
        Todo todo = new Todo("read book");
        todo.setDone(true);
        assertEquals("T | 1 | read book", todo.toSaveString());
    }

    // A todo carries no date, which is what sends it to the end when the list is sorted
    @Test
    public void getSortDate_always_isEmpty() {
        assertTrue(new Todo("read book").getSortDate().isEmpty());
    }

    // Descriptions are whatever the user typed, so non-ASCII has to survive intact
    @Test
    public void toString_nonAsciiDescription_keptIntact() {
        assertEquals("[T][ ] 读一本书", new Todo("读一本书").toString());
    }

    @Test
    public void getName_returnsDescriptionUnchanged() {
        assertEquals("read book", new Todo("read book").getName());
    }
}
