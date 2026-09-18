package chud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class EventTest {
    private final Event event = new Event("project meeting",
            TaskDateTime.parse("2019-12-02"), TaskDateTime.parse("2019-12-04"));

    @Test
    void occursOn_dateBeforeFrom_returnsFalse() {
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    void occursOn_dateEqualsFrom_returnsTrue() {
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void occursOn_dateBetweenFromAndTo_returnsTrue() {
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    void occursOn_dateEqualsTo_returnsTrue() {
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    void occursOn_dateAfterTo_returnsFalse() {
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    void occursOn_singleDayEvent_matchesOnlyThatDate() {
        Event singleDay = new Event("standup", TaskDateTime.parse("2019-12-02 0900"),
                TaskDateTime.parse("2019-12-02 0930"));

        assertTrue(singleDay.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(singleDay.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    void toString_includesTagStatusDescriptionFromAndTo() {
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)", event.toString());
    }

    @Test
    void toFileString_encodesTagDoneFlagDescriptionFromAndTo() {
        assertEquals("E | 0 | project meeting | 2019-12-02 | 2019-12-04", event.toFileString());
    }

    @Test
    void getSortDate_returnsFromDateNotToDate() {
        assertEquals(LocalDate.of(2019, 12, 2), event.getSortDate());
    }
}
