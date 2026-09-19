package nori.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests the clock times read out of free-form task details.
 */
public class ClockTimesTest {

    @Test
    public void findFirstIn_compactTime_readsTwentyFourHourClock() {
        assertTime(0, 0, "0000");
        assertTime(9, 0, "0900");
        assertTime(10, 0, "1000");
        assertTime(18, 0, "1800");
        assertTime(23, 59, "2359");
    }

    @Test
    public void findFirstIn_separatedTime_readsBothSeparators() {
        assertTime(9, 0, "9:00");
        assertTime(9, 0, "09:00");
        assertTime(14, 30, "14:30");
        assertTime(9, 30, "9.30");
    }

    @Test
    public void findFirstIn_wholeHourWithMeridiem_readsTwelveHourClock() {
        assertTime(14, 0, "2pm");
        assertTime(14, 0, "2 pm");
        assertTime(9, 0, "9am");
        assertTime(21, 0, "9 P.M.");
        assertTime(9, 0, "9AM");
    }

    @Test
    public void findFirstIn_separatedTimeWithMeridiem_readsTwelveHourClock() {
        assertTime(14, 30, "2:30pm");
        assertTime(14, 30, "2.30pm");
        assertTime(9, 15, "9:15 am");
    }

    @Test
    public void findFirstIn_midnightAndNoon_readTheTwelveHourCorrectly() {
        assertTime(0, 0, "12am");
        assertTime(12, 0, "12pm");
        assertTime(0, 30, "12:30am");
        assertTime(12, 30, "12:30pm");
    }

    @Test
    public void findFirstIn_meridiemAfterAfternoonHour_ignoresTheMeridiem() {
        assertTime(13, 30, "13:30pm");
    }

    @Test
    public void findFirstIn_timeAmongOtherWords_findsTheTime() {
        assertTime(14, 0, "Mon 2pm");
        assertTime(9, 0, " 0900 sharp");
        assertTime(9, 30, "starts at 9:30 in the hall");
    }

    @Test
    public void findFirstIn_severalTimes_takesTheFirst() {
        assertTime(9, 0, "0900 and 1100");
        assertTime(14, 0, "2pm to 4pm");
    }

    @Test
    public void findFirstIn_impossibleTime_findsNothing() {
        assertNoTime("2400");
        assertNoTime("1260");
        assertNoTime("25:00");
        assertNoTime("9:60");
        assertNoTime("13pm");
    }

    @Test
    public void findFirstIn_digitsThatAreNotATime_findNothing() {
        assertNoTime("2");
        assertNoTime("12345");
        assertNoTime("");
        assertNoTime("Mon");
    }

    @Test
    public void findImpossibleTime_timeNoClockShows_returnsItAsWritten() {
        assertEquals(Optional.of("25:00"), ClockTimes.findImpossibleTime("25:00"));
        assertEquals(Optional.of("9:60"), ClockTimes.findImpossibleTime(" 9:60"));
        assertEquals(Optional.of("2400"), ClockTimes.findImpossibleTime("2400"));
        assertEquals(Optional.of("1260"), ClockTimes.findImpossibleTime(" 1260 "));
        assertEquals(Optional.of("13pm"), ClockTimes.findImpossibleTime("13pm"));
        assertEquals(Optional.of("0am"), ClockTimes.findImpossibleTime("Mon 0am"));
    }

    @Test
    public void findImpossibleTime_realTimes_returnsEmpty() {
        assertNoImpossibleTime("0930");
        assertNoImpossibleTime("23:59");
        assertNoImpossibleTime("2:30 PM");
        assertNoImpossibleTime("12:30am");
        assertNoImpossibleTime("13:30pm");
        assertNoImpossibleTime("Mon 2pm");
        assertNoImpossibleTime("0900 and 1100");
    }

    @Test
    public void findImpossibleTime_textThatIsNotATime_returnsEmpty() {
        assertNoImpossibleTime("room 2500");
        assertNoImpossibleTime("costs 9.75");
        assertNoImpossibleTime("12345");
        assertNoImpossibleTime("soon");
        assertNoImpossibleTime("");
    }

    /**
     * Checks that some details give exactly one expected time.
     *
     * @param expectedHour the hour of the day the details should give.
     * @param expectedMinute the minute the details should give.
     * @param details the task details to read.
     */
    private void assertTime(int expectedHour, int expectedMinute, String details) {
        assertEquals(Optional.of(LocalTime.of(expectedHour, expectedMinute)),
                ClockTimes.findFirstIn(details), details);
    }

    /**
     * Checks that some details give no time at all.
     *
     * @param details the task details to read.
     */
    private void assertNoTime(String details) {
        assertTrue(ClockTimes.findFirstIn(details).isEmpty(), details);
    }

    /**
     * Checks that some details hold no time-shaped text that fails to be a real time.
     *
     * @param details the task details to read.
     */
    private void assertNoImpossibleTime(String details) {
        assertTrue(ClockTimes.findImpossibleTime(details).isEmpty(), details);
    }
}
