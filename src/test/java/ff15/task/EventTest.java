package ff15.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/**
 * Tests how an {@link Event} displays itself, saves itself, and answers whether it
 * is running during a queried span of dates.
 *
 * <p>Unlike a deadline, an event covers a stretch of days, so a query matches when
 * the two spans share any day at all, not only when the event starts or ends on
 * the day asked about.
 */
public class EventTest {

    private static Event event(String description, String from, String to) throws FF15Exception {
        return new Event(description, TaskTime.parse(from), TaskTime.parse(to));
    }

    @Test
    public void toString_datesAndTimes_showsFromAndTo() throws FF15Exception {
        assertEquals("[E][ ] project meeting (from: Dec 05 2019, 2:00pm to: Dec 05 2019, 4:00pm)",
                event("project meeting", "2019-12-05 1400", "2019-12-05 1600").toString());
    }

    @Test
    public void toString_datesOnly_showsNoTimes() throws FF15Exception {
        assertEquals("[E][ ] holiday (from: Dec 20 2019 to: Dec 26 2019)",
                event("holiday", "2019-12-20", "2019-12-26").toString());
    }

    @Test
    public void toString_doneEvent_showsCrossInStatusBox() throws FF15Exception {
        Event task = event("holiday", "2019-12-20", "2019-12-26");
        task.markAsDone();
        assertEquals("[E][X] holiday (from: Dec 20 2019 to: Dec 26 2019)", task.toString());
    }

    @Test
    public void toFileFormat_datesAndTimes_writesBothInInputForm() throws FF15Exception {
        assertEquals("E | 0 | project meeting | 2019-12-05 1400 | 2019-12-05 1600",
                event("project meeting", "2019-12-05 1400", "2019-12-05 1600").toFileFormat());
    }

    @Test
    public void toFileFormat_doneEventWithDatesOnly_writesOneForDone() throws FF15Exception {
        Event task = event("holiday", "2019-12-20", "2019-12-26");
        task.markAsDone();
        assertEquals("E | 1 | holiday | 2019-12-20 | 2019-12-26", task.toFileFormat());
    }

    @Test
    public void occursIn_dayInTheMiddleOfTheEvent_returnsTrue() throws FF15Exception {
        assertTrue(event("holiday", "2019-12-20", "2019-12-26").occursIn(DateRange.parse("2019-12-23")));
    }

    @Test
    public void occursIn_firstAndLastDayOfTheEvent_returnsTrue() throws FF15Exception {
        Event task = event("holiday", "2019-12-20", "2019-12-26");
        assertTrue(task.occursIn(DateRange.parse("2019-12-20")));
        assertTrue(task.occursIn(DateRange.parse("2019-12-26")));
    }

    @Test
    public void occursIn_dayJustOutsideTheEvent_returnsFalse() throws FF15Exception {
        Event task = event("holiday", "2019-12-20", "2019-12-26");
        assertFalse(task.occursIn(DateRange.parse("2019-12-19")));
        assertFalse(task.occursIn(DateRange.parse("2019-12-27")));
    }

    @Test
    public void occursIn_eventSpanningTheEndOfAMonth_matchesBothMonths() throws FF15Exception {
        Event task = event("holiday", "2019-12-28", "2020-01-03");
        assertTrue(task.occursIn(DateRange.parse("2019-12")));
        assertTrue(task.occursIn(DateRange.parse("2020-01")));
        assertTrue(task.occursIn(DateRange.parse("2019")));
        assertTrue(task.occursIn(DateRange.parse("2020")));
        assertFalse(task.occursIn(DateRange.parse("2019-11")));
        assertFalse(task.occursIn(DateRange.parse("2020-02")));
    }

    @Test
    public void occursIn_singleDayEvent_matchesOnlyThatDay() throws FF15Exception {
        Event task = event("project meeting", "2019-12-05 1400", "2019-12-05 1600");
        assertTrue(task.occursIn(DateRange.parse("2019-12-05")));
        assertFalse(task.occursIn(DateRange.parse("2019-12-04")));
        assertFalse(task.occursIn(DateRange.parse("2019-12-06")));
    }

    @Test
    public void isSameAs_sameDescriptionAndSpan_isTrue() throws FF15Exception {
        Event a = new Event("meeting", TaskTime.parse("2019-12-05 1400"), TaskTime.parse("2019-12-05 1600"));
        Event b = new Event("meeting", TaskTime.parse("2019-12-05 1400"), TaskTime.parse("2019-12-05 1600"));
        assertTrue(a.isSameAs(b));
    }

    @Test
    public void isSameAs_sameDescriptionDifferentEnd_isFalse() throws FF15Exception {
        Event a = new Event("meeting", TaskTime.parse("2019-12-05 1400"), TaskTime.parse("2019-12-05 1600"));
        Event b = new Event("meeting", TaskTime.parse("2019-12-05 1400"), TaskTime.parse("2019-12-05 1700"));
        assertFalse(a.isSameAs(b));
    }

    @Test
    public void isSameAs_differentDescriptionSameSpan_isFalse() throws FF15Exception {
        Event a = new Event("meeting", TaskTime.parse("2019-12-05"), TaskTime.parse("2019-12-06"));
        Event b = new Event("the meeting", TaskTime.parse("2019-12-05"), TaskTime.parse("2019-12-06"));
        assertFalse(a.isSameAs(b));
    }

    @Test
    public void isSameAs_taskOfAnotherKind_isFalseRatherThanCrashing() throws FF15Exception {
        Event event = new Event("meeting", TaskTime.parse("2019-12-05"), TaskTime.parse("2019-12-07"));
        assertFalse(event.isSameAs(new Todo("meeting")));
        assertFalse(event.isSameAs(new Deadline("meeting", TaskTime.parse("2019-12-07"))));
    }

    @Test
    public void isSameAs_sameDescriptionDifferentStart_isFalse() throws FF15Exception {
        Event a = new Event("meeting", TaskTime.parse("2019-12-05"), TaskTime.parse("2019-12-07"));
        Event b = new Event("meeting", TaskTime.parse("2019-12-06"), TaskTime.parse("2019-12-07"));
        assertFalse(a.isSameAs(b));
    }
}
