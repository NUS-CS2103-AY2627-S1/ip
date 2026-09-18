package benjamin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import benjamin.BenjaminException;

public class TaskTest {
    @Test
    public void toString_newTodo_showsEmptyStatusBox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_todoMarkedDone_showsCrossInStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void markAsNotDone_doneTask_clearsStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_deadlineWithTime_showsFormattedDate() throws BenjaminException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2/12/2019 1800"));

        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)", deadline.toString());
    }

    @Test
    public void toString_eventWithRange_showsBothEnds() throws BenjaminException {
        Event event = new Event("camp",
                TaskDateTime.parse("2019-08-06"),
                TaskDateTime.parse("2019-08-08"));

        assertEquals("[E][ ] camp (from: Aug 06 2019 to: Aug 08 2019)", event.toString());
    }

    @Test
    public void toSaveFormat_todo_usesTypeAndDoneFlag() {
        Todo todo = new Todo("join sports club");
        todo.markAsDone();

        assertEquals("T | 1 | join sports club", todo.toSaveFormat());
    }

    @Test
    public void toSaveFormat_deadline_appendsStoredDate() throws BenjaminException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2/12/2019 1800"));

        assertEquals("D | 0 | return book | 2019-12-02 1800", deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_event_appendsBothStoredDates() throws BenjaminException {
        Event event = new Event("camp",
                TaskDateTime.parse("2019-08-06 1400"),
                TaskDateTime.parse("2019-08-08 1600"));

        assertEquals("E | 0 | camp | 2019-08-06 1400 | 2019-08-08 1600", event.toSaveFormat());
    }

    @Test
    public void hasKeyword_wordInDescription_returnsTrue() {
        assertTrue(new Todo("read book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_differentCapitalization_returnsTrue() {
        assertTrue(new Todo("read book").hasKeyword("BOOK"));
        assertTrue(new Todo("Read Book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_wordNotInDescription_returnsFalse() {
        assertFalse(new Todo("read book").hasKeyword("holiday"));
    }

    @Test
    public void hasKeyword_matchesDescriptionOnly_ignoresTypeAndDate() throws BenjaminException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-06-06"));

        assertFalse(deadline.hasKeyword("Jun"));
        assertTrue(deadline.hasKeyword("return"));
    }

    @Test
    public void occursOn_todo_neverMatches() {
        assertFalse(new Todo("read book").occursOn(LocalDate.of(2019, 6, 6)));
    }

    @Test
    public void occursOn_deadlineOnThatDay_matchesRegardlessOfTime() throws BenjaminException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-06-06 1800"));

        assertTrue(deadline.occursOn(LocalDate.of(2019, 6, 6)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 6, 7)));
    }

    @Test
    public void occursOn_dayWithinEvent_matchesInclusively() throws BenjaminException {
        Event event = new Event("camp",
                TaskDateTime.parse("2019-08-06"),
                TaskDateTime.parse("2019-08-08"));

        assertTrue(event.occursOn(LocalDate.of(2019, 8, 6)));
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 7)));
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 8)));
        assertFalse(event.occursOn(LocalDate.of(2019, 8, 5)));
        assertFalse(event.occursOn(LocalDate.of(2019, 8, 9)));
    }
}
