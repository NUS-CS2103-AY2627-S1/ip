package nob.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing and display formatting of task dates and times.
 */
public class DateTimeUtilTest {
    @Test
    public void parseDateTime_supportedDateTimeFormats_expectedValuesReturned() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeUtil.parseDateTime("2/12/2019 1800").orElseThrow());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 30),
                DateTimeUtil.parseDateTime("2019-12-02T18:30").orElseThrow());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeUtil.parseDateTime("Dec 2 2019, 6:00PM").orElseThrow());
    }

    @Test
    public void parseDateTime_isoDateWithoutTime_startOfDayReturned() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                DateTimeUtil.parseDateTime(" 2019-12-02 ").orElseThrow());
    }

    @Test
    public void parseDateTime_isoDateAndCompactTime_expectedValueReturned() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 6, 15),
                DateTimeUtil.parseDateTime("2019-12-02 615").orElseThrow());
    }

    @Test
    public void parseDateTime_nullBlankOrUnsupported_emptyReturned() {
        assertEquals(Optional.empty(), DateTimeUtil.parseDateTime(null));
        assertEquals(Optional.empty(), DateTimeUtil.parseDateTime("   "));
        assertFalse(DateTimeUtil.parseDateTime("next pancake day").isPresent());
    }

    @Test
    public void formatDisplay_midnight_dateOnlyReturned() {
        assertEquals("Dec 02 2019", DateTimeUtil.formatDisplay(LocalDateTime.of(2019, 12, 2, 0, 0)));
    }

    @Test
    public void formatDisplay_timePresent_dateAndTimeReturned() {
        assertEquals("Dec 02 2019, 6:05PM",
                DateTimeUtil.formatDisplay(LocalDateTime.of(2019, 12, 2, 18, 5)));
    }

    @Test
    public void formatDisplay_nullValue_emptyStringReturned() {
        assertEquals("", DateTimeUtil.formatDisplay(null));
    }
}
