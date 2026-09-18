package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * TaskDate decides which dates Milo accepts at all, and both Deadline and
 * Event depend on it agreeing with itself, so its edges are worth pinning
 * down: the two accepted layouts, the date-only and date-and-time forms, and
 * the impossible dates a lenient parser would otherwise wave through.
 */
class TaskDateTest {
    @Test
    void parse_isoDateOnly_readsItWithoutATime() {
        Optional<TaskDate.Parsed> parsed = TaskDate.parse("2019-10-15");
        assertTrue(parsed.isPresent());
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), parsed.get().value());
        assertFalse(parsed.get().hasTime());
    }

    @Test
    void parse_isoDateWithTime_keepsTheTime() {
        Optional<TaskDate.Parsed> parsed = TaskDate.parse("2019-10-15 1800");
        assertTrue(parsed.isPresent());
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), parsed.get().value());
        assertTrue(parsed.get().hasTime());
    }

    @Test
    void parse_slashDateOnly_isAccepted() {
        Optional<TaskDate.Parsed> parsed = TaskDate.parse("2/12/2019");
        assertTrue(parsed.isPresent());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), parsed.get().value());
        assertFalse(parsed.get().hasTime());
    }

    @Test
    void parse_slashDateWithTime_isAccepted() {
        Optional<TaskDate.Parsed> parsed = TaskDate.parse("2/12/2019 1800");
        assertTrue(parsed.isPresent());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsed.get().value());
        assertTrue(parsed.get().hasTime());
    }

    @Test
    void parse_dateThatDoesNotExist_isRejected() {
        // A lenient parser would quietly move this to the 28th.
        assertTrue(TaskDate.parse("30/2/2019").isEmpty());
        assertTrue(TaskDate.parse("2019-02-30").isEmpty());
    }

    @Test
    void parse_impossibleTime_isRejected() {
        assertTrue(TaskDate.parse("2019-10-15 2500").isEmpty());
    }

    @Test
    void parse_freeText_isRejected() {
        assertTrue(TaskDate.parse("Mon 2pm").isEmpty());
        assertTrue(TaskDate.parse("").isEmpty());
        assertTrue(TaskDate.parse("tomorrow").isEmpty());
    }

    @Test
    void parse_dateOnlyFormatDoesNotSwallowATime() {
        // "2019-10-15" matches the front of "2019-10-15 1800"; the
        // time-carrying formats have to win or the time is silently lost.
        assertTrue(TaskDate.parse("2019-10-15 1800").get().hasTime());
    }
}
