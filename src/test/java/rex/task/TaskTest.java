package rex.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests what the kinds of task have in common and what makes them differ: the
 * done flag, the letter and details each kind shows in a listing, the line
 * each writes to the save file, and which days each falls on.
 *
 * The days are the part worth the most care. An event and a within-period task
 * both count every day from one end of their window to the other, ends
 * included, so the tests below sit on those ends rather than only in the
 * middle: an off-by-one there would lose a task from the day it is actually
 * due, while every test placed in the middle still passed.
 *
 * Test methods are deliberately not public, for the reason given in
 * TaskDateTimeTest.
 */
public class TaskTest {
    private static final TaskDateTime OCT_15 = TaskDateTime.parse("2019-10-15");
    private static final TaskDateTime OCT_18 = TaskDateTime.parse("2019-10-18");

    @Test
    void getStatusIcon_newTask_blank() {
        assertEquals(" ", new ToDo("read book").getStatusIcon());
    }

    @Test
    void markAsDone_thenNotDone_iconFollows() {
        Task task = new ToDo("read book");

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void getTypeIcon_eachKind_ownLetter() {
        assertEquals("T", new ToDo("read book").getTypeIcon());
        assertEquals("D", new Deadline("return book", OCT_15).getTypeIcon());
        assertEquals("E", new Event("retreat", OCT_15, OCT_18).getTypeIcon());
        assertEquals("W", new WithinPeriod("collect", OCT_15, OCT_18).getTypeIcon());
    }

    @Test
    void getDetails_todo_nothingToAdd() {
        assertEquals("", new ToDo("read book").getDetails());
    }

    @Test
    void getDetails_datedKinds_ownWording() {
        // An event and a within-period task carry the same two dates, so the
        // wording is the only thing telling a reader which kind a listing is
        // showing. That makes it worth pinning down.
        assertEquals(" (by: Oct 15 2019)", new Deadline("return book", OCT_15).getDetails());
        assertEquals(" (from: Oct 15 2019 to: Oct 18 2019)",
                new Event("retreat", OCT_15, OCT_18).getDetails());
        assertEquals(" (within: Oct 15 2019 to Oct 18 2019)",
                new WithinPeriod("collect", OCT_15, OCT_18).getDetails());
    }

    @Test
    void toSaveFormat_eachKind_typeFlagDescriptionThenDates() {
        assertEquals("T | 0 | read book", new ToDo("read book").toSaveFormat());
        assertEquals("D | 0 | return book | 2019-10-15",
                new Deadline("return book", OCT_15).toSaveFormat());
        assertEquals("E | 0 | retreat | 2019-10-15 | 2019-10-18",
                new Event("retreat", OCT_15, OCT_18).toSaveFormat());
        assertEquals("W | 0 | collect | 2019-10-15 | 2019-10-18",
                new WithinPeriod("collect", OCT_15, OCT_18).toSaveFormat());
    }

    @Test
    void toSaveFormat_doneTask_flagIsOne() {
        Task task = new Deadline("return book", OCT_15);

        task.markAsDone();

        assertEquals("D | 1 | return book | 2019-10-15", task.toSaveFormat());
    }

    @Test
    void toSaveFormat_dateCarryingATime_timeKept() {
        // The save format keeps the time so the task can be read back as it
        // was, even though a listing may show the date on its own.
        Deadline deadline = new Deadline("submit report", TaskDateTime.parse("2019-10-15 1800"));

        assertEquals("D | 0 | submit report | 2019-10-15 1800", deadline.toSaveFormat());
    }

    @Test
    void isOn_todo_neverOnAnyDay() {
        assertFalse(new ToDo("read book").isOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void isOn_deadline_onlyTheDayItIsDue() {
        Deadline deadline = new Deadline("return book", OCT_15);

        assertTrue(deadline.isOn(LocalDate.of(2019, 10, 15)));
        assertFalse(deadline.isOn(LocalDate.of(2019, 10, 14)));
        assertFalse(deadline.isOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    void isOn_deadlineWithATime_stillTheWholeDay() {
        // A time of day narrows when the task is due but not which day it falls
        // on, so a search for that day has to find it whatever the time says.
        Deadline deadline = new Deadline("submit report", TaskDateTime.parse("2019-10-15 1800"));

        assertTrue(deadline.isOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void isOn_event_everyDayItCoversIncludingBothEnds() {
        Event event = new Event("retreat", OCT_15, OCT_18);

        assertTrue(event.isOn(LocalDate.of(2019, 10, 15)), "The first day is part of the event");
        assertTrue(event.isOn(LocalDate.of(2019, 10, 16)));
        assertTrue(event.isOn(LocalDate.of(2019, 10, 18)), "The last day is part of the event");
        assertFalse(event.isOn(LocalDate.of(2019, 10, 14)));
        assertFalse(event.isOn(LocalDate.of(2019, 10, 19)));
    }

    @Test
    void isOn_withinPeriod_everyDayOfTheWindowIncludingBothEnds() {
        WithinPeriod within = new WithinPeriod("collect certificate", OCT_15, OCT_18);

        assertTrue(within.isOn(LocalDate.of(2019, 10, 15)), "The first day is part of the window");
        assertTrue(within.isOn(LocalDate.of(2019, 10, 16)));
        assertTrue(within.isOn(LocalDate.of(2019, 10, 18)), "The last day is part of the window");
        assertFalse(within.isOn(LocalDate.of(2019, 10, 14)));
        assertFalse(within.isOn(LocalDate.of(2019, 10, 19)));
    }

    @Test
    void isOn_windowOfOneDay_thatDayOnly() {
        WithinPeriod within = new WithinPeriod("collect certificate", OCT_15, OCT_15);

        assertTrue(within.isOn(LocalDate.of(2019, 10, 15)));
        assertFalse(within.isOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    void getDescription_taskBuilt_readBackUnchanged() {
        assertEquals("read book", new ToDo("read book").getDescription());
    }
}
