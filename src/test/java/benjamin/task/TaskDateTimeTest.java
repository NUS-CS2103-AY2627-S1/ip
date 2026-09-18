package benjamin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import benjamin.BenjaminException;

public class TaskDateTimeTest {
    @Test
    public void parse_isoDateOnly_showsDateWithoutTime() throws BenjaminException {
        assertEquals("Oct 15 2019", TaskDateTime.parse("2019-10-15").toString());
    }

    @Test
    public void parse_isoDateWithTime_showsDateAndTime() throws BenjaminException {
        assertEquals("Dec 02 2019, 6:00pm", TaskDateTime.parse("2019-12-02 1800").toString());
    }

    @Test
    public void parse_slashDateWithTime_showsDateAndTime() throws BenjaminException {
        assertEquals("Dec 02 2019, 6:00pm", TaskDateTime.parse("2/12/2019 1800").toString());
    }

    @Test
    public void parse_slashDateOnly_showsDateWithoutTime() throws BenjaminException {
        assertEquals("Dec 02 2019", TaskDateTime.parse("2/12/2019").toString());
    }

    @Test
    public void parse_morningTime_showsAmSuffix() throws BenjaminException {
        assertEquals("Dec 02 2019, 9:00am", TaskDateTime.parse("2019-12-02 0900").toString());
    }

    @Test
    public void parse_midnight_showsTwelveAm() throws BenjaminException {
        assertEquals("Dec 02 2019, 12:00am", TaskDateTime.parse("2019-12-02 0000").toString());
    }

    @Test
    public void parse_surroundingSpaces_ignoresThem() throws BenjaminException {
        assertEquals("Oct 15 2019", TaskDateTime.parse("  2019-10-15  ").toString());
    }

    @Test
    public void parse_unknownWording_exceptionListsAcceptedFormats() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> TaskDateTime.parse("June 6th"));
        assertTrue(exception.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    public void parse_emptyText_exceptionThrown() {
        assertThrows(BenjaminException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    public void parse_timeWithoutDate_exceptionThrown() {
        assertThrows(BenjaminException.class, () -> TaskDateTime.parse("1800"));
    }

    @Test
    public void toStorageString_dateOnly_omitsTime() throws BenjaminException {
        assertEquals("2019-10-15", TaskDateTime.parse("15/10/2019").toStorageString());
    }

    @Test
    public void toStorageString_dateWithTime_keepsTime() throws BenjaminException {
        assertEquals("2019-12-02 1800", TaskDateTime.parse("2/12/2019 1800").toStorageString());
    }

    @Test
    public void toStorageString_savedValue_canBeParsedBackUnchanged() throws BenjaminException {
        TaskDateTime original = TaskDateTime.parse("2/12/2019 1800");
        TaskDateTime reloaded = TaskDateTime.parse(original.toStorageString());

        assertEquals(original.toString(), reloaded.toString());
        assertEquals(original.toStorageString(), reloaded.toStorageString());
    }

    @Test
    public void getDate_dateWithTime_dropsTheTime() throws BenjaminException {
        assertEquals(LocalDate.of(2019, 12, 2), TaskDateTime.parse("2019-12-02 1800").getDate());
    }

    @Test
    public void formatDate_anyDate_usesDisplayFormat() {
        assertEquals("Jan 05 2020", TaskDateTime.formatDate(LocalDate.of(2020, 1, 5)));
    }
}
