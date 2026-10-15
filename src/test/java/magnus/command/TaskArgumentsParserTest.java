package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests strict parsing of named fields in task command arguments.
 */
public class TaskArgumentsParserTest {
    @Test
    public void parseDeadline_validArguments_returnsDescriptionAndDeadline() {
        TaskArgumentsParser.DeadlineArguments arguments =
                TaskArgumentsParser.parseDeadline("submit report /by 02/09/2026 1500");

        assertEquals("submit report", arguments.description());
        assertEquals("02/09/2026 1500", arguments.deadline());
    }

    @Test
    public void parseDeadline_wrongDelimiter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                TaskArgumentsParser.parseDeadline("submit report /from 02/09/2026 1500"));
    }

    @Test
    public void parseDeadline_repeatedDelimiter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskArgumentsParser.parseDeadline(
                "submit report /by 02/09/2026 1500 /by 03/09/2026 1600"));
    }

    @Test
    public void parseEvent_validArguments_returnsDescriptionAndTimes() {
        TaskArgumentsParser.EventArguments arguments = TaskArgumentsParser.parseEvent(
                "team meeting /from 02/09/2026 1500 /to 02/09/2026 1600");

        assertEquals("team meeting", arguments.description());
        assertEquals("02/09/2026 1500", arguments.start());
        assertEquals("02/09/2026 1600", arguments.end());
    }

    @Test
    public void parseEvent_reversedDelimiters_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskArgumentsParser.parseEvent(
                "team meeting /to 02/09/2026 1600 /from 02/09/2026 1500"));
    }

    @Test
    public void parseEvent_repeatedDelimiter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskArgumentsParser.parseEvent(
                "team meeting /from 02/09/2026 1400 /from 02/09/2026 1500 "
                        + "/to 02/09/2026 1600"));
    }

    @Test
    public void containsFieldDelimiter_slashInsideWord_returnsFalse() {
        assertEquals(false, TaskArgumentsParser.containsFieldDelimiter("review from/to notation"));
    }

    @Test
    public void containsFieldDelimiter_reservedStandaloneFields_returnsTrue() {
        assertEquals(true, TaskArgumentsParser.containsFieldDelimiter("task /by Friday"));
        assertEquals(true, TaskArgumentsParser.containsFieldDelimiter("task /from today"));
        assertEquals(true, TaskArgumentsParser.containsFieldDelimiter("task /to tomorrow"));
    }

    @Test
    public void containsFieldDelimiter_nullText_returnsFalse() {
        assertEquals(false, TaskArgumentsParser.containsFieldDelimiter(null));
    }

    @Test
    public void parseDeadline_blankNullOrMultilineInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskArgumentsParser.parseDeadline("   "));
        assertThrows(IllegalArgumentException.class, () -> TaskArgumentsParser.parseDeadline(null));
        assertThrows(IllegalArgumentException.class, () ->
                TaskArgumentsParser.parseDeadline("submit report\n/by 20/09/2026 1700"));
        assertThrows(IllegalArgumentException.class, () ->
                TaskArgumentsParser.parseDeadline("submit report\r/by 20/09/2026 1700"));
    }

    @Test
    public void parseEvent_blankNullOrMultilineInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskArgumentsParser.parseEvent("   "));
        assertThrows(IllegalArgumentException.class, () -> TaskArgumentsParser.parseEvent(null));
        assertThrows(IllegalArgumentException.class, () -> TaskArgumentsParser.parseEvent(
                "meeting /from 20/09/2026 0900\n/to 20/09/2026 1000"));
    }
}
