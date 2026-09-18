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
        assertEquals("[D][ ] return book (by: 15 Oct 2019)",
                new Deadline("return book", "2019-10-15").toString());
        assertEquals("[D][ ] return book (by: 02 Dec 2019 6:00PM)",
                new Deadline("return book", "2/12/2019 1800").toString());
    }

    @Test
    public void toString_event_showsBothEnds() throws BibiException {
        assertEquals("[E][ ] camp (from: 10 Aug 2019 to: 12 Aug 2019)",
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
    public void occursOn_overnightEventWithNewDateFormats_matchesBothDays() throws BibiException {
        Event event = new Event("night shift", "21 Dec 2026 11:30 pm", "22/12/26 12:30 am");

        assertFalse(event.occursOn(LocalDate.of(2026, 12, 20)));
        assertTrue(event.occursOn(LocalDate.of(2026, 12, 21)));
        assertTrue(event.occursOn(LocalDate.of(2026, 12, 22)));
        assertFalse(event.occursOn(LocalDate.of(2026, 12, 23)));
        assertEquals("[E][ ] night shift (from: 21 Dec 2026 11:30PM to: 22 Dec 2026 12:30AM)",
                event.toString());
    }

    @Test
    public void hasKeyword_wordInTheDescription_matches() throws BibiException {
        assertTrue(new Todo("read book").hasKeyword("book"));
        assertTrue(new Todo("read book").hasKeyword("read"));
    }

    @Test
    public void hasKeyword_differentCase_stillMatches() throws BibiException {
        assertTrue(new Todo("Read Book").hasKeyword("book"));
        assertTrue(new Todo("read book").hasKeyword("BOOK"));
    }

    @Test
    public void hasKeyword_partOfAWord_matches() throws BibiException {
        // Searching is a plain substring match, so a prefix finds the word.
        assertTrue(new Todo("read book").hasKeyword("boo"));
    }

    @Test
    public void hasKeyword_absentWord_doesNotMatch() throws BibiException {
        assertFalse(new Todo("read book").hasKeyword("essay"));
    }

    @Test
    public void hasKeyword_textOutsideTheDescription_doesNotMatch() throws BibiException {
        // Only the description is searched, not the dates or the type code.
        Deadline deadline = new Deadline("return book", "2019-06-06");
        assertFalse(deadline.hasKeyword("2019"));
        assertFalse(deadline.hasKeyword("Jun"));
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
        assertEquals("An event cannot end before it starts. "
                + "Use event <description> /from <start> /to <end>, "
                + "for example: event study group /from 21/12/2026 1800 /to 21/12/2026 2000.",
                thrown.getMessage());
    }

    @Test
    public void newEvent_startingAndEndingAtTheSameMoment_allowed() throws BibiException {
        assertEquals("[E][ ] camp (from: 10 Aug 2019 to: 10 Aug 2019)",
                new Event("camp", "2019-08-10", "2019-08-10").toString());
    }

    @Test
    public void describeAddition_eachType_hasItsOwnWording() throws BibiException {
        assertEquals("Added. Do or do not. There is no try:",
                new Todo("read book").describeAddition());
        assertEquals("Added. Remembering it is my job. Doing it is still yours:",
                new Deadline("return book", "2019-06-06").describeAddition());
        assertEquals("Added. It starts on time. Be there or be square:",
                new Event("camp", "2019-08-10", "2019-08-12").describeAddition());
    }

    @Test
    public void describeAddition_everyType_endsWithAColon() throws BibiException {
        // The task itself is printed on the line below, so a line that trailed
        // off with a full stop would read as though nothing followed it.
        for (Task task : new Task[] {new Todo("read book"),
            new Deadline("return book", "2019-06-06"),
            new Event("camp", "2019-08-10", "2019-08-12")}) {
            assertTrue(task.describeAddition().endsWith(":"),
                    "no colon on: " + task.describeAddition());
        }
    }
}
