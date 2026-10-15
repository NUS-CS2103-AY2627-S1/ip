package carlo.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import carlo.exception.CarloException;

/**
 * Tests for {@link Event#occursOn(LocalDate)}.
 */
class EventTest {

    @Test
    void occursOn_dateWithinRange_returnsTrue() throws CarloException {
        Event event = new Event("trip", "2019-12-01", "2019-12-05");
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    void occursOn_dateEqualsStart_returnsTrue() throws CarloException {
        Event event = new Event("trip", "2019-12-01", "2019-12-05");
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    void occursOn_dateEqualsEnd_returnsTrue() throws CarloException {
        Event event = new Event("trip", "2019-12-01", "2019-12-05");
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    void occursOn_dateBeforeStart_returnsFalse() throws CarloException {
        Event event = new Event("trip", "2019-12-01", "2019-12-05");
        assertFalse(event.occursOn(LocalDate.of(2019, 11, 30)));
    }

    @Test
    void occursOn_dateAfterEnd_returnsFalse() throws CarloException {
        Event event = new Event("trip", "2019-12-01", "2019-12-05");
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 6)));
    }

    @Test
    void constructor_unparseableStartTime_throwsCarloException() {
        assertThrows(CarloException.class, () -> new Event("trip", "whenever it starts", "2019-12-05"));
    }

    @Test
    void constructor_unparseableEndTime_throwsCarloException() {
        assertThrows(CarloException.class, () -> new Event("trip", "2019-12-01", "whenever it ends"));
    }

    @Test
    void constructor_neitherTimeParseable_throwsCarloException() {
        assertThrows(CarloException.class, () -> new Event("trip", "someday", "some other day"));
    }

    @Test
    void constructor_startTimeAfterEndTime_throwsCarloException() {
        assertThrows(CarloException.class, () -> new Event("trip", "2019-12-05", "2019-12-01"));
    }
}
