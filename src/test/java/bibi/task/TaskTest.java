package bibi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bibi.BibiException;

/**
 * Tests the display form, save form, date matching, and validation shared by
 * the three task types.
 */
public class TaskTest {

    @Test
    public void toString_todo_showsTypeAndStatus() throws BibiException {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());

        todo.markComplete();
        assertEquals("[T][X] read book", todo.toString());

        todo.markIncomplete();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_deadlineWithAndWithoutTime_showsTheDate() throws BibiException {
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                new Deadline("return book", "2019-10-15").toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00PM)",
                new Deadline("return book", "2/12/2019 1800").toString());
    }

    @Test
    public void toString_event_showsBothEnds() throws BibiException {
        assertEquals("[E][ ] camp (from: Aug 10 2019 to: Aug 12 2019)",
                new Event("camp", "2019-08-10", "2019-08-12").toString());
    }

    @Test
    public void toFileFormat_eachType_matchesTheSavedLayout() throws BibiException {
        Todo todo = new Todo("read book");
        todo.markComplete();

        assertEquals("T | 1 | read book", todo.toFileFormat());
        assertEquals("D | 0 | return book | 2019-06-06",
                new Deadline("return book", "2019-06-06").toFileFormat());
        assertEquals("E | 0 | camp | 2019-08-10 | 2019-08-12",
                new Event("camp", "2019-08-10", "2019-08-12").toFileFormat());
    }

    @Test
    public void occursOn_todo_neverMatches() throws BibiException {
        assertFalse(new Todo("read book").occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_deadline_matchesOnlyItsOwnDate() throws BibiException {
        Deadline deadline = new Deadline("return book", "2019-10-15 1800");

        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 14)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void occursOn_multiDayEvent_matchesEveryDayItRuns() throws BibiException {
        Event event = new Event("camp", "2019-08-10", "2019-08-12");

        assertFalse(event.occursOn(LocalDate.of(2019, 8, 9)));
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 10)));
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 11)));
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 12)));
        assertFalse(event.occursOn(LocalDate.of(2019, 8, 13)));
    }

    @Test
    public void newTask_blankDescription_exceptionThrown() {
        assertThrows(BibiException.class, () -> new Todo(""));
        assertThrows(BibiException.class, () -> new Todo("   "));
        assertThrows(BibiException.class, () -> new Todo(null));
        assertThrows(BibiException.class, () -> new Deadline("", "2019-10-15"));
        assertThrows(BibiException.class, () -> new Event("", "2019-08-10", "2019-08-12"));
    }

    @Test
    public void newTask_missingDate_exceptionThrown() {
        assertThrows(BibiException.class, () -> new Deadline("return book", ""));
        assertThrows(BibiException.class, () -> new Deadline("return book", null));
        assertThrows(BibiException.class, () -> new Event("camp", "", "2019-08-12"));
        assertThrows(BibiException.class, () -> new Event("camp", "2019-08-10", null));
    }

    @Test
    public void newTask_textContainingTheFieldSeparator_exceptionThrown() {
        // Such text would split into the wrong fields when the file is read back.
        BibiException thrown = assertThrows(BibiException.class, () -> new Todo("read | book"));
        assertTrue(thrown.getMessage().contains(Task.FIELD_SEPARATOR));
    }

    @Test
    public void newEvent_endingBeforeItStarts_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () ->
                new Event("camp", "2019-08-12", "2019-08-10"));
        assertEquals("An event cannot end before it starts.", thrown.getMessage());
    }

    @Test
    public void newEvent_startingAndEndingAtTheSameMoment_allowed() throws BibiException {
        assertEquals("[E][ ] camp (from: Aug 10 2019 to: Aug 10 2019)",
                new Event("camp", "2019-08-10", "2019-08-10").toString());
    }
}
