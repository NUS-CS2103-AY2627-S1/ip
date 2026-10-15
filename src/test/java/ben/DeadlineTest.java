package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests for {@link Deadline}'s date/time parsing, display, and round-tripping. */
class DeadlineTest {
    @Test
    void toString_isoDateNoTime_showsReadableDate() throws BenException {
        Deadline d = new Deadline("return book", "2019-12-02");
        assertEquals("[D][ ] return book (by: Dec 2 2019)", d.toString());
    }

    @Test
    void constructor_dayMonthYearFormat_alsoAccepted() throws BenException {
        Deadline d = new Deadline("x", "2/12/2019");
        assertEquals("[D][ ] x (by: Dec 2 2019)", d.toString());
    }

    @Test
    void toString_dateWithTime_appendsTime() throws BenException {
        Deadline d = new Deadline("return book", "2019-12-02 1800");
        assertTrue(d.toString().startsWith("[D][ ] return book (by: Dec 2 2019, "),
                d.toString());
        assertTrue(d.toString().contains("6:00"), d.toString());
    }

    @Test
    void constructor_unparseableDate_throwsBenException() {
        assertThrows(BenException.class, () -> new Deadline("x", "someday"));
    }

    @Test
    void constructor_invalidTime_throwsBenException() {
        assertThrows(BenException.class, () -> new Deadline("x", "2019-12-02 2599"));
    }

    @Test
    void constructor_nonExistentCalendarDate_throwsBenException() {
        // Feb 30 does not exist; a lenient (smart) resolver would silently
        // roll this over to Feb 28 instead of rejecting it.
        assertThrows(BenException.class, () -> new Deadline("x", "2019-2-30"));
    }

    @Test
    void serialize_roundTripsThroughDeserialize() throws BenException {
        Deadline original = new Deadline("submit report", "2019-12-02 0930");
        String line = original.serialize();
        Task restored = Task.deserialize(line);
        assertEquals(original.toString(), restored.toString());
        assertEquals(line, restored.serialize());
    }
}
