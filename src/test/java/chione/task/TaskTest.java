package chione.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the task types and the two renderings every task has to provide: the one
 * the user reads, and the one written to the save file.
 *
 * <p>The two are checked separately for each type, because they are produced by
 * separate methods and only one of them has to survive being read back.
 */
public class TaskTest {
    private static final LocalDateTime SIX_PM = LocalDateTime.of(2019, 10, 15, 18, 0);
    private static final LocalDateTime MIDNIGHT = LocalDateTime.of(2019, 10, 15, 0, 0);

    @Test
    public void toString_newTodo_shownAsNotDone() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void markAsDone_todo_statusBecomesX() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void markAsNotDone_doneTodo_statusClearedAgain() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void getStatusIcon_notDone_singleSpace() {
        // A space rather than an empty string, so the brackets stay the same width
        // on every line of a listing.
        assertEquals(" ", new Todo("read book").getStatusIcon());
    }

    @Test
    public void toSaveFormat_notDoneTodo_zeroFlag() {
        assertEquals("T | 0 | read book", new Todo("read book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneTodo_oneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }

    @Test
    public void toString_deadlineWithTime_timeShown() {
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00pm)",
                new Deadline("return book", SIX_PM).toString());
    }

    @Test
    public void toString_deadlineAtMidnight_dateOnly() {
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                new Deadline("return book", MIDNIGHT).toString());
    }

    @Test
    public void toSaveFormat_deadline_dateWrittenInFull() {
        assertEquals("D | 0 | return book | 2019-10-15 1800",
                new Deadline("return book", SIX_PM).toSaveFormat());
    }

    @Test
    public void toString_event_bothTimesShown() {
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 17 2019, 4:00pm)",
                new Event("meeting",
                        LocalDateTime.of(2019, 10, 15, 14, 0),
                        LocalDateTime.of(2019, 10, 17, 16, 0)).toString());
    }

    @Test
    public void toSaveFormat_event_bothTimesWritten() {
        assertEquals("E | 0 | meeting | 2019-10-15 1400 | 2019-10-17 1600",
                new Event("meeting",
                        LocalDateTime.of(2019, 10, 15, 14, 0),
                        LocalDateTime.of(2019, 10, 17, 16, 0)).toSaveFormat());
    }

    @Test
    public void hasKeyword_wholeWordInDescription_true() {
        assertTrue(new Todo("read book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_differentCase_true() {
        // Searching should not depend on how the user happened to capitalise it.
        assertTrue(new Todo("read book").hasKeyword("BOOK"));
        assertTrue(new Todo("Read Book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_partOfAWord_true() {
        // The search is over the text, not over whole words.
        assertTrue(new Todo("read book").hasKeyword("oo"));
    }

    @Test
    public void hasKeyword_wordNotInDescription_false() {
        assertFalse(new Todo("read book").hasKeyword("essay"));
    }

    @Test
    public void hasKeyword_wordOnlyInTheDate_false() {
        // Only the description is searched, so the rendered date is not matched.
        assertFalse(new Deadline("return book", SIX_PM).hasKeyword("Oct"));
    }

    @Test
    public void occursOn_todo_alwaysFalse() {
        assertFalse(new Todo("read book").occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_deadlineSameDay_true() {
        // The time of day is not part of the question being asked.
        assertTrue(new Deadline("return book", SIX_PM).occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_deadlineDayBefore_false() {
        assertFalse(new Deadline("return book", SIX_PM).occursOn(LocalDate.of(2019, 10, 14)));
    }

    @Test
    public void occursOn_eventFirstDay_true() {
        assertTrue(buildThreeDayEvent().occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_eventMiddleDay_true() {
        assertTrue(buildThreeDayEvent().occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void occursOn_eventLastDay_true() {
        // Both ends of the range count, so the day it finishes is a day it runs.
        assertTrue(buildThreeDayEvent().occursOn(LocalDate.of(2019, 10, 17)));
    }

    @Test
    public void occursOn_eventDayAfter_false() {
        assertFalse(buildThreeDayEvent().occursOn(LocalDate.of(2019, 10, 18)));
    }

    @Test
    public void occursOn_eventDayBefore_false() {
        assertFalse(buildThreeDayEvent().occursOn(LocalDate.of(2019, 10, 14)));
    }

    @Test
    public void isDuplicateOf_sameDescriptionDifferentCase_true() {
        assertTrue(new Todo("Read Book").isDuplicateOf(new Todo("read book")));
    }

    @Test
    public void isDuplicateOf_differentDescription_false() {
        assertFalse(new Todo("read book").isDuplicateOf(new Todo("return book")));
    }

    @Test
    public void isDuplicateOf_todoAndDeadlineSameDescription_false() {
        // Same words, different kind of task: not a repeat.
        assertFalse(new Todo("report").isDuplicateOf(new Deadline("report", SIX_PM)));
    }

    @Test
    public void isDuplicateOf_oneTaskDone_true() {
        // Finishing a task and adding it again is still adding it again.
        Todo done = new Todo("read book");
        done.markAsDone();
        assertTrue(new Todo("read book").isDuplicateOf(done));
    }

    @Test
    public void isDuplicateOf_deadlinesSameMoment_true() {
        assertTrue(new Deadline("report", SIX_PM).isDuplicateOf(new Deadline("report", SIX_PM)));
    }

    @Test
    public void isDuplicateOf_deadlinesDifferentMoments_false() {
        assertFalse(new Deadline("report", SIX_PM).isDuplicateOf(new Deadline("report", MIDNIGHT)));
    }

    @Test
    public void isDuplicateOf_eventsSameMoments_true() {
        assertTrue(buildThreeDayEvent().isDuplicateOf(buildThreeDayEvent()));
    }

    @Test
    public void isDuplicateOf_eventsDifferentEndTime_false() {
        Event shorter = new Event("meeting",
                LocalDateTime.of(2019, 10, 15, 14, 0),
                LocalDateTime.of(2019, 10, 16, 16, 0));
        assertFalse(buildThreeDayEvent().isDuplicateOf(shorter));
    }

    @Test
    public void isDuplicateOf_eventsDifferentStartTime_false() {
        Event later = new Event("meeting",
                LocalDateTime.of(2019, 10, 16, 14, 0),
                LocalDateTime.of(2019, 10, 17, 16, 0));
        assertFalse(buildThreeDayEvent().isDuplicateOf(later));
    }

    @Test
    public void isDuplicateOf_deadlineAndEventSameDescription_false() {
        // Called both ways round: each override casts, relying on the class
        // check in Task to have refused first.
        Deadline deadline = new Deadline("report", SIX_PM);
        Event event = new Event("report", SIX_PM, LocalDateTime.of(2019, 10, 15, 20, 0));
        assertFalse(deadline.isDuplicateOf(event));
        assertFalse(event.isDuplicateOf(deadline));
    }

    /** An event running from the 15th to the 17th of October 2019. */
    private Event buildThreeDayEvent() {
        return new Event("meeting",
                LocalDateTime.of(2019, 10, 15, 14, 0),
                LocalDateTime.of(2019, 10, 17, 16, 0));
    }
}
