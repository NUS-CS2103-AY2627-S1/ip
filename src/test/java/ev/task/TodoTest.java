package ev.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ev.EvException;

public class TodoTest {

    @Test
    public void toString_newTodo_emptyStatusBox() throws EvException {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_doneTodo_tickedStatusBox() throws EvException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toString_unmarkedAgain_backToEmptyStatusBox() throws EvException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFormat_newTodo_notDoneFlag() throws EvException {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_doneTodo_doneFlag() throws EvException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void occursOn_anyDate_false() throws EvException {
        assertFalse(new Todo("read book").occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void hasKeyword_wordInDescription_true() throws EvException {
        assertTrue(new Todo("read book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_partOfWord_true() throws EvException {
        assertTrue(new Todo("read book").hasKeyword("oo"));
    }

    @Test
    public void hasKeyword_differentCase_true() throws EvException {
        assertTrue(new Todo("Read Book").hasKeyword("book"));
        assertTrue(new Todo("read book").hasKeyword("BOOK"));
    }

    @Test
    public void hasKeyword_wordNotInDescription_false() throws EvException {
        assertFalse(new Todo("read book").hasKeyword("plants"));
    }

    @Test
    public void applyUpdate_description_replaced() throws EvException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.applyUpdate(Task.OPTION_DESC, "read the whole book");
        assertEquals("[T][X] read the whole book", todo.toString());
    }

    @Test
    public void applyUpdate_optionThisTypeLacks_rejectedAndUnchanged() throws EvException {
        Todo todo = new Todo("read book");
        EvException thrown = assertThrows(EvException.class, () ->
                todo.applyUpdate("/by", "2019-12-05"));
        assertTrue(thrown.getMessage().contains("a todo"));
        assertTrue(thrown.getMessage().contains("/desc"));
        assertEquals("[T][ ] read book", todo.toString());
    }
}
