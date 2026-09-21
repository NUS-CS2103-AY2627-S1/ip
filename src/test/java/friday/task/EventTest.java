package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests interval validation and inclusive calendar-date matching, including midnight boundaries.
 */
class EventTest {
    @Test
    void constructor_overnightEvent_preservesBothDateTimes() {
        Event event = new Event("overnight", "2019-12-02 23:00", "2019-12-03 01:00");
        assertEquals(LocalDateTime.of(2019, 12, 2, 23, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 1, 0), event.getTo());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2019-12-01 12:00", "2019-12-02 11:59"})
    void constructor_endBeforeStart_throwsException(String end) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Event("backwards", "2019-12-02 12:00", end));
        assertEquals("An event cannot end before it starts.", error.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"Sunday,2019-12-02", "2019-12-02,2019-02-29"})
    void constructor_invalidEndpoint_throwsException(String start, String end) {
        assertThrows(IllegalArgumentException.class, () -> new Event("invalid", start, end));
    }

    @ParameterizedTest
    @CsvSource({"2019-11-30,false", "2019-12-01,true", "2019-12-02,true",
            "2019-12-03,true", "2019-12-04,false"})
    void occursOn_multiDayEvent_includesBothBoundaryDatesRegardlessOfStatus(String date, boolean isExpectedMatch) {
        Event event = new Event("conference", "2019-12-01 10:00", "2019-12-03 00:00");
        assertEquals(isExpectedMatch, event.occursOn(LocalDate.parse(date)));
        event.markAsDone();
        assertEquals(isExpectedMatch, event.occursOn(LocalDate.parse(date)));
    }

    @ParameterizedTest
    @CsvSource({"2019-12-01,false", "2019-12-02,true", "2019-12-03,false"})
    void occursOn_equalEndpoints_matchesOnlyTheSingleDate(String date, boolean isExpectedMatch) {
        Event event = new Event("reminder", "2019-12-02", "2019-12-02");
        assertEquals(event.getFrom(), event.getTo());
        assertEquals(isExpectedMatch, event.occursOn(LocalDate.parse(date)));
    }
}
