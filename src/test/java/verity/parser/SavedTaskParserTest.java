package verity.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import verity.exception.VerityException;

/**
 * Tests rejection of corrupted saved-task records.
 */
class SavedTaskParserTest {
    private final SavedTaskParser parser = new SavedTaskParser();

    @Test
    void parseSavedTasks_tooFewFields_reportsLineNumber() {
        assertParseFailure(
                List.of("T\t0"),
                "Line 1: expected at least three fields.");
    }

    @Test
    void parseSavedTasks_invalidFieldCounts_rejectsEachTaskType() {
        assertParseFailure(
                List.of("T\t0\tread book\textra"),
                "Line 1: a todo has an invalid number of fields.");
        assertParseFailure(
                List.of("D\t0\treport\t2026-08-10\textra"),
                "Line 1: a deadline has an invalid number of fields.");
        assertParseFailure(
                List.of("E\t0\tmeeting\t2026-08-10\t2026-08-11\textra"),
                "Line 1: a event has an invalid number of fields.");
    }

    @Test
    void parseSavedTasks_duplicateOrEmptyClientIds_throwsVerityException() {
        assertParseFailure(
                List.of("T\t0\tinvoice\tC001,c001\t"),
                "Line 1: client IDs must be valid and unique.");
        assertParseFailure(
                List.of("T\t0\tinvoice\tC001,\t"),
                "Line 1: client IDs must be valid and unique.");
    }

    @Test
    void parseSavedTasks_blankFormerClientNote_throwsVerityException() {
        assertParseFailure(
                List.of("T\t0\tinvoice\t\tfirst\\n\\nsecond"),
                "Line 1: former-client notes cannot be blank.");
    }

    private void assertParseFailure(List<String> lines, String expectedMessage) {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parseSavedTasks(lines));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
