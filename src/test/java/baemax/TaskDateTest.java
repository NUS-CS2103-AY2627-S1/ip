package baemax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests for {@link TaskDate}, which parses and formats task dates and times. */
public class TaskDateTest {

    // ---- parse: date-only formats ----

    @Test
    public void parse_isoDate_accepted() throws BaemaxException {
        assertEquals("Oct 15 2019", TaskDate.parse("2019-10-15").toString());
    }

    @Test
    public void parse_slashDate_accepted() throws BaemaxException {
        assertEquals("Oct 15 2019", TaskDate.parse("15/10/2019").toString());
    }

    @Test
    public void parse_dashDayFirstDate_accepted() throws BaemaxException {
        assertEquals("Oct 15 2019", TaskDate.parse("15-10-2019").toString());
    }

    @Test
    public void parse_spelledMonthDate_accepted() throws BaemaxException {
        assertEquals("Oct 15 2019", TaskDate.parse("15 Oct 2019").toString());
        assertEquals("Oct 15 2019", TaskDate.parse("Oct 15 2019").toString());
        assertEquals("Oct 15 2019", TaskDate.parse("15 October 2019").toString());
    }

    @Test
    public void parse_monthNameIsCaseInsensitive() throws BaemaxException {
        assertEquals("Oct 15 2019", TaskDate.parse("15 oct 2019").toString());
    }

    // ---- parse: date with a time ----

    @Test
    public void parse_twentyFourHourTime_accepted() throws BaemaxException {
        assertEquals("Oct 15 2019, 6:00PM", TaskDate.parse("2019-10-15 1800").toString());
        assertEquals("Oct 15 2019, 6:00PM", TaskDate.parse("15/10/2019 18:00").toString());
    }

    @Test
    public void parse_amPmTime_accepted() throws BaemaxException {
        assertEquals("Oct 15 2019, 6:00PM", TaskDate.parse("15 Oct 2019 6pm").toString());
        assertEquals("Oct 15 2019, 6:30PM", TaskDate.parse("15 Oct 2019 6:30PM").toString());
        assertEquals("Oct 15 2019, 9:00AM", TaskDate.parse("2019-10-15 9am").toString());
    }

    @Test
    public void parse_timeIsStoredInIsoDateTimeForm() throws BaemaxException {
        assertEquals("2019-10-15T18:00", TaskDate.parse("2019-10-15 1800").toStorageString());
    }

    @Test
    public void parse_dateOnlyIsStoredInIsoDateForm() throws BaemaxException {
        assertEquals("2019-10-15", TaskDate.parse("15/10/2019").toStorageString());
    }

    // ---- parse: rejected input ----

    @Test
    public void parse_nonDateText_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> TaskDate.parse("someday"));
    }

    @Test
    public void parse_impossibleDate_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> TaskDate.parse("2019-13-40"));
    }

    @Test
    public void parse_empty_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> TaskDate.parse(""));
    }

    @Test
    public void parse_unrecognisedTrailingToken_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> TaskDate.parse("2019-10-15 soon"));
    }

    // ---- fromStorage / round trip ----

    @Test
    public void fromStorage_isoDate_readsBack() throws BaemaxException {
        assertEquals("Oct 15 2019", TaskDate.fromStorage("2019-10-15").toString());
    }

    @Test
    public void fromStorage_isoDateTime_readsBack() throws BaemaxException {
        assertEquals("Oct 15 2019, 6:00PM", TaskDate.fromStorage("2019-10-15T18:00").toString());
    }

    @Test
    public void fromStorage_garbage_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> TaskDate.fromStorage("not-a-date"));
    }

    @Test
    public void parseThenStoreThenLoad_roundTrips() throws BaemaxException {
        TaskDate original = TaskDate.parse("15 Oct 2019 6:30pm");
        TaskDate reloaded = TaskDate.fromStorage(original.toStorageString());
        assertEquals(original.toString(), reloaded.toString());
    }
}
