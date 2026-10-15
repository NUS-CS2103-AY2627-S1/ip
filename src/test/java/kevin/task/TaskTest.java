package kevin.task;

import kevin.KevinException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static kevin.task.Task.parseSavedDateTimeString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaskTest {
    @Test
    public void parseSavedDateTimeString_withMinutes() throws KevinException {
        assertEquals(LocalDateTime.of(2026, 11, 12, 23, 30),
                parseSavedDateTimeString("12 Nov 2026 1130PM"));
    }

    @Test
    public void parseSavedDateTimeString_withoutMinutes() throws KevinException {
        assertEquals(LocalDateTime.of(2026, 1, 12, 23, 0),
                parseSavedDateTimeString("12 Jan 2026 11PM"));
    }

    @Test
    public void parseSavedDateTimeString_PMAndAM() throws KevinException {
        assertEquals(LocalDateTime.of(2026, 9, 9, 23, 30),
                parseSavedDateTimeString("9 Sep 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 7, 6, 11, 30),
                parseSavedDateTimeString("6 Jul 2026 1130AM"));
    }

    @Test
    public void parseSavedDateTimeString_AllMonths() throws KevinException {
        assertEquals(LocalDateTime.of(2026, 1, 9, 23, 30),
                parseSavedDateTimeString("9 Jan 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 2, 6, 11, 30),
                parseSavedDateTimeString("6 Feb 2026 1130AM"));
        assertEquals(LocalDateTime.of(2026, 3, 9, 23, 30),
                parseSavedDateTimeString("9 Mar 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 4, 9, 23, 30),
                parseSavedDateTimeString("9 Apr 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 5, 9, 23, 30),
                parseSavedDateTimeString("9 May 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 6, 9, 23, 30),
                parseSavedDateTimeString("9 Jun 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 7, 9, 23, 30),
                parseSavedDateTimeString("9 Jul 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 8, 9, 23, 30),
                parseSavedDateTimeString("9 Aug 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 9, 9, 23, 30),
                parseSavedDateTimeString("9 Sep 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 10, 9, 23, 30),
                parseSavedDateTimeString("9 Oct 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 11, 9, 23, 30),
                parseSavedDateTimeString("9 Nov 2026 1130PM"));
        assertEquals(LocalDateTime.of(2026, 12, 9, 23, 30),
                parseSavedDateTimeString("9 Dec 2026 1130PM"));
    }

    @Test
    public void parseSavedDateTimeString_oneDigitHour() throws KevinException {
        assertEquals(LocalDateTime.of(2026, 9, 17, 13, 45),
                parseSavedDateTimeString("17 Sep 2026 145PM"));
    }

    @Test
    public void parseSavedDateTimeString_extraWhitespace_exceptionThrown() {
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17  Sep 2026 145PM"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17 Sep  2026 145PM"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17 Sep 2026  145PM"));
    }

    @Test
    public void parseSavedDateTimeString_invalidDay_exceptionThrown() {
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("0 Sep 2026 145PM"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("32 Sep 2026 145PM"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("Sep 2026 145PM"));
    }

    @Test
    public void parseSavedDateTimeString_invalidMonth_exceptionThrown() {
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17 2026 145PM"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17 Sept 2026 145PM"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17 December 2026 145PM"));
    }

    @Test
    public void parseSavedDateTimeString_invalidYear_exceptionThrown() {
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("19 Feb 145PM"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17 Dec 26 145PM"));
    }

    @Test
    public void parseSavedDateTimeString_invalidTime_exceptionThrown() {
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("19 Feb 2026"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17 Dec 2026 1345PM"));
        assertThrows(KevinException.class, () -> parseSavedDateTimeString("17 Dec 2026 1:45PM"));
    }
}
