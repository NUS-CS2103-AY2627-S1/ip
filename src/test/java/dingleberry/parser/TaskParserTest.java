package dingleberry.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dingleberry.exception.DingleberryException;

/**
 * Tests task contents and validation of description and date parameters.
 */
class TaskParserTest {
    @Test
    void parseTodo_description_preservesContent() throws DingleberryException {
        assertEquals("read /by notes | chapter one",
                TaskParser.parseTodo("  read /by notes | chapter one  ")
                        .getDescription());
    }

    @Test
    void parseDeadline_byValue_constructsExpectedDeadline()
            throws DingleberryException {
        assertEquals("D | 0 | submit report | 2026-08-25T18:00:00",
                TaskParser.parseDeadline("submit report /by 2026-08-25 1800")
                        .toSaveFormat());
    }

    @Test
    void parseEvent_absoluteTimes_constructsExpectedEvent()
            throws DingleberryException {
        assertEquals("E | 0 | meeting | 2026-08-25T14:00:00"
                        + " | 2026-08-26T15:00:00",
                TaskParser.parseEvent("meeting /from 2026-08-25 1400"
                        + " /to 2026-08-26 1500").toSaveFormat());
    }

    @Test
    void parseEvent_timeOnlyEnd_inheritsStartDate()
            throws DingleberryException {
        assertEquals("E | 0 | meeting | 2026-08-25T14:00:00"
                        + " | 2026-08-25T16:00:00",
                TaskParser.parseEvent("meeting /from 2026-08-25 1400 /to 4pm")
                        .toSaveFormat());
    }

    @Test
    void parse_mixedCaseMarkers_acceptsDeadlineAndEvent()
            throws DingleberryException {
        assertEquals("D | 0 | report | 2026-08-25T18:00:00",
                TaskParser.parseDeadline("report /BY 2026-08-25 1800")
                        .toSaveFormat());
        assertEquals("E | 0 | meeting | 2026-08-25T14:00:00"
                        + " | 2026-08-25T15:00:00",
                TaskParser.parseEvent("meeting /FROM 2026-08-25 1400"
                        + " /TO 2026-08-25 1500").toSaveFormat());
    }

    @Test
    void parseTodo_blankDescription_rejectsTask() {
        assertThrows(DingleberryException.class,
                () -> TaskParser.parseTodo("   "));
    }

    @Test
    void parseDeadline_invalidMarkers_rejectsTask() {
        for (String input : new String[] {"report", "report /bydate",
                "report /to 2026-08-25 1800", "report /from 2026-08-25 1800",
                "report /by 2026-08-25 1800 /by 2026-08-26 1800"}) {
            assertThrows(DingleberryException.class,
                    () -> TaskParser.parseDeadline(input));
        }
    }

    @Test
    void parseDeadline_blankFields_rejectsTask() {
        for (String input : new String[] {
                "/by 2026-08-25 1800", "report /by"}) {
            assertThrows(DingleberryException.class,
                    () -> TaskParser.parseDeadline(input));
        }
    }

    @Test
    void parseDeadline_invalidDate_rejectsTask() {
        assertThrows(DingleberryException.class,
                () -> TaskParser.parseDeadline("report /by 2026-02-30 1800"));
    }

    @Test
    void parseEvent_invalidMarkers_rejectsTask() {
        for (String input : new String[] {"meeting",
                "meeting /from 2026-08-25 1400",
                "meeting /to 2026-08-25 1500 /from 2026-08-25 1400",
                "meeting /by 2026-08-25 1400 /to 2026-08-25 1500",
                "meeting /from 2026-08-25 1400 /by 2026-08-25 1500",
                "meeting /from 2026-08-25 1400 /from 2026-08-25 1500",
                "meeting /from 2026-08-25 1400 /to 2026-08-25 1500 /to 4pm"}) {
            assertThrows(DingleberryException.class,
                    () -> TaskParser.parseEvent(input));
        }
    }

    @Test
    void parseEvent_blankFields_reportsMissingDetails() {
        for (String input : new String[] {
                "/from 2026-08-25 1400 /to 2026-08-25 1500",
                "meeting /from /to 2026-08-25 1500",
                "meeting /from 2026-08-25 1400 /to"}) {
            DingleberryException exception = assertThrows(
                    DingleberryException.class,
                    () -> TaskParser.parseEvent(input));
            assertEquals("The event needs a non-empty description and details.",
                    exception.getMessage());
        }
    }

    @Test
    void parseEvent_invalidDate_rejectsTask() {
        for (String input : new String[] {
                "meeting /from invalid /to 2026-08-25 1500",
                "meeting /from 2026-08-25 1400 /to invalid"}) {
            assertThrows(DingleberryException.class,
                    () -> TaskParser.parseEvent(input));
        }
    }

    @Test
    void parseEvent_equalOrReversedTimes_rejectsTask() {
        for (String endTime : new String[] {"1400", "1300"}) {
            DingleberryException exception = assertThrows(
                    DingleberryException.class,
                    () -> TaskParser.parseEvent("meeting /from 2026-08-25 1400"
                            + " /to 2026-08-25 " + endTime));
            assertEquals("An event's end time must be after its start time.",
                    exception.getMessage());
        }
    }
}
