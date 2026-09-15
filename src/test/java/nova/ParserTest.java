package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ParserTest {

    // ---------------------------------------------------------------- //
    // parseDataLine                                                     //
    // ---------------------------------------------------------------- //

    @Test
    public void parseDataLine_todo_rebuildsTheTask() {
        Task task = Parser.parseDataLine("T | 0 | read book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseDataLine_deadline_rebuildsTheTask() {
        Task task = Parser.parseDataLine("D | 0 | return book | 2019-10-15T18:00");

        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00PM)", task.toString());
    }

    @Test
    public void parseDataLine_event_rebuildsTheTask() {
        Task task = Parser.parseDataLine("E | 0 | meeting | 2019-10-15T14:00 | 2019-10-15T16:00");

        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)",
                task.toString());
    }

    @Test
    public void parseDataLine_doneFlagOfOne_marksTheTaskDone() {
        assertTrue(Parser.parseDataLine("T | 1 | read book").isDone());
        assertFalse(Parser.parseDataLine("T | 0 | read book").isDone());
    }

    @Test
    public void parseDataLine_generousWhitespace_isTrimmedAway() {
        Task task = Parser.parseDataLine("  T  |  1  |  read book  ");

        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void parseDataLine_tooFewFields_returnsNull() {
        assertNull(Parser.parseDataLine(""));
        assertNull(Parser.parseDataLine("T"));
        assertNull(Parser.parseDataLine("T | 0"));
        assertNull(Parser.parseDataLine("garbage"));
    }

    @Test
    public void parseDataLine_unknownTypeLetter_returnsNull() {
        assertNull(Parser.parseDataLine("X | 0 | read book"));
        assertNull(Parser.parseDataLine("t | 0 | read book"));
    }

    @Test
    public void parseDataLine_doneFlagNotANumber_returnsNull() {
        assertNull(Parser.parseDataLine("T | yes | read book"));
        assertNull(Parser.parseDataLine("T |  | read book"));
    }

    @Test
    public void parseDataLine_emptyDescription_returnsNull() {
        assertNull(Parser.parseDataLine("T | 0 |    "));
    }

    @Test
    public void parseDataLine_deadlineWithNoDateField_returnsNull() {
        assertNull(Parser.parseDataLine("D | 0 | return book"));
    }

    @Test
    public void parseDataLine_deadlineWithFreeTextDate_returnsNull() {
        // Lines written before dates were typed hold text like "2pm". They are
        // treated as corrupt so the caller can count and report them.
        assertNull(Parser.parseDataLine("D | 0 | return book | 2pm"));
        assertNull(Parser.parseDataLine("D | 0 | return book | Sunday"));
    }

    @Test
    public void parseDataLine_eventMissingItsEnd_returnsNull() {
        assertNull(Parser.parseDataLine("E | 0 | meeting | 2019-10-15T14:00"));
    }

    @Test
    public void parseDataLine_backwardsEvent_returnsNull() {
        assertNull(Parser.parseDataLine("E | 0 | oops | 2026-01-06T00:00 | 2026-01-05T00:00"));
    }

    @Test
    public void parseDataLine_zeroLengthEvent_isAccepted() {
        assertNotNull(Parser.parseDataLine("E | 0 | ping | 2026-01-05T09:00 | 2026-01-05T09:00"));
    }

    // ---------------------------------------------------------------- //
    // parseStoredDateTime                                               //
    // ---------------------------------------------------------------- //

    @Test
    public void parseStoredDateTime_isoDateTime_isRead() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                Parser.parseStoredDateTime("2019-10-15T18:00"));
    }

    @Test
    public void parseStoredDateTime_surroundingWhitespace_isTrimmed() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                Parser.parseStoredDateTime("   2019-10-15T18:00   "));
    }

    @Test
    public void parseStoredDateTime_anythingElse_returnsNull() {
        // A stored field always carries a time, so a bare date is not valid here
        // even though it is valid as something the user types.
        assertNull(Parser.parseStoredDateTime("2019-10-15"));
        assertNull(Parser.parseStoredDateTime("2pm"));
        assertNull(Parser.parseStoredDateTime(""));
    }

    // ---------------------------------------------------------------- //
    // isInteger                                                         //
    // ---------------------------------------------------------------- //

    @Test
    public void isInteger_wholeNumbers_returnsTrue() {
        assertTrue(Parser.isInteger("0"));
        assertTrue(Parser.isInteger("12"));
        assertTrue(Parser.isInteger("-3"));
    }

    @Test
    public void isInteger_notAWholeNumber_returnsFalse() {
        assertFalse(Parser.isInteger(null));
        assertFalse(Parser.isInteger(""));
        assertFalse(Parser.isInteger("12a"));
        assertFalse(Parser.isInteger("1.5"));
    }

    @Test
    public void isInteger_paddedWithSpaces_returnsFalse() {
        // parseInt does not trim, so neither does this.
        assertFalse(Parser.isInteger(" 12 "));
    }

    @Test
    public void isInteger_tooLargeForAnInt_returnsFalse() {
        assertTrue(Parser.isInteger("2147483647"));
        assertFalse(Parser.isInteger("2147483648"));
    }

    // ---------------------------------------------------------------- //
    // splitOnMarker                                                     //
    // ---------------------------------------------------------------- //

    @Test
    public void splitOnMarker_markerPresent_returnsBothHalvesTrimmed() {
        MarkerParts parts = Parser.splitOnMarker("  return book   /by   2019-10-15  ", "/by");

        assertEquals("return book", parts.before());
        assertEquals("2019-10-15", parts.after());
    }

    @Test
    public void splitOnMarker_markerMissing_returnsNull() {
        assertNull(Parser.splitOnMarker("return book", "/by"));
    }

    @Test
    public void splitOnMarker_nothingBeforeTheMarker_returnsNull() {
        assertNull(Parser.splitOnMarker("/by 2019-10-15", "/by"));
        assertNull(Parser.splitOnMarker("   /by 2019-10-15", "/by"));
    }

    @Test
    public void splitOnMarker_nothingAfterTheMarker_returnsNull() {
        assertNull(Parser.splitOnMarker("return book /by", "/by"));
        assertNull(Parser.splitOnMarker("return book /by    ", "/by"));
    }

    @Test
    public void splitOnMarker_markerAppearsTwice_splitsOnTheFirst() {
        MarkerParts parts = Parser.splitOnMarker("a /by b /by c", "/by");

        assertEquals("a", parts.before());
        assertEquals("b /by c", parts.after());
    }

    // ---------------------------------------------------------------- //
    // The /force marker                                                 //
    // ---------------------------------------------------------------- //

    @Test
    public void hasForceMarker_trailingMarker_returnsTrue() {
        assertTrue(Parser.hasForceMarker("meeting /from 2026-01-05 /to 2026-01-06 /force"));
        assertTrue(Parser.hasForceMarker("meeting /force   "));
    }

    @Test
    public void hasForceMarker_markerNotAtEnd_returnsFalse() {
        assertFalse(Parser.hasForceMarker("/force meeting"));
        assertFalse(Parser.hasForceMarker("meeting /from 2026-01-05 /to 2026-01-06"));
        assertFalse(Parser.hasForceMarker(""));
    }

    @Test
    public void removeForceMarker_trailingMarker_isStripped() {
        assertEquals("meeting /from 2026-01-05 /to 2026-01-06",
                Parser.removeForceMarker("meeting /from 2026-01-05 /to 2026-01-06 /force"));
    }

    @Test
    public void removeForceMarker_noMarker_returnsTrimmedInput() {
        assertEquals("meeting", Parser.removeForceMarker("  meeting  "));
    }

    @Test
    public void removeForceMarker_markerNotAtEnd_isLeftAlone() {
        assertEquals("/force the issue", Parser.removeForceMarker("  /force the issue  "));
    }

    // ---------------------------------------------------------------- //
    // parseDateTime                                                     //
    // ---------------------------------------------------------------- //

    @Test
    public void parseDateTime_dateOnly_timeIsMidnight() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                Parser.parseDateTime("2019-10-15"));
    }

    @Test
    public void parseDateTime_dateAndTime_bothKept() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                Parser.parseDateTime("2019-10-15 1800"));
    }

    @Test
    public void parseDateTime_explicitMidnight_isRead() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                Parser.parseDateTime("2019-10-15 0000"));
    }

    @Test
    public void parseDateTime_unrecognisedText_returnsNull() {
        assertNull(Parser.parseDateTime("Sunday"));
        assertNull(Parser.parseDateTime("2pm"));
        assertNull(Parser.parseDateTime("2019-1-5"));
        assertNull(Parser.parseDateTime(""));
    }

    @Test
    public void parseDateTime_timeNotFourDigits_returnsNull() {
        assertNull(Parser.parseDateTime("2019-10-15 800"));
        assertNull(Parser.parseDateTime("2019-10-15 18:00"));
    }

    @Test
    public void parseDateTime_impossibleTime_returnsNull() {
        assertNull(Parser.parseDateTime("2019-10-15 2400"));
        assertNull(Parser.parseDateTime("2019-10-15 1860"));
    }

    @Test
    public void parseDateTime_impossibleDate_returnsNull() {
        // Strict resolving is what stops these being quietly moved to a real
        // date: lenient resolving would turn Feb 30 into Feb 28.
        assertNull(Parser.parseDateTime("2019-02-30 1800"));
        assertNull(Parser.parseDateTime("2019-04-31 1800"));
        assertNull(Parser.parseDateTime("2019-13-01 1800"));
    }

    @Test
    public void parseDateTime_leapDay_isAcceptedOnlyInALeapYear() {
        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0), Parser.parseDateTime("2020-02-29"));
        assertNull(Parser.parseDateTime("2019-02-29"));
    }
}
