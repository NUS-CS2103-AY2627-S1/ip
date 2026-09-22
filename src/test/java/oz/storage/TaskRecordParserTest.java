package oz.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;
import oz.task.Task;

/**
 * Tests conversion of persisted task records into task objects.
 */
public class TaskRecordParserTest {
    /** Verifies all non-recurring task formats preserve their complete records. */
    @Test
    public void parse_supportedOrdinaryRecords_restoresEachTask() throws OzException {
        String[] records = {
            "T | 1 | choose tea | coffee",
            "D | 0 | return book | 2026-10-10",
            "E | 1 | meeting | 2026-10-10 1400 | 2026-10-10 1630"
        };

        for (String record : records) {
            assertEquals(record, TaskRecordParser.parse(record).toFileFormat());
        }
    }

    /** Verifies recurring rules and completed occurrences are restored. */
    @Test
    public void parse_recurringRecord_restoresRuleAndOccurrences() throws OzException {
        String record = "R | 0 | project meeting | 2026-10-02 1400 | "
                + "2026-10-02 1500 | 2 | 2026-12-31 | 2026-10-16,2026-10-30";

        Task task = TaskRecordParser.parse(record);

        assertEquals(record, task.toFileFormat());
    }

    /** Verifies unknown task types produce a focused parsing error. */
    @Test
    public void parse_unknownTaskType_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                TaskRecordParser.parse("X | 0 | unknown"));

        assertEquals("Unknown task type: X", exception.getMessage());
    }

    /** Verifies invalid completion codes are rejected before task parsing. */
    @Test
    public void parse_invalidCompletionStatus_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                TaskRecordParser.parse("T | 2 | read book"));

        assertEquals("Invalid completion status (must be 0 or 1): 2",
                exception.getMessage());
    }

    /** Verifies recurring series cannot use a global completed status. */
    @Test
    public void parse_completedRecurringSeries_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                TaskRecordParser.parse(
                        "R | 1 | meeting | 2026-10-02 1400 | "
                        + "2026-10-02 1500 | 1 | - | -"));

        assertEquals("A recurring series status must be 0.", exception.getMessage());
    }
}
