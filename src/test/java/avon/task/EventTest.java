package avon.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {
    private static final LocalDateTime EVENT_TIME = LocalDateTime.of(2026, 8, 20, 14, 0);

    @Test
    void constructor_sameStartAndEnd_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event("lecture", EVENT_TIME, EVENT_TIME));
    }

    @Test
    void constructor_endBeforeStart_throwsIllegalArgumentException() {
        LocalDateTime earlierTime = EVENT_TIME.minusHours(1);

        assertThrows(IllegalArgumentException.class, () -> new Event("lecture", EVENT_TIME, earlierTime));
    }

    @Test
    void constructors_nullDateTime_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Deadline("essay", null));
        assertThrows(NullPointerException.class, () -> new Event("lecture", null, EVENT_TIME));
        assertThrows(NullPointerException.class, () -> new Event("lecture", EVENT_TIME, null));
    }
}
