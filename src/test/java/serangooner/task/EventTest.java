package serangooner.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import serangooner.SerangoonerException;

public class EventTest {
    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                new Event("demo", "2026-09-05", "2026-09-04"));
    }

    @Test
    public void constructor_endsBeforeStartOnTheSameDay_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                new Event("demo", "2026-09-05 1600", "2026-09-05 1400"));
    }

    @Test
    public void constructor_startAndEndAtTheSameTime_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                new Event("demo", "2026-09-05 1400", "2026-09-05 1400"));
    }

    @Test
    public void constructor_bareDateAndThatDateAtMidnight_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                new Event("demo", "2026-09-05", "2026-09-05 0000"));
    }

    @Test
    public void constructor_sameBareDateAtBothEnds_coversThatWholeDay() {
        Task event = new Event("demo", "2026-09-05", "2026-09-05");
        assertTrue(event.isWithin(LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void constructor_unreadableDate_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                new Event("demo", "sometime", "2026-09-05"));
    }

    @Test
    public void toSaveFormat_always_writesBothEndsBackTheWayTheyWereTyped() {
        assertEquals("E | 0 | demo | 2026-09-05 1400 | 2026-09-05 1600",
                new Event("demo", "2026-09-05 1400", "2026-09-05 1600").toSaveFormat());
    }

    @Test
    public void toString_always_showsBothEnds() {
        assertEquals("[E][ ] demo (from: 05 Sep 2026, 2:00PM to: 05 Sep 2026, 4:00PM)",
                new Event("demo", "2026-09-05 1400", "2026-09-05 1600").toString());
    }

    @Test
    public void isWithin_eventInsideTheRange_returnsTrue() {
        Task event = new Event("demo", "2026-09-02", "2026-09-03");
        assertTrue(event.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_eventSpanningTheWholeRange_returnsTrue() {
        Task event = new Event("camp", "2026-08-01", "2026-12-01");
        assertTrue(event.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_eventEndingOnTheFirstDayOfTheRange_returnsTrue() {
        Task event = new Event("camp", "2026-08-01", "2026-09-01");
        assertTrue(event.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_eventStartingOnTheLastDayOfTheRange_returnsTrue() {
        Task event = new Event("camp", "2026-09-05", "2026-10-01");
        assertTrue(event.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_eventEndingTheDayBeforeTheRange_returnsFalse() {
        Task event = new Event("camp", "2026-08-01", "2026-08-31");
        assertFalse(event.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_eventStartingTheDayAfterTheRange_returnsFalse() {
        Task event = new Event("camp", "2026-09-06", "2026-09-10");
        assertFalse(event.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void parseSaveFields_validLine_returnsEvent() {
        Optional<Task> task = Event.parseSaveFields(
                new String[] {"E", "0", "demo", "2026-09-05 1400", "2026-09-05 1600"});
        assertTrue(task.isPresent());
        assertEquals("[E][ ] demo (from: 05 Sep 2026, 2:00PM to: 05 Sep 2026, 4:00PM)",
                task.get().toString());
    }

    @Test
    public void parseSaveFields_endBeforeStart_returnsNothing() {
        assertTrue(Event.parseSaveFields(
                new String[] {"E", "0", "demo", "2026-09-05", "2026-09-04"}).isEmpty());
    }

    @Test
    public void parseSaveFields_missingEndField_returnsNothing() {
        assertTrue(Event.parseSaveFields(
                new String[] {"E", "0", "demo", "2026-09-05"}).isEmpty());
    }

    @Test
    public void getTypeName_always_namesTheKindForMessages() {
        assertEquals("event", new Event("demo", "2026-09-05", "2026-09-06").getTypeName());
    }

    @Test
    public void toSaveFormat_done_writesTheDoneFlag() {
        Task event = new Event("demo", "2026-09-05 1400", "2026-09-05 1600");
        event.markDone();
        assertEquals("E | 1 | demo | 2026-09-05 1400 | 2026-09-05 1600", event.toSaveFormat());
    }

    @Test
    public void isWithin_singleDayEventOnTheOnlyDayOfTheRange_returnsTrue() {
        Task event = new Event("demo", "2026-09-05", "2026-09-05");
        assertTrue(event.isWithin(LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isDuplicateOf_sameDescriptionAndTimes_returnsTrue() {
        assertTrue(new Event("Orbital", "2026-09-03", "2026-09-04")
                .isDuplicateOf(new Event("orbital", "2026-09-03", "2026-09-04")));
    }

    @Test
    public void isDuplicateOf_differentStart_returnsFalse() {
        assertFalse(new Event("orbital", "2026-09-03", "2026-09-04")
                .isDuplicateOf(new Event("orbital", "2026-09-02", "2026-09-04")));
    }

    @Test
    public void isDuplicateOf_differentEnd_returnsFalse() {
        assertFalse(new Event("orbital", "2026-09-03", "2026-09-04")
                .isDuplicateOf(new Event("orbital", "2026-09-03", "2026-09-05")));
    }

    @Test
    public void isDuplicateOf_deadlineWithTheSameDescriptionAndStart_returnsFalse() {
        assertFalse(new Event("orbital", "2026-09-03", "2026-09-03")
                .isDuplicateOf(new Deadline("orbital", "2026-09-03")));
    }

    @Test
    public void isDuplicateOf_differentDescriptionSameTimes_returnsFalse() {
        assertFalse(new Event("orbital demo", "2026-09-05 1400", "2026-09-05 1600")
                .isDuplicateOf(new Event("hackathon", "2026-09-05 1400", "2026-09-05 1600")));
    }
}
