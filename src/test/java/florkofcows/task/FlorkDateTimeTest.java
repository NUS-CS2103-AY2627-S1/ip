package florkofcows.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class FlorkDateTimeTest {

    @Test
    public void parse_validDateTime_displaysFormattedDateTime() {
        FlorkDateTime dt = FlorkDateTime.parse("2019-12-02 1800");
        assertEquals("Dec 2 2019, 6:00pm", dt.toDisplayString());
    }

    @Test
    public void parse_validDateTimeOnly_displaysFormattedDateTime() {
        FlorkDateTime dt = FlorkDateTime.parse("2019-12-02");
        assertEquals("Dec 2 2019", dt.toDisplayString());
    }

    @Test
    public void parse_plainText_displaysTextUnchanged() {
        FlorkDateTime dt = FlorkDateTime.parse("no idea :-p");
        assertEquals("no idea :-p", dt.toDisplayString());
    }

    @Test
    public void parse_wrongSeparatorFormat_fallsBackToText() {
        FlorkDateTime dt = FlorkDateTime.parse("2/12/2019 1800");
        assertEquals("2/12/2019 1800", dt.toDisplayString());
    }

    @Test
    public void parse_invalidCalendarDate_fallsBackToText() {
        FlorkDateTime dt = FlorkDateTime.parse("2019-13-45");
        assertEquals("2019-13-45", dt.toDisplayString());
    }

    @Test
    public void parse_nonZeroPaddedDate_fallsBackToText() {
        // Documents actual behaviour for the boundary case flagged during manual testing.
        FlorkDateTime dt = FlorkDateTime.parse("2019-1-5");
        assertEquals("2019-1-5", dt.toDisplayString());
    }

    @Test
    public void parse_dateTimeBoundary_validatesAsDatetime() {
        FlorkDateTime dt = FlorkDateTime.parse("2024-02-29 2359");
        assertEquals("Feb 29 2024, 11:59pm", dt.toDisplayString());
    }

    @Test
    public void getDateTimeOrNull_datetime_returnsDateTime() {
        FlorkDateTime dt = FlorkDateTime.parse("2019-12-02 1800");
        assertEquals("2019-12-02T18:00", dt.getDateTimeOrNull().toString());
    }

    @Test
    public void getDateTimeOrNull_plainText_returnsNull() {
        FlorkDateTime dt = FlorkDateTime.parse("whenever");
        assertNull(dt.getDateTimeOrNull());
    }

    @Test
    public void toSaveFormat_dateTime_roundTripsToSameDisplay() {
        FlorkDateTime original = FlorkDateTime.parse("2019-12-02 1800");
        FlorkDateTime reloaded = FlorkDateTime.parse(original.toSaveFormat());
        assertEquals(original.toDisplayString(), reloaded.toDisplayString());
    }

    @Test
    public void toSaveFormat_dateOnly_roundTripsToSameDisplay() {
        FlorkDateTime original = FlorkDateTime.parse("2019-12-02");
        FlorkDateTime reloaded = FlorkDateTime.parse(original.toSaveFormat());
        assertEquals(original.toDisplayString(), reloaded.toDisplayString());
    }

    @Test
    public void toSaveFormat_plainText_roundTripsToSameDisplay() {
        FlorkDateTime original = FlorkDateTime.parse("someday");
        FlorkDateTime reloaded = FlorkDateTime.parse(original.toSaveFormat());
        assertEquals(original.toDisplayString(), reloaded.toDisplayString());
    }

    @Test
    public void getDateOrNull_dateTime_returnsUnderlyingDate() {
        FlorkDateTime dt = FlorkDateTime.parse("2019-12-02 1800");
        assertEquals(LocalDate.of(2019, 12, 2), dt.getDateOrNull());
    }

    @Test
    public void getDateOrNull_dateOnly_returnsUnderlyingDate() {
        FlorkDateTime dt = FlorkDateTime.parse("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2), dt.getDateOrNull());
    }

    @Test
    public void getDateOrNull_plainText_returnsNull() {
        FlorkDateTime dt = FlorkDateTime.parse("whenever");
        assertNull(dt.getDateOrNull());
    }
}
