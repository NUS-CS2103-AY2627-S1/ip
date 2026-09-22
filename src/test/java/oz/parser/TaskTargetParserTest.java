package oz.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests task-index and occurrence-target parsing.
 */
public class TaskTargetParserTest {
    /** Verifies a valid one-based task number becomes a zero-based index. */
    @Test
    public void parseIndex_validTaskNumber_returnsZeroBasedIndex() throws OzException {
        assertEquals(1, TaskTargetParser.parseIndex(" 2 ", 3));
    }

    /** Verifies a mark target can identify one recurring occurrence. */
    @Test
    public void parseMarkTarget_taskAndDate_returnsOccurrenceTarget() throws OzException {
        TaskTarget target = TaskTargetParser.parseMarkTarget("2 /on 17/9/2026", 3);

        assertEquals(1, target.taskIndex());
        assertEquals(LocalDate.of(2026, 9, 17), target.occurrenceDate());
    }

    /** Verifies an unmark target can identify an ordinary task. */
    @Test
    public void parseUnmarkTarget_taskOnly_returnsWholeTaskTarget() throws OzException {
        TaskTarget target = TaskTargetParser.parseUnmarkTarget("3", 3);

        assertEquals(2, target.taskIndex());
        assertNull(target.occurrenceDate());
    }

    /** Verifies duplicate occurrence flags are rejected before index parsing. */
    @Test
    public void parseMarkTarget_duplicateOccurrenceFlag_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                TaskTargetParser.parseMarkTarget(
                        "1 /on 2026-09-17 /on 2026-09-24", 1));

        assertEquals("Duplicate '/on' parameter detected.", exception.getMessage());
    }

    /** Verifies task-creation flags are rejected with command-specific usage. */
    @Test
    public void parseUnmarkTarget_unexpectedFlag_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                TaskTargetParser.parseUnmarkTarget("1 /by 2026-09-17", 1));

        assertEquals("Unexpected 'flag' parameter in unmark command. "
                + "Use: unmark <number> [/on <date>].", exception.getMessage());
    }
}
