package will.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class FlexibleDateTest {

    @Test
    public void toDisplayString_recognizedDate_formattedAsMonthDayYear() {
        FlexibleDate date = new FlexibleDate("2019-10-15");
        assertEquals("Oct 15 2019", date.toDisplayString());
    }

    @Test
    public void toDisplayString_freeTextInput_returnedVerbatim() {
        FlexibleDate date = new FlexibleDate("Sunday");
        assertEquals("Sunday", date.toDisplayString());
    }

    @Test
    public void toSaveString_recognizedDate_normalizedToYyyyMmDd() {
        FlexibleDate date = new FlexibleDate("2019-10-15");
        assertEquals("2019-10-15", date.toSaveString());
    }

    @Test
    public void toSaveString_freeTextInput_returnedVerbatim() {
        FlexibleDate date = new FlexibleDate("no idea :-p");
        assertEquals("no idea :-p", date.toSaveString());
    }

    @Test
    public void getDate_recognizedDate_returnsParsedLocalDate() {
        FlexibleDate date = new FlexibleDate("2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), date.getDate());
    }

    @Test
    public void getDate_freeTextInput_returnsNull() {
        FlexibleDate date = new FlexibleDate("Sunday");
        assertNull(date.getDate());
    }

    @Test
    public void parseExact_validYyyyMmDdString_parsesToLocalDate() {
        LocalDate parsed = FlexibleDate.parseExact("2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), parsed);
    }

    @Test
    public void parseExact_invalidFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> FlexibleDate.parseExact("15/10/2019"));
    }

    @Test
    public void formatForDisplay_arbitraryLocalDate_formattedAsMonthDayYear() {
        assertEquals("Oct 15 2019", FlexibleDate.formatForDisplay(LocalDate.of(2019, 10, 15)));
    }
}
