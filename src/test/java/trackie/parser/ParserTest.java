package trackie.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import trackie.exception.TrackieException;
import trackie.task.Task;

/** Tests command parsing and validation. */
public class ParserTest {
    @Test
    public void parseTaskIndex_validOneBasedNumber_returnsZeroBasedIndex() throws TrackieException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", "mark", 3));
    }

    @Test
    public void parseTaskIndex_outOfRangeNumber_throwsTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseTaskIndex(
                "delete 4", "delete", 3));

        assertEquals("Oops! Choose a task number between 1 and 3.", exception.getMessage());
    }

    @Test
    public void parseDeadline_validIsoDate_returnsFormattedDeadline() throws TrackieException {
        Task deadline = Parser.parseDeadline("deadline submit report /by 2026-09-15");

        assertEquals("[D][ ] submit report (by: Sep 15 2026)", deadline.toString());
    }

    @Test
    public void parseDeadline_invalidDate_throwsTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseDeadline(
                "deadline submit report /by 15-09-2026"));

        assertEquals("Oops! Use a deadline date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseDeadline_duplicateByMarker_throwsTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseDeadline(
                "deadline submit report /by 2026-09-15 /by 2026-09-16"));

        assertEquals("Oops! Use: deadline DESCRIPTION /by DATE", exception.getMessage());
    }

    @Test
    public void parseTodo_storageDelimiterInDescription_throwsTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseTodo(
                "todo compare option A | option B"));

        assertEquals("Oops! Task descriptions cannot contain the | character.", exception.getMessage());
    }

    @Test
    public void parseEvent_duplicateFromMarker_throwsTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseEvent(
                "event meeting /from 2026-09-15 /from 2026-09-16 /to 2026-09-17"));

        assertEquals("Oops! Use: event DESCRIPTION /from START_DATE /to END_DATE",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_nonexistentDate_throwsSpecificTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseDeadline(
                "deadline submit report /by 2026-02-30"));

        assertEquals("Oops! Enter a valid deadline date.", exception.getMessage());
    }

    @Test
    public void parseEvent_nonexistentDate_throwsSpecificTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseEvent(
                "event meeting /from 2026-02-30 /to 2026-03-01"));

        assertEquals("Oops! Enter valid event dates.", exception.getMessage());
    }

    @Test
    public void parseEvent_endBeforeStart_throwsTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseEvent(
                "event meeting /from 2026-09-25 /to 2026-09-15"));

        assertEquals("Oops! The end date cannot be before the start date.", exception.getMessage());
    }

    @Test
    public void parseWithinPeriod_validInclusivePeriod_returnsFormattedTask() throws TrackieException {
        Task task = Parser.parseWithinPeriod(
                "within collect certificate /from 2026-09-15 /to 2026-09-25");

        assertEquals("[W][ ] collect certificate (from: Sep 15 2026 to: Sep 25 2026)",
                task.toString());
    }

    @Test
    public void parseWithinPeriod_endBeforeStart_throwsTrackieException() {
        TrackieException exception = assertThrows(TrackieException.class, () -> Parser.parseWithinPeriod(
                "within collect certificate /from 2026-09-25 /to 2026-09-15"));

        assertEquals("Oops! The end date cannot be before the start date.", exception.getMessage());
    }
}
