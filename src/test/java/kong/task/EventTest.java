package kong.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EventTest {
    @ParameterizedTest
    @CsvSource({
        "2019-10-13, false",
        "2019-10-14, true",
        "2019-10-15, true",
        "2019-10-16, true",
        "2019-10-17, false"
    })
    void occursOnReturnsWhetherDateIsInRangeForMultiDayEvent(String dateText, boolean expected) {
        Event event = new Event("conference", LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));

        assertEquals(expected, event.occursOn(LocalDate.parse(dateText)));
    }

    @ParameterizedTest
    @CsvSource({
        "2019-10-14, false",
        "2019-10-15, true",
        "2019-10-16, false"
    })
    void occursOnReturnsWhetherDateMatchesOneDayEvent(String dateText, boolean expected) {
        Event event = new Event("workshop", LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-15"));

        assertEquals(expected, event.occursOn(LocalDate.parse(dateText)));
    }

    @ParameterizedTest
    @CsvSource({
        "false, [E][ ] conference (from: Oct 14 2019 to: Oct 16 2019), "
                + "E | 0 | conference | 2019-10-14 | 2019-10-16",
        "true, [E][X] conference (from: Oct 14 2019 to: Oct 16 2019), "
                + "E | 1 | conference | 2019-10-14 | 2019-10-16"
    })
    void displayAndStorageFormatsShowDoneStatusConsistently(boolean isDone, String expectedDisplay,
            String expectedFileText) {
        Event event = new Event("conference", isDone, LocalDate.parse("2019-10-14"),
                LocalDate.parse("2019-10-16"));

        assertEquals(expectedDisplay, event.toString());
        assertEquals(expectedFileText, event.toFileString());
    }

    @Test
    void getSortDate_returnsStartDate() {
        Event event = new Event("conference", LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));

        assertEquals(Optional.of(LocalDate.parse("2019-10-14")), event.getSortDate());
    }

    @Test
    void isSameTask_sameDescriptionAndDates_returnsTrueIgnoringCase() {
        Event first = new Event("conference", LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));
        Event second = new Event("CONFERENCE", true, LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));
        Event differentFrom = new Event("conference", LocalDate.parse("2019-10-13"), LocalDate.parse("2019-10-16"));
        Event differentTo = new Event("conference", LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-17"));

        assertTrue(first.isSameTask(second));
        assertFalse(first.isSameTask(differentFrom));
        assertFalse(first.isSameTask(differentTo));
        assertFalse(first.isSameTask(null));
        assertFalse(first.isSameTask(new Todo("conference")));
    }

    @Test
    void equalsAndHashCode_contract_validatesEquality() {
        Event event1 = new Event("conference", false, LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));
        Event event2 = new Event("conference", false, LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));
        Event eventDone = new Event("conference", true, LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16"));
        Event eventDiffDate = new Event("conference", false, LocalDate.parse("2019-10-15"),
                LocalDate.parse("2019-10-16"));

        assertEquals(event1, event1);
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());

        assertNotEquals(event1, eventDone);
        assertNotEquals(event1, eventDiffDate);
        assertNotEquals(event1, null);
        assertNotEquals(event1, "other");
    }
}
