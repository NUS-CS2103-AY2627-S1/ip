package herta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import herta.exception.HertaException;

/**
 * Tests task-number, archive-range, and restore-number parsing.
 */
class TaskSelectionParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTaskNumbers_rejectZeroSignedDecimalAndMultipleValues() {
        for (String input : new String[] {"mark 0", "mark -1", "mark +1", "mark 1.0", "mark 1 2"}) {
            HertaException exception = assertThrows(HertaException.class, () ->
                    parser.parseTaskIndex(input, "mark"));
            assertEquals("That's not a task number. Try: mark 1.", exception.getMessage());
        }
    }

    @Test
    void parseTaskIndex_oneBasedInput_returnsZeroBasedIndex() throws HertaException {
        assertEquals(0, parser.parseTaskIndex("mark 1", "mark"));
        assertEquals(2, parser.parseTaskIndex("delete 3", "delete"));
    }

    @Test
    void parseTaskIndex_nonNumericInput_throwsHelpfulException() {
        HertaException exception = assertThrows(HertaException.class, () ->
                parser.parseTaskIndex("delete nope", "delete"));

        assertEquals("That's not a task number. Try: delete 1.", exception.getMessage());
    }

    @Test
    void parseArchiveSelection_validSelectors_returnsRangesInInputOrder() throws HertaException {
        ArchiveSelection selection = parser.parseArchiveSelection("archive 02 2-2 3-5 4");

        assertFalse(selection.isAllSelected());
        assertEquals(List.of(new ArchiveRange(2, 2), new ArchiveRange(2, 2),
                new ArchiveRange(3, 5), new ArchiveRange(4, 4)), selection.getRanges());
        assertEquals(new ArchiveRange(2, 2),
                parser.parseArchiveSelection("archive 0000000000000000000002").getRanges().getFirst());
        assertTrue(parser.parseArchiveSelection("archive all").isAllSelected());
    }

    @Test
    void parseArchiveSelection_invalidInput_usesSpecificSelectionErrors() {
        assertEquals("You gave me nothing to archive. Try: archive 1 or archive all.",
                assertArchiveError("archive").getMessage());
        assertEquals("That selection won't do. Use task numbers or ranges, such as archive 1 3-5.",
                assertArchiveError("archive -1").getMessage());
        assertEquals("That selection won't do. Use task numbers or ranges, such as archive 1 3-5.",
                assertArchiveError("archive 0").getMessage());
        assertEquals("That selection won't do. Use task numbers or ranges, such as archive 1 3-5.",
                assertArchiveError("archive 1 - 3").getMessage());
        assertEquals("The range runs the wrong way. Try: archive 2-5.",
                assertArchiveError("archive 5-2").getMessage());
        assertEquals("That selection won't do. Use task numbers or ranges, such as archive 1 3-5.",
                assertArchiveError("archive 999999999999999999").getMessage());
        assertEquals("Pick one: archive all, or archive 1 3-5. Mixing them is unnecessary.",
                assertArchiveError("archive all 1").getMessage());
    }

    @Test
    void parseRestoreTaskNumber_validAndInvalidInputs_areDistinguished() throws HertaException {
        assertEquals(0, parser.parseRestoreTaskNumber("restore 001"));
        assertEquals(0, parser.parseRestoreTaskNumber("restore 0000000000000000000001"));
        for (String input : List.of("restore", "restore 1 2", "restore 1-2", "restore 0", "restore -1",
                "restore +1", "restore 999999999999999999")) {
            HertaException exception = assertRestoreError(input);
            String expected = input.equals("restore")
                    ? "Tell me which archived task to restore. Try: restore 1."
                    : "That archived task number won't do. Try: restore 1.";
            assertEquals(expected, exception.getMessage());
        }
    }

    @Test
    void parseNumericArguments_boundariesAndOverflow_areRejectedPrecisely() throws HertaException {
        assertEquals(Integer.MAX_VALUE - 1,
                parser.parseTaskIndex("mark " + Integer.MAX_VALUE, "mark"));
        assertEquals(2, parser.parseUpcomingDays("upcoming 0002"));

        String[] invalidTaskNumbers = {
            "mark 0", "mark -1", "mark +1", "mark 1.0", "mark 1 trailing",
            "mark 2147483648", "mark " + "1".repeat(64)
        };
        for (String input : invalidTaskNumbers) {
            assertThrows(HertaException.class, () -> parser.parseTaskIndex(input, "mark"));
        }
        assertThrows(HertaException.class, () -> parser.parseUpcomingDays("upcoming 0"));
        assertThrows(HertaException.class, () -> parser.parseUpcomingDays("upcoming -1"));
        assertThrows(HertaException.class, () -> parser.parseUpcomingDays("upcoming 1.0"));
        assertThrows(HertaException.class, () -> parser.parseUpcomingDays("upcoming 2147483648"));
    }

    @Test
    void parseTaskNumbers_everyIndexCommand_rejectsIntegerOverflow() {
        for (String commandKeyword : List.of("mark", "unmark", "delete")) {
            assertThrows(HertaException.class, () -> parser.parseTaskIndex(
                    commandKeyword + " 2147483648", commandKeyword));
        }
        assertThrows(HertaException.class, () -> parser.parseArchiveSelection("archive 2147483648"));
        assertThrows(HertaException.class, () -> parser.parseRestoreTaskNumber("restore 2147483648"));
    }

    private HertaException assertArchiveError(String input) {
        return assertThrows(HertaException.class, () -> parser.parseArchiveSelection(input));
    }

    private HertaException assertRestoreError(String input) {
        return assertThrows(HertaException.class, () -> parser.parseRestoreTaskNumber(input));
    }
}
